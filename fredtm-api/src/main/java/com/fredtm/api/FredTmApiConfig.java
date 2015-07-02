package com.fredtm.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.hateoas.config.EnableEntityLinks;
import org.springframework.test.context.ActiveProfiles;

import com.fredtm.data.repository.OperationRepository;

@Configuration
@EnableJpaRepositories(basePackages = { "com.fredtm.data",
		"com.fredtm.data.repository" }, transactionManagerRef = "transactionManager")
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
@ComponentScan(basePackages = { "com.fredtm.core.model", "com.fredtm.data",
		"com.fredtm.service", "com.fredtm.api", "com.fredtm.api.rest",
		"com.fredtm.api.resource" })
@EnableEntityLinks
@ActiveProfiles(profiles = "dev,test,prod")
public class FredTmApiConfig {

	@Autowired
	private OperationRepository repo;

	public static void main(String[] args) {
		String webPort = System.getenv("PORT");
		if (webPort == null || webPort.isEmpty()) {
			webPort = "9000";
		}
		SpringApplication.run(FredTmApiConfig.class, args);
	}

}
