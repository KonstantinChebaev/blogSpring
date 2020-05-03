package main.domain.post;

import main.domain.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Component
public class PostUseCase {
    @Autowired
    PostRepositoryPort prp;

    public PostsDto getAll(int offset, int limit, String mode) {
        List<Post> all = prp.findAll();
        ArrayList<Post> posts = new ArrayList<>();
        int count = all.size();
        for (Post post : all) {
            if (count >= offset && count <= limit) {
                posts.add(post);
            }
        }
        return new PostsDto(count, posts);
    }

    public Optional<Post> findById(int id) {
        return prp.findById(id);
    }

    public void postPost(String time, boolean active, String title, String text, String tags) {
        LocalDate ldate = LocalDate.parse(time);
        Date date = java.sql.Date.valueOf(ldate);
        if (date.before(new Date(System.currentTimeMillis()))) {
            date = new Date(System.currentTimeMillis());
        }
        Post newPost = Post.builder()
                .time(date)
                .isActive(active)
                .title(title)
                .text(text)
                .moderStat(Post.ModerStat.NEW)
                .viewCount(0)
//                .user(main.domain.user.User.builder().id(0).build())
                .moderatorId(0)
                .build();
        prp.savePost(newPost);

    }
}
