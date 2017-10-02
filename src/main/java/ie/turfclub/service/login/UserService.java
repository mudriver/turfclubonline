package ie.turfclub.service.login;

import ie.turfclub.model.login.User;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
 
public interface UserService extends UserDetailsService {
 
 
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

	public User findByEmail(String userName);
	
}
