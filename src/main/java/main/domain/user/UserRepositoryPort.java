package main.domain.user;


public interface UserRepositoryPort {
    User findByEmail(String email);
    User findByName(String name);
    User findById(int userId);

    void addUser(User user);
    void save(User user);
}
