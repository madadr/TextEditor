package textEditor.controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.*;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.model.Paragraph;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.TwoDimensional;
import org.reactfx.collection.LiveList;
import textEditor.controller.inject.ClientInjectionTarget;
import textEditor.controller.inject.ProjectInjectionTarget;
import textEditor.controller.inject.UserInjectionTarget;
import textEditor.controller.inject.WindowSwitcherInjectionTarget;
import textEditor.model.EditorControllerObserver;
import textEditor.model.RemoteObserverImpl;
import textEditor.model.StylesHolder;
import textEditor.model.interfaces.*;
import textEditor.utils.RMIClient;
import textEditor.utils.ReadOnlyBoolean;
import textEditor.utils.TextFormatter;
import textEditor.view.AlertManager;
import textEditor.view.WindowSwitcher;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.ResourceBundle;

import static textEditor.utils.Const.Format.*;
import static textEditor.view.WindowSwitcher.Window.POPUP_ACTIVE_USERS;

public class EditorController implements Initializable, ClientInjectionTarget, WindowSwitcherInjectionTarget, UserInjectionTarget, ProjectInjectionTarget {
    @FXML
    public TextField searchTextField;
    @FXML
    public TextField replaceTextField;
    @FXML
    public HBox replaceBox;
    @FXML
    private ChoiceBox<String> fontSize, fontType, fontColor, paragraphHeading, bulletList;
    @FXML
    private HBox searchBox;
    @FXML
    private ToggleButton boldButton, italicButton, underscoreButton,
            alignmentLeftButton, alignmentCenterButton, alignmentRightButton, alignmentAdjustButton;
    @FXML
    private StyleClassedTextArea mainTextArea;

    private ProjectManager projectManager;
    private EditorModel editorModel;
    private ActiveUserHandler activeUserHandler;
    private User user;
    private Project project;
    private RMIClient rmiClient;
    private WindowSwitcher switcher;

    private RemoteObserver observer;
    private TextFormatter textFormatter;
    private ReadOnlyBoolean isThisClientUpdatingText;

    //FontStyle Listeners
    private ChangeListener<String> fontSizeListener;
    private ChangeListener<String> fontFamilyListener;
    private ChangeListener<String> fontColorListener;
    private ChangeListener<String> paragraphHeadingListener;
    private ChangeListener<String> bulletListListener;

    private IndexRange searchTextIndex;

    @Override
    public void injectClient(RMIClient client) {
        this.rmiClient = client;
    }

    @Override
    public void injectWindowSwitcher(WindowSwitcher switcher) {
        this.switcher = switcher;
    }

    @Override
    public void injectProject(Project project) {
        this.project = project;
    }

    @Override
    public void injectUser(User user) {
        this.user = user;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textFormatter = new TextFormatter(mainTextArea);

        searchTextIndex = new IndexRange(-1, -1);

        initModels();

        initListeners();

        initialTextSettings();

        initTextArea();

        loadCssStyleSheet();

        initTextSelection();

        handleUserInProject();
    }

    private void initListeners() {
        fontSizeListener = (observable, oldValue, newValue) -> {
            textFormatter.styleSelectedArea(newValue, FONTSIZE_PATTERN_KEY);
            notifyOthers();
        };

        fontFamilyListener = (observable, oldValue, newValue) -> {
            textFormatter.styleSelectedArea(newValue, FONTFAMILY_PATTERN_KEY);
            notifyOthers();
        };

        fontColorListener = (observable, oldValue, newValue) -> {
            textFormatter.styleSelectedArea(newValue, FONTCOLOR_PATTERN_KEY);
            notifyOthers();
        };

        paragraphHeadingListener = (observable, oldValue, newValue) -> {
            textFormatter.styleSelectedParagraph(getParagraphRange(), newValue, new ArrayList<>(Arrays.asList(HEADING_PATTERN_KEY, FONTSIZE_PATTERN_KEY, FONTCOLOR_PATTERN_KEY, FONTFAMILY_PATTERN_KEY)));
            notifyOthers();
        };

        bulletListListener = (observable, oldValue, newValue) -> {
            textFormatter.applyBulletList(getParagraphRange(), newValue);
            notifyOthers();
        };
    }

    private void initModels() {
        try {
            projectManager = (ProjectManager) rmiClient.getModel("ProjectManager");
            this.editorModel = (EditorModel) rmiClient.getModel(projectManager.getEditorModelId(this.project));
            this.activeUserHandler = (ActiveUserHandler) rmiClient.getModel(projectManager.getActiveUserHandlerId(this.project));
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }

    private void handleUserInProject() {
        try {
            activeUserHandler.addUserToProject(project.getId(), user.getUsername());
        } catch (RemoteException e) {
            System.err.println("Error when getting user name");
        }
        switcher.getMainStage().setOnCloseRequest(event -> {
            event.consume();
            try {
                activeUserHandler.removeUserFromProject(project.getId(), user.getUsername());
            } catch (RemoteException e) {
                System.err.println("Error when getting user name");
            }
            Platform.exit();
            System.exit(0);
        });
    }

    private void initTextArea() {
        initObserver();

        mainTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (!isThisClientUpdatingText.getValue()) {
                    editorModel.setTextString(newValue, observer);
                    notifyOthers();
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

    @FXML
    private void editUndoClicked() {
        mainTextArea.undo();
    }

    @FXML
    private void editActiveUsers() {
        try {
            switcher.loadWindow(POPUP_ACTIVE_USERS);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        mainTextArea.paste();
    }

    private void notifyOthers() {
        try {
            int begin = 0;
            int end = mainTextArea.getText().length();

            StyleSpans<Collection<String>> styleSpans = mainTextArea.getStyleSpans(begin, end);

            LiveList<Paragraph<Collection<String>, String, Collection<String>>> paragraphs = mainTextArea.getParagraphs();

            editorModel.setTextStyle(new StylesHolder(begin, styleSpans, paragraphs), observer);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void helpHelpClicked() {
        //TODO:  implement javafx stage appear with help content
    }

    @FXML
    private void editSearchClicked() {
        searchBox.setVisible(true);
        replaceBox.setVisible(true);
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
        File file = fileChooser.showOpenDialog(switcher.getMainStage());
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
    private void filePrintClicked() {
        PrinterJob printerJob = PrinterJob.createPrinterJob();

        boolean successOnPrinting = printerJob.showPrintDialog(switcher.getMainStage());

        if (successOnPrinting) {
            PageLayout pageLayout = Printer.getDefaultPrinter().createPageLayout(Paper.A4, PageOrientation.PORTRAIT, Printer.MarginType.HARDWARE_MINIMUM);
            printerJob.getJobSettings().setPageLayout(pageLayout);
            successOnPrinting = printerJob.printPage(pageLayout, mainTextArea);
            if (successOnPrinting) {
                AlertManager.displayAlert(Alert.AlertType.CONFIRMATION, "Printer information", "Printing end with result: SUCCESS");
                printerJob.endJob();
            } else {
                AlertManager.displayAlert(Alert.AlertType.WARNING, "Printer information", "Printing end with result: FAILED");
            }
        } else {
            AlertManager.displayAlert(Alert.AlertType.WARNING, "Printer information", "Job Canceled");
        }
    }

    @FXML
    private void fileCloseClicked() {
        try {
            activeUserHandler.removeUserFromProject(project.getId(), user.getUsername());
        } catch (RemoteException e) {
            System.err.println("Error when getting user name");
        }
        Platform.exit();
    }

    @FXML
    private void closeSearchBoxClicked() {
        searchBox.setVisible(false);
        replaceBox.setVisible(false);
        textFormatter.clearHighlight(searchTextIndex);
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

        String replace = replaceTextField.getText();

        if (searchTextIndex.getStart() > -1) {
            textFormatter.clearHighlight(searchTextIndex);
            mainTextArea.replaceText(searchTextIndex, replace);
        }
        searchButtonClicked();
    }

    @FXML
    private void replaceAllButtonClicked() {
        searchTextIndex = new IndexRange(-1, -1);
        searchButtonClicked();
        while (searchTextIndex.getStart() > -1) {
            replaceButtonClicked();
        }
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

        if (!searchText.isEmpty()) {
            textFormatter.clearHighlight(searchTextIndex);
            int index = mainTextArea.getText().indexOf(searchText, searchTextIndex.getStart() + 1);
            searchTextIndex = new IndexRange(index, index + searchText.length());
            textFormatter.addHighlight(searchTextIndex);
        }
    }

    @FXML
    private void nextSearchButtonClicked() {
        searchButtonClicked();
    }

    @FXML
    private void previousSearchButtonClicked() {
        String searchText = searchTextField.getText();

        if (!searchText.isEmpty()) {
            textFormatter.clearHighlight(searchTextIndex);
            int index = mainTextArea.getText().substring(0, searchTextIndex.getStart() + searchText.length() - 1).lastIndexOf(searchText);
            searchTextIndex = new IndexRange(index, index + searchText.length());
            textFormatter.addHighlight(new IndexRange(searchTextIndex));

        }
    }

}
