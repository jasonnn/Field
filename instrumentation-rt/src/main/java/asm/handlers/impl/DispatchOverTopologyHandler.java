package asm.handlers.impl;



import asm.FieldBytecodeAdapter;
import asm.handlers.EntryHandler;
import asm.handlers.ExitHandler;
import asm.handlers.dispatch.DispatchSupport;

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
            DispatchSupport.enter(name, fromName, fromThis, methodName, parameters, argArray, className);
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
            return DispatchSupport.exit(this.name, fromThis, returningThis, parameters, className);
        } catch (Throwable t) {
            t.printStackTrace();
            throw new IllegalArgumentException(t);
        }
    }
}
