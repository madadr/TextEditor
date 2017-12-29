package textEditor.model;

import org.fxmisc.richtext.model.StyleSpans;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class EditorModelImpl implements EditorModel, RemoteObservable {
    private String text = "";
    private StyleSpans<String> styleSpans;

    private ArrayList<RemoteObserver> observers;

    public EditorModelImpl() throws RemoteException {
        System.out.println("EditorModelImpl::ctor");
        this.observers = new ArrayList<>();
    }

    @Override
    public synchronized void setTextString(String text) throws RemoteException {
        if (text != null) {
            System.out.println("Updating text to:");
            System.out.println("\t" + text);
            this.text = text;
            notifyObservers();
        }
    }

    @Override
    public String getTextString() throws RemoteException {
        return this.text;
    }

    @Override
    public synchronized void setTextStyle(int from, StyleSpans<String> styleSpans) throws RemoteException {
        if (styleSpans != null) {
            System.out.println("Updating style to to:");
            System.out.println("\t" + styleSpans);
            this.styleSpans = styleSpans;
            notifyObservers();
        }
    }

    @Override
    public synchronized StyleSpans<String> getTextStyle() throws RemoteException {
        return this.styleSpans;
    }

    @Override
    public synchronized void addObserver(RemoteObserver observer) throws RemoteException {
        if (observer == null) {
            System.out.println("addObserver: arg observer is null");
            return;
        }

        if (observers.contains(observer)) {
            System.out.println("addObserver: observer already added");
        }

        observers.add(observer);
        System.out.println("addObserver: observer added");
    }

    @Override
    public synchronized void deleteObserver(RemoteObserver observer) throws RemoteException {
        if (observer == null) {
            System.out.println("deleteObserver: arg observer is null");
            return;
        }

        if (!observers.contains(observer)) {
            System.out.println("deleteObserver: cannot delete observer; it isn't in the list");
        }

        observers.remove(observer);
        System.out.println("deleteObserver: observer deleted");
    }

    @Override
    public void deleteObservers() throws RemoteException {
        observers.clear();
    }

    @Override
    public synchronized void notifyObservers() throws RemoteException {
        System.out.println("notifyObservers");
        System.out.println("observers: " + observers);
        for (RemoteObserver observer : observers) {
            System.out.println("observer update: " + observer);
            try {
                observer.update(this);
            } catch (RemoteException e) {
                System.out.println("\tError, removing observer");
                deleteObserver(observer);
            }
        }
    }
}