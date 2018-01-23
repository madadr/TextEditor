package textEditor;

import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import textEditor.model.DatabaseModelImpl;
import textEditor.model.ProjectManagerImpl;
import textEditor.model.interfaces.DatabaseModel;
import textEditor.model.interfaces.ProjectManager;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;

public class Server extends Application {
    private Registry registry;

    private DatabaseModel databaseModel;
    private ProjectManager projectManager;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
         this.registry = LocateRegistry.createRegistry(4321);

        primaryStage.setTitle("Text editor - server");

        Label info = new Label("");
        info.setFont(Font.font("Arial", 15));
        info.setAlignment(Pos.CENTER);

        Button btn = new Button();
        Button btn2 = new Button();
        btn.setText("Start");
        btn.setFont(Font.font("Arial", 20));
        btn.setOnAction(e -> {
                    try {
                        startRMI();
                        info.setText("Server is running!");
                        btn.setDisable(true);
                        btn2.setDisable(false);
                    } catch (RemoteException e1) {
                        info.setText("Host connection failed.");
                        System.out.println(e1.getMessage());
                    } catch (SQLException | ClassNotFoundException e1) {
                        info.setText("Database connection failed.");
                        System.out.println(e1.getMessage());
                    }
                }
        );

        btn2.setText("Stop");
        btn2.setFont(Font.font("Arial", 20));
        btn2.setOnAction(e -> {
                    try {
                        UnicastRemoteObject.unexportObject(this.databaseModel, true);
                        UnicastRemoteObject.unexportObject(this.projectManager, true);
                        this.registry.unbind("DatabaseModel");
                        this.registry.unbind("ProjectManager");
                    } catch (RemoteException | NotBoundException e1) {
                        // object was unbound previously
                    }
            btn.setDisable(false);
                    btn2.setDisable(true);
                    info.setText("Server stopped!");
                }
        );
        btn2.setDisable(true);

        Label lbl = new Label("Text editor server");
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 30));

        HBox btnHbox = new HBox();
        btnHbox.getChildren().addAll(btn, btn2);
        btnHbox.setAlignment(Pos.CENTER);
        btnHbox.setSpacing(15);

        VBox box = new VBox();
        box.setAlignment(Pos.CENTER);
        box.setSpacing(30);
        box.getChildren().addAll(lbl, btnHbox, new Separator(Orientation.HORIZONTAL), info);

        StackPane root = new StackPane();
        root.getChildren().add(box);
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
    }

    private void startRMI() throws RemoteException, SQLException, ClassNotFoundException {
        //Creating models implementation of our classes
        this.databaseModel = new DatabaseModelImpl();
        this.projectManager = new ProjectManagerImpl(this.registry);

        //Exporting models interface to client
        DatabaseModel databaseModelExport = (DatabaseModel) UnicastRemoteObject.exportObject(databaseModel, 0);
        ProjectManager projectManagerExport = (ProjectManager) UnicastRemoteObject.exportObject(projectManager, 0);

        //Binding names and models interfaces
        this.registry.rebind("DatabaseModel", databaseModelExport);
        this.registry.rebind("ProjectManager", projectManagerExport);
    }
}
