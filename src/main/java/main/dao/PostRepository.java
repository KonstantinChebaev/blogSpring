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

    @Query(nativeQuery = true, value = "SELECT * FROM posts p\n" +
            "WHERE p.moderation_status = 'ACCEPTED' AND p.time <= NOW() AND p.is_active = 1 "+
            "ORDER BY (SELECT Count(*) FROM post_votes pv WHERE pv.post_id = p.id AND pv.value = 1) DESC, p.`time` DESC")
    Page<Post> findAllPostsByBest(Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT * FROM posts p " +
                        "WHERE p.moderation_status = 'ACCEPTED' AND p.time <= NOW() AND p.is_active = 1 " +
                        "ORDER BY (SELECT Count(*) FROM post_comments pc WHERE pc.post_id = p.id) DESC, p.`time` DESC")
    Page<Post> findAllPostsByPopular(Pageable pageable);
}
