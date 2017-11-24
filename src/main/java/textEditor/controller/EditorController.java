package textEditor.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import textEditor.model.EditorModel;
import textEditor.model.EditorModelService;
import textEditor.model.ObserverService;

import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ResourceBundle;

public class EditorController extends UnicastRemoteObject implements Initializable {
    @FXML
    private Menu fileMenu, editMenu, helpMenu;
    @FXML
    private ChoiceBox fontType, fontSize;
    @FXML
    private HBox searchBox;
    @FXML
    private Button searchButton, nextSearchButton, previousSearchButton, closeSearchBox;
    @FXML
    private ToggleButton boldButton, italicButton, underscoreButton,
            aligmentLeftButton, aligmentCenterButton, aligmentRightButton, aligmentAdjustButton;
    @FXML
    private TextField searchTextField;
    @FXML
    private TextArea mainTextArea;

    private EditorModel editorModel;

    private EditorModelService editorModelService;
    private ObserverService observerService;

    public EditorController() throws RemoteException {
        super();
    }

    //Run when app starts
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 4321);

            editorModelService = (EditorModelService) registry.lookup("EditorModelService");
            observerService = (ObserverService) registry.lookup("ObserverService");
//            editorModelService.setTextAreaString();
            observerService.addObserver(editorModelService);
            mainTextArea.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    try {
                        editorModelService.setTextAreaString(newValue);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @FXML
    private void editUndoClicked() {

    }

    @FXML
    private void editRedoClicked() {

    }

    @FXML
    private void editCopyClicked() {

    }

    @FXML
    private void editCutClicked() {

    }

    @FXML
    private void editPasteClicked() {

    }

    @FXML
    private void helpHelpClicked() {

    }

    @FXML
    private void helpAboutUsClicked() {

    }

    @FXML
    private void editSearchClicked() {
        searchBox.setVisible(true);
    }

    @FXML
    private void fileNewClicked() {
        System.out.println("New file will be created");
    }

    @FXML
    private void fileOpenClicked() {
        System.out.println("File will be open");
    }

    @FXML
    private void fileSaveClicked() {
        System.out.println("file will be save");
    }

    @FXML
    private void fileCloseClicked() {

    }

    @FXML
    private void closeSearchBoxClicked() {
        searchBox.setVisible(false);
    }

    @FXML
    private void boldButtonClicked() {
        if(boldButton.isSelected())
        {
            mainTextArea.setStyle("-fx-font-weight: bold");
        }
        else{
            mainTextArea.setStyle("-fx-font-weight: normal");
        }

    }

    @FXML
    private void italicButtonClicked() {
        if(boldButton.isSelected())
        {
            mainTextArea.setStyle("-fx-font-style: italic");
        }
        else{
            mainTextArea.setStyle("-fx-font-style: normal");
        }

    }

    @FXML
    private void underscoreButtonClicked() {

    }

    @FXML
    private void fontSizePlusButtonClicked() {

    }

    @FXML
    private void fontSizeMinusButtonClicked() {

    }

    @FXML
    private void alligmentLeftButtonClicked() {

    }

    @FXML
    private void alligmentCenterButtonClicked() {

    }

    @FXML
    private void alligmentRightButtonClicked() {

    }

    @FXML
    private void aligmentAdjustButtonClicked() {

    }

    @FXML
    private void searchButtonClicked() {

    }

    @FXML
    private void nextSearchButtonClicked() {

    }

    @FXML
    private void previousSearchButtonClicked() {

    }


}
