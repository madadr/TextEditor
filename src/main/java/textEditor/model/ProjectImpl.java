package textEditor.model;

import textEditor.model.interfaces.Project;
import textEditor.model.interfaces.User;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class ProjectImpl implements Project, Serializable {
    private Integer id;
    private String title;
    private String description;
    private List<User> contributors;

    public ProjectImpl() throws RemoteException {
        System.out.println("ProjectImpl::ctor ");
        this.id = -1;
        this.title = "";
        this.description = "";
        this.contributors = new ArrayList<>();
    }

    public ProjectImpl(Integer id, String title, String description, List<User> contributors) throws RemoteException {
        System.out.println("ProjectImpl::ctor with args");
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

        return id != null ? id.equals(project.id) : project.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
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

    public List<User> getContributors() {
        return contributors;
    }

    public void setContributors(List<User> contributors) {
        this.contributors = contributors;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return this.title;
    }
}
