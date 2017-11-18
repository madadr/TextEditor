package textEditor.view;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import textEditor.controller.EditorController;

public class EditorStage extends Stage {
    public EditorStage() {
        setTitle("Editor");
        setScene(createScene(600, 400));
        setResizable(false);
    }

    private Scene createScene(int width, int height) {
        Scene scene = new Scene(createRoot(), width, height);

        return scene;
    }

    private Parent createRoot() {
        BorderPane root = new BorderPane();

        MenuBar menuBar = new MenuBarView();
        VBox editor = new EditorView();

        root.setTop(menuBar);
        root.setCenter(editor);

        // attach to controller
        new EditorController((EditorView) editor);

        return root;
    }
}
