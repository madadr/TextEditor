package textEditor.controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.StyledTextArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.TwoDimensional;
import textEditor.RMIClient;
import textEditor.model.*;
import textEditor.utils.ReadOnlyBoolean;
import textEditor.view.WindowSwitcher;

import java.io.File;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.*;
import java.util.regex.Pattern;

import static textEditor.controller.ConstValues.*;

public class EditorController implements Initializable, ClientInjectionTarget, WindowSwitcherInjectionTarget {
    @FXML
    public TextField searchTextField;
    @FXML
    public TextField replaceTextField;;
    @FXML
    public HBox replaceBox;
    @FXML
    private Menu fileMenu, editMenu, helpMenu;
    @FXML
    private ChoiceBox<String> fontSize, fontType, fontColor, paragraphHeading, bulletList;
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
    private DatabaseModel databaseModel;
    private RMIClient rmiClient;
    private WindowSwitcher switcher;
    private RemoteObserver observer;
    private TextFormatter textFormatter;
    private ReadOnlyBoolean isThisClientUpdatingText;

    //FontStyle Listeners
    private ChangeListener<? super String> fontSizeListener;
    private ChangeListener<? super String> fontFamilyListener;
    private ChangeListener<? super String> fontColorListener;
    private ChangeListener<? super String> paragraphHeadingListener;
    private ChangeListener<? super String> bulletListListener;
    private int searchTextIndex = -1;
    private boolean isSearched;

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
        textFormatter = new TextFormatter(mainTextArea);

        fontSizeListener = (ChangeListener<String>) (observable, oldValue, newValue) -> {
            textFormatter.styleSelectedArea(newValue, FONTSIZE_PATTERN_KEY);
            notifyOthers();
        };
        fontFamilyListener = (ChangeListener<String>) (observable, oldValue, newValue) -> {
            textFormatter.styleSelectedArea(newValue, FONTFAMILY_PATTERN_KEY);
            notifyOthers();
        };
        fontColorListener = (ChangeListener<String>) (observable, oldValue, newValue) -> {
            textFormatter.styleSelectedArea(newValue, FONTCOLOR_PATTERN_KEY);
            notifyOthers();
        };

        paragraphHeadingListener = (ChangeListener<String>) (observable, oldValue, newValue) -> {
            textFormatter.styleSelectedParagraph(getParagraphRange(), newValue, new ArrayList<>(Arrays.asList(HEADING_PATTERN_KEY, FONTSIZE_PATTERN_KEY, FONTCOLOR_PATTERN_KEY, FONTFAMILY_PATTERN_KEY)));
            notifyOthers();
        };
        bulletListListener = (ChangeListener<String>) (observable, oldValue, newValue) -> {
            textFormatter.applyBulletList(getParagraphRange(), newValue);
            notifyOthers();
        };

        clipboard = Clipboard.getSystemClipboard();
        editorModel = (EditorModel) rmiClient.getModel("EditorModel");
        databaseModel = (DatabaseModel) rmiClient.getModel("DatabaseModel");

        initialTextSettings();

        initTextArea();

        loadCssStyleSheet();

        initTextSelection();
    }

    private void initTextArea() {
        initObserver();

        mainTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (!isThisClientUpdatingText.getValue()) {
                    editorModel.setTextString(newValue, observer);
                    editorModel.setTextStyle(new StyleSpansWrapper(0, mainTextArea.getStyleSpans(0, mainTextArea.getText().length())), observer);
                }
            } catch (RemoteException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void initObserver() {
        try {
            EditorControllerObserver ecObserver = new EditorControllerObserver(mainTextArea);
            isThisClientUpdatingText = ecObserver.getIsUpdating();

            observer = new RemoteObserverImpl(ecObserver);

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
        //Bullet List
        bulletList.getItems().addAll(" ", "Unlist", "BulletList");
        bulletList.setValue(" ");
        bulletList.getSelectionModel().selectedItemProperty().addListener(bulletListListener);

    }


    private void initTextSelection() {
        mainTextArea.selectedTextProperty().addListener((observable, oldValue, newValue) -> {
            //FontWeight handling
            textFormatter.styleSpanFollower(boldButton, newValue, BOLD_PATTERN_KEY);
            textFormatter.styleSpanFollower(italicButton, newValue, ITALIC_PATTERN_KEY);
            textFormatter.styleSpanFollower(underscoreButton, newValue, UNDERSCORE_PATTERN_KEY);
            //FontChange handling
            textFormatter.styleSpanFollower(fontSize, fontSizeListener, FONTSIZE_PATTERN_KEY, "12px");
            textFormatter.styleSpanFollower(fontType, fontFamilyListener, FONTFAMILY_PATTERN_KEY, "CourierNew");
            textFormatter.styleSpanFollower(fontColor, fontColorListener, FONTCOLOR_PATTERN_KEY, "Black");
            textFormatter.styleSpanFollower(paragraphHeading, paragraphHeadingListener, HEADING_PATTERN_KEY, "None");
            //BulletList handling
            textFormatter.bulletListFollower(bulletList, bulletListListener, getParagraphRange());
            //Align Handling
            alignmentLeftButton.setSelected(true);
            textFormatter.paragraphStyleFollower(alignmentAdjustButton, getParagraphRange(), ALIGN_ADJUST);
            textFormatter.paragraphStyleFollower(alignmentCenterButton, getParagraphRange(), ALIGN_CENTER);
            textFormatter.paragraphStyleFollower(alignmentLeftButton, getParagraphRange(), ALIGN_LEFT);
            textFormatter.paragraphStyleFollower(alignmentRightButton, getParagraphRange(), ALIGN_RIGHT);
        });
    }


    private IndexRange getParagraphRange() {
        int start = mainTextArea.offsetToPosition(mainTextArea.getSelection().getStart(), TwoDimensional.Bias.Forward).getMajor();
        int end = mainTextArea.offsetToPosition(mainTextArea.getSelection().getEnd(), TwoDimensional.Bias.Backward).getMajor();
        return new IndexRange(start, end);
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
        mainTextArea.copy();
    }

    @FXML
    private void editCutClicked() {
        mainTextArea.cut();
    }

    @FXML
    private void editPasteClicked() {
        //TODO: refactor this shit
        StyledTextArea textInput = getFocusedText();
        if (textInput != null) {
            textInput.appendText(textInput.getText(0, textInput.getCaretPosition()) + clipboard.getString() + textInput.getText(textInput.getCaretPosition(), textInput.getLength()));
        }
    }

    private void notifyOthers() {
        try {
            editorModel.setTextStyle(new StyleSpansWrapper(0, mainTextArea.getStyleSpans(0, mainTextArea.getText().length())), observer);
        } catch (RemoteException e) {
            e.printStackTrace();
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
        replaceBox.setVisible(true);
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
        replaceBox.setVisible(false);
        if(searchTextIndex > -1)
        {
            IndexRange range = new IndexRange(searchTextIndex, searchTextIndex+searchTextField.getText().length());
            StyleSpans<Collection<String>> newSpans = mainTextArea.getStyleSpans(range).mapStyles(currentStyle -> {
                List<String> currentStyles = new ArrayList<>(currentStyle);
                currentStyles.remove("highlight");
                return currentStyles;
            });
            mainTextArea.setStyleSpans(range.getStart(), newSpans);

        }
    }

    @FXML
    private void boldButtonClicked() {
        if (boldButton.isSelected()) {
            textFormatter.styleSelectedArea(TEXT_BOLD, BOLD_PATTERN_KEY);
        } else {
            textFormatter.styleSelectedArea(TEXT_NORMAL, BOLD_PATTERN_KEY);
        }
        notifyOthers();
    }

    @FXML
    private void italicButtonClicked() {
        if (italicButton.isSelected()) {
            textFormatter.styleSelectedArea(TEXT_ITALIC, ITALIC_PATTERN_KEY);
        } else {
            textFormatter.styleSelectedArea(TEXT_NORMAL, ITALIC_PATTERN_KEY);
        }
        notifyOthers();
    }

    @FXML
    private void underscoreButtonClicked() {
        if (underscoreButton.isSelected()) {
            textFormatter.styleSelectedArea(TEXT_UNDERSCORE, UNDERSCORE_PATTERN_KEY);
        } else {
            textFormatter.styleSelectedArea(TEXT_NORMAL, UNDERSCORE_PATTERN_KEY);
        }
        notifyOthers();
    }

    @FXML
    private void replaceButtonClicked() {

        String search = searchTextField.getText();
        String replace = replaceTextField.getText();

        if(searchTextIndex > -1)
        {
            textFormatter.clearHighlight(new IndexRange(searchTextIndex, searchTextIndex + search.length()));
            mainTextArea.replaceText(new IndexRange(searchTextIndex, searchTextIndex + search.length()), replace);
            searchButtonClicked();
        }
        else
            searchButtonClicked();
    }

    @FXML
    private void replaceAllButtonClicked() {
        searchTextIndex = -1;
        searchButtonClicked();
        while (searchTextIndex > -1)
        {
            replaceButtonClicked();
        }
    }

    @FXML
    private void closeReplaceBoxClicked() {

    }

    @FXML
    private void alignmentLeftButtonClicked() {
        textFormatter.styleParagraphs(getParagraphRange(), alignmentLeftButton, ALIGN_LEFT);
        notifyOthers();
    }

    @FXML
    private void alignmentCenterButtonClicked() {
        textFormatter.styleParagraphs(getParagraphRange(), alignmentCenterButton, ALIGN_CENTER);
        notifyOthers();
    }

    @FXML
    private void alignmentRightButtonClicked() {
        textFormatter.styleParagraphs(getParagraphRange(), alignmentRightButton, ALIGN_RIGHT);
        notifyOthers();
    }

    @FXML
    private void alignmentAdjustButtonClicked() {
        textFormatter.styleParagraphs(getParagraphRange(), alignmentAdjustButton, ALIGN_ADJUST);
        notifyOthers();
    }


    @FXML
    private void searchButtonClicked() {
        String searchText = searchTextField.getText();

        if(!searchText.isEmpty())
        {
            textFormatter.clearHighlight(new IndexRange(searchTextIndex, searchTextIndex + searchText.length()));
            searchTextIndex = mainTextArea.getText().indexOf(searchText, searchTextIndex + 1);
            textFormatter.addHighlight(new IndexRange(searchTextIndex, searchTextIndex+searchText.length()));
        }
    }

    @FXML
    private void nextSearchButtonClicked() {
        searchButtonClicked();
    }

    @FXML
    private void previousSearchButtonClicked() {
        String searchText = searchTextField.getText();

        if(!searchText.isEmpty())
        {
            textFormatter.clearHighlight(new IndexRange(searchTextIndex, searchTextIndex + searchText.length()));
            searchTextIndex = mainTextArea.getText().substring(0, searchTextIndex + searchText.length() - 1).lastIndexOf(searchText);
            textFormatter.addHighlight(new IndexRange(searchTextIndex, searchTextIndex+searchText.length()));

        }
    }
}
