package main.web.api.post;

import main.domain.post.Post;
import main.domain.post.PostUseCase;
import main.domain.post.PostsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class ApiPostController {

    @Autowired
    PostUseCase puc;

    @GetMapping("/api/post/")
    public PostsDto getAllPosts (@RequestParam int offset, @RequestParam int limit, @RequestParam String mode){
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
    public String postPost (@RequestParam String time ,
                            @RequestParam byte active,
                            @RequestParam String title,
                            @RequestParam String text,
                            @RequestParam String tags){
        boolean a = active==1;
        puc.postPost(time,a,title,text,tags);
        return "{result: true}";
    }

}
