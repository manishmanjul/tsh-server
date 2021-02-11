package com.tsh.service.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tsh.entities.BatchDetails;
import com.tsh.entities.BatchProgress;
import com.tsh.entities.StudentBatches;
import com.tsh.entities.Teacher;
import com.tsh.entities.TeacherDetails;
import com.tsh.library.dto.DashboardItems;
import com.tsh.library.dto.StudentTO;
import com.tsh.library.dto.TeacherTO;
import com.tsh.service.IBatchService;
import com.tsh.service.IDashboardService;
import com.tsh.service.IProgressService;
import com.tsh.service.IStudentService;
import com.tsh.service.ITeacherService;
import com.tsh.utility.TshUtil;

@Service
public class DashboardService implements IDashboardService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private IStudentService studentService;
	@Autowired
	private ITeacherService teacherService;
	@Autowired
	private IBatchService batchService;
	@Autowired
	private IProgressService progressService;

	private List<DashboardItems> convertToDashboardItems(List<BatchDetails> batchDetailsList) {
		List<DashboardItems> items = new ArrayList<DashboardItems>();
		ModelMapper mapper = new ModelMapper();
		logger.info("Convert all batchdetails to dashboard items");
		for (BatchDetails batchDetails : batchDetailsList) {
			List<StudentBatches> studentBatches = studentService.getStudentBatches(batchDetails);
			DashboardItems item = new DashboardItems();
			item.setBatchDetailId(batchDetails.getId());
			item.setStartTime(TshUtil.formatTimeToHHmm(batchDetails.getBatch().getTimeSlot().getStartTime()));
			item.setCourse(batchDetails.getCourse().getShortDescription());
			item.setGrade(batchDetails.getGrade().getGrade() + "");
			item.setTeacher(mapper.map(batchDetails.getTeacher(), TeacherTO.class));
			item.setStudentList(studentBatches.stream().map(s -> {
				StudentTO student = new StudentTO();
				student.setId(s.getId());
				student.setName(s.getStudent().getStudentName());
				return student;
			}).collect(Collectors.toList()));

			items.add(item);
		}
		return items;
	}

	/**
	 * Get all batch Details for the given date. Find it from the BatchDetails
	 * table. Then check the BatchProgress Table. Some attributes of the batch might
	 * have been modified. LIke teacher, start time or may be got cancelled. Then
	 * there might be some extra batch progress records other than what was fetched
	 * from Batch Details. Meaning - May be some batch got rescheduled to a
	 * different date. May be today, that we don't get from the batchDetails table.
	 * Add them to the dash board list
	 */
	@Override
	public List<DashboardItems> getAllBatchesFor(String batchDate) throws ParseException {
		List<BatchDetails> batchDetailsList = batchService.getAllBatchDetailsOn(batchDate);
		List<DashboardItems> dashboardItemsList = convertToDashboardItems(batchDetailsList);
		dashboardItemsList = checkForOverridesInBatchProgress(dashboardItemsList, batchDate);
		return dashboardItemsList;
	}

	/**
	 * Check if the batch properties were overridden by the administrator. Such
	 * property changes are saved in Batch Progress table.
	 * 
	 * @param dashboardItemsList
	 * @param batchDate
	 * @return
	 * @throws ParseException
	 */
	private List<DashboardItems> checkForOverridesInBatchProgress(List<DashboardItems> dashboardItemsList,
			String batchDate) throws ParseException {
		ModelMapper mapper = new ModelMapper();
		List<BatchProgress> batchProgressList = progressService.getAllBatchProgressPlannedOn(batchDate);

		for (DashboardItems dashboardItem : dashboardItemsList) {
			BatchProgress progress = findBatchBatchProgress(batchProgressList, dashboardItem);
			if (progress != null) {

				if (progress.getPlannedTime() != null)
					dashboardItem.setStartTime(TshUtil.formatTimeToHHmm(progress.getPlannedTime()));

				if (progress.getTeacher() != null)
					dashboardItem.setTeacher(mapper.map(progress.getTeacher(), TeacherTO.class));

				if (progress.isCanceled())
					dashboardItem.setCancelled(true);
				else
					dashboardItem.setCancelled(false);

				batchProgressList.remove(progress);
			}

			Teacher t = mapper.map(dashboardItem.getTeacher(), Teacher.class);
			TeacherDetails teacherDetails = teacherService.getTeacherDetails(t);
			if (teacherDetails == null) {
				dashboardItem.setBgColor("black");
				dashboardItem.setFontColor("white");
			} else {
				dashboardItem.setBgColor(teacherDetails.getBgColor());
				dashboardItem.setFontColor(teacherDetails.getFontColor());
			}
		}

		dashboardItemsList.addAll(getAllDashboadItemsFromBatchProgressList(batchProgressList));
		return dashboardItemsList;
	}

	private List<DashboardItems> getAllDashboadItemsFromBatchProgressList(List<BatchProgress> batchProgressList) {
		List<DashboardItems> newDashboardItemList = new ArrayList<>();
		ModelMapper mapper = new ModelMapper();
		for (BatchProgress batchProgress : batchProgressList) {
			if (!batchProgress.getBatchDetails().isActive())
				continue;

			DashboardItems newItem = new DashboardItems();

			newItem.setBatchDetailId(batchProgress.getBatchDetails().getId());
			if (batchProgress.getPlannedTime() != null)
				newItem.setStartTime(TshUtil.formatTimeToHHmm(batchProgress.getPlannedTime()));
			else
				newItem.setStartTime(TshUtil
						.formatTimeToHHmm(batchProgress.getBatchDetails().getBatch().getTimeSlot().getStartTime()));

			newItem.setCourse(batchProgress.getBatchDetails().getCourse().getShortDescription());
			newItem.setGrade(batchProgress.getBatchDetails().getGrade().getGrade() + "");

			if (batchProgress.getTeacher() != null)
				newItem.setTeacher(mapper.map(batchProgress.getTeacher(), TeacherTO.class));
			else
				newItem.setTeacher(mapper.map(batchProgress.getBatchDetails().getTeacher(), TeacherTO.class));

			List<StudentBatches> studentBatches = studentService.getStudentBatches(batchProgress.getBatchDetails());
			newItem.setStudentList(studentBatches.stream().map(s -> {
				StudentTO student = new StudentTO();
				student.setId(s.getId());
				student.setName(s.getStudent().getStudentName());
				return student;
			}).collect(Collectors.toList()));

			Teacher t = mapper.map(newItem.getTeacher(), Teacher.class);
			TeacherDetails teacherDetails = teacherService.getTeacherDetails(t);
			if (teacherDetails == null) {
				newItem.setBgColor("black");
				newItem.setFontColor("white");
			} else {
				newItem.setBgColor(teacherDetails.getBgColor());
				newItem.setFontColor(teacherDetails.getFontColor());
			}

			newDashboardItemList.add(newItem);
		}

		return newDashboardItemList;
	}

	private BatchProgress findBatchBatchProgress(List<BatchProgress> progressList, DashboardItems dashboardItem) {
		BatchProgress batchProgress = progressList.stream()
				.filter(pl -> pl.getBatchDetails().getId() == dashboardItem.getBatchDetailId()).findFirst()
				.orElse(null);
		return batchProgress;
	}
}
