package main.domain.post;

import main.dao.TagRepository;
import main.domain.tag.Tag;
import main.domain.tag.TagToPost;
import main.domain.tag.TagToPostRepositoryPort;
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

    @Autowired
    TagToPostRepositoryPort tagToPostRP;

    @Autowired
    TagRepository tagRepository;

    private static ArrayList<Post> lastQueryPosts;
    private static String lastQuery;

    public PostsDtoResponse getAll(int offset, int limit, String mode) {
        List<Post> allPosts = prp.findAll();
        ArrayList<Post> posts = this.cutArray(offset, limit, allPosts);
        return new PostsDtoResponse(allPosts.size(), posts);
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

    public PostsDtoResponse searchPost(int offset, int limit, String query) {
        List<Post> allGoodPosts = prp.findAllGood();
        allGoodPosts.removeIf(post -> !(post.getText().contains(query) || post.getTitle().contains(query)));
        ArrayList<Post> posts = this.cutArray(offset, limit, allGoodPosts);
        return new PostsDtoResponse(allGoodPosts.size(), posts);
    }

    public PostsDtoResponse getDatePosts (int offset, int limit, String time){
        LocalDate ldate = LocalDate.parse(time);
        Date date = java.sql.Date.valueOf(ldate);
        List<Post> allGoodPosts = prp.findAllGood();
        allGoodPosts.removeIf(post -> !post.getTime().equals(date));
        ArrayList<Post> posts = this.cutArray(offset, limit, allGoodPosts);
        return new PostsDtoResponse(allGoodPosts.size(), posts);
    }


    public PostsDtoResponse getTagPosts(int offset, int limit, String tagName) {
        List<Post> allPosts = prp.findAllGood();
        Tag justTag;
        Optional<Tag> tag = tagRepository.findByName(tagName);
        if (tag.isPresent()){
            justTag = tag.get();
        } else {
            return null; //переделать чтобы возвращалось сообщение о несуществующем теге
        }
        List<TagToPost> someTTPs = tagToPostRP.findByTagId(justTag.getId());
        ArrayList <Post> posts = new ArrayList<>();
        for (TagToPost ttp : someTTPs) {
            for (Post post : allPosts) {
                if (ttp.getPostId() == post.getId()) {
                    posts.add(post);
                }
            }
        }
        PostsDtoResponse pdr = new PostsDtoResponse();
        pdr.setCount(posts.size());
        posts = this.cutArray(offset, limit, posts);
        pdr.setPosts(posts);
        return pdr;

    }


    private ArrayList<Post> cutArray(int offset, int limit, List<Post> list) {
        ArrayList<Post> finalList = new ArrayList<>();
        if(limit>list.size()){
            limit = list.size();
        }
        for (int i = offset; i < limit; i++) {
            finalList.add(list.get(i));
        }
        return finalList;
    }
}
