package com.tsh.rest.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tsh.entities.Features;
import com.tsh.library.dto.AuthenticationRequest;
import com.tsh.library.dto.AuthenticationResponse;
import com.tsh.library.dto.FeatureTypeTO;
import com.tsh.library.dto.FeaturesTO;
import com.tsh.library.dto.ResponseMessage;
import com.tsh.library.dto.TeacherTO;
import com.tsh.library.dto.UserPrinciple;
import com.tsh.library.dto.UserTO;
import com.tsh.library.dto.WelcomeKit;
import com.tsh.service.ILoginService;
import com.tsh.service.ITeacherService;
import com.tsh.utility.JwtUtil;

@RestController
@RequestMapping("/tsh/login")
public class LoginController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserDetailsService userDetailService;

	@Autowired
	private JwtUtil jwtUtilToken;

	@Autowired
	private ITeacherService teacherService;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@PostMapping
	public ResponseEntity<?> login(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
		logger.info("Login controller...");
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
					authenticationRequest.getUserName(), authenticationRequest.getPassword()));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.ok(ResponseMessage.INVALID_USERNAME_PASSWORD);
		}
		logger.info("LoginController : authenticated...");
		UserPrinciple principle = (UserPrinciple) userDetailService
				.loadUserByUsername(authenticationRequest.getUserName());
		final String jwt = jwtUtilToken.generateToken(principle);
		logger.info("JWT generated....");
		ModelMapper mapper = new ModelMapper();
		WelcomeKit welcome = new WelcomeKit();
		welcome.setUser(mapper.map(principle.getUser(), UserTO.class));
		List<Features> featureList = ((ILoginService) userDetailService)
				.finAllFeaturesByRole(principle.getUser().getRole());
		logger.info("Features fetched...");
		welcome.setFeatures(featureList.stream().map(f -> {
			FeatureTypeTO featureTypeTO = mapper.map(f.getFeatureType(), FeatureTypeTO.class);
			FeaturesTO featureTO = mapper.map(f, FeaturesTO.class);
			featureTO.setFeatureType(featureTypeTO);
			featureTO.setKey(f.getId() + "");
			return featureTO;
		}).collect(Collectors.toList()));

		welcome.setTeacher(mapper.map(teacherService.findByUser(principle.getUser()), TeacherTO.class));
		logger.info("Welcome kit prepared.");
		return ResponseEntity.ok(new AuthenticationResponse(jwt, welcome));
	}

	@GetMapping("/getPageFeatures/{pageName}")
	public ResponseEntity<List<FeaturesTO>> getPageFeatures(@PathVariable("pageName") String pageName) {
		ResponseEntity<List<FeaturesTO>> response = null;

		UserPrinciple principle = (UserPrinciple) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		logger.info("Get features for {} specific to {} page", principle.getUser().getName(), pageName);

		List<FeaturesTO> features = ((ILoginService) userDetailService)
				.findAllFeaturesByRoleAndContainerPage(principle.getUser().getRole(), pageName);

		HttpHeaders header = new HttpHeaders();
		header.set("statusCode", "200");
		header.set("message", "Success");
		response = new ResponseEntity<>(features, header, HttpStatus.ACCEPTED);

		return response;
	}
}
