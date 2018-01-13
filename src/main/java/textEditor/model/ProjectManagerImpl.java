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

    @Override
    public EditorModel getEditorModel(Project project) throws RemoteException {
        if (!this.projectEditor.containsKey(project)) {
            this.projectEditor.put(project, createEditorModel(project));
        }

        return this.projectEditor.get(project);
    }

    private EditorModel createEditorModel(Project project) throws RemoteException {
        File modelFile = new File(buildProjectUri(project));

        EditorModelData data;

        boolean editorModelFile = modelFile.exists() && !modelFile.isDirectory();
        if (editorModelFile) {
            data = EditorModel.getEditorModelData(modelFile);
        } else {
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
