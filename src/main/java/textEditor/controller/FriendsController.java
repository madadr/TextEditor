package textEditor.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import textEditor.RMIClient;
import textEditor.model.DatabaseModel;
import textEditor.view.WindowSwitcher;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FriendsController implements Initializable, UserInjectionTarget, ClientInjectionTarget, WindowSwitcherInjectionTarget {
    @FXML
    private Button removeButton;
    @FXML
    private TextField nicknameField;
    @FXML
    private Button addButton;
    @FXML
    public Button backButton;

    private WindowSwitcher switcher;
    private DatabaseModel dbService;
    private RMIClient client;
    private UserImpl user;

    @Override
    public void injectUser(UserImpl user) {
        this.user = user;
    }

    @Override
    public void injectWindowSwitcher(WindowSwitcher switcher) {
        this.switcher = switcher;
    }

    @Override
    public void injectClient(RMIClient client) {
        this.client = client;
    }

    public FriendsController() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initBackButton();
    }

    private void initBackButton() {
        backButton.setOnMouseClicked(e -> {
            try {
                switcher.loadWindow(WindowSwitcher.Window.CHOOSE_ACTION);
            } catch (IOException e1) {
                // error
            }
        });
    }
}
