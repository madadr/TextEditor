package textEditor.view.ui.editor;

import javafx.scene.control.Button;

public class ToolBarView extends javafx.scene.control.ToolBar {
    private Button bold;
    private Button italic;
    private Button underscore;

    public ToolBarView() {
        super();
        init();
        getItems().addAll(bold, italic, underscore);
    }

    private void init() {
        bold = new Button("Bold");
        italic = new Button("Italic");
        underscore = new Button("Underscore");
    }
}
