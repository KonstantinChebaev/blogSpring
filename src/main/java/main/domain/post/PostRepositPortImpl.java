package main.domain.post;

import main.dao.PostRepository;
import main.domain.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class PostRepositPortImpl implements PostRepositoryPort {

    @Autowired
    PostRepository pr;

    @Override
    public Optional <Post> findById(int id) {
        return pr.findById(id);
    }

    @Override
    public List<Post> findAll() {
        Iterable<Post> postIterable = pr.findAll();
        ArrayList<Post> posts = new ArrayList<>();
        for (Post post : postIterable) {
            posts.add(post);
        }
        return posts;
    }

    @Override
    public List<Post> findAllGood() {
        List<Post> posts = findAll();
        ArrayList<Post> goodPosts = new ArrayList<>();
        posts.stream()
                .filter(p -> p.isActive()
                && p.getModerStat().equals(Post.ModerStat.ACCEPTED)
                && p.getTime().isBefore(LocalDateTime.now()))
                .forEach(goodPosts::add);
        return goodPosts;
    }

    @Override
    public List<Post> findByModerStat(String moderStat) {
        return pr.findByModerStat(moderStat);
    }

    @Override
    public List<Post> findByQuery(String query) {
        List<Post> posts = findAllGood();
        if(query == null){
            return posts;
        }
        posts.removeIf(p -> !containsIgnoreCase(p.getText(), query));
        return posts;
    }

    @Override
    public List<Post> findByDate(LocalDate date) {
        List<Post> posts = findAllGood();
        List<Post> finalPosts = new ArrayList<>();
        for (Post post : posts) {
            LocalDate localDate = post.getTime().toLocalDate();
            if(localDate.isEqual(date)){
                finalPosts.add(post);
            }
        }
        return finalPosts;
    }


    @Override
    public int getCount() {
       int x = (int) pr.count();
        return x;
    }

    @Override
    public void savePost(Post post) {
        pr.save(post);
    }

    @Override
    public long countByUser(User user) {
        return pr.countByUser(user);
    }

    @Override
    public long countViewsByUser(User user) {
        return pr.getViewsByUser(user);
    }

    @Override
    public long getFirstPostDate(User user) {
        String str = pr.getFirstPostDateByUser(user);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(str, formatter);
        return dateTime.toEpochSecond(ZoneOffset.UTC);
    }

    private boolean containsIgnoreCase(String src, String what) {
        final int length = what.length();
        if (length == 0)
            return true; // Empty string is contained

        final char firstLo = Character.toLowerCase(what.charAt(0));
        final char firstUp = Character.toUpperCase(what.charAt(0));

        for (int i = src.length() - length; i >= 0; i--) {
            // Quick check before calling the more expensive regionMatches() method:
            final char ch = src.charAt(i);
            if (ch != firstLo && ch != firstUp)
                continue;

            if (src.regionMatches(true, i, what, 0, length))
                return true;
        }
        return false;
    }

}
