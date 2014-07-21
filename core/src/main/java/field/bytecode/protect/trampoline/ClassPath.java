package field.bytecode.protect.trampoline;

import field.util.Registration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by jason on 7/20/14.
 */
public class ClassPath {
    public static ClassPath getInstance() {
        return Singleton.INSTANCE;
    }

    static class Singleton {
        static final ClassPath INSTANCE = new ClassPath();
    }

    private final List<String> classPath = new CopyOnWriteArrayList<String>();
    private final List<String> libPath = new CopyOnWriteArrayList<String>();
    private final List<ClassPathListener> listeners = new ArrayList<ClassPathListener>(2);

    public Registration addClassPathListener(final ClassPathListener listener) {
        listeners.add(listener);
        return new Registration() {
            @Override
            public void remove() {
                removeClassPathListener(listener);
            }
        };
    }

    public void removeClassPathListener(ClassPathListener listener) {
        listeners.remove(listener);
    }

    public void addLibraryPath(String path) {
        libPath.add(path);
    }

    public void addClassPath(String path) {
        classPath.add(path);
        for (ClassPathListener listener : listeners) {
            listener.pathAdded(path);
        }
    }

    public List<String> getExtendedLibraryPaths() {
        return libPath;
    }

    public List<String> getExtendedClassPath() {
        return classPath;
    }
}
