package field.bytecode.protect.trampoline;

import field.launch.SystemProperties;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

/**
 * Created by jason on 7/16/14.
 */
public
class InstrumentationFilter {
    public static
    InstrumentationFilter getInstance() {
        return Singleton.INSTANCE;
    }

    static
    class Singleton {
        static final InstrumentationFilter INSTANCE = new InstrumentationFilter();
    }

    public final TreeSet<String> ignored;
    public final TreeSet<String> allowed;

    public
    boolean shouldIgnore(String name) {
        return find(ignored, name);
    }

    static
    boolean find(TreeSet<String> set, String query) {
        String lte = set.floor(query);
        return (lte != null) && query.startsWith(lte);
    }

    public
    boolean shouldAllow(String name) {
        return find(allowed, name);
    }

    InstrumentationFilter() {

        ignored = new TreeSet<String>(DEFAULT_IGNORED());
        allowed = new TreeSet<String>(DEFAULT_ALLOWED());


        String exceptions = SystemProperties.getProperty("trampolineExceptions", null);
        if (exceptions != null) {
            Collections.addAll(ignored, exceptions.split(":"));
        }


    }

    static
    List<String> DEFAULT_IGNORED() {
        return Arrays.asList("apple.",
                             "java.",
                             "javax.",
                             "sun.",
                             "com.apple",
                             "apple.",
                             "field.namespace",
                             "field.math",
                             "field.launch.",
                             "org.objectweb",
                             "com.sun",
                             "org.xml",
                             "org.w3c",
                             "$Prox",
                             "org.eclipse",
                             "main",
                             "field.util.BetterWeak",
                             "field.misc.ANSIColorUtils",
                             "ch.rand",
                             "org.python",
                             "org.apache.batik",
                             "org.antlr",
                             "field.util.TaskQueue",
                             "com.lowagie",
                             "net.sf.cglib.proxy",
                             "com.seaglasslookandfeel",
                             "org.pushingpixels",
                             "net.sourceforge.napkinlaf.",
                             "com.kenai.jaffl");
    }

    static
    List<String> DEFAULT_ALLOWED() {
        return Arrays.asList("phobos",
                             "com.sun.script.",
                             "com.sun.scenario",
                             "com.sun.stylesheet",
                             "com.sun.opengl",
                             "com.sun.gluegen",
                             "javax.media.opengl",
                             "javax.media.nativewindow",
                             "javax.jmdns");
    }
}
