package com.tsh.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tsh.entities.Features;

@Repository
public interface FeaturesRepository extends JpaRepository<Features, Integer>{

	@Query("Select f from Features f where f.permission Like CONCAT('', :permissionStr, '%') order by f.order")
	public List<Features> findByRole(@Param("permissionStr") String permissionStr);
	
}
