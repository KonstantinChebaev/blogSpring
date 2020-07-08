package main.domain.post;

import main.domain.CalendarResponseDto;
import main.domain.ModerationRequestDto;
import main.domain.ResultResponse;
import main.domain.tag.Tag;
import main.domain.tag.TagUseCase;
import main.domain.user.User;
import main.domain.user.UserAuthServise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Component
public class PostUseCase {
    @Autowired
    PostRepositoryPort postRepositoryPort;

    @Autowired
    UserAuthServise userServise;

    @Autowired
    TagUseCase tagUseCase;

    @Autowired
    VotesService votesService;

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


    public ResponseEntity<?> getTagPosts(int offset, int limit, String tagName) {
        List<Tag> tags = tagUseCase.getQueryTag(tagName);
        if (tags == null){
            return getErrorResponce("tag", "Такого тега не существует");
        }
        List<Post> posts = tags.get(0).getPosts();
        posts = postRepositoryPort.findAllGood(posts);
        PostsDtoResponse pdr = new PostsDtoResponse();
        pdr.setCount(posts.size());
        posts = this.cutArray(offset, limit, posts);
        pdr.setPosts(posts);
        return new ResponseEntity<>(pdr, HttpStatus.OK);

    }

    public PostsDtoResponse getModerationPosts(int offset, int limit, String status, HttpServletRequest request) {
        int moderId = userServise.getCurrentUser(request).getId();
        List<Post> posts = postRepositoryPort.findByModerStat(status);
        posts.removeIf(post -> !(post.isActive() && (post.getModeratorId() == moderId || status.equals("NEW"))));
        int count = posts.size();
        posts = cutArray(offset, limit, posts);
        return new PostsDtoResponse(count, posts);
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

    public ResponseEntity<ResultResponse> editPost(int id, HttpServletRequest request, PostPostDto postPostDto) {
        ResultResponse resultResponse = new ResultResponse();
        HashMap<String, Object> response = new HashMap<>();
        User user = userServise.getCurrentUser(request);
        Optional <Post> optionalPost = postRepositoryPort.findById(id);
        if(optionalPost.isEmpty()){
            resultResponse.setResult(false);
            response.put("errors", "Пост с id " + id + " не найден");
            resultResponse.setErrors(response);
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        Post post = optionalPost.get();

        if(user.getId()!=post.getUser().getId()){
            resultResponse.setResult(false);
            response.put("errors", "У вас нет прав редактировать пост");
            resultResponse.setErrors(response);
            return new ResponseEntity<>(resultResponse, HttpStatus.FORBIDDEN);
        }
        response = checkPostInput(postPostDto);
        if (response.get("result").equals(false)){
            resultResponse.setResult(false);
            resultResponse.setErrors(response);
            return new ResponseEntity<>(resultResponse, HttpStatus.BAD_REQUEST);
        }
        resultResponse.setResult(true);
        savePost(postPostDto,post);
        return new ResponseEntity<>(resultResponse, HttpStatus.OK);
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

    public ResponseEntity<?>  moderate(ModerationRequestDto moderationRequestDto, HttpServletRequest request) {
        Optional <Post> optpost = postRepositoryPort.findById(moderationRequestDto.getPostId());
        if (optpost.isEmpty()){
            return getErrorResponce("badId", "Post with this id does not found");
        }
        Post post = optpost.get();
        int userId = userServise.getCurrentUser(request).getId();
        if (moderationRequestDto.getDecision().equals("DECLINE")){
            post.setModerStat(Post.ModerStat.DECLINED);
        } else if (moderationRequestDto.getDecision().equals("ACCEPT")){
            post.setModerStat(Post.ModerStat.ACCEPTED);
        }
        post.setModeratorId(userId);
        return new ResponseEntity<>(new ResultResponse(true,null),HttpStatus.OK);

    }

    //аргументы по два передаются ключ - значение
    private ResponseEntity<?> getErrorResponce (String ... messages){
        ResultResponse resultResponse = new ResultResponse();
        resultResponse.setResult(false);
        HashMap <String, Object> errors = new HashMap<>();
        if (messages.length>0){
            for (int x = 0; x < messages.length; x++){
                if(x%2!=0) continue;
                errors.put(messages[x],messages[x+1]);
            }
        }
        resultResponse.setErrors(errors);
        return  new ResponseEntity<>(resultResponse, HttpStatus.BAD_REQUEST);

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

    public ResponseEntity<CalendarResponseDto> getCalend(String year) {
        Integer searchingYear = LocalDate.now().getYear();
        if (year != null) {
            int inputYear = Integer.parseInt(year);
            if (inputYear<searchingYear){
                searchingYear = inputYear;
            }
        }

        Set<Integer> allYears = new TreeSet<>();
        Map<String, Long> posts = new HashMap<>();
        int postYear;
        String day;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Post post : postRepositoryPort.findAll()){
            postYear = post.getTime().getYear();
            allYears.add(postYear);
            if (searchingYear == postYear){
                day = post.getTime().format(formatter);
                if(posts.containsKey(day)){
                    long count = posts.get(day)+1;
                    posts.replace(day, count);
                } else {
                    posts.put(day,1L);
                }
            }
        }
        return new ResponseEntity<>(new CalendarResponseDto(allYears,posts), HttpStatus.OK);
       }

    public ResponseEntity<ResultResponse> votePost(String vote, Integer postId, HttpServletRequest request) {
        User user = userServise.getCurrentUser(request);
        if (postId <= 0) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        Post post = postRepositoryPort.findById(postId).orElse(null);
        if (post == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        ResultResponse result = new ResultResponse();
        result.setResult(votesService.vote(vote, user, post));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
