package field.core.dispatch.override;

import field.bytecode.protect.BaseRef;
import field.bytecode.protect.annotations.GenerateMethods;
import field.bytecode.protect.annotations.Mirror;
import field.core.dispatch.*;
import field.core.plugins.log.ElementInvocationLogging;
import field.core.plugins.log.Logging;
import field.launch.IUpdateable;
import field.math.graph.TopologyViewOfGraphNodes;
import field.math.graph.visitors.hint.StandardTraversalHint;
import field.math.graph.visitors.hint.TraversalHint;
import field.namespace.dispatch.DispatchOverTopology;
import field.util.Dict.Prop;
import org.eclipse.swt.widgets.Event;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;

@GenerateMethods
public
interface IVisualElementOverrides {

    static
    class Adaptor implements IVisualElementOverrides {
        public
        TraversalHint added(IVisualElement newSource) {
            return StandardTraversalHint.CONTINUE;
        }

        public
        TraversalHint beginExecution(IVisualElement source) {
            return StandardTraversalHint.CONTINUE;
        }

        public
        TraversalHint deleted(IVisualElement source) {
            return StandardTraversalHint.CONTINUE;
        }

        public
        <T> TraversalHint deleteProperty(IVisualElement source, VisualElementProperty<T> prop) {
            return StandardTraversalHint.CONTINUE;
        }

        public
        TraversalHint endExecution(IVisualElement source) {
            return StandardTraversalHint.CONTINUE;
        }

        public
        <T> TraversalHint getProperty(IVisualElement source, VisualElementProperty<T> prop, Ref<T> ref) {
            return StandardTraversalHint.CONTINUE;
        }

        public
        TraversalHint handleKeyboardEvent(IVisualElement newSource, Event event) {
            return StandardTraversalHint.CONTINUE;
        }

        public
        TraversalHint inspectablePropertiesFor(IVisualElement source, List<Prop> properties) {
            return StandardTraversalHint.CONTINUE;
        }

        public
        TraversalHint isHit(IVisualElement source, Event event, Ref<Boolean> is) {
            return StandardTraversalHint.CONTINUE;
        }

        public
        TraversalHint menuItemsFor(IVisualElement source, Map<String, IUpdateable> items) {
            return StandardTraversalHint.CONTINUE;
        }

        public
        TraversalHint paintNow(IVisualElement source, Rect bounds, boolean visible) {
            return StandardTraversalHint.CONTINUE;
        }

        public
        TraversalHint prepareForSave() {
            return StandardTraversalHint.CONTINUE;
        }

        public
        <T> TraversalHint setProperty(IVisualElement source, VisualElementProperty<T> prop, Ref<T> to) {
            return StandardTraversalHint.CONTINUE;
        }

        public
        TraversalHint shouldChangeFrame(IVisualElement source, Rect newFrame, Rect oldFrame, boolean now) {
            return StandardTraversalHint.CONTINUE;
        }

    }

    public static
    class DefaultOverride extends Adaptor implements iDefaultOverride {
        public IVisualElement forElement;

        public
        DefaultOverride() {
        }

        @Override
        public
        TraversalHint deleted(IVisualElement source) {
            if (source == forElement) {
                source.dispose();
            }
            return StandardTraversalHint.CONTINUE;
        }

        @Override
        public
        <T> TraversalHint deleteProperty(IVisualElement source, VisualElementProperty<T> prop) {
            if (source == forElement) {
                VisualElementProperty<T> a = prop.getAliasedTo();
                while (a != null) {
                    prop = a;
                    a = a.getAliasedTo();
                }

                forElement.deleteProperty(prop);
            }
            return StandardTraversalHint.CONTINUE;
        }

        @Override
        public
        <T> TraversalHint getProperty(IVisualElement source, VisualElementProperty<T> prop, Ref<T> ref) {
            if ((ref.to == null || (source == forElement)) && forElement != null) {

                VisualElementProperty<T> a = prop.getAliasedTo();
                while (a != null) {
                    prop = a;
                    a = a.getAliasedTo();
                }
                assert forElement != null : "problem in class " + this.getClass();
                T property = forElement.getProperty(prop);
                if (property != null) {
                    ref.set(property, forElement);
                }
            }
            return StandardTraversalHint.CONTINUE;
        }

        @Override
        public
        <T> TraversalHint setProperty(IVisualElement source, VisualElementProperty<T> prop, Ref<T> to) {
            if (source == forElement) {
                VisualElementProperty<T> a = prop.getAliasedTo();
                while (a != null) {
                    prop = a;
                    a = a.getAliasedTo();
                }

                if (Logging.enabled()) Logging.logging.addEvent(new ElementInvocationLogging.DidSetProperty(prop,
                                                                                                            source,
                                                                                                            to.to,
                                                                                                            forElement.getProperty(prop)));
                forElement.setProperty(prop, to.get());
            }
            return StandardTraversalHint.CONTINUE;
        }

        public
        DefaultOverride setVisualElement(IVisualElement ve) {
            forElement = ve;
            return this;
        }

        @Override
        public
        TraversalHint shouldChangeFrame(IVisualElement source, Rect newFrame, Rect oldFrame, boolean now) {
            if (source == forElement) {
                forElement.setFrame(newFrame);
                return StandardTraversalHint.CONTINUE;
            }
            return StandardTraversalHint.CONTINUE;
        }
    }

    public static
    interface iDefaultOverride {
        public
        iDefaultOverride setVisualElement(IVisualElement ve);
    }

    static
    class MakeDispatchProxy {

        public static int dispatchBackwardCount = 0;

        public static int dispatchForwardCount = 0;

        public
        IVisualElementOverrides getBackwardsOverrideProxyFor(final IVisualElement element) {
            final TopologyViewOfGraphNodes<IVisualElement> topView = new TopologyViewOfGraphNodes<IVisualElement>(true);

            return (IVisualElementOverrides) Proxy.newProxyInstance(element.getClass().getClassLoader(),
                                                                    new Class[]{IVisualElementOverrides.class},
                                                                    new InvocationHandler() {

                                                                        DispatchOverTopology<IVisualElement> dispatch =
                                                                                new DispatchOverTopology<IVisualElement>(topView);

                                                                        DispatchOverTopology<IVisualElement>.Raw raw =
                                                                                dispatch.new Raw(true) {
                                                                                    @Override
                                                                                    public
                                                                                    Object getObject(IVisualElement e) {
                                                                                        dispatchBackwardCount++;
                                                                                        return e.getProperty(IVisualElement.overrides);
                                                                                    }

                                                                                };

                                                                        public
                                                                        Object invoke(Object arg0,
                                                                                      Method arg1,
                                                                                      Object[] arg2) throws Throwable {

                                                                            TraversalHint o =
                                                                                    raw.dispatch(arg1, element, arg2);

                                                                            return o;
                                                                        }
                                                                    });
        }

        public
        IVisualElementOverrides getOverrideProxyFor(final IVisualElement element) {
            final TopologyViewOfGraphNodes<IVisualElement> topView = new TopologyViewOfGraphNodes<IVisualElement>();

            return (IVisualElementOverrides) Proxy.newProxyInstance(element.getClass().getClassLoader(),
                                                                    new Class[]{IVisualElementOverrides.class},
                                                                    new InvocationHandler() {

                                                                        DispatchOverTopology<IVisualElement> dispatch =
                                                                                new DispatchOverTopology<IVisualElement>(topView);

                                                                        DispatchOverTopology<IVisualElement>.Raw raw =
                                                                                dispatch.new Raw(true) {
                                                                                    @Override
                                                                                    public
                                                                                    Object getObject(IVisualElement e) {
                                                                                        dispatchForwardCount++;
                                                                                        IVisualElementOverrides o =
                                                                                                e.getProperty(IVisualElement.overrides);
                                                                                        return o;
                                                                                    }


                                                                                };

                                                                        public
                                                                        Object invoke(Object arg0,
                                                                                      Method arg1,
                                                                                      Object[] arg2) throws Throwable {
                                                                            arg1.setAccessible(true);
                                                                            TraversalHint o =
                                                                                    raw.dispatch(arg1, element, arg2);
                                                                            return o;
                                                                        }
                                                                    });
        }
    }

    public static
    class Ref<T> extends BaseRef<T> {
        public IVisualElement storageSource;

        public
        Ref(T to) {
            super(to);
        }

        public
        IVisualElement getStorageSource() {
            return storageSource;
        }

        public
        Ref<T> set(T to, IVisualElement storedBy) {
            this.to = to;
            this.storageSource = storedBy;
            unset = false;
            return this;
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
