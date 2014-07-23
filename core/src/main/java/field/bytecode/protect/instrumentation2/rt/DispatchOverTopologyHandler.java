package field.bytecode.protect.instrumentation2.rt;

import field.bytecode.protect.dispatch.DispatchSupport;
import field.bytecode.protect.instrumentation.EntryHandler;
import field.bytecode.protect.instrumentation.ExitHandler;
import field.bytecode.protect.instrumentation.FieldBytecodeAdapter;

import java.util.Map;

/**
 * Created by jason on 7/22/14.
 */
public
class DispatchOverTopologyHandler implements EntryHandler, ExitHandler {
    public static
    DispatchOverTopologyHandler newInstance(String name, String clsName) {
        DispatchOverTopologyHandler handler = new DispatchOverTopologyHandler(name, clsName);
        FieldBytecodeAdapter.registerHandler(name, handler);
        return handler;

    }

    final String name;

    private final String className;

    final DispatchSupport support = new DispatchSupport();


    public
    DispatchOverTopologyHandler(String name, String className) {
        this.name = name;
        this.className = className;
    }

    @Override
    public
    void handleEntry(String fromName,
                     Object fromThis,
                     String methodName,
                     Map<String, Object> parameters,
                     Object[] argArray) {
        try {
            support.enter(name, fromName, fromThis, methodName, parameters, argArray, className);
        } catch (Throwable t) {
            t.printStackTrace();
            throw new IllegalArgumentException(t);
        }

    }

    @Override
    public
    Object handleExit(Object returningThis,
                      String fromName,
                      Object fromThis,
                      String methodName,
                      Map<String, Object> parameters,
                      String methodReturnName) {
        try {
            return support.exit(this.name, fromThis, returningThis, parameters, className);
        } catch (Throwable t) {
            t.printStackTrace();
            throw new IllegalArgumentException(t);
        }
    }
}
