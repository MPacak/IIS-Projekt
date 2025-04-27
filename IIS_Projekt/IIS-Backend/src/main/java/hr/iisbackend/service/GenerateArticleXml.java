package hr.iisbackend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import hr.iisbackend.soap.ArticleSoap;
import hr.iisbackend.soap.SearchResponse;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
public class GenerateArticleXml {
    private ObjectMapper mapper;

    public void GenerateXMLfromJson() {
        List<String> articleIds = Arrays.asList(
                "d274dbdedd2e",
                "eeead1cc591a",
                "b22dc4682072"
        );
        List<ArticleSoap> articles = new ArrayList<>();
        for (String articleId : articleIds) {
            try {
                String json = ArticleFetcher.fetchArticleJson(articleId);
                ArticleSoap article = mapper.readValue(json, ArticleSoap.class);
                articles.add(article);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        saveArticlesAsXml(articles, "articles.xml");
    }

    private void saveArticlesAsXml(List<ArticleSoap> articles, String filename) {
        try {
            SearchResponse searchResponse = new SearchResponse();
            searchResponse.setArticles(articles);
            JAXBContext context = JAXBContext.newInstance(SearchResponse.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            File file = new File(filename);
            marshaller.marshal(searchResponse, file);
        } catch (Exception e) {
            System.out.println("Error: " + filename);
        }

    }
}
