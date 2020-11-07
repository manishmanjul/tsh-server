package com.tsh.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tsh.entities.BaseEntity;
import com.tsh.entities.Course;
import com.tsh.entities.Feedback;
import com.tsh.entities.Grades;
import com.tsh.entities.Student;
import com.tsh.entities.StudentBatches;
import com.tsh.entities.StudentFeedback;
import com.tsh.entities.TopicProgress;
import com.tsh.entities.TopicStatus;
import com.tsh.entities.Topics;
import com.tsh.exception.TSHException;
import com.tsh.service.IFeedbackService;
import com.tsh.service.IGeneralService;
import com.tsh.service.IStudentService;
import com.tsh.service.ITopicService;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/tsh/feedback")
public class FeedbackReader {

	private final int CONTROL_LINE = 1;
	private final int PROGRESS_HEADER = 2;
	private final int PROGRESS_LINE = 3;
	private final int FEEDBACK_HEADER = 4;
	private final int FEEDBACK_LINE = 5;
	private final int ABSENCE_HEADER = 6;
	private final int ABSENCE_LINE = 7;
	private int foundHeader = 0;

	@Autowired
	private IStudentService studentService;
	@Autowired
	private IFeedbackService feedbackService;
	@Autowired
	private ITopicService topicService;
	@Autowired
	private IGeneralService generalService;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public FeedbackReader() {
	}

	@GetMapping
	public String process() throws TSHException, IOException, ParseException {
		File directory = new File("./Feedback");
		List<TopicProgress> progressList = new ArrayList<>();
		List<StudentFeedback> feedbacks = new ArrayList<>();

		if (!directory.exists()) {
			System.out.println("Directory does not exist");
			throw new TSHException("Directory does not exist");
		}

		File[] fileList = directory.listFiles();
		System.out.println("Number of files found : " + fileList.length);
		int i = 1;
		for (File file : fileList) {

			FileInputStream fis = new FileInputStream(file);
			Student stud = null;
			Course course = null;
			StudentBatches batches = null;
			InputStreamReader ir = new InputStreamReader(fis);
			BufferedReader reader = new BufferedReader(ir);
			String line = null;
			logger.info("Reading file number : {} - {}", i++, file.getName());
			while ((line = reader.readLine()) != null) {
				int lineType = identifyLineType(line);

				switch (lineType) {
				case CONTROL_LINE:
					Map<String, BaseEntity> val = getStudentAndCourse(stud, course, line);
					stud = (Student) val.get("student");
					course = (Course) val.get("course");
					batches = studentService.getStudentBatches(stud, course)
							.orElseThrow(() -> new TSHException("Student Batch not found"));
					break;
				case PROGRESS_HEADER:
					break;
				case PROGRESS_LINE:
					progressList.addAll(processProgress(line, stud, course));
					break;
				case FEEDBACK_HEADER:
					break;
				case FEEDBACK_LINE:
					feedbacks.addAll(extractFeedback(line, batches));
					break;
				case ABSENCE_HEADER:
					break;
				case ABSENCE_LINE:
					break;
				default:
					break;
				}
			}
			logger.info("{} feedbacks read\n", feedbacks.size());
			reader.close();
		}
		feedbackService.saveAllStudentFeedbacks(feedbacks);
		topicService.saveAllTopicProgress(progressList);
		logger.info("Save {} feedbacks and {} topic Progress.", feedbacks.size(), progressList.size());
		return "Saved " + feedbacks.size() + " feedbacks and " + progressList.size() + " topic Progress.";
	}

	private List<StudentFeedback> extractFeedback(String line, StudentBatches batches)
			throws ParseException, TSHException {
		List<StudentFeedback> feedbacks = new ArrayList<>();
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy h:mm:ss a");

		StringTokenizer token = new StringTokenizer(line, "||");
		Date feedbackDate = formatter.parse(token.nextToken().trim());

		String feedbackStr = token.nextToken();
		feedbackStr = feedbackStr.replaceAll("::", "||");
		feedbackStr = feedbackStr.replaceAll(":", "||");
		feedbackStr = feedbackStr.replaceAll("-", "||");

		StringTokenizer token2 = new StringTokenizer(feedbackStr, "||");
		String mappedIdStr = token2.nextToken().trim();
		Topics topic = topicService.getTopicByMappedId(mappedIdStr);

		String perfHeader = "";
		while (!perfHeader.equalsIgnoreCase("Performance")) {
			perfHeader = token2.nextToken().trim();
		}

		// Handle Category 1 feedback
		String perfStr = token2.nextToken();
		token = new StringTokenizer(perfStr, ",");
		while (token.hasMoreTokens()) {
			StudentFeedback studFeedback = new StudentFeedback();
			studFeedback.setFeedbackDate(feedbackDate);
			studFeedback.setStudentBatches(batches);
			studFeedback.setTeacher(batches.getBatchDetails().getTeacher());
			studFeedback.setTopic(topic);
			String cat1Feedback = token.nextToken().trim();
			if (cat1Feedback.length() > 0) {
				Feedback feedback = feedbackService.getFeedbackByShortDescription(cat1Feedback);
				studFeedback.setFeedback(feedback);
				feedbacks.add(studFeedback);
			}
		}

		// Handle category 2 feedback
		token2.nextToken(); // Don't need the COncenrs with text
		String generalStr = token2.nextToken().trim();
		token = new StringTokenizer(generalStr, ",");
		while (token.hasMoreTokens()) {
			StudentFeedback studentFeedback = new StudentFeedback();
			studentFeedback.setFeedbackDate(feedbackDate);
			studentFeedback.setStudentBatches(batches);
			studentFeedback.setTeacher(batches.getBatchDetails().getTeacher());
			studentFeedback.setTopic(topic);
			String cat2Feedback = token.nextToken().trim();
			if (cat2Feedback.length() > 0) {
				Feedback feedback = feedbackService.getFeedbackByShortDescription(cat2Feedback);
				studentFeedback.setFeedback(feedback);
				feedbacks.add(studentFeedback);
			}
		}

		// Handle category 3 feedback
		String cat3Str = token2.nextToken().trim();
		String feedbackText = null;
		if (token2.hasMoreTokens())
			feedbackText = token2.nextToken().trim();
		else
			feedbackText = "";
		if (feedbackText.length() > 0) {
			StudentFeedback studentFeedback = new StudentFeedback();
			studentFeedback.setFeedbackDate(feedbackDate);
			studentFeedback.setStudentBatches(batches);
			studentFeedback.setTeacher(batches.getBatchDetails().getTeacher());
			studentFeedback.setTopic(topic);
			Feedback feedback = feedbackService.getFeedbackByShortDescription(cat3Str);
			studentFeedback.setFeedback(feedback);
			studentFeedback.setFeedbackText(feedbackText);
			feedbacks.add(studentFeedback);
		}

		return feedbacks;
	}

	private List<TopicProgress> processProgress(String line, Student student, Course course) throws TSHException {
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
		List<TopicProgress> progressList = new ArrayList<>();
		StringTokenizer mainToken = new StringTokenizer(line, ";");
		while (mainToken.hasMoreTokens()) {
			StringTokenizer token = new StringTokenizer(mainToken.nextToken(), ",");
			Topics topic = new Topics();
			topic.setMappedId(token.nextToken());
			topic = topicService.getTopicByMappedId(topic.getMappedId());
			TopicProgress topicProgress = new TopicProgress();
			topicProgress.setTopic(topic);
			try {
				topicProgress.setStartDate(formatter.parse(token.nextToken()));

				if (token.hasMoreTokens())
					topicProgress.setEndDate(formatter.parse(token.nextToken()));

				topicProgress.setPlannedStartDate(topicProgress.getStartDate());
				topicProgress.setPlannedEndDate(topicProgress.getEndDate());
			} catch (ParseException e) {
				logger.info("Unable to arse date : " + e.getMessage());
				throw new TSHException("Unable to parse date : " + e.getMessage());
			}

			if (topicProgress.getStartDate() != null && topicProgress.getEndDate() != null)
				topicProgress.setStatus(topicService.getTopicStatusByStatus(TopicStatus.COMPLETED));
			if (topicProgress.getStartDate() != null && topicProgress.getEndDate() == null)
				topicProgress.setStatus(topicService.getTopicStatusByStatus(TopicStatus.IN_PROGRESS));
			if (topicProgress.getStartDate() != null && topicProgress.getEndDate() != null)
				topicProgress.setStatus(topicService.getTopicStatusByStatus(TopicStatus.COMPLETED));

			topicProgress.setStudent(student);
			topicProgress.setCourse(course);

			progressList.add(topicProgress);
		}
		return progressList;
	}

	private Map<String, BaseEntity> getStudentAndCourse(Student stud, Course course, String line)
			throws ParseException, TSHException {

		stud = new Student();

		StringTokenizer token = new StringTokenizer(line, ",");

		stud.setStudentName(token.nextToken());
		course = new Course(token.nextToken());
		token = new StringTokenizer(token.nextToken(), " ");
		token.nextToken();
		String temp = token.nextToken();
		char lastChar = temp.charAt(temp.length() - 1);
		if (!Character.isDigit(lastChar)) {
			course.setShortDescription(course.getShortDescription() + " " + temp.charAt(temp.length() - 1));
			Grades grade = new Grades(Integer.parseInt(temp.substring(0, temp.length() - 1)));
			stud.setGrade(grade);
		} else {
			Grades grade = new Grades(Integer.parseInt(temp));
			stud.setGrade(grade);
		}

		stud.setGrade(generalService.getGrades(stud.getGrade().getGrade())
				.orElseThrow(() -> new TSHException("Grade not found")));
		stud = studentService.getStudentByNameAndGrade(stud.getStudentName(), stud.getGrade());
		course = generalService.getCourses(course.getShortDescription())
				.orElseThrow(() -> new TSHException("Course not found"));
		Map<String, BaseEntity> returnVal = new HashMap<>();
		returnVal.put("student", stud);
		returnVal.put("course", course);

		return returnVal;
	}

	private int identifyLineType(String line) {
		int lineType = 0;
		StringTokenizer token = new StringTokenizer(line, ",");
		if ((line.contains("Maths") || line.contains("English") || line.contains("GA")) && line.contains("Year")
				&& token.countTokens() == 3) {
			lineType = CONTROL_LINE;
			foundHeader = 0;
		}
		if (line.equals("PROGRESS"))
			lineType = foundHeader = PROGRESS_HEADER;
		if (line.equals("FEEDBACK"))
			lineType = foundHeader = FEEDBACK_HEADER;
		if (line.equals("ABSENCE"))
			lineType = foundHeader = ABSENCE_HEADER;
		if (foundHeader == PROGRESS_HEADER && !line.equals("PROGRESS"))
			lineType = PROGRESS_LINE;
		if (foundHeader == FEEDBACK_HEADER && !line.equals("FEEDBACK"))
			lineType = FEEDBACK_LINE;
		if (foundHeader == ABSENCE_HEADER && !line.equals("ABSENCE"))
			lineType = ABSENCE_LINE;

		return lineType;
	}

	public static void main(String args[]) throws TSHException, Exception {
		FeedbackReader reader = new FeedbackReader();
		reader.process();
	}
}
