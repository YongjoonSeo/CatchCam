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

	// authenticationManagerλ₯? Bean ?±λ‘ν©??€.
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	// ??Έ?? ??? PasswordEncoder λ₯? Bean ?±λ‘ν©??€.
	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.httpBasic().disable() // rest api ?΄λ―?λ‘? κΈ°λ³Έ?€?  ?¬?©??¨. κΈ°λ³Έ?€? ?? λΉμΈμ¦μ λ‘κ·Έ?Έ?Ό ?λ©΄μΌλ‘? λ¦¬λ€?΄? ?Έ ??€.
				.csrf().disable() // rest api?΄λ―?λ‘? csrf λ³΄μ?΄ ????Όλ―?λ‘? disableμ²λ¦¬.
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // jwt token?Όλ‘? ?Έμ¦νλ―?λ‘? ?Έ???
																							// ????Όλ―?λ‘? ??±??¨.
				.and().authorizeRequests() // ?€? λ¦¬ν?μ€?Έ? ??? ?¬?©κΆν μ²΄ν¬
				.antMatchers("/**/login", "/**/signup", "/**/valid/{nickname}").permitAll() // κ°?? κ΄?? ¨ API? ?κ΅¬λ ? κ·? κ°??₯
				.antMatchers("/**/count").permitAll() // λ©μΈ ?λ©΄μ κ΄?? ¨? API ?? ?κ΅¬λ ? κ·? κ°??₯
//				.antMatchers(HttpMethod.GET, "/**/lectures", "/**/lectures/count", "/**/lectures/tags", "/**/lectures/{lectureId}").permitAll()
				.antMatchers("/**/").permitAll()
				.anyRequest().hasRole("USER") // κ·ΈμΈ ?λ¨Έμ? ?μ²??? λͺ¨λ ?Έμ¦λ ??λ§? ? κ·? κ°??₯
				.and().addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
						UsernamePasswordAuthenticationFilter.class); // jwt token ??°λ₯? id/password ?Έμ¦? ??° ? ? ?£??€
	}

	@Override // ignore check swagger resource
	public void configure(WebSecurity web) {
		web.ignoring().antMatchers("/v2/api-docs", "/swagger-resources/**", "/swagger-ui.html", "/webjars/**",
				"/swagger/**");
	}
}
