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
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
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

        mainTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                editorModel.setTextAreaString(newValue);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });

        // HIGHLY dependent on boldButtonClicked() and italicButtonClicked() methods
        mainTextArea.selectedTextProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.equals("")) {
                boldButton.setSelected(false);
                italicButton.setSelected(false);

                return;
            }

            boolean isAllBold = true;
            boolean isAllItalic = true;
            IndexRange range = mainTextArea.getSelection();

            for (int i = range.getStart(); i < range.getEnd(); ++i) {
                Collection<String> list = new ArrayList<String>(mainTextArea.getStyleOfChar(i));
                if (isAllBold && !list.contains("boldWeight")) {
                    isAllBold = false;
                }

                if (isAllItalic && !list.contains("italicStyle")) {
                    isAllItalic = false;
                }
            }

            boldButton.setSelected(isAllBold);
            italicButton.setSelected(isAllItalic);
        });

        mainTextArea.getStylesheets().add(EditorController.class.getResource("styles.css").toExternalForm());
    }

    private StyledTextArea getFocusedText() {
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
        String selectedText = mainTextArea.getSelectedText();
        IndexRange selection = mainTextArea.getSelection();

        if (boldButton.isSelected()) {
            IndexRange range = mainTextArea.getSelection();

            for (int i = range.getStart(); i < range.getEnd(); ++i) {
                Collection<String> list = new ArrayList<String>(mainTextArea.getStyleOfChar(i));
                if(!list.contains("boldWeight")) {
                    list.add("boldWeight");
                    list.remove("normalWeight");
                }
                mainTextArea.setStyle(i, i + 1, list);
            }
        } else {
            IndexRange range = mainTextArea.getSelection();

            for (int i = range.getStart(); i < range.getEnd(); ++i) {
                Collection<String> list = new ArrayList<String>(mainTextArea.getStyleOfChar(i));
                if(!list.contains("normalWeight")) {
                    list.add("normalWeight");
                    list.remove("boldWeight");
                }
                mainTextArea.setStyle(i, i + 1, list);
            }
        }

        mainTextArea.requestFocus();
    }

    @FXML
    private void italicButtonClicked() {
        String selectedText = mainTextArea.getSelectedText();
        IndexRange selection = mainTextArea.getSelection();

        if (italicButton.isSelected()) {
            IndexRange range = mainTextArea.getSelection();

            for (int i = range.getStart(); i < range.getEnd(); ++i) {
                Collection<String> list = new ArrayList<String>(mainTextArea.getStyleOfChar(i));
                if(!list.contains("italicStyle")) {
                    list.add("italicStyle");
                    list.remove("normalStyle");
                    mainTextArea.setStyle(i, i + 1, list);
                }
//                mainTextArea.setStyle(i, i + 1, list);
            }
        } else {
            IndexRange range = mainTextArea.getSelection();

            for (int i = range.getStart(); i < range.getEnd(); ++i) {
                Collection<String> list = new ArrayList<String>(mainTextArea.getStyleOfChar(i));
                if(!list.contains("normalStyle")) {
                    list.add("normalStyle");
                    list.remove("italicStyle");
                    mainTextArea.setStyle(i, i + 1, list);
                }
            }

        }

        mainTextArea.requestFocus();
    }

    @FXML
    private void underscoreButtonClicked() {
        String selectedText = mainTextArea.getSelectedText();
        IndexRange selection = mainTextArea.getSelection();

        if (underscoreButton.isSelected()) {
            IndexRange range = mainTextArea.getSelection();
            mainTextArea.setStyleClass(range.getStart(), range.getEnd(), "underscoreDecoration");
            mainTextArea.requestFocus();
        } else {
            IndexRange range = mainTextArea.getSelection();
            mainTextArea.setStyleClass(range.getStart(), range.getEnd(), "normalDecoration");
            mainTextArea.requestFocus();
        }
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
