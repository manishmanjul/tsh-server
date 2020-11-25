package com.tsh.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tsh.entities.ProcessDetails;

@Repository
public interface ProcessDetailsRepository extends JpaRepository<ProcessDetails, Integer> {
}
