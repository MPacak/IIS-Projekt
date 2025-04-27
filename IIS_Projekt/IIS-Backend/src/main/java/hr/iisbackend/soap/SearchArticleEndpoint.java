package hr.iisbackend.soap;

import hr.iisbackend.model.ValidationResult;
import hr.iisbackend.service.GenerateArticleXml;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

@Endpoint
public class SearchArticleEndpoint {
    private static final String NAMESPACE_URI = "http://issprojekt/articles";
    private static final String ARTICLEPATH = "articles.xml";
    @Autowired
    private GenerateArticleXml generateArticleXml;
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "SearchRequest")
    @ResponsePayload
    public Object search(@RequestPayload SearchRequest request) {
        String term = request.getSearchTerm();
        List<ArticleSoap> matchingArticles = new ArrayList<>();
        System.out.println("started endpoint");
        try {
            File f = new File(ARTICLEPATH);
            if(!f.exists() || f.length() == 0) {
                generateArticleXml.GenerateXMLfromJson();
            }
            File xsd = new File("validators/articles-soap.xsd");
            ValidationResult validationResult = XmlValidator.validateXmlWithJaxb(f, xsd);

            if (!validationResult.isValid()) {
                return new ValidationErrorResponse(false, validationResult.getErrors());
            }

            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document xmlDocument = builder.parse(f);
            XPath xPath = XPathFactory.newInstance().newXPath();
            String expression = "/SearchResponse/articles/article";
            NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
            JAXBContext jaxbContext = JAXBContext.newInstance(ArticleSoap.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                if (node.getTextContent().toLowerCase().contains(term.toLowerCase())) {
                    ArticleSoap article = (ArticleSoap) unmarshaller.unmarshal(new DOMSource(node));
                    matchingArticles.add(article);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
System.out.println("finished endpoint");
        SearchResponse response = new SearchResponse();
        response.setArticles(matchingArticles);
        return response;
    }
}
