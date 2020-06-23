package main.domain.post;

import main.dao.TagRepository;
import main.domain.tag.Tag;
import main.domain.tag.TagToPost;
import main.domain.tag.TagToPostRepositoryPort;
import main.domain.tag.TagUseCase;
import main.domain.user.User;
import main.domain.user.UserAuthUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


@Component
public class PostUseCase {
    @Autowired
    PostRepositoryPort postRepositoryPort;

    @Autowired
    TagToPostRepositoryPort tagToPostRP;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    UserAuthUseCase userServise;

    @Autowired
    TagUseCase tagUseCase;

    private static ArrayList<Post> lastQueryPosts;
    private static String lastQuery;

    public PostsDtoResponse getAll(int offset, int limit, String mode) {
        List<Post> posts = postRepositoryPort.findAll();
        int count = posts.size();
        posts = this.cutArray(offset, limit,posts);
        return new PostsDtoResponse(count, posts);
    }

    public Post findById(int id) {
        Optional <Post> optionalPost = postRepositoryPort.findById(id);
        if (optionalPost.isEmpty()){
            return null;
        }
         return optionalPost.get();
    }



    public PostsDtoResponse searchPost(int offset, int limit, String query) {
        List<Post> allGoodPosts = postRepositoryPort.findAllGood();
        allGoodPosts.removeIf(post -> !(post.getText().contains(query) || post.getTitle().contains(query)));
        ArrayList<Post> posts = this.cutArray(offset, limit, allGoodPosts);
        return new PostsDtoResponse(allGoodPosts.size(), posts);
    }

    public PostsDtoResponse getDatePosts(int offset, int limit, String time) {
        LocalDate ldate = LocalDate.parse(time);
        Date date = java.sql.Date.valueOf(ldate);
        List<Post> allGoodPosts = postRepositoryPort.findAllGood();
        allGoodPosts.removeIf(post -> !post.getTime().equals(date));
        ArrayList<Post> posts = this.cutArray(offset, limit, allGoodPosts);
        return new PostsDtoResponse(allGoodPosts.size(), posts);
    }


    public PostsDtoResponse getTagPosts(int offset, int limit, String tagName) {
        List<Post> allPosts = postRepositoryPort.findAllGood();
        Tag justTag;
        Optional<Tag> tag = tagRepository.findByName(tagName);
        if (tag.isPresent()) {
            justTag = tag.get();
        } else {
            return null; //переделать чтобы возвращалось сообщение о несуществующем теге
        }
        List<TagToPost> someTTPs = tagToPostRP.findByTagId(justTag.getId());
        ArrayList<Post> posts = new ArrayList<>();
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

    public PostsDtoResponse getModerationPosts(int offset, int limit, String status, HttpServletRequest request) {
        int moderId = userServise.getCurrentUser(request).getId();
        List<Post> posts = postRepositoryPort.findByModerStat(status);
        posts.removeIf(post -> !(post.isActive() && (post.getModeratorId() == moderId || status.equals("NEW"))));
        int count = posts.size();
        posts = cutArray(offset, limit, posts);
        return new PostsDtoResponse(count, posts);
    }


    private ArrayList<Post> cutArray(int offset, int limit, List<Post> list) {
        ArrayList<Post> finalList = new ArrayList<>();
        if (limit > list.size()) {
            limit = list.size();
        }
        for (int i = offset; i < limit; i++) {
            finalList.add(list.get(i));
        }
        return finalList;
    }


    public PostsDtoResponse getUserPosts(int offset, int limit, String status, HttpServletRequest request) {
        User user = userServise.getCurrentUser(request);
        List <Post> posts = user.getPosts();
        List <Post> finalPosts = new ArrayList<>();
        if(status.equals("inactive")){
            posts.removeIf(Post::isActive);
            finalPosts = posts;
        } else if (status.equals("pending")){
            for (Post post : posts){
                if (post.isActive()&&post.getModerStat().equals(Post.ModerStat.NEW)){
                    finalPosts.add(post);
                }
            }
        } else if (status.equals("declined")){
            for (Post post : posts){
                if (post.isActive()&&post.getModerStat().equals(Post.ModerStat.DECLINED)){
                    finalPosts.add(post);
                }
            }
        } else if (status.equals("published")){
            for (Post post : posts){
                if (post.isActive()&&post.getModerStat().equals(Post.ModerStat.ACCEPTED)){
                    finalPosts.add(post);
                }
            }
        }
        int count = finalPosts.size();
        finalPosts = cutArray(offset,limit,finalPosts);
        return new PostsDtoResponse(count,finalPosts);
    }


    //Методы для создания или редактирования постов

    public HashMap<String, Object> createPost(PostPostDto postPostDto, HttpServletRequest request) {
        User user = userServise.getCurrentUser(request);
        HashMap<String, Object> response = new HashMap<>();
        response = checkPostInput(postPostDto);
        if (response.get("result").equals(false)){
            return response;
        }
        final Post newPost = Post.builder()
                .viewCount(0)
                .user(user)
                .moderatorId(0)
                .build();
        savePost(postPostDto,newPost);
        return response;
    }

    public HashMap<String, Object> editPost(int id, HttpServletRequest request, PostPostDto postPostDto) {
        HashMap<String, Object> response = new HashMap<>();
        User user = userServise.getCurrentUser(request);
        Optional <Post> optionalPost = postRepositoryPort.findById(id);
        if(optionalPost.isEmpty()){
            response.put("result", false);
            response.put("errors",new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));
            return response;
        }
        Post post = optionalPost.get();

        if(user.getId()!=post.getUser().getId()){
            response.put("result", false);
            response.put("errors",new ResponseEntity<>(null, HttpStatus.FORBIDDEN));
            return response;
        }
        response = checkPostInput(postPostDto);
        if (response.get("result").equals(false)){
            return response;
        }
        savePost(postPostDto,post);
        return response;
    }

    public HashMap <String, Object> checkPostInput (PostPostDto postPostDto){
        HashMap<String, Object> response = new HashMap<>();
        HashMap <String, Object> errors = new HashMap<>();
        if(postPostDto.getText() == null || postPostDto.getText().length() < 500){
            errors.put("text", "Текст публикации слишком короткий");
        }
        if (postPostDto.getTitle() == null || postPostDto.getTitle().length() < 10){
            errors.put("title", "Заголовок слишком короткий");
        }
        if(!errors.isEmpty()){
            response.put("result", false);
            response.put("errors",errors);
            return response;
        }
        response.put("result", true);
        return response;
    }

    public void savePost(PostPostDto postPostDto, Post post) {
        post.setActive(postPostDto.getActive());
        post.setText(postPostDto.getText());
        post.setTitle(postPostDto.getTitle());
        post.setModerStat(Post.ModerStat.NEW);
        LocalDateTime date = postPostDto.getTime();
        if (date.isBefore(LocalDateTime.now())) {
            date = LocalDateTime.now();
        }
        post.setTime(date);
        if (postPostDto.getTags() != null) {
            postPostDto.getTags().forEach(tag -> post.getTags().add(tagUseCase.saveTag(tag)));
        }
        postRepositoryPort.savePost(post);
    }
}
