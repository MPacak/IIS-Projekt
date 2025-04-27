package hr.iisbackend.soap;

import jakarta.xml.bind.annotation.*;

import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "articles"
})
@XmlRootElement(name = "SearchResponse")
public class SearchResponse {

    @XmlElementWrapper(name = "articles")
    @XmlElement(name = "article")
    private List<ArticleSoap> articles;

    public List<ArticleSoap> getArticles() {
        return articles;
    }

    public void setArticles(List<ArticleSoap> articles) {
        this.articles = articles;
    }
}