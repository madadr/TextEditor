package textEditor.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import textEditor.model.EditorModel;
import textEditor.view.EditorView;

public class EditorController implements EventHandler<ActionEvent> {
    private EditorView view;
    private EditorModel model;

    private String text;

    public EditorController(EditorView view) {
        this.view = view;
        model = new EditorModel();

        text = "";
        updateText();

        view.getToolBar().addEventHandler(ActionEvent.ACTION, this);
    }

    private void updateText() {
        view.getTextArea().setText(text);
    }

    @Override
    public void handle(ActionEvent event) {
        // TODO: add implementation
    }
}
