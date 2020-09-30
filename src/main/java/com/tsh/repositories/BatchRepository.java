package com.tsh.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tsh.entities.Batch;

@Repository
public interface BatchRepository extends JpaRepository<Batch, Integer>{
}
