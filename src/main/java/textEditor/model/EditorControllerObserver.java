package textEditor.model;


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
//        Platform.runLater(new UpdateTextWrapper(observable));
        new Thread(new UpdateTextWrapper(observable)).start();
//        Platform.runLater(new UpdateStyleWrapper(observable));
        new Thread(new UpdateStyleWrapper(observable)).start();
    }

    @Override
    public synchronized void update(RemoteObservable observable, RemoteObservable.UpdateTarget target) throws RemoteException {
        if (target == RemoteObservable.UpdateTarget.ONLY_TEXT) {
            System.out.println("LOG LOG ");
//            Platform.runLater(new UpdateTextWrapper(observable));
            new Thread(new UpdateTextWrapper(observable)).start();
            System.out.println("LOG LOG 123213312312123");
        }

        if (target == RemoteObservable.UpdateTarget.ONLY_STYLE) {
            new Thread(new UpdateStyleWrapper(observable)).start();
//            Platform.runLater(new UpdateStyleWrapper(observable));
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
            System.out.println("UpdateTextWrapper run");
            isUpdating.set(true);
            System.out.println(textArea == null ? "\t\ttextarea is null " : "\t\ttextarea not null" );
            int oldCaretPosition = textArea.getCaretPosition();
            System.out.println("UpdateTextWrapper getCaretPosition");
            String oldText = textArea.getText();
            System.out.println("UpdateTextWrapper getText");
            try {
                String newText = ((EditorModel) observable).getTextString();
                System.out.println("newText " + newText);
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
            if(newStyle == null) {
                System.out.println("INNA OOODUPA");
            }

            if(newStyle.getParagraphStyles() == null) {
                System.out.println("INNA DUPA ");

            } else {
                System.out.println("newStyle.getParagraphStyles() " + newStyle.getParagraphStyles());
            }

            List<List<String>> paragraphStyles = newStyle.getParagraphStyles();
            for (int i = 0; i < paragraphStyles.size(); ++i) {
                textArea.setParagraphStyle(i, paragraphStyles.get(i));
            }
        }

        private void updateStyleSpans(StylesHolder newStyle) {
            if(newStyle == null) {
                System.out.println("OOODUPA");
            }

            if(textArea == null) {
                System.out.println("textarea null");
            } else {
                System.out.println("textarea not null");
            }

            if(newStyle.getStyleSpans() == null) {
                System.out.println("DUPA ");

            } else {
                System.out.println("newStyle.getStylesStart() " + newStyle.getStylesStart());
            }

            if(newStyle.getStyleSpans() == null) {
                System.out.println("DUPA UDPAUDPAUPUADP UPDAP UD");
            } else {
                System.out.println("newStyle.getStyleSpans() " + newStyle.getStyleSpans());
            }
            textArea.setStyleSpans(newStyle.getStylesStart(), newStyle.getStyleSpans());
        }
    }
}