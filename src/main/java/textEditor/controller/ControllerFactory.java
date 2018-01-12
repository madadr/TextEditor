package textEditor.controller;

import javafx.util.Callback;
import textEditor.RMIClient;
import textEditor.view.WindowSwitcher;

public class ControllerFactory implements Callback<Class<?>, Object> {
    private RMIClient rmiClient;
    private WindowSwitcher switcher;
    private UserImpl user;
    private Project project;

    public ControllerFactory(RMIClient rmiClient, WindowSwitcher switcher, UserImpl user, Project project) {
        this.rmiClient = rmiClient;
        this.switcher = switcher;
        this.user = user;
        this.project = project;
    }

    @Override
    public Object call(Class<?> p) {
        Object controller = null;
        try {
            controller = p.newInstance();

            if (controller instanceof ClientInjectionTarget) {
                ((ClientInjectionTarget) controller).injectClient(rmiClient);
            }

            if (controller instanceof WindowSwitcherInjectionTarget) {
                ((WindowSwitcherInjectionTarget) controller).injectWindowSwitcher(switcher);
            }

            if (controller instanceof UserInjectionTarget) {
                ((UserInjectionTarget) controller).injectUser(user);
            }

            if (controller instanceof SelectedProjectInjectionTarget) {
                ((SelectedProjectInjectionTarget) controller).injectSelectedProject(project);
            }

            return controller;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}
