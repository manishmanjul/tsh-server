package com.tsh.library.dto;

import java.util.List;

public class CreateTopicRequest {
	private List<TopicRequest> topicRequest;

	public List<TopicRequest> getTopicRequest() {
		return topicRequest;
	}

	public void setTopicRequest(List<TopicRequest> topicRequest) {
		this.topicRequest = topicRequest;
	}
}
