package main.dao;

import main.domain.post.Post;
import main.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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

    @Query(nativeQuery = true, value = "SELECT * FROM posts WHERE is_active = 1 "
            + "AND time <= NOW() AND moderation_status = 'ACCEPTED' "
            + "AND text LIKE %:query% OR title LIKE %:query%")
    Page<Post> findAllPostsByQuery(String query, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT * FROM posts WHERE is_active = 1 "
            + "AND time <= NOW() AND moderation_status = 'ACCEPTED' ")
    Page<Post> findAllVisible(Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT COUNT(*) FROM posts WHERE is_active = 1 "
            + "AND time <= NOW() AND moderation_status = 'ACCEPTED' ")
    long findAllVisibleCount();

    @Query(nativeQuery = true, value = "SELECT * FROM posts WHERE is_active = 1 "
            + "AND time <= NOW() AND moderation_status = 'ACCEPTED' "
            +" AND time >= :date AND time <= :dateplusday")
    Page<Post> findAllPostsByDate(String date, String dateplusday, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT * FROM posts WHERE moderation_status = :status " +
            "AND moderator_id != :moderid")
    Page<Post> findAllPostsByModerStat(String status, int moderid, Pageable pageable);

    //java.sql.SQLSyntaxErrorException: You have an error in your SQL syntax;
    // check the manual that corresponds to your MySQL server version for the
    // right syntax to use near
    // ') FROM post_votes LEFT JOIN posts p ON post_votes.post_id = p.id GROUP BY p.id' at line 1
    @Query(nativeQuery = true, value = "SELECT p.id, p.is_active, p.moderation_status, p.text, "
            + "p.time, p.title, p.view_count, p.moderator_id, p.user_id FROM post_votes "
            + "LEFT JOIN posts p ON post_votes.post_id = p.id GROUP BY p.id ORDER BY count(p.id) DESC")
    Page<Post> findAllPostsByBest(Pageable pageable);

    //java.sql.SQLSyntaxErrorException: You have an error in your SQL syntax;
    // check the manual that corresponds to your MySQL server version for the
    // right syntax to use near
    // ') FROM post_comments LEFT JOIN posts p ON post_comments.post_id = p.id GROUP BY p.id' at line 1
    @Query(nativeQuery = true, value = "SELECT p.id, p.is_active, p.moderation_status, p.text, p.time, p.title, p.view_count, p.moderator_id, p.user_id " +
            "FROM post_comments " +
            "LEFT JOIN posts p " +
            "ON post_comments.post_id = p.id " +
            "GROUP BY p.id ORDER BY count(p.id) DESC")
    Page<Post> findAllPostsByPopular(Pageable pageable);
}
