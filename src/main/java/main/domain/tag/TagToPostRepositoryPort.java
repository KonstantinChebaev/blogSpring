package main.domain.tag;

import main.domain.post.Post;

import java.util.HashMap;
import java.util.List;

public interface TagToPostRepositoryPort {
    List<TagToPost> findByTagId (Integer tagId);
    List<TagToPost> findByPostId (Integer postId);
    List<TagToPost> findAll();

    void save(TagToPost tagToPost);
    // HashMap<String, Integer> countPostsByTags (List<Tag> tags);
}
