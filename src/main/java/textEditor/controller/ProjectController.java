package textEditor.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;


public class ProjectController implements Initializable, UserInjectionTarget {
    @FXML
    public Button editButton;

    @FXML
    public Label description;

    @FXML
    public Label contributors;

    @FXML
    private Button openButton;

    @FXML
    private Label descriptionLabel;

    @FXML
    private ListView<String> projectList;

    private User user;

    @Override
    public void injectUser(User user) {
        this.user = user;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("user=" + user);
    }
}
