package com.tsh.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tsh.entities.BatchDetails;
import com.tsh.entities.User;
import com.tsh.library.dto.ScheduleTO;

@Service
public interface IConverter extends TshService {

	public List<ScheduleTO> convertToScheduleTO(List<BatchDetails> batchDetails, User loggedInUser);

	public ScheduleTO convertToScheduleTO(BatchDetails batchDetails);
}
