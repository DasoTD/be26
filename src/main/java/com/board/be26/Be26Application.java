package com.board.be26;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class Be26Application {

	public static void main(String[] args) {
		SpringApplication.run(Be26Application.class, args);
	}

}
