package com.tsh.service.impl;

import java.time.DayOfWeek;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.tsh.entities.BatchDetails;
import com.tsh.entities.BatchProgress;
import com.tsh.entities.User;
import com.tsh.library.dto.ScheduleTO;
import com.tsh.library.dto.StudentTO;
import com.tsh.service.IConverter;
import com.tsh.utility.TshUtil;

@Service
public class ConverterService implements IConverter {

	@Override
	public List<ScheduleTO> convertToScheduleTO(List<BatchDetails> batchDetails, User loggedInUser) {

		List<BatchDetails> filteredBatches = null;
		if (loggedInUser.isTeacher1() || loggedInUser.isTeacher2()) {
			filteredBatches = batchDetails.stream().filter(b -> b.isThisMyClass(loggedInUser))
					.collect(Collectors.toList());
		} else {
			filteredBatches = batchDetails;
		}

		List<ScheduleTO> scheduleTOs = filteredBatches.stream().map(b -> convertToScheduleTO(b))
				.collect(Collectors.toList());
		return scheduleTOs;
	}

	@Override
	public ScheduleTO convertToScheduleTO(BatchDetails batchDetails) {
		ScheduleTO schedule = new ScheduleTO();
		schedule.setKey(batchDetails.getId() + "");
		schedule.setCourseId(batchDetails.getCourse().getId());
		schedule.setCourse(batchDetails.getCourse().getShortDescription());
		schedule.setCourseDescription(batchDetails.getCourse().getDescription());
		schedule.setDay(DayOfWeek.of(batchDetails.getBatch().getTimeSlot().getWeekday()).minus(1).toString());
		schedule.setStartTime(batchDetails.getBatch().getTimeSlot().getStartTime().toString());
		schedule.setEndTime(batchDetails.getBatch().getTimeSlot().getEndTime().toString());
		schedule.setGrade(batchDetails.getGrade().getGrade());
		schedule.setTeacherName(batchDetails.getTeacher().getTeacherName());

		schedule.setAttendies(batchDetails.getStudentBatchList().stream().map(s -> {
			StudentTO studentTo = new StudentTO();
			studentTo.setName(s.getStudent().getStudentName());
			return studentTo;
		}).collect(Collectors.toList()));

		BatchProgress progress = batchDetails.getBatchProgress();
		if (progress != null) {

			if (progress.getPlannedStartDate() != null) {
				int weekDay = TshUtil.getWeekDayOf(progress.getPlannedStartDate());
				schedule.setDay(DayOfWeek.of(weekDay).minus(1).toString());
			}

			if (progress.getPlannedTime() != null)
				schedule.setStartTime(progress.getPlannedTime().toString());
			if (progress.getTeacher() != null)
				schedule.setTeacherName(progress.getTeacher().getTeacherName());
			if (progress.isCanceled())
				schedule.cancelBatch();
		}

		return schedule;
	}

}
