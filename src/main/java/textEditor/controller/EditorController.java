package textEditor.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Menu;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import textEditor.model.EditorModel;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

public class EditorController implements Initializable {
    @FXML
    private Menu fileMenu,editMenu,helpMenu;
    @FXML
    private CheckBox bold,italic,underscore;
    @FXML
    private ChoiceBox fontType,fontSize;
    @FXML
    private TabPane tabBar;
    @FXML
    private HBox searchBox;
    private SingleSelectionModel<Tab> tabSelection;
    //Run when app starts
    private EditorModel editorModel;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        tabSelection = tabBar.getSelectionModel();
    }
    @FXML
    private void addingTab()
    {
        //TODO Add handling of only one tab(at this moment exception occured)
        int indexOfAddingPane = tabBar.getTabs().size() -1;

        tabBar.getTabs().add(indexOfAddingPane,new Tab("New tab"));

        Tab newItem = tabBar.getTabs().get(indexOfAddingPane);
        tabSelection.select(newItem);
        newItem.setContent(new TextArea());
        //TODO Make method that change name of Tabs on click request
        //TODO change return value of EditorModel addTab to boolean and handle this
//        editorModel.addTab(tabBar,tabSelection);
    }
    @FXML
    private void editUndoClicked()
    {

    }
    @FXML
    private void editRedoClicked()
    {

    }
    @FXML
    private void editCopyClicked()
    {

    }
    @FXML
    private void editCutClicked()
    {

    }
    @FXML
    private void editPasteClicked()
    {

    }
    @FXML
    private void helpHelpClicked()
    {

    }
    @FXML
    private void helpAboutUsClicked()
    {

    }
    @FXML
    private void editSearchClicked()
    {
        searchBox.setVisible(true);
    }
    @FXML
    private void fileNewClicked()
    {
        System.out.println("New file will be created");
    }
    @FXML
    private void fileOpenClicked()
    {
        System.out.println("File will be open");
    }
    @FXML
    private void fileSaveClicked()
    {
        System.out.println("file will be save");
    }
    @FXML
    private void closeSearchBar()
    {
        searchBox.setVisible(false);
    }
    @FXML
    private void fileCloseClicked()
    {

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
