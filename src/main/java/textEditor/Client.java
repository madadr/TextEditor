package textEditor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import textEditor.controller.ControllerFactory;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client extends Application {
    public class RMIClient {
        private Registry registry;

        public RMIClient() {
            try {
                registry = LocateRegistry.getRegistry("localhost", 4321);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        public Remote getModel(String modelName) {
            try {
                return registry.lookup(modelName);
            } catch (RemoteException | NotBoundException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        RMIClient rmiClient = new RMIClient();

        //Started Application
        FXMLLoader loader = new FXMLLoader(getClass().getResource("\\view\\Login.fxml"));

        loader.setControllerFactory(new ControllerFactory(rmiClient));

        primaryStage.setTitle("Editor");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene((Parent)loader.load(), 600, 400));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
