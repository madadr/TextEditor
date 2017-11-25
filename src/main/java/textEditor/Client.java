package textEditor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import textEditor.controller.ControllerFactory;

public class Client extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        RMIClient rmiClient = new RMIClient();

        //Started Application
        FXMLLoader loader = new FXMLLoader(getClass().getResource("\\view\\Login.fxml"));

        loader.setControllerFactory(new ControllerFactory(rmiClient));

        primaryStage.setTitle("Editor");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene((Parent) loader.load(), 600, 400));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
