package textEditor.rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Observable;
import java.util.Observer;

public class RmiServer extends Observable implements RmiService {

    private class WrappedObserver implements Observer {
        private String text;
        private IRemoteObserver ro = null;

        public WrappedObserver(IRemoteObserver ro) {
            this.ro = ro;
        }

        @Override
        public void update(Observable o, Object arg) {
            try {
                ro.update(o.toString(), arg);
            } catch (RemoteException e) {
                System.out.println("Remote exception removing observer:" + this);
                o.deleteObserver(this);
            }
        }
    }

    @Override
    public void addObserver(IRemoteObserver o) throws RemoteException {

    }

    public static void main(String[] args) {
        Registry registry = null;
        try {
            registry = LocateRegistry.createRegistry(4321);

            IRemoteObserver obs = new RemoteObserver();

            IRemoteObserver iobs = (IRemoteObserver) UnicastRemoteObject.exportObject(obs, 0);

            registry.rebind("xD", iobs);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
