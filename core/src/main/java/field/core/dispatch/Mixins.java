package field.core.dispatch;

import field.core.dispatch.override.IVisualElementOverrides;
import field.core.dispatch.override.IDefaultOverride;
import field.math.graph.visitors.hint.StandardTraversalHint;
import field.math.graph.visitors.hint.TraversalHint;
import field.namespace.generic.IFunction;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public
class Mixins {

    public
    interface iMixinProxy<T> {
        public
        Class<T> getMixinInterface();

        public
        List<T> getCallList();
    }

    public static
    <T> T make(final Class<T> t, final IFunction<List<Object>, Object> combine, T... over) {
        final iMixinProxy<T> m = new iMixinProxy<T>() {
            List<T> callList = new ArrayList<T>();

            public
            List<T> getCallList() {
                return callList;
            }

            public
            Class<T> getMixinInterface() {
                return t;
            }
        };
        if (over != null) m.getCallList().addAll(Arrays.asList(over));
        Object o = Proxy.newProxyInstance(t.getClassLoader(),
                                          new Class[]{t, IDefaultOverride.class, iMixinProxy.class},
                                          new InvocationHandler() {
                                              public
                                              Object invoke(Object proxy, Method method, Object[] args)
                                                      throws Throwable {
                                                  if ("getCallList".equals(method.getName())) {
                                                      return method.invoke(m, args);
                                                  }
                                                  if ("getMixinInterface".equals(method.getName())) {
                                                      return method.invoke(m, args);
                                                  }
                                                  else {
                                                      List<T> l = m.getCallList();
                                                      List<Object> r = new ArrayList<Object>();
                                                      for (T t : l) {
                                                          Object rr = method.invoke(t, args);
                                                          r.add(rr);
                                                      }
                                                      Object ret = null;
                                                      if (combine != null) {
                                                          ret = combine.apply(r);
                                                      }
                                                      else {
                                                          ret = !r.isEmpty() ? r.get(0) : null;
                                                      }
                                                      return ret;
                                                  }
                                              }
                                          });
        return (T) o;
    }
//TODO I think i might have messed this up?
    public static IFunction<List<Object>, Object> visitCodeCombiner = new IFunction<List<Object>, Object>() {

        public
        Object apply(List<Object> in) {
            if (in.isEmpty()) return null;
            if (in.size() == 1) return in.get(0);
            boolean isVisitCode = false;
            for (int i = 0; i < in.size(); i++) {
                if (in.get(i) == StandardTraversalHint.STOP) return StandardTraversalHint.STOP;
                if (in.get(i) == StandardTraversalHint.SKIP) return StandardTraversalHint.SKIP;
                if (in instanceof TraversalHint) isVisitCode = true;
            }
            return isVisitCode ? StandardTraversalHint.CONTINUE : in.get(0);
        }
    };

    public static
    iMixinProxy<IVisualElementOverrides> upgradeOverrides(IVisualElement e) {
        IVisualElementOverrides o = e.getProperty(IVisualElement.overrides);
        if (o instanceof iMixinProxy) return (iMixinProxy<IVisualElementOverrides>) o;

        IVisualElementOverrides oo = make(IVisualElementOverrides.class, visitCodeCombiner, o);
        e.setProperty(IVisualElement.overrides, oo);
        return (iMixinProxy<IVisualElementOverrides>) oo;
    }

    public
    <T extends IDefaultOverride> T mixInOverride(Class<T> ty, IVisualElement e) {
        iMixinProxy<IVisualElementOverrides> m = upgradeOverrides(e);
        List<IVisualElementOverrides> c = m.getCallList();
        for (IVisualElementOverrides o : c) {
            if (ty.isInstance(o)) {
                return (T) o;
            }
        }
        try {
            IDefaultOverride o = ty.newInstance();
            o.setVisualElement(e);
            c.add((IVisualElementOverrides) o);
            return (T) o;
        } catch (InstantiationException e1) {
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        }
        return null;
    }

}
