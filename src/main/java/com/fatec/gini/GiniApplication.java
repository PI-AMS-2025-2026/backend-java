package com.fatec.gini;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class GiniApplication {
	public static void main(String[] args) {
		SpringApplication.run(GiniApplication.class, args);
	}

}
