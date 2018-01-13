package textEditor;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIClient {
    private Registry registry;

    public RMIClient() {
        try {
            registry = LocateRegistry.getRegistry("localhost", 4321);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public Remote getModel(String modelName) throws RemoteException, NotBoundException {

        return registry.lookup(modelName);
    }
}