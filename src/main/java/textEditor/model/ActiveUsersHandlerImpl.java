package textEditor.model;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ActiveUsersHandlerImpl implements Serializable, ActiveUserHandler {
    private HashMap<Integer, ArrayList<String>> activeUserInProjects;

    public ActiveUsersHandlerImpl() {
        System.out.println("Im here and im probably Acrive User Handler");
        activeUserInProjects = new HashMap<>();
    }

    @Override
    public void addUserToProject(int projectId, String userName) throws RemoteException {
        if(activeUserInProjects.containsKey(projectId) && !activeUserInProjects.get(projectId).contains(userName))
        {
            activeUserInProjects.get(projectId).add(userName);
        }
        else if (!activeUserInProjects.containsKey(projectId)){
            activeUserInProjects.put(projectId,new ArrayList<>(Arrays.asList(userName)));
        }
    }

    @Override
    public void removeUserToProject(int projectId, String userName) throws RemoteException {
        if (activeUserInProjects.containsKey(projectId)) {
            System.out.println("REMOVING user");
            activeUserInProjects.get(projectId).remove(userName);
        }
    }

    @Override
    public ArrayList<String> getActiveUserInProject(int projectId) throws RemoteException {
        if (activeUserInProjects.containsKey(projectId)) {
            return activeUserInProjects.get(projectId);
        }
        return null;
    }
}
