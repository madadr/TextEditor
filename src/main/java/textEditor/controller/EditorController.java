package textEditor.controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
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
import org.fxmisc.richtext.model.*;
import textEditor.RMIClient;
import textEditor.model.*;
import textEditor.view.WindowSwitcher;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

public class EditorController implements Initializable, ClientInjectionTarget, WindowSwitcherInjectionTarget {
    @FXML
    private Menu fileMenu, editMenu, helpMenu;
    @FXML
    private ChoiceBox<String> fontSize, fontType, fontColor, paragraphHeading;
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
    private RMIClient rmiClient;
    private WindowSwitcher switcher;
    private Pattern fontSizePattern, fontFamilyPattern, fontColorPattern, paragraphHeadingPattern;
    private RemoteObserver observer;

    // flag for avoiding cycling dependencies during updates after observer event
    private AtomicBoolean isTextUpdatedByObserverEvent = new AtomicBoolean(false);

    //FontStyle Listeners
    private ChangeListener<? super String> fontSizeListener = (ChangeListener<String>) (observable, oldValue, newValue) -> fontChange("fontsize", newValue);
    private ChangeListener<? super String> fontFamilyListener = (ChangeListener<String>) (observable, oldValue, newValue) -> fontChange("fontFamily", newValue);
    private ChangeListener<? super String> fontColorListener = (ChangeListener<String>) (observable, oldValue, newValue) -> fontChange("color", newValue);
    private ChangeListener<? super String> paragraphHeadingListener = (ChangeListener<String>) (observable, oldValue, newValue) -> headingChange("heading", newValue);

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

        initTextArea();

        loadCssStyleSheet();

        initTextSelection();
    }

    private void initTextArea() {
        initObserver();

        mainTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (!isTextUpdatedByObserverEvent.get()) {
                    editorModel.setTextString(newValue, observer);
                    editorModel.setTextStyle(new StyleSpansWrapper(0, mainTextArea.getStyleSpans(0, mainTextArea.getText().length())));
                }
            } catch (RemoteException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void initObserver() {
        try {
            observer = new RemoteObserverImpl(new EditorControllerObserver());

            editorModel.addObserver(observer);

            observer.update(editorModel);
        } catch (RemoteException e) {
            System.out.println(e.getMessage());
        }
    }

    private void loadCssStyleSheet() {
        mainTextArea.getStylesheets().add(EditorController.class.getResource("styles.css").toExternalForm());
    }

    private void initialTextSettings() {
        //Patterns
        String matchFontSize = "fontsize\\d{1,2}px";
        String matchFontFamily = "fontFamily\\w{1,}";
        String matchFontColor = "color\\w{1,}";
        String matchParagraphHeading = "heading\\w{1,}";

        fontSizePattern = Pattern.compile(matchFontSize);
        fontFamilyPattern = Pattern.compile(matchFontFamily);
        fontColorPattern = Pattern.compile(matchFontColor);
        paragraphHeadingPattern = Pattern.compile(matchParagraphHeading);

        //Font Size
        fontSize.getItems().addAll(" ", "10px", "12px", "14px", "16px", "18px", "20px", "22px", "32px", "48px", "70px");
        fontSize.setValue("12px");
        fontSize.getSelectionModel().selectedItemProperty().addListener(fontSizeListener);
        //Font Type
        fontType.getItems().addAll(" ", "Broadway", "Arial", "Calibri", "CourierNew");
        fontType.setValue("CourierNew");
        fontType.getSelectionModel().selectedItemProperty().addListener(fontFamilyListener);
        //Font Color
        fontColor.getItems().addAll(" ", "Red", "Blue", "Green", "Yellow", "Purple", "White", "Black");
        fontColor.setValue("Black");
        fontColor.getSelectionModel().selectedItemProperty().addListener(fontColorListener);
        //Paragraph Heading
        paragraphHeading.getItems().addAll(" ", "None", "Header1", "Header2", "Header3");
        paragraphHeading.setValue(" ");
        paragraphHeading.getSelectionModel().selectedItemProperty().addListener(paragraphHeadingListener);
    }

    private void fontChange(String prefix, String newValue) {
        IndexRange range = mainTextArea.getSelection();

        StyleSpans<Collection<String>> spans = mainTextArea.getStyleSpans(range);

        StyleSpans<Collection<String>> newSpans = spans.mapStyles(currentStyle -> {
            List<String> styles = new ArrayList<>(currentStyle);
            styles.removeIf(s -> s.matches(prefix + ".*"));
            styles.add(prefix + newValue);
            return styles;
        });

        mainTextArea.setStyleSpans(range.getStart(), newSpans);
        try {
            editorModel.setTextStyle(new StyleSpansWrapper(0, mainTextArea.getStyleSpans(0, mainTextArea.getText().length())), observer);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        mainTextArea.requestFocus();
    }

    private void headingChange(String prefix, String newValue) {
        int startParagraphInSelection = mainTextArea.offsetToPosition(mainTextArea.getSelection().getStart(), TwoDimensional.Bias.Forward).getMajor();
        int lastParagraphInSelection = mainTextArea.offsetToPosition(mainTextArea.getSelection().getEnd(), TwoDimensional.Bias.Backward).getMajor();
        IndexRange range = mainTextArea.getSelection();
        int currentParagraphIndex = startParagraphInSelection;
        while (currentParagraphIndex <= lastParagraphInSelection) {
            Paragraph<Collection<String>, String, Collection<String>> currentParagraph = mainTextArea.getParagraph(currentParagraphIndex);
            StyleSpans<Collection<String>> actualStyles = currentParagraph.getStyleSpans();
            StyleSpans<Collection<String>> newStyles = actualStyles.mapStyles(currentStyle -> {
                List<String> styles = new ArrayList<>(currentStyle);
                //Remove all coresponding styles from paragraph
                styles.removeIf(s -> paragraphHeadingPattern.matcher(s).matches() || fontSizePattern.matcher(s).matches()
                        || fontFamilyPattern.matcher(s).matches() || fontColorPattern.matcher(s).matches());
                styles.add(prefix + newValue);
                styles.add("listType");
                System.out.println("New Style" + styles);
                return styles;
            });
            mainTextArea.setStyleSpans(currentParagraphIndex, 0, newStyles);
            currentParagraphIndex++;
        }
        try {
            editorModel.setTextStyle(new StyleSpansWrapper(0, mainTextArea.getStyleSpans(0, mainTextArea.getText().length())), observer);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        mainTextArea.requestFocus();
    }

    private void initTextSelection() {
        mainTextArea.selectedTextProperty().addListener((observable, oldValue, newValue) -> {
            //FontWeight handling
            // make buttons unselected, when user didn't select any text
            if (newValue.equals("")) {
                boldButton.setSelected(false);
                italicButton.setSelected(false);
                underscoreButton.setSelected(false);
                return;
            }
            // check if whole selected text is bold or whole selected text is italic or underscore
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
            }
            boldButton.setSelected(isWholeBold);
            italicButton.setSelected(isWholeItalic);
            underscoreButton.setSelected(isWholeUnderscore);

            //FontChange handling
            fontBoxStyle(fontSize, fontSizeListener, fontSizePattern, "12px", "fontsize");
            fontBoxStyle(fontType, fontFamilyListener, fontFamilyPattern, "CourierNew", "fontFamily");
            fontBoxStyle(fontColor, fontColorListener, fontColorPattern, "Black", "color");
            fontBoxStyle(paragraphHeading, paragraphHeadingListener, paragraphHeadingPattern, " ", "heading");
            //ParagraphStyles Handling
            paragraphStyleButtons();
        });
    }

    private void paragraphStyleButtons() {
        int startParagraphInSelection = mainTextArea.offsetToPosition(mainTextArea.getSelection().getStart(), TwoDimensional.Bias.Forward).getMajor();
        int lastParagraphInSelection = mainTextArea.offsetToPosition(mainTextArea.getSelection().getEnd(), TwoDimensional.Bias.Backward).getMajor();

        for (int paragraph = startParagraphInSelection; paragraph <= lastParagraphInSelection; paragraph++) {
            Collection<String> style = mainTextArea.getParagraph(paragraph).getParagraphStyle();
            if (style.equals(Collections.singleton("alignmentRight"))) {
                alignmentRightButton.setSelected(true);
            } else if (style.equals(Collections.singleton("alignmentCenter"))) {
                alignmentCenterButton.setSelected(true);
            } else if (style.equals(Collections.singleton("alignmentJustify"))) {
                alignmentAdjustButton.setSelected(true);
            } else {
                alignmentLeftButton.setSelected(true);
            }
        }
    }

    private void fontBoxStyle(ChoiceBox<String> box, ChangeListener<? super String> listener, Pattern pattern, String defaultValue, String replaceText) {
        IndexRange range = mainTextArea.getSelection();
        //Ceasing listener handling
        box.getSelectionModel().selectedItemProperty().removeListener(listener);

        StyleSpans<Collection<String>> styleSpans = mainTextArea.getStyleSpans(range);

        ArrayList<String> currentStyles = new ArrayList<>(styleSpans.getStyleSpan(0).getStyle());

        currentStyles.removeIf(s -> !(pattern.matcher(s).matches()));

        if (styleSpans.getSpanCount() == 1) {
            if (currentStyles.isEmpty()) {
                box.setValue(defaultValue);
            } else {
                String actualSizes = currentStyles.get(0);
                actualSizes = actualSizes.replace(replaceText, "");
                box.setValue(actualSizes);
            }
        } else {
            box.setValue(" ");
        }
        //listener handling is now raised
        box.getSelectionModel().selectedItemProperty().addListener(listener);
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
    private void bulletListClicked()
    {
        int startParagraphInSelection = mainTextArea.offsetToPosition(mainTextArea.getSelection().getStart(), TwoDimensional.Bias.Forward).getMajor();
        int lastParagraphInSelection = mainTextArea.offsetToPosition(mainTextArea.getSelection().getEnd(), TwoDimensional.Bias.Backward).getMajor();
        System.out.println("Start " + startParagraphInSelection + "end " + lastParagraphInSelection);
        int currentParagraph = startParagraphInSelection;
        while(currentParagraph<= lastParagraphInSelection)
        {
            System.out.println("Current " + currentParagraph);
            Paragraph<Collection<String>, String, Collection<String>> paragraph = mainTextArea.getParagraph(currentParagraph);
            String nonListedElement = mainTextArea.getText(currentParagraph);
            if(nonListedElement.matches("-.{1,}")) {
                //element has list prefix skipping
                ++currentParagraph;
                continue;
            }
            String bulletListElement = "-"+" "+nonListedElement;
            IndexRange paragraphRange = mainTextArea.getParagraphSelection(currentParagraph);
            System.out.println(paragraphRange.getStart()+ "   " + paragraphRange.getEnd() + "  " + paragraph.length());
            //REMEBER ABOUT RESTYLE FOR ALIGMENT
            StyleSpans<Collection<String>> currentParagraphStyles = mainTextArea.getStyleSpans(currentParagraph);
            mainTextArea.replaceText(currentParagraph,0,currentParagraph,paragraph.length(),bulletListElement);
            System.out.println("STYLE AFTER" + currentParagraphStyles);
            
            mainTextArea.setStyleSpans(currentParagraph,2,currentParagraphStyles);
            System.out.println("STYLE IN MAIN AFTER " + mainTextArea.getStyleSpans(currentParagraph));
            ++currentParagraph;
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
        IndexRange range = area.getSelection();

        boolean replaceNormalStyle = triggeringButton.isSelected();

        String newStyle = replaceNormalStyle ? transformedStyle : normalStyle;
        String oldStyle = replaceNormalStyle ? normalStyle : transformedStyle;

        StyleSpans<Collection<String>> spans = area.getStyleSpans(range);

        StyleSpans<Collection<String>> newSpans = spans.mapStyles(currentStyle -> {
            List<String> style = new ArrayList<>(Arrays.asList(newStyle));
            style.addAll(currentStyle);
            style.remove(oldStyle);
            return style;
        });
        area.setStyleSpans(range.getStart(), newSpans);
        try {
            editorModel.setTextStyle(new StyleSpansWrapper(0, area.getStyleSpans(0, area.getText().length())), observer);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        area.requestFocus();
    }

    @FXML
    private void underscoreButtonClicked() {
        transformTextStyle(mainTextArea, underscoreButton, "underscoreDecoration", "normalDecoration");
    }

    @FXML
    private void replaceButtonClicked() {

    }

    @FXML
    private void replaceAllButtonClicked() {

    }

    @FXML
    private void closeReplaceBoxClicked() {

    }

    @FXML
    private void alignmentLeftButtonClicked() {
        int startParagraphInSelection = mainTextArea.offsetToPosition(mainTextArea.getSelection().getStart(), TwoDimensional.Bias.Forward).getMajor();
        int lastParagraphInSelection = mainTextArea.offsetToPosition(mainTextArea.getSelection().getEnd(), TwoDimensional.Bias.Backward).getMajor();
        changeParagraphs(startParagraphInSelection, lastParagraphInSelection, alignmentLeftButton, "alignmentLeft");
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
        changeParagraphs(startParagraphInSelection, lastParagraphInSelection, alignmentAdjustButton, "alignmentJustify");
    }

    private void changeParagraphs(int firstParagraph, int lastParagraph, ToggleButton toggleButton, String style) {
        if (toggleButton.isSelected()) {
            for (int paragraph = firstParagraph; paragraph < lastParagraph + 1; paragraph++)
                mainTextArea.setParagraphStyle(paragraph, Collections.singleton(style));
        } else {
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

    public class EditorControllerObserver implements Serializable, RemoteObserver {
        private class UpdateTextWrapper implements Runnable {
            private RemoteObservable observable;

            public UpdateTextWrapper(RemoteObservable observable) {
                this.observable = observable;
            }

            @Override
            public void run() {
                isTextUpdatedByObserverEvent.set(true);
                int oldCaretPosition = mainTextArea.getCaretPosition();
                String oldText = mainTextArea.getText();
                try {
                    String newText = ((EditorModel) observable).getTextString();
                    mainTextArea.replaceText(newText);
                    int newCaretPosition = calculateNewCaretPosition(oldCaretPosition, oldText, newText);
                    mainTextArea.moveTo(newCaretPosition);
                } catch (RemoteException e) {
                    e.printStackTrace();
                } finally {
                    isTextUpdatedByObserverEvent.set(false);
                }
            }
        }

        private class UpdateStyleWrapper implements Runnable {
            private RemoteObservable observable;

            public UpdateStyleWrapper(RemoteObservable observable) {
                this.observable = observable;
            }

            @Override
            public void run() {
                try {
                    StyleSpansWrapper newStyle = ((EditorModel) observable).getTextStyle();
                    if (newStyle != null && newStyle.getStyleSpans() != null) {
                        mainTextArea.setStyleSpans(newStyle.getStylesStart(), newStyle.getStyleSpans());
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("CRASH");
                }
            }
        }

        @Override
        public void update(RemoteObservable observable) throws RemoteException {
            Platform.runLater(new UpdateTextWrapper(observable));
            Platform.runLater(new UpdateStyleWrapper(observable));
        }

        @Override
        public synchronized void update(RemoteObservable observable, RemoteObservable.UpdateTarget target) throws RemoteException {
            if (target == RemoteObservable.UpdateTarget.ONLY_TEXT) {
                Platform.runLater(new UpdateTextWrapper(observable));
            }

            if (target == RemoteObservable.UpdateTarget.ONLY_STYLE) {
                Platform.runLater(new UpdateStyleWrapper(observable));
            }
        }

        // issues when using redo/undo actions as clients can undo another client operations
        private int calculateNewCaretPosition(int oldCaretPosition, String oldText, String newText) {
            if (newText.length() == 0) {
                return 0;
            }

            int indexOfTextBeforeCaret = newText.indexOf(oldText.substring(0, oldCaretPosition));
            if (indexOfTextBeforeCaret != -1) {
                return oldCaretPosition;
            }

            int indexOfTextAfterCaret = newText.indexOf(oldText.substring(oldCaretPosition, oldText.length()));
            if (indexOfTextAfterCaret != -1) {
                return indexOfTextAfterCaret;
            }

            return findFirstDifferenceIndex(oldText, newText);
        }

        private int findFirstDifferenceIndex(String oldText, String newText) {
            int longestLength = oldText.length() > newText.length() ? oldText.length() : newText.length();

            for (int i = 0; i < longestLength; ++i) {
                if (oldText.charAt(i) != newText.charAt(i)) {
                    return i;
                }
            }

            return 0;
        }
    }
}
