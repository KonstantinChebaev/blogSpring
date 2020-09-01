package main.domain.tag;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.domain.post.ModerationStatus;
import main.domain.post.Post;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name="tags")
@AllArgsConstructor
@NoArgsConstructor
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "tag2post",
            joinColumns = {@JoinColumn(name = "tag_id")},
            inverseJoinColumns = {@JoinColumn(name = "post_id")})

    private List<Post> posts;

    public Tag(String tagName) {
        this.name = tagName;
    }

    public double getGoodPostsAmount (){
        return (double) posts.stream().filter(p -> p.isActive()
                && p.getModerStat().equals(ModerationStatus.ACCEPTED)
                && p.getTime().isBefore(LocalDateTime.now())).count();
    }


}
