package com.tsh.service;

import org.springframework.stereotype.Service;

import com.tsh.exception.TSHException;

@Service
public interface IInitService {

	public boolean startInitialisation() throws TSHException;

	public void createAllBatchProgress() throws TSHException;

	public void completeInitialisation() throws TSHException;
}
