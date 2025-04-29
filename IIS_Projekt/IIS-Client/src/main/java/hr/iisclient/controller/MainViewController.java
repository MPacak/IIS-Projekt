package hr.iisclient.controller;


import hr.iisclient.jwt.ArticleRestPane;
import hr.iisclient.jwt.AuthDialog;
import hr.iisclient.jwt.AuthenticationService;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {
    @FXML
    private TabPane mainTabPane;

    @FXML
    private Tab articlesTab;

    @FXML
    private AnchorPane articlesPane;

    private boolean articlesLoaded = false;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
    @FXML
    private void onArticlesTabSelectionChanged(Event event) {
        if (!articlesTab.isSelected() || articlesLoaded) {
            return;
        }

        AuthenticationService authService = AuthenticationService.getInstance();
        boolean loggedIn;
        if (!authService.isAuthenticated()) {
            loggedIn = AuthDialog.showAndLogin();
        } else {
            loggedIn = true;
        }

        if (loggedIn) {
            loadArticlesUI();
            articlesLoaded = true;
        } else {
            // User cancelled or login failed; return to first tab
            mainTabPane.getSelectionModel().selectFirst();
        }
    }

    private void loadArticlesUI() {
        articlesPane.getChildren().clear();
        ArticleRestPane articleRestPane = new ArticleRestPane();
        AnchorPane.setTopAnchor(articleRestPane, 0.0);
        AnchorPane.setBottomAnchor(articleRestPane, 0.0);
        AnchorPane.setLeftAnchor(articleRestPane, 0.0);
        AnchorPane.setRightAnchor(articleRestPane, 0.0);
        articlesPane.getChildren().add(articleRestPane);
    }
}
