package com.tsh.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tsh.entities.ImportItem;

@Repository
public interface ImportItemRepository extends JpaRepository<ImportItem, Integer> {

	public List<ImportItem> findByNameAndGradeAndSubject(String name, String grade, String subject);

	@Query(value = "select max(cycle) from tsh.import_item", nativeQuery = true)
	public int getLastCycle();

	public List<ImportItem> findByCycle(int cycle);

	@Query(value = "select message as importDesc, status, count(*) as count from tsh.import_item where cycle = :cycle group by message", nativeQuery = true)
	public List<IImportStats> getImportStatistics(@Param("cycle") int cycle);

	@Query(value = "select max(import_date) from tsh.import_item where cycle= :cycle", nativeQuery = true)
	public Date getImportDate(@Param("cycle") int cycle);
}
