package main.model;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
@Data
@Builder(toBuilder = true)
@Entity
@Table(name = "posts")
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
    @Column(name = "user_id", nullable = false)
    private int userId;
    @Column(nullable = false, columnDefinition = "DATETIME")
    private Date time;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;
    @Column(name = "view_count", nullable = false)
    private int viewCount;
    

}
