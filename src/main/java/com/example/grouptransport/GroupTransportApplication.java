package com.example.grouptransport;

import com.example.grouptransport.model.User;
import com.example.grouptransport.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GroupTransportApplication {

	public static void main(String[] args) {
		SpringApplication.run(GroupTransportApplication.class, args);
	}


	@Bean
	public CommandLineRunner run(UserRepository userRepository) {
		return args -> userRepository.save(new User(1L, "Adam", null));
	}

}