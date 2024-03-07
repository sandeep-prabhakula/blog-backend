package com.sandeepprabhakula.blogging;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BlogsiteApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogsiteApplication.class, args);
	}

}
