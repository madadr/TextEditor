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
    private HashMap<Project, ActiveUserHandler> projectActive;

    public ProjectManagerImpl(Registry registry) {
        this.registry = registry;

        projectEditor = new HashMap<>();
        projectActive = new HashMap<>();
    }

    @Override
    public String getEditorModelId(Project project) throws RemoteException {
        System.out.println("getEditorModelId");
        if (!this.projectEditor.containsKey(project)) {
            System.out.println("Project " + project.getId() + " isn't in editorMap!");
            EditorModel model = createEditorModel(project);
            String modelId = "EditorModel" + project.getId();

            System.out.println("\tmodel " + model);
            System.out.println("\tmodelId " + modelId);

            EditorModel modelExport = (EditorModel) UnicastRemoteObject.exportObject(model, 0);
            System.out.println("exported");
            registry.rebind(modelId, modelExport);
            System.out.println("bound");

//            registry.rebind(modelId, model);


            this.projectEditor.put(project, new Pair<String, EditorModel>(modelId, model));
        } else {
            System.out.println("Project " + project.getId() + " IS in editorMap!");
        }

        Pair<String, EditorModel> model = this.projectEditor.get(project);

        System.out.println("\tmodel " + model.getValue());
        System.out.println("\t\tmodel.getTextString() " + model.getValue().getTextString());
        System.out.println("\tmodelId " + model.getKey());

        return model.getKey();
    }

    private EditorModel createEditorModel(Project project) throws RemoteException {
        System.out.println("createEditorModel");
        File modelFile = new File(buildProjectUri(project));

        EditorModelData data;

        boolean editorModelFile = modelFile.exists() && !modelFile.isDirectory();
        System.out.println("\tchecked if exists");
        if (editorModelFile) {
            System.out.println("\tgetting new data");
            data = getEditorModelData(modelFile);
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
