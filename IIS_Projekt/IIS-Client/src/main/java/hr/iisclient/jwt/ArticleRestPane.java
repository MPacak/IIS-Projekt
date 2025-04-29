package hr.iisclient.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import hr.iisclient.model.Article;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class ArticleRestPane extends VBox {
    private final TableView<Article> table = new TableView<>();
    private final AuthenticationService authService = AuthenticationService.getInstance();
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ArticleRestPane() {
        getChildren().add(table);
        setupTableColumns();
        loadArticles();
    }

    private void setupTableColumns() {
        table.getColumns().addAll(
                createColumn("ID", "id", 50),
                createColumn("Title", "title", 150),
                createColumn("Subtitle", "subtitle", 150),
                createColumn("Author", "author", 100),
                createColumn("Published At", "publishedAt", 120),
                createColumn("URL", "url", 200)
        );
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private <T> TableColumn<Article, T> createColumn(String title, String propName, int minWidth) {
        TableColumn<Article, T> col = new TableColumn<>(title);
        col.setMinWidth(minWidth);
        col.setCellValueFactory(new PropertyValueFactory<>(propName));
        return col;
    }

    private void loadArticles() {
        try {
            HttpRequest.Builder reqBuilder = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/articles"))
                    .GET();
            authService.attachAuthHeader(reqBuilder);
            HttpResponse<String> response = httpClient.send(
                    reqBuilder.build(), HttpResponse.BodyHandlers.ofString()
            );
            if (response.statusCode() == 200) {
                List<Article> list = objectMapper.readValue(
                        response.body(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, Article.class)
                );
                table.setItems(FXCollections.observableArrayList(list));
            } else {
                throw new RuntimeException("Failed to load: " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error loading articles: " + e.getMessage()).showAndWait();
        }
    }
}
