package com.tsh.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tsh.entities.Process;

@Repository
public interface ProcessRepository extends JpaRepository<Process, Integer> {
}
