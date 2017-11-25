package textEditor.controller;

import javafx.util.Callback;
import textEditor.Client;

public class ControllerFactory implements Callback<Class<?>, Object> {
    private Client.RMIClient rmiClient;

    public ControllerFactory(Client.RMIClient rmiClient) {
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
