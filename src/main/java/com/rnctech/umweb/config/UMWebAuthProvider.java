package com.rnctech.umweb.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;


/**
 * @contributor zilin
 * 
 */

@Component
public class UMWebAuthProvider implements AuthenticationProvider {
 
	@Autowired
	Environment environment;
	
    public Authentication authenticate(String name, String password) throws AuthenticationException {
    	return authenticate(new UsernamePasswordAuthenticationToken(name,password));
	}
	
    @Override
    public Authentication authenticate(Authentication authentication) 
      throws AuthenticationException {
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();


			boolean yn = BCrypt.checkpw(password, "useradmin");
			if(!yn) throw new BadCredentialsException("Incorrect Password");
			try {
				List<String> roles = new ArrayList<>();
				roles.add("useradmin");
				Authentication auth= new UsernamePasswordAuthenticationToken(name, password, getGrantedAuthorities(roles));
				((UsernamePasswordAuthenticationToken)auth).setDetails("");
				SecurityContextHolder.getContext().setAuthentication(auth);
				return auth;
			} catch (Exception e) {
				throw new UsernameNotFoundException("Incorrect Username: " + name);
			}
		
		
    }
 
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
    
    public List<String> getRolesAsList(Set<Object> roles) {
        List <String> rolesAsList = new ArrayList<String>();
        for(Object role : roles){

        }
        return rolesAsList;
    }

    public static List<GrantedAuthority> getGrantedAuthorities(List<String> roles) {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }
        return authorities;
    }

    public Collection<? extends GrantedAuthority> getAuthorities(Set<Object> roles) {
        List<GrantedAuthority> authList = getGrantedAuthorities(getRolesAsList(roles));
        return authList;
    }
}
