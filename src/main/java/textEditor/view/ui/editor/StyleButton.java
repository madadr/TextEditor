package textEditor.view.ui.editor;

import javafx.scene.control.Button;
import textEditor.model.StyleAction;

public class StyleButton extends Button {
    private String buttonText;
    private StyleAction action;

    public StyleButton(String text, StyleAction action) {
        this.buttonText = text;
        this.action = action;

        setText(buttonText);
    }

    public String getButtonText() {
        return buttonText;
    }

    public StyleAction getAction() {
        return action;
    }
}
