package textEditor.view;

import javafx.scene.Scene;
import textEditor.controller.EditorController;
import textEditor.model.EditorModel;

public class EditorScene extends Scene {
    // is app ref required to switch between scenes?
    private MainApp app;
    private EditorView view;

    public EditorScene(MainApp app, int width, int height) {
        super(new EditorView(), width, height);
        this.app = app;

        EditorModel model = new EditorModel();
        EditorView view = (EditorView) getRoot();
        EditorController controller = new EditorController(app, model, view);

        this.view = view;
    }
}
