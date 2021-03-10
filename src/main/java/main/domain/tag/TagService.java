package main.domain.tag;

import main.dao.PostRepository;
import main.dao.TagRepository;
import main.domain.TagNotFoundException;
import main.domain.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Component
public class TagService {

    private TagRepository tagRepository;
    private PostRepository postRepository;

    public TagService(TagRepository tagRepository, PostRepository postRepository){
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
    }

    public Tag saveTag(String tagName) {
        return tagRepository.findByName(tagName)
                .orElseGet(() -> tagRepository.save(new Tag(tagName.toLowerCase())));
    }

    public Tag findTag(String tagName) {
        return tagRepository.findByName(tagName).orElse(null);
    }

    public Tag saveTag(String tagName, Post post) {
        List<Post> tagPosts;
        Tag tag = tagRepository.findByName(tagName).orElse(null);
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


    public TagResponseDto getTagsWeights(String query) {
        return query == null ? getAllTagsWeights() :
                getSingleTagWeights(query);
    }

    private TagResponseDto getSingleTagWeights(String query) throws TagNotFoundException {
        List <Tag> tags = getQueryTag(query);
        if (tags == null){
            throw new TagNotFoundException("Tag with " + query + " not found");
        }
        TagResponseDto result = new TagResponseDto();
        double postsTotalCount = postRepository.findAllVisibleCount();
        double weight = 0;
        for (Tag tag : tags){
            weight = tag.getGoodPostsAmount()/postsTotalCount;
            result.addTagToList(tag.getName(), weight);
        }
        return result;
    }

    private TagResponseDto getAllTagsWeights() {
        Pageable paged = PageRequest.of(0,20);
        Page<Tag> tags = tagRepository.findPopular20Tags(paged);
        TagResponseDto result = new TagResponseDto();
        double postsTotalCount = postRepository.findAllVisibleCount();
        double weight = 0;
        for (Tag tag : tags){
            weight = tag.getGoodPostsAmount()/postsTotalCount;
            weight=weight*100;
            int res = (int)Math.round(weight);
            weight = (double) res / 100;
            result.addTagToList(tag.getName(), weight);
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
