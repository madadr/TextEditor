package textEditor.utils;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.IndexRange;
import javafx.scene.control.ToggleButton;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.model.Paragraph;
import org.fxmisc.richtext.model.StyleSpan;
import org.fxmisc.richtext.model.StyleSpans;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static textEditor.utils.ConstValues.*;

public class TextFormatter {
    private StyleClassedTextArea textArea;

    private HashMap<String, Pattern> patternHashMap;

    public TextFormatter(StyleClassedTextArea textArea) {
        this.textArea = textArea;
        compileAllPatterns();
    }

    //*****************************************************STYLERS**************************************************************
    //this method serve to style selection, by using styleSpans
    public void styleSelectedArea(String newValue, String patternKey) {
        IndexRange range = textArea.getSelection();

        StyleSpans<Collection<String>> newSpans = textArea.getStyleSpans(range).mapStyles(currentStyle -> {
            List<String> currentStyles = new ArrayList<>(currentStyle);
            Pattern pattern = patternHashMap.get(patternKey);
            currentStyles.removeIf(s -> (pattern.matcher(s).matches()));
            currentStyles.add(patternKey + newValue);
            return currentStyles;
        });
        textArea.setStyleSpans(range.getStart(), newSpans);

        textArea.requestFocus();
    }

    //This method style textarea paragraph by using styleSpan
    //patternKeys array should always have on first place prefix to style!!!!
    public void styleSelectedParagraph(IndexRange paragraphRange, String newValue, ArrayList<String> patternKeys) {
        int currentParagraphIndex = paragraphRange.getStart();
        while (currentParagraphIndex <= paragraphRange.getEnd()) {
            Paragraph<Collection<String>, String, Collection<String>> currentParagraph = textArea.getParagraph(currentParagraphIndex);
            StyleSpans<Collection<String>> newStyles = currentParagraph.getStyleSpans().mapStyles(currentStyle -> {
                List<String> currentStyles = new ArrayList<>(currentStyle);
                //Remove all coresponding styles from paragraph
                for (String patternKey : patternKeys) {
                    Pattern pattern = patternHashMap.get(patternKey);
                    currentStyles.removeIf(s -> !(pattern.matcher(s).matches()));
                }
                currentStyles.add(patternKeys.get(0) + newValue);
                return currentStyles;
            });
            textArea.setStyleSpans(currentParagraphIndex, 0, newStyles);

            currentParagraphIndex++;
        }
    }

    public void styleParagraphs(IndexRange paragraphRange, ToggleButton toggleButton, String style) {
        if (toggleButton.isSelected()) {
            for (int paragraph = paragraphRange.getStart(); paragraph < paragraphRange.getEnd() + 1; paragraph++) {
                textArea.setParagraphStyle(paragraph, Collections.singleton(style));
            }
        } else {

            for (int paragraph = paragraphRange.getStart(); paragraph < paragraphRange.getEnd() + 1; paragraph++) {
                textArea.setParagraphStyle(paragraph, Collections.singleton(ALIGN_LEFT));
            }
        }

    }

    public void clearHighlight(IndexRange indexRange) {
        if (indexRange.getStart() > -1) {
            StyleSpans<Collection<String>> newSpans = textArea.getStyleSpans(indexRange).mapStyles(currentStyle -> {
                List<String> currentStyles = new ArrayList<>(currentStyle);
                currentStyles.removeIf(s -> Pattern.compile("highlight").matcher(s).find());
                return currentStyles;
            });
            textArea.setStyleSpans(indexRange.getStart(), newSpans);
        }
    }

    public void addHighlight(IndexRange indexRange) {
        if (indexRange.getStart() > -1) {
            StyleSpans<Collection<String>> newSpans = textArea.getStyleSpans(indexRange).mapStyles(currentStyle -> {
                List<String> currentStyles = new ArrayList<>(currentStyle);
                currentStyles.add("highlight");
                return currentStyles;
            });
            textArea.setStyleSpans(indexRange.getStart(), newSpans);
        }
    }

    //this method make paragraphs in range a part of bulletList, also include reversing
    public void applyBulletList(IndexRange paragraphRange, String newValue) {
        if (newValue.equals(" ")) {
            return;
        }

        int currentParagraph = paragraphRange.getStart();
        while (currentParagraph <= paragraphRange.getEnd()) {
            Paragraph<Collection<String>, String, Collection<String>> paragraph = textArea.getParagraph(currentParagraph);
            String paragraphText = textArea.getText(currentParagraph);
            StyleSpans<Collection<String>> currentParagraphStyles = textArea.getStyleSpans(currentParagraph);

            paragraphText = listPrefix(!paragraphText.matches("-.+"), paragraphText, newValue.equals(BULLET_LIST));
            if (newValue.equals(BULLET_LIST) && paragraphText.matches("-.+")) {
                textArea.replaceText(currentParagraph, 0, currentParagraph, paragraph.length(), paragraphText);

                ArrayList<String> stylesInFirstSpan = new ArrayList<>(currentParagraphStyles.getStyleSpan(0).getStyle());
                String bulletListStyle = findStyleElement(patternHashMap.get(FONTSIZE_PATTERN_KEY), stylesInFirstSpan);

                currentParagraphStyles = currentParagraphStyles.prepend(new StyleSpan<>(new ArrayList<>(Arrays.asList(bulletListStyle)), 2));
            } else {
                textArea.replaceText(currentParagraph, 0, currentParagraph, paragraph.length(), paragraphText);
                currentParagraphStyles = currentParagraphStyles.subView(2, paragraph.length());
            }
            textArea.setStyleSpans(currentParagraph, 0, currentParagraphStyles);
            ++currentParagraph;
        }
    }

    //*****************************************************FOLOWERS**************************************************************
    public void styleSpanFollower(ToggleButton button, String newValue, String patternKey) {
        if (newValue.equals("")) {
            button.setSelected(false);
            return;
        }

        boolean isWholeStyled = true;
        Pattern pattern = patternHashMap.get(patternKey);

        IndexRange range = textArea.getSelection();
        StyleSpans<Collection<String>> stylesInSelection = textArea.getStyleSpans(range);
        for (StyleSpan<Collection<String>> currentStyle : stylesInSelection) {
            String stylesToSearch = currentStyle.getStyle().toString();
            if (!pattern.matcher(stylesToSearch).find()) {
                isWholeStyled = false;
            }
        }
        button.setSelected(isWholeStyled);
    }

    public void styleSpanFollower(ChoiceBox<String> box, ChangeListener<? super String> listener, String patternKey, String defaultValue) {
        box.getSelectionModel().selectedItemProperty().removeListener(listener);

        IndexRange range = textArea.getSelection();
        StyleSpans<Collection<String>> styleSpans = textArea.getStyleSpans(range);
        Pattern pattern = patternHashMap.get(patternKey);

        ArrayList<String> currentStyles = new ArrayList<>(styleSpans.getStyleSpan(0).getStyle());
        currentStyles.removeIf(s -> !(pattern.matcher(s).matches()));

        if (styleSpans.getSpanCount() == 1) {
            if (currentStyles.isEmpty()) {
                box.setValue(defaultValue);
            } else {
                String actualValue = currentStyles.get(0);
                actualValue = actualValue.replace(patternKey, "");
                box.setValue(actualValue);
            }
        } else {
            box.setValue(" ");
        }
        //listener handling is now raised
        box.getSelectionModel().selectedItemProperty().addListener(listener);
    }

    //TODO: THIS SHOULD INCLUDE SOME Default value protection setted to LEFT but only when others styles are not apply
    //TODO: I DONT HAVE IDEA HOW TO MAKE IT
    public void paragraphStyleFollower(ToggleButton button, IndexRange paragraphRange, String value) {
        int currentParagraphIndex = paragraphRange.getStart();
        boolean isWholeStyled = true;
        while (currentParagraphIndex <= paragraphRange.getEnd()) {
            ArrayList<String> currentParagraphStyles = new ArrayList<>(textArea.getParagraph(currentParagraphIndex).getParagraphStyle());
            Matcher matcher = Pattern.compile(value).matcher(currentParagraphStyles.toString());
            if (!matcher.find()) {
                isWholeStyled = false;
            }
            currentParagraphIndex++;
        }
        button.setSelected(isWholeStyled);
    }

    public void bulletListFollower(ChoiceBox<String> box, ChangeListener<? super String> listener, IndexRange paragraphRange) {
        boolean isBulleted = false, isCombined = false;
        int currentParagraph = paragraphRange.getStart() + 1;

        box.getSelectionModel().selectedItemProperty().removeListener(listener);

        String firstParagraphText = textArea.getText(paragraphRange.getStart());
        box.setValue(BULLET_UNLIST);
        if (firstParagraphText.matches("-.+")) {
            isBulleted = true;
            box.setValue(BULLET_LIST);
        }

        while (currentParagraph <= paragraphRange.getEnd()) {
            String paragraphText = textArea.getText(currentParagraph);
            if (!isBulleted && paragraphText.matches("-.+")) {
                isCombined = true;
                break;
            } else if (isBulleted && !paragraphText.matches("-.+")) {
                isCombined = true;
                break;
            }
            currentParagraph++;
        }

        if (isCombined) {
            box.setValue(" ");
        }
        //listener handling is now raised
        box.getSelectionModel().selectedItemProperty().addListener(listener);
    }


    //*****************************************************PRIVATE METHOD SECTION**************************************************************
    private String findStyleElement(Pattern pattern, ArrayList<String> styles) {
        for (String style : styles) {
            if (style.matches(pattern.pattern())) {
                return style;
            }
        }

        return "";
    }

    private String listPrefix(boolean addPrefix, String text, boolean styling) {
        if (styling) {
            return (addPrefix) ? "- " + text : text;
        }

        return (addPrefix) ? text : text.replaceFirst("- ", "");

    }

    private void compileAllPatterns() {
        String matchFontSize = "fontsize\\d{1,2}px";
        String matchFontFamily = "fontFamily\\w{1,}";
        String matchFontColor = "color\\w{1,}";
        String matchParagraphHeading = "heading\\w{1,}";
        String matchBold = "weight.+";
        String matchItalic = "style.+";
        String matchUnderscore = "decoration.+";
        String matchAlign = "alignment.+";

        Pattern fontSizePattern = Pattern.compile(matchFontSize);
        Pattern fontFamilyPattern = Pattern.compile(matchFontFamily);
        Pattern fontColorPattern = Pattern.compile(matchFontColor);
        Pattern paragraphHeadingPattern = Pattern.compile(matchParagraphHeading);
        Pattern boldPattern = Pattern.compile(matchBold);
        Pattern italicPattern = Pattern.compile(matchItalic);
        Pattern underscorePattern = Pattern.compile(matchUnderscore);
        Pattern alignPattern = Pattern.compile(matchAlign);

        patternHashMap = new HashMap<>();
        patternHashMap.put(FONTSIZE_PATTERN_KEY, fontSizePattern);
        patternHashMap.put(FONTFAMILY_PATTERN_KEY, fontFamilyPattern);
        patternHashMap.put(FONTCOLOR_PATTERN_KEY, fontColorPattern);
        patternHashMap.put(HEADING_PATTERN_KEY, paragraphHeadingPattern);
        patternHashMap.put(BOLD_PATTERN_KEY, boldPattern);
        patternHashMap.put(ITALIC_PATTERN_KEY, italicPattern);
        patternHashMap.put(UNDERSCORE_PATTERN_KEY, underscorePattern);
        patternHashMap.put(ALIGN_PATTERN_KEY, alignPattern);
    }
}
