package textEditor.utils;

import javafx.scene.control.Alert;

public class AlertManager {
    // short alert
    static public void displayAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);

        alert.setContentText(message);

        alert.showAndWait();
    }

    // longer alert
    static public void displayAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);

        alert.setTitle(title);

        alert.setHeaderText(title);

        alert.setContentText(message);

        alert.showAndWait();
    }

    // alert with callback
    static public void displayAlert(Alert.AlertType alertType, String message, Runnable action) {
        Alert alert = new Alert(alertType);

        alert.setContentText(message);

        alert.showAndWait();

        action.run();
    }
}
