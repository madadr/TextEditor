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

    private HashMap<Project, Pair<String, EditorModel>> projectEditorModelBinding;
    private Pair<String, ActiveUserHandler> activeUserHandlerBinding;

    public ProjectManagerImpl(Registry registry) {
        this.registry = registry;

        projectEditorModelBinding = new HashMap<>();
        activeUserHandlerBinding = null;
    }

    @Override
    public String getEditorModelId(Project project) throws RemoteException {
        if (!this.projectEditorModelBinding.containsKey(project)) {
            EditorModel model = createEditorModel(project);
            String modelId = "EditorModel" + project.getId();

            EditorModel modelExport = (EditorModel) UnicastRemoteObject.exportObject(model, 0);
            registry.rebind(modelId, modelExport);
            this.projectEditorModelBinding.put(project, new Pair<String, EditorModel>(modelId, model));
        }

        Pair<String, EditorModel> model = this.projectEditorModelBinding.get(project);

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

    @Override
    public String getActiveUserHandlerId(Project project) throws RemoteException {
        if (this.activeUserHandlerBinding == null) {
            ActiveUserHandler handler = new ActiveUsersHandlerImpl();
            String handlerId = "ActiveHandler";

            ActiveUserHandler handlerExport = (ActiveUserHandler) UnicastRemoteObject.exportObject(handler, 0);
            registry.rebind(handlerId, handlerExport);
            this.activeUserHandlerBinding = new Pair<String, ActiveUserHandler>(handlerId, handler);
        }

        return activeUserHandlerBinding.getKey();
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
