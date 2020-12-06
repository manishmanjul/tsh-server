package com.tsh.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tsh.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	public User findByName(String name);

	public List<User> findAllByName(String name);

	public boolean existsByName(String name);

}
