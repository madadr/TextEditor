package textEditor;

import javafx.application.Application;
import javafx.stage.Stage;
import textEditor.view.WindowSwitcher;

public class Client extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        WindowSwitcher switcher = new WindowSwitcher(primaryStage);

        switcher.loadWindow(WindowSwitcher.Window.LOGIN);
    }
}
