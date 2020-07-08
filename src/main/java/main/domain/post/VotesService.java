package main.domain.post;


import main.dao.PostVoteRepository;
import main.domain.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class VotesService {
    @Autowired
    private PostVoteRepository votesRepository;

    public Boolean vote(String voteType, User user, Post post) {
        int voteRequested = voteType.equals("like") ? 1 : -1;
        PostVote postVote = votesRepository.findByUserAndPost(user, post);
        if (postVote == null) {
            PostVote newVote = new PostVote();
            newVote.setPost(post);
            newVote.setUser(user);
            newVote.setValue(voteRequested);
            newVote.setTime(LocalDateTime.now());
            votesRepository.save(newVote);
            return true;
        }
        if (voteRequested == postVote.getValue()) {
            return false;
        }
        votesRepository.delete(postVote);
        PostVote newVote = new PostVote();
        newVote.setPost(post);
        newVote.setUser(user);
        newVote.setValue(voteRequested);
        newVote.setTime(LocalDateTime.now());
        votesRepository.save(newVote);
        return true;
    }

    public Integer countByUserAndValue(User user, String voteValue) {
        Integer voteIntValue = voteValue.equals("LIKE") ? 1 : -1;
        return votesRepository.countByUserAndValue(user, voteIntValue);
    }


    public long countByValue(String value) {
        Integer voteIntValue = value.equals("LIKE") ? 1 : -1;
        return votesRepository.countByValue(voteIntValue);
    }
}
