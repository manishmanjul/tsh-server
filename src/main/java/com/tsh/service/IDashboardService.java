package com.tsh.service;

import java.text.ParseException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tsh.library.dto.DashboardItems;

@Service
public interface IDashboardService {

	public List<DashboardItems> getAllBatchesFor(String batchDate) throws ParseException;
}
