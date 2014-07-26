package agent.util;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URISyntaxException;

/**
 * Created by jason on 7/25/14.
 */
public
class Utils {
    public static
    void loadAgent(Class<?> c) {
        try {
            loadAgent(JarGenerator.makeBootJar(c).getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static
    void loadAgent(String agentPath) {
        try {
            VirtualMachine vm = VirtualMachine.attach(getProcessID());
            vm.loadAgent(agentPath, "");
            vm.detach();
        } catch (AttachNotSupportedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AgentInitializationException e) {
            e.printStackTrace();
        } catch (AgentLoadException e) {
            e.printStackTrace();
        }
    }

    public static
    String getProcessID() {
        return pid(ManagementFactory.getRuntimeMXBean().getName());
    }

    static
    String pid(String vmName) {
        int p = vmName.indexOf('@');
        return vmName.substring(0, p);
    }


    public static
    String cp(Class c) {
        return '/' + c.getName().replace('.', '/') + ".class";
    }

    public static
    boolean isLoadedAsJar() throws URISyntaxException {
        //jar::
        // jar:file:/Users/jason/IdeaProjects/Field/instrumentation-rt/build/libs/instrumentation-rt-1.0.jar!/agent/util/Utils.class
        //class::
        // /Users/jason/IdeaProjects/Field/instrumentation-rt/build/classes/main/agent/util/Utils.class
        String relPath = cp(Utils.class);
        String fullPath = Utils.class.getResource(relPath).getPath();
        String basePath = fullPath.substring(0, fullPath.length() - relPath.length());
        return basePath.endsWith("jar!");
    }
}
