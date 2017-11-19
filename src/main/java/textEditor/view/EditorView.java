package textEditor.view;

import javafx.scene.control.MenuBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import textEditor.model.EditorModel;
import textEditor.view.ui.editor.TextAreaView;
import textEditor.view.ui.editor.ToolBarView;

public class EditorView extends VBox {
    private MenuBar menuBar;
    private ToolBar toolBar;
    private TextArea textArea;

    private EditorModel model;

    public EditorView(EditorModel model) {
        this.model = model;

        menuBar = new MenuBarView();
        toolBar = new ToolBarView();
        textArea = new TextAreaView();
        setupTextArea();

        getChildren().addAll(menuBar, toolBar, textArea);
    }

    private void setupTextArea() {
        setupTextAreaSize();
    }

    private void setupTextAreaSize() {
        textArea.setWrapText(true);
        setVgrow(textArea, Priority.ALWAYS);
    }

    public MenuBar getMenuBar() {
        return menuBar;
    }

    public ToolBar getToolBar() {
        return toolBar;
    }

    public TextArea getTextArea() {
        return textArea;
    }
}
