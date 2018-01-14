package textEditor.model;

import javafx.util.Pair;
import textEditor.model.interfaces.*;

import java.io.*;
import java.net.URI;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class ProjectManagerImpl implements ProjectManager {
    public final static String PROJECTS_DIR = "project_model";
    public final static String PROJECTS_EXTENSION = ".txtfile";

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
            this.projectEditorModelBinding.put(project, new Pair<>(modelId, model));
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
        File file = new File(Paths.get(PROJECTS_DIR).toUri());
        if (file.mkdirs()) {
            System.out.println("Created main directory");
        }

        return Paths.get(PROJECTS_DIR, project.getId() + PROJECTS_EXTENSION).toUri();
    }

    @Override
    public String getActiveUserHandlerId(Project project) throws RemoteException {
        if (this.activeUserHandlerBinding == null) {
            ActiveUserHandler handler = new ActiveUsersHandlerImpl();
            String handlerId = "ActiveHandler";

            ActiveUserHandler handlerExport = (ActiveUserHandler) UnicastRemoteObject.exportObject(handler, 0);
            registry.rebind(handlerId, handlerExport);
            this.activeUserHandlerBinding = new Pair<>(handlerId, handler);
        }

        return activeUserHandlerBinding.getKey();
    }

    @Override
    public void saveProject(Project project) throws RemoteException {
        EditorModel model = projectEditorModelBinding.get(project).getValue();
        EditorModelData data = model.getData();

        saveProject(project, data);
    }

    @Override
    public void saveProject(Project project, EditorModelData data) {
        FileOutputStream fout = null;
        ObjectOutputStream oos = null;
        try {
            File file = new File(buildProjectUri(project));
            file.createNewFile();


            fout = new FileOutputStream(file, false);
            oos = new ObjectOutputStream(fout);
            oos.writeObject(data);
        } catch (IOException e) {
            System.err.println("Failed during saving file.");
        } finally {
            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public EditorModelData getEditorModelData(File modelFile) {
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
