package textEditor;

import javafx.application.Application;
import javafx.stage.Stage;
import textEditor.view.WindowSwitcher;

public class Client extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        WindowSwitcher switcher = new WindowSwitcher(primaryStage);

        switcher.loadLoginWindow();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
