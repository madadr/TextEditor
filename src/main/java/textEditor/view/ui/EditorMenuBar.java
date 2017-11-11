package textEditor.view.ui;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import textEditor.view.ui.menu.MenuBarElement;

import java.util.ArrayList;
import java.util.Arrays;

public class EditorMenuBar extends MenuBar {
    private Menu file;
    private Menu edit;
    private Menu help;

    public EditorMenuBar() {
        super();
        init();
        getMenus().addAll(file, edit, help);
    }

    private void init() {
        file = new MenuBarElement("File", new ArrayList<>(Arrays.asList("New", "Open", "Save", "Close")));
        edit = new MenuBarElement("Edit", new ArrayList<>(Arrays.asList("Undo", "Redo", "---", "Copy", "Cut", "Paste")));
        help = new MenuBarElement("Help", new ArrayList<>(Arrays.asList("Help", "---", "About us")));
    }
}
