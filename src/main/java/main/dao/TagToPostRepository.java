package main.dao;

import main.domain.tag.TagToPost;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TagToPostRepository extends CrudRepository<TagToPost, Integer> {
    List<TagToPost> findByTagId (Integer tagId);
    List<TagToPost> findByPostId (Integer postId);
}
