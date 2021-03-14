package com.tsh.service.impl;

import java.sql.Time;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
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
import com.tsh.entities.Process;
import com.tsh.entities.ProcessDetails;
import com.tsh.entities.Student;
import com.tsh.entities.StudentBatches;
import com.tsh.entities.Teacher;
import com.tsh.entities.Term;
import com.tsh.entities.TimeSlot;
import com.tsh.entities.Topics;
import com.tsh.entities.TrainingType;
import com.tsh.exception.TSHException;
import com.tsh.library.DataImporter;
import com.tsh.library.dto.ImportItemTO;
import com.tsh.library.dto.ImportStatistics;
import com.tsh.library.dto.ReImportRequest;
import com.tsh.repositories.IImportStats;
import com.tsh.repositories.ImportItemRepository;
import com.tsh.service.IBatchService;
import com.tsh.service.IDataImportService;
import com.tsh.service.IFeedbackService;
import com.tsh.service.IGeneralService;
import com.tsh.service.IProcessService;
import com.tsh.service.IStudentService;
import com.tsh.service.ITeacherService;
import com.tsh.service.ITopicService;
import com.tsh.utility.TshUtil;

@Service
public class DataImportService implements IDataImportService {

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
	private IGeneralService generalService;
	@Autowired
	private ITeacherService teacherService;
	@Autowired
	private ITopicService topicService;
	@Autowired
	private Processor commandFInder;
	@Autowired
	private IProcessService processService;

	private int importCycle;

	private DataImportService() {
	}

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public String importDataFromOutlook(int parentProcessId, Date startDate, Date endDate) throws Exception {
		Process parent = processService.getProcessById(parentProcessId);
		this.setImportCycle(getLastImportCycle() + 1);
		dataImporter.setStartAndEndDates(startDate, endDate);
		List<ImportItem> items = dataImporter.importData(parent);
		if (items == null)
			return "Success";

		logger.info("Data received from external interface... Number of items to Import : {}", items.size());
		int result = 0;

		// Here we will take care of managing the percentage completion.
		// Total items divided in 4 steps. Each step having equal weight
		int totalItems = items.size();
		int chunkSize = totalItems / 4;
		int stepNum = 5;
		ProcessDetails oldStep = processService.newProcessStep("Importing Data to System", stepNum++, 1.5, parent);
		ProcessDetails newStep = null;
		int nextChunk = chunkSize;
		int itemNumber = 0;
		for (ImportItem it : items) {
			if (itemNumber++ == nextChunk) {
				newStep = processService.closeOldAndCreateNewStep(oldStep, "Importing Data to System", stepNum++, 1.5,
						parent);
				nextChunk = nextChunk + chunkSize;
				oldStep = newStep;
			}
			try {
				result = importThisItem(it);
			} catch (Exception e) {
				processService.failStep(newStep, parent);
				throw e;
			}
		}
		items.clear();
		newStep = processService.closeOldAndCreateNewStep(oldStep, "Finally Cleaning up", stepNum++, 0.66, parent);
		manageOrphanBatchDetails();
		if (result == 1) {
			processService.completeProcessStep(newStep);
		} else {
			processService.failStep(newStep, parent);
		}
		return (result == 1) ? "Success" : "Error";
	}

	@Transactional
	private void manageOrphanBatchDetails() throws TSHException {
//		logger.info("Retrieve all Batch Details that are either orphans or effectvely inactive.");

		logger.info("Find all students that was not in import data. Existed since before. The deleted students.. ");
		List<Student> students = studentService.getAllActiveStudents();
		boolean imported = false;
		for (Student stud : students) {
			List<StudentBatches> studBatches = studentService.getAllActiveBatches(stud);
			for (StudentBatches studBatch : studBatches) {
				if (wasImported(studBatch.getStudent().getStudentName(),
						"Year " + studBatch.getBatchDetails().getGrade().getGrade(),
						studBatch.getBatchDetails().getCourse().getShortDescription())) {
					imported = true;
					continue;
				} else {
					imported = false;
				}
			}
			if (!imported) {
				stud.setActive(false);
				for (StudentBatches studBatch : studBatches) {
					studBatch.setEndDate(TshUtil.getCurrentDate());

					// Add this item to import Item as deleted student. This will be used in
					// reports.
					ImportItem deletedItem = new ImportItem();
					deletedItem.setCycle(getImportCycle());
					deletedItem.setName(stud.getStudentName());
					deletedItem.setSubject(studBatch.getCourse().getDescription());
					deletedItem.setGrade("Year " + stud.getGrade().getGrade());
					deletedItem.setTeacher(studBatch.getBatchDetails().getTeacher().getTeacherName());
					deletedItem.setBatchDate(studBatch.getBatchDetails().getBatch().getStartDate());
					deletedItem.setBatchStartTime(studBatch.getBatchDetails().getBatch().getTimeSlot().getStartTime());
					deletedItem.setBatchEndTime(studBatch.getBatchDetails().getBatch().getTimeSlot().getEndTime());
					deletedItem.setStatus(ImportItem.SUCCESS);
					deletedItem.setImportDate(TshUtil.getCurrentDate());
					deletedItem.setMessage("Discontinued");
					importItemRepo.save(deletedItem);
				}

				studentService.saveStudentBatches(studBatches);
				studentService.saveStudent(stud);
			}
		}

		logger.info("Handle orphan batch details");
		List<BatchDetails> batchDetailList = batchService.getAllActiveBatchDetails();
		for (BatchDetails batchDetails : batchDetailList) {
			if (studentService.getAllActiveStudentBatches(batchDetails).size() > 0) {
				continue;
			} else {
				batchDetails.setActive(false);
				batchService.saveBatchDetails(batchDetails);
			}
		}
		logger.info("Finished cleanup...");
	}

	@Transactional
	private int importThisItem(ImportItem item) {
		int retries = 0;
		for (retries = 1; retries <= 3; retries++) {
			try {
				logger.info("Preparing to import {}.", item.getName());
				item = startImport(item);
				logger.info("Import STATUS for Item ID: {} - {}", item.getId(), item.getStatusAsString());
				break;
			} catch (Exception e) {
				item.failed();
				item.setMessage("Failed - Unknown Reason");
				saveImportItem(item);
				logger.error("Item id : " + item.getId());
				logger.error(e.getMessage());
				logger.error(e.getStackTrace().toString());
				logger.error("Failed importing Item : " + item);
				logger.error("Retrying to import again. Attempt number : {} out of 3", retries + 1);
			}
		}
		if (item.hasFailedImport())
			return 1;
		logger.info("{} ready to import.", item.getName());
		try {
			item = save(item);
			logger.info("{} successfully Imported to the system.", item.getName());
		} catch (TSHException e) {
			e.printStackTrace();
			logger.error("Item id : " + item.getId());
			logger.error(e.getStackTrace().toString());
			logger.error(
					"Failed extracting data from import item ID : {}. Try importing again from the import data table.",
					item.getId());
			logger.error(e.getMessage());
			item.failed();
			item.setMessage(e.getMessage());
			saveImportItem(item);
			return 1;
		}
		return 1;
	}

	@Transactional
	private ImportItem startImport(ImportItem item) {
		logger.info("Importing item : " + item);
		item.setCycle(this.getImportCycle());
		item.readyForImport();
		item = saveImportItem(item); // Save raw data to import tables
		return item;
	}

	private ImportItem save(ImportItem item) throws TSHException {
		logger.info("Extracting data from Import Item...");
		item.startImport();
		item = saveImportItem(item);

		int batchWeekDay = item.getBatchWeekDay();
		Time batchStartTime = item.getBatchStartTime();
		Time batchEndTime = item.getBatchEndTime();
		int gradeNumber = item.getGradeNumber();
		String subject = item.getSubject();
		String teacherName = item.getTeacher();

		if (item.getName().length() == 0 || item.getSubject().length() == 0 || item.getGrade().length() == 0) {
			logger.info("No Data found in Item : {}. Nothing to Import.", item.getId());
			item.skip();
			item = saveImportItem(item);
			return item;
		}

		// Get All master data from DB. We would need them to validate and create new
		// instances.

		TimeSlot timeSlot = generalService
				.getTimeSlot(item.getBatchWeekDay(), item.getBatchStartTime(), item.getBatchEndTime())
				.orElseThrow(() -> new TSHException("No Time Slot found : " + DayOfWeek.of(batchWeekDay) + " : "
						+ batchStartTime + " to " + batchEndTime));
		Grades grade = generalService.getGrades(item.getGradeNumber())
				.orElseThrow(() -> new TSHException("Grade not found : " + gradeNumber));
		Course course = generalService.getCourses(item.getSubject())
				.orElseThrow(() -> new TSHException("Course not found : " + subject));
		Teacher teacher = teacherService.getTeachers(item.getTeacher())
				.orElseThrow(() -> new TSHException("Teacher not found : " + teacherName));
		TrainingType trainingType = generalService.getTrainingType(item.getLocation()).orElse(null);
		Term term = generalService.getCurrentTerm().orElseThrow(() -> new TSHException("Current term not found"));
		BatchDetails batchDetails = batchService.getBatchDetails(teacher, grade, course, timeSlot, trainingType, term)
				.orElse(null);

		if (batchDetails == null) {
			logger.info("No Open Batch found. Creating a new Batch");
			// Check if a batch at that TimeSot exists. If not then create a Batch with this
			// timeSlot.
			Batch batch = batchService.getBatch(timeSlot).orElse(null);
			if (batch == null) {
				batch = Batch.getNewInstance(timeSlot);
				batchService.saveBatch(batch);
			}
			// Now create a batchDetails
			batchDetails = BatchDetails.getNewInstance(batch, teacher, course, grade);
			batchDetails.setTrainingType(trainingType);
			batchDetails.setTerm(term);
			batchService.saveBatchDetails(batchDetails);

			logger.info(batchDetails.toString());
		}

		Student student = studentService.getStudent(item).orElse(null);
		StudentBatches studentBatches = null;
		if (student == null) { // New Student & new StudentBatch.
			logger.info("No record found for Student : {}. Creating a new record", item.getName());
			item.setMessage("New Student");
			student = Student.getNewInstance(item, grade);
			student = studentService.saveStudent(student);

			studentBatches = StudentBatches.getNewInstance(student, course);
			studentBatches.setBatchDetails(batchDetails);
			logger.info(studentBatches.toString());
		} else { // Old Student. Maybe the batch Changed.
			logger.info("Student record found for : {}", item.getName());
			studentBatches = studentService.getActiveStudentBatchesForCourseCategory(student, course)
					.orElse(StudentBatches.getNewInstance(student, course));
			if (studentBatches.getBatchDetails() == null)
				studentBatches.setBatchDetails(batchDetails);

			if (studentBatches.getId() != 0 && !studentBatches.getBatchDetails().equals(batchDetails)) { // If the batch
																											// has been
																											// changed.
				logger.info("Old Batch and current bactch mismatch. Assigning new Batch to {}", item.getName());

				// If studentBatches id is 0, then student existed but was not enrolled to this
				// category of course.
				// Else then probably the batch just changed.
				if (studentBatches.getId() == 0)
					item.setMessage("Existing - New Enrollment");
				else
					item.setMessage("Existing - Batch Changed");

				StudentBatches newStudentBatch = StudentBatches.getNewInstance(student, course); // Create a new Student
																									// Batch. We want to
				logger.info("Ending {}'s enrollment to old batch - id : {}, for course : {} grade :{} and Term: {}",
						student.getStudentName(), studentBatches.getId(),
						studentBatches.getCourse().getShortDescription(),
						studentBatches.getBatchDetails().getGrade().getGrade(),
						studentBatches.getBatchDetails().getTerm().getTerm()); // keep the history.
				studentBatches.setEndDate(Calendar.getInstance().getTime()); // Set old studentBatch end date to
																				// deactivate
				newStudentBatch.setBatchDetails(batchDetails);
				logger.info("Preparing to save import data in Database...");
				studentService.save(newStudentBatch);
				logger.info("Import Data saved successfully to database.");
			} else {
				item.setMessage("Existing - No Change");
				logger.info("Item {} already exist. Skiping this item", item.getName());
			}
		}
		studentService.save(studentBatches);
		item.imported();
		item = saveImportItem(item);
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
		List<Topics> topicList = commandProcessor.execute();

		Map<String, List<Topics>> topicsToSave = feedbackService.validateAndSync(topicList);
		if (topicsToSave.get("Modified").size() > 0)
			topicService.saveAllTopics(topicsToSave.get("Modified"));
		if (topicsToSave.get("New").size() > 0)
			topicService.saveAllTopics(topicsToSave.get("New"));
		logger.info("All topics successfully saved to database.");
		return topicsToSave;
	}

	@Override
	public boolean wasImported(String name, String grade, String subject) {
		List<ImportItem> items = importItemRepo.findByNameAndGradeAndSubject(name, grade, subject);
		if (items.size() > 0)
			return true;
		else
			return false;
	}

	@Override
	public int getLastImportCycle() {
		int cycle = 0;
		try {
			cycle = importItemRepo.getLastCycle();
		} catch (Exception e) {
			// Do nothing. There is no data in import item so this should be the first
			// cycle. Return zero
		}

		return cycle;
	}

	public int getImportCycle() {
		return importCycle;
	}

	private void setImportCycle(int importCycle) {
		this.importCycle = importCycle;
	}

	@Override
	public List<ImportItemTO> getAllImportedItems(int cycleNumber) {
		List<ImportItem> importItemList = importItemRepo.findByCycle(cycleNumber);
		ModelMapper mapper = new ModelMapper();
		List<ImportItemTO> importItemTOList = importItemList.stream().map(it -> {
			ImportItemTO itemTO = mapper.map(it, ImportItemTO.class);
			try {
				if (it.getBatchDate() != null) {
					itemTO.setWeekday(TshUtil.getWeekDayAsString(it.getBatchDate()));
					itemTO.setBatchDate(TshUtil.toString(it.getBatchDate()));
				}
			} catch (TSHException e) {
				itemTO.setBatchDate(e.getMessage());
			}
			try {
				if (it.getImportDate() != null)
					itemTO.setImportDate(TshUtil.toString(it.getImportDate()));
			} catch (TSHException e) {
				itemTO.setImportDate(e.getMessage());
			}
			if (it.getBatchStartTime() != null)
				itemTO.setBatchStartTime(TshUtil.formatTimeToHHmm24Hr(it.getBatchStartTime()));
			return itemTO;
		}).collect(Collectors.toList());
		return importItemTOList;
	}

	@Override
	public List<ImportStatistics> getImportStatistics(int cycle) {

		List<IImportStats> stats = importItemRepo.getImportStatistics(cycle);
		return stats.stream().map(s -> {
			ImportStatistics st = new ImportStatistics();
			st.setCount(Integer.parseInt(s.getCOUNT()));
			st.setImportDesc(s.getIMPORTDESC());
			st.setStatus(Integer.parseInt(s.getSTATUS()));
			return st;
		}).collect(Collectors.toList());
	}

	@Override
	public String getImportDate(int cycle) throws TSHException {

		Date importDate = importItemRepo.getImportDate(cycle);
		return TshUtil.toString(importDate);
	}

	@Override
	public ImportItemTO reImport(ReImportRequest request) throws TSHException, ParseException {

		ImportItemTO returnResult = null;

		ImportItem item = importItemRepo.findById(request.getId()).orElse(null);
		if (item == null)
			throw new TSHException("Item to Import not found.");

		this.setImportCycle(item.getCycle());
		item.setName(request.getName());
		item.setGrade(request.getGrade());
		item.setSubject(request.getSubject());
		item.setTeacher(request.getTeacher());
		Time t = Time.valueOf(request.getStart());
		item.setBatchStartTime(t);
		item.setBatchDate(TshUtil.toDate(request.getWeekday()));

		this.importThisItem(item);
		return returnResult;

	}

}
