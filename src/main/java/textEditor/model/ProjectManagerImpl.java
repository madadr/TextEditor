package textEditor.model;

import javafx.util.Pair;
import textEditor.model.interfaces.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URI;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

import static textEditor.utils.Const.Files.PROJECTS_PATH;

public class ProjectManagerImpl implements ProjectManager {
    private Registry registry;

    private HashMap<Project, Pair<String, EditorModel>> projectEditor;
    private HashMap<Project, Pair<String, ActiveUserHandler>> projectActive;

    public ProjectManagerImpl(Registry registry) {
        this.registry = registry;

        projectEditor = new HashMap<>();
        projectActive = new HashMap<>();
    }

    @Override
    public String getEditorModelId(Project project) throws RemoteException {
        if (!this.projectEditor.containsKey(project)) {
            EditorModel model = createEditorModel(project);
            String modelId = "EditorModel" + project.getId();

            EditorModel modelExport = (EditorModel) UnicastRemoteObject.exportObject(model, 0);
            registry.rebind(modelId, modelExport);
            this.projectEditor.put(project, new Pair<String, EditorModel>(modelId, model));
        }

        Pair<String, EditorModel> model = this.projectEditor.get(project);

        return model.getKey();
    }

    private EditorModel createEditorModel(Project project) throws RemoteException {
        File modelFile = new File(buildProjectUri(project));

        EditorModelData data;

        boolean editorModelFile = modelFile.exists() && !modelFile.isDirectory();
        if (editorModelFile) {
            data = getEditorModelData(modelFile);
        } else {
            data = new EditorModelDataImpl();
        }

        return new EditorModelImpl(data);
    }

    private URI buildProjectUri(Project project) {
        return Paths.get(PROJECTS_PATH, project.getId() + ".model").toUri();
    }

//    @Override
//    public ActiveUserHandler getActiveUserHandler(Project project) {
//        if (!projectActive.containsKey(project)) {
//            projectActive.put(project, new ActiveUsersHandlerImpl());
//        }
//
//        return projectActive.get(project);
//    }

    @Override
    public String getActiveUserHandlerId(Project project) throws RemoteException {
//        if (!projectActive.containsKey(project)) {
//            projectActive.put(project, new ActiveUsersHandlerImpl());
//        }
//
//        return projectActive.get(project);

        if (!this.projectActive.containsKey(project)) {
//            EditorModel model = createEditorModel(project);
            ActiveUserHandler handler = new ActiveUsersHandlerImpl();
            String handlerId = "ActiveHandler" + project.getId();

            ActiveUserHandler handlerExport = (ActiveUserHandler) UnicastRemoteObject.exportObject(handler, 0);
            registry.rebind(handlerId, handlerExport);
            this.projectActive.put(project, new Pair<String, ActiveUserHandler>(handlerId, handler));
        }

        Pair<String, ActiveUserHandler> handler = this.projectActive.get(project);

        return handler.getKey();
    }

    // TODO: find better place for that method
    private EditorModelData getEditorModelData(File modelFile) {
        ObjectInputStream ois = null;
        try {
            FileInputStream inputStream = new FileInputStream(modelFile);
            ois = new ObjectInputStream(inputStream);
            return (EditorModelData) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Failed");
            return null;
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
