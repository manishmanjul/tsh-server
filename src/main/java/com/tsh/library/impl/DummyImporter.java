package com.tsh.library.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.tsh.entities.ImportItem;
import com.tsh.exception.TSHException;
import com.tsh.library.DataImporter;

@Component
public class DummyImporter implements DataImporter{

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Override
	public List<ImportItem> importData() throws TSHException {
		logger.info("This is a dummy importer. Nothing to Import. Plug in a actual importer implementation.");
		return null;
	}

}
