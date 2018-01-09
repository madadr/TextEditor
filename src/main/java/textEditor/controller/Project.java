package textEditor.controller;

import java.util.ArrayList;
import java.util.List;

public class Project {
    private String title;
    private String description;
    private List<String> contributors;

    // these two will be available via dbService query
//    private String text;
//    private StyleSpansWrapper styleSpansWrapper;

    public Project() {
        this.title = "";
        this.description = "";
        this.contributors = new ArrayList<>();
    }

    public Project(String title, String description, List<String> contributors) {//, String text, StyleSpansWrapper styleSpansWrapper) {
        this.title = title;
        this.description = description;
        this.contributors = contributors;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getContributors() {
        return contributors;
    }

    public void setContributors(List<String> contributors) {
        this.contributors = contributors;
    }
}
