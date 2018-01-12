package textEditor.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import textEditor.RMIClient;
import textEditor.model.DatabaseModel;
import textEditor.view.WindowSwitcher;

import java.io.IOException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

public class ActionController implements Initializable, UserInjectionTarget, ClientInjectionTarget, WindowSwitcherInjectionTarget {
    @FXML
    private Button friendsListButton;
    @FXML
    private Button manageProjectsButton;
    @FXML
    private Button closeButton;

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

    public ActionController() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            DatabaseModel databaseModel = (DatabaseModel) client.getModel("DatabaseModel");
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }

        initButtons();
    }

    private void initButtons() {
        initCloseButton();
        initFriendsListButton();
        initManageProjectsButton();
    }

    private void initCloseButton() {
        closeButton.setOnMouseClicked(e -> {
            System.out.println("Close action");
            switcher.getStage().close();
        });
    }

    private void initFriendsListButton() {
        friendsListButton.setOnMouseClicked(e -> {
            System.out.println("Friends action");
            try {
                switcher.loadWindow(WindowSwitcher.Window.FRIENDS_LIST);
            } catch (IOException e1) {
                // error
            }
        });
    }

    private void initManageProjectsButton() {
        manageProjectsButton.setOnMouseClicked(e -> {
            try {
                switcher.loadWindow(WindowSwitcher.Window.PICKPROJECT);
            } catch (IOException e1) {
                // error
            }
        });
    }
}
