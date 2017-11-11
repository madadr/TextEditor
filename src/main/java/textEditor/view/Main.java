package textEditor.view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import textEditor.view.ui.EditorMenuBar;

public class Main extends Application {
    private BorderPane root;
    private MenuBar menuBar;

    @Override
    public void start(Stage stage) throws Exception {
        root = new BorderPane();
        int width = 600, height = 400;
        Scene scene = new Scene(root, width, height);

        menuBar = new EditorMenuBar();

        root.setTop(menuBar);

        stage.setTitle("Text Editor");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
