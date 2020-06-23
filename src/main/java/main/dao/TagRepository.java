package main.dao;

import main.domain.tag.Tag;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends CrudRepository<Tag, Integer> {
    Optional<Tag> findByName (String name);
    Tag findByNameIgnoreCase(String name);
}
