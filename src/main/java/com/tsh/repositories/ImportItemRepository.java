package com.tsh.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import com.tsh.entities.ImportItem;

@Repository
public interface ImportItemRepository extends JpaRepository<ImportItem, Integer>{}
