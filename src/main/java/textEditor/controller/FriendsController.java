package textEditor.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import textEditor.RMIClient;
import textEditor.model.DatabaseModel;
import textEditor.view.WindowSwitcher;

import java.io.IOException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.ResourceBundle;

public class FriendsController implements Initializable, UserInjectionTarget, ClientInjectionTarget, WindowSwitcherInjectionTarget {
    @FXML
    private Button removeButton;
    @FXML
    private TextField usernameField;
    @FXML
    private Button addButton;
    @FXML
    public Button backButton;
    @FXML
    private ListView<User> friendsListView;

    private List<User> friends;

    private WindowSwitcher switcher;
    private DatabaseModel dbService;
    private RMIClient client;
    private User user;

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

    public FriendsController() {

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
        // get all projects data from database
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
            try {
                String friendUsername = usernameField.getText();

                if(this.user.getUsername().equals(friendUsername)) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Already in friends list");
                    alert.setContentText("You cannot add yourself to friends list!");

                    alert.showAndWait();
                }

                if (dbService.userExist(friendUsername)) {
                    User friend = new UserImpl(dbService.getUserId(friendUsername), friendUsername);

                    if (!friends.contains(friend)) {
                        dbService.addFriend(user, friend);

                        updateFriendsList();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Already in friends list");
                        alert.setHeaderText("Already in friends list");
                        alert.setContentText("User is already in your friends list!");

                        alert.showAndWait();
                    }
                } else {
                    // TODO: user doesn't exists
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Invalid user");
                    alert.setHeaderText("Invalid user");
                    alert.setContentText("User doesn't exists!");

                    alert.showAndWait();
                }
            } catch (RemoteException e1) {
                // TODO
            }
        });
    }

    private void initRemoveButton() {
        removeButton.setOnMouseClicked(e -> {
            try {
                User friend = friendsListView.getSelectionModel().getSelectedItem();

                dbService.removeFriend(user, friend);

                updateFriendsList();
            } catch (RemoteException e1) {
                // TODO
            }
        });
    }
}
