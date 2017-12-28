package textEditor.controller;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
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
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.TwoDimensional;
import textEditor.RMIClient;
import textEditor.model.EditorModel;
import textEditor.model.ObserverModel;
import textEditor.view.WindowSwitcher;

import java.io.File;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditorController implements Initializable, ClientInjectionTarget, WindowSwitcherInjectionTarget {
    @FXML
    private Menu fileMenu, editMenu, helpMenu;
    @FXML
    private ChoiceBox fontType;
    @FXML
    private ChoiceBox<String> fontSize;
    @FXML
    private HBox searchBox;
    @FXML
    private Button searchButton, nextSearchButton, previousSearchButton, closeSearchBox;
    @FXML
    private ToggleButton boldButton, italicButton, underscoreButton,
            alignmentLeftButton, alignmentCenterButton, alignmentRightButton, alignmentAdjustButton;
    @FXML
    private InlineCssTextArea searchArea;
    @FXML
    private StyleClassedTextArea mainTextArea;

    private Clipboard clipboard;

    private EditorModel editorModel;
    private ObserverModel observerModel;
    private RMIClient rmiClient;
    private WindowSwitcher switcher;
    private Pattern fontSizePattern;
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

        initialTextSettings();

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
    private void initialTextSettings()
    {
        //FontSize
        String matchFontSize = "fontsize\\d{1,2}px";
        fontSizePattern = Pattern.compile(matchFontSize);

        fontSize.getItems().addAll(" ","10px","12px","14px","16px","18px","20px","22px","32px","48px","70px");
        fontSize.setValue("12px");
        mainTextArea.setStyle("-fx-font-size: " + fontSize.getValue());
        fontSize.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> fontSizeChange(newValue) );
    }
    private void initTextSelection() {
        // TODO: create another scalable solution
        //SOLUTION: Meybe we should create enum ? to make this easier ?
        // HIGHLY dependent on boldButtonClicked() and italicButtonClicked() methods
        mainTextArea.selectedTextProperty().addListener((observable, oldValue, newValue) -> {
            // make both buttons unselected, when user didn't select any text
            if (newValue.equals("")) {
                boldButton.setSelected(false);
                italicButton.setSelected(false);
                underscoreButton.setSelected(false);
                return;
            }
            // check if whole selected text is bold or whole selected text is italic
            boolean isWholeBold = true;
            boolean isWholeItalic = true;
            boolean isWholeUnderscore = true;
            IndexRange range = mainTextArea.getSelection();

            for (int i = range.getStart(); i < range.getEnd(); ++i) {
                ArrayList<String> list = new ArrayList<String>(mainTextArea.getStyleOfChar(i));
                if (isWholeBold && !list.contains("boldWeight")) {
                    isWholeBold = false;
                }
                if (isWholeItalic && !list.contains("italicStyle")) {
                    isWholeItalic = false;
                }
                if (isWholeUnderscore && !list.contains("underscoreDecoration")) {
                    isWholeUnderscore = false;
                }

                ArrayList<String> newValuesSize = new ArrayList<String>();
                for (String input: list)
                {
                    if(fontSizePattern.matcher(input).matches())
                    {
                        newValuesSize.add(input);
                    }
                }
                System.out.println(newValuesSize.size());
                if(newValuesSize.size() == 0)
                {
                  fontSize.setValue("12px");
                }
                else if(newValuesSize.size() >= 2)
                {
                    fontSize.setValue(" ");
                }
                else
                {
                    String newValueSize = newValuesSize.get(0).replace("fontsize","");
                    System.out.println(newValueSize);
                    fontSize.setValue(newValueSize);
                }
            }

            boldButton.setSelected(isWholeBold);
            italicButton.setSelected(isWholeItalic);
            underscoreButton.setSelected(isWholeUnderscore);

            //check if paragraph styles
            paragraphStyleButtons();


        });

    }
    private void paragraphStyleButtons()
    {
        int startParagraphInSelection = mainTextArea.offsetToPosition(mainTextArea.getSelection().getStart(), TwoDimensional.Bias.Forward).getMajor();
        int lastParagraphInSelection = mainTextArea.offsetToPosition(mainTextArea.getSelection().getEnd(), TwoDimensional.Bias.Backward).getMajor();

        for (int paragraph = startParagraphInSelection;paragraph<=lastParagraphInSelection;paragraph++)
        {
            Collection<String> style = mainTextArea.getParagraph(paragraph).getParagraphStyle();
            if(style.equals(Collections.singleton("alignmentRight")))
            {
                alignmentRightButton.setSelected(true);
            }
            else if(style.equals(Collections.singleton("alignmentCenter"))){
                alignmentCenterButton.setSelected(true);
            }
            else if(style.equals(Collections.singleton("alignmentJustify"))){
                alignmentAdjustButton.setSelected(true);
            }
            else
            {
                alignmentLeftButton.setSelected(true);
            }
        }
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
        mainTextArea.undo();
    }

    @FXML
    private void editRedoClicked() {
        mainTextArea.redo();
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
        //TODO: Should we make one method for textStyle paragraphStyle and also for TextSize ?
        String selectedText = area.getSelectedText();
        IndexRange range = area.getSelection();

        boolean replaceNormalStyle = triggeringButton.isSelected();

        String newStyle = replaceNormalStyle ? transformedStyle : normalStyle;
        String oldStyle = replaceNormalStyle ? normalStyle : transformedStyle;

        StyleSpans<Collection<String>> spans = area.getStyleSpans(range);

        StyleSpans<Collection<String>> newSpans = spans.mapStyles(currentStyle -> {
            List<String> style = new ArrayList<String>(Arrays.asList(newStyle));
            style.addAll(currentStyle);
            style.remove(oldStyle);
            return style;
        });
        area.setStyleSpans(range.getStart(), newSpans);

        area.requestFocus();
    }

    @FXML
    private void underscoreButtonClicked() {
        transformTextStyle(mainTextArea, underscoreButton, "underscoreDecoration", "normalDecoration");
    }

    @FXML
    private void fontSizePlusButtonClicked()
    {
        int oldValueIndex = fontSize.getItems().indexOf(fontSize.getValue());
        String newValue = fontSize.getItems().get(oldValueIndex+1);
        fontSizeChange(newValue);
    }
    private void fontSizeChange(String newValue)
    {
        //TODO: Should we make one method for textStyle paragraphStyle and also for TextSize ?
        String selectedText = mainTextArea.getSelectedText();
        IndexRange range = mainTextArea.getSelection();

        StyleSpans<Collection<String>> spans = mainTextArea.getStyleSpans(range);

        StyleSpans<Collection<String>> newSpans = spans.mapStyles(currentStyle -> {
            List<String> style = new ArrayList<String>(Arrays.asList("fontsize"+newValue));
            List<String> stylesToRemove = new ArrayList<String>();
            for (String input: currentStyle)
            {
                if(fontSizePattern.matcher(input).matches())
                {
                    stylesToRemove.add(input);
                }
            }
            style.addAll(currentStyle);
            style.removeAll(stylesToRemove);
            return style;
        });
        mainTextArea.setStyleSpans(range.getStart(), newSpans);
        mainTextArea.requestFocus();
    }
    @FXML
    private void fontSizeMinusButtonClicked() {

    }

    @FXML
    private void alignmentLeftButtonClicked() {
        int startParagraphInSelection = mainTextArea.offsetToPosition(mainTextArea.getSelection().getStart(), TwoDimensional.Bias.Forward).getMajor();
        int lastParagraphInSelection = mainTextArea.offsetToPosition(mainTextArea.getSelection().getEnd(), TwoDimensional.Bias.Backward).getMajor();
        changeParagraphs(startParagraphInSelection,lastParagraphInSelection,alignmentLeftButton,"alignmentLeft");
    }

    @FXML
    private void alignmentCenterButtonClicked() {
        int startParagraphInSelection = mainTextArea.offsetToPosition(mainTextArea.getSelection().getStart(), TwoDimensional.Bias.Forward).getMajor();
        int lastParagraphInSelection = mainTextArea.offsetToPosition(mainTextArea.getSelection().getEnd(), TwoDimensional.Bias.Backward).getMajor();
        changeParagraphs(startParagraphInSelection, lastParagraphInSelection, alignmentCenterButton, "alignmentCenter");
    }
    @FXML
    private void alignmentRightButtonClicked() {
            int startParagraphInSelection = mainTextArea.offsetToPosition(mainTextArea.getSelection().getStart(), TwoDimensional.Bias.Forward).getMajor();
            int lastParagraphInSelection = mainTextArea.offsetToPosition(mainTextArea.getSelection().getEnd(), TwoDimensional.Bias.Backward).getMajor();
            changeParagraphs(startParagraphInSelection, lastParagraphInSelection, alignmentRightButton, "alignmentRight");
    }
    @FXML
    private void alignmentAdjustButtonClicked() {
        int startParagraphInSelection = mainTextArea.offsetToPosition(mainTextArea.getSelection().getStart(), TwoDimensional.Bias.Forward).getMajor();
        int lastParagraphInSelection = mainTextArea.offsetToPosition(mainTextArea.getSelection().getEnd(), TwoDimensional.Bias.Backward).getMajor();
        changeParagraphs(startParagraphInSelection,lastParagraphInSelection,alignmentAdjustButton,"alignmentJustify");
    }
    private void changeParagraphs(int firstParagraph, int lastParagraph,ToggleButton toggleButton,String style) {
        //TODO: Should we make one method for textStyle paragraphStyle and also for TextSize ?
        if(toggleButton.isSelected())
        {
            for (int paragraph = firstParagraph; paragraph < lastParagraph + 1; paragraph++)
                mainTextArea.setParagraphStyle(paragraph, Collections.singleton(style));
        }
        else
        {
            for (int paragraph = firstParagraph; paragraph < lastParagraph + 1; paragraph++)
                mainTextArea.setParagraphStyle(paragraph, Collections.singleton("alignmentLeft"));
        }

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
