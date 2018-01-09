package textEditor.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import textEditor.RMIClient;
import textEditor.controller.projectManagerPopups.ProjectPopupViewFactory;
import textEditor.model.DatabaseModel;
import textEditor.view.WindowSwitcher;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


public class ProjectController implements Initializable, UserInjectionTarget, ClientInjectionTarget, WindowSwitcherInjectionTarget {
    @FXML
    private Label description;

    @FXML
    private Label contributors;

    @FXML
    private ListView<String> projectListView;

    @FXML
    private Button newButton;

    @FXML
    private Button editButton;

    @FXML
    private Button removeButton;

    @FXML
    private Button openButton;

    @FXML
    private Button importButton;

    @FXML
    private Button exportButton;

    private WindowSwitcher switcher;
    private DatabaseModel dbService;
    private RMIClient client;
    private UserImpl user;

    private List<Project> projects;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("user=" + user);

        // get database service object
        System.out.println("ProjectController getting DatabaseModel");
        dbService = (DatabaseModel) client.getModel("DatabaseModel");
        System.out.println("ProjectController got DatabaseModel");

        if (dbService == null) {
            System.out.println("null");
        } else {
            System.out.println(dbService);
        }

        // get all projects data from database
        try {
            projects = dbService.getProjects(user);
        } catch (RemoteException e) {
            e.printStackTrace(); // todo handle and do sth
        }

        setupProjectsListView();

        description.setText("This project is about...");
        contributors.setText("John, Anna, Mike");

        initButtonsActions();
    }

    private void setupProjectsListView() {
        System.out.println("generating");
        ObservableList<String> items = generateObservableProjectTitleList(this.projects);
        System.out.println("generated");
        projectListView.setItems(items);
    }

    private ObservableList<String> generateObservableProjectTitleList(List<Project> projects) {
        List<String> list = projects.stream()
                .map(project -> project.getTitle())
                .collect(Collectors.toList());

        return FXCollections.observableArrayList(list);
    }

    private void initButtonsActions() {
        newButton.setOnAction(event -> {
            final Stage popup = ProjectPopupViewFactory.createNewProjectView();

            popup.initModality(Modality.APPLICATION_MODAL);
            popup.show();
        });

        // TODO: Remove code duplication. Find better solution to avoid another fxml file and another controller.
        editButton.setOnAction(event -> {
            final Stage popup = ProjectPopupViewFactory.createEditProjectView();

            popup.show();
        });

        openButton.setOnAction(event -> {
            try {
                switcher.loadWindow(WindowSwitcher.Window.EDITOR);
            } catch (IOException ignored) {

            }
        });
    }
}
