package textEditor.model;

import textEditor.controller.EditorModelData;
import textEditor.controller.EditorModelDataImpl;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class EditorModelImpl implements Serializable, EditorModel, RemoteObservable {
    private String text = "";
    private StyleSpansWrapper styleSpans;

    private transient ArrayList<RemoteObserver> observers;

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
            notifyObservers(UpdateTarget.ONLY_TEXT);
        }
    }

    @Override
    public synchronized void setTextString(String text, RemoteObserver source) throws RemoteException {
        if (text != null) {
            RemoteObserver skippedObserver = source;
            this.deleteObserver(skippedObserver);

            System.out.println("Updating text to:");
            System.out.println("\t" + text);
            this.text = text;
            notifyObservers(UpdateTarget.ONLY_TEXT);

            this.addObserver(skippedObserver);
        }
    }

    @Override
    public String getTextString() throws RemoteException {
        return this.text;
    }

    @Override
    public synchronized void setTextStyle(StyleSpansWrapper styleSpans) throws RemoteException {
        if (styleSpans != null) {
            System.out.println("Updating style to:");
            System.out.println("\t" + styleSpans);
            this.styleSpans = styleSpans;
            notifyObservers(UpdateTarget.ONLY_STYLE);
        }
    }

    @Override
    public synchronized void setTextStyle(StyleSpansWrapper styleSpans, RemoteObserver source) throws RemoteException {
        if (styleSpans != null) {
            RemoteObserver skippedObserver = source;
            this.deleteObserver(skippedObserver);

            System.out.println("Updating style to:");
            System.out.println("\t" + styleSpans);
            this.styleSpans = styleSpans;
            notifyObservers(UpdateTarget.ONLY_STYLE);

            this.addObserver(skippedObserver);
        }
    }

    @Override
    public synchronized StyleSpansWrapper getTextStyle() throws RemoteException {
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
    public synchronized void notifyObservers(UpdateTarget target) throws RemoteException {
        List<RemoteObserver> invalidObservers = new ArrayList<>();
        for (RemoteObserver observer : observers) {
            System.out.println("observer update: " + observer);
            try {
                observer.update(this, target);
            } catch (RemoteException e) {
                System.out.println("\tError. Invalid observer, it will be removed!");
                invalidObservers.add(observer);
            }
        }

        invalidObservers.forEach(obs -> {
            try {
                deleteObserver(obs);
            } catch (RemoteException ignored) {
                // cannot remove observer but nothing to do here?
            }
        });
    }

    @Override
    public EditorModelData getData() throws RemoteException {
        return new EditorModelDataImpl(this.text, this.styleSpans);
    }
}