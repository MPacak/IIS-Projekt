package hr.iisbackend.soap;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@XmlRootElement(name = "article")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "id",
        "title",
        "subtitle",
        "author",
        "publicationId",
        "publishedAt",
        "lastModifiedAt",
        "boostedAt",
        "tags",
        "topics",
        "claps",
        "voters",
        "wordCount",
        "responsesCount",
        "readingTime",
        "url",
        "uniqueSlug",
        "imageUrl",
        "lang",
        "isSeries",
        "isLocked",
        "isShortform",
        "topHighlight"
})
@Getter
@Setter
public class ArticleSoap {
    private String id;
    private String title;
    private String subtitle;
    private String author;

    @XmlElement(name = "publication_id")
    private String publicationId;

    @XmlElement(name = "published_at")
    private String publishedAt;

    @XmlElement(name = "last_modified_at")
    private String lastModifiedAt;

    @XmlElement(name = "boosted_at")
    private String boostedAt;

    @XmlElementWrapper(name = "tags")
    @XmlElement(name = "tag")
    private List<String> tags;

    @XmlElementWrapper(name = "topics")
    @XmlElement(name = "topic")
    private List<String> topics;

    private int claps;
    private int voters;

    @XmlElement(name = "word_count")
    private int wordCount;

    @XmlElement(name = "responses_count")
    private int responsesCount;

    @XmlElement(name = "reading_time")
    private float readingTime;

    private String url;

    @XmlElement(name = "unique_slug")
    private String uniqueSlug;

    @XmlElement(name = "image_url")
    private String imageUrl;

    private String lang;

    @XmlElement(name = "is_series")
    private boolean isSeries;

    @XmlElement(name = "is_locked")
    private boolean isLocked;

    @XmlElement(name = "is_shortform")
    private boolean isShortform;

    @XmlElement(name = "top_highlight")
    private String topHighlight;

}
