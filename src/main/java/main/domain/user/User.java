package main.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.web.security.Authority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "users")
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails, Serializable {
    private static final long serialVersionUID = 902783495L;

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
    //    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
//    private List<Post> posts;

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//    @JsonIgnore
//    private Set<UserRole> userRoles = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        // userRoles.forEach(ur -> authorities.add(new Authority(ur.getRole().getName()))); изначальная строчка
        //Далее моя реализация
        authorities.add(new Authority("USER"));
        if(isModerator){
            authorities.add(new Authority("ADMIN"));
        }
        return authorities;
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
