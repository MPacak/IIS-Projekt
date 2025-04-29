package hr.iisbackend.service;

import hr.iisbackend.iservice.ArticleService;
import hr.iisbackend.model.Article;
import hr.iisbackend.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ArticleServiceImpl implements ArticleService {
    @Autowired
    private ArticleRepository articleRepository;
    @Override
    public Article save(Article article) {
        return articleRepository.save(article);
    }

    @Override
    public List<Article> findAll() {
        return articleRepository.findAll();
    }

    @Override
    public Optional<Article> findById(Long id) {
        return articleRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        articleRepository.deleteById(id);
    }

    @Override
    public Article update(Long id, Article article) {
        articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found"));
        return articleRepository.save(article);
    }
}
