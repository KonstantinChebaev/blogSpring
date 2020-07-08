package main.domain.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.domain.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name="post_votes")
public class PostVote {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(nullable = false)
    private int value;

    @ManyToOne(cascade = CascadeType.MERGE, optional = false)
    @JoinColumn(name="user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name="post_id", referencedColumnName = "id", nullable = false)
    private Post post;

    @Column(nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime time;




}
