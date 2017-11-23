package textEditor.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import textEditor.view.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    private Button submitLogin;
    @FXML
    private Button registryButton;
    @FXML
    private TextField userLoginField;
    @FXML
    private TextField userPasswordField;
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
    }

    @FXML
    private void onClickRegistry() {

    }

    public void onClickSubmit(ActionEvent actionEvent) throws IOException {
        if (checkLoginAndPassword()) {
            System.out.println("Login correct Password Correct, entering Service");

            Parent parent = FXMLLoader.load(getClass().getResource("Editor.fxml"));
            Scene editorScene = new Scene(parent);

            //Geting primaryStage
            Stage primaryStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            primaryStage.setResizable(true);

            primaryStage.setScene(editorScene);
            primaryStage.show();
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
