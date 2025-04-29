package hr.iisbackend.controller;

import hr.iisbackend.iservice.ArticleService;
import hr.iisbackend.model.Article;
import hr.iisbackend.model.ValidationResult;
import hr.iisbackend.service.ArticleFetcher;
import hr.iisbackend.service.GenerateArticleXml;
import hr.iisbackend.service.RngValidatorService;
import hr.iisbackend.service.XSDValidatorService;
import hr.iisbackend.soap.SearchArticleEndpoint;
import hr.iisbackend.soap.SearchRequest;
import hr.iisbackend.utils.XmlParser;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@AllArgsConstructor
@RestController
@RequestMapping("/api/articles")
public class ArticleController {
    private RngValidatorService rngValidatorService;
    private XSDValidatorService xsdValidator;
    private ArticleService articleService;
    private GenerateArticleXml generateArticleXml;

    private final SearchArticleEndpoint searchArticleEndpoint;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadXml(@RequestParam("file") MultipartFile file,
                                       @RequestParam("type") String validationType) {
        ValidationResult result = null;

        if ("xsd".equalsIgnoreCase(validationType)) {
            result = xsdValidator.validateWithXSD(file, "validators/article.xsd");
        } else if ("rng".equalsIgnoreCase(validationType)) {
            result = rngValidatorService.validateWithRNG(file, "validators/article.rng");
        }
        if(result == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There was no results, please try again");
        }
        else if (!result.isValid()) {
            List<String> formattedErrors = result.getErrors()
                    .stream()
                    .map(Object::toString)
                    .toList();
            return ResponseEntity.badRequest().body(formattedErrors);
        } else {
            try {
                Article article = XmlParser.parseArticle(file.getInputStream());
                articleService.save(article);
                return ResponseEntity.ok("XML is valid and saved.");
            } catch (Exception e) {
                return ResponseEntity.internalServerError().body("Parsing error: " + e.getMessage());
            }
        }
    }
    @PostMapping("/validate/rng")
    public ResponseEntity<?> validateRngFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(" File is empty or not received.");
        }

        ValidationResult result = rngValidatorService.validateWithRNG(file, "validators/article.rng");

        if (result.isValid()) {
            return ResponseEntity.ok("XML is valid!");
        } else {
            List<String> formattedErrors = result.getErrors()
                    .stream()
                    .map(Object::toString)
                    .toList();

            return ResponseEntity.badRequest().body(formattedErrors);
        }
    }

    @PostMapping("/validate/xsd")
    public ResponseEntity<?> validateXsdFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty or not received.");
        }
        ValidationResult result = xsdValidator.validateWithXSD(file, "validators/article.xsd");

        if (result.isValid()) {
            return ResponseEntity.ok("XML is valid!");
        } else {
            List<String> formattedErrors = result.getErrors()
                    .stream()
                    .map(Object::toString)
                    .toList();

            return ResponseEntity.badRequest().body(formattedErrors);
        }
    }
    @GetMapping("/fetcher")
    public ResponseEntity<String> getArticleJson() {
        try {
            String json = ArticleFetcher.fetchArticleJson("1c560214fbac");
            return ResponseEntity.ok(json);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch article: " + e.getMessage());
        }
    }
    @GetMapping("/generate")
    public String generateXml() {
        generateArticleXml.GenerateXMLfromJson();
        return "XML generated successfully!";
    }
    @GetMapping("/search")
    public Object searchArticles(@RequestParam String term) {
        SearchRequest request = new SearchRequest();
        request.setSearchTerm(term);
        return searchArticleEndpoint.search(request);
    }
    @GetMapping("/get/all")
    public List<Article> getAllArticles() {
        return articleService.findAll();
    }

    // GET by ID
    @GetMapping("/get/{id}")
    public ResponseEntity<Article> getArticleById(@PathVariable Long id) {
        Article article = articleService.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found"));
        return ResponseEntity.ok(article);
    }

    // POST create new article
    @PostMapping("/create")
    public ResponseEntity<Article> createArticle(@RequestBody Article article) {
        Article created = articleService.save(article);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT update existing article
    @PutMapping("/update/{id}")
    public ResponseEntity<Article> updateArticle(
            @PathVariable Long id,
            @RequestBody Article article) {
        Article updated = articleService.update(id, article);
        return ResponseEntity.ok(updated);
    }

    // DELETE remove article
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        articleService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
