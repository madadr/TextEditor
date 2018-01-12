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
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

public class LoginController implements Initializable, ClientInjectionTarget, WindowSwitcherInjectionTarget, UserInjectionTarget {
    @FXML
    private Button submitLogin, registrationButton;
    @FXML
    private Label resultOfAuthorization;
    @FXML
    private TextField userLoginField;
    @FXML
    private TextField userPasswordField;

    private RMIClient rmiClient;

    private WindowSwitcher switcher;
    private DatabaseModel databaseModel;
    private UserImpl user;

    public LoginController() {
    }

    @Override
    public void injectClient(RMIClient client) {
        this.rmiClient = client;
    }

    @Override
    public void injectUser(UserImpl user) {
        this.user = user;
    }

    @Override
    public void injectWindowSwitcher(WindowSwitcher switcher) {
        this.switcher = switcher;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        resultOfAuthorization.setVisible(false);
        try {
            databaseModel = (DatabaseModel) rmiClient.getModel("DatabaseModel");
        } catch (RemoteException | NotBoundException e) {
            setNotConnected();
        }

        runEnableKeyEventHandler();
    }

    private void setNotConnected() {
        setResultText("Brak połączenia z bazą danych", false);
        submitLogin.setDisable(true);
        registrationButton.setDisable(true);
        userLoginField.setDisable(true);
        userPasswordField.setDisable(true);
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

        if (!login.isEmpty() && !password.isEmpty() && databaseModel.userExist(login)) {
            if (databaseModel.checkPassword(login, password)) {
                int userId = databaseModel.getUserId(login);
                this.user.setUsername(login);
                this.user.setId(userId);
                setResultText("Authorization success", true);
                switcher.loadWindow(WindowSwitcher.Window.PICK_PROJECT);
            } else {
                setResultText("Password is incorrect", false);
            }
        } else {
            setResultText("User don't exist!", false);
        }
    }

    public void setResultText(String resultText, boolean isValid) {
        resultOfAuthorization.setText(resultText);
        if(isValid)
        {
            resultOfAuthorization.setTextFill(Color.web("#2eb82e"));
        }
        else
        {
            resultOfAuthorization.setTextFill(Color.web("#ff3300"));
        }
        resultOfAuthorization.setVisible(true);
    }
}
