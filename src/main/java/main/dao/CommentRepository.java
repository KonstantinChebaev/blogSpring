package main.dao;

import main.domain.comment.PostComment;
import org.springframework.data.repository.CrudRepository;

public interface CommentRepository extends CrudRepository<PostComment, Integer> {
}
