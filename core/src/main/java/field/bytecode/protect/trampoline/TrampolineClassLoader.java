package field.bytecode.protect.trampoline;

import field.bytecode.protect.Notable;
import field.namespace.generic.ReflectionTools;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by jason on 7/14/14.
 */
public
class TrampolineClassLoader extends BaseTrampolineClassLoader {
    private static final Logger log = Logger.getLogger(TrampolineClassLoader.class.getName());

    private final TrampolineInstrumentation instrumentation;

    ClassLoader deferTo;
    final Stack<String> loading = new Stack<String>();

    private Method findLoadedClass_method1;

    HashMap<String, Class> previous = new HashMap<String, Class>();
    HashSet<String> alreadyFailed = new HashSet<String>();

    HashMap<String, Class> already = new HashMap<String, Class>();

    private final InstrumentationFilter filter = InstrumentationFilter.getInstance();


    public
    TrampolineClassLoader(URL[] u, ClassLoader loader, TrampolineInstrumentation instrumentation) {
        super(u, loader);
        this.instrumentation = instrumentation;


    }


    @Deprecated
    public
    boolean shouldLoadLocal(String s) {

        s = s.replace('/', '.');
        boolean failed = false;
        for (String root : filter.ignored) {
            if (s.startsWith(root)) failed = true;
        }

        if (s.contains(".protect")) failed = true;

        if (failed) for (String root : filter.allowed)
            if (s.contains(root)) failed = false;

        return !failed;
    }

    public
    Class<?> _defineClass(String name, byte[] b, int off, int len) throws ClassFormatError {
        Class<?> name2 = super.defineClass(name, b, off, len);
        name2.getDeclaredMethods();
        already.put(name, name2);
        return name2;

        // callDefineClass(getParent(), name, b, off, l)
    }


    @SuppressWarnings("unchecked")
    public
    Set<Class> getAllLoadedClasses() {
        try {
            HashSet<Class> al = new HashSet<Class>();
            al.addAll(previous.values());
            al.addAll(already.values());

            Vector<Class> vThere = (Vector) ReflectionTools.illegalGetObject(deferTo, "classes");
            al.addAll(vThere);
            return al;
        } catch (ConcurrentModificationException e) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            return getAllLoadedClasses();
        }
    }


    protected
    Class checkHasBeenLoaded(String s) {
        try {
            Class c = already.get(s);
            if (c != null) return c;

            if (findLoadedClass_method1 == null) {
                findLoadedClass_method1 = ClassLoader.class.getDeclaredMethod("findLoadedClass", String.class);
                findLoadedClass_method1.setAccessible(true);
            }
            ClassLoader dt = getParent();
            while (dt != null) {
                Object r = findLoadedClass_method1.invoke(dt, s);
                if (r != null) {
                    // if (debug)
                    // System.out.println(" class <"
                    // + s +
                    // "> already loaded in class loader <"
                    // + dt + ">");
                    return (Class) r;
                }
                dt = dt.getParent();
            }
            return null;
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }


    LinkedHashSet<String> knownPackages = new LinkedHashSet<String>();

    @Override
    protected synchronized
    Class<?> loadClass(String class_name, boolean resolve) throws ClassNotFoundException {


        if (alreadyFailed.contains(class_name)) throw new ClassNotFoundException(class_name);

        deferTo = getParent();
        try {
            ClassNotFoundException classNotFound = null;

            loading.push(class_name);
            try {

                Class loaded = previous.get(class_name);
                if (loaded == null) if (!shouldLoadLocal(class_name)) {
                    try {
                        loaded = getParent().loadClass(class_name);
                    } catch (ClassNotFoundException ex) {
                        classNotFound = ex;
                        log.log(Level.WARNING, "class {0} not found", class_name);
                    }
                }
                if (loaded == null) {
                    loaded = checkHasBeenLoaded(class_name);
                }

                if (classNotFound == null) if (loaded == null) {
                    deferTo = getParent();
                    byte[] bytes = instrumentation.instrumentClass(this, class_name);

                    if (bytes != null) {

                        if (class_name.lastIndexOf('.') != -1) {
                            String packageName = class_name.substring(0, class_name.lastIndexOf('.'));
                            if (!knownPackages.contains(packageName)) {
                                try {
                                    definePackage(packageName, null, null, null, null, null, null, null);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                knownPackages.add(packageName);
                            }
                        }

                        loaded = Trampoline2.reloadingSupport.delegate(class_name, bytes);

                        if (loaded == null) {

                            try {
                                loaded = defineClass(class_name, bytes, 0, bytes.length);
                            } catch (LinkageError le) {
                                le.printStackTrace();
                                return null;
                            }

                            if (resolve) resolveClass(loaded);
                            previous.put(class_name, loaded);
                        }
                    }

                }
                if (classNotFound == null) if (loaded == null) {
                    try {
                        loaded = Class.forName(class_name);

                        // recent change
                        previous.put(class_name, loaded);

                    } catch (ClassNotFoundException ex) {
                        classNotFound = ex;
                        log.log(Level.WARNING, "?!", ex);
                    }
                }

                if (classNotFound != null) {

                    log.log(Level.WARNING,
                            "exception ("
                            + classNotFound.getClass()
                            + "): while trying to load <"
                            + class_name
                            + " / <"
                            + loading
                            + '>');

                    alreadyFailed.add(class_name);

                    throw classNotFound;
                }

                already.put(class_name, loaded);

                if (loaded.isAnnotationPresent(Notable.class)) {
                    log.info(" CLASS IS NOTABLE :" + loaded + ' ' + Trampoline2.notifications);
                    // System.out.println(" CLASS IS NOTABLE :"+loaded+" "+notifications);

                    for (ClassLoadedNotification n : Trampoline2.notifications) {
                        n.notify(loaded);
                    }
                }

                return loaded;
            } finally {
                String popped = loading.pop();
            }
        } catch (ClassNotFoundException e) {
            throw e;
        } catch (Throwable t) {
            t.printStackTrace();
            // System.out.println(" unexpected trouble loading <"
            // + loading + ">");
            return null;
        }

    }

}
