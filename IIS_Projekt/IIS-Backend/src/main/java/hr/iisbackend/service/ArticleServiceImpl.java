package hr.iisbackend.service;

import hr.iisbackend.iservice.ArticleService;
import hr.iisbackend.model.Article;
import hr.iisbackend.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArticleServiceImpl implements ArticleService {
    @Autowired
    private ArticleRepository articleRepository;
    @Override
    public Article save(Article article) {
        return articleRepository.save(article);
    }
}
