package main.domain.tag;

import main.dao.TagToPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component //Может быть можно обойтись без этого класса, а напрямую использовать репозиторий
public class TagToPostRepositoryPortImpl implements TagToPostRepositoryPort{
    @Autowired
    TagToPostRepository ttpRepository;

    @Override
    public List<TagToPost> findByTagId(Integer tagId) {
        Iterable<TagToPost> all = ttpRepository.findByTagId(tagId);
        return this.revert(all);
    }

    @Override
    public List<TagToPost> findByPostId(Integer postId) {
        Iterable<TagToPost> all = ttpRepository.findByPostId(postId);
        return this.revert(all);
    }

    @Override
    public List<TagToPost> findAll() {
        Iterable<TagToPost> all = ttpRepository.findAll();
        return this.revert(all);
    }

    @Override
    public void save(TagToPost tagToPost) {
        ttpRepository.save(tagToPost);
    }

    private ArrayList<TagToPost> revert (Iterable<TagToPost> tags){
        ArrayList<TagToPost> andAll = new ArrayList<>();
        for (TagToPost tag : tags) {
            andAll.add(tag);
        }
        return andAll;
    }
}
