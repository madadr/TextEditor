package textEditor.controller;


import javafx.application.Platform;
import org.fxmisc.richtext.StyleClassedTextArea;
import textEditor.model.EditorModel;
import textEditor.model.RemoteObservable;
import textEditor.model.RemoteObserver;
import textEditor.model.StyleSpansWrapper;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.concurrent.atomic.AtomicBoolean;

public class EditorControllerObserver implements Serializable, RemoteObserver {
    private transient StyleClassedTextArea textArea;

    // Flag for avoiding cycling dependencies during updates after observer event.
    // Helps to determine if update event was initialized by this client/observer.
    private AtomicBoolean isUpdating = new AtomicBoolean(false);

    public EditorControllerObserver(StyleClassedTextArea textArea) {
        this.textArea = textArea;
    }

    private class UpdateTextWrapper implements Runnable {
        private RemoteObservable observable;

        public UpdateTextWrapper(RemoteObservable observable) {
            this.observable = observable;
        }

        @Override
        public void run() {
            int oldCaretPosition = textArea.getCaretPosition();
            String oldText = textArea.getText();
            try {
                String newText = ((EditorModel) observable).getTextString();
                textArea.replaceText(newText);
                int newCaretPosition = calculateNewCaretPosition(oldCaretPosition, oldText, newText);
                textArea.moveTo(newCaretPosition);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private class UpdateStyleWrapper implements Runnable {
        private RemoteObservable observable;

        public UpdateStyleWrapper(RemoteObservable observable) {
            this.observable = observable;
        }

        @Override
        public void run() {
            try {
                StyleSpansWrapper newStyle = ((EditorModel) observable).getTextStyle();
                if (newStyle != null && newStyle.getStyleSpans() != null) {
                    textArea.setStyleSpans(newStyle.getStylesStart(), newStyle.getStyleSpans());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (IndexOutOfBoundsException e) {
                System.out.println("CRASH");
            }
        }
    }

    @Override
    public void update(RemoteObservable observable) throws RemoteException {
        isUpdating.set(true);
        Platform.runLater(new UpdateTextWrapper(observable));
        Platform.runLater(new UpdateStyleWrapper(observable));
        isUpdating.set(false);
    }

    @Override
    public synchronized void update(RemoteObservable observable, RemoteObservable.UpdateTarget target) throws RemoteException {
        isUpdating.set(true);

        if (target == RemoteObservable.UpdateTarget.ONLY_TEXT) {
            Platform.runLater(new UpdateTextWrapper(observable));
        }

        if (target == RemoteObservable.UpdateTarget.ONLY_STYLE) {
            Platform.runLater(new UpdateStyleWrapper(observable));
        }

        isUpdating.set(false);
    }

    // issues when using redo/undo actions as clients can undo another client operations
    private int calculateNewCaretPosition(int oldCaretPosition, String oldText, String newText) {
        if (newText.length() == 0) {
            return 0;
        }

        int indexOfTextBeforeCaret = newText.indexOf(oldText.substring(0, oldCaretPosition));
        if (indexOfTextBeforeCaret != -1) {
            return oldCaretPosition;
        }

        int indexOfTextAfterCaret = newText.indexOf(oldText.substring(oldCaretPosition, oldText.length()));
        if (indexOfTextAfterCaret != -1) {
            return indexOfTextAfterCaret;
        }

        return findFirstDifferenceIndex(oldText, newText);
    }

    private int findFirstDifferenceIndex(String oldText, String newText) {
        int longestLength = oldText.length() > newText.length() ? oldText.length() : newText.length();

        for (int i = 0; i < longestLength; ++i) {
            if (oldText.charAt(i) != newText.charAt(i)) {
                return i;
            }
        }

        return 0;
    }

    public ReadOnlyBoolean getIsUpdating() {
        return new ReadOnlyBoolean(isUpdating);
    }
}