package textEditor.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import textEditor.model.EditorModel;

import java.net.URL;
import java.util.ResourceBundle;

public class EditorController implements Initializable {
    @FXML
    private Menu fileMenu, editMenu, helpMenu;
    @FXML
    private ChoiceBox fontType, fontSize;
    @FXML
    private HBox searchBox;
    @FXML
    private Button boldButton, italicButton,underscoreButton,
                   aligmentLeftButton,aligmentCenterButton,aligmentRightButton,aligmentAdjustButton,
                   searchButton,nextSearchButton,previousSearchButton,closeSearchBox;
    @FXML TextField searchTextField;

    private EditorModel editorModel;
    //Run when app starts
    @Override
    public void initialize(URL location, ResourceBundle resources) {

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

    }
    @FXML
    private void italicButtonClicked() {

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

    //        this.view.getTextArea().textProperty().addListener(model);
//        this.model.addTextObserver(s -> updateTextArea(s));
//
//
////        });
//    }

//    private void updateTextArea(String st) {
////        view.getTextArea().setText(st);
//    }


}
