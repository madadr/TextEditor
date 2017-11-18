package textEditor.view;

import javafx.scene.control.MenuBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.VBox;
import textEditor.view.ui.editor.TextAreaView;
import textEditor.view.ui.editor.ToolBarView;

public class EditorView extends VBox {
    private MenuBar menuBar;
    private ToolBar toolBar;
    private TextArea textArea;

    public EditorView() {
        super();
        menuBar = new MenuBarView();
        toolBar = new ToolBarView();
        textArea = new TextAreaView();

        getChildren().addAll(menuBar, toolBar, textArea);
    }

    public ToolBar getToolBar() {
        return toolBar;
    }

    public TextArea getTextArea() {
        return textArea;
    }
}
