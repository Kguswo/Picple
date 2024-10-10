package com.ssafy.picple;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class PicpleApplication {

	public static void main(String[] args) {
		SpringApplication.run(PicpleApplication.class, args);
	}

}
