package textEditor.model;

import org.fxmisc.richtext.model.StyleSpans;

import java.rmi.RemoteException;
import java.util.Observable;
import java.util.Observer;

public class EditorModelImpl extends Observable implements EditorModel {
    private String textAreaString = "";

    @Override
    public synchronized void setTextString(String value) throws RemoteException {
        // TODO: fix implementation
        textAreaString = value;
        if (!value.isEmpty()) {
            System.out.print(textAreaString.charAt(textAreaString.length() - 1));
            notifyObservers();
        }
    }

    @Override
    public synchronized void setTextStyle(int from, StyleSpans<String> styleSpans) {
        // TODO: add implementation
    }

    @Override
    public synchronized void addObserver(Observer o) {
        super.addObserver(o);
    }

    @Override
    public synchronized void deleteObserver(Observer o) {
        super.deleteObserver(o);
    }

    @Override
    public void notifyObservers() {
        super.notifyObservers();
    }

    @Override
    public synchronized void deleteObservers() {
        super.deleteObservers();
    }
}