package field.bytecode.protect.trampoline;

import field.bytecode.protect.FastClassLoader;
import field.bytecode.protect.Notable;
import field.core.Platform;
import field.namespace.generic.ReflectionTools;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

/**
 * Created by jason on 7/14/14.
 */
public class TrampolineClassLoader extends FastClassLoader {
    private final TrampolineInstrumentation instrumentation;

    ClassLoader deferTo;
    final Stack<String> loading = new Stack<String>();
    String indentation = "";


    private Method findLoadedClass_method1;

    HashMap<String, Class> previous = new HashMap<String, Class>();
    HashSet<String> alreadyFailed = new HashSet<String>();

    HashMap<String, Class> already = new HashMap<String, Class>();
    private List<String> ignored = getDefaultIgnored();
    private List<String> allowed = getDefaultAllowed();

    public TrampolineClassLoader(URL[] u, ClassLoader loader, TrampolineInstrumentation instrumentation) {
        super(u, loader);
        this.instrumentation = instrumentation;
    }




    static List<String> getDefaultIgnored() {
        return getDefaultIgnored(true);
    }
    static List<String> getDefaultIgnored(boolean mutate) {

        return mutate ? new ArrayList<String>(DEFAULT_IGNORED) : DEFAULT_IGNORED;
    }

    static List<String> getDefaultAllowed() {
        return getDefaultAllowed(false);
    }

    static List<String> getDefaultAllowed(boolean mutate) {

        return mutate ? new ArrayList<String>(DEFAULT_ALLOWED) : DEFAULT_ALLOWED;
    }

    public boolean shouldLoadLocal(String s) {

        s = s.replace('/', '.');
        boolean failed = false;
        for (String root : ignored) {
            if (s.startsWith(root))
                failed = true;
        }

        if (s.contains(".protect"))
            failed = true;

        if (failed)
            for (String root : allowed)
                if (s.contains(root))
                    failed = false;

        return !failed;
    }

    public Class<?> _defineClass(String name, byte[] b, int off, int len) throws ClassFormatError {
        Class<?> name2 = super.defineClass(name, b, off, len);
        name2.getDeclaredMethods();
        already.put(name, name2);
        return name2;

        // callDefineClass(getParent(), name, b, off, l)
    }

    @Override
    public void addURL(URL url) {

        super.addURL(url);

        String oldCP = System.getProperty("java.class.path");
        oldCP += ":" + url.getFile();
        System.setProperty("java.class.path", oldCP);
    }

    public Set<Class> getAllLoadedClasses() {
        try {
            HashSet<Class> al = new HashSet<Class>();
            al.addAll(previous.values());
            al.addAll(already.values());

            Vector vThere = (Vector) ReflectionTools.illegalGetObject(deferTo, "classes");
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

    @Override
    protected Class<?> findClass(String arg0) throws ClassNotFoundException {
        // ;//System.out.println("ZZZZZ find class <" + arg0 +
        // ">");
        return super.findClass(arg0);
    }

    private Class callDefineClass(ClassLoader parent, String class_name, byte[] bytes, int i, int length) {

        try {
            Method cc = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, Integer.TYPE, Integer.TYPE);
            cc.setAccessible(true);
            return (Class) cc.invoke(parent, class_name, bytes, i, length);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;

    }

    protected Class checkHasBeenLoaded(String s) {
        try {
            Class c = already.get(s);
            if (c != null)
                return c;

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

    @Override
    protected String findLibrary(String rawName) {

        // System.out.println("####\n\n looking for <" + rawName
        // + "> \n\n #############");
        if (Platform.isMac()) {
            String name = "lib" + rawName + ".dylib";

//				System.out.println(extendedLibraryPaths);
            for (String s : Trampoline2.extendedLibraryPaths) {
                File file = new File(s, name);
                if (file.exists()) {
                    // System.out.println(" found it <"
                    // + file + ">");
                    return file.getAbsolutePath();
                }
            }
            for (String s : Trampoline2.extendedClassPaths) {
                File file = new File(s, name);
                if (file.exists()) {
                    // System.out.println(" found it <"
                    // + file + ">");
                    return file.getAbsolutePath();
                }
            }
        }
        if (Platform.isLinux()) {
            String name = "lib" + rawName + ".so";

            for (String s : Trampoline2.extendedLibraryPaths) {
                File file = new File(s, name);
                if (file.exists()) {
//						System.out.println(" found it <" + file + ">");
                    return file.getAbsolutePath();
                }
            }
            for (String s : Trampoline2.extendedClassPaths) {
                File file = new File(s, name);
                if (file.exists()) {
//						System.out.println(" found it <" + file + ">");
                    return file.getAbsolutePath();
                }
            }
        }
        return super.findLibrary(rawName);
    }

    LinkedHashSet<String> knownPackages = new LinkedHashSet<String>();

    @Override
    synchronized protected Class<?> loadClass(String class_name, boolean resolve) throws ClassNotFoundException {

        System.out.println(" load :" + class_name);

        if (alreadyFailed.contains(class_name))
            throw new ClassNotFoundException(class_name);

        deferTo = getParent();
        try {
            ClassNotFoundException classNotFound = null;

            loading.push(class_name);
            try {
                if (Trampoline2.debug) {
                    // System.out.println(indentation
                    // + "? entered " +
                    // class_name + " " +
                    // resolve);
                    indentation += " ";
                }

                Class loaded = previous.get(class_name);
                if (loaded == null)
                    if (!shouldLoadLocal(class_name)) {
                        try {
                            loaded = getParent().loadClass(class_name);
                        } catch (ClassNotFoundException ex) {
                            classNotFound = ex;
                            if (Trampoline2.debug)
                                ;// System.out.println(ANSIColorUtils.red("-- class not found <"
                            // +
                            // class_name
                            // +
                            // ">"));
                        }
                    }
                if (loaded == null) {
                    loaded = checkHasBeenLoaded(class_name);
                }

                if (classNotFound == null)
                    if (loaded == null) {
                        deferTo = getParent();
                        byte[] bytes = instrumentation.instrumentClass(this, class_name);

                        if (bytes != null) {

                            if (class_name.lastIndexOf(".") != -1) {
                                String packageName = class_name.substring(0, class_name.lastIndexOf("."));
                                if (!knownPackages.contains(packageName)) {
                                    try {
                                        definePackage(packageName, null, null, null, null, null, null, null);
                                    } catch (IllegalArgumentException e) {
                                        // e.printStackTrace();
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
                                // ;//System.out.println("
                                // >>
                                // about
                                // to
                                // resolve
                                // <"+class_name+">
                                // <"+resolve+">");
                                if (resolve)
                                    resolveClass(loaded);
                                previous.put(class_name, loaded);
                            } else {
                                // System.out.println(" loaded <"
                                // +
                                // class_name
                                // +
                                // "> in RS classloader");
                            }
                        }
                        // ;//System.out.println("
                        // >>
                        // loaded
                        // <"+class_name+">");
                    }
                if (classNotFound == null)
                    if (loaded == null) {
                        try {
                            loaded = Class.forName(class_name);

                            // recent change
                            previous.put(class_name, loaded);

                        } catch (ClassNotFoundException ex) {
                            classNotFound = ex;
                            if (Trampoline2.debug) {
                                // System.out.println(ANSIColorUtils.red("-- class not found <"
                                // +
                                // class_name
                                // +
                                // ">"));
                                ex.printStackTrace();
                            }
                        }
                    }
                if (Trampoline2.debug) {
                    indentation = indentation.substring(1);
                    // System.out.println(indentation
                    // + "?" + class_name +
                    // " complete");
                    // assert
                    // popped.equals(class_name);
                }
                if (classNotFound != null) {
                    if (Trampoline2.debug) {
                        System.err.println("exception (" + classNotFound.getClass() + "): while trying to load <" + class_name + " / <" + loading + ">");
                        new Exception().printStackTrace();
                    }
                    alreadyFailed.add(class_name);

                    throw classNotFound;
                }

                already.put(class_name, loaded);

                if (loaded.isAnnotationPresent(Notable.class)) {

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

    private static final List<String> DEFAULT_IGNORED = Arrays.asList(
            "apple.", "java.", "javax.", "sun.", "com.apple", "apple.",
            "field.namespace", "field.math", "field.launch.",
            "org.objectweb", "com.sun", "org.xml", "org.w3c", "$Prox", "org.eclipse",
            "main", "field.util.BetterWeak", "field.misc.ANSIColorUtils",
            "ch.rand", "org.python", "org.apache.batik", "org.antlr",
            "field.util.TaskQueue", "com.lowagie", "net.sf.cglib.proxy",
            "com.seaglasslookandfeel", "org.pushingpixels", "net.sourceforge.napkinlaf.",
            "com.kenai.jaffl");
    private static final List<String> DEFAULT_ALLOWED = Arrays.asList(
            "phobos", "com.sun.script.", "com.sun.scenario", "com.sun.stylesheet",
            "com.sun.opengl", "com.sun.gluegen", "javax.media.opengl",
            "javax.media.nativewindow", "javax.jmdns");
}
