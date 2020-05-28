package main.domain.tag;

import main.dao.TagRepository;
import main.domain.post.PostRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TagUseCase {
    @Autowired
    PostRepositoryPort postRP;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    TagToPostRepositoryPort tagToPostRP;


    //В случае с этим методом можно пойти двумя путями
    //1 выгрузить все записи TagToPost и работать с ними здесь (реализовано)
    //2 за количеством постов по каждому тегу обращаться с запросом в БД
    //Открытый вопрос что быстрее
    public List<Tags> getQueryTags (String query){
        ArrayList <Tags> tags = new ArrayList<>();
        ArrayList<TagToPost> allTagToPosts = (ArrayList<TagToPost>) tagToPostRP.findAll();
        double postsCount = postRP.getCount();
        Iterable<Tag> tagIterable = tagRepository.findAll();
        double count= 0;
        for (Tag tag : tagIterable) {
            if(tag.getName().contains(query)||query.equals("")){
                for(TagToPost ttp : allTagToPosts){
                    if(ttp.getTagId()==tag.getId()){
                        count++;
                    }
                }
                tags.add(new Tags(tag.getName(),count/postsCount));
                count = 0;
            }
        }
        return tags;
    }



}
