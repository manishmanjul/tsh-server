package com.tsh.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class CorsClass implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		WebMvcConfigurer.super.addCorsMappings(registry);
		registry.addMapping("/tsh/**").allowedOrigins("https://austsh.com", "https://www.austsh.com");
		registry.addMapping("/tsh/**/**").allowedOrigins("https://austsh.com", "https://www.austsh.com");
		registry.addMapping("/tsh/**/**/**").allowedOrigins("https://austsh.com", "https://www.austsh.com");
		registry.addMapping("/tshServices/**").allowedOrigins("https://austsh.com", "https://www.austsh.com");
		registry.addMapping("/tsh/schedule/**").allowedOrigins("https://austsh.com", "https://www.austsh.com");
	}

//	@Override
//	public void addCorsMappings(CorsRegistry registry) {
//		WebMvcConfigurer.super.addCorsMappings(registry);
//		registry.addMapping("/tsh/**").allowedOrigins("http://localhost:3000");
//		registry.addMapping("/tsh/**/**").allowedOrigins("http://localhost:3000");
//		registry.addMapping("/tsh/**/**/**").allowedOrigins("http://localhost:3000");
//		registry.addMapping("/tshServices/**").allowedOrigins("http://localhost:3000");
//		registry.addMapping("/tsh/schedule/**").allowedOrigins("http://localhost:3000");
//	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new CorsClass();
	}
}
