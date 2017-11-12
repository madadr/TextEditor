package textEditor.view;

import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.VBox;

public class EditorView extends VBox {
    private ToolBar toolBar;
    private TextArea textArea;

    public EditorView() {
        super();
        toolBar = new ToolBarView();
        textArea = new TextAreaView();

        getChildren().addAll(toolBar, textArea);
    }

    public ToolBar getToolBar() {
        return toolBar;
    }

    public TextArea getTextArea() {
        return textArea;
    }
}
