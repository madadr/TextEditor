package textEditor.view;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import textEditor.RMIClient;
import textEditor.controller.ControllerFactory;
import textEditor.model.ProjectImpl;
import textEditor.model.UserImpl;
import textEditor.model.interfaces.Project;
import textEditor.model.interfaces.User;

import java.io.IOException;
import java.rmi.RemoteException;

import static textEditor.view.WindowSwitcher.Window.EDITOR;
import static textEditor.view.WindowSwitcher.Window.POPUP_ACTIVE_USERS;

public class WindowSwitcher {
    private final ControllerFactory controllerFactory;
    private Stage stage, popupStage;
    private FXMLLoader loader;
    private User user;
    private Project project;

    public WindowSwitcher(Stage stage) throws RemoteException {
        RMIClient rmiClient = new RMIClient();
        this.stage = stage;
        user = new UserImpl();
        project = new ProjectImpl();
        controllerFactory = new ControllerFactory(rmiClient, this, user, project);
    }

    public Stage getMainStage() {
        return this.stage;
    }

    public Stage getPopupStage() {
        return this.popupStage;
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
            case POPUP_ACTIVE_USERS:
                loadActiveUsersPopup();
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

        reinitializeCloseHandler(window);
    }

    private void loadActiveUsersPopup() throws IOException {
        loadResource("PopupUsersEditing.fxml");

        popupStage = new Stage();
        popupStage.setScene(new Scene((Parent) loader.load(), 230, 240));
        popupStage.setResizable(false);
        popupStage.show();
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
        loadWindow("Action.fxml", "Editor - choose action", false);
    }

    private void loadFriendsWindow() throws IOException {
        loadWindow("Friends.fxml", "Editor - friends list", false);
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

    private void reinitializeCloseHandler(Window window) {
        if (window.equals(EDITOR) || window.equals(POPUP_ACTIVE_USERS)) {
            return;
        }

        this.stage.setOnCloseRequest(event -> {
            event.consume();
            Platform.exit();
            System.exit(0);
        });
    }

    private boolean isStageDisplayed() {
        return stage != null && stage.isShowing();
    }

    public enum Window {
        LOGIN, REGISTER, PICK_PROJECT, EDITOR, POPUP_ACTIVE_USERS, ADD_PROJECT, EDIT_PROJECT, FRIENDS_LIST, CHOOSE_ACTION
    }
}
