package main.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
@Data
@Entity
@Table(name="post_votes")
public class PostVote {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "user_id", nullable = false)
    private int userId;
    @Column(name = "post_id", nullable = false)
    private int postId;
    @Column(nullable = false, columnDefinition = "DATETIME")
    private Date time;
    @Column(nullable = false)
    private byte value;

    public PostVote (){

    }

}
