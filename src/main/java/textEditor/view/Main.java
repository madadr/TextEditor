package textEditor.view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import textEditor.controller.EditorController;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        BorderPane root = new BorderPane();
        int width = 600, height = 400;
        Scene scene = new Scene(root, width, height);

        MenuBar menuBar = new MenuBarView();
        VBox editor = new EditorView();

        root.setTop(menuBar);
        root.setCenter(editor);

        stage.setTitle("Text EditorView");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        new EditorController((EditorView) editor);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
