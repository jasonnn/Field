package asm;

import asm.handlers.*;
import field.bytecode.protect.RefactorCarefully;
import field.util.Registration;
import field.util.Registrations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created on Mar 13, 2004
 *
 * @author marc
 */
@RefactorCarefully
@SuppressWarnings("UnusedDeclaration")
public final
class FieldBytecodeAdapter {


    static final Set<String> knownAliasingParameters = new ConcurrentSkipListSet<String>();

    static final Map<String, DeferedHandler> deferedHandlers = new ConcurrentHashMap<String, DeferedHandler>();

    static final List<FastCancelHandler> entryCancelList = new CopyOnWriteArrayList<FastCancelHandler>();

    static final List<FastEntryHandler> entryHandlerList = new CopyOnWriteArrayList<FastEntryHandler>();

    static final Map<String, EntryHandler> entryHandlers = new ConcurrentHashMap<String, EntryHandler>();

    static final Map<String, ExitHandler> exitHandlers = new ConcurrentHashMap<String, ExitHandler>();

    static final Map<String, Map<String, Object>> parameters = new ConcurrentHashMap<String, Map<String, Object>>();

    static final Map<String, YieldHandler> yieldHandlers = new ConcurrentHashMap<String, YieldHandler>();

    public static
    Registration registerParameters(final String id, Map<String, Object> params) {
        parameters.put(id, params);
        return new Registration() {
            @Override
            public
            void remove() {
                unregisterParameters(id);
            }
        };
    }

    public static
    void unregisterParameters(String id) {
        parameters.remove(id);
    }

    public static
    Registration registerHandler(String name, Object handler) {
        List<Registration> regs = new ArrayList<Registration>(4);
        if (handler instanceof EntryHandler) regs.add(registerEntryHandler(name, (EntryHandler) handler));
        if (handler instanceof ExitHandler) regs.add(registerExitHandler(name, (ExitHandler) handler));
        if (handler instanceof YieldHandler) regs.add(registerYieldHandler(name, (YieldHandler) handler));
        if (handler instanceof DeferedHandler) regs.add(registerDeferedHandler(name, (DeferedHandler) handler));
        return new Registrations(regs);
    }

    public static
    void unregisterHandler(String name) {

    }

    public static
    void unregisterEntryHandler(String name) {
        entryHandlers.remove(name);
    }

    public static
    void unregisterExitHandler(String name) {
        exitHandlers.remove(name);
    }

    public static
    void unregisterYieldHandler(String name) {
        yieldHandlers.remove(name);
    }

    public static
    void unregisterDeferedHandler(String name) {
        deferedHandlers.remove(name);
    }

    public static
    Registration registerEntryHandler(final String name, EntryHandler handler) {
        assert !entryHandlers.containsKey(name);
        entryHandlers.put(name, handler);
        return new Registration() {
            @Override
            public
            void remove() {
                unregisterEntryHandler(name);
            }
        };
    }

    public static
    Registration registerExitHandler(final String name, ExitHandler handler) {
        assert !exitHandlers.containsKey(name);
        exitHandlers.put(name, handler);
        return new Registration() {
            @Override
            public
            void remove() {
                unregisterExitHandler(name);
            }
        };
    }

    public static
    Registration registerYieldHandler(final String name, YieldHandler handler) {
        assert !yieldHandlers.containsKey(name);
        yieldHandlers.put(name, handler);
        return new Registration() {
            @Override
            public
            void remove() {
                unregisterYieldHandler(name);
            }
        };
    }

    public static
    Registration registerDeferedHandler(final String name, DeferedHandler handler) {
        assert !deferedHandlers.containsKey(name);
        deferedHandlers.put(name, handler);
        return new Registration() {
            @Override
            public
            void remove() {
                unregisterDeferedHandler(name);
            }
        };
    }

    public static
    Registration registerCancelHandler(final FastCancelHandler handler) {
        entryCancelList.add(handler);
        return new Registration() {
            @Override
            public
            void remove() {
                unregisterCancelHandler(handler);
            }
        };
    }

    public static
    void unregisterCancelHandler(FastCancelHandler handler) {
        entryCancelList.remove(handler);
    }

    public static
    Registration registerEntryHandler(final FastEntryHandler handler) {
        entryHandlerList.add(handler);
        return new Registration() {
            @Override
            public
            void remove() {
                unregisterEntryHandler(handler);
            }
        };
    }

    public static
    void unregisterEntryHandler(FastEntryHandler handler) {
        entryHandlerList.remove(handler);
    }

    public static
    Object handleExit(Object returningThis,
                      String fromName,
                      Object fromThis,
                      String methodName,
                      String parameterName,
                      String methodReturnName) {
        return exitHandlers.get(fromName)
                           .handleExit(returningThis,
                                       fromName,
                                       fromThis,
                                       methodName,
                                       parameters.get(parameterName),
                                       methodReturnName);
    }

    public static
    void handleEntry(String fromName, Object fromThis, String methodName, String parameterName, Object[] argArray) {
        assert entryHandlers.containsKey(fromName) : fromName + ' ' + entryHandlers;
        entryHandlers.get(fromName)
                     .handleEntry(fromName, fromThis, methodName, parameters.get(parameterName), argArray);
    }

    public static
    void handleDefered(String fromName,
                       Object fromThis,
                       String methodName,
                       String parameterName,
                       Object[] argArray,
                       Class[] paramArray) {
        deferedHandlers.get(fromName)
                       .handleDefered(fromName,
                                      fromThis,
                                      methodName,
                                      parameters.get(parameterName),
                                      argArray,
                                      paramArray);
    }

    public static
    int handle_yieldIndex(String fromName, Object fromThis, String methodName) {
        return yieldHandlers.get(fromName).yieldIndexFor(fromName, fromThis, methodName);
    }

    public static
    Object[] handle_yieldLoad(String fromName, Object fromThis, String methodName) {
        return yieldHandlers.get(fromName).yieldLoad(fromName, fromThis, methodName);
    }

    public static
    Object handle_yieldStore(Object wasReturn,
                             Object[] localStorage,
                             String fromName,
                             Object fromThis,
                             String methodName,
                             int resumeLabel) {
        return yieldHandlers.get(fromName)
                            .yieldStore(wasReturn, localStorage, fromName, fromThis, methodName, resumeLabel);
    }

    public static
    Object handleCancelFast(int name, Object from, String method, Object[] args) {
        return entryCancelList.get(name).handle(name, from, method, args);
    }


    public static
    void handleFast(int fromName, Object fromThis, Object[] argArray) {
        entryHandlerList.get(fromName).handle(fromName, fromThis, argArray);
    }
}