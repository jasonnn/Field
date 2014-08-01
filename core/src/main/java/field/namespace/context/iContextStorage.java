package field.namespace.context;

import java.lang.reflect.Method;

/**
 *
 */
public
interface IContextStorage<K, I> {

    /**
     * I has method m in it which returns VisitCode's
     */
    public
    I get(K at, Method m);
}
