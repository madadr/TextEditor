package textEditor.controller.targetInjections;

import textEditor.model.interfaces.Project;

public interface ProjectInjectionTarget {
    void injectProject(Project project);
}
