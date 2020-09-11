package main.domain;

import main.domain.post.PostRepositoryPort;
import main.domain.post.VotesService;
import main.domain.user.User;
import main.domain.user.UserRepositoryPort;
import main.domain.user.UserServise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class StatisticServise {

    private PostRepositoryPort postRepositoryPort;
    private VotesService votesService;
    private UserRepositoryPort userRepositoryPort;

    public StatisticServise( PostRepositoryPort postRepositoryPort,
                             VotesService votesService,
                             UserRepositoryPort userRepositoryPort){
        this.postRepositoryPort = postRepositoryPort;
        this.votesService = votesService;
        this.userRepositoryPort = userRepositoryPort;
    }

    public StatisticsDto getStatistics(String statType, String userEmail) {
        User user = null;
        if(statType.equals("my")){
            user = userRepositoryPort.findByEmail(userEmail);
        }
        StatisticsDto statisticsDto = new StatisticsDto();
        statisticsDto.setPostsCount(postRepositoryPort.countByUser(user));
        statisticsDto.setLikesCount(votesService.countByUserAndValue(user, "LIKE"));
        statisticsDto.setDislikesCount(votesService.countByUserAndValue(user, "DISLIKE"));
        statisticsDto.setViewsCount(postRepositoryPort.countViewsByUser(user));
        statisticsDto.setFirstPublication(postRepositoryPort.getFirstPostDate(user));
        return statisticsDto;
    }
}
