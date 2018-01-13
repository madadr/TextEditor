package textEditor.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import org.controlsfx.control.CheckComboBox;
import textEditor.RMIClient;
import textEditor.model.DatabaseModel;
import textEditor.view.WindowSwitcher;

import java.io.IOException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class AddProjectController implements Initializable, ClientInjectionTarget, WindowSwitcherInjectionTarget, UserInjectionTarget {
    @FXML
    public TextField projectNameField;
    @FXML
    public CheckComboBox contributorsField;
    @FXML
    public TextArea projectDescriptionField;
    @FXML
    public Label information;


    private RMIClient rmiClient;
    private User user;
    private WindowSwitcher windowSwitcher;

    private DatabaseModel databaseModel;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            databaseModel = (DatabaseModel) rmiClient.getModel("DatabaseModel");
            contributorsField.getItems().add(user);
            contributorsField.getItems().addAll(databaseModel.getFriends(user));
            contributorsField.getCheckModel().check(user);
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void injectClient(RMIClient client) {
        this.rmiClient = client;
    }

    @Override
    public void injectUser(User user) {
        this.user = user;
    }

    @Override
    public void injectWindowSwitcher(WindowSwitcher switcher) {
        this.windowSwitcher = switcher;
    }

    public void addButtonClicked(ActionEvent actionEvent) {
        try {
            List contributors = Arrays.asList(contributorsField.getCheckModel().getCheckedItems().toArray());
            if(!contributors.contains(user))
            {
                setInformation("You can't create a project without participating in it!");
                return;
            }
            if(projectNameField.getText().isEmpty())
            {
                setInformation("You need to fill project name");
                return;
            }
            Project project = new ProjectImpl(0, projectNameField.getText(), projectDescriptionField.getText(), contributors);
            databaseModel.addProject(project);
            windowSwitcher.loadWindow(WindowSwitcher.Window.PICK_PROJECT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setInformation(String text)
    {
        information.setVisible(true);
        information.setTextFill(Color.RED);
        information.setText(text);
    }

    public void cancelButtonClicked(ActionEvent actionEvent) {
        try {
            windowSwitcher.loadWindow(WindowSwitcher.Window.PICK_PROJECT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
