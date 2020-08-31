package main.domain.post;

import main.domain.DtoConverter;
import main.domain.CalendarResponseDto;
import main.domain.ModerationRequestDto;
import main.domain.ResultResponse;
import main.domain.globallSettings.GSettingsDto;
import main.domain.globallSettings.SettingsService;
import main.domain.post.dto.*;
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

    @Autowired
    DtoConverter dtoConverter;

    @Autowired
    SettingsService settingsService;

    public AllPostsResponseDto getAll(int offset, int limit, String mode) {
        List<Post> posts = postRepositoryPort.findAllGood();
        if (posts.isEmpty()) {
            return null;
        }
        int count = posts.size();
        List<PostPlainDto> plainPosts = dtoConverter.listPostToDtoList(posts);
        if (plainPosts.size() < limit) {
            limit = plainPosts.size();
        }
        sortPlainPostsByMode(plainPosts, mode);
        plainPosts = plainPosts.subList(offset, limit);
        return new AllPostsResponseDto(count, plainPosts);
    }

    private List<PostPlainDto> sortPlainPostsByMode(List<PostPlainDto> list, String mode) {
        switch (mode) {
            case "best":
                list.sort(Comparator.comparing(PostPlainDto::getLikeCount).reversed());
                break;
            case "early":
                list.sort(Comparator.comparing(PostPlainDto::getTimestamp));
                break;
            case "recent":
                list.sort(Comparator.comparing(PostPlainDto::getTimestamp).reversed());
                break;
            case "popular":
                list.sort(Comparator.comparing(PostPlainDto::getCommentCount).reversed());
                break;
        }
        return list;
    }

    public ResponseEntity<PostWithCommentsDto> findById(int id, HttpServletRequest request) {
        Optional<Post> optionalPost = postRepositoryPort.findById(id);
        if (optionalPost.isEmpty()) {
            Boolean result = false;
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        Post post = optionalPost.get();

        User currentUser = userServise.getCurrentUser(request);
        if (currentUser == null || !currentUser.isModerator() ||
                currentUser.getId() != post.getUser().getId()) {
            post.incrementViewCount();
            postRepositoryPort.savePost(post);
        }
        var postWithCommentsDto = dtoConverter.postToPostWithComments(post);
        return new ResponseEntity(postWithCommentsDto, HttpStatus.OK);
    }

    public AllPostsResponseDto searchPost(int offset, int limit, String query) {
        List<Post> posts = postRepositoryPort.findByQuery(query);
        if (posts.size() < limit) {
            limit = posts.size();
        }
        posts.subList(offset, limit);
        return new AllPostsResponseDto(posts.size(), dtoConverter.listPostToDtoList(posts));
    }

    public AllPostsResponseDto getDatePosts(int offset, int limit, String time) {
        LocalDate ldate = LocalDate.parse(time);
        List<Post> posts = postRepositoryPort.findByDate(ldate);
        int size = posts.size();
        if (size < limit) {
            limit = size;
        }
        posts = posts.subList(offset, limit);
        return new AllPostsResponseDto(size, dtoConverter.listPostToDtoList(posts));
    }


    public ResponseEntity<?> getTagPosts(int offset, int limit, String tagName) {
        Tag tag = tagServise.findTag(tagName);
        if (tag == null) {
            return getErrorResponce("tag", "Такого тега не существует");
        }
        List<Post> posts = tag.getPosts();
        if (posts == null || posts.isEmpty()) {
            return new ResponseEntity<>(new AllPostsResponseDto(0, null), HttpStatus.OK);
        }
        List<Post> finalPosts = new ArrayList<>();
        posts.stream()
                .filter(p -> p.isActive()
                        && p.getModerStat().equals(Post.ModerStat.ACCEPTED)
                        && p.getTime().isBefore(LocalDateTime.now()))
                .forEach(finalPosts::add);
        int size = finalPosts.size();
        if (size < limit) {
            limit = size;
        }
        finalPosts = finalPosts.subList(offset, limit);

        AllPostsResponseDto pdr = new AllPostsResponseDto();
        pdr.setCount(size);
        pdr.setPosts(dtoConverter.listPostToDtoList(finalPosts));
        return new ResponseEntity<>(pdr, HttpStatus.OK);
    }

    public AllPostsResponseDto getModerationPosts(int offset, int limit, String status, HttpServletRequest request) {
        int moderId = userServise.getCurrentUser(request).getId();
        List<Post> posts = postRepositoryPort.findByModerStat(status);
        posts.removeIf(post -> !(post.isActive() && (post.getModeratorId() == moderId || status.equals("NEW"))));
        int count = posts.size();
        if (count < limit) {
            limit = count;
        }
        posts = posts.subList(offset, limit);
        return new AllPostsResponseDto(count, dtoConverter.listPostToDtoList(posts));
    }

    public AllPostsResponseDto getUserPosts(int offset, int limit, String status, HttpServletRequest request) {
        User user = userServise.getCurrentUser(request);
        List<Post> posts = user.getPosts();
        List<Post> finalPosts = new ArrayList<>();
        if (status.equals("inactive")) {
            posts.removeIf(Post::isActive);
            finalPosts = posts;
        } else if (status.equals("pending")) {
            for (Post post : posts) {
                if (post.isActive() && post.getModerStat().equals(Post.ModerStat.NEW)) {
                    finalPosts.add(post);
                }
            }
        } else if (status.equals("declined")) {
            for (Post post : posts) {
                if (post.isActive() && post.getModerStat().equals(Post.ModerStat.DECLINED)) {
                    finalPosts.add(post);
                }
            }
        } else if (status.equals("published")) {
            for (Post post : posts) {
                if (post.isActive() && post.getModerStat().equals(Post.ModerStat.ACCEPTED)) {
                    finalPosts.add(post);
                }
            }
        }
        int count = finalPosts.size();
        if (count == 0) {
            return new AllPostsResponseDto(count, new ArrayList<PostPlainDto>());
        }
        if (count < limit) {
            limit = count - 1;
        }
        finalPosts = finalPosts.subList(offset, limit);
        return new AllPostsResponseDto(count, dtoConverter.listPostToDtoList(posts));
    }

    //Методы для создания или редактирования постов

    public HashMap<String, Object> createPost(PostPostDto postPostDto, HttpServletRequest request) {
        User user = userServise.getCurrentUser(request);
        HashMap<String, Object> response = new HashMap<>();
        response = checkPostInput(postPostDto);
        if (response.get("result").equals(false)) {
            return response;
        }
        final Post newPost = Post.builder()
                .viewCount(0)
                .user(user)
                .moderatorId(0)
                .build();
        savePost(postPostDto, newPost);
        return response;
    }

    public ResponseEntity<ResultResponse> editPost(int id, HttpServletRequest request, PostPostDto postPostDto) {
        ResultResponse resultResponse = new ResultResponse();
        HashMap<String, Object> response = new HashMap<>();
        User user = userServise.getCurrentUser(request);
        Optional<Post> optionalPost = postRepositoryPort.findById(id);
        if (optionalPost.isEmpty()) {
            resultResponse.setResult(false);
            response.put("errors", "Пост с id " + id + " не найден");
            resultResponse.setErrors(response);
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        Post post = optionalPost.get();

        if (user.getId() != post.getUser().getId()) {
            resultResponse.setResult(false);
            response.put("errors", "У вас нет прав редактировать пост");
            resultResponse.setErrors(response);
            return new ResponseEntity<>(resultResponse, HttpStatus.FORBIDDEN);
        }
        response = checkPostInput(postPostDto);
        if (response.get("result").equals(false)) {
            resultResponse.setResult(false);
            resultResponse.setErrors(response);
            return new ResponseEntity<>(resultResponse, HttpStatus.BAD_REQUEST);
        }
        resultResponse.setResult(true);
        savePost(postPostDto, post);
        return new ResponseEntity<>(resultResponse, HttpStatus.OK);
    }


    public void savePost(PostPostDto postPostDto, Post post) {
        post.setActive(postPostDto.getActive());
        post.setText(postPostDto.getText());
        post.setTitle(postPostDto.getTitle());

        Post.ModerStat moderStat = getModerStatus(post.getUser().isModerator());
        post.setModerStat(moderStat);

        LocalDateTime date = LocalDateTime.ofEpochSecond(postPostDto.getTimestamp(), 0, java.time.ZoneOffset.UTC);
        if (date.isBefore(LocalDateTime.now())) {
            date = LocalDateTime.now();
        }
        post.setTime(date);

        if (postPostDto.getTags() != null) {
            List<Tag> postTags = post.getTags();
            if (postTags == null) {
                postTags = new ArrayList<>();
            }
            for (String tagName : postPostDto.getTags()) {
                Tag tag = tagServise.saveTag(tagName, post);
                postTags.add(tag);
            }
            post.setTags(postTags);
        }
        postRepositoryPort.savePost(post);
    }

    // модерационные методы

    private Post.ModerStat getModerStatus(boolean moderator) {
        if (moderator) {
            return Post.ModerStat.ACCEPTED;
        }
        GSettingsDto settings = settingsService.getSettings();
        if (settings.getPostPremoderation()) {
            return Post.ModerStat.NEW;
        } else {
            return Post.ModerStat.ACCEPTED;
        }
    }


    public ResponseEntity<?> moderate(ModerationRequestDto moderationRequestDto, HttpServletRequest request) {
        Optional<Post> optpost = postRepositoryPort.findById(moderationRequestDto.getPostId());
        if (optpost.isEmpty()) {
            return getErrorResponce("badId", "Post with this id does not found");
        }
        Post post = optpost.get();
        int userId = userServise.getCurrentUser(request).getId();
        if (moderationRequestDto.getDecision().equals("DECLINE")) {
            post.setModerStat(Post.ModerStat.DECLINED);
        } else if (moderationRequestDto.getDecision().equals("ACCEPT")) {
            post.setModerStat(Post.ModerStat.ACCEPTED);
        }
        post.setModeratorId(userId);
        return new ResponseEntity<>(new ResultResponse(true, null), HttpStatus.OK);

    }

    //аргументы по два передаются ключ - значение
    private ResponseEntity<?> getErrorResponce(String... messages) {
        ResultResponse resultResponse = new ResultResponse();
        resultResponse.setResult(false);
        HashMap<String, Object> errors = new HashMap<>();
        if (messages.length > 0) {
            for (int x = 0; x < messages.length; x++) {
                if (x % 2 != 0) continue;
                errors.put(messages[x], messages[x + 1]);
            }
        }
        resultResponse.setErrors(errors);
        return new ResponseEntity<>(resultResponse, HttpStatus.BAD_REQUEST);

    }

    //исправить чтобы возращал объект ResultResponse или только ошибки в мапе
    public HashMap<String, Object> checkPostInput(PostPostDto postPostDto) {
        HashMap<String, Object> response = new HashMap<>();
        HashMap<String, Object> errors = new HashMap<>();
        if (postPostDto.getText() == null || postPostDto.getText().length() < 500) {
            errors.put("text", "Текст публикации слишком короткий");
        }
        if (postPostDto.getTitle() == null || postPostDto.getTitle().length() < 10) {
            errors.put("title", "Заголовок слишком короткий");
        }
        if (!errors.isEmpty()) {
            response.put("result", false);
            response.put("errors", errors);
            return response;
        }
        response.put("result", true);
        return response;
    }

    public ResponseEntity<CalendarResponseDto> getCalend(String year) {
        Integer searchingYear = LocalDate.now().getYear();
        if (year != null) {
            int inputYear = Integer.parseInt(year);
            if (inputYear < searchingYear) {
                searchingYear = inputYear;
            }
        }

        Set<Integer> allYears = new TreeSet<>();
        Map<String, Long> posts = new HashMap<>();
        int postYear;
        String day;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Post post : postRepositoryPort.findAll()) {
            postYear = post.getTime().getYear();
            allYears.add(postYear);
            if (searchingYear == postYear) {
                day = post.getTime().format(formatter);
                if (posts.containsKey(day)) {
                    long count = posts.get(day) + 1;
                    posts.replace(day, count);
                } else {
                    posts.put(day, 1L);
                }
            }
        }
        return new ResponseEntity<>(new CalendarResponseDto(allYears, posts), HttpStatus.OK);
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
