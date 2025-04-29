package hr.iisclient.jwt;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
public class AuthenticationService {
    private static final AuthenticationService INSTANCE = new AuthenticationService();
    private String accessToken;
    private String refreshToken;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private AuthenticationService() {}
    public static AuthenticationService getInstance() { return INSTANCE; }

    /**
     * Perform login against the backend /api/auth/login endpoint.
     * Throws AuthException on failure.
     */
    public void login(String username, String password) throws AuthException {
        try {
            String requestBody = objectMapper.writeValueAsString(
                    new Pair<>(username, password)
            );
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/auth/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                // parse JSON { accessToken, refreshToken }
                var node = objectMapper.readTree(response.body());
                this.accessToken = node.get("accessToken").asText();
                this.refreshToken = node.get("refreshToken").asText();
            } else {
                throw new AuthException("Login failed: " + response.body());
            }
        } catch (IOException | InterruptedException e) {
            throw new AuthException("Login error", e);
        }
    }

    public boolean isAuthenticated() {
        return accessToken != null;
    }

    /**
     * Attach the Bearer auth header to the given request builder.
     */
    public void attachAuthHeader(HttpRequest.Builder builder) {
        builder.header("Authorization", "Bearer " + accessToken);
    }

    public String getAccessToken() {
        return accessToken;
    }

    // Optional: add refresh logic using refreshToken
    public static class AuthException extends Exception {
        public AuthException(String msg) { super(msg); }
        public AuthException(String msg, Throwable cause) { super(msg, cause); }
    }
}
