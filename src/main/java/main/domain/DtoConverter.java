package main.domain;

import main.domain.comment.PostComment;
import main.domain.post.Post;
import main.domain.post.dto.*;
import main.domain.tag.Tag;
import main.domain.user.User;
import main.domain.user.dto.LoggedInUserDto;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DtoConverter {

    public PostPlainDto postToDto(Post p) {
        String postText = Jsoup.parse(p.getText()).text();
        String announce = postText.length() > 150 ? postText.substring(0, 150) + "..." : postText;
        PostPlainDto ppd = PostPlainDto.builder()
                .commentCount(p.getPostComments().size())
                .id(p.getId())
                .title(p.getTitle())
                .viewCount(p.getViewCount())
                .time(p.getTime())
                .user(new PostUserDto(p.getUser().getId(), p.getUser().getName()))
                .announce(announce)
                .dislikeCount(p.getPostVotes().stream().filter(item -> item.getValue() < 0).count())
                .likeCount(p.getPostVotes().stream().filter(item -> item.getValue() > 0).count())
                .build();
        return ppd;
    }

    public List<PostPlainDto> listPostToDtoList(List <Post> posts){
        List<PostPlainDto> postPlainDtos = new ArrayList<>();
        for (Post p: posts) {
            PostPlainDto ppd = postToDto(p);
            postPlainDtos.add(ppd);
        }
        return postPlainDtos;
    }

    public PostWithCommentsDto postToPostWithComments (Post p){
        PostWithCommentsDto postWithCom = PostWithCommentsDto.builder()
                .id(p.getId())
                .text(p.getText())
                .time(p.getTime())
                .title(p.getTitle())
                .viewCount(p.getViewCount())
                .user(new PostUserDto(p.getUser().getId(), p.getUser().getName()))
                .comments(commentListToCommentDtoList(p.getPostComments()))
                .tags(p.getTags().stream().map(Tag::getName).collect(Collectors.toList()))
                .dislikeCount(p.getPostVotes().stream().filter(item -> item.getValue() < 0).count())
                .likeCount(p.getPostVotes().stream().filter(item -> item.getValue() > 0).count())
                .build();
        return postWithCom;
    }

    public List <CommentDto> commentListToCommentDtoList (List <PostComment> postCommentList) {
        List<CommentDto> commentDtoList = new ArrayList<>(postCommentList.size());
        for (PostComment pc : postCommentList) {
            User user = pc.getUser();
            CommentDto commentDto = CommentDto.builder()
                    .id(pc.getId())
                    .text(pc.getText())
                    .time(pc.getTime())
                    .user(new UserWithPhotoDto(user.getId(), user.getName(), user.getPhoto()))
                    .build();
            commentDtoList.add(commentDto);
        }
        return commentDtoList;
    }

    //узнать что такое settings и как доставать modCount
    public LoggedInUserDto userToLoggedInUser (User u){
        LoggedInUserDto loggedInUserDto = LoggedInUserDto.builder()
                .id(u.getId())
                .email(u.getEmail())
                .moderation(u.isModerator())
                .name(u.getName())
                .photo(u.getPhoto())
                .settings(true)
                .moderationCount(0)
                .build();
        return loggedInUserDto;
    }


}
