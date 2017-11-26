package textEditor.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import textEditor.RMIClient;

import java.io.IOException;

public class WindowSwitcher {
    private Stage stage;
    private FXMLLoader loader;
    private RMIClient rmiClient;
    private final ControllerFactory controllerFactory;

    public WindowSwitcher(Stage stage) {
        this.stage = stage;

        rmiClient = new RMIClient();

        controllerFactory = new ControllerFactory(rmiClient, this);
    }

    public void setLoginWindow() throws IOException {
        if(stage != null && stage.isShowing()) {
            stage.close();
        }

        loader = new FXMLLoader(getClass().getResource("Login.fxml"));
        loader.setControllerFactory(controllerFactory);

        stage.setTitle("Editor - login");
        stage.setResizable(false);
        stage.setScene(new Scene((Parent) loader.load(), 600, 400));
        stage.show();
    }

    public void setEditorWindow() throws IOException {
        loader = new FXMLLoader(getClass().getResource("Editor.fxml"));
        loader.setControllerFactory(controllerFactory);

        stage.setTitle("Editor");
        stage.setResizable(true);
        stage.setScene(new Scene((Parent) loader.load()));
        stage.setMaximized(true);
        stage.show();
    }
}
