package main.web.api.post;

import main.domain.ModerationRequestDto;
import main.domain.ResultResponse;
import main.domain.post.ModerationStatus;
import main.domain.post.dto.PostPostDto;
import main.domain.post.PostServise;
import main.domain.post.dto.AllPostsResponseDto;
import main.domain.post.dto.PostWithCommentsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping(value = "/api")
public class ApiPostController {

    @Autowired
    PostServise postServise;

    @GetMapping("/post")
    public AllPostsResponseDto getAllPosts(@RequestParam int offset,
                                           @RequestParam int limit,
                                           @RequestParam String mode) {
        return postServise.getAll(offset, limit, mode);
    }

    @GetMapping("/post/{id}")
    public ResponseEntity<PostWithCommentsDto> getPost(@PathVariable int id, HttpServletRequest request) {
        if (request.isRequestedSessionIdValid() && request.getUserPrincipal() != null) {
            String emailUser = request.getUserPrincipal().getName();
            return postServise.findById(id, emailUser);
        } else {
            return postServise.findById(id, null);
        }

    }

    @PutMapping("/post/{id}")
    public ResponseEntity<ResultResponse> putPost(@PathVariable int id,
                                                  HttpServletRequest request,
                                                  @RequestBody PostPostDto postPostDto) {
        if (request.isRequestedSessionIdValid() && request.getUserPrincipal() != null) {
            String emailUser = request.getUserPrincipal().getName();
            return postServise.editPost(id, emailUser, postPostDto);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/post")
    public ResponseEntity<ResultResponse> postPost(@RequestBody PostPostDto ppDto, HttpServletRequest request) {
        if (request.isRequestedSessionIdValid() && request.getUserPrincipal() != null) {
            String emailUser = request.getUserPrincipal().getName();
            return postServise.createPost(ppDto, emailUser);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/post/search")
    public AllPostsResponseDto searchPost(@RequestParam int offset,
                                          @RequestParam int limit,
                                          @RequestParam String query) {
        return postServise.searchPost(offset, limit, query);
    }

    @GetMapping("/post/byDate")
    public AllPostsResponseDto getDatePosts(@RequestParam int offset,
                                            @RequestParam int limit,
                                            @RequestParam String date) {
        return postServise.getDatePosts(offset, limit, date);
    }

    @GetMapping("/post/byTag")
    public ResponseEntity<AllPostsResponseDto> getTagPosts(@RequestParam int offset,
                                                           @RequestParam int limit,
                                                           @RequestParam String tag) {
        return postServise.getTagPosts(offset, limit, tag);
    }

    @GetMapping("/post/moderation")
    public ResponseEntity<AllPostsResponseDto> getModerationPosts(@RequestParam int offset,
                                                                  @RequestParam int limit,
                                                                  @RequestParam String status,
                                                                  HttpServletRequest request) {
        if (request.isRequestedSessionIdValid() && request.getUserPrincipal() != null) {
            String emailUser = request.getUserPrincipal().getName();
            return new ResponseEntity<>(postServise.getModerationPosts(offset, limit, status, emailUser), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

    }

    @PostMapping("/moderation")
    public boolean postModeration(@RequestBody ModerationRequestDto moderationRequestDto,
                                  HttpServletRequest request) {
        if (request.isRequestedSessionIdValid() && request.getUserPrincipal() != null) {
            String emailUser = request.getUserPrincipal().getName();
            return postServise.moderate(moderationRequestDto, emailUser);
        } else {
            return false;
        }

    }

    @GetMapping("/post/my")
    public ResponseEntity<AllPostsResponseDto> getMyPosts(@RequestParam int offset,
                                          @RequestParam int limit,
                                          @RequestParam String status,
                                          HttpServletRequest request) {
        if (request.isRequestedSessionIdValid() && request.getUserPrincipal() != null) {
            String emailUser = request.getUserPrincipal().getName();
            return new ResponseEntity<>(postServise.getUserPosts(offset, limit, status, emailUser), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/post/{vote}")
    public ResponseEntity<ResultResponse> votePost(@PathVariable String vote, @RequestBody Map<String, Integer> body, HttpServletRequest request) {
        if (request.isRequestedSessionIdValid() && request.getUserPrincipal() != null) {
            String emailUser = request.getUserPrincipal().getName();
            return postServise.votePost(vote, body.getOrDefault("post_id", 0), emailUser);
        } else {
            return new ResponseEntity<>(new ResultResponse(), HttpStatus.OK);
        }

    }
}
