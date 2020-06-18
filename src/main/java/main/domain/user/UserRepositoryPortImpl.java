package main.domain.user;

import main.dao.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserRepositoryPortImpl implements UserRepositoryPort {

    @Autowired
    UserRepository userRepository;
    @Override
    public User findByEmail(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isEmpty()){
            throw new RuntimeException("User witn email:"+email+" not found");
        }
        return optionalUser.get();
    }

    @Override
    public User findByName(String name) {
        Optional<User> optionalUser = userRepository.findByName(name);
        if(optionalUser.isEmpty()){
            throw new RuntimeException("User witn name:"+name+" not found");
        }
        return optionalUser.get();
    }



    @Override
    public User findById(int userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isEmpty()){
            throw new RuntimeException("User witn id:"+userId+" not found");
        }
        return optionalUser.get();
    }

    @Override
    public void addUser(User user) {
        userRepository.save(user);
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

}
