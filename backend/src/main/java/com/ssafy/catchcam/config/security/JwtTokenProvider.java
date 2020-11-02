package com.ssafy.catchcam.config.security;

import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenProvider {

	// application.propertie?�� ?��?�� key �? 매핑
	@Value("${spring.jwt.secret}")
	private String secretKey;

	private long tokenValidMilisecond = 60 * 60 * 1000000000L; // 1 ?��간만 ?��?�� ?��?��

	@Autowired
	private UserDetailsService userDetailsService;

	@PostConstruct
	protected void init() {
		secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
	}

	// JWT ?��?�� ?��?��
	public String createToken(String userId, List<String> roles) {
		Claims claims = Jwts.claims().setSubject(userId); // JWT payload ?�� ???��?��?�� ?��보단?��
		claims.put("roles", roles);
		Date now = new Date();
		return Jwts.builder().setClaims(claims) // ?���? ???��
				.setIssuedAt(now) // ?��?�� 발행 ?���? ?���?
				.setExpiration(new Date(now.getTime() + tokenValidMilisecond)) // set Expire Time
				.signWith(SignatureAlgorithm.HS256, secretKey) // ?��?��?�� ?��?��?�� ?��고리즘과
				.compact(); // signature ?�� ?��?���? secret�? ?��?��
	}

	// Jwt ?��?��?���? ?���? ?��보�?? 조회
	public Authentication getAuthentication(String token) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserPk(token));
		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
	}

	// ?��?��?��?�� ?��?�� ?���? 추출
	public String getUserPk(String token) {
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
	}

	// Request?�� Header?��?�� token 값을 �??��?��?��?��. "X-AUTH-TOKEN" : "TOKEN�?'
	public String resolveToken(HttpServletRequest request) {
		return request.getHeader("X-AUTH-TOKEN");
	}

	// ?��?��?�� ?��?��?�� + 만료?��?�� ?��?��
	public boolean validateToken(String jwtToken) {
		try {
			Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
			return !claims.getBody().getExpiration().before(new Date());
		} catch (Exception e) {
			return false;
		}
	}
}