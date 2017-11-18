package textEditor.view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    private static final int EDITOR_SCENE_WIDTH = 600;
    private static final int EDITOR_SCENE_HEIGHT = 400;

    private Stage stage;
    private Scene editorScene = new EditorScene(this, EDITOR_SCENE_WIDTH, EDITOR_SCENE_HEIGHT);

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;

        stage.setTitle("Editor");
        stage.setScene(editorScene);
        stage.setResizable(false);

        stage.show();
    }

    public Scene getEditorScene() {
        return editorScene;
    }

    public void setEditorScene(Scene scene) {
        this.stage.setScene(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
