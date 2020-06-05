package main.web.security;


import main.domain.user.User;
import main.domain.user.UserRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserSecurityService implements UserDetailsService{

    private static final Logger LOG = LoggerFactory.getLogger(UserSecurityService.class);

    @Autowired
    private UserRepositoryPort userRepositoryPort;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepositoryPort.findByName(username);
        if(user.isEmpty()) {
            LOG.warn("Username {} not found", username);
            throw new UsernameNotFoundException("Username "+username+" not found");
        }
        return user.get();
    }
}
