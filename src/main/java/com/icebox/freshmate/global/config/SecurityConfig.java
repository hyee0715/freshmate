package com.icebox.freshmate.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.icebox.freshmate.domain.auth.application.JwtService;
import com.icebox.freshmate.domain.auth.application.AuthService;
import com.icebox.freshmate.domain.auth.application.filter.JwtAuthenticationProcessingFilter;
import com.icebox.freshmate.domain.auth.application.handler.LoginFailureHandler;
import com.icebox.freshmate.domain.member.domain.MemberRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

	private final MemberRepository memberRepository;
	private final JwtService jwtService;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		return http.formLogin(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)
			.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(request -> request
				.requestMatchers("/api/**").permitAll()
				.anyRequest().authenticated())
			.addFilterBefore(jwtAuthenticationProcessingFilter(), UsernamePasswordAuthenticationFilter.class)
			.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder(){

		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setPasswordEncoder(passwordEncoder());
		provider.setUserDetailsService(new AuthService(memberRepository, passwordEncoder(), jwtService));

		return new ProviderManager(provider);
	}

//    @Bean
//    public LoginSuccessJwtProvideHandler loginSuccessJwtProvideHandler(){
//
//        return new LoginSuccessJwtProvideHandler(jwtService, memberRepository);
//    }

    @Bean
    public LoginFailureHandler loginFailureHandler(){
        return new LoginFailureHandler();
    }

	@Bean
	public JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter(){

		return new JwtAuthenticationProcessingFilter(jwtService, memberRepository);
	}
}
