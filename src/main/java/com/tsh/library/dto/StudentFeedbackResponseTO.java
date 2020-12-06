package com.tsh.library.dto;

import java.util.ArrayList;
import java.util.List;

public class StudentFeedbackResponseTO {

	List<TopicsTO> topics;
	ResponseMessage message;

	public List<TopicsTO> getTopics() {
		return topics;
	}

	public void setTopics(List<TopicsTO> topics) {
		this.topics = topics;
	}

	public void addTopic(TopicsTO topic) {
		if (topics == null)
			topics = new ArrayList<>();
		topics.add(topic);
	}

	public ResponseMessage getMessage() {
		return message;
	}

	public void setMessage(ResponseMessage message) {
		this.message = message;
	}

}
