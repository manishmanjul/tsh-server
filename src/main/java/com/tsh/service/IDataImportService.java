package com.tsh.service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tsh.entities.Topics;
import com.tsh.exception.TSHException;
import com.tsh.library.dto.ImportItemTO;
import com.tsh.library.dto.ImportStatistics;
import com.tsh.library.dto.ReImportRequest;

@Service
public interface IDataImportService extends TshService {

	String importDataFromOutlook(int parentProcessId, Date startDate, Date endDate) throws Exception;

	ImportItemTO reImport(ReImportRequest request) throws TSHException, ParseException;

//	Future<String> importDataFromOutlookAsync(Process Parent);

	Map<String, List<Topics>> processFile(MultipartFile importedFile, String command) throws TSHException;

	public boolean wasImported(String name, String grade, String subject);

	public int getLastImportCycle();

	public List<ImportItemTO> getAllImportedItems(int cycleNumber);

	public List<ImportStatistics> getImportStatistics(int cycle);

	public String getImportDate(int cycle) throws TSHException;
}
