package main.domain.tag;

import java.util.List;

public class Tags {
    private List<Tag> tags;

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public Tags() {
    }

    public Tags(List<Tag> tags) {
        this.tags = tags;
    }
}