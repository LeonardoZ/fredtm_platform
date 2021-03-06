package com.fredtm.api;

import static springfox.documentation.schema.AlternateTypeRules.newRule;

import java.text.SimpleDateFormat;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.hateoas.HypermediaAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.config.EnableEntityLinks;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.context.request.async.DeferredResult;

import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fredtm.api.security.JwtFilter;
import com.google.common.base.Predicates;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableJpaRepositories(basePackages = { "com.fredtm.data",
		"com.fredtm.data.repository" }, transactionManagerRef = "transactionManager")
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class, HypermediaAutoConfiguration.class })
@ComponentScan(basePackages = { "" + "com.fredtm.core.model", "com.fredtm.data", "com.fredtm.data.repository",
		"com.fredtm.service", "com.fredtm.api", "com.fredtm.web", "com.fredtm.api.email", "com.fredtm.api.rest",
		"com.fredtm.api.resource", "com.fredtm.api.security", "com.fredtm.api.exception" })
@ActiveProfiles(profiles = "dev,test,prod")
@EnableEntityLinks
@EnableSwagger2
public class FredTmApiConfig {
	
	@Autowired
	private TypeResolver typeResolver;

	@Bean
	public FilterRegistrationBean jwtFilter() {
		final FilterRegistrationBean registrationBean = new FilterRegistrationBean();
		registrationBean.setFilter(new JwtFilter());
		registrationBean.addUrlPatterns("/*");
		registrationBean.setOrder(10);
		return registrationBean;
	}

	@Bean
	public Jackson2ObjectMapperBuilder jacksonBuilder() {
		Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
		builder.indentOutput(true).dateFormat(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"));
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
		objectMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
		builder.configure(objectMapper);
		return builder;
	}

	public static void main(String[] args) {
		String webPort = System.getenv("PORT");
		if (webPort == null || webPort.isEmpty()) {
			webPort = "9000";
		}
		SpringApplication.run(FredTmApiConfig.class, args);
	}

	@Bean
	public Docket fredTmApi() {
		return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select()
				.apis(Predicates.and(Predicates.not(RequestHandlerSelectors.basePackage("org.springframework.boot")),
						Predicates.not(RequestHandlerSelectors.basePackage("com.fredtm.web"))))
				.paths(PathSelectors.any()).build().pathMapping("/")
				.directModelSubstitute(LocalDate.class, String.class)
				.genericModelSubstitutes(ResponseEntity.class,PagedResources.class)
				.alternateTypeRules(newRule(
						typeResolver.resolve(DeferredResult.class,
								typeResolver.resolve(ResponseEntity.class, WildcardType.class)),
						typeResolver.resolve(WildcardType.class)))
				.useDefaultResponseMessages(false);
	}

	private ApiInfo apiInfo() {
		return new ApiInfo("FredTM API", "API for consuming infomations from Motion-Time Study", "1.0", "",
				"leo.zapparoli@gmail.com", "", "");

	}

	@Bean
	SecurityConfiguration security() {
		return new SecurityConfiguration("test-app-client-id", "test-app-realm", "test-app", "apiKey");
	}

}
