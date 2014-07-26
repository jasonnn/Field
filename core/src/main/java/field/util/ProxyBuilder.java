package field.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jason on 7/14/14.
 */
public
class ProxyBuilder<T> {
    ClassLoader loader;
    List<Class> interfaces = new ArrayList<Class>();
    InvocationHandler handler;

    public static
    <T> ProxyBuilder<T> proxyFor(Class<T> cls, Class... rest) {
        ProxyBuilder<T> b = new ProxyBuilder<T>();
        b.interfaces.add(cls);
        Collections.addAll(b.interfaces, rest);
        b.loader = cls.getClassLoader();
        return b;
    }


    public
    ProxyBuilder<T> withClassLoader(ClassLoader loader) {
        this.loader = loader;
        return this;
    }

    public
    T withHandler(InvocationHandler handler) {
        this.handler = handler;
        return build();
    }


    @SuppressWarnings("unchecked")
    public
    T build() {
        return (T) Proxy.newProxyInstance(loader, interfaces.toArray(new Class[interfaces.size()]), handler);
    }


}
