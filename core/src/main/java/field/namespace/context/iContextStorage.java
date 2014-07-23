package field.namespace.context;

import java.lang.reflect.Method;

/**
* Created by jason on 7/14/14.
*/
public interface iContextStorage<K, I> {

    /**
     * I has method m in it which returns VisitCode's
     */
    public I get(K at, Method m);
}
