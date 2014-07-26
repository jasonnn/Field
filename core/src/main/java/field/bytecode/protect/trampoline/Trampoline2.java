package field.bytecode.protect.trampoline;

import field.bytecode.protect.ModificationCache;
import field.bytecode.protect.ReloadingSupport;
import field.bytecode.protect.security.Security;
import field.launch.Launcher;
import field.launch.SystemProperties;
import field.launch.iLaunchable;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

public
class Trampoline2 implements iLaunchable, TrampolineInstrumentation {
    private static final Logger log = Logger.getLogger(Trampoline2.class.getName());

    public static final List<ClassLoadedNotification> notifications = new ArrayList<ClassLoadedNotification>();

    public static final ModificationCache cache = new ModificationCache();

    public static ReloadingSupport reloadingSupport = new ReloadingSupport();

    public static Trampoline2 trampoline = null;
    public static HashSet<String> plugins = new LinkedHashSet<String>();

    public static final boolean debug = false;

    public static List<String> extendedClassPaths = new ArrayList<String>();

    static String classToLaunch;

    static {


        // new MiscNative().splashUp_safe();

        // TODO: 64 \u2014 need new property mechanism
        // String c =
        // NSUserDefaults.standardUserDefaults().stringForKey("main.class");
        // if (c == null)
        // {
        String c = SystemProperties.getProperty("main.class");
        log.info(" class to launch :" + c);

        classToLaunch = c;
    }

    public static
    void handle(Throwable e) {
        e.printStackTrace();
        if (SystemProperties.getIntProperty("exitOnException", 0) == 1
            || e instanceof Error
            || e.getCause() instanceof Error) System.exit(1);
    }


    public TrampolineClassLoader loader = null;


    public
    ClassLoader getClassLoader() {
        return loader;
    }

    public
    void addJar(String n) {
//		System.out.println(" add jar :" + n);
        try {
            loader.addURL(new URL("file://" + n));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public
    void addExtensionsDirectory(File path) {
        ClassPath classPath = ClassPath.getInstance();

        if (path.getName().endsWith("**"))
            path = new File(path.getAbsolutePath().substring(0, path.getAbsolutePath().length() - 2));

        if (path.exists()) {

            try {

//				System.out.println(" adding to loader <" + "file://" + path.getAbsolutePath() + "/" + ">");

                loader.addURL(new URL("file://" + path.getCanonicalPath() + '/'));

                // URL[] uu = loader.getURLs();
                // for(URL uuu : uu)
                // ;//System.out.println("     "+uuu);

            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            classPath.addClassPath(path.getAbsolutePath());
            //extendedClassPaths.add(path.getAbsolutePath());

            String[] jars = path.list(new FilenameFilter() {
                public
                boolean accept(File dir, String name) {
                    return (name.endsWith(".jar"));
                }
            });

            if (jars != null) for (String j : jars) {
                try {
                    loader.addURL(new URL("file://" + path.getCanonicalPath() + '/' + j));
                    classPath.addClassPath(path.getCanonicalPath() + '/' + j);
                    // extendedClassPaths.add(path.getCanonicalPath() + "/" + j);

                    JarFile m = new JarFile(new File(path.getCanonicalFile() + "/" + j));
                    Manifest manifest = m.getManifest();
                    if (manifest != null) {
                        String a = (String) manifest.getMainAttributes().get(new Attributes.Name("Field-PluginClass"));
                        // System.out.println(" jar <"
                        // + path +
                        // "> declares plugin <"
                        // + a + ">");
                        if (a != null) {
                            plugins.add(a);
                        }

                        injectManifestProperties(manifest);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
//			System.out.println(" checking <" + path + "> for natives ");
            File[] natives = path.listFiles(new FileFilter() {
                public
                boolean accept(File file) {
                    return file.getPath().endsWith(".dylib") || file.getPath().endsWith(".jnilib");
                }
            });
//			System.out.println(" found <" + natives.length + ">");
            for (File n : natives) {
                try {
                    // System.load(n.getAbsolutePath());
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }

            File[] dirs = path.listFiles(new FileFilter() {
                public
                boolean accept(File file) {
                    return file.isDirectory() && !file.getName().endsWith("_");
                }
            });
            for (File j : dirs) {
                // ;//System.out.println(" adding next dir <" +
                // j +
                // ">");
                addExtensionsDirectory(j);
                // try {
                // loader.addURL(new URL("file://" +
                // j.getAbsolutePath()));
                // extendedClassPaths.add(j.getAbsolutePath());
                // } catch (MalformedURLException e) {
                // e.printStackTrace();
                // }
            }

            File[] rawManifests = path.listFiles(new FileFilter() {
                public
                boolean accept(File file) {
                    return (file.getAbsolutePath().endsWith(".mf") && !prohibitExtension(file.getName()
                                                                                             .substring(0,
                                                                                                        file.getName()
                                                                                                            .length()
                                                                                                        - 3)))
                           || ((file.getAbsolutePath().endsWith(".mf_")) && alsoAcceptExtension(file.getName()
                                                                                                    .substring(0,
                                                                                                               file.getName()
                                                                                                                   .length()
                                                                                                               - 4)));
                }
            });
            for (File j : rawManifests) {
                // ;//System.out.println(" adding raw manifest <"
                // +
                // j + ">");
                try {
                    Manifest m = new Manifest(new BufferedInputStream(new FileInputStream(j)));
                    String aa = (String) m.getMainAttributes().get(new Attributes.Name("Field-RedirectionPath"));
                    // System.out.println(aa + " " + j);

                    if (aa != null && aa.endsWith("**")) {

                        addWildcardPath(aa);
                    }
                    else if (aa != null) {
                        for (String a : aa.split(":")) {
                            a = a.trim();
                            String fp = (new File(a).isAbsolute()
                                         ? new File(a).getAbsolutePath()
                                         : new File(j.getParent(), a).getAbsolutePath());
                            if (!classPath.getExtendedClassPath().contains(fp)) {
//  if (!extendedClassPaths.contains(fp)) {

                                if (!new File(fp).exists()) {
                                    log.log(Level.WARNING,
                                            " warning, path <"
                                            + new File(fp).getAbsolutePath()
                                            + ">added to classpath through Field-RedirectionPath inside extension "
                                            + j
                                            + " doesn't exist");
                                }
                                else {

                                    URL url = new URL("file://" + fp + (fp.endsWith(".jar") ? "" : "/"));

                                    // System.out.println(" adding url to main classloader <"
                                    // +
                                    // url
                                    // +
                                    // "> <"
                                    // +
                                    // new
                                    // File(url.getPath()).exists()
                                    // +
                                    // ">");

                                    loader.addURL(url);
                                    classPath.addClassPath(fp);
                                    //extendedClassPaths.add(fp);
                                }
                            }
                        }
                    }
                    else {
                    }
                    String b = (String) m.getMainAttributes().get(new Attributes.Name("Field-PluginClass"));
                    if (b != null) {
                        plugins.add(b);
                    }

                    injectManifestProperties(m);

                } catch (FileNotFoundException e) {
                    // TODO
                    // Auto-generated
                    // catch
                    // block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO
                    // Auto-generated
                    // catch
                    // block
                    e.printStackTrace();
                }
            }
        }
    }

    public
    void addWildcardPath(String aa) throws MalformedURLException {

        File dir = new File(aa.replace("**", ""));
        if (dir.exists()) {

            loader.addURL(new URL("file://" + dir.getAbsolutePath() + '/'));

            // extendedClassPaths.add(dir.getAbsolutePath());
            ClassPath.getInstance().addClassPath(dir.getAbsolutePath());
            log.info(" extending library path by :" + dir.getAbsolutePath());
            extendLibraryPath(dir.getAbsolutePath());

            String[] ll = dir.list(new FilenameFilter() {

                public
                boolean accept(File dir, String name) {
                    return name.endsWith(".jar");
                }
            });
            if (ll != null) for (String l : ll) {

                String fp = new File(dir.getAbsolutePath() + '/' + l).getAbsolutePath();

                URL url = new URL("file://" + fp + (fp.endsWith(".jar") ? "" : "/"));

                loader.addURL(url);
                ClassPath.getInstance().addClassPath(fp);
                //extendedClassPaths.add(fp);
            }
            File[] f = dir.listFiles(new FileFilter() {

                public
                boolean accept(File pathname) {
                    return pathname.isDirectory();
                }
            });
            if (f != null) {
                for (File ff : f) {
                    addExtensionsDirectory(ff);
                }
            }

//			System.out.println(" checking <" + dir + "> for natives ");
            File[] natives = dir.listFiles(new FileFilter() {
                public
                boolean accept(File file) {
                    return file.getPath().endsWith(".dylib") || file.getPath().endsWith(".jnilib");
                }
            });
//			System.out.println(" found <" + natives.length + ">");
            for (File n : natives) {
                try {
//					System.out.println(" preemptive load of <" + n + ">");
//					System.load(n.getAbsolutePath());
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
        else {
            log.log(Level.WARNING, " warning: wildcard path <" + aa + "> is not a directory or does not exist ");
        }
    }

    public
    void addWildcardPathRecursively(String aa) throws MalformedURLException {

        if (aa.contains("examples")) return;

        File dir = new File(aa.replace("**", ""));
        if (dir.exists()) {

            extendLibraryPath(dir.getAbsolutePath());

            String[] ll = dir.list(new FilenameFilter() {

                public
                boolean accept(File dir, String name) {
                    return name.endsWith(".jar");
                }
            });
            if (ll != null) for (String l : ll) {

                // System.out.println(" l = " + l);

                String fp = new File(dir.getAbsolutePath() + '/' + l).getAbsolutePath();

                URL url = new URL("file://" + fp + (fp.endsWith(".jar") ? "" : "/"));

                loader.addURL(url);
                ClassPath.getInstance().addClassPath(fp);
                //extendedClassPaths.add(fp);
            }
            File[] f = dir.listFiles(new FileFilter() {

                public
                boolean accept(File pathname) {
                    return pathname.isDirectory();
                }
            });
            if (f != null) {
                for (File ff : f) {
                    addWildcardPathRecursively(ff.getAbsolutePath());
                }
            }
        }
        else {
            log.log(Level.WARNING, " warning: wildcard path <" + aa + "> is not a directory or does not exist ");
        }
    }

    protected static
    boolean prohibitExtension(String substring) {
        String p = SystemProperties.getProperty("withoutExtensions", null);
        if (p == null) return false;

        String[] parts = p.split(":");
        for (String pp : parts) {
            if (pp.toLowerCase().equals(substring.toLowerCase())) return true;
        }
        return false;
    }

    protected static
    boolean alsoAcceptExtension(String substring) {
        String p = SystemProperties.getProperty("withExtensions", null);
        if (p == null) return false;

        String[] parts = p.split(":");
        for (String pp : parts) {
            if (pp.toLowerCase().equals(substring.toLowerCase())) return true;
        }
        return false;
    }

    //static public List<String> extendedLibraryPaths = new ArrayList<String>();

    private static
    void extendLibraryPath(String s) {
        ClassPath.getInstance().addLibraryPath(s);
        //extendedLibraryPaths.add(s);
        System.setProperty("java.library.path", System.getProperty("java.library.path") + File.pathSeparator + s);
        System.setProperty("jna.library.path", System.getProperty("jna.library.path") + File.pathSeparator + s);

//		System.out.println(" library paths now "+System.getProperty("java.library.path")+" and "+System.getProperty("jna.library.path"));
        // This enables the java.library.path to be modified at runtime
        // From a Sun engineer at
        // http://forums.sun.com/thread.jspa?threadID=707176

        try {
            Field field = ClassLoader.class.getDeclaredField("usr_paths");
            field.setAccessible(true);
            String[] paths = (String[]) field.get(null);
            for (String path : paths) {
                if (s.equals(path)) {
                    return;
                }
            }
            String[] tmp = new String[paths.length + 1];
            System.arraycopy(paths, 0, tmp, 0, paths.length);
            tmp[paths.length] = s;
            field.set(null, tmp);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Failed to get permissions to set library path");
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("Failed to get field handle to set library path");
        }

        File[] f = new File(s).listFiles();
        if (f != null) {
            for (File ff : f) {
                if (ff.getName().endsWith(".dylib") | ff.getName().endsWith(".so")) {
//					System.out.println(" premptivly loading <"+ff+">");
//					try{
//						System.load(ff.getAbsolutePath());
//					}
//					catch(Throwable t){System.out.println(" didn't load, continuing");}
                }
            }
        }

    }

    public static
    byte[] bytesForClass(java.lang.ClassLoader deferTo, String class_name) {

        System.out.println(" bytes for class :" + class_name);

        InputStream s = deferTo.getResourceAsStream(resourceNameForClassName(class_name));
        if (s == null) return null;

        // try to load it
        // here we might cache modification dates

        BufferedInputStream stream = new BufferedInputStream(s, 80000);
        try {
            byte[] a = new byte[stream.available()];
            stream.read(a);
            return a;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public
    byte[] instrumentClass(java.lang.ClassLoader deferTo, String class_name) {
        // if (debug)
        // System.out.println(" getResource <" + class_name +
        // "> <" +
        // deferTo.getResource(resourceNameForClassName(class_name))
        // + ">");

        InputStream s = deferTo.getResourceAsStream(resourceNameForClassName(class_name));
        if (s == null) return null;

        // try to load it
        // here we might cache modification dates

        //if (debug)
        // System.out.println(indentation + "#" +
        // (class_name.replace('.', File.separatorChar))
        // + ">");
        BufferedInputStream stream = new BufferedInputStream(s, 80000);
        try {
            byte[] a = new byte[stream.available()];
            int read = stream.read(a);
            //if (debug)
            // System.out.println(" about to instrument <"
            // + class_name + "> inside <" + this +
            // "> !! ");
            return instrumentBytecodes(a, class_name, deferTo);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    protected static
    String resourceNameForClassName(String class_name) {
        return class_name.replace('.', File.separatorChar) + ".class";
    }

    public
    void launch() {
        log.log(Level.INFO,
                "## trampoline <this: {0} loader: {1} >",
                new Object[]{this.getClass(), this.getClass().getClassLoader()});
        trampoline = this;

        String exceptions = SystemProperties.getProperty("trampolineExceptions", null);
//        ignored = new String[]{"apple.", "java.", "javax.", "sun.", "com.apple", "apple.", "field.namespace", "field.math", "field.launch.", "org.objectweb", "com.sun", "org.xml", "org.w3c", "$Prox", "org.eclipse", "main", "field.util.BetterWeak", "field.misc.ANSIColorUtils", "ch.rand", "org.python", "org.apache.batik", "org.antlr", "field.util.TaskQueue", "com.lowagie", "net.sf.cglib.proxy", "com.seaglasslookandfeel", "org.pushingpixels", "net.sourceforge.napkinlaf.", "com.kenai.jaffl"};
//        allowed = new String[]{"phobos", "com.sun.script.", "com.sun.scenario", "com.sun.stylesheet", "com.sun.opengl", "com.sun.gluegen", "javax.media.opengl", "javax.media.nativewindow", "javax.jmdns"};
//
//        if (exceptions != null) {
//
//            ArrayList<String> a = new ArrayList<String>(Arrays.asList(ignored));
//            a.addAll(Arrays.asList(exceptions.split(":")));
//            ignored = (String[]) a.toArray(ignored);
//        }

        loader = new TrampolineClassLoader(((URLClassLoader) this.getClass().getClassLoader()).getURLs(),
                                           (this.getClass().getClassLoader()),
                                           this);
        //System.setSecurityManager(new PermissiveSecurityManager());
        Security.getInstance().usePermissiveSecurityManager();

        String extendedJars = SystemProperties.getProperty("extendedJars", null);
        if (extendedJars != null) {
            String[] ex = extendedJars.split(":");
            for (String e : ex) {

                try {
                    loader.addJar(e);
                } catch (MalformedURLException e1) {
                    log.log(Level.WARNING, "failed to add jar: " + e, e1);
                }
            }
        }

        //   Vector v = (Vector) ReflectionTools.illegalGetObject(this.getClass().getClassLoader(), "classes");
        //if (debug)
        //     System.out.println(" already loaded all of <" + v +
        // ">");

        if (!"none".equals(System.getProperty("asserts", "none"))) loader.setDefaultAssertionStatus(true);

        Thread.currentThread().setContextClassLoader(loader);

        String extensionsDir = SystemProperties.getProperty("extensions.dir", "../../extensions/");
        Trampoline2.trampoline.addExtensionsDirectory(new File(extensionsDir));
        String extensionsDir2 = System.getProperty("user.home") + "/Library/Application Support/Field/extensions";

        if (!new File(extensionsDir2).exists()) new File(extensionsDir2).mkdirs();

        if (new File(extensionsDir2).exists()) Trampoline2.trampoline.addExtensionsDirectory(new File(extensionsDir2));

        try {
            // System.out.println(Arrays.asList(loader.getURLs()));
            //field.Blank2 is loaded
            final Class<?> c = (loader.loadClass(classToLaunch));
            // System.out.println(" c = " + c + " " +
            // c.getClassLoader() + " " + loader);
            try {
                Method main = c.getDeclaredMethod("main", String[].class);
                try {
                    main.invoke(null, new Object[]{null});
                    return;
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                return;
            } catch (SecurityException e) {
                e.printStackTrace();
                return;
            } catch (NoSuchMethodException ignored) {
            }

            Launcher.getLauncher().mainThread = Thread.currentThread();
            printInfo();
            Launcher.mainInstance = (iLaunchable) c.newInstance();
            printInfo();
            Launcher.mainInstance.launch();
            printInfo();
            printInfo();

        } catch (Throwable e) {
            e.printStackTrace();
            if (SystemProperties.getIntProperty("exitOnException", 0) == 1
                || e instanceof Error
                || e.getCause() instanceof Error) System.exit(1);
        }

        if (SystemProperties.getIntProperty("nosave", 0) == 1) Security.getInstance().useNoWriteSecurityManager();
            //System.setSecurityManager(new NoWriteSecurityManager());
        else if (SystemProperties.getIntProperty("collectResources", 0) == 1)
            Security.getInstance().useCollectResourcesSecurityManager();
            // System.setSecurityManager(new CollectResourcesSecurityManager());
        else Security.getInstance().useNoopSecurityManager();
        // System.setSecurityManager(new NoopSecurityManager());

    }

    public
    boolean shouldLoadLocal(String s) {

        return loader.shouldLoadLocal(s);
    }

    private static
    Set<Object> injectManifestProperties(Manifest manifest) {

        // System.out.println(" inject manifest properties ");

        Set<Object> ks = manifest.getMainAttributes().keySet();
        for (Object o : ks) {
            if (o instanceof Attributes.Name) {
                Attributes.Name an = (Attributes.Name) o;

                if (an.toString().startsWith("Field-Property-")) {
                    String prop = an.toString().substring("Field-Property-".length());

                    if (prop.startsWith("Append-")) {
                        prop = prop.substring("Append-".length());
                        prop = prop.replace("-", ".");

                        String pp = SystemProperties.getProperty(prop, null);
                        pp = (pp == null
                              ? pathify(manifest.getMainAttributes().getValue(an))
                              : (pp + ":" + pathify(manifest.getMainAttributes().getValue(an))));
                        SystemProperties.setProperty(prop, pp);

                        // System.out.println(" property <"
                        // + prop + "> now <" + pp +
                        // ">");

                    }
                    else {
                        SystemProperties.setProperty(prop, manifest.getMainAttributes().getValue(an));
                    }
                }
            }
        }
        return ks;
    }

    private static
    String pathify(String value) {
        try {
            if (new File(value).exists()) return new File(value).getCanonicalPath();
            else return value;
        } catch (Throwable t) {
            return value;
        }
    }

    private static
    void printInfo() {
        if (debug) {
            // System.out.println("/n/n");

            // Vector vThere = (Vector) ReflectionTools.illegalGetObject(deferTo, "classes");
            // System.out.println("local: " + loader.already);

            // for (int i = 0; i < vThere.size(); i++)
            // System.out.println("global:" + vThere.get(i)
            // + " " +
            // loader.already.containsValue(vThere.get(i)));
        }
    }

    protected static
    boolean check() {
        // if (!debug)
        return true;
//        Vector vThere = (Vector) ReflectionTools.illegalGetObject(deferTo, "classes");
//        for (int i = 0; i < vThere.size(); i++) {
//            String s = ((Class) vThere.get(i)).getName();
//            boolean failed = !shouldLoadLocal(s);
//            if (!failed) {
//                // System.out.println("illegally loaded class <"
//                // + s + "> <" + vThere + ">");
//                System.exit(1);
//            }
//        }
//        return true;
    }


    protected
    byte[] instrumentBytecodes(byte[] a, String class_name, java.lang.ClassLoader deferTo) {
        return a;
    }

    public
    Class<?> loadClass(String classname, byte[] b) {

        Class<?> c = loader._defineClass(classname, b, 0, b.length);
        return c;
    }

}
