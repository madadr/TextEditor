package textEditor.controller;

import textEditor.model.EditorModel;
import textEditor.view.EditorView;
import textEditor.view.MainApp;

public class EditorController {
    private MainApp app;
    private EditorView view;
    private EditorModel model;

    public EditorController(MainApp app, EditorModel model, EditorView view) {
        this.app = app;
        this.view = view;
        this.model = model;


        this.model.addTextObserver(st -> updateTextArea(st));
    }

    private void updateTextArea(String st) {
        view.getTextArea().setText(st);
    }
}
