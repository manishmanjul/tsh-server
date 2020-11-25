package com.tsh.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tsh.entities.Topics;
import com.tsh.exception.TSHException;

@Service
public interface IDataImportService extends TshService {

	String importDataFromOutlook(int parentProcessId) throws Exception;

//	Future<String> importDataFromOutlookAsync(Process Parent);

	Map<String, List<Topics>> processFile(MultipartFile importedFile, String command) throws TSHException;

}
