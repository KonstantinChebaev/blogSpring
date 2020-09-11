package main.domain.user;


public interface UserRepositoryPort {
    User findByEmail(String email);
    User findByCode(String code);
    User findById(int userId);

    void save(User user);
}
