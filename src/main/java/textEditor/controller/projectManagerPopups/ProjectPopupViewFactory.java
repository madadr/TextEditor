package textEditor.controller.projectManagerPopups;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ProjectPopupViewFactory {
    public static Stage createNewProjectView() {
        Stage view = new Stage();

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        Label newProjectLabel = new Label("New project");
        newProjectLabel.setFont(new Font("System Bold", 20));

        Label projectNameLabel = new Label("ProjectImpl name");
        projectNameLabel.setFont(new Font("System Bold", 12));
        TextField projectNameField = new TextField("");

        Label projectDescriptionLabel = new Label("ProjectImpl description");
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

        view.setScene(new Scene(vbox, 400, 500));

        return view;
    }

    public static Stage createEditProjectView() {
        Stage view = new Stage();
        view.initModality(Modality.APPLICATION_MODAL);

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        Label newProjectLabel = new Label("Edit project");
        newProjectLabel.setFont(new Font("System Bold", 20));

        Label projectNameLabel = new Label("ProjectImpl name");
        projectNameLabel.setFont(new Font("System Bold", 12));
        TextField projectNameField = new TextField("");

        Label projectDescriptionLabel = new Label("ProjectImpl description");
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

        view.setScene(new Scene(vbox, 400, 500));

        return view;
    }
}
