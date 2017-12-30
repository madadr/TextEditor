package textEditor.model;

import textEditor.controller.EditorController;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RemoteObserverImpl extends UnicastRemoteObject implements RemoteObserver {
    private RemoteObserver observer;

    public RemoteObserverImpl(EditorController.EditorControllerObserver controller) throws RemoteException {
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
