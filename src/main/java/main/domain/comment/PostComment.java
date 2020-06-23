package main.domain.comment;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.domain.post.Post;
import main.domain.user.User;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
@Data
@Entity
@Builder(toBuilder = true)
@Table(name="post_comments")
@NoArgsConstructor
@AllArgsConstructor
public class PostComment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    private PostComment parentPostComment;

    @ManyToOne(cascade = CascadeType.ALL)
    private Post post;

    @ManyToOne(cascade = CascadeType.ALL)
    private User user;

    @Column(nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime time;

    @Column(length = 65535, columnDefinition="TEXT", nullable = false)
    @Type(type="text")
    private String text;
}
