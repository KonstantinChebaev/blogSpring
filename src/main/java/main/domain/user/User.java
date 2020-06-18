package main.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
<<<<<<< HEAD
=======
import main.security.Authority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
>>>>>>> experiments

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
<<<<<<< HEAD
=======
import java.util.HashSet;
import java.util.Set;
>>>>>>> experiments

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
    private Date regTime;
    @Column(nullable = false, unique = true)
    private String name;
    @Email
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    private String code;
    @Column(columnDefinition = "TEXT")
    private String photo;
//        @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
//    private List<Post> posts;




}
