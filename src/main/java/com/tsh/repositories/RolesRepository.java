package com.tsh.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tsh.entities.Role;

@Repository
public interface RolesRepository extends JpaRepository<Role, Integer>{
	public Role findByRoleName(String roleName);
}
