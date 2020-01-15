package com.rmi.gatewayzuul.security;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

//import com.rmi.configservice.model.JwtConfig;

public class JwtTokenAuthenticationFilter extends  OncePerRequestFilter {
	private static final Logger logger = LoggerFactory.getLogger(JwtTokenAuthenticationFilter.class);
	
//	private final JwtConfig jwtConfig;
	@Value("${security.jwt.uri}")
    private String Uri;

    @Value("${security.jwt.header}")
    private String header;

    @Value("${security.jwt.prefix}")
    private String prefix;

    @Value("${security.jwt.expiration}")
    private int expiration;
    
    @Value("${security.jwt.secret}")
    private String secret;

	
//	public JwtTokenAuthenticationFilter(JwtConfig jwtConfig) {
//		this.jwtConfig = jwtConfig;
//	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		logger.error("**********************************************************************************");
		logger.error("Authenticating User request: " + request.getRequestURL().toString());
		Enumeration<String> headerNames = request.getHeaderNames();
		while(headerNames.hasMoreElements()) {
		  String headerName = headerNames.nextElement();
		  logger.warn("Header Name - " + headerName + ", Value - " + request.getHeader(headerName));
		}
		
		if (request.getRequestURL().toString().contains("/auth")) {
			logger.warn("Calling chain.doFilter without any checks since it's /auth request!");
			chain.doFilter(request, response);
			return;
		}
		
		logger.error("**********************************************************************************");
		// 1. get the authentication header. Tokens are supposed to be passed in the authentication header
//		String header = request.getHeader(jwtConfig.getHeader());
		String header = request.getHeader(this.header);
		
		// 2. validate the header and check the prefix
//		if(header == null || !header.startsWith(jwtConfig.getPrefix())) {
		if(header == null || !header.startsWith(prefix)) {
			chain.doFilter(request, response);  		// If not valid, go to the next filter.
			return;
		}
		
		// If there is no token provided and hence the user won't be authenticated. 
		// It's Ok. Maybe the user accessing a public path or asking for a token.
		
		// All secured paths that needs a token are already defined and secured in config class.
		// And If user tried to access without access token, then he won't be authenticated and an exception will be thrown.
		
		// 3. Get the token
//		String token = header.replace(jwtConfig.getPrefix(), "");
		String token = header.replace(prefix, "");
		
		try {	// exceptions might be thrown in creating the claims if for example the token is expired
			
			// 4. Validate the token
			Claims claims = Jwts.parser()
//					.setSigningKey(jwtConfig.getSecret().getBytes())
					.setSigningKey(secret.getBytes())
					.parseClaimsJws(token)
					.getBody();
			
			String username = claims.getSubject();
			if(username != null) {
				@SuppressWarnings("unchecked")
				List<String> authorities = (List<String>) claims.get("authorities");
				
				// 5. Create auth object
				// UsernamePasswordAuthenticationToken: A built-in object, used by spring to represent the current authenticated / being authenticated user.
				// It needs a list of authorities, which has type of GrantedAuthority interface, where SimpleGrantedAuthority is an implementation of that interface
				 UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
								 username, null, authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
				 
				 // 6. Authenticate the user
				 // Now, user is authenticated
				 SecurityContextHolder.getContext().setAuthentication(auth);
			}
			
		} catch (Exception e) {
			// In case of failure. Make sure it's clear; so guarantee user won't be authenticated
			SecurityContextHolder.clearContext();
		}
		
		// go to the next filter in the filter chain
		chain.doFilter(request, response);
	}

}
