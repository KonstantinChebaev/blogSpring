package main.domain.post;

import main.dao.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class PostRepositPortImpl implements PostRepositoryPort {

    @Autowired
    PostRepository pr;

    @Override
    public Optional<Post> findByTitle(String title) {
        return pr.findByTitle(title);
    }

    @Override
    public Optional<Post> findById(int postId) {
        return pr.findById(postId);
    }

    @Override
    public List<Post> findAll() {
        Iterable<Post> postIterable = pr.findAll();
        ArrayList<Post> posts = new ArrayList<>();
        for (Post post : postIterable) {
            posts.add(post);
            System.out.println(post);
        }
        return posts;
    }

    @Override
    public void addPost(Post post) {
        pr.save(post);
    }

    @Override
    public void savePost(Post post) {
        pr.save(post);
    }
}
