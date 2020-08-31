package main.web.api.post;

import main.domain.ModerationRequestDto;
import main.domain.ResultResponse;
import main.domain.post.dto.PostPostDto;
import main.domain.post.PostServise;
import main.domain.post.dto.AllPostsResponseDto;
import main.domain.post.dto.PostWithCommentsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/api")
public class ApiPostController {

    @Autowired
    PostServise puc;


    @GetMapping("/post")
    public AllPostsResponseDto getAllPosts(@RequestParam int offset,
                                           @RequestParam int limit,
                                           @RequestParam String mode) {
        return puc.getAll(offset, limit, mode);
    }

    @GetMapping("/post/{id}")
    public ResponseEntity <PostWithCommentsDto> getPost(@PathVariable int id, HttpServletRequest request) {
        return puc.findById(id, request);
    }

    //need tests
    @PutMapping("/post/{id}")
    public ResponseEntity<ResultResponse> putPost(@PathVariable int id,
                                                  HttpServletRequest request,
                                                  @RequestBody PostPostDto postPostDto) {
        return puc.editPost(id, request, postPostDto);
    }

    @PostMapping("/post")
    public HashMap<String, Object> postPost(@RequestBody PostPostDto ppDto, HttpServletRequest request) {
        return puc.createPost(ppDto, request);
    }

    @GetMapping("/post/search")
    public AllPostsResponseDto searchPost(@RequestParam int offset,
                                          @RequestParam int limit,
                                          @RequestParam String query) {
        return puc.searchPost(offset, limit, query);
    }

    @GetMapping("/post/byDate")
    public AllPostsResponseDto getDatePosts(@RequestParam int offset,
                                            @RequestParam int limit,
                                            @RequestParam String date) {
        return puc.getDatePosts(offset, limit, date);
    }

    @GetMapping("/post/byTag")
    public ResponseEntity<?> getTagPosts(@RequestParam int offset,
                                         @RequestParam int limit,
                                         @RequestParam String tag) {
        return puc.getTagPosts(offset, limit, tag);
    }

    //need tests
    @GetMapping("/post/moderation")
    public AllPostsResponseDto getModerationPosts(@RequestParam int offset,
                                                  @RequestParam int limit,
                                                  @RequestParam String status,
                                                  HttpServletRequest request) {
        return puc.getModerationPosts(offset, limit, status, request);
    }

    //need tests
    @PostMapping("/moderation")
    public ResponseEntity<?> postModeration(@RequestBody ModerationRequestDto moderationRequestDto,
                                            HttpServletRequest request) {
        return puc.moderate(moderationRequestDto, request);
    }


    //need tests
    @GetMapping("/post/my")
    public AllPostsResponseDto getMyPosts(@RequestParam int offset,
                                          @RequestParam int limit,
                                          @RequestParam String status,
                                          HttpServletRequest request) {
        return puc.getUserPosts(offset, limit, status, request);
    }

    //need tests
    @PostMapping("/post/{vote}")
    public ResponseEntity<ResultResponse> votePost(@PathVariable String vote, @RequestBody Map<String, Integer> body, HttpServletRequest request) {
        return puc.votePost(vote, body.getOrDefault("post_id", 0), request);
    }

}
