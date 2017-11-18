package textEditor.view;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import textEditor.controller.EditorController;

public class EditorScene extends Scene {
    // is app ref required to switch between scenes?
    private MainApp app;

    public EditorScene(MainApp app, int width, int height) {
        super(createRoot(), width, height);
        System.out.println("I am");
        this.app = app;
    }

    // must be static to let super class constructor use it
    private static Parent createRoot() {
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
