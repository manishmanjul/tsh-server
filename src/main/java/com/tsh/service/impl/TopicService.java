package com.tsh.service.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.tsh.entities.BatchDetails;
import com.tsh.entities.BatchProgress;
import com.tsh.entities.Course;
import com.tsh.entities.Grades;
import com.tsh.entities.Term;
import com.tsh.entities.TopicProgress;
import com.tsh.entities.TopicStatus;
import com.tsh.entities.Topics;
import com.tsh.entities.Week;
import com.tsh.exception.TSHException;
import com.tsh.library.dto.CourseGenTO;
import com.tsh.library.dto.GradeTO;
import com.tsh.library.dto.TermTO;
import com.tsh.library.dto.TopicGenerationRequest;
import com.tsh.library.dto.TopicRequest;
import com.tsh.library.dto.TopicResponse;
import com.tsh.repositories.TopicProgressRepository;
import com.tsh.repositories.TopicStatusRepository;
import com.tsh.repositories.TopicsRepository;
import com.tsh.service.IGeneralService;
import com.tsh.service.IProgressService;
import com.tsh.service.ITopicService;

@Service
public class TopicService implements ITopicService {

	@Autowired
	private IGeneralService generalService;
	@Autowired
	private IProgressService progressService;
	@Autowired
	private TopicsRepository topicsRepo;
	@Autowired
	private TopicStatusRepository topicStatusRepo;
	@Autowired
	private TopicProgressRepository topicProgressRepo;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public Topics getTopicById(int topicId) {
		return topicsRepo.findById(topicId).orElse(null);
	}

	@Override
	public Topics getTopicByMappedId(String mappedId) throws TSHException {
		List<Topics> topicList = topicsRepo.findByMappedId(mappedId);
		if (topicList.size() > 1)
			throw new TSHException("Multiple topics found with mapped ID :" + mappedId);
		if (topicList.size() == 0)
			return null;
		return topicList.get(0);
	}

	@Override
	public List<Topics> saveAllTopics(List<Topics> topicList) {
		return topicsRepo.saveAll(topicList);
	}

	@Override
	public TopicStatus getTopicStatusByStatus(String status) {
		return topicStatusRepo.findByStatus(status);
	}

	@Override
	public List<TopicProgress> saveAllTopicProgress(List<TopicProgress> progressList) {
		return topicProgressRepo.saveAll(progressList);
	}

	/**
	 * This method will only return topics for the current term.
	 */
	@Override
	public List<Topics> getAllActiveTopicsForCourseAndGrade(Course course, Grades grade) {
		Term term = generalService.getCurrentTerm().orElse(new Term(0));
		return topicsRepo.findAllByCourseAndGradeAndTermAndActive(course, grade, term, true);
	}

	/**
	 * The method will return a topic that is expected to be learned today. The
	 * search begins in the BatchProgress Table. If there is a batch progress that
	 * is in progress and today is the batch class day, then the topic in the latest
	 * in-progress Batch Progress is returned. In case there are no in-progress
	 * BatchProgress, then the method will look for any planned Batch Progress and
	 * will return its topic only if the class date and planned date are same.
	 * Otherwise a blank topic is returned.
	 */
	@Override
	public Topics getCurrentTopic(BatchDetails batchDetails) throws ParseException, TSHException {

		Topics topicToReturn = null;

		BatchProgress batchProgress = progressService.getBatchProgressAsOfToday(batchDetails);
		// If this batch class timing is not today. return an empty topic.
		if (batchProgress != null) {
			topicToReturn = batchProgress.getTopic();
		}
		if (topicToReturn == null) {
			topicToReturn = new Topics();
			topicToReturn.setDescription("No Topic assigned for Today");
		}

		return topicToReturn;
	}

	/**
	 * This method will return a topic that is expected to start next week or next
	 * class. Will check the Batch progress table. If there is a batch progress
	 * planned for next class. It will return that topic else if there is anything
	 * in progress it will return the topic in progress. If nothing found, it will
	 * return an Empty topic.
	 */
	@Override
	public Topics getNextTopic(BatchDetails batchDetails) throws ParseException, TSHException {
		Topics topicToReturn = null;

		BatchProgress progress = progressService.getNextPlannedBatchProgress(batchDetails);
		if (progress != null)
			topicToReturn = progress.getTopic();
		else {
			topicToReturn = new Topics();
			topicToReturn.setDescription("No Topic planned so far");
		}

		return topicToReturn;
	}

//	private Topics findInProgressTopic(BatchDetails batchDetails) {
//		Topics topicToReturn = null;
//		TopicStatus inProgress = topicStatusRepo.findByStatus("In Progress");
//
//		List<BatchProgress> batchProgressList = progressService.getAllBatchProgressForStatus(batchDetails, inProgress);
//		if (batchProgressList.size() > 0) {
//			topicToReturn = batchProgressList.get(0).getTopic();
//		}
//
//		return topicToReturn;
//	}
//
//	private Topics findPlannedTopicBetween(BatchDetails batchDetails, Calendar startOfWeek, Calendar endOfWeek) {
//		Topics topicToReturn = null;
//		TopicStatus planned = topicStatusRepo.findByStatus("Planned");
//
//		List<BatchProgress> batchProgressList = progressService.getAllBatchProgressForStatus(batchDetails, planned);
//		for (BatchProgress batchProgress : batchProgressList) {
//			Calendar plannedDate = Calendar.getInstance();
//			plannedDate.setTime(batchProgress.getPlannedStartDate());
//			if ((plannedDate.after(startOfWeek) || plannedDate.equals(startOfWeek))
//					&& (plannedDate.before(endOfWeek) || plannedDate.equals(endOfWeek))) {
//				topicToReturn = batchProgress.getTopic(); // The first planned topic found is good enough. Break the
//															// loop
//				break;
//			}
//		}
//		return topicToReturn;
//	}

	@Override
	public int generateNewTopics(TopicGenerationRequest request) throws TSHException {
		List<Week> weekList = generalService.getAllWeekRange(0, 12);
		List<Topics> topicList = new ArrayList<>();

		for (TermTO termTO : request.getTermResponse()) {
			Term term = generalService.getTerm(termTO.getTerm());

			for (GradeTO gradeTO : request.getGradeResponse()) {
				Grades grade = generalService.getGrades(gradeTO.getGrade()).orElse(null);
				if (grade == null)
					throw new TSHException("No Grade found : " + gradeTO.getGrade());

				for (CourseGenTO courseGenTO : request.getCourseResponse()) {
					Course course = generalService.getCourse(courseGenTO.getId());

					logger.info("Generating topic for Term {} - Grade {} - {}", termTO.getTerm(), gradeTO.getGrade(),
							courseGenTO.getDescription());

					for (int i = 1; i <= 12; i++) {
						Topics topic = new Topics();
						topic.setChapter(term.getDescription() + " " + weekList.get(i - 1).getDescription());
						topic.setCourse(course);
						topic.setDefaultComplexity();
						topic.setDefaultEstimatedHours();
						topic.setDescription("");
						topic.setGrade(grade);
						if (course.getDescription().contains("English"))
							topic.setHoursToComplete(1.0);
						else
							topic.setHoursToComplete(1.5);
						topic.setTerm(term);
						topic.setTopicName("");
						topic.setWeek(weekList.get(i - 1));
						topic.setActive(true);

						topicList.add(topic);
					}
				}
			}
		}
		logger.info("{} new topics were generated", topicList.size());

		topicsRepo.saveAll(topicList);
		return topicList.size();
	}

	@Cacheable("TshCache")
	@Override
	public List<Topics> getAllActiveTopics() {
		return topicsRepo.findAllByActiveOrderByGradeId(true);
	}

	@Override
	public List<TopicResponse> getAllActiveTopicsAsTO() {

		ModelMapper mapper = new ModelMapper();
		List<Topics> topicList = this.getAllActiveTopics();
		List<TopicResponse> topicTOList = null;

		topicTOList = topicList.stream().map(t -> {
			TopicResponse topicTO = mapper.map(t, TopicResponse.class);
			topicTO.setGrade(t.getGrade().getGrade() + "");
			topicTO.setTerm(t.getTerm().getTerm() + "");
			topicTO.setCourse(t.getCourse().getShortDescription());
			topicTO.setWeek(t.getWeek().getWeekNumber() + "");
			topicTO.setActive(1);
			return topicTO;
		}).collect(Collectors.toList());

		return topicTOList;
	}

	@Override
	public Topics updateTopic(TopicRequest topicRequest) throws TSHException {
		Topics topic = topicsRepo.findById(topicRequest.getId())
				.orElseThrow(() -> (new TSHException("No Topic Found")));
		topic.setActive(topicRequest.getActive() == 1 ? true : false);
		topic.setChapter(topicRequest.getChapter());
		topic.setComplexity(topicRequest.getComplexity());
		topic.setDescription(topicRequest.getDescription());
		topic.setTopicName(topicRequest.getTopicName());
		if (!topic.getCourse().getShortDescription().equalsIgnoreCase(topicRequest.getCourse())) {
			Course course = generalService.getCourses(topicRequest.getCourse())
					.orElseThrow(() -> new TSHException("No Course Found"));
			topic.setCourse(course);
		}

		if (topic.getGrade().getGrade() != Integer.parseInt(topicRequest.getGrade())) {
			Grades grade = generalService.getGrades(Integer.parseInt(topicRequest.getGrade()))
					.orElseThrow(() -> new TSHException("No HGrade found"));
			topic.setGrade(grade);
		}

		if (topic.getTerm().getTerm() != Integer.parseInt(topicRequest.getTerm())) {
			Term term = generalService.getTerm(Integer.parseInt(topicRequest.getTerm()));
			topic.setTerm(term);
		}

		if (topic.getWeek().getWeekNumber() != Integer.parseInt(topicRequest.getWeek())) {
			Week week = generalService.getWeekByWeekNumber(Integer.parseInt(topicRequest.getWeek()));
			topic.setWeek(week);
		}

		return topicsRepo.save(topic);
	}

	@Override
	public void createAllTopics(List<TopicRequest> topicList) {
		List<Topics> topics = new ArrayList<>();
		ModelMapper mapper = new ModelMapper();

		for (TopicRequest r : topicList) {
			Topics t = mapper.map(r, Topics.class);
			t.setActive(true);
			t.setDefaultComplexity();
			t.setDefaultEstimatedHours();
			t.setCourse(generalService.getCourses(r.getCourse()).orElse(null));
			t.setGrade(generalService.getGrades(Integer.parseInt(r.getGrade())).orElse(null));
			t.setTerm(generalService.getTerm(Integer.parseInt(r.getTerm())));
			t.setWeek(generalService.getWeekByWeekNumber(Integer.parseInt(r.getWeek())));
			topics.add(t);
		}

		this.saveAllTopics(topics);
		logger.info("{} topics saved successfully.", topics.size());
	}

	@Override
	public Topics getForGradeCourseTermAndWeek(Grades grade, Course course, Term term, Week week) {
		List<Topics> topicList = topicsRepo.findByGradeAndCourseAndTermAndWeekAndActive(grade, course, term, week,
				true);
		if (topicList.size() > 0)
			return topicList.get(0);
		else
			return null;
	}
}
