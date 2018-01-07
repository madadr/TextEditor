package textEditor.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import textEditor.RMIClient;
import textEditor.model.DatabaseModel;
import textEditor.view.WindowSwitcher;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController implements Initializable, ClientInjectionTarget, WindowSwitcherInjectionTarget {

    @FXML
    public Button submitRegister;

    @FXML
    public PasswordField userPasswordField;

    @FXML
    public TextField userLoginField, emailField, zipCodeField, addressField, regionField, lastNameField, firstNameField;

    @FXML
    public Label informationLabel;

    private RMIClient client;
    private WindowSwitcher switcher;

    private DatabaseModel databaseModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        databaseModel = (DatabaseModel) client.getModel("DatabaseModel");
    }

    @Override
    public void injectClient(RMIClient client) {
        this.client = client;
    }

    @Override
    public void injectWindowSwitcher(WindowSwitcher switcher) {
        this.switcher = switcher;
    }

    @FXML
    public void onClickRegister(ActionEvent actionEvent) {
        String login = userLoginField.getText();
        String password = userPasswordField.getText();
        String email = emailField.getText();
        String zipCode = zipCodeField.getText();
        String address = addressField.getText();
        String region = regionField.getText();
        String lastName = lastNameField.getText();
        String firstName = firstNameField.getText();

        //If we fill required fields
        if (!login.isEmpty() && !password.isEmpty() && !email.isEmpty()) {
            try {
                if (!databaseModel.userExist(login)) {
                    if (login.length() < 30 && password.length() < 50 && firstName.length() < 30 && lastName.length() < 40 &&
                            email.length() < 50 && address.length() < 50 && region.length() < 50 && zipCode.length() < 6) {
                        databaseModel.registerUser(login, password, email, zipCode, address, region, lastName, firstName);
                        informationLabel.setTextFill(Color.GREEN);
                        informationLabel.setText("Registration was successful");
                        switcher.loadWindow(WindowSwitcher.Window.LOGIN);
                    } else {
                        informationLabel.setTextFill(Color.RED);
                        informationLabel.setText("The length of data is to big!");
                    }
                } else {
                    informationLabel.setTextFill(Color.RED);
                    informationLabel.setText("This username is already used");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            informationLabel.setTextFill(Color.RED);
            informationLabel.setText("Fill all required fields");
        }
    }
}
