package main.domain;

import main.dao.PostRepository;
import main.domain.post.VotesService;
import main.domain.user.User;
import main.domain.user.UserRepositoryPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Service
public class StatisticServise {

    private PostRepository postRepository;
    private VotesService votesService;
    private UserRepositoryPort userRepositoryPort;

    public StatisticServise( PostRepository postRepository,
                             VotesService votesService,
                             UserRepositoryPort userRepositoryPort){
        this.postRepository = postRepository;
        this.votesService = votesService;
        this.userRepositoryPort = userRepositoryPort;
    }

    public ResponseEntity<StatisticsDto> getStatistics(String statType, String userEmail, boolean statIsPublic) {
        User user = null;
        if(statType.equals("my")){
            user = userRepositoryPort.findByEmail(userEmail);
            if(!statIsPublic&&!user.isModerator()){
                return new ResponseEntity<StatisticsDto>(HttpStatus.FORBIDDEN);
            }
        }
        StatisticsDto statisticsDto = new StatisticsDto();
        statisticsDto.setPostsCount(postRepository.countByUser(user));
        statisticsDto.setLikesCount(votesService.countByUserAndValue(user, "LIKE"));
        statisticsDto.setDislikesCount(votesService.countByUserAndValue(user, "DISLIKE"));
        statisticsDto.setViewsCount(postRepository.getViewsByUser(user));

        String str = postRepository.getFirstPostDateByUser(user);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(str, formatter);
        statisticsDto.setFirstPublication(dateTime.toEpochSecond(ZoneOffset.UTC));
        return new ResponseEntity<StatisticsDto>(statisticsDto, HttpStatus.OK);
    }
}
