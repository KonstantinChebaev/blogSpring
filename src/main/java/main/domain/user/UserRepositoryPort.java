package main.domain.user;

import main.domain.user.User;

import java.util.Optional;

public interface UserRepositoryPort {
    User findByEmail(String email);
    User findByName(String name);
    User findById(int userId);

    void addUser(User user);
    void save(User user);
}
