package field.bytecode.protect.trampoline;

import field.bytecode.protect.FastClassLoader;
import field.core.Platform;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLStreamHandlerFactory;
import java.util.logging.Logger;

/**
 * Created by jason on 7/20/14.
 */
public class BaseTrampolineClassLoader extends FastClassLoader {
    private static final Logger log = Logger.getLogger(BaseTrampolineClassLoader.class.getName());

    public BaseTrampolineClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
    }

    public BaseTrampolineClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public BaseTrampolineClassLoader(URL[] urls) {
        super(urls);
    }

    public void addJar(String path) throws MalformedURLException {
        URL url = new URL("file://" + path);
        log.info("adding url: " + url + " to classloader");
        addURL(url);
    }

    @Override
    public void addURL(URL url) {

        super.addURL(url);

        String oldCP = System.getProperty("java.class.path");
        oldCP += ':' + url.getFile();
        System.setProperty("java.class.path", oldCP);
    }

    static String findLib(String name) {
        for (String s : ClassPath.getInstance().getExtendedLibraryPaths()) {
            File file = new File(s, name);
            if (file.exists()) {

                return file.getAbsolutePath();
            }
        }
        for (String s : ClassPath.getInstance().getExtendedClassPath()) {
            File file = new File(s, name);
            if (file.exists()) {
                return file.getAbsolutePath();
            }
        }
        return null;
    }

    @Override
    protected String findLibrary(String rawName) {
        String result = null;
        if (Platform.isMac()) {
            result = findLib("lib" + rawName + "dylib");
        } else if (Platform.isLinux()) {
            result = findLib("lib" + rawName + ".so");
        }
        if (result == null) {
            result = super.findLibrary(rawName);
        }
        log.info(rawName + " resolved to: " + String.valueOf(result));
        return result;
    }

    private static
    Class callDefineClass(ClassLoader parent, String class_name, byte[] bytes, int i, int length) {

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
}
