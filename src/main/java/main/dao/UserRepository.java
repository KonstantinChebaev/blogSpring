package main.dao;

import main.domain.user.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository  extends CrudRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    Optional<User> findByName(String name);
    Optional<User> findByCode(String code);
}
