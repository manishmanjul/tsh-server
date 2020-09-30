package com.tsh.rest.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tsh.entities.Role;
import com.tsh.entities.Teacher;
import com.tsh.entities.User;
import com.tsh.exception.TSHException;
import com.tsh.library.dto.ResponseMessage;
import com.tsh.library.dto.UserTO;
import com.tsh.service.ILoginService;
import com.tsh.service.ITeacherService;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/tsh/Signup")
public class SignupController {
	
	@Autowired
	private ILoginService loginService;
	@Autowired
	private ITeacherService teacherServive;
	
	@PostMapping
	public ResponseEntity<?> registerUser(@RequestBody UserTO newUser) throws Exception {
		
		Role role = validateAndGetRole(newUser.getRole());
		if(role ==null) {
			return ResponseEntity.ok(ResponseMessage.INVALID_ROLE);
		}

		ModelMapper mapper = new ModelMapper();
		User userToRegister = mapper.map(newUser, User.class);
		userToRegister.setRole(role);
		
		if(loginService.isExistingUser(userToRegister)) {
			return ResponseEntity.ok(ResponseMessage.USER_ALREADY_EXIST);
		}
		
		try {
			Teacher teacher = teacherServive.findByName(newUser.getTeacherName());
			if(teacher == null) {
				throw new TSHException("Teacher Not Found");
			}
			userToRegister = loginService.addNewUser(userToRegister);
			teacher.setUser(userToRegister);
			teacherServive.updateTeacher(teacher);			
		}catch(Exception e) {
			return ResponseEntity.ok(ResponseMessage.FAILED_TO_REGISTER.appendMessage(e.getMessage()));
		}
		
		return ResponseEntity.ok(ResponseMessage.SUCCESSFULLY_NEW_USER_ADDED.appendMessage(userToRegister.getName()));
	}
	
	private Role validateAndGetRole(String roleName){
		return loginService.getRole(roleName);
	}
}
