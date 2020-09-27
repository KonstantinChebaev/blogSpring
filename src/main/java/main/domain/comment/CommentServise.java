package main.domain.comment;

import main.dao.CommentRepository;
import main.dao.PostRepository;
import main.dao.UserRepository;
import main.domain.ResultResponse;
import main.domain.post.Post;
import main.domain.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class CommentServise {

    private PostRepository postRepository;
    private CommentRepository commentsRepository;
    private UserRepository userRepository;

    public CommentServise (PostRepository postRepository,CommentRepository commentsRepository,
                           UserRepository userRepository){
        this.postRepository =  postRepository;
        this.commentsRepository = commentsRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<?> createComment (NewCommentRequestDto newCommentRequestDto, String userEmail){
        User user = userRepository.findByEmail(userEmail).get();
        Optional<Post> optionalPost = postRepository.findById(newCommentRequestDto.getPostId());
        if(optionalPost.isEmpty()){
            return new ResponseEntity<>(ResultResponse.getBadResultResponse("not_found", "Пост не найден"), HttpStatus.BAD_REQUEST);
        }
        if (newCommentRequestDto.getText().length()<6){
            return new ResponseEntity<>(ResultResponse.getBadResultResponse("text", "Текст комментария не задан или слишком короткий"), HttpStatus.BAD_REQUEST);
        }

        PostComment parentComment = null;
        if(newCommentRequestDto.getParentId()!=null){
            Optional<PostComment> optionalPostComment = commentsRepository.findById(newCommentRequestDto.getParentId());
            if(optionalPostComment.isPresent()){
                parentComment = optionalPostComment.get();
            }
        }
        PostComment newPostComment = PostComment.builder()
                .parentPostComment(parentComment)
                .post(optionalPost.get())
                .user(user)
                .time(LocalDateTime.now())
                .text(newCommentRequestDto.getText())
                .build();
        newPostComment = commentsRepository.save(newPostComment);
        return new ResponseEntity<>(new CommentIdDto(newPostComment.getId()), HttpStatus.OK);
    }
}
