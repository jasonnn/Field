package field.bytecode.protect;

import java.io.*;
import java.net.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

public class FastClassLoader extends URLClassLoader {
    private static final Logger log = Logger.getLogger(FastClassLoader.class.getName());

    public FastClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
    }

    public FastClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public FastClassLoader(URL[] urls) {
        super(urls);
    }

    volatile Map<String, String> a = new LinkedHashMap<String, String>();
    long maphash = -1;
    long loadedmaphash = -1;

    {
        final String filename = System.getProperty("user.home") + "/Library/Application Support/Field" + "/classmap.xml";
        try {

            ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(new File(filename))));
            //noinspection unchecked
            a = (Map<String, String>) ois.readObject();
            ois.close();
            String o = a.get("__maphash__");
            if (o != null) {
                loadedmaphash = Long.parseLong(o);
            }
            log.info(" loaded classmap with <" + a + '>');
        } catch (Throwable x) {
            x.printStackTrace();
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {

                try {

                    Map<String, String> a = FastClassLoader.this.a;
                    FastClassLoader.this.a = new HashMap<String, String>();

                    a.put("__maphash__", String.valueOf(maphash));

                    ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(new File(filename))));
                    oos.writeObject(a);
                    oos.close();

                    log.info(" wrote :" + a.size() + " classmap");

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void addURL(URL url) {
        try {
            URI uri = url.toURI();
            super.addURL(url);
            maphash = 31 * maphash + uri.hashCode();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    boolean skipped = false;

    @Override
    public URL findResource(String name) {
        if (maphash == loadedmaphash) {
            String q = a.get(name);
            if (q != null) {
                try {
                    return new URL(q);
                } catch (MalformedURLException ignored) {
                }
            }

            if (a.containsKey(name))
                return null;
        } else {
            if (!skipped) {
                log.warning("WARNING: skipping cache, classpath is not final or correct <" + maphash + " " + loadedmaphash + ">. This is completely benign, but starting Field might take longer than usual.");
                skipped = true;
            }
        }
        // long a = System.nanoTime();
        URL o = super.findResource(name);
        // long b = System.nanoTime();

        {
            this.a.put(name, (o == null) ? null : o.toString());
        }
        return o;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        log.info(" find class <" + name + ">");
        return super.findClass(name);
    }

    @Override
    protected String findLibrary(String libname) {
        log.info(" find library <" + libname + ">");
        return super.findLibrary(libname);
    }

    @Override
    public Enumeration<URL> findResources(String name) throws IOException {
        log.info(" find resources <" + name + ">");
        return super.findResources(name);
    }

}
