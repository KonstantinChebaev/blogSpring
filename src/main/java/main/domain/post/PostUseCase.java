package main.domain.post;

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

    private static ArrayList<Post> lastQueryPosts;
    private static String lastQuery;

    public PostsDto getAll(int offset, int limit, String mode) {
        List<Post> allPosts = prp.findAll();
        ArrayList<Post> posts = this.cutArray(offset, limit, allPosts);
        return new PostsDto(allPosts.size(), posts);
    }

    public Optional<Post> findById(int id) {
        return prp.findById(id);
    }

    public void postPost(PostPostDto ppDto) {
        LocalDate ldate = LocalDate.parse(ppDto.getTime());
        Date date = java.sql.Date.valueOf(ldate);
        if (date.before(new Date(System.currentTimeMillis()))) {
            date = new Date(System.currentTimeMillis());
        }
        boolean active = ppDto.getActive()==1;
        Post newPost = Post.builder()
                .time(date)
                .isActive(active)
                .title(ppDto.getTitle())
                .text(ppDto.getText())
                .moderStat(Post.ModerStat.NEW)
                .viewCount(0)
//                .user(main.domain.user.User.builder().id(0).build())
                .moderatorId(0)
                .build();
        prp.savePost(newPost);
    }

    public PostsDto searchPost(int offset, int limit, String query) {
        List<Post> allGoodPosts = prp.findAllGood();
        allGoodPosts.removeIf(post -> !(post.getText().contains(query) || post.getTitle().contains(query)));
        ArrayList<Post> posts = this.cutArray(offset, limit, allGoodPosts);
        return new PostsDto(allGoodPosts.size(), posts);
    }

    public PostsDto getDatePosts (int offset, int limit, String time){
        LocalDate ldate = LocalDate.parse(time);
        Date date = java.sql.Date.valueOf(ldate);
        List<Post> allGoodPosts = prp.findAllGood();
        allGoodPosts.removeIf(post -> !post.getTime().equals(date));
        ArrayList<Post> posts = this.cutArray(offset, limit, allGoodPosts);
        return new PostsDto(allGoodPosts.size(), posts);
    }

    private ArrayList<Post> cutArray(int offset, int limit, List<Post> list) {
        ArrayList<Post> finalList = new ArrayList<>();
        for (int i = offset; i < limit; i++) {
            finalList.add(list.get(i));
        }
        return finalList;
    }

}
