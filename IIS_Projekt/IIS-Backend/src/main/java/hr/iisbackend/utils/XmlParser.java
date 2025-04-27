package hr.iisbackend.utils;

import hr.iisbackend.model.Article;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class XmlParser {
    private XmlParser() {}
    public static Article parseArticle(InputStream xmlInput) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(xmlInput);
        doc.getDocumentElement().normalize();

        Article article = new Article();

        article.setTitle(getTextContent(doc, "title"));
        article.setSubtitle(getTextContent(doc, "subtitle"));
        article.setAuthor(getTextContent(doc, "author"));
        article.setPublishedAt(getTextContent(doc, "published_at"));
        article.setUrl(getTextContent(doc, "url"));

        NodeList tagNodes = doc.getElementsByTagName("tag");
        List<String> tags = new ArrayList<>();
        for (int i = 0; i < tagNodes.getLength(); i++) {
            tags.add(tagNodes.item(i).getTextContent());
        }
        article.setTags(tags);

        return article;
    }

    private static String getTextContent(Document doc, String tagName) {
        NodeList nodes = doc.getElementsByTagName(tagName);
        return nodes.getLength() > 0 ? nodes.item(0).getTextContent() : "";
    }
}
