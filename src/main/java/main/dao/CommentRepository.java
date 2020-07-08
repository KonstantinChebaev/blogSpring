package main.dao;

import main.domain.comment.PostComment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends CrudRepository<PostComment, Integer> {

}
