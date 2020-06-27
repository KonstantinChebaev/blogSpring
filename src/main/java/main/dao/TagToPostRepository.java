package main.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagToPostRepository extends CrudRepository<TagToPost, Integer> {
    Iterable<TagToPost> findByTagId (Integer tagId);
    Iterable<TagToPost> findByPostId (Integer postId);
}
