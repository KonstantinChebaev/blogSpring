package main.domain.post;

import main.domain.user.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PostRepositoryPort {
    Optional <Post>  findById(int postId) ;

    List<Post> findAll();
    List<Post> getAllGood(List<Post> posts);
    List<Post> findByModerStat (String moderStat);

    List<Post> findByQuery (String query);
    List<Post> findByDate (LocalDate date);

    int getCount();

    void savePost(Post post);

    long countByUser(User user);

    long countViewsByUser(User user);

    long getFirstPostDate(User user);

}
