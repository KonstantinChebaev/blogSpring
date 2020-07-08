package main.domain.post;

import main.domain.user.User;

import java.util.List;
import java.util.Optional;

public interface PostRepositoryPort {
    Optional <Post>  findById(int postId) ;

    List<Post> findAll();
    List<Post> findAllGood();
    List<Post> findAllGood(List<Post> posts);
    List<Post> findByModerStat (String moderStat);
    int getCount();

    void savePost(Post post);


    long countByUser(User user);

    long countViewsByUser(User user);

    String getFirstPostDate(User user);
}
