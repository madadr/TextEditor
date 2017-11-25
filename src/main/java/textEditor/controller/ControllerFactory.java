package textEditor.controller;

import javafx.util.Callback;
import textEditor.RMIClient;

public class ControllerFactory implements Callback<Class<?>, Object> {
    private RMIClient rmiClient;

    public ControllerFactory(RMIClient rmiClient) {
        this.rmiClient = rmiClient;
    }

    @Override
    public Object call(Class<?> p) {
        Object controller = null;
        try {
            controller = p.newInstance();

            if (controller instanceof ClientInjectionTarget) {
                ((ClientInjectionTarget) controller).injectClient(rmiClient);
            }
            return controller;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}
