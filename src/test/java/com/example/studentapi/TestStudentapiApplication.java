package com.example.studentapi;

import org.springframework.boot.SpringApplication;

public class TestStudentapiApplication {

	public static void main(String[] args) {
		SpringApplication.from(StudentapiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
