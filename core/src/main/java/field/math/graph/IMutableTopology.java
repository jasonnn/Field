package field.math.graph;

import field.namespace.generic.ReflectionTools;

import java.lang.reflect.Method;

/**
 * can act as it's own notification
 */
public
interface IMutableTopology<T> extends ITopology<T> {
    public static Method method_begin = ReflectionTools.methodOf("begin", IMutableTopology.class);
    public static Method method_end = ReflectionTools.methodOf("end", IMutableTopology.class);
    public static Method method_addChild =
            ReflectionTools.methodOf("addChild", IMutableTopology.class, Object.class, Object.class);
    public static Method method_removeChild =
            ReflectionTools.methodOf("removeChild", IMutableTopology.class, Object.class, Object.class);

    public
    void begin();

    public
    void end();

    public
    void addChild(T from, T to);

    public
    void removeChild(T from, T to);

    public
    void registerNotify(IMutableTopology<? super T> here);

    public
    void deregisterNotify(IMutableTopology<? super T> here);
}
