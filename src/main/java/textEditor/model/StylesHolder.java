package textEditor.model;


import javafx.util.Pair;
import org.fxmisc.richtext.model.Paragraph;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.collection.LiveList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class StylesHolder implements Serializable {
    private int stylesStart;
    private ArrayList<Pair<Integer, ArrayList<String>>> pairsOfLengthAndStyle;
    private ArrayList<ArrayList<String>> paragraphStyles;

    public StylesHolder(int from, StyleSpans<Collection<String>> styleSpans,
                        LiveList<Paragraph<Collection<String>, String, Collection<String>>> paragraphs) {
        this.stylesStart = from;
        this.pairsOfLengthAndStyle = new ArrayList<>(styleSpans.length());
        this.paragraphStyles = new ArrayList<>(paragraphs.size());

        styleSpans.forEach(styleSpan -> {
            this.pairsOfLengthAndStyle.add(new Pair<>(styleSpan.getLength(), new ArrayList<>(styleSpan.getStyle())));
        });

        paragraphs.forEach(paragraph -> {
            this.paragraphStyles.add(new ArrayList<>(paragraph.getParagraphStyle()));
        });

        System.out.println(this.pairsOfLengthAndStyle);
    }

    public int getStylesStart() {
        return this.stylesStart;
    }

    public StyleSpans<Collection<String>> getStyleSpans() {
        StyleSpansBuilder<Collection<String>> builder = new StyleSpansBuilder<>(pairsOfLengthAndStyle.size());

        this.pairsOfLengthAndStyle.forEach(pair -> {
            builder.add(pair.getValue(), pair.getKey());
        });

        return builder.create();
    }

    public void setStyleSpans(int from, StyleSpans<Collection<String>> styleSpans) {
        this.stylesStart = from;
        this.pairsOfLengthAndStyle = new ArrayList<>(styleSpans.length());

        styleSpans.forEach(styleSpan -> {
            this.pairsOfLengthAndStyle.add(new Pair<>(styleSpan.getLength(), new ArrayList<>(styleSpan.getStyle())));
        });
    }

    public LiveList<Paragraph<Collection<String>, String, Collection<String>>> getParagraphLiveList() {
        // TODO: add implementation
        LiveList<Paragraph<Collection<String>, String, Collection<String>>> liveList = null;

        return liveList;
    }

    public void setParagraphLiveList(LiveList<Paragraph<Collection<String>, String, Collection<String>>> paragraphs) {
        this.paragraphStyles = paragraphStyles;
    }
}