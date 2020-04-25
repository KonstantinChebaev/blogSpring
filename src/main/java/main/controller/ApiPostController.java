package main.controller;

import main.model.GeneralInfo;
import main.model.Post;
import main.model.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
public class ApiPostController {

    @Autowired
    private PostRepository postRepo;

    @GetMapping("/api/post/")
    public List<Post> getAllPosts (@PathVariable int offset, int limit, String mode){
        Iterable<Post> postIterable = postRepo.findAll();

        return new ArrayList<>();
    }
    @GetMapping("/api/post/{id}")
    public ResponseEntity getPost (@PathVariable int id){
        Optional <Post> opPost = postRepo.findById(id);
        if(!opPost.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return new ResponseEntity(opPost.get(),HttpStatus.OK);
    }
    @PostMapping("/api/post/") //доделать конечно
    public String postPost (){
        Post newPost = Post.builder()
                .time(new Date())
                .isActive(true)
                .title("title")
                .text("text")
                .build();
        postRepo.save(newPost);
        return "{result: true}";
    }



}
