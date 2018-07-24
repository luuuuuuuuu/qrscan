package com.gentlelu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class QrscanApplication {

	public static void main(String[] args) {
		SpringApplication.run(QrscanApplication.class, args);
	}
}
