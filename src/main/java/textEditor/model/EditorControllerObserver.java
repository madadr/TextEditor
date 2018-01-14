package textEditor.model;


import javafx.application.Platform;
import org.fxmisc.richtext.StyleClassedTextArea;
import textEditor.model.interfaces.EditorModel;
import textEditor.model.interfaces.RemoteObservable;
import textEditor.model.interfaces.RemoteObserver;
import textEditor.utils.ReadOnlyBoolean;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class EditorControllerObserver implements Serializable, RemoteObserver {
    private transient StyleClassedTextArea textArea;

    // Flag for avoiding cycling dependencies during updates after observer event.
    // Helps to determine if update event was initialized by this client/observer.
    private AtomicBoolean isUpdating = new AtomicBoolean(false);

    public EditorControllerObserver(StyleClassedTextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void update(RemoteObservable observable) throws RemoteException {
        Platform.runLater(new UpdateTextWrapper(observable));
        Platform.runLater(new UpdateStyleWrapper(observable));
    }

    @Override
    public synchronized void update(RemoteObservable observable, RemoteObservable.UpdateTarget target) throws RemoteException {
        if (target == RemoteObservable.UpdateTarget.ONLY_TEXT) {
            Platform.runLater(new UpdateTextWrapper(observable));
        }

        if (target == RemoteObservable.UpdateTarget.ONLY_STYLE) {
            Platform.runLater(new UpdateStyleWrapper(observable));
        }
    }

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

    private class UpdateTextWrapper implements Runnable {
        private RemoteObservable observable;

        public UpdateTextWrapper(RemoteObservable observable) {
            System.out.println("UpdateTextWrapper ctro");
            this.observable = observable;
        }

        @Override
        public void run() {
            isUpdating.set(true);
            int oldCaretPosition = textArea.getCaretPosition();
            String oldText = textArea.getText();
            try {
                String newText = ((EditorModel) observable).getTextString();
                textArea.replaceText(newText);
                int newCaretPosition = calculateNewCaretPosition(oldCaretPosition, oldText, newText);
                textArea.moveTo(newCaretPosition);
            } catch (RemoteException e) {
                e.printStackTrace();
            } finally {
                isUpdating.set(false);
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
            StylesHolder newStyle = null;
            try {
                newStyle = ((EditorModel) observable).getTextStyle();

                System.out.println(newStyle.getStyleSpans() == null ? "TO TU JEST PROBLEM " : "TO NIE TU JEST PROBLEM ");

                if (newStyle != null && newStyle.getStyleSpans() != null) {
                    System.out.println("not null");
                    System.out.println("\t\t\t newStyle" + newStyle);
                    updateStyleSpans(newStyle);
                    updateParagraphs(newStyle);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        private void updateParagraphs(StylesHolder newStyle) {
            List<List<String>> paragraphStyles = newStyle.getParagraphStyles();
            for (int i = 0; i < paragraphStyles.size(); ++i) {
                textArea.setParagraphStyle(i, paragraphStyles.get(i));
            }
        }

        private void updateStyleSpans(StylesHolder newStyle) {
            textArea.setStyleSpans(newStyle.getStylesStart(), newStyle.getStyleSpans());
        }
    }
}