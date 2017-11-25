package textEditor.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import textEditor.Client;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable, ClientInjectionTarget {
    @FXML
    private Button submitLogin, registrationLabel;
    @FXML
    private Label resultOfAuthorization;
    @FXML
    private TextField userLoginField;
    @FXML
    private TextField userPasswordField;

    private Client.RMIClient rmiClient;

    public LoginController() {
    }

    @Override
    public void injectClient(Client.RMIClient client) {
        this.rmiClient = client;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        resultOfAuthorization.setVisible(false);

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
            Parent parent = FXMLLoader.load(getClass().getResource("Editor.fxml"));
            Scene editorScene = new Scene(parent);

            //Geting primaryStage
            Stage primaryStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            primaryStage.setResizable(true);

            primaryStage.setScene(editorScene);
            //primaryStage.show();
        }
        else{
            System.out.println("Authorization failed");
            resultOfAuthorization.setText("Authorization failed");
            resultOfAuthorization.setTextFill(Color.web("#ff3300"));
            resultOfAuthorization.setVisible(true);
        }
    }
    private boolean checkLoginAndPassword()
    {
        if(userLoginField.getText().isEmpty() || userPasswordField.getText().isEmpty())
        {
            System.out.println("Login or Password werent typed");
            return false;
        }
        else if(userLoginField.getText().equals("admin") && userPasswordField.getText().equals("admin"))
        {
            System.out.println("Correct Login and Password");
            return true;
        }
        System.out.println("Wrong Login or Password");
        return false;
    }
}
