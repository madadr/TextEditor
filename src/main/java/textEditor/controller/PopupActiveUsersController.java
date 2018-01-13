package textEditor.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import textEditor.RMIClient;
import textEditor.controller.targetInjections.ClientInjectionTarget;
import textEditor.controller.targetInjections.ProjectInjectionTarget;
import textEditor.controller.targetInjections.WindowSwitcherInjectionTarget;
import textEditor.model.interfaces.ActiveUserHandler;
import textEditor.model.interfaces.Project;
import textEditor.view.WindowSwitcher;

import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.SECONDS;
import static textEditor.utils.ConstValues.TWO_SECONDS;


public class PopupActiveUsersController implements WindowSwitcherInjectionTarget, Initializable, ClientInjectionTarget, ProjectInjectionTarget {

    @FXML
    private ListView<String> authorsListView;

    //Injections and RMI helpers
    private WindowSwitcher window;
    private RMIClient rmiClient;
    private Project project;
    private ActiveUserHandler activeUserHandler;

    //Update thread
    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> updateHandler;
    private ArrayList<String> activeUsers;
    private Runnable updater;

    @Override
    public void injectProject(Project project) {
        this.project = project;
    }

    @Override
    public void injectWindowSwitcher(WindowSwitcher switcher) {
        this.window = switcher;
    }

    @Override
    public void injectClient(RMIClient client) {
        this.rmiClient = client;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            activeUserHandler = (ActiveUserHandler) rmiClient.getModel("ActiveUserHandler");
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
        establishUpdater();
    }

    @FXML
    private void onClickClose() {
        updateHandler.cancel(false);
        scheduler.shutdownNow();
        window.getPopupStage().close();
    }


    private void updateActiveUserList() {
        updater = () -> {
            Platform.runLater(() -> {
                try {
                    activeUsers = activeUserHandler.getActiveUserInProject(project.getId());
                    authorsListView.setItems(FXCollections.observableArrayList(activeUsers));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            });
        };
    }

    private void establishUpdater() {
        updateActiveUserList();
        scheduler = Executors.newScheduledThreadPool(1);
        updateHandler = scheduler.scheduleAtFixedRate(updater, 0, TWO_SECONDS, SECONDS);
        defineClosing();
    }

    private void defineClosing() {
        window.getPopupStage().setOnCloseRequest(event -> {
            event.consume();
            updateHandler.cancel(false);
            scheduler.shutdownNow();
            window.getPopupStage().close();
        });
    }
}
