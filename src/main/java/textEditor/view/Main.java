package textEditor.view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
    private BorderPane root;
    private MenuBar menuBar;
    private ToolBar toolBar;

    @Override
    public void start(Stage stage) throws Exception {
        root = new BorderPane();
        int width = 600, height = 400;
        Scene scene = new Scene(root, width, height);

        menuBar = new EditorMenuBar();
        toolBar = new EditorToolBar();

        VBox topBox = new VBox();
        topBox.getChildren().add(menuBar);
        topBox.getChildren().add(toolBar);

        root.setTop(topBox);

        stage.setTitle("Text Editor");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
