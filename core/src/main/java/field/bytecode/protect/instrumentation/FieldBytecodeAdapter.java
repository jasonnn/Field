package field.bytecode.protect.instrumentation;

import field.bytecode.protect.RefactorCarefully;
import field.util.Registration;
import field.util.Registrations;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created on Mar 13, 2004
 *
 * @author marc
 */
@RefactorCarefully
@SuppressWarnings("UnusedDeclaration")
public final
class FieldBytecodeAdapter {

    static final AtomicInteger _counter = new AtomicInteger(0);
    public static HashSet<String> knownAliasingParameters = new HashSet<String>();

    static Map<String, DeferedHandler> deferedHandlers = new HashMap<String, DeferedHandler>();

    static FastCancelHandler[] entryCancelList = new FastCancelHandler[0];

    static FastEntryHandler[] entryHandlerList = new FastEntryHandler[0];

    public static Map<String, EntryHandler> entryHandlers = new HashMap<String, EntryHandler>();

    public static Map<String, ExitHandler> exitHandlers = new HashMap<String, ExitHandler>();

    static Map<String, Map<String, Object>> parameters = new HashMap<String, Map<String, Object>>();

    static int uniq_parameter = 0;

    static Map<String, YieldHandler> yieldHandlers = new HashMap<String, YieldHandler>();

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
    void addCancelHandler(FastCancelHandler handler) {
        entryCancelList = Arrays.copyOf(entryCancelList, FieldBytecodeAdapter.entryCancelList.length + 1);
        entryCancelList[entryCancelList.length - 1] = handler;

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

        //  java.lang.reflect.Method[] m = ReflectionTools.findAllMethodsCalled(fromThis.getClass(), methodName);
        //  java.lang.reflect.Method mFound = ReflectionTools.findMethodWithParameters(paramArray, m);
        // if (StandardTrampoline.debug)
        //System.out.println(" found method <" + mFound + ">");
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
        // if (StandardTrampoline.debug)
        //System.out.println(" fromname is <" + wasReturn + "> <" + localStorage + "> <" + fromName + "> <" + fromThis + "> <" + methodName + ">");
        return yieldHandlers.get(fromName)
                            .yieldStore(wasReturn, localStorage, fromName, fromThis, methodName, resumeLabel);
    }

    public static
    Object handleCancelFast(int name, Object from, String method, Object[] args) {
        return entryCancelList[name].handle(name, from, method, args);
    }

    static
    int unique() {
        return _counter.getAndIncrement();
    }

    public static
    void handleFast(int fromName, Object fromThis, Object[] argArray) {
        entryHandlerList[fromName].handle(fromName, fromThis, argArray);
    }
}