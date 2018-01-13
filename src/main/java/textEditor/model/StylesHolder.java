package textEditor.model;


import javafx.util.Pair;
import org.fxmisc.richtext.model.Paragraph;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.collection.LiveList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StylesHolder implements Serializable {
    private int stylesStart;
    private List<Pair<Integer, List<String>>> pairsOfLengthAndStyle;
    private List<List<String>> paragraphStyles;

    public StylesHolder(int from, StyleSpans<Collection<String>> styleSpans,
                        LiveList<Paragraph<Collection<String>, String, Collection<String>>> paragraphs) {
        this.stylesStart = from;
        this.pairsOfLengthAndStyle = new ArrayList<>(styleSpans.length());
        this.paragraphStyles = new ArrayList<>(paragraphs.size());

        setStyleSpans(from, styleSpans);

        setParagraphStyles(paragraphs);

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

    public List<List<String>> getParagraphStyles() {
        return paragraphStyles;
    }

    public void setParagraphStyles(List<List<String>> paragraphStyles) {
        this.paragraphStyles = paragraphStyles;
    }

    public void setParagraphStyles(LiveList<Paragraph<Collection<String>, String, Collection<String>>> paragraphStyles) {
        paragraphStyles.forEach(paragraph -> {
            this.paragraphStyles.add(new ArrayList<>(paragraph.getParagraphStyle()));
        });
    }
}