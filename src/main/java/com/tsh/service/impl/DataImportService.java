package com.tsh.service.impl;

import java.sql.Time;
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
import com.tsh.repositories.ImportItemRepository;
import com.tsh.service.IBatchService;
import com.tsh.service.IDataImportService;
import com.tsh.service.IFeedbackService;
import com.tsh.service.IGeneralService;
import com.tsh.service.IProcessService;
import com.tsh.service.IStudentService;
import com.tsh.service.ITeacherService;
import com.tsh.service.ITopicService;

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

	private DataImportService() {
	}

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public String importDataFromOutlook(int parentProcessId) throws Exception {
		Process parent = processService.getProcessById(parentProcessId);
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
			result = importThisItem(it);
		}
		items.clear();
		newStep = processService.closeOldAndCreateNewStep(oldStep, "Finally Cleaning up", stepNum++, 0.25, parent);
		manageOrphanBatchDetails();
		if (result == 1) {
			processService.completeProcessStep(newStep);
		} else {
			processService.failStep(newStep, parent);
		}
		return (result == 1) ? "Success" : "Error";
	}

//	@Override
//	public Future<String> importDataFromOutlookAsync(Process parent) {
//		return executor.submit(() -> {
//			importDataFromOutlook(parent);
//			return null;
//		});
//	}

	@Transactional
	private void manageOrphanBatchDetails() {
		logger.info("Retrieve all Batch Details that are either orphans or effectvely inactive.");
		List<BatchDetails> batchDetails = batchService.findAllOrphanBatchDetails();
		logger.info("{} Orphan or effectively inactive Batch Details found.", batchDetails.size());

		for (BatchDetails batch : batchDetails) {
			batch.setEndDate(Calendar.getInstance().getTime());
			batch.setActive(false);
		}
		if (batchDetails.size() > 0) {
			batchService.saveBatchDetails(batchDetails);
			logger.info("{} orphan Batch Details marked inactive successfully", batchDetails.size());
		}
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
				saveImportItem(item);
				logger.error("Item id : " + item.getId());
				logger.error(e.getMessage());
				logger.error(e.getStackTrace().toString());
				logger.error("Failed importing Item : " + item);
				logger.error("Retrying to import again. Attempt number : {} out of 3", retries + 1);
			}
		}
		if (item.hasFailedImport())
			return 0;
		logger.info("{} ready for import.", item.getName());
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
			saveImportItem(item);
			return 0;
		}
		return 1;
	}

	@Transactional
	private ImportItem startImport(ImportItem item) {
		logger.info("Importing item : " + item);
		item.readyForImport();
		item = saveImportItem(item); // Save raw data to import tables
		return item;
	}

	private ImportItem save(ImportItem item) throws TSHException {
		logger.info("Extracting data from Import Item...");
		item.startImport();
		item = saveImportItem(item);

		int itemId = item.getId();
		int batchWeekDay = item.getBatchWeekDay();
		Time batchStartTime = item.getBatchStartTime();
		int gradeNumber = item.getGradeNumber();
		String subject = item.getSubject();
		String teacherName = item.getTeacher();

		if (item.getName().length() == 0 || item.getSubject().length() == 0 || item.getGrade().length() == 0) {
			logger.info("No Data found in Item : {}. Nothing to Import.", item.getId());
			item.imported();
			item = saveImportItem(item);
			return item;
		}

		// Get All master data from DB. We would need them to validate and create new
		// instances.
		TimeSlot timeSlot = generalService
				.getTimeSlot(item.getBatchWeekDay(), item.getBatchStartTime(), item.getBatchEndTime())
				.orElseThrow(() -> new TSHException(
						"Item id : " + itemId + " No Time Slot found : " + batchWeekDay + " : " + batchStartTime));
		Grades grade = generalService.getGrades(item.getGradeNumber())
				.orElseThrow(() -> new TSHException("Item id : " + itemId + "Grade not found : " + gradeNumber));
		Course course = generalService.getCourses(item.getSubject())
				.orElseThrow(() -> new TSHException("Item id : " + itemId + "Course not found : " + subject));
		Teacher teacher = teacherService.getTeachers(item.getTeacher())
				.orElseThrow(() -> new TSHException("Item id : " + itemId + "Teacher not found : " + teacherName));
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
			student = Student.getNewInstance(item, grade);
			student = studentService.saveStudent(student);

			studentBatches = StudentBatches.getNewInstance(student, course);
			studentBatches.setBatchDetails(batchDetails);
			logger.info(studentBatches.toString());
		} else { // Old Student. Maybe the batch Changed.
			logger.info("Student record found for : {}", item.getName());
			studentBatches = studentService.getStudentBatches(student, course)
					.orElse(StudentBatches.getNewInstance(student, course));
			if (studentBatches.getBatchDetails() == null)
				studentBatches.setBatchDetails(batchDetails);

			if (studentBatches.getId() != 0 && !studentBatches.getBatchDetails().equals(batchDetails)) { // If the batch
																											// has been
																											// changed.
				logger.info("Old Batch and current bactch mismatch. Assigning new Batch to {}", item.getName());
				StudentBatches newStudentBatch = StudentBatches.getNewInstance(student, course); // Create a new Student
																									// Batch. We want to
																									// keep the history.
				studentBatches.setEndDate(Calendar.getInstance().getTime()); // Set old studentBatch end date to
																				// deactivate
				newStudentBatch.setBatchDetails(batchDetails);
				logger.info("Preparing to save import data in Database...");
				studentService.save(newStudentBatch);
				logger.info("Import Data saved successfully to database.");
			} else {
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

}
