package textEditor.model;

import textEditor.model.interfaces.*;

import java.io.File;
import java.net.URI;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.HashMap;

import static textEditor.utils.Const.Files.PROJECTS_PATH;

public class ProjectManagerImpl implements ProjectManager {
    private HashMap<Project, EditorModel> projectEditor;
    private HashMap<Project, ActiveUserHandler> projectActive;

    public ProjectManagerImpl() {
        projectEditor = new HashMap<>();
        projectActive = new HashMap<>();
    }

    @Override
    public EditorModel getEditorModel(Project project) throws RemoteException {
        System.out.println("getEditorModel");
        if (!this.projectEditor.containsKey(project)) {
            System.out.println("Project " + project.getId() + " isn't in editorMap!");
            this.projectEditor.put(project, createEditorModel(project));
        } else {
            System.out.println("Project " + project.getId() + " IS in editorMap!");
        }

        EditorModel model = this.projectEditor.get(project);
        System.out.println(model.getTextString());
        System.out.println(model.getTextStyle());
        System.out.println(model.getData());

        return model;
    }

    private EditorModel createEditorModel(Project project) throws RemoteException {
        System.out.println("createEditorModel");
        File modelFile = new File(buildProjectUri(project));

        EditorModelData data;

        boolean editorModelFile = modelFile.exists() && !modelFile.isDirectory();
        System.out.println("\tchecked if exists");
        if (editorModelFile) {
            System.out.println("\tgetting new data");
            data = EditorModel.getEditorModelData(modelFile);
        } else {
            System.out.println("\tcreating new data");
            data = new EditorModelDataImpl();
        }

        return new EditorModelImpl(data);
    }

    private URI buildProjectUri(Project project) {
        return Paths.get(PROJECTS_PATH, project.getId() + ".model").toUri();
    }

    @Override
    public ActiveUserHandler getActiveUserHandler(Project project) {
        if (!projectActive.containsKey(project)) {
            projectActive.put(project, new ActiveUsersHandlerImpl());
        }

        return projectActive.get(project);
    }
}
