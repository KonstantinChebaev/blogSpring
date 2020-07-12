package main.domain.post;

import main.dao.PostRepository;
import main.domain.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class PostRepositPortImpl implements PostRepositoryPort {

    @Autowired
    PostRepository pr;

    @Override
    public Optional <Post> findById(int id) {
        return pr.findById(id);
    }

    @Override
    public List<Post> findAll() {
        Iterable<Post> postIterable = pr.findAll();
        ArrayList<Post> posts = new ArrayList<>();
        for (Post post : postIterable) {
            posts.add(post);
        }
        return posts;
    }

    @Override
    public List<Post> getAllGood(List<Post> posts) {
        ArrayList<Post> goodPosts = new ArrayList<>();
        for (Post post : posts) {
            if (post.isActive()
                    && post.getModerStat().equals(Post.ModerStat.ACCEPTED)
                    && post.getTime().isBefore(LocalDateTime.now())) {
                goodPosts.add(post);
            }
        }
        return goodPosts;
    }

    @Override
    public List<Post> findByModerStat(String moderStat) {
        return pr.findByModerStat(moderStat);
    }

    @Override
    public List<Post> findByQuery(String query) {
        Iterable<Post> postIterable = pr.findAll();
        ArrayList<Post> posts = new ArrayList<>();
        for (Post post : postIterable) {
            if((post.getText().contains(query) || post.getTitle().contains(query))){
                if (post.isActive()
                        && post.getModerStat().equals(Post.ModerStat.ACCEPTED)
                        && post.getTime().isBefore(LocalDateTime.now())) {
                    posts.add(post);
                }
            }
        }
        return posts;
    }

    @Override
    public List<Post> findByDate(LocalDate date) {
        Iterable<Post> postIterable = pr.findAll();
        ArrayList<Post> posts = new ArrayList<>();
        for (Post post : postIterable) {
            LocalDate localDate = post.getTime().toLocalDate();
            if(localDate.isEqual(date)){
                if (post.isActive()
                        && post.getModerStat().equals(Post.ModerStat.ACCEPTED)
                        && post.getTime().isBefore(LocalDateTime.now())) {
                    posts.add(post);
                }
            }
        }
        return posts;
    }


    @Override
    public int getCount() {
       int x = (int) pr.count();
        return x;
    }

    @Override
    public void savePost(Post post) {
        pr.save(post);
    }

    @Override
    public long countByUser(User user) {
        return pr.countByUser(user);
    }

    @Override
    public long countViewsByUser(User user) {
        return pr.getViewsByUser(user);
    }

    @Override
    public String getFirstPostDate(User user) {
        return pr.getFirstPostDateByUser(user);
    }

}
