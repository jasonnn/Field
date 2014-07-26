package agent;

import agent.util.Utils;

import java.lang.instrument.Instrumentation;
import java.util.logging.Logger;

/**
 * Created by jason on 7/25/14.
 */
public
class MyAgent {
    public static
    Instrumentation getInstrumentation() {
        if (instrumentation == null) {
            Utils.loadAgent(MyAgent.class);
        }
        if (instrumentation == null) {
            throw new NullPointerException("?!");
        }
        return instrumentation;
    }

    private static final Logger log = Logger.getLogger(MyAgent.class.getName());

    private static volatile Instrumentation instrumentation;

    public static
    void premain(String agentArgs, Instrumentation inst) {
        MyAgent.instrumentation = inst;
        log.info("args: " + agentArgs + " inst: " + inst);
    }

    public static
    void agentmain(String agentArgs, Instrumentation inst) {
        MyAgent.instrumentation = inst;
        log.info("args: " + agentArgs + " inst: " + inst);
    }
}
