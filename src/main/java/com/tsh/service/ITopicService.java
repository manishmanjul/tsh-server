package com.tsh.service;

import java.text.ParseException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tsh.entities.BatchDetails;
import com.tsh.entities.Course;
import com.tsh.entities.Grades;
import com.tsh.entities.TopicProgress;
import com.tsh.entities.TopicStatus;
import com.tsh.entities.Topics;
import com.tsh.exception.TSHException;
import com.tsh.library.dto.TopicGenerationRequest;
import com.tsh.library.dto.TopicRequest;
import com.tsh.library.dto.TopicResponse;

@Service
public interface ITopicService extends TshService {

	public int generateNewTopics(TopicGenerationRequest request) throws TSHException;

	public Topics updateTopic(TopicRequest topicRequest) throws TSHException;

	public Topics getTopicById(int topicId);

	Topics getTopicByMappedId(String mappedId) throws TSHException;

	List<Topics> saveAllTopics(List<Topics> topicList);

	TopicStatus getTopicStatusByStatus(String status);

	List<TopicProgress> saveAllTopicProgress(List<TopicProgress> progressList);

	List<Topics> getAllActiveTopicsForCourseAndGrade(Course course, Grades grade);

	public Topics getCurrentTopic(BatchDetails batchDetails) throws ParseException, TSHException;

	public Topics getNextTopic(BatchDetails batchDetails) throws ParseException, TSHException;

	public List<Topics> getAllActiveTopics();

	public List<TopicResponse> getAllActiveTopicsAsTO();

}
