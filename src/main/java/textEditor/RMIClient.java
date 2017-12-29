package textEditor;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIClient {
    private Registry registry;

    public RMIClient() {
        System.out.println("RMIClient::ctor start");
        try {
            registry = LocateRegistry.getRegistry("localhost", 4321);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        System.out.println("RMIClient::ctor end");

    }

    public Remote getModel(String modelName) {
        try {
            return registry.lookup(modelName);
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}