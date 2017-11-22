package textEditor.rmi;

import java.rmi.RemoteException;

public class RemoteObserver implements IRemoteObserver {
    @Override
    public void update(Object observable, Object text) throws RemoteException {
        System.out.println("impl");
    }

    @Override
    public void update2(Object text) throws RemoteException {
        System.out.println("text: " + text);
    }
}
