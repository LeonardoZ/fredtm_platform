package com.fredtm.api.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fredtm.core.util.HashGenerator;

@Configuration
@EnableWebSecurity
public class RestSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private DataSource ds;

	class HashPasswordEncoder implements PasswordEncoder{

		@Override
		public String encode(CharSequence pass) {
			return new HashGenerator().toHash(pass);
		}

		@Override
		public boolean matches(CharSequence pass, String encoded) {
			return encode(pass).equals(encoded);
		}
		
	}
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth)
			throws Exception {
		 auth.jdbcAuthentication()
		 .passwordEncoder(new HashPasswordEncoder())
		 .dataSource(ds)
		
		 .usersByUsernameQuery(
		 "select email as username, password_hash as password,1 as enabled from account where email = ?")
		 .authoritiesByUsernameQuery(
		 "select email as username,'ROLE_USER' as authority from account where email = ?");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().
		antMatchers(
				"/fredapi/account/login",
				"/fredapi/base",
				"/fredapi/account"
		)
		.permitAll()
		.antMatchers("/**").hasRole("USER")
		.and().httpBasic()
		.and().formLogin().disable();
		http.csrf().disable();

	}

}
