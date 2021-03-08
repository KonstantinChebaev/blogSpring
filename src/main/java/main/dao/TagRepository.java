package main.dao;

import main.domain.post.Post;
import main.domain.tag.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer>, JpaSpecificationExecutor<Tag> {
    Optional<Tag> findByName (String name);

    @Query(nativeQuery = true, value = "SELECT tags.id, tags.name FROM tags " +
            "LEFT JOIN tag2post ON tag2post.tag_id = tags.id " +
            "GROUP BY tag2post.tag_id " +
            "ORDER BY COUNT(*) DESC ")
    Page<Tag> findPopular20Tags(Pageable paged);
}
