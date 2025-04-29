package hr.iisbackend.iservice;

import hr.iisbackend.model.Article;

import java.util.List;
import java.util.Optional;

public interface ArticleService {
    Article save(Article article);
    List<Article> findAll();
    Optional<Article> findById(Long id);
    void delete(Long id);
    Article update(Long id, Article article);

}
