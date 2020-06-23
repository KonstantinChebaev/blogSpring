package main.domain.tag;


import lombok.Data;

import javax.persistence.*;
@Data
@Entity
@Table(name="tag2post")
public class TagToPost {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "post_id", nullable = false)
    private int postId;
    @Column(name = "tag_id", nullable = false)
    private int tagId;

    public TagToPost (){

    }
    public TagToPost(int postId,int tagId){
        this.postId = postId;
        this.tagId = tagId;
    }

}
