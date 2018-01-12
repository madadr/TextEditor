package textEditor.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Popup;
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
        fetchFriendsListFromDatabase();
        setupFriendsListView();

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
                String username = usernameField.getText();

                if(dbService.userExist(username)) {
                    User friend = new UserImpl(dbService.getUserId(username), username);
                    dbService.addFriend(user, friend);

                    fetchFriendsListFromDatabase();
                    setupFriendsListView();
                } else {
                    // TODO: user doesn't exists
                    Popup popup = new Popup();
                    popup.show(switcher.getStage());
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

                fetchFriendsListFromDatabase();
                setupFriendsListView();
            } catch (RemoteException e1) {
                // TODO
            }
        });
    }
}
