package com.tsh.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tsh.entities.Term;

public interface TermRepository extends JpaRepository<Term, Integer>{
	public List<Term> findByTerm(int termNumber);
}
