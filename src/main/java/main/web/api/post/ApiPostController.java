package main.web.api.post;

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
@RequestMapping(value = "/api/post")
public class ApiPostController {

    @Autowired
    PostServise puc;


    @GetMapping("")
    public AllPostsResponseDto getAllPosts(@RequestParam int offset,
                                           @RequestParam int limit,
                                           @RequestParam String mode) {
        return puc.getAll(offset, limit, mode);
    }

    //need tests
    @GetMapping("/{id}")
    public ResponseEntity getPost(@PathVariable int id) {
        PostWithCommentsDto postWithCommentsDto = puc.findById(id);
        if (postWithCommentsDto == null) {
            Boolean result = false;
            return new ResponseEntity(result, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(postWithCommentsDto, HttpStatus.OK);
    }

    //need tests
    @PutMapping("/{id}")
    public ResponseEntity<ResultResponse> putPost(@PathVariable int id,
                                                  HttpServletRequest request,
                                                  @RequestBody PostPostDto postPostDto) {
        return puc.editPost(id, request, postPostDto);
    }

    @PostMapping("")
    public HashMap<String, Object> postPost(@RequestBody PostPostDto ppDto, HttpServletRequest request) {
        return puc.createPost(ppDto, request);
    }

    @GetMapping("/search")
    public AllPostsResponseDto searchPost(@RequestParam int offset,
                                          @RequestParam int limit,
                                          @RequestParam String query) {
        return puc.searchPost(offset, limit, query);
    }

    @GetMapping("/byDate")
    public AllPostsResponseDto getDatePosts(@RequestParam int offset,
                                            @RequestParam int limit,
                                            @RequestParam String date) {
        return puc.getDatePosts(offset, limit, date);
    }

    @GetMapping("/byTag")
    public ResponseEntity<?> getTagPosts(@RequestParam int offset,
                                         @RequestParam int limit,
                                         @RequestParam String tag) {
        return puc.getTagPosts(offset, limit, tag);
    }

    //need tests
    @GetMapping("/moderation")
    public AllPostsResponseDto getModerationPosts(@RequestParam int offset,
                                                  @RequestParam int limit,
                                                  @RequestParam String status,
                                                  HttpServletRequest request) {
        return puc.getModerationPosts(offset, limit, status, request);
    }

    //need tests
    @PostMapping("/moderation")
    public ResponseEntity<?> postModeration(@RequestParam int post_id,
                                            @RequestParam String desision,
                                            HttpServletRequest request) {
        return puc.setModeration(post_id, desision, request);
    }


    //need tests
    @GetMapping("/my")
    public AllPostsResponseDto getMyPosts(@RequestParam int offset,
                                          @RequestParam int limit,
                                          @RequestParam String status,
                                          HttpServletRequest request) {
        return puc.getUserPosts(offset, limit, status, request);
    }

    //need tests
    @PostMapping("/{vote}")
    public ResponseEntity<ResultResponse> votePost(@PathVariable String vote, @RequestBody Map<String, Integer> body, HttpServletRequest request) {
        return puc.votePost(vote, body.getOrDefault("post_id", 0), request);
    }

}
