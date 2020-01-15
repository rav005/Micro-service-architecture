package com.rmi.authservice.SQL.security;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rmi.authservice.model.AppUser;
import com.rmi.authservice.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
	
	private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);
	
    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String usernameOrEmail)
            throws UsernameNotFoundException {
    	logger.info("loading UserByUsername: " + usernameOrEmail);
        // Let people login with either username or email1
        
    	AppUser user = // userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
        		userRepository.findByUsername(usernameOrEmail)
                .orElseThrow(() -> 
                        new UsernameNotFoundException("User not found with username or email : " + usernameOrEmail)
                );
        
    	/*try {
    		Optional<AppUser> user = userRepository.findByUsername(usernameOrEmail);
	    	if(!user.isPresent()) {
	    		throw new UsernameNotFoundException("User not found with username or email : " + usernameOrEmail);
	    	}
    	
        	logger.info("Found user: ", user.get());
        	return UserPrincipal.create(user.get());
        	
    	} catch (UsernameNotFoundException e) {
    		throw e;
    	} catch(Exception e) {
    		logger.error("loadUserByUsername throwed unknown error: ", e);
    		throw e;
    	}*/

        return UserPrincipal.create(user);
    }

    // This method is used by JWTAuthenticationFilter
    @Transactional
    public UserDetails loadUserById(Long id) {
    	try {
	    	AppUser user = userRepository.findById(id).orElseThrow(
	            () -> new UsernameNotFoundException("User not found with id : " + id)
	        );
	    	
	    	logger.info("[CustomUserDetailsService] loadUserById: found user: " + user);
	        return UserPrincipal.create(user);
	        
    	} catch(Exception e) {
    		logger.error("[CustomUserDetailsService] loadUserById: exception thrown: " + e.getMessage(), e);
    		throw e;
    	}
    }
}
