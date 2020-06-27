package main.domain.tag;

import main.dao.TagRepository;
import main.domain.post.PostRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class TagUseCase {
    @Autowired
    TagRepository tagRepository;

    @Autowired
    PostRepositoryPort postRepositoryPort;

    public List<Tag> getQueryTag (String query){
        ArrayList <Tag> tags = new ArrayList<>();
        Iterable<Tag> tagIterable = tagRepository.findAll();
        for (Tag tag : tagIterable) {
            if(tag.getName().toLowerCase().contains(query)){
                tags.add(tag);
            }
        }
        if(tags.isEmpty()){
            return null;
        }
        return tags;
    }

    public Tag saveTag(String tagName) {
        Tag tag = tagRepository.findByNameIgnoreCase(tagName);
        return (tag != null) ? tag : tagRepository.save(new Tag(tagName.toLowerCase()));
    }


    public HashMap <String, Object> getTagsWeights(String query) {
        List <Tag> tags;
        HashMap <String, Object> result = new HashMap<>();
        if(query == null){
            tags = new ArrayList<>();
           for (Tag tag : tagRepository.findAll()){
               tags.add(tag);
           }
        } else {
            tags = getQueryTag(query);
            if (tags == null){
                result.put("errors", "Tag with "+query+" not found");
                return result;
            }
        }
        int postsTotalCount = postRepositoryPort.findAllGood().size();
        double weight = 0;
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        for (Tag tag : tags){
            weight = tag.getPostsAmount()/postsTotalCount;
            result.put(tag.getName(), df.format(weight));
        }
        return result;
    }
}
