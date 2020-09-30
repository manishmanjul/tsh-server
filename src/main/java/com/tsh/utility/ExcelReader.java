package com.tsh.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tsh.command.impl.ImportTopicExcelProps;
import com.tsh.entities.Course;
import com.tsh.entities.Grades;
import com.tsh.entities.Term;
import com.tsh.entities.Topics;
import com.tsh.entities.Week;
import com.tsh.exception.TSHException;

public class ExcelReader {
	
	private File file;
	private List<Topics> topicList = new ArrayList<>();
	private ImportTopicExcelProps props;
	Workbook workbook = null;
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public ExcelReader() {}
	
	public ExcelReader(File file, ImportTopicExcelProps props) {
		this.file = file;
		this.props = props;
	}
	
	public List<Topics> consume() throws TSHException {
		Map<String, String> stats = new LinkedHashMap<>();
		FileInputStream fis = openFileToRead();
		getWorkbook(fis);
		
		for(String definedName : this.props.getTopicsFor()){
			stats.put(definedName, "" + readTopicsFor(definedName) + " Topics");
		}
		
		logger.info("--- DONE Importing all topics ---");
		printStatistics(stats);
		
		this.closeAll();
		return this.topicList;
	}
	
	private FileInputStream openFileToRead() throws TSHException {
		FileInputStream fis = null;
		if(!file.exists()) {
			logger.error("File {} not found. Nothing to consume.",file.getName());
			throw new TSHException("File " + file.getName() + " not found. Nothing to consume.");
		}
		logger.info("Reading file {} for topics...", file.getName());
		try {
			fis = new FileInputStream(this.file);
		} catch (FileNotFoundException e) {
			logger.error("File {} not found. Nothing to consume.{}",file.getName(), e.getMessage());
			throw new TSHException("File " + file.getName() + " not found. Nothing to consume." + e.getMessage());
		}
		logger.info("{} is ready to be consumed.",this.file.getName());
		return fis;
	}
	
	private void getWorkbook(FileInputStream fis) throws TSHException {
		try {
			this.workbook = new XSSFWorkbook(fis);
		} catch (IOException e) {
			logger.error("File {} not found. Nothing to consume.{}",file.getName(), e.getMessage());
			throw new TSHException("File " + file.getName() + " not found. Nothing to consume." + e.getMessage());
		}
	}
	
	private int readTopicsFor(String definedName) throws TSHException {
		Topics topic = null;
		int countOfTopics = 0;
		List<String> gradeAndCourse = null;
		int namedCellIdx = this.workbook.getNameIndex(definedName);
		Name nameAtCell = this.workbook.getNameAt(namedCellIdx);
		
		@SuppressWarnings("deprecation")
		AreaReference areaRef = new AreaReference(nameAtCell.getRefersToFormula());
		CellReference[] crefs = areaRef.getAllReferencedCells();
		
		gradeAndCourse = extractGradeAndCourse(definedName);
		logger.info("Importing topics for Year {} , {}",gradeAndCourse.get(0), gradeAndCourse.get(1));
		String value = null;
		
		for(int i =3; i<crefs.length; i++) {
			Sheet s = this.workbook.getSheet(crefs[i].getSheetName());
			Row r = s.getRow(crefs[i].getRow());
			Cell cell = r.getCell(crefs[i].getCol());
			
			try { //If null pointer. This is the end of the named range.
				value = cell.getStringCellValue();
			}catch(NullPointerException e) {
				break;
			}
			
			if(i%3 == 0) { //This is the topic id 
				topic = new Topics();
				topic.setMappedId(value);
				topic = setGradeAndCourse(topic, gradeAndCourse);
				
			}else if(i%3 == 1) { //This is the chapter name
				topic.setChapter(value);
				
			}else if(i%3 == 2) { //This is the topic name
				topic.setDescription(value);
				topic = extractTermAndWeek(topic);
				
				// All mandatory values should be populated by now. Check if mapped Id and descriptions are there. If not skip this topic.
				if(!isValid(topic)) {
					topic = null;
				}else {
					addTopics(topic);
					countOfTopics++;
				}
			}
		}
		logger.info("Year {} - {} : {} topics imported successfully.",gradeAndCourse.get(0), gradeAndCourse.get(1), countOfTopics);
		return countOfTopics;
	}
	
	private List<String> extractGradeAndCourse(String definedName) throws TSHException {
		String temp = definedName;
		String subject, extension, gradeStr;
		subject = gradeStr = extension = null;
		if(temp.contains("Maths")) {subject = "Maths"; temp = temp.substring(6);}
		if(temp.contains("English")) {subject = "English"; temp = temp.substring(8);}
		if(temp.contains("Ability")) {subject = "GA";temp = temp.substring(8);}
		if(Character.isDigit(temp.charAt(temp.length()-1))) {
			extension = "";
			gradeStr = temp;
		}else {
			extension = "" + temp.charAt(temp.length()-1);
			gradeStr = temp.substring(0, temp.length()-1);
		}
		List<String> gradeAndCourse = new ArrayList<>();
		gradeAndCourse.add(gradeStr);
		gradeAndCourse.add(subject + " " + extension);
		
		return gradeAndCourse;
	}
	
	private Topics extractTermAndWeek(Topics topic) {
		String termStr, weekStr;
		termStr = weekStr = null;
		String bigString = topic.getChapter() + " " + topic.getDescription();
		StringTokenizer token = new StringTokenizer(bigString);
		while(token.hasMoreTokens()) {
			String str = token.nextToken();
			if(termStr == null && str.equalsIgnoreCase("Term")) termStr = token.nextToken();
			if(weekStr == null && str.equalsIgnoreCase("Week")) weekStr = token.nextToken(); 
		}
		
		try{
			topic.setTerm(new Term(Integer.parseInt(termStr)));
		}catch(NumberFormatException e) {
			//Do nothing. let the term be null.
		}
		try {
			topic.setWeek(new Week(Integer.parseInt(weekStr)));
		}catch(NumberFormatException e) {
			// Do nothing. Let the week number be null.
		}
		
		return topic;
	}
	
	private Topics setGradeAndCourse(Topics topic, List<String> gradeAndCourse) throws TSHException {
		topic.setGrade(new Grades(Integer.parseInt(gradeAndCourse.get(0))));
		topic.setCourse(new Course(gradeAndCourse.get(1)));
		
		return topic;
	}
	
	private boolean isValid(Topics topic) {
		if(topic.getMappedId() == null || topic.getMappedId().length() == 0 || topic.getDescription() == null || topic.getDescription().length() == 0)
			return false;
		else
			return true;
	}
	
	private void printStatistics(Map<String, String> stats) {
		for(String key : stats.keySet()) {
			logger.info("{} - {} imported", key, stats.get(key) );
		}
	}
	
	private void closeAll() throws TSHException {
		try {
			this.workbook.close();
		} catch (IOException e) {
			logger.error("Unable to close workbook..{}", e.getMessage());
			throw new TSHException("Unable to close workbook.." + e.getMessage());
		}
	}
	
	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
	
	public List<Topics> getTopicList() {
		return topicList;
	}

	public void setTopicList(List<Topics> topicList) {
		this.topicList = topicList;
	}

	public void addTopics(Topics topic) {
		this.topicList.add(topic);
	}
	
	public static void main(String args[]) {
		File file = new File("./uploads/Topic_1594508975232.xlsm");
		ImportTopicExcelProps props = new ImportTopicExcelProps();
		props.addTopicsFor("MathsY3");
		props.addTopicsFor("MathsY3S");
		props.addTopicsFor("MathsY4");
		
		ExcelReader reader = new ExcelReader(file, props);
		try {
			reader.consume();
		} catch (TSHException e) {
			e.printStackTrace();
		}
	}
}
