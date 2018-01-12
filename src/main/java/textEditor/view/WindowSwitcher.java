package textEditor.view;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import textEditor.RMIClient;
import textEditor.controller.ControllerFactory;
import textEditor.controller.Project;
import textEditor.controller.ProjectImpl;
import textEditor.controller.UserImpl;

import java.io.IOException;
import java.rmi.RemoteException;

public class WindowSwitcher {
    private Stage stage;
    private FXMLLoader loader;
    private final ControllerFactory controllerFactory;
    private UserImpl user;

    public enum Window {
        LOGIN, REGISTER, PICK_PROJECT, EDITOR, ADD_PROJECT, EDIT_PROJECT, FRIENDS_LIST, CHOOSE_ACTION
    }

    public WindowSwitcher(Stage stage) throws RemoteException {
        RMIClient rmiClient = new RMIClient();
        this.stage = stage;
        user = new UserImpl();
        Project project = new ProjectImpl();

        controllerFactory = new ControllerFactory(rmiClient, this, user, project);
    }

    public final Stage getStage() {
        return this.stage;
    }

    public void loadWindow(Window window) throws IOException {
        switch (window) {
            case LOGIN:
                loadLoginWindow();
                break;
            case REGISTER:
                loadRegisterWindow();
                break;
            case PICK_PROJECT:
                loadPickProjectWindow();
                break;
            case CHOOSE_ACTION:
                loadChooseActionWindow();
                break;
            case EDITOR:
                loadEditorWindow();
                break;
            case ADD_PROJECT:
                loadAddProjectWindow();
                break;
            case EDIT_PROJECT:
                loadEditProjectWindow();
                break;
            case FRIENDS_LIST:
                loadFriendsWindow();
                break;
            default:
                System.err.println("Invalid window!");
                Platform.exit();
                System.exit(1);
        }
        reinitializeCloseHandler();
    }

    private void loadEditProjectWindow() throws IOException {
        loadWindow("EditProject.fxml", "Editor - edit project", false);
    }

    private void loadAddProjectWindow() throws IOException {
        loadWindow("AddProject.fxml", "Editor - add project", false);
    }

    private void loadLoginWindow() throws IOException {
        loadWindow("Login.fxml", "Editor - login", false);
    }

    private void loadRegisterWindow() throws IOException {
        loadWindow("Register.fxml", "Editor - register", false);
    }

    private void loadChooseActionWindow() throws IOException {
        loadResource("Action.fxml");

        stage.setTitle("Editor - choose action");
        stage.setResizable(false);
        stage.setScene(new Scene((Parent) loader.load()));

        if (!isStageDisplayed()) {
            stage.show();
        }
    }

    private void loadFriendsWindow() throws IOException {
        loadResource("Friends.fxml");

        stage.setTitle("Editor - friends list");
        stage.setResizable(false);
        stage.setScene(new Scene((Parent) loader.load()));

        if (!isStageDisplayed()) {
            stage.show();
        }
    }

    private void loadPickProjectWindow() throws IOException {
        loadWindow("ManageProject.fxml", "Editor - project", false);
    }

    private void loadEditorWindow() throws IOException {
        loadWindow("Editor.fxml", "Editor", true);
    }

    private void loadWindow(String file, String title, boolean isResizable) throws IOException {
        loadResource(file);

        stage.setTitle(title);
        stage.setResizable(isResizable);
        stage.setScene(new Scene((Parent) loader.load()));

        if (!isStageDisplayed()) {
            stage.show();
        }
    }

    private void loadResource(String resource) {
        loader = new FXMLLoader(getClass().getResource(resource));
        loader.setControllerFactory(controllerFactory);
    }

    private void reinitializeCloseHandler() {
        this.stage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
    }

    private boolean isStageDisplayed() {
        return stage != null && stage.isShowing();
    }
}
