package field.util;

import field.launch.IUpdateable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * thread safe task queue
 */
public
class HashQueue implements IUpdateable {

    protected Map<Object, Task> live = new LinkedHashMap<Object, Task>();

    protected Object lock = new Object();

    public
    void update() {

        if (live.isEmpty()) return;
        Map<Object, Task> todo;
        synchronized (lock) {
            todo = live;
            live = new LinkedHashMap<Object, Task>();
        }
        for (Task t : todo.values()) {
            t.run();
        }
        todo.clear();
    }

    protected
    void addTask(Object key, Task task) {
        synchronized (lock) {
            live.put(key, task);
        }
    }

    public
    void removeTask(Object key) {
        synchronized (lock) {
            Task task = live.remove(key);
            if (task != null) task.remove();
        }
    }

    public abstract
    class Task {

        StackTraceElement[] alloc;
        private final Object key;

        public
        Task(Object key) {
            this.key = key;
            assert (alloc = new Exception().getStackTrace()) != null;
            HashQueue.this.addTask(key, this);
        }

        public abstract
        void run();

        /**
         * run methods can call this if they want to have another crack at it
         *
         * @param q
         */
        protected
        void recur() {
            addTask(key, this);
        }

        protected
        void remove() {
        }

        public
        String toString() {
            return "alloc at :" + Arrays.asList(alloc);
        }
    }


    public static
    class Gate extends Task {
        boolean con = true;

        private final IUpdateable up;

        private final boolean touch;

        public
        Gate(Object key, HashQueue in, IUpdateable up, boolean touch) {
            in.super(key);
            this.up = up;
            this.touch = touch;
            if (touch) con = false;
        }

        @Override
        public
        void run() {
            up.update();
            if (con) recur();
            if (touch) con = false;
        }

        public
        void goOn() {
            con = true;
        }

        public
        void dontGoOn() {
            con = false;
        }

    }

    public
    class Updateable extends Task {

        IUpdateable u;

        public
        Updateable(Object key, IUpdateable u) {
            super(key);
            this.u = u;
        }

        public
        void run() {
            u.update();
            recur();
        }
    }

    HashMap upMap = new HashMap();

    public
    void addUpdateable(Object key, IUpdateable updateable) {
        Updateable up = new Updateable(key, updateable);
        upMap.put(updateable, up);
    }

    public
    void removeUpdateable(IUpdateable view) {
        Updateable up = (Updateable) upMap.remove(view);
        up.remove();
    }

    public
    int getNumTasks() {
        synchronized (lock) {
            return live.size();
        }
    }

    public
    void clear() {
        synchronized (lock) {
            live.clear();
        }
    }
}