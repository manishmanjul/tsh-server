package com.tsh.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tsh.entities.Attendence;

@Repository
public interface AttendenceRepository extends JpaRepository<Attendence, Integer> {

}