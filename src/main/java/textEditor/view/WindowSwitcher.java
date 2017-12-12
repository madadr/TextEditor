package textEditor.view;

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

    public WindowSwitcher(Stage stage) {
        this.stage = stage;

        RMIClient rmiClient = new RMIClient();

        controllerFactory = new ControllerFactory(rmiClient, this);
    }

    public void loadLoginWindow() throws IOException {
        loadWindow("Login.fxml");

        stage.setTitle("Editor - login");
        stage.setResizable(false);
        stage.setScene(new Scene((Parent) loader.load(), 600, 400));

        if (!isStageDisplayed()) {
            stage.show();
        }
    }
    public final Stage getStage()
    {
        return this.stage;
    }
    public void loadEditorWindow() throws IOException {
        loadWindow("Editor.fxml");

        stage.setTitle("Editor");
        stage.setResizable(true);
        stage.setScene(new Scene((Parent) loader.load()));
        stage.setMaximized(true);

        if (!isStageDisplayed()) {
            stage.show();
        }
    }

    private void loadWindow(String resource) {
        loader = new FXMLLoader(getClass().getResource(resource));
        loader.setControllerFactory(controllerFactory);
    }


    private boolean isStageDisplayed() {
        return stage != null && stage.isShowing();
    }
}
