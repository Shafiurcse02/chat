package com.chat.sr.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.chat.sr.exception.CustomAccessDeniedHandler;
import com.chat.sr.exception.CustomAuthenticationEntryPoint;
import com.chat.sr.security.CustomUserDetailsService;
import com.chat.sr.security.JwtUtilsFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Autowired
	private CustomUserDetailsService customEmpDetailsService;
	@Autowired
	private JwtUtilsFilter jwtUtilsFilter;
	@Autowired
	private CustomAccessDeniedHandler customAccessDeniedHandler;
	@Autowired
	private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable).cors(Customizer.withDefaults())
				.authorizeHttpRequests(request -> request.requestMatchers("/auth/register", "/auth/login", "/logout").permitAll()
						.requestMatchers("/admin/**").hasRole("ADMIN").requestMatchers("/user/**").hasRole("USER")
						.anyRequest().authenticated())
				.sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.exceptionHandling(exception -> exception.accessDeniedHandler(customAccessDeniedHandler)
						.authenticationEntryPoint(customAuthenticationEntryPoint))
				.authenticationProvider(authenticationProvider())
				.addFilterBefore(jwtUtilsFilter, UsernamePasswordAuthenticationFilter.class)
				.logout(logout -> logout.logoutUrl("/auth/logout").logoutSuccessUrl("/login").invalidateHttpSession(true)
						.deleteCookies("jwt"));

		return http.build();
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(customEmpDetailsService);
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		return daoAuthenticationProvider;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

}
