package textEditor.model.interfaces;

import java.rmi.Remote;
import java.util.List;

public interface Project extends Remote {

    public String getTitle();

    public void setTitle(String title);

    public String getDescription();

    public void setDescription(String description);

    public List<String> getContributors();

    public void setContributors(List<String> contributors);

    public void setId(Integer id);

    public Integer getId();
}
