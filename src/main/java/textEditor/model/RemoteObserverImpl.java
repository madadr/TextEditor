package textEditor.model;

import textEditor.controller.EditorController;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RemoteObserverImpl extends UnicastRemoteObject implements RemoteObserver {
    private RemoteObserver observer;

    public RemoteObserverImpl(EditorController.EditorControllerObserver controller) throws RemoteException {
        System.out.println("RemoteObserverImpl::ctor");
        this.observer = (RemoteObserver) controller;
    }

    @Override
    public void update(RemoteObservable observable) throws RemoteException {
        System.out.println("RemoteObserverImpl::update");
        observer.update(observable);
    }
}
