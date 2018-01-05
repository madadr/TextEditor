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
import textEditor.model.DatabaseModel;
import textEditor.view.WindowSwitcher;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
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
    private DatabaseModel databaseModel;

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
        databaseModel = (DatabaseModel) rmiClient.getModel("DatabaseModel");

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
        try {
            switcher.loadWindow(WindowSwitcher.Window.REGISTER);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onClickSubmit(ActionEvent actionEvent) throws IOException {
        String login = userLoginField.getText();
        String password = userPasswordField.getText();

        if(!login.isEmpty() && !password.isEmpty() && databaseModel.userExist(login))
        {
            if(databaseModel.checkPassword(login, password))
            {
                resultOfAuthorization.setText("Authorization success");
                resultOfAuthorization.setTextFill(Color.web("#2eb82e"));
                resultOfAuthorization.setVisible(true);
                switcher.loadWindow(WindowSwitcher.Window.EDITOR);
            }
            else
            {
                resultOfAuthorization.setText("Password is incorrect");
                resultOfAuthorization.setTextFill(Color.web("#ff3300"));
                resultOfAuthorization.setVisible(true);
            }
        }
        else
        {
            resultOfAuthorization.setText("User don't exist!");
            resultOfAuthorization.setTextFill(Color.web("#ff3300"));
            resultOfAuthorization.setVisible(true);
        }
    }
}
