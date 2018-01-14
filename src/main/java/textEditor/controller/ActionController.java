package textEditor.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import textEditor.controller.inject.ClientInjectionTarget;
import textEditor.controller.inject.UserInjectionTarget;
import textEditor.controller.inject.WindowSwitcherInjectionTarget;
import textEditor.model.interfaces.User;
import textEditor.utils.RMIClient;
import textEditor.view.WindowSwitcher;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

public class ActionController implements Initializable, UserInjectionTarget, ClientInjectionTarget, WindowSwitcherInjectionTarget {
    @FXML
    public Label hello;
    @FXML
    private Button friendsListButton;
    @FXML
    private Button manageProjectsButton;
    @FXML
    private Button closeButton;

    private WindowSwitcher switcher;
    private RMIClient client;
    private User user;

    public ActionController() {

    }

    @Override
    public void injectUser(User user) {
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initButtons();
        initHelloLabel();
    }

    private void initHelloLabel() {
        try {
            hello.setText("Hello, " + user.getUsername() + "!");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void initButtons() {
        initCloseButton();
        initFriendsListButton();
        initManageProjectsButton();
    }

    private void initCloseButton() {
        closeButton.setOnMouseClicked(e -> {
            switcher.getMainStage().close();
        });
    }

    private void initFriendsListButton() {
        friendsListButton.setOnMouseClicked(e -> {
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
                switcher.loadWindow(WindowSwitcher.Window.PICK_PROJECT);
            } catch (IOException e1) {
                // error
            }
        });
    }
}
