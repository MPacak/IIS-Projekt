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
import hr.iisbackend.soap.SearchResponse;
import hr.iisbackend.utils.XmlParser;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@AllArgsConstructor
@RestController
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
    public SearchResponse searchArticles(@RequestParam String term) {
        SearchRequest request = new SearchRequest();
        request.setSearchTerm(term);
        return searchArticleEndpoint.search(request);
    }
}
