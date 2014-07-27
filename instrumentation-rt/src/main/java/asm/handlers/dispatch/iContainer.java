package asm.handlers.dispatch;


import field.util.ProxyBuilder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;


public
interface iContainer {

    public
    List propagateTo(String tag, Class clazz, Method method, Object... args);

    public static
    class ProxyGenerator {
        DispatchOverContainer container = new DispatchOverContainer();

        public static
        <T> T generate(final Class<T> implement, final iContainer on, final String tag) {
            return ProxyBuilder.proxyFor(implement).withHandler(new InvocationHandler() {
                @Override
                public
                Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    DispatchOverContainer.dispatch(tag, implement, method, on, args);
                    return null;
                }
            });

        }
    }

//    public
//    interface iContainerUpdateable extends iContainer, iUpdateable {
//        public static final Method UPDATE_METHOD = ReflectionTools.methodOf("update", iContainerUpdateable.class);
//    }
//
//    public
//    interface iContainerUpdateableAtTime extends iContainer {
//        public static final Method UPDATE_METHOD =
//                ReflectionTools.methodOf("update", iContainerUpdateable.class, Double.TYPE);
//
//        public
//        void update(double time);
//    }
}
