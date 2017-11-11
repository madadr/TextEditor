package textEditor.view;

import javafx.scene.control.Button;

public class EditorToolBar extends javafx.scene.control.ToolBar {
    private Button bold;
    private Button italic;
    private Button underscore;

    public EditorToolBar() {
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
