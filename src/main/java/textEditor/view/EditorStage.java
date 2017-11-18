package textEditor.view;

import javafx.scene.Scene;
import textEditor.controller.EditorController;
import textEditor.model.EditorModel;

public class EditorStage extends javafx.stage.Stage {
    private MainApp app;

    public EditorStage(MainApp app) {
        this.app = app;
        setScene(createEditorScene());
        setupSize();
    }

    private Scene createEditorScene() {
        EditorModel model = new EditorModel();
        EditorView view = new EditorView(model);
        EditorController controller = new EditorController(app, model, view);

        return new Scene(view);
    }

    private void setupSize() {
        setMinWidth(300);
        setMinHeight(150);
    }
}
