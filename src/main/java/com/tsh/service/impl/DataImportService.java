package com.tsh.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.tsh.command.ICommand;
import com.tsh.command.Processor;
import com.tsh.entities.Batch;
import com.tsh.entities.BatchDetails;
import com.tsh.entities.Course;
import com.tsh.entities.Grades;
import com.tsh.entities.ImportItem;
import com.tsh.entities.Student;
import com.tsh.entities.StudentBatches;
import com.tsh.entities.Teacher;
import com.tsh.entities.Term;
import com.tsh.entities.TimeSlot;
import com.tsh.entities.Topics;
import com.tsh.entities.TrainingType;
import com.tsh.exception.TSHException;
import com.tsh.library.DataImporter;
import com.tsh.repositories.ImportItemRepository;
import com.tsh.service.IBatchService;
import com.tsh.service.IDataImportService;
import com.tsh.service.IFeedbackService;
import com.tsh.service.IStudentService;


@Service
public class DataImportService implements IDataImportService{
	
	@Autowired
	private DataImporter dataImporter;
	@Autowired
	private IBatchService batchService;
	@Autowired
	private IStudentService studentService;
	@Autowired
	private ImportItemRepository importItemRepo;
	@Autowired
	private IFeedbackService feedbackService;
	@Autowired
	private Processor commandFInder;
	
	private DataImportService() {}
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public String importData() throws Exception {
		List<ImportItem> items = dataImporter.importData();
		if (items == null) return "Success";
		
		logger.info("Data received from external interface... Number of items to Import : {}", items.size());
		int result = 0;
		for(ImportItem it : items) {
			result = importThisItem(it);
		}
		items.clear();
		manageOrphanBatchDetails();
		return (result == 1)?"Success":"Error";
	}
	
	@Transactional
	private void manageOrphanBatchDetails() {
		logger.info("Retrieve all Batch Details that are either orphans or effectvely inactive.");
		List<BatchDetails> batchDetails = batchService.findAllOrphanBatchDetails();
		logger.info("{} Orphan or effectively inactive Batch Details found.",batchDetails.size());
		
		for(BatchDetails batch : batchDetails) {
			batch.setEndDate(Calendar.getInstance().getTime());
			batch.setActive(false);
		}
		if(batchDetails.size() > 0) {
			batchService.saveBatchDetails(batchDetails);
			logger.info("{} orphan Batch Details marked inactive successfully", batchDetails.size());
		}
	}

	@Transactional
	private int importThisItem(ImportItem item) {
		int retries = 0;
		for(retries= 1; retries <=3;retries ++) {
			try {
				logger.info("Preparing to import {}.", item.getName());
				item = startImport(item);				
				logger.info("Import STATUS for Item ID: {} - {}",item.getId(), item.getStatusAsString());
				break;
			} catch(Exception e) {
				item.failed();
				logger.error(e.getMessage());
				logger.error(e.getStackTrace().toString());
				logger.error("Failed importing Item : " + item);
				logger.error("Retrying to import again. Attempt number : {} out of 3", retries +1); 
			}
		}
		if(item.hasFailedImport()) return 0;
		logger.info("{} ready for import.", item.getName());
		try {
			item = save(item);
			item.imported();
			logger.info("{} successfully Imported to the system.",item.getName());
		} catch (TSHException e) {
			e.printStackTrace();
			logger.error(e.getStackTrace().toString());
			logger.error("Failed extracting data from import item ID : {}. Try importing again from the import data table.",item.getId());
			logger.error(e.getMessage());
			item.failed();
			return 0;
		}
		return 1;
	}
	
	@Transactional
	private ImportItem startImport(ImportItem item) {
		logger.info("Importing item : " + item);
		item.readyForImport();
		item = saveImportItem(item);		//Save raw data to import tables
		item.startImport();
		return item; 
	}
	
	private ImportItem save(ImportItem item) throws TSHException {
		logger.info("Extracting data from Import Item...");
		if(item.getName().length() == 0 || item.getSubject().length() == 0 || item.getGrade().length() == 0) {
			logger.info("No Data found in Item : {}. Nothing to Import.", item.getId());
			item.imported();
			return item;
		}
		
		// Get All master data from DB. We would need them to validate and create new instances.
		TimeSlot timeSlot = batchService.getTimeSlot(item.getBatchWeekDay(), item.getBatchStartTime()).orElseThrow(()-> new TSHException("No Time Slot found : " + item.getBatchWeekDay() + " : " + item.getBatchStartTime()));
		Grades grade = batchService.getGrades(item.getGradeNumber()).orElseThrow(()-> new TSHException("Grade not found : " + item.getGradeNumber()));
		Course course = batchService.getCourses(item.getSubject()).orElseThrow(() -> new TSHException("Course not found : " + item.getSubject()));
		Teacher teacher = batchService.getTeachers(item.getTeacher()).orElseThrow(()-> new TSHException("Teacher not found : " + item.getTeacher()));
		TrainingType trainingType = batchService.getTrainingType(item.getLocation()).orElseThrow(()-> new TSHException("Location : " + item.getLocation() + " not a valid Location."));
		Term term = batchService.getCurrentTerm().orElseThrow(() -> new TSHException("Current term not found"));
		BatchDetails batchDetails = batchService.getBatchDetails(teacher, grade, course, timeSlot, trainingType, term).orElse(null);
		
		if(batchDetails == null) {
			logger.info("No Open Batch found. Creating a new Batch");
			// Check if a batch at that TimeSot exists. If not then create a Batch with this timeSlot.
			Batch batch = batchService.getBatch(timeSlot).orElse(null);
			if(batch==null) batch = Batch.getNewInstance(timeSlot);
			
  			// Now create a batchDetails
			batchDetails = BatchDetails.getNewInstance(batch, teacher, course, grade);
			batchDetails.setTrainingType(trainingType);
			batchDetails.setTerm(term);
			logger.info(batchDetails.toString());
		}
		
		Student student = studentService.getStudent(item).orElse(null);
		StudentBatches studentBatches = null;
		if(student == null) {													//New Student & new StudentBatch.
			logger.info("No record found for Student : {}. Creating a new record", item.getName());
			student = Student.getNewInstance(item, grade);
			studentBatches = StudentBatches.getNewInstance(student, course);
			studentBatches.setBatchDetails(batchDetails);
			logger.info(studentBatches.toString());
		} else {																//Old Student. Maybe the batch Changed.
			logger.info("Student record found for : {}" , item.getName());
			studentBatches = studentService.getStudentBatches(student, course).orElse(StudentBatches.getNewInstance(student, course));
			if(studentBatches.getBatchDetails() == null) studentBatches.setBatchDetails(batchDetails);
			
			if(studentBatches.getId() != 0 && !studentBatches.getBatchDetails().equals(batchDetails)) { 	//If the batch has been changed.
				logger.info("Old Batch and current bactch mismatch. Assigning new Batch to {}",item.getName());
				StudentBatches newStudentBatch = StudentBatches.getNewInstance(student, course);			//Create a new Student Batch. We want to keep the history.
				studentBatches.setEndDate(Calendar.getInstance().getTime());								//Set old studentBatch end date to deactivate
				newStudentBatch.setBatchDetails(batchDetails);
				logger.info("Preparing to save import data in Database...");
				studentService.save(newStudentBatch);
				logger.info("Import Data saved successfully to database.");
				item.imported();
			}else {
				logger.info("Item {} already exist. Skiping this item" , item.getName());
				item.imported();
			}
				
		}
		studentService.save(studentBatches);		
		return item;
	}
	
	public ImportItem saveImportItem(ImportItem item) {
		return importItemRepo.save((ImportItem) item);
	}

	@Override
	public Map<String, List<Topics>> processFile(MultipartFile importedFile, String command) throws TSHException {
		List<Object> params = new ArrayList<>();
		params.add(importedFile);
		commandFInder.setParams(command, params);
		ICommand<Topics> commandProcessor = commandFInder.findCommandProcessor();
		List<Topics> topicList =  commandProcessor.execute();
		
		Map<String, List<Topics>> topicsToSave = feedbackService.validateAndSync(topicList);
		if(topicsToSave.get("Modified").size() > 0 ) feedbackService.saveAllTopics(topicsToSave.get("Modified"));
		if(topicsToSave.get("New").size() > 0 ) feedbackService.saveAllTopics(topicsToSave.get("New"));
		logger.info("All topics successfully saved to database.");
		return topicsToSave;
	}
	

}
