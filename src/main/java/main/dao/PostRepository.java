package main.dao;

import main.domain.post.Post;
import main.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer>,
        JpaSpecificationExecutor<Post> {
    @Query("SELECT COUNT(*) FROM Post p WHERE (:user IS NULL OR p.user = :user)")
    Integer countByUser(@Param("user") User user);

    @Query("SELECT SUM(p.viewCount) FROM Post p WHERE (:user IS NULL OR p.user = :user)")
    Integer getViewsByUser(@Param("user") User user);

    @Query("SELECT DATE_FORMAT(MIN(p.time),'%Y-%m-%d %H:%m') " +
            "FROM Post p WHERE (:user IS NULL OR p.user = :user)")
    String getFirstPostDateByUser(@Param("user") User user);

    @Query(nativeQuery = true, value = "SELECT * FROM post WHERE is_active = 1 "
            + "AND time <= NOW() AND moderation_status = 'ACCEPTED' "
            + "AND text LIKE %:query% OR title LIKE %:query%")
    List<Post> findAllPostsByQuery(String query, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT * FROM post WHERE is_active = 1 "
            + "AND time <= NOW() AND moderation_status = 'ACCEPTED' ")
    List<Post> findAllVisible(Pageable pageable);
}
