package textEditor.controller.inject;

import textEditor.model.interfaces.Project;

public interface ProjectInjectionTarget {
    void injectProject(Project project);
}
