package textEditor.controller;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import textEditor.RMIClient;
import textEditor.model.DatabaseModel;
import textEditor.view.WindowSwitcher;

import java.io.IOException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.ResourceBundle;


public class ProjectController implements Initializable, UserInjectionTarget, ClientInjectionTarget, WindowSwitcherInjectionTarget, ProjectInjectionTarget {
    @FXML
    private Label description;

    @FXML
    private Label contributors;

    @FXML
    private ListView<Project> projectListView;

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
    @FXML
    private Button backButton;

    private WindowSwitcher switcher;
    private DatabaseModel dbService;
    private RMIClient client;
    private User user;

    private List<Project> projects;
    private Project project;

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
        try {
            dbService = (DatabaseModel) client.getModel("DatabaseModel");
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }

        fetchUserProjectsFromDatabase();

        setupProjectsListView();

        initButtonsActions();
    }

    private void fetchUserProjectsFromDatabase() {
        try {
            projects = dbService.getProjects(user);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void setupProjectsListView() {
        projectListView.setItems(FXCollections.observableArrayList(this.projects));
        projectListView.getSelectionModel().getSelectedItems().addListener((ListChangeListener<? super Project>) (e) -> {
            Project project = projectListView.getSelectionModel().getSelectedItem();
            if (project != null) {
                description.setText(project.getDescription());
                contributors.setText(project.getContributors().toString());
            } else {
                description.setText("");
                contributors.setText("");
            }
        });
    }


    private void initButtonsActions() {
        newButton.setOnAction(event -> {
            try {
                switcher.loadWindow(WindowSwitcher.Window.ADD_PROJECT);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        editButton.setOnAction(event -> {
            try {
                Project project = projectListView.getSelectionModel().getSelectedItem();
                final int selectedIdx = projectListView.getSelectionModel().getSelectedIndex();
                if (selectedIdx != -1) {
                    project.setId(project.getId());
                    project.setTitle(project.getTitle());
                    project.setDescription(project.getDescription());
                    project.setContributors(project.getContributors());
                    switcher.loadWindow(WindowSwitcher.Window.EDIT_PROJECT);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        openButton.setOnAction(event -> {
            try {
                switcher.loadWindow(WindowSwitcher.Window.EDITOR);
            } catch (IOException ignored) {

            }
        });


        backButton.setOnAction(event -> {
            try {
                switcher.loadWindow(WindowSwitcher.Window.CHOOSE_ACTION);
            } catch (IOException ignored) {

            }
        });
    }

    @FXML
    public void onClickRemove(ActionEvent actionEvent) {
        Project projectToDelete = projectListView.getSelectionModel().getSelectedItem();
        final int selectedIdx = projectListView.getSelectionModel().getSelectedIndex();
        if (selectedIdx != -1) {
            try {
                dbService.removeProject(projectToDelete);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            projectListView.getItems().remove(selectedIdx);
        }

    }

    @Override
    public void injectProject(Project project) {
        this.project = project;
    }
}
