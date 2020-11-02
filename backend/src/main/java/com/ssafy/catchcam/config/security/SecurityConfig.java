package com.ssafy.catchcam.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	// authenticationManager�? Bean ?��록합?��?��.
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	// ?��?��?��?�� ?��?��?�� PasswordEncoder �? Bean ?��록합?��?��.
	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.httpBasic().disable() // rest api ?���?�? 기본?��?�� ?��?��?��?��. 기본?��?��?? 비인증시 로그?��?�� ?��면으�? 리다?��?��?�� ?��?��.
				.csrf().disable() // rest api?���?�? csrf 보안?�� ?��?��?��?���?�? disable처리.
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // jwt token?���? ?��증하�?�? ?��?��??
																							// ?��?��?��?���?�? ?��?��?��?��.
				.and().authorizeRequests() // ?��?�� 리�?�스?��?�� ???�� ?��?��권한 체크
				.antMatchers("/**/login", "/**/signup", "/**/valid/{nickname}").permitAll() // �??�� �??�� API?�� ?��구나 ?���? �??��
				.antMatchers("/**/count").permitAll() // 메인 ?��면에 �??��?�� API ?��?�� ?��구나 ?���? �??��
//				.antMatchers(HttpMethod.GET, "/**/lectures", "/**/lectures/count", "/**/lectures/tags", "/**/lectures/{lectureId}").permitAll()
				.antMatchers("/**/").permitAll()
				.anyRequest().hasRole("USER") // 그외 ?��머�? ?���??? 모두 ?��증된 ?��?���? ?���? �??��
				.and().addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
						UsernamePasswordAuthenticationFilter.class); // jwt token ?��?���? id/password ?���? ?��?�� ?��?�� ?��?��?��
	}

	@Override // ignore check swagger resource
	public void configure(WebSecurity web) {
		web.ignoring().antMatchers("/v2/api-docs", "/swagger-resources/**", "/swagger-ui.html", "/webjars/**",
				"/swagger/**");
	}
}
