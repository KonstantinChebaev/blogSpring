package main.dao;

import main.domain.post.Post;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends CrudRepository<Post, Integer> {
    Optional<Post> findByTitle(String title);
    List<Post> findByModerStat (String moderStat);
}
