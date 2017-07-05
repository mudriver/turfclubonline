package ie.turfclub.service.login;

import java.util.Collection;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;







import ie.turfclub.model.login.User;
 
@Service
@Transactional
public class UserServiceImpl implements UserService {
    static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
     
    @Autowired
    private SessionFactory sessionFactory;
    
 
    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }
    
    

    
    //TODO Dummy role added temporarily 
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	Query query = getCurrentSession().createQuery("from User where user_login = :usersName ");
        query.setString("usersName", username);
         
        logger.info(query.toString());
        if (query.list().size() == 0 ) {
            throw new UsernameNotFoundException("User [" + username.toString() + "] not found");
        } else {
        	 @SuppressWarnings("unchecked")
			List<User> list = (List<User>)query.list();
        	 query = getCurrentSession().createQuery("from UserRole where user_name = :usersName ");
             query.setString("usersName", username); 
             
             User user = (User) list.get(0);
             @SuppressWarnings("unchecked")
			 Collection<GrantedAuthority> authorities = query.list();
             user.setAuthorities(authorities);
             
             
             logger.info(user.toString());
             return user;
        }
    }
}