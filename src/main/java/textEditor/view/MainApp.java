package textEditor.view;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {
    private Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;

        stage = new EditorStage(this);

        stage.setTitle("Editor");

        stage.setResizable(true);

        stage.show();
    }

    public Stage getStage() {
        return stage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
