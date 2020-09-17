package main.domain.tag;

import main.dao.PostRepository;
import main.dao.TagRepository;
import main.domain.post.Post;
import org.springframework.stereotype.Component;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class TagServise {

    private TagRepository tagRepository;
    private PostRepository postRepository;

    public TagServise(TagRepository tagRepository, PostRepository postRepository){
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
    }

    public Tag saveTag(String tagName) {
        Tag tag = tagRepository.findByNameIgnoreCase(tagName);
        return (tag != null) ? tag : tagRepository.save(new Tag(tagName.toLowerCase()));
    }

    public Tag findTag(String tagName) {
        return tagRepository.findByNameIgnoreCase(tagName);
    }

    public Tag saveTag(String tagName, Post post) {
        List<Post> tagPosts;
        Tag tag = tagRepository.findByNameIgnoreCase(tagName);
        if (tag == null){
            tag = new Tag(tagName.toLowerCase());
            tagPosts = new ArrayList<>();
        } else {
            tagPosts  = tag.getPosts();
        }
        tagPosts.add(post);
        tag.setPosts(tagPosts);
        return tagRepository.save(tag);
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
        double postsTotalCount = postRepository.findAllVisibleCount();
        double weight = 0;
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        for (Tag tag : tags){
            weight = tag.getGoodPostsAmount()/postsTotalCount;
            result.put(tag.getName(), df.format(weight));
        }
        return result;
    }


    private List<Tag> getQueryTag (String query){
        if(query == null){
            return null;
        }
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
}
