package agent.util;

import agent.MyAgent;
import org.junit.Test;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public
class UtilsTest {
    @Test
    public
    void testLoadAgent() throws Exception {
        assertNotNull(MyAgent.getInstrumentation());
    }

    static final Comparator<Class> CLS_NAME_COMPARATOR = new Comparator<Class>() {
        @Override
        public
        int compare(Class o1, Class o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };


    @Test
    public
    void testIsLoadedAsJar() throws Exception {
        assertFalse(Utils.isLoadedAsJar());
    }


    @Test
    public
    void testClassLoaderSpelunking() throws Exception {
        ClassLoader thisLoader = UtilsTest.class.getClassLoader();
        ClassLoader systemLoader = ClassLoader.getSystemClassLoader();
        ClassLoader threadLoader = Thread.currentThread().getContextClassLoader();
        List<ClassLoader> loaderPath = loaderPath(thisLoader);

        //     System.out.println(Arrays.asList(thisLoader, systemLoader, threadLoader, loaderPath));

    }

    @Test
    public
    void testDumpInstrumentInfo() throws Exception {
        Instrumentation i = MyAgent.getInstrumentation();
        System.out.println("i.isRedefineClassesSupported() = " + i.isRedefineClassesSupported());
        System.out.println("i.isRedefineClassesSupported() = " + i.isRedefineClassesSupported());
        System.out.println("i.isNativeMethodPrefixSupported() = " + i.isNativeMethodPrefixSupported());

    }

    static
    List<ClassLoader> loaderPath(ClassLoader child) {
        List<ClassLoader> loaders = new ArrayList<ClassLoader>();
        for (ClassLoader l = child; l != null; l = l.getParent()) {
            loaders.add(l);
        }
        return loaders;
    }
}