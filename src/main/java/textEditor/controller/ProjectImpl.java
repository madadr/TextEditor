package textEditor.controller;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class ProjectImpl implements Project, Serializable {
    private Integer id;
    private String title;
    private String description;
    private List<String> contributors;

    // these two will be available via dbService query
//    private String text;
//    private StyleSpansWrapper styleSpansWrapper;

    public ProjectImpl() throws RemoteException {
        this.id = -1;
        this.title = "";
        this.description = "";
        this.contributors = new ArrayList<>();
    }

    public ProjectImpl(Integer id, String title, String description, List<String> contributors) throws RemoteException {
        this.id = id;
        this.title = title;
        this.description = description;
        this.contributors = contributors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProjectImpl project = (ProjectImpl) o;

        if (id != null ? !id.equals(project.id) : project.id != null) return false;
        if (title != null ? !title.equals(project.title) : project.title != null) return false;
        if (description != null ? !description.equals(project.description) : project.description != null) return false;
        return contributors != null ? contributors.equals(project.contributors) : project.contributors == null;
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

    public void setId(Integer id) { this.id = id; }

    public Integer getId() { return id; }

    @Override
    public String toString() {
        return this.title;
    }
}
