package main.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.domain.post.Post;
import main.domain.post.PostVote;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.time.LocalDateTime;
import java.util.*;

@Data
@Entity
@Table(name = "users")
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class User  {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "is_moderator", nullable = false, columnDefinition = "TINYINT")
    private boolean isModerator;
    @Column(name = "reg_time", nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime regTime;
    @Column(nullable = false, unique = true)
    private String name;
    @Email
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    private String code;

    @Column(length = 65535, columnDefinition="TEXT")
    @Type(type="text")
    private String photo;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Post> posts;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    private final List<PostVote> votes = new ArrayList<>();

    @Override
    public String toString (){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("User").append(this.hashCode())
                .append("id").append(id)
                .append("isModer").append(isModerator)
                .append("regTime").append(regTime.toString())
                .append("name").append(name)
                .append("email").append(email)
                .append(".");
        return stringBuilder.toString();
    }




}
