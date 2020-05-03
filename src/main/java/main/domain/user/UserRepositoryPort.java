package main.domain.user;

import main.domain.user.User;

import java.util.Optional;

public interface UserRepositoryPort {
    Optional<User> findByEmail(String email);
    Optional<User> findByName(String name);
    Optional<User> findById(int userId);

    void addUser(User user);
    void save(User user);
}
