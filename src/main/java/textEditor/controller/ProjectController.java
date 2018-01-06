package textEditor.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import textEditor.view.WindowSwitcher;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class ProjectController implements Initializable, UserInjectionTarget, WindowSwitcherInjectionTarget {
    @FXML
    public Label description;

    @FXML
    public Label contributors;

    @FXML
    private ListView<String> projectList;

    @FXML
    public Button editButton;

    @FXML
    private Button openButton;

    @FXML
    private Button newButton;

    private User user;
    private WindowSwitcher switcher;

    @Override
    public void injectUser(User user) {
        this.user = user;
    }

    @Override
    public void injectWindowSwitcher(WindowSwitcher switcher) {
        this.switcher = switcher;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("user=" + user);

        ObservableList<String> items = FXCollections.observableArrayList(
                "We", "shall", "not", "pass");

        projectList.setItems(items);

        initButtonsActions();
    }

    private void initButtonsActions() {
        newButton.setOnAction(event -> {
            final Stage popup = new Stage();
            popup.initModality(Modality.APPLICATION_MODAL);

            VBox vbox = new VBox(10);
            vbox.setPadding(new Insets(10));

            Label newProjectLabel = new Label("New project");
            newProjectLabel.setFont(new Font("System Bold", 20));

            Label projectNameLabel = new Label("Project name");
            projectNameLabel.setFont(new Font("System Bold", 12));
            TextField projectNameField = new TextField("");

            Label projectDescriptionLabel = new Label("Project description");
            projectDescriptionLabel.setFont(new Font("System Bold", 12));
            TextField projectDescriptionField = new TextField("");
            projectDescriptionField.setPrefHeight(200);
            projectDescriptionField.setAlignment(Pos.TOP_LEFT);

            Label contributorsLabel = new Label("Contributors");
            contributorsLabel.setFont(new Font("System Bold", 12));
            TextField contributorsField = new TextField("");

            Button addButton = new Button("Add");
            Button cancelButton = new Button("Cancel");

            HBox buttonBox = new HBox();
            buttonBox.setSpacing(10);
            buttonBox.setPadding(new Insets(10));
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.getChildren().addAll(addButton, cancelButton);

            vbox.getChildren().addAll(newProjectLabel, new Separator(Orientation.HORIZONTAL),
                    projectNameLabel, projectNameField, new Separator(Orientation.HORIZONTAL),
                    projectDescriptionLabel, projectDescriptionField, new Separator(Orientation.HORIZONTAL),
                    contributorsLabel, contributorsField,
                    buttonBox);

            popup.setScene(new Scene(vbox, 400, 500));
            popup.show();
        });

        editButton.setOnAction(event -> {
            System.out.println("Edit button");
        });

        openButton.setOnAction(event -> {
            try {
                switcher.loadWindow(WindowSwitcher.Window.EDITOR);
            } catch (IOException ignored) {

            }
        });
    }
}