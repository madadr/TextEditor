package textEditor.rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RmiClient extends UnicastRemoteObject {//implements IRemoteObserver {
    protected RmiClient() throws RemoteException {
        super();
    }

    private static final long serialVersionUID = 1L;

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 4321);

            IRemoteObserver i = (IRemoteObserver) registry.lookup("xD");

            i.update2("xdd");

//            i.update();

//            RmiService remoteService = (RmiService) Naming.lookup("//localhost:9999/RmiService");
//
//            RmiClient client = new RmiClient();
//            remoteService.addObserver(client);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

//    @Override
//    public void update(Object observable, Object updateMsg)
//            throws RemoteException {
//        System.out.println("got message:" + updateMsg);
//    }
}