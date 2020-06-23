package main.domain.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.domain.comment.PostComment;
import main.domain.tag.Tag;
import main.domain.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder(toBuilder = true)
@Entity
@Table(name = "posts")
@AllArgsConstructor
@NoArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "is_active", nullable = false, columnDefinition = "TINYINT")
    private boolean isActive;
    @Column(name = "moderation_status", nullable = false)

    @Enumerated(EnumType.STRING)
    private ModerStat moderStat;
    public enum ModerStat {NEW, ACCEPTED, DECLINED}

    @Column(name = "moderator_id", nullable = false)
    private int moderatorId;

    @ManyToOne(optional = false, cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName="id")
    private User user;

    @Column(nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime time;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column(name = "view_count", nullable = false)
    private int viewCount;


    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "tag2post",
            joinColumns = {@JoinColumn(name = "post_id")},
            inverseJoinColumns = {@JoinColumn(name = "tag_id")})
    private List<Tag> tags;

    @OneToMany(mappedBy = "post")
    private List<PostComment> postComments;

//    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, orphanRemoval = true)
//    private List<PostVote> postVotes;
//

//
//    public void incrementViewCount() {
//        this.viewCount++;
//    }
}
