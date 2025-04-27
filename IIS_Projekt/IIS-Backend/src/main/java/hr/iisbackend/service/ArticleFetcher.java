package hr.iisbackend.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ArticleFetcher {
    private ArticleFetcher() {
    }

    public static String fetchArticleJson(String articleId) throws Exception {
        String url = "https://medium2.p.rapidapi.com/article/" + articleId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", "89e7f7c1e4mshaa13892c91a0b75p1b600fjsn9e5cb756f375");
        headers.set("X-RapidAPI-Host", "medium2.p.rapidapi.com");

        HttpEntity<String> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, entity, String.class);
        return response.getBody();

    }
}