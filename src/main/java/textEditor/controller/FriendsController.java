package textEditor.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import textEditor.controller.inject.ClientInjectionTarget;
import textEditor.controller.inject.UserInjectionTarget;
import textEditor.controller.inject.WindowSwitcherInjectionTarget;
import textEditor.model.UserImpl;
import textEditor.model.interfaces.DatabaseModel;
import textEditor.model.interfaces.User;
import textEditor.utils.RMIClient;
import textEditor.view.AlertManager;
import textEditor.view.WindowSwitcher;

import java.io.IOException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.ResourceBundle;

public class FriendsController implements Initializable, UserInjectionTarget, ClientInjectionTarget, WindowSwitcherInjectionTarget {
    @FXML
    public Button backButton;
    @FXML
    private Button removeButton;
    @FXML
    private TextField usernameField;
    @FXML
    private Button addButton;
    @FXML
    private ListView<User> friendsListView;

    private List<User> friends;

    private WindowSwitcher switcher;
    private DatabaseModel dbService;
    private RMIClient client;
    private User user;

    public FriendsController() {

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
        initDatabaseService();
        updateFriendsList();

        initBackButton();
        initAddButton();
        initRemoveButton();
    }

    private void initDatabaseService() {
        try {
            dbService = (DatabaseModel) client.getModel("DatabaseModel");
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }

    private void updateFriendsList() {
        fetchFriendsListFromDatabase();
        setupFriendsListView();
    }

    private void fetchFriendsListFromDatabase() {
        try {
            friends = dbService.getFriends(user);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void setupFriendsListView() {
        friendsListView.setItems(FXCollections.observableArrayList(this.friends));
    }

    private void initBackButton() {
        backButton.setOnMouseClicked(e -> {
            try {
                switcher.loadWindow(WindowSwitcher.Window.CHOOSE_ACTION);
            } catch (IOException e1) {
                // error
            }
        });
    }

    private void initAddButton() {
        addButton.setOnMouseClicked(e -> {
            String friendUsername = usernameField.getText();

            if (friendUsername.equals("")) {
                return;
            }

            try {
                if (this.user.getUsername().equals(friendUsername)) {
                    AlertManager.displayAlert(Alert.AlertType.INFORMATION, "You cannot add yourself to friends list!");
                    return;
                }

                if (dbService.userExist(friendUsername)) {
                    User friend = new UserImpl(dbService.getUserId(friendUsername), friendUsername);

                    if (!friends.contains(friend)) {
                        dbService.addFriend(user, friend);

                        updateFriendsList();
                    } else {
                        AlertManager.displayAlert(Alert.AlertType.INFORMATION, "User is already in your friends list!");
                    }
                } else {
                    AlertManager.displayAlert(Alert.AlertType.WARNING, "User doesn't exist!");
                }
            } catch (RemoteException e1) {
            }
        });
    }

    private void initRemoveButton() {
        removeButton.setOnMouseClicked(e -> {
            try {
                User friend = friendsListView.getSelectionModel().getSelectedItem();

                if (friend == null) {
                    return;
                }

                dbService.removeFriend(user, friend);

                updateFriendsList();
            } catch (RemoteException e1) {
            }
        });
    }
}
