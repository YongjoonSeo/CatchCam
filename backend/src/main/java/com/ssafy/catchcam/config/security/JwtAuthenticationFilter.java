package com.ssafy.catchcam.config.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

public class JwtAuthenticationFilter extends GenericFilterBean {

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
		this.jwtTokenProvider = jwtTokenProvider;
	}

	// Request�? ?��?��?��?�� Jwt Token?�� ?��?��?��?�� �?�?(jwtTokenProvider.validateToken)?��?�� filter�?
	// filterChain?�� ?��록합?��?��.
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		// ?��?��?��?�� JWT�? 받아?��
		String token = jwtTokenProvider.resolveToken((HttpServletRequest) request);
		// ?��?��?�� ?��?��?���? ?��?��
		if (token != null && jwtTokenProvider.validateToken(token)) {
			// ?��?��?�� ?��?��?���? ?��?��?���? �??�� ?��?? ?���? 받아?��
			Authentication authentication = jwtTokenProvider.getAuthentication(token);
			
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		filterChain.doFilter(request, response);
	}
}
