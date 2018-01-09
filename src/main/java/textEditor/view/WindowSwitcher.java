package textEditor.view;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import textEditor.RMIClient;
import textEditor.controller.ControllerFactory;

import java.io.IOException;

public class WindowSwitcher {
    private Stage stage;
    private FXMLLoader loader;
    private final ControllerFactory controllerFactory;

    public enum Window {
        LOGIN, REGISTER, EDITOR
    }

    public WindowSwitcher(Stage stage) {
        this.stage = stage;

        RMIClient rmiClient = new RMIClient();

        controllerFactory = new ControllerFactory(rmiClient, this);
    }

    public final Stage getStage() {
        return this.stage;
    }

    public void loadWindow(Window window) throws IOException {
        switch (window) {
            case LOGIN:
                loadLoginWindow();
                break;
            case EDITOR:
                loadEditorWindow();
                break;
            case REGISTER:
                loadRegisterWindow();
                break;
            default:
                System.err.println("Invalid window!");
                Platform.exit();
                System.exit(1);
        }
        reinitializeCloseHandler();
    }

    private void loadRegisterWindow() throws IOException {
        loadResource("Register.fxml");

        stage.setTitle("Editor - register");
        stage.setResizable(false);
        stage.setScene(new Scene((Parent) loader.load(), 600, 400));

        if (!isStageDisplayed()) {
            stage.show();
        }
    }

    private void loadLoginWindow() throws IOException {
        loadResource("Login.fxml");

        stage.setTitle("Editor - login");
        stage.setResizable(false);
        stage.setScene(new Scene((Parent) loader.load(), 600, 400));

        if (!isStageDisplayed()) {
            stage.show();
        }
    }

    private void loadEditorWindow() throws IOException {
        loadResource("Editor.fxml");

        stage.setTitle("Editor");
        initEditorLayout();
        stage.setScene(new Scene((Parent) loader.load()));

        if (!isStageDisplayed()) {
            stage.show();
        }
    }

    private void initEditorLayout() {
        stage.setResizable(true);
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
