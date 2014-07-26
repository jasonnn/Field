package agent.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * Created by jason on 7/25/14.
 */
public
class JarGenerator {

    /**
     * for post startup agents.
     * must point to a class with method:
     * public static void agentmain(String agentArgs, Instrumentation inst);
     * public static void agentmain(String agentArgs);
     */
    static
    boolean hasAgentMainMethod(Class<?> c) {
        for (Method m : c.getDeclaredMethods()) {
            if ("agentmain".equals(m.getName())) {
                if ((m.getModifiers() ^ (Modifier.PUBLIC + Modifier.STATIC)) == 0) {
                    Class[] params = m.getParameterTypes();
                    if (params.length == 1 && params[0] == String.class) {
                        return true;
                    }
                    else if (params.length == 2 && params[0] == String.class && params[1] == Instrumentation.class) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    static
    File makeBootJar(Class<?> agentmainCls) throws IOException {
        assert hasAgentMainMethod(agentmainCls);
        File jar = File.createTempFile(agentmainCls.getName().replace('.', '_'), ".jar");
        jar.deleteOnExit();

        AgentManifest manifest = new AgentManifest();
        manifest.getMainAttributes()
                .setManifestVersion("1.0")
                .setAgentClass(agentmainCls)
                .setCanRedefineClasses(true)
                .setCanReTransformClasses(true);

        JarOutputStream out = new JarOutputStream(new FileOutputStream(jar), manifest);
        out.close();
        return jar;
    }


    static
    class AgentManifest extends Manifest {
        @Override
        public
        AgentManifestAttrs getMainAttributes() {
            return new AgentManifestAttrs(super.getMainAttributes());
        }
    }

    static
    class AgentManifestAttrs extends Attributes {
        AgentManifestAttrs(Attributes delegate) {
            super(delegate);
            super.map = delegate;
        }

        public
        AgentManifestAttrs setManifestVersion(String versionString) {
            put(Attributes.Name.MANIFEST_VERSION, versionString);
            return this;
        }

        public
        AgentManifestAttrs setAgentClass(Class c) {
            return setAgentClass(c.getName());
        }

        public
        AgentManifestAttrs setAgentClass(String clsName) {
            put(new Name("Agent-Class"), clsName);
            return this;
        }

        public
        AgentManifestAttrs setCanRedefineClasses(boolean canRedefineClasses) {
            put(new Name("Can-Redefine-Classes"), String.valueOf(canRedefineClasses));
            return this;
        }

        public
        AgentManifestAttrs setCanReTransformClasses(boolean canReTransformClasses) {
            put(new Name("Can-Retransform-Classes"), String.valueOf(canReTransformClasses));
            return this;
        }
    }


}
