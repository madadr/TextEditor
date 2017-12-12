package textEditor.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import textEditor.RMIClient;
import textEditor.view.WindowSwitcher;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable, ClientInjectionTarget, WindowSwitcherInjectionTarget {
    @FXML
    private Button submitLogin, registrationLabel;
    @FXML
    private Label resultOfAuthorization;
    @FXML
    private TextField userLoginField;
    @FXML
    private TextField userPasswordField;

    private RMIClient rmiClient;

    private WindowSwitcher switcher;

    public LoginController() {
    }

    @Override
    public void injectClient(RMIClient client) {
        this.rmiClient = client;
    }

    @Override
    public void injectWindowSwitcher(WindowSwitcher switcher) {
        this.switcher = switcher;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        resultOfAuthorization.setVisible(false);
        runEnableKeyEventHandler();
    }

    private void runEnableKeyEventHandler() {
        Platform.runLater(() -> {
            while (this.switcher.getStage().getScene() == null) {

            }
            enableKeyEventHandler();
        });
    }

    private void enableKeyEventHandler() {
        this.switcher.getStage().getScene().setOnKeyPressed((keyEvent) -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                try {
                    onClickSubmit(null);
                } catch (IOException ignored) {

                }
                keyEvent.consume();
            }
        });
    }

    @FXML
    private void onClickRegistry() {

    }

    public void onClickSubmit(ActionEvent actionEvent) throws IOException {
        if (checkLoginAndPassword()) {
            System.out.println("Login correct Password Correct, entering Service");
            resultOfAuthorization.setText("Authorization success");
            resultOfAuthorization.setTextFill(Color.web("#2eb82e"));
            resultOfAuthorization.setVisible(true);

            switcher.loadEditorWindow();
        } else {
            System.out.println("Authorization failed");
            resultOfAuthorization.setText("Authorization failed");
            resultOfAuthorization.setTextFill(Color.web("#ff3300"));
            resultOfAuthorization.setVisible(true);
        }
    }

    private boolean checkLoginAndPassword() {
        if (userLoginField.getText().isEmpty() || userPasswordField.getText().isEmpty()) {
            System.out.println("Login or Password werent typed");
            return false;
        } else if (userLoginField.getText().equals("admin") && userPasswordField.getText().equals("admin")) {
            System.out.println("Correct Login and Password");
            return true;
        }
        System.out.println("Wrong Login or Password");
        return false;
    }
}
