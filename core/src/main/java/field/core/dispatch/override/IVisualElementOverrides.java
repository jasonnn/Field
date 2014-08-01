package field.core.dispatch.override;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import field.bytecode.protect.annotations.GenerateMethods;
import field.bytecode.protect.annotations.Mirror;
import field.core.dispatch.*;
import field.launch.IUpdateable;
import field.math.graph.TopologyViewOfGraphNodes;
import field.math.graph.visitors.hint.TraversalHint;
import field.namespace.dispatch.DispatchOverTopology;
import field.util.Dict.Prop;
import org.eclipse.swt.widgets.Event;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.*;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@GenerateMethods
public
interface IVisualElementOverrides {

    static abstract
    class MakeDispatchProxy {
        private static final Logger log = Logger.getLogger(MakeDispatchProxy.class.getName());

        private
        MakeDispatchProxy() {
        }


        private static final LoadingCache<ClassLoader, Constructor<IVisualElementOverrides>> cache;

        static {
            cache = CacheBuilder.newBuilder()
                                .weakKeys()
                                .weakValues()
                                .build(new CacheLoader<ClassLoader, Constructor<IVisualElementOverrides>>() {
                                    @SuppressWarnings("unchecked")
                                    @Override
                                    public
                                    Constructor<IVisualElementOverrides> load(@NotNull ClassLoader key)
                                            throws Exception {
                                        log.info("cache miss for: " + key);
                                        return (Constructor) Proxy.getProxyClass(key, IVisualElementOverrides.class)
                                                                  .getDeclaredConstructor(InvocationHandler.class);
                                    }
                                });
        }

        static
        IVisualElementOverrides makeProxy(IVisualElement element, InvocationHandler handler) {
            Constructor<IVisualElementOverrides> ctor = cache.getUnchecked(element.getClass().getClassLoader());
            try {
                return ctor.newInstance(handler);
            } catch (InstantiationException e) {
                log.log(Level.WARNING, "fail", e);
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                log.log(Level.WARNING, "fail", e);
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                log.log(Level.WARNING, "fail", e);
                throw new RuntimeException(e);
            }
        }

        public static int dispatchBackwardCount = 0;

        public static int dispatchForwardCount = 0;

        static
        class OverrideProxy implements InvocationHandler {
            final IVisualElement delegate;

            final DispatchOverTopology<IVisualElement>.Raw raw;

            OverrideProxy(IVisualElement delegate) {
                this.delegate = delegate;
                final TopologyViewOfGraphNodes<IVisualElement> topView = new TopologyViewOfGraphNodes<IVisualElement>();
                DispatchOverTopology<IVisualElement> dispatch = new DispatchOverTopology<IVisualElement>(topView);
                raw = dispatch.new Raw(true) {
                    @Override
                    public
                    Object getObject(IVisualElement e) {
                        dispatchForwardCount++;
                        return e.getProperty(IVisualElement.overrides);
                    }
                };
            }

            @Override
            public
            Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return raw.dispatch(method, delegate, args);
            }
        }

        static
        class BackwardsProxy implements InvocationHandler {
            final IVisualElement delegate;

            final DispatchOverTopology<IVisualElement>.Raw raw;

            BackwardsProxy(IVisualElement delegate) {

                this.delegate = delegate;
                TopologyViewOfGraphNodes<IVisualElement> topView = new TopologyViewOfGraphNodes<IVisualElement>(true);
                DispatchOverTopology<IVisualElement> dispatch = new DispatchOverTopology<IVisualElement>(topView);
                raw = dispatch.new Raw(true) {
                    @Override
                    public
                    Object getObject(IVisualElement e) {
                        dispatchBackwardCount++;
                        return e.getProperty(IVisualElement.overrides);
                    }

                };
            }

            @Override
            public
            Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return raw.dispatch(method, delegate, args);
            }
        }

        public static
        IVisualElementOverrides getBackwardsOverrideProxyFor(final IVisualElement element) {
            return makeProxy(element, new BackwardsProxy(element));
        }


        public static
        IVisualElementOverrides getOverrideProxyFor(final IVisualElement element) {
            return makeProxy(element, new OverrideProxy(element));
        }
    }

    public static VisualElementContextTopology topology = new VisualElementContextTopology(null);

//	static public iVisualElementOverrides_m backward = new iVisualElementOverrides_m(new Dispatch<iVisualElement, iVisualElementOverrides>(topology).getBackwardsOverrideProxyFor(iVisualElementOverrides.class));

    public static IVisualElementOverrides_m backward =
            new IVisualElementOverrides_m(new FastVisualElementOverridesDispatch(true));
    public static IVisualElementOverrides_m forward =
            new IVisualElementOverrides_m(new FastVisualElementOverridesDispatch(false));

    //	static public iVisualElementOverrides_m forward = new iVisualElementOverrides_m(new Dispatch<iVisualElement, iVisualElementOverrides>(topology).getOverrideProxyFor(iVisualElementOverrides.class));
//	static public iVisualElementOverrides_m forwardAbove = new iVisualElementOverrides_m(new Dispatch<iVisualElement, iVisualElementOverrides>(topology).getAboveOverrideProxyFor(iVisualElementOverrides.class));
    public static IVisualElementOverrides_m forwardAbove =
            new IVisualElementOverrides_m(new FastVisualElementOverridesDispatchAbove(false));

    @Mirror
    public
    TraversalHint added(IVisualElement newSource);

    @Mirror
    public
    TraversalHint beginExecution(IVisualElement source);

    @Mirror
    public
    TraversalHint deleted(IVisualElement source);

    @Mirror
    public
    <T> TraversalHint deleteProperty(IVisualElement source, VisualElementProperty<T> prop);

    @Mirror
    public
    TraversalHint endExecution(IVisualElement source);

    @Mirror
    public
    <T> TraversalHint getProperty(IVisualElement source, VisualElementProperty<T> prop, Ref<T> ref);

    @Mirror
    public
    TraversalHint handleKeyboardEvent(IVisualElement newSource, Event event);

    @Mirror
    public
    TraversalHint inspectablePropertiesFor(IVisualElement source, List<Prop> properties);

    @Mirror
    public
    TraversalHint isHit(IVisualElement source, Event event, Ref<Boolean> is);

    @Mirror
    public
    TraversalHint menuItemsFor(IVisualElement source, Map<String, IUpdateable> items);

    @Mirror
    public
    TraversalHint paintNow(IVisualElement source, Rect bounds, boolean visible);

    @Mirror
    public
    TraversalHint prepareForSave();

    @Mirror
    public
    <T> TraversalHint setProperty(IVisualElement source, VisualElementProperty<T> prop, Ref<T> to);

    @Mirror
    public
    TraversalHint shouldChangeFrame(IVisualElement source, Rect newFrame, Rect oldFrame, boolean now);

}
