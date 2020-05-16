package main.web.api.post;

import main.domain.post.Post;
import main.domain.post.PostPostDto;
import main.domain.post.PostUseCase;
import main.domain.post.PostsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class ApiPostController {

    @Autowired
    PostUseCase puc;

    @GetMapping("/api/post/")
    public PostsDto getAllPosts (@RequestParam int offset,
                                 @RequestParam int limit,
                                 @RequestParam String mode){
        return puc.getAll(offset,limit,mode);
    }

    @GetMapping("/api/post/{id}")
    public ResponseEntity getPost (@PathVariable int id){
        Optional <Post> opPost = puc.findById(id);
        if(!opPost.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return new ResponseEntity(opPost.get(),HttpStatus.OK);
    }
    @PostMapping("/api/post/") //как-то еще надо автора доставать
    public String postPost (@RequestBody PostPostDto ppDto){
        puc.postPost(ppDto);
        return "{result: true}";
    }

    @GetMapping("/api/post/search/")
    public PostsDto searchPost (@RequestParam int offset,
                                      @RequestParam int limit,
                                      @RequestParam String query) {
        return puc.searchPost(offset, limit, query);
    }
    @GetMapping("/api/post/byDate")
    public PostsDto getDatePosts (@RequestParam int offset,
                                @RequestParam int limit,
                                @RequestParam String date) {
        return puc.getDatePosts(offset, limit, date);
    }

}
