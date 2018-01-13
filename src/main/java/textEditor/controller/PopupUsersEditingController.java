package textEditor.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import textEditor.RMIClient;
import textEditor.model.ActiveUserHandler;
import textEditor.view.WindowSwitcher;

import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class PopupUsersEditingController implements WindowSwitcherInjectionTarget, Initializable, ClientInjectionTarget,ProjectInjectionTarget {

    private WindowSwitcher window;
    private RMIClient rmiClient;
    private ActiveUserHandler activeUserHandler;
    private Project project;
    @FXML
    private ListView authorsListView;

    private ArrayList<String> activeUsers;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            activeUserHandler = (ActiveUserHandler) rmiClient.getModel("ActiveUserHandler");
            if(project == null)
            {
                System.out.println("IM NULL BITCH PROJECT");

            }
            System.out.println(project.getId()+"STH");


            System.out.println("NeW LIST");
            activeUsers = activeUserHandler.getActiveUserInProject(project.getId());
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
        authorsListView.setItems(FXCollections.observableArrayList(activeUsers));
    }

    @Override
    public void injectWindowSwitcher(WindowSwitcher switcher) {
        this.window = switcher;
    }

    @Override
    public void injectClient(RMIClient client) {
        this.rmiClient = client;
    }

    @FXML
    private void onClickClose(){
        window.getPopupStage().close();
    }

    @Override
    public void injectProject(Project project) {
        this.project = project;
    }
}
