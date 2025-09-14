package com.chat.sr.config;


import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
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
//export JAVA_HOME=$(/usr/libexec/java_home -v 21)
//mvn clean spring-boot:run
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
				.authorizeHttpRequests(request ->

        request.requestMatchers("/auth/**", "/api/upload").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/user/**").hasRole("USER")
                .requestMatchers("/my-profile").hasAnyRole("ADMIN", "USER","VET","OWNER")
                .anyRequest().authenticated())

                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.exceptionHandling(exception -> exception.accessDeniedHandler(customAccessDeniedHandler)
						.authenticationEntryPoint(customAuthenticationEntryPoint))
				.authenticationProvider(authenticationProvider())
				.addFilterBefore(jwtUtilsFilter, UsernamePasswordAuthenticationFilter.class)
            /*    .logout(logout -> logout
                        // Logout URL (frontend / Postman থেকে POST পাঠাতে হবে)
                        .logoutUrl("/auth/logout")
                        // Logout successful হলে redirect /login
                        .logoutSuccessHandler((request, response, authentication) -> {
                            // JWT cookie clear করা
                            ResponseCookie cookie = ResponseCookie.from("jwt", "")
                                    .httpOnly(true)
                                    .secure(false) // production এ true
                                    .path("/")
                                    .maxAge(0)
                                    .sameSite("Strict")
                                    .build();

                            response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
                            response.setStatus(HttpServletResponse.SC_OK);
                            response.getWriter().write("Logout successful");
                        })
                        // Session invalidate
                        .invalidateHttpSession(true)
                        .deleteCookies("jwt")
                );*/
                .logout(logout -> logout.disable());




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
