package main.domain.post;

import main.dao.PostRepository;
import main.dao.UserRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Component
public class PostServise {
    private PostRepository postRepository;
    private TagServise tagServise;
    private VotesService votesService;
    private DtoConverter dtoConverter;
    private UserRepository userRepository;
    private SettingsService settingsService;

    public PostServise(TagServise tagServise,
                       VotesService votesService,
                       DtoConverter dtoConverter,
                       UserRepository userRepository,
                       PostRepository postRepository,
                       SettingsService settingsService) {
        this.tagServise = tagServise;
        this.votesService = votesService;
        this.dtoConverter = dtoConverter;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.settingsService = settingsService;

    }

    public AllPostsResponseDto getAll(int offset, int limit, String mode) {
        Pageable pagedByMode;
        Page<Post> postPage;
        switch (mode) {
            case "early":
                pagedByMode = PageRequest.of(offset / limit, limit, Sort.by("time"));
                postPage = postRepository.findAllVisible(pagedByMode);
                break;
            case "recent":
                pagedByMode = PageRequest.of(offset / limit, limit, Sort.by("time").descending());
                postPage = postRepository.findAllVisible(pagedByMode);
                break;
            case "best":
                pagedByMode = PageRequest.of(offset / limit, limit);
                postPage = postRepository.findAllPostsByBest(pagedByMode);
                break;
            case "popular":
                pagedByMode = PageRequest.of(offset / limit, limit);
                postPage = postRepository.findAllPostsByPopular(pagedByMode);
                break;
            default:
                pagedByMode = PageRequest.of(offset / limit, limit);
                postPage = postRepository.findAllVisible(pagedByMode);
                break;
        }
        int count = (int) postPage.getTotalElements();
        if (count == 0) {
            return new AllPostsResponseDto(count, new ArrayList<>());
        }
        List<PostPlainDto> plainPosts = dtoConverter.listPostToDtoList(postPage);
        return new AllPostsResponseDto(count, plainPosts);
    }

    public ResponseEntity<PostWithCommentsDto> findById(int id, String userEmail) {
        Post post = postRepository.findById(id).orElseThrow();

        if(userEmail!=null) {
            userRepository.findByEmail(userEmail).ifPresent((user) -> {
                if (!(user.isModerator() || user.getId() == post.getUser().getId())) {
                    post.incrementViewCount();
                    postRepository.save(post);
                }
            });
        } else {
            post.incrementViewCount();
            postRepository.save(post);
        }

        var postWithCommentsDto = dtoConverter.postToPostWithComments(post);
        return new ResponseEntity(postWithCommentsDto, HttpStatus.OK);
    }

    public AllPostsResponseDto searchPost(int offset, int limit, String query) {
        Page<Post> posts = postRepository.findAllPostsByQuery(query, PageRequest.of(offset / limit, limit));
        return new AllPostsResponseDto(posts.getTotalPages(), dtoConverter.listPostToDtoList(posts));
    }

    public AllPostsResponseDto getDatePosts(int offset, int limit, String time) {
        if (time == null) {
            return new AllPostsResponseDto(0, null);
        }
        LocalDate dayXPlusOne = LocalDate.parse(time).plusDays(1);
        String date = time.replace("-", "");
        String datePlusOne = dayXPlusOne.toString().replace("-", "");
        Page<Post> posts = postRepository.findAllPostsByDate(date, datePlusOne, PageRequest.of(offset / limit, limit));
        return new AllPostsResponseDto(posts.getTotalPages(), dtoConverter.listPostToDtoList(posts));
    }


    public ResponseEntity<AllPostsResponseDto> getTagPosts(int offset, int limit, String tagName) {
        Tag tag = tagServise.findTag(tagName);
        if (tag == null) {
            return new ResponseEntity<>(new AllPostsResponseDto(0, null), HttpStatus.OK);
        }
        List<Post> posts = tag.getPosts();
        if (posts == null || posts.isEmpty()) {
            return new ResponseEntity<>(new AllPostsResponseDto(0, null), HttpStatus.OK);
        }
        List<Post> finalPosts = posts.stream()
                .filter(p -> p.isActive()
                        && p.getModerStat().equals(ModerationStatus.ACCEPTED)
                        && p.getTime().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());
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

    public AllPostsResponseDto getModerationPosts(int offset, int limit, String status, String userEmail) {
        int moderId = userRepository.findByEmail(userEmail).get().getId();
        Page <Post> postPage = postRepository.findAllPostsByModerStat(status.toUpperCase(),moderId, PageRequest.of(offset / limit, limit));
        return new AllPostsResponseDto(postPage.getTotalPages()*limit, dtoConverter.listPostToDtoList(postPage));
    }

    public AllPostsResponseDto getUserPosts(int offset, int limit, String status, String userEmail) {
        User user = userRepository.findByEmail(userEmail).get();
        List<Post> posts = user.getPosts();
        List<Post> finalPosts = new ArrayList<>();
        if (status.equals("inactive")) {
            posts.removeIf(Post::isActive);
            finalPosts = posts;
        } else if (status.equals("pending")) {
            for (Post post : posts) {
                if (post.isActive() && post.getModerStat().equals(ModerationStatus.NEW)) {
                    finalPosts.add(post);
                }
            }
        } else if (status.equals("declined")) {
            for (Post post : posts) {
                if (post.isActive() && post.getModerStat().equals(ModerationStatus.DECLINED)) {
                    finalPosts.add(post);
                }
            }
        } else if (status.equals("published")) {
            for (Post post : posts) {
                if (post.isActive() && post.getModerStat().equals(ModerationStatus.ACCEPTED)) {
                    finalPosts.add(post);
                }
            }
        }
        int count = finalPosts.size();
        if (count == 0) {
            return new AllPostsResponseDto(count, new ArrayList<PostPlainDto>());
        }
        if (count < limit) {
            limit = count;
        }
        finalPosts = finalPosts.subList(offset, limit);
        return new AllPostsResponseDto(count, dtoConverter.listPostToDtoList(finalPosts));
    }

    //Методы для создания или редактирования постов

    public ResponseEntity<ResultResponse> createPost(PostPostDto postPostDto, String userEmail) {
        User user = userRepository.findByEmail(userEmail).get();
        ResultResponse response = checkPostInput(postPostDto);
        if (!response.isResult()) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        final Post newPost = Post.builder()
                .viewCount(0)
                .user(user)
                .moderatorId(0)
                .build();
        savePost(postPostDto, newPost, user.isModerator());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<ResultResponse> editPost(int id, String userEmail, PostPostDto postPostDto) {
        User user = userRepository.findByEmail(userEmail).get();
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isEmpty()) {
            return new ResponseEntity<>(ResultResponse.getBadResultResponse("errors", "Пост с id " + id + " не найден"), HttpStatus.BAD_REQUEST);
        }
        Post post = optionalPost.get();

        if (user.getId() != post.getUser().getId()) {
            return new ResponseEntity<>(ResultResponse.getBadResultResponse("errors", "У вас нет прав редактировать пост"), HttpStatus.FORBIDDEN);
        }
        ResultResponse resultResponse = checkPostInput(postPostDto);
        if (resultResponse.isResult()) {
            return new ResponseEntity<>(resultResponse, HttpStatus.BAD_REQUEST);
        }
        savePost(postPostDto, post, user.isModerator());
        return new ResponseEntity<>(resultResponse, HttpStatus.OK);
    }


    public void savePost(PostPostDto postPostDto, Post post, boolean isModer) {
        post.setActive(postPostDto.getActive());
        post.setText(postPostDto.getText());
        post.setTitle(postPostDto.getTitle());
        post.setViewCount(0);

        ModerationStatus moderationStatus;
        GSettingsDto settings = settingsService.getSettings();
        if(!settings.getPostPremoderation()||isModer){
            moderationStatus = ModerationStatus.ACCEPTED;
        } else {
            moderationStatus = ModerationStatus.NEW;
        }
        post.setModerStat(moderationStatus);

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
        postRepository.save(post);
    }

    // модерационные методы
    //может быть стоит написать методы со сложными запросами id по email у пользоваелей
    public boolean moderate(ModerationRequestDto moderationRequestDto, String userEmail) {
        Optional<Post> optpost = postRepository.findById(moderationRequestDto.getPostId());
        if (optpost.isEmpty()) {
            return false;
        }
        Post post = optpost.get();
        int userId = userRepository.findByEmail(userEmail).get().getId();
        if (moderationRequestDto.getDecision().equals("decline")) {
            post.setModerStat(ModerationStatus.DECLINED);
        } else if (moderationRequestDto.getDecision().equals("accept")) {
            post.setModerStat(ModerationStatus.ACCEPTED);
        }
        post.setModeratorId(userId);
        postRepository.save(post);
        return true;
    }

    public ResultResponse checkPostInput(PostPostDto postPostDto) {
        ResultResponse resultResponse = new ResultResponse(true, new HashMap<>());
        if (postPostDto.getText() == null || postPostDto.getText().length() < 50) {
            resultResponse.addErrors("text", "Текст публикации слишком короткий");
        }
        if (postPostDto.getTitle() == null || postPostDto.getTitle().length() < 3) {
            resultResponse.addErrors("title", "Заголовок слишком короткий");
        }
        return resultResponse;
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

        for (Post post : postRepository.findAll()) {
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

    public ResponseEntity<ResultResponse> votePost(String vote, Integer postId, String userEmail) {
        User user = userRepository.findByEmail(userEmail).get();
        if (postId <= 0) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        Optional <Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        ResultResponse result = new ResultResponse();
        result.setResult(votesService.vote(vote, user, optionalPost.get()));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


}
