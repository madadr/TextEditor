package textEditor.view.ui.menuBar;

import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

import java.util.ArrayList;

public class MenuBarElement extends javafx.scene.control.Menu {
    private final ArrayList<String> actions;

    public MenuBarElement(String text, ArrayList<String> actions) {
        super(text);
        this.actions = actions;
        addMenuItems();
    }

    private void addMenuItems() {
        ObservableList<MenuItem> items = getItems();
        for (String action : actions) {
            if(action == "---") {
                items.add(new SeparatorMenuItem());
            } else {
                items.add(new MenuItem(action));
            }
        }
    }

    public ArrayList<String> getActions() {
        return actions;
    }
}
