package field.util;

import field.launch.iUpdateable;

import java.util.HashMap;
import java.util.Map;


public
class SimpleHashQueue extends TaskQueue {

    Map<Object, Task> tokens = new HashMap<Object, Task>();

    @Override
    public
    void update() {
        super.update();
        synchronized (lock) {
            tokens.clear();
        }
    }

    public
    Task queueSingleUpdate(Object token, iUpdateable u) {
        synchronized (lock) {

            if (tokens.containsKey(token)) return tokens.get(token);
            Task t = super.queueSingleUpdate(u);
            tokens.put(token, t);
            return t;
        }
    }
}
