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


public class ProjectController implements Initializable, UserInjectionTarget, ClientInjectionTarget, WindowSwitcherInjectionTarget, SelectedProjectInjectionTarget {
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

    private WindowSwitcher switcher;
    private DatabaseModel dbService;
    private RMIClient client;
    private UserImpl user;

    private List<Project> projects;
    private Project selectedProject;

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
        // get all projects data from database
        try {
            projects = dbService.getProjects(user);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void setupProjectsListView() {
        projectListView.setItems(FXCollections.observableArrayList(this.projects));
        projectListView.getSelectionModel().getSelectedItems().addListener((ListChangeListener<? super Project>) (e) -> {
            Project selectedProject = projectListView.getSelectionModel().getSelectedItem();
            if (selectedProject != null) {
                description.setText(selectedProject.getDescription());
                contributors.setText(selectedProject.getContributors().toString());
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
                    selectedProject.setId(project.getId());
                    selectedProject.setTitle(project.getTitle());
                    selectedProject.setDescription(project.getDescription());
                    selectedProject.setContributors(project.getContributors());
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
    public void injectSelectedProject(Project project) {
        this.selectedProject = project;
    }
}
