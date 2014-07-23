package field.bytecode.protect.instrumentation;

import field.bytecode.protect.RefactorCarefully;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created on Mar 13, 2004
 *
 * @author marc
 */
@SuppressWarnings("UnusedDeclaration")
@RefactorCarefully
final public
class FieldBytecodeAdapter {

    static final AtomicInteger _counter = new AtomicInteger(0);
    static public HashSet<String> knownAliasingParameters = new HashSet<String>();

    static Map<String, DeferedHandler> deferedHandlers = new HashMap<String, DeferedHandler>();

    static FastCancelHandler[] entryCancelList = new FastCancelHandler[0];

    static FastEntryHandler[] entryHandlerList = new FastEntryHandler[0];

    public static Map<String, EntryHandler> entryHandlers = new HashMap<String, EntryHandler>();

    public static Map<String, ExitHandler> exitHandlers = new HashMap<String, ExitHandler>();

    static Map<String, Map<String, Object>> parameters = new HashMap<String, Map<String, Object>>();

    static int uniq_parameter = 0;

    static Map<String, YieldHandler> yieldHandlers = new HashMap<String, YieldHandler>();

    public static
    void registerParameters(String id, Map<String, Object> params) {
        params.put(id, params);
    }

    public static
    void registerHandler(String name, Object handler) {
        if (handler instanceof EntryHandler) registerEntryHandler(name, (EntryHandler) handler);
        if (handler instanceof ExitHandler) registerExitHandler(name, (ExitHandler) handler);
        if (handler instanceof YieldHandler) registerYieldHandler(name, (YieldHandler) handler);
        if (handler instanceof DeferedHandler) registerDeferedHandler(name, (DeferedHandler) handler);

    }

    public static
    void registerEntryHandler(String name, EntryHandler handler) {
        assert !entryHandlers.containsKey(name);
        entryHandlers.put(name, handler);
    }

    public static
    void registerExitHandler(String name, ExitHandler handler) {
        assert !exitHandlers.containsKey(name);
        exitHandlers.put(name, handler);
    }

    public static
    void registerYieldHandler(String name, YieldHandler handler) {
        assert !yieldHandlers.containsKey(name);
        yieldHandlers.put(name, handler);
    }

    public static
    void registerDeferedHandler(String name, DeferedHandler handler) {
        assert !deferedHandlers.containsKey(name);
        deferedHandlers.put(name, handler);
    }

    public static
    void addCancelHandler(FastCancelHandler handler) {
        entryCancelList = Arrays.copyOf(entryCancelList, FieldBytecodeAdapter.entryCancelList.length + 1);
        entryCancelList[entryCancelList.length - 1] = handler;

    }

    static public
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

    static public
    void handleEntry(String fromName, Object fromThis, String methodName, String parameterName, Object[] argArray) {
        assert entryHandlers.containsKey(fromName) : fromName + " " + entryHandlers;
        entryHandlers.get(fromName)
                     .handleEntry(fromName, fromThis, methodName, parameters.get(parameterName), argArray);
    }

    static public
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

    static public
    int handle_yieldIndex(String fromName, Object fromThis, String methodName) {
        return yieldHandlers.get(fromName).yieldIndexFor(fromName, fromThis, methodName);
    }

    static public
    Object[] handle_yieldLoad(String fromName, Object fromThis, String methodName) {
        return yieldHandlers.get(fromName).yieldLoad(fromName, fromThis, methodName);
    }

    static public
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

    static public
    Object handleCancelFast(int name, Object from, String method, Object[] args) {
        return entryCancelList[name].handle(name, from, method, args);
    }

    static
    int unique() {
        return _counter.getAndIncrement();
    }

    static public
    void handleFast(int fromName, Object fromThis, Object[] argArray) {
        entryHandlerList[fromName].handle(fromName, fromThis, argArray);
    }
}