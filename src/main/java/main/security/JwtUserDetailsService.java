package main.security;

import lombok.extern.slf4j.Slf4j;
import main.domain.user.User;
import main.domain.user.UserRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepositoryPort userRepositoryPort;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepositoryPort.findByEmail(email);
        if(userOptional.isEmpty()) {
            System.out.println("Data recieved. User not found.");
            throw new UsernameNotFoundException("Username with email"+email+" not found");
        }
        return userOptional.get();
    }
}