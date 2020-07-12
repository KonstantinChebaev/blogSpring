package main.domain.post;

import org.jsoup.Jsoup;
import main.domain.CalendarResponseDto;
import main.domain.ModerationRequestDto;
import main.domain.ResultResponse;
import main.domain.post.dto.AllPostsResponseDto;
import main.domain.post.dto.PostPostDto;
import main.domain.post.dto.PostPlainDto;
import main.domain.post.dto.PostUserDto;
import main.domain.tag.Tag;
import main.domain.tag.TagServise;
import main.domain.user.User;
import main.domain.user.UserServise;
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
public class PostServise {
    @Autowired
    PostRepositoryPort postRepositoryPort;

    @Autowired
    UserServise userServise;

    @Autowired
    TagServise tagServise;

    @Autowired
    VotesService votesService;

    public AllPostsResponseDto getAll(int offset, int limit, String mode) {
        List<Post> posts = postRepositoryPort.findAll();
        int count = posts.size();
        List<PostPlainDto> plainPosts = getPlainPosts(posts);
        plainPosts.subList(offset,limit);
        sortPlainPostsByMode(plainPosts, mode);
        return new AllPostsResponseDto(count, plainPosts);
    }

    private List<PostPlainDto> getPlainPosts (List <Post> posts){
        List<PostPlainDto> postPlainDtos = new ArrayList<>();
        for (Post p: posts) {
            String postText = Jsoup.parse(p.getText()).text();
            String announce = postText.length() > 150 ? postText.substring(0, 150) + "..." : postText;
            PostPlainDto ppd = PostPlainDto.builder()
                    .commentCount(p.getPostComments().size())
                    .id(p.getId())
                    .title(p.getTitle())
                    .viewCount(p.getViewCount())
                    .time(p.getTime())
                    .user(new PostUserDto(p.getUser().getId(),p.getUser().getName()))
                    .announce(announce)
                    .dislikeCount(p.getPostVotes().stream().filter(item -> item.getValue() < 0).count())
                    .likeCount(p.getPostVotes().stream().filter(item -> item.getValue() > 0).count())
                    .build();
            postPlainDtos.add(ppd);
        }
        return postPlainDtos;
    }

    private List<PostPlainDto> sortPlainPostsByMode(List<PostPlainDto> list, String mode) {
        switch (mode) {
            case "BEST":
                list.sort(Comparator.comparing(PostPlainDto::getLikeCount).reversed());
                break;
            case "EARLY":
                list.sort(Comparator.comparing(PostPlainDto::getTime));
                break;
            case "RECENT":
                list.sort(Comparator.comparing(PostPlainDto::getTime).reversed());
                break;
            case "POPULAR":
                list.sort(Comparator.comparing(PostPlainDto::getCommentCount).reversed());
                break;
        }
        return list;
    }

    public Post findById(int id) {
        Optional <Post> optionalPost = postRepositoryPort.findById(id);
        if (optionalPost.isEmpty()){
            return null;
        }
         return optionalPost.get();
    }

    public AllPostsResponseDto searchPost(int offset, int limit, String query) {
        List<Post> allGoodPosts = postRepositoryPort.findByQuery(query);
        allGoodPosts.subList(offset,limit);
        return new AllPostsResponseDto(allGoodPosts.size(), getPlainPosts(allGoodPosts));
    }

    public AllPostsResponseDto getDatePosts(int offset, int limit, String time) {
        LocalDate ldate = LocalDate.parse(time);
        List<Post> posts = postRepositoryPort.findByDate(ldate);
        int size = posts.size();
        posts.subList(offset, limit);
        return new AllPostsResponseDto(size, getPlainPosts(posts));
    }


    public ResponseEntity<?> getTagPosts(int offset, int limit, String tagName) {
        List<Tag> tags = tagServise.getQueryTag(tagName);
        if (tags == null || tags.isEmpty()){
            return getErrorResponce("tag", "Такого тега не существует");
        }
        List<Post> posts = tags.get(0).getPosts();
        posts = postRepositoryPort.getAllGood(posts);
        int size = posts.size();
        posts.subList(offset,limit);

        AllPostsResponseDto pdr = new AllPostsResponseDto();
        pdr.setCount(size);
        pdr.setPosts(getPlainPosts(posts));
        return new ResponseEntity<>(pdr, HttpStatus.OK);
    }

    public AllPostsResponseDto getModerationPosts(int offset, int limit, String status, HttpServletRequest request) {
        int moderId = userServise.getCurrentUser(request).getId();
        List<Post> posts = postRepositoryPort.findByModerStat(status);
        posts.removeIf(post -> !(post.isActive() && (post.getModeratorId() == moderId || status.equals("NEW"))));
        int count = posts.size();
        posts.subList(offset,limit);
        return new AllPostsResponseDto(count, getPlainPosts(posts));
    }

    public AllPostsResponseDto getUserPosts(int offset, int limit, String status, HttpServletRequest request) {
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
        finalPosts.subList(offset,limit);
        return new AllPostsResponseDto(count,getPlainPosts(finalPosts));
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
            List <Tag> postTags = post.getTags();
            if (postTags == null){
                postTags = new ArrayList<>();
            }
            for (String tagName :  postPostDto.getTags() ){
                Tag tag = tagServise.saveTag(tagName, post);
                postTags.add(tag);
            }
            post.setTags(postTags);
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

    public ResponseEntity<?> setModeration(int postId, String desision, HttpServletRequest request) {
        Optional <Post> optpost = postRepositoryPort.findById(postId);
        if (optpost.isEmpty()){
            return getErrorResponce("badId", "Post with this id does not found");
        }
        Post post = optpost.get();
        if (desision.equals("ACCEPT")){
            post.setModerStat(Post.ModerStat.ACCEPTED);
        } else {
            post.setModerStat(Post.ModerStat.DECLINED);
        }
        post.setModeratorId(userServise.getCurrentUser(request).getId());
        postRepositoryPort.savePost(post);
        return new ResponseEntity<>(new ResultResponse(true,null), HttpStatus.OK);
    }
}
