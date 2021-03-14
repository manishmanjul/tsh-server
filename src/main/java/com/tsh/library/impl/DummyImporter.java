package com.tsh.library.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tsh.entities.ImportItem;
import com.tsh.entities.Process;
import com.tsh.exception.TSHException;
import com.tsh.library.DataImporter;

//@Component
public class DummyImporter implements DataImporter {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public List<ImportItem> importData(Process parent) throws TSHException {
		logger.info("This is a dummy importer. Nothing to Import. Plug in a actual importer implementation.");
		return null;
	}

	@Override
	public void setStartDate(Date startDate) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setEndDate(Date endDate) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setStartAndEndDates(Date startDate, Date endDate) {
		// TODO Auto-generated method stub

	}

}
