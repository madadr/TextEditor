package textEditor.controller;

import java.rmi.Remote;
import java.util.List;

public interface Project extends Remote {

    public String getTitle();

    public void setTitle(String title);

    public String getDescription();

    public void setDescription(String description);

    public List<User> getContributors();

    public void setContributors(List<User> contributors);

    public void setId(Integer id);

    public Integer getId();
}
