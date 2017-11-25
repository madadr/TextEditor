package textEditor.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import textEditor.RMIClient;

import java.io.IOException;

public class WindowSwitcher {
    private Stage stage;
    private FXMLLoader loader;
    private RMIClient rmiClient;

    public WindowSwitcher(Stage stage) {
        this.stage = stage;
        rmiClient = new RMIClient();
        loader = new FXMLLoader(getClass().getResource("..\\view\\Login.fxml"));
        loader.setControllerFactory(new ControllerFactory(rmiClient, this));
    }

    public void setLoginWindow() throws IOException {
        stage.setTitle("Editor");
        stage.setResizable(false);
        stage.setScene(new Scene((Parent) loader.load(), 600, 400));
        stage.show();
    }

    public void setEditorWindow() throws IOException {
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("Editor.fxml"));

//        loader.setControllerFactory(new ControllerFactory(rmiClient, switcher));
//
//            //Geting primaryStage
//        Stage primaryStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

//        stage = new Stage();

        ((AnchorPane) stage.getScene().getRoot()).getChildren().setAll(new Scene(loader.load("Editor.fxml")));

//        loader.setLocation(getClass().getResource("Editor.fxml"));
//        stage.setResizable(true);
//        stage.setScene(new Scene(loader.load(), 600, 400));
////            primaryStage.setScene(editorScene);
//        stage.show();
    }
}
