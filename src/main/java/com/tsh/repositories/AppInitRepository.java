package com.tsh.repositories;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tsh.entities.AppInit;

@Repository
public interface AppInitRepository extends JpaRepository<AppInit, Integer> {

	public AppInit findByInitForAndActivity(Date initFor, String activity);
}
