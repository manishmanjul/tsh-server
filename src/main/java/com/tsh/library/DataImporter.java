package com.tsh.library;

import java.util.Date;
import java.util.List;

import com.tsh.entities.ImportItem;
import com.tsh.entities.Process;
import com.tsh.exception.TSHException;

public interface DataImporter {
	public List<ImportItem> importData(Process parent) throws TSHException;

	public void setStartDate(Date startDate);

	public void setEndDate(Date endDate);

	public void setStartAndEndDates(Date startDate, Date endDate);
}
