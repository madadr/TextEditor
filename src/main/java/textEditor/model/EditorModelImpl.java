package textEditor.model;

import org.fxmisc.richtext.model.StyleSpans;

import java.rmi.RemoteException;
import java.util.Observable;
import java.util.Observer;

public class EditorModelImpl extends Observable implements EditorModel {
    private String text = "";
    private StyleSpans<String> styleSpans;

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
    public synchronized void setTextStyle(int from, StyleSpans<String> styleSpans) {
        if(styleSpans != null) {
            System.out.println("Updating style to to:");
            System.out.println("\t" + styleSpans);
            this.styleSpans = styleSpans;
            notifyObservers();
        }
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