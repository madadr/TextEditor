package textEditor.controller;

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
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.regex.Pattern;

import static textEditor.controller.RegistrationFields.*;

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
    private ArrayList<String> entryForm;
    private DatabaseModel databaseModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            databaseModel = (DatabaseModel) client.getModel("DatabaseModel");
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
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
    public void onClickRegister() {
        entryForm = new ArrayList<>(Arrays.asList(userLoginField.getText(), userPasswordField.getText(), emailField.getText(), zipCodeField.getText(), addressField.getText(),
                regionField.getText(), lastNameField.getText(), firstNameField.getText()));

        if (checkRequiredField() && checkAdditionalFields(entryForm.get(ZIPCODE).isEmpty())) {
            try {
                if (!databaseModel.userExist(entryForm.get(USER_LOGIN))) {
                    databaseModel.registerUser(entryForm);
                    informationLabel.setTextFill(Color.GREEN);
                    informationLabel.setText("Registration was successful");
                    switcher.loadWindow(WindowSwitcher.Window.LOGIN);
                } else {
                    informationLabel.setTextFill(Color.RED);
                    informationLabel.setText("User exist!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            informationLabel.setTextFill(Color.RED);
            informationLabel.setText("Some fields was not fill correctly");
        }
    }

    private boolean checkRequiredField() {
        String matchEmail = "\\b[a-zA-Z0-9]{2,}\\b@\\b[a-zA-Z0-9]{2,}\\b\\.\\b[a-zA-Z0-9]{2,}\\b";
        String matchLogin = "\\A[a-zA-Z0-9]{1,30}\\Z";
        String matchPassword = "\\A[a-zA-Z0-9]{1,50}\\Z";

        Pattern emailPattern = Pattern.compile(matchEmail);
        Pattern loginPattern = Pattern.compile(matchLogin);
        Pattern passwordPattern = Pattern.compile(matchPassword);

        boolean isLoginFilled = loginPattern.matcher(entryForm.get(USER_LOGIN)).matches();
        boolean isPasswordFilled = passwordPattern.matcher(entryForm.get(USER_PASSWORD)).matches();
        boolean isEmailMeetRequirement = emailPattern.matcher(entryForm.get(EMAIL)).matches();

        return isLoginFilled && isPasswordFilled && isEmailMeetRequirement;
    }

    private boolean checkAdditionalFields(boolean isZipCodeEmpty) {
        String matchZipCode = "\\A[0-9]{2}-[0-9]{3}\\Z";
        String matchRegion = "\\A[a-zA-Z\\p{L}]{0,50}\\Z";

        Pattern zipCodePattern = Pattern.compile(matchZipCode);
        Pattern regionPattern = Pattern.compile(matchRegion);
        boolean isZipCodeCorrect = true, isRegionCorrect;

        if (!isZipCodeEmpty) {
            isZipCodeCorrect = zipCodePattern.matcher(entryForm.get(ZIPCODE)).matches();
        }
        isRegionCorrect = regionPattern.matcher(entryForm.get(REGION)).matches();

        return isZipCodeCorrect && isRegionCorrect;
    }
}
