package com.tsh.library;

import java.util.List;

import com.tsh.entities.ImportItem;
import com.tsh.exception.TSHException;

public interface DataImporter {
	public List<ImportItem> importData() throws TSHException;	
}
