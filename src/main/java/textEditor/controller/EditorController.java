package textEditor.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.StyledTextArea;
import org.fxmisc.richtext.model.TwoDimensional;
import textEditor.RMIClient;
import textEditor.model.EditorModel;
import textEditor.model.ObserverModel;
import textEditor.view.WindowSwitcher;

import java.io.File;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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

        loadCssStyleSheet();

        initTextSelection();
    }

    private void loadCssStyleSheet() {
        mainTextArea.getStylesheets().add(EditorController.class.getResource("styles.css").toExternalForm());
    }

    private void initTextSelection() {
        // TODO: create another scalable solution
        // HIGHLY dependent on boldButtonClicked() and italicButtonClicked() methods
        mainTextArea.selectedTextProperty().addListener((observable, oldValue, newValue) -> {
            // make both buttons unselected, when user didn't select any text
            if (newValue.equals("")) {
                boldButton.setSelected(false);
                italicButton.setSelected(false);

                return;
            }

            // check if whole selected text is bold or whole selected text is italic
            boolean isWholeBold = true;
            boolean isWholeItalic = true;
            IndexRange range = mainTextArea.getSelection();

            for (int i = range.getStart(); i < range.getEnd(); ++i) {
                Collection<String> list = new ArrayList<String>(mainTextArea.getStyleOfChar(i));
                if (isWholeBold && !list.contains("boldWeight")) {
                    isWholeBold = false;
                }

                if (isWholeItalic && !list.contains("italicStyle")) {
                    isWholeItalic = false;
                }
            }

            boldButton.setSelected(isWholeBold);
            italicButton.setSelected(isWholeItalic);
        });

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
        //TODO:  implement javafx stage appear with help content
    }

    @FXML
    private void helpAboutUsClicked() {
        //TODO:  implement javafx stage appear with aboutUs content
    }

    @FXML
    private void editSearchClicked() {
        searchBox.setVisible(true);
        //TODO:  implement search
    }

    @FXML
    private void fileNewClicked() {
        System.out.println("New file will be created");
        //TODO: how this should look like ?
    }

    @FXML
    private void fileOpenClicked() {
        System.out.println("File will be open");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose resource");
        File file = fileChooser.showOpenDialog(switcher.getStage());
        if (file != null) {
            //TODO: handle this
            //openFile(file);
        }
    }

    @FXML
    private void fileSaveClicked() {
        System.out.println("file will be save");
    }

    @FXML
    private void fileCloseClicked() {
        Platform.exit();
    }

    @FXML
    private void closeSearchBoxClicked() {
        searchBox.setVisible(false);
    }

    @FXML
    private void boldButtonClicked() {
        transformTextStyle(mainTextArea, boldButton, "boldWeight", "normalWeight");
    }

    @FXML
    private void italicButtonClicked() {
        transformTextStyle(mainTextArea, italicButton, "italicStyle", "normalStyle");
    }

    private void transformTextStyle(StyleClassedTextArea area, ToggleButton triggeringButton, String transformedStyle, String normalStyle) {
        String selectedText = area.getSelectedText();
        IndexRange selection = area.getSelection();

        IndexRange range = area.getSelection();

        boolean replaceNormalStyle = triggeringButton.isSelected();

        String newStyle = replaceNormalStyle ? transformedStyle : normalStyle;
        String oldStyle = replaceNormalStyle ? normalStyle : transformedStyle;

        for (int i = range.getStart(); i < range.getEnd(); ++i) {
            Collection<String> list = new ArrayList<>(area.getStyleOfChar(i));
            if (!list.contains(newStyle)) {
                list.add(newStyle);
                list.remove(oldStyle);
            }
            area.setStyle(i, i + 1, list);
        }

        area.requestFocus();
    }

    @FXML
    private void underscoreButtonClicked() {
        transformTextStyle(mainTextArea, underscoreButton, "underscoreDecoration", "normalDecoration");
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
        int startParagraphInSelection = mainTextArea.offsetToPosition(mainTextArea.getSelection().getStart(), TwoDimensional.Bias.Forward).getMajor();
        int lastParagraphInSelection = mainTextArea.offsetToPosition(mainTextArea.getSelection().getEnd(), TwoDimensional.Bias.Backward).getMajor();
        for (int paragraph = startParagraphInSelection; paragraph < lastParagraphInSelection + 1; paragraph++)
            mainTextArea.setParagraphStyle(paragraph, Collections.singleton("alligmentCenter"));
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
