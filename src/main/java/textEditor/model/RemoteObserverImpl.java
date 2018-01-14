package textEditor.model;

import textEditor.model.interfaces.RemoteObservable;
import textEditor.model.interfaces.RemoteObserver;

import java.io.Serializable;
import java.rmi.RemoteException;

public class RemoteObserverImpl implements RemoteObserver, Serializable {
    private RemoteObserver observer;

    public RemoteObserverImpl(EditorControllerObserver controller) throws RemoteException {
        this.observer = (RemoteObserver) controller;
    }

    @Override
    public void update(RemoteObservable observable) throws RemoteException {
        observer.update(observable);
    }

    @Override
    public void update(RemoteObservable observable, RemoteObservable.UpdateTarget target) throws RemoteException {
        observer.update(observable, target);
    }
}
