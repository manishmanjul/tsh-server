package com.tsh.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.tsh.entities.Features;
import com.tsh.entities.Role;
import com.tsh.entities.User;
import com.tsh.library.dto.FeaturesTO;

@Service
public interface ILoginService extends UserDetailsService {

	public List<Features> finAllFeaturesByRole(Role role);

	public Role getRole(String role);

	public User addNewUser(User newUser);

	public boolean isExistingUser(User newUser);

	public boolean isUserNameExist(String userName);

	public List<FeaturesTO> findAllFeaturesByRoleAndContainerPage(Role role, String pageName);
}
