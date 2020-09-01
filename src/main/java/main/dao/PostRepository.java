package main.dao;

import main.domain.post.Post;
import main.domain.user.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends CrudRepository<Post, Integer> {
    @Query("SELECT COUNT(*) FROM Post p WHERE (:user IS NULL OR p.user = :user)")
    Integer countByUser(@Param("user") User user);

    @Query("SELECT SUM(p.viewCount) FROM Post p WHERE (:user IS NULL OR p.user = :user)")
    Integer getViewsByUser(@Param("user") User user);

    @Query("SELECT DATE_FORMAT(MIN(p.time),'%Y-%m-%d %H:%m') " +
            "FROM Post p WHERE (:user IS NULL OR p.user = :user)")
    String getFirstPostDateByUser(@Param("user") User user);
}
