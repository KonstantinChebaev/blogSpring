package main.web.api.post;

import main.domain.post.Post;
import main.domain.post.PostPostDto;
import main.domain.post.PostUseCase;
import main.domain.post.PostsDtoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@RestController
@RequestMapping(value = "/api/post")
public class ApiPostController {

    @Autowired
    PostUseCase puc;


    @GetMapping("")
    public PostsDtoResponse getAllPosts(@RequestParam int offset,
                                        @RequestParam int limit,
                                        @RequestParam String mode) {
        return puc.getAll(offset, limit, mode);
    }

    @GetMapping("/{id}")
    public ResponseEntity getPost(@PathVariable int id) {
        Post post = puc.findById(id);
        if (post == null) {
            Boolean result = false;
            return new ResponseEntity(result, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(post, HttpStatus.OK);
    }

    //need tests
    @PutMapping("/{id}")
    public HashMap<String,Object> putPost(@PathVariable int id,
                                          HttpServletRequest request,
                                          @RequestBody PostPostDto postPostDto) {
         HashMap <String, Object> response = puc.editPost(id,request, postPostDto);
        return response;
    }

    @PostMapping("") //need tests
    public HashMap<String,Object> postPost(@RequestBody PostPostDto ppDto, HttpServletRequest request) {
        return puc.createPost(ppDto, request);
    }

    @GetMapping("/search")
    public PostsDtoResponse searchPost(@RequestParam int offset,
                                       @RequestParam int limit,
                                       @RequestParam String query) {
        return puc.searchPost(offset, limit, query);
    }

    @GetMapping("/byDate")
    public PostsDtoResponse getDatePosts(@RequestParam int offset,
                                         @RequestParam int limit,
                                         @RequestParam String date) {
        return puc.getDatePosts(offset, limit, date);
    }

    @GetMapping("/byTag")
    public PostsDtoResponse getTagPosts(@RequestParam int offset,
                                        @RequestParam int limit,
                                        @RequestParam String tag) {
        return puc.getTagPosts(offset, limit, tag);
    }

    //need tests
    @GetMapping("/moderation")
    public PostsDtoResponse getModerationPosts(@RequestParam int offset,
                                               @RequestParam int limit,
                                               @RequestParam String status,
                                               HttpServletRequest request) {
        return puc.getModerationPosts(offset, limit, status, request);
    }

    //need tests
    @GetMapping("/my")
    public PostsDtoResponse getMyPosts(@RequestParam int offset,
                                               @RequestParam int limit,
                                               @RequestParam String status,
                                               HttpServletRequest request) {
        return puc.getUserPosts(offset, limit, status, request);
    }

}