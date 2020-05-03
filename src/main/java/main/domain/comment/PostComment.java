package main.domain.comment;


import lombok.Data;

import javax.persistence.*;
import java.util.Date;
@Data
@Entity
@Table(name="post_comments")
public class PostComment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "parent_id")
    private int parentId;
    @Column(name = "post_id", nullable = false)
    private int postId;
    @Column(name = "user_id", nullable = false)
    private int userId;
    @Column(nullable = false, columnDefinition = "DATETIME")
    private Date time;

    public PostComment (){

    }
}
