package textEditor.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.control.Button;
import textEditor.model.EditorModel;
import textEditor.model.StyleAction;
import textEditor.view.EditorView;
import textEditor.view.MainApp;
import textEditor.view.ui.editor.StyleButton;

public class EditorController implements EventHandler<ActionEvent> {
    private EditorView view;
    private EditorModel model;

    private String text;

    public EditorController(MainApp app, EditorModel model, EditorView view) {
        this.view = view;
        this.model = model;

        text = "";
        updateText();

        view.getToolBar().addEventHandler(ActionEvent.ACTION, this);
    }

    private void updateText() {
        view.getTextArea().setText(text);
    }

    @Override
    public void handle(ActionEvent event) {
        EventTarget target = event.getTarget();

        if (!(target instanceof Button)) {
            return;
        }

        String selectedText = view.getTextArea().getSelectedText();

        if (target instanceof StyleButton) {
            StyleButton button = (StyleButton) target;
            StyleAction action = button.getAction();

            if (action == null) {
                return;
            }

            switch (action) {
                case BOLD:
                    model.setText(boldText(selectedText));
//                    view.getTextArea().replaceSelection(boldText(selectedText));
                    break;
                case ITALIC:
                    break;
                case UNDERSCORE:
                    break;
            }
        }

//        updateText();
    }

    // TODO: create separate class for tags manipulation
    private String boldText(String text) {
        StringBuilder sb = new StringBuilder();
        String boldOpenTag = "<b>";
        String boldCloseTag = "</b>";
        sb.append(boldOpenTag).append(text).append(boldCloseTag);

        return sb.toString();
    }

    public void setText(String text) {
        this.text = text;
    }
}
