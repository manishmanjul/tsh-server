package com.tsh.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tsh.entities.Features;
import com.tsh.entities.Role;
import com.tsh.entities.User;
import com.tsh.library.dto.FeaturesTO;
import com.tsh.library.dto.UserPrinciple;
import com.tsh.repositories.FeaturesRepository;
import com.tsh.repositories.RolesRepository;
import com.tsh.repositories.UserRepository;
import com.tsh.service.ILoginService;

@Service
public class LoginService implements ILoginService {

	@Autowired
	private UserRepository userRepo;
	@Autowired
	private FeaturesRepository featuresRepo;
	@Autowired
	private RolesRepository rolesRepo;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		User user = userRepo.findByName(username);
		if (user == null)
			throw new UsernameNotFoundException("User 404");
		return new UserPrinciple(user);
	}

	@Override
	public List<Features> finAllFeaturesByRole(Role role) {

		return featuresRepo.findByRole(role.getPermissionString());
	}

	@Override
	public Role getRole(String role) {
		return rolesRepo.findByRoleName(role);
	}

	@Override
	public User addNewUser(User newUser) {
		return userRepo.save(newUser);
	}

	@Override
	public boolean isExistingUser(User newUser) {
		List<User> users = userRepo.findAllByName(newUser.getName());
		for (User user : users) {
			if (user.equals(newUser))
				return true;
		}
		return false;
	}

	@Override
	public boolean isUserNameExist(String userName) {
		return userRepo.existsByName(userName);
	}

	@Override
	public List<FeaturesTO> findAllFeaturesByRoleAndContainerPage(Role role, String pageName) {
		List<Features> featureList = null;
		List<FeaturesTO> featureTOList = null;

		featureList = featuresRepo.findByPermissionAndPage(role.getPermissionString(), pageName);
		ModelMapper mapper = new ModelMapper();

		featureTOList = featureList.stream().map(f -> {
			FeaturesTO featureTO = mapper.map(f, FeaturesTO.class);
			featureTO.setKey("" + f.getId());
			return featureTO;
		}).collect(Collectors.toList());

		logger.info("{} features fetched for {}", featureTOList.size(), pageName);
		return featureTOList;
	}

	@Override
	public User updateUser(User usr) {
		return userRepo.save(usr);
	}
}
