package textEditor.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.StyledTextArea;
import textEditor.RMIClient;
import textEditor.model.EditorModel;
import textEditor.model.ObserverModel;
import textEditor.view.WindowSwitcher;

import java.net.URL;
import java.util.ResourceBundle;

public class EditorController implements Initializable, ClientInjectionTarget, WindowSwitcherInjectionTarget {
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
    private InlineCssTextArea searchArea;
    @FXML
    private StyleClassedTextArea mainTextArea;

    private Clipboard clipboard;

    private EditorModel editorModel;
    private ObserverModel observerModel;
    private RMIClient rmiClient;
    private WindowSwitcher switcher;

    public EditorController() {
    }

    @Override
    public void injectClient(RMIClient client) {
        this.rmiClient = client;
    }

    @Override
    public void injectWindowSwitcher(WindowSwitcher switcher) {
        this.switcher = switcher;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        clipboard = Clipboard.getSystemClipboard();
        editorModel = (EditorModel) rmiClient.getModel("EditorModel");

//        mainTextArea.textProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
//            try {
//                editorModel.setTextAreaString(newValue);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        });
    }

    private StyledTextArea getFocusedText() {
        if (mainTextArea == null) {
            System.out.println("nulll");
        }

        if (mainTextArea.isFocused()) {
            return mainTextArea;

        } else if (searchArea.isFocused()) {
            return searchArea;
        }
        return null;
    }

    @FXML
    private void editUndoClicked() {

    }

    @FXML
    private void editRedoClicked() {

    }

    @FXML
    private void editCopyClicked() {
        ClipboardContent clipboardContent = new ClipboardContent();
        // getting text from focused area
        StyledTextArea textInput = getFocusedText();
        if (textInput != null) {
            clipboardContent.putString(textInput.getSelectedText());
            clipboard.setContent(clipboardContent);
        }
    }

    @FXML
    private void editCutClicked() {
        ClipboardContent clipboardContent = new ClipboardContent();
        StyledTextArea textInput = getFocusedText();
        if (textInput != null) {
            clipboardContent.putString(textInput.getSelectedText());
            // clearing coresponding area from cuted text
            IndexRange indexRange = textInput.getSelection();
            textInput.replaceText(indexRange, "");

            clipboard.setContent(clipboardContent);
        }
    }

    @FXML
    private void editPasteClicked() {
        //TODO: refactor this shit
        StyledTextArea textInput = getFocusedText();
        if (textInput != null) {
            textInput.appendText(textInput.getText(0, textInput.getCaretPosition()) + clipboard.getString() + textInput.getText(textInput.getCaretPosition(), textInput.getLength()));
        }
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

        if (boldButton.isSelected()) {
            mainTextArea.setStyle("-fx-font-weight: bold");
        } else {
            mainTextArea.setStyle("-fx-font-weight: normal");
        }

    }

    @FXML
    private void italicButtonClicked() {
        if (boldButton.isSelected()) {
            mainTextArea.setStyle("-fx-font-style: italic");
        } else {
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
