package com.tsh.rest.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tsh.entities.Role;
import com.tsh.entities.Teacher;
import com.tsh.entities.User;
import com.tsh.library.dto.ResponseMessage;
import com.tsh.library.dto.SimpleStringRequest;
import com.tsh.library.dto.TeacherTO;
import com.tsh.library.dto.UserPrinciple;
import com.tsh.library.dto.UserTO;
import com.tsh.service.ILoginService;
import com.tsh.service.ITeacherService;

@RestController
@RequestMapping("/tsh/Signup")
public class SignupController {

	@Autowired
	private ILoginService loginService;
	@Autowired
	private ITeacherService teacherServive;
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@PostMapping
	public ResponseEntity<?> registerUser(@RequestBody UserTO newUser) throws Exception {

		Role role = null;
		if (newUser.getName().equalsIgnoreCase("Admin")) {
			role = validateAndGetRole("Administrator"); // Get Admin role
		} else {
			role = validateAndGetRole(newUser.getRole());
		}

		if (role == null) {
			return ResponseEntity.ok(ResponseMessage.INVALID_ROLE);
		}

		ModelMapper mapper = new ModelMapper();
		User userToRegister = mapper.map(newUser, User.class);
		userToRegister.setRole(role);

		if (loginService.isExistingUser(userToRegister)) {
			return ResponseEntity.ok(ResponseMessage.USER_ALREADY_EXIST);
		}

		// Check for just the user name. Not the entire User Object.
		if (loginService.isUserNameExist(userToRegister.getName())) {
			return ResponseEntity.ok(ResponseMessage.USER_ALREADY_EXIST);
		}

		try {
			Teacher teacher = null;
			if (StringUtils.isNumeric(newUser.getTeacherName())) {
				teacher = teacherServive.findById(Integer.parseInt(newUser.getTeacherName()));
			} else {
				teacher = teacherServive.findByName(newUser.getTeacherName());
			}

			userToRegister.setFirstLogin(true);
			userToRegister = loginService.addNewUser(userToRegister);
			if (teacher != null) {
				teacher.setUser(userToRegister);
				teacherServive.updateTeacher(teacher);
			}
		} catch (Exception e) {
			return ResponseEntity.ok(ResponseMessage.FAILED_TO_REGISTER.appendMessage(e.getMessage()));
		}

		return ResponseEntity.ok(ResponseMessage.SUCCESSFULLY_NEW_USER_ADDED.appendMessage(userToRegister.getName()));
	}

	@GetMapping("/getTeachers")
	public List<TeacherTO> fetchAllTeachers() {
		logger.info("Getting all teachers...");
		List<Teacher> teachers = teacherServive.findAllTeachers();
		ModelMapper mapper = new ModelMapper();
		List<TeacherTO> teachersTO = teachers.stream().map(t -> mapper.map(t, TeacherTO.class))
				.collect(Collectors.toList());
		// Add an entry for Admin
		TeacherTO admin = new TeacherTO();
		admin.setId(999999);
		admin.setTeacherName("Admin");
		teachersTO.add(admin);
		return teachersTO;
	}

	@PostMapping("/isExist")
	public ResponseMessage validateUser(@RequestBody SimpleStringRequest request) {
		logger.info("Validating if user name already exists ...");
		ResponseMessage response = null;

		User usr = new User();
		usr.setName(request.getRequest());

		boolean result = loginService.isExistingUser(usr);
		if (result) {
			response = ResponseMessage.GENERAL_SUCCESS;
		} else {
			response = ResponseMessage.GENERAL_FAIL;
		}
		return response;
	}

	@PostMapping("/changePass")
	public ResponseEntity<String> changePassword(@RequestBody SimpleStringRequest request) {
		ResponseEntity<String> response = null;

		UserPrinciple principle = (UserPrinciple) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User user = principle.getUser();
		logger.info("Request to change password for user {}", user.getName());
		user.setPassword(request.getRequest());
		user.setFirstLogin(false);

		user = loginService.updateUser(user);

		if (user == null) {
			logger.error("Unable to change password.");
			HttpHeaders headers = new HttpHeaders();
			headers.set("statusCode", "204");
			headers.set("message", "Unable to change password");
			response = new ResponseEntity<String>("Unable to change password", headers,
					HttpStatus.INTERNAL_SERVER_ERROR);
		} else {
			logger.info("Password successfully changed.");
			HttpHeaders headers = new HttpHeaders();
			headers.set("statusCode", "202");
			headers.set("message", "Password changed");
			response = new ResponseEntity<>("Password changed", headers, HttpStatus.ACCEPTED);
		}
		return response;
	}

	private Role validateAndGetRole(String roleName) {
		return loginService.getRole(roleName);
	}
}
