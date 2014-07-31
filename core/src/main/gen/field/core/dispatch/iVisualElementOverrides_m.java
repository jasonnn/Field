package field.core.dispatch;

import field.bytecode.mirror.IBoundNoArgsMethod;
import field.bytecode.mirror.impl.MirrorMethod;
import field.bytecode.mirror.impl.MirrorNoArgsMethod;
import field.launch.IUpdateable;
import field.math.abstraction.IAcceptor;
import field.math.abstraction.IProvider;
import field.math.graph.visitors.hint.TraversalHint;
import field.namespace.generic.IFunction;
import field.namespace.generic.ReflectionTools;

import java.lang.reflect.Method;

public
class iVisualElementOverrides_m {
    public static final Method added_m = ReflectionTools.methodOf("added",
                                                                  IVisualElementOverrides.class,
                                                                  IVisualElement.class);

    public static final MirrorMethod<IVisualElementOverrides, IVisualElement, TraversalHint> added_s =
            new MirrorMethod<IVisualElementOverrides, IVisualElement, TraversalHint>(IVisualElementOverrides.class,
                                                                                     "added",
                                                                                     new Class[]{IVisualElement.class});

    public static final Method beginExecution_m = ReflectionTools.methodOf("beginExecution",
                                                                           IVisualElementOverrides.class,
                                                                           IVisualElement.class);

    public static final MirrorMethod<IVisualElementOverrides, IVisualElement, TraversalHint> beginExecution_s =
            new MirrorMethod<IVisualElementOverrides, IVisualElement, TraversalHint>(IVisualElementOverrides.class,
                                                                                     "beginExecution",
                                                                                     new Class[]{IVisualElement.class});

    public static final Method deleted_m = ReflectionTools.methodOf("deleted",
                                                                    IVisualElementOverrides.class,
                                                                    IVisualElement.class);

    public static final MirrorMethod<IVisualElementOverrides, IVisualElement, TraversalHint> deleted_s =
            new MirrorMethod<IVisualElementOverrides, IVisualElement, TraversalHint>(IVisualElementOverrides.class,
                                                                                     "deleted",
                                                                                     new Class[]{IVisualElement.class});

    public static final Method deleteProperty_m = ReflectionTools.methodOf("deleteProperty",
                                                                           IVisualElementOverrides.class,
                                                                           IVisualElement.class,
                                                                           IVisualElement.VisualElementProperty.class);

    public static final MirrorMethod<IVisualElementOverrides, Object[], TraversalHint> deleteProperty_s =
            new MirrorMethod<IVisualElementOverrides, Object[], TraversalHint>(IVisualElementOverrides.class,
                                                                               "deleteProperty",
                                                                               new Class[]{IVisualElement.class,
                                                                                           IVisualElement.VisualElementProperty.class});

    public static final Method endExecution_m = ReflectionTools.methodOf("endExecution",
                                                                         IVisualElementOverrides.class,
                                                                         IVisualElement.class);

    public static final MirrorMethod<IVisualElementOverrides, IVisualElement, TraversalHint> endExecution_s =
            new MirrorMethod<IVisualElementOverrides, IVisualElement, TraversalHint>(IVisualElementOverrides.class,
                                                                                     "endExecution",
                                                                                     new Class[]{IVisualElement.class});

    public static final Method getProperty_m = ReflectionTools.methodOf("getProperty",
                                                                        IVisualElementOverrides.class,
                                                                        IVisualElement.class,
                                                                        IVisualElement.VisualElementProperty.class,
                                                                        IVisualElementOverrides.Ref.class);

    public static final MirrorMethod<IVisualElementOverrides, Object[], TraversalHint> getProperty_s =
            new MirrorMethod<IVisualElementOverrides, Object[], TraversalHint>(IVisualElementOverrides.class,
                                                                               "getProperty",
                                                                               new Class[]{IVisualElement.class,
                                                                                           IVisualElement.VisualElementProperty.class,
                                                                                           IVisualElementOverrides.Ref.class});

    public static final Method handleKeyboardEvent_m = ReflectionTools.methodOf("handleKeyboardEvent",
                                                                                IVisualElementOverrides.class,
                                                                                IVisualElement.class,
                                                                                org.eclipse.swt.widgets.Event.class);

    public static final MirrorMethod<IVisualElementOverrides, Object[], TraversalHint> handleKeyboardEvent_s =
            new MirrorMethod<IVisualElementOverrides, Object[], TraversalHint>(IVisualElementOverrides.class,
                                                                               "handleKeyboardEvent",
                                                                               new Class[]{IVisualElement.class,
                                                                                           org.eclipse.swt.widgets.Event.class});

    public static final Method inspectablePropertiesFor_m = ReflectionTools.methodOf("inspectablePropertiesFor",
                                                                                     IVisualElementOverrides.class,
                                                                                     IVisualElement.class,
                                                                                     java.util.List.class);

    public static final MirrorMethod<IVisualElementOverrides, Object[], TraversalHint> inspectablePropertiesFor_s =
            new MirrorMethod<IVisualElementOverrides, Object[], TraversalHint>(IVisualElementOverrides.class,
                                                                               "inspectablePropertiesFor",
                                                                               new Class[]{IVisualElement.class,
                                                                                           java.util.List.class});

    public static final Method isHit_m = ReflectionTools.methodOf("isHit",
                                                                  IVisualElementOverrides.class,
                                                                  IVisualElement.class,
                                                                  org.eclipse.swt.widgets.Event.class,
                                                                  IVisualElementOverrides.Ref.class);

    public static final MirrorMethod<IVisualElementOverrides, Object[], TraversalHint> isHit_s =
            new MirrorMethod<IVisualElementOverrides, Object[], TraversalHint>(IVisualElementOverrides.class,
                                                                               "isHit",
                                                                               new Class[]{IVisualElement.class,
                                                                                           org.eclipse.swt.widgets.Event.class,
                                                                                           IVisualElementOverrides.Ref.class});

    public static final Method menuItemsFor_m = ReflectionTools.methodOf("menuItemsFor",
                                                                         IVisualElementOverrides.class,
                                                                         IVisualElement.class,
                                                                         java.util.Map.class);

    public static final MirrorMethod<IVisualElementOverrides, Object[], TraversalHint> menuItemsFor_s =
            new MirrorMethod<IVisualElementOverrides, Object[], TraversalHint>(IVisualElementOverrides.class,
                                                                               "menuItemsFor",
                                                                               new Class[]{IVisualElement.class,
                                                                                           java.util.Map.class});

    public static final Method paintNow_m = ReflectionTools.methodOf("paintNow",
                                                                     IVisualElementOverrides.class,
                                                                     IVisualElement.class,
                                                                     IVisualElement.Rect.class,
                                                                     boolean.class);

    public static final MirrorMethod<IVisualElementOverrides, Object[], TraversalHint> paintNow_s =
            new MirrorMethod<IVisualElementOverrides, Object[], TraversalHint>(IVisualElementOverrides.class,
                                                                               "paintNow",
                                                                               new Class[]{IVisualElement.class,
                                                                                           IVisualElement.Rect.class,
                                                                                           boolean.class});

    public static final Method prepareForSave_m = ReflectionTools.methodOf("prepareForSave",
                                                                           IVisualElementOverrides.class);

    public static final MirrorNoArgsMethod<IVisualElementOverrides, TraversalHint> prepareForSave_s =
            new MirrorNoArgsMethod<IVisualElementOverrides, TraversalHint>(IVisualElementOverrides.class,
                                                                           "prepareForSave");

    public static final Method setProperty_m = ReflectionTools.methodOf("setProperty",
                                                                        IVisualElementOverrides.class,
                                                                        IVisualElement.class,
                                                                        IVisualElement.VisualElementProperty.class,
                                                                        IVisualElementOverrides.Ref.class);

    public static final MirrorMethod<IVisualElementOverrides, Object[], TraversalHint> setProperty_s =
            new MirrorMethod<IVisualElementOverrides, Object[], TraversalHint>(IVisualElementOverrides.class,
                                                                               "setProperty",
                                                                               new Class[]{IVisualElement.class,
                                                                                           IVisualElement.VisualElementProperty.class,
                                                                                           IVisualElementOverrides.Ref.class});

    public static final Method shouldChangeFrame_m = ReflectionTools.methodOf("shouldChangeFrame",
                                                                              IVisualElementOverrides.class,
                                                                              IVisualElement.class,
                                                                              IVisualElement.Rect.class,
                                                                              IVisualElement.Rect.class,
                                                                              boolean.class);

    public static final MirrorMethod<IVisualElementOverrides, Object[], TraversalHint> shouldChangeFrame_s =
            new MirrorMethod<IVisualElementOverrides, Object[], TraversalHint>(IVisualElementOverrides.class,
                                                                               "shouldChangeFrame",
                                                                               new Class[]{IVisualElement.class,
                                                                                           IVisualElement.Rect.class,
                                                                                           IVisualElement.Rect.class,
                                                                                           boolean.class});

    public final added_interface added;

    public final beginExecution_interface beginExecution;

    public final deleted_interface deleted;

    public final deleteProperty_interface deleteProperty;

    public final endExecution_interface endExecution;

    public final getProperty_interface getProperty;

    public final handleKeyboardEvent_interface handleKeyboardEvent;

    public final inspectablePropertiesFor_interface inspectablePropertiesFor;

    public final isHit_interface isHit;

    public final menuItemsFor_interface menuItemsFor;

    public final paintNow_interface paintNow;

    public final IBoundNoArgsMethod<TraversalHint> prepareForSave;

    public final setProperty_interface setProperty;

    public final shouldChangeFrame_interface shouldChangeFrame;

    public
    iVisualElementOverrides_m(final IVisualElementOverrides x) {
        added = new added_interface() {

            IAcceptor a = added_s.acceptor(x);

            IFunction f = added_s.function(x);


            public
            TraversalHint added(final IVisualElement p0) {
                return x.added(p0);
            }

            public
            IAcceptor<IVisualElement> set(IVisualElement p) {
                a.set(p);
                return this;
            }

            public
            TraversalHint apply(IVisualElement p) {
                return (TraversalHint) f.apply(p);
            }

            public
            IUpdateable updateable(final IVisualElement p0) {
                return new IUpdateable() {
                    public
                    void update() {
                        added(p0);
                    }
                };
            }

            public
            IProvider<TraversalHint> bind(final IVisualElement p0) {
                return new IProvider() {
                    public
                    Object get() {
                        return added(p0);
                    }
                };
            }
        };

        beginExecution = new beginExecution_interface() {

            IAcceptor a = beginExecution_s.acceptor(x);

            IFunction f = beginExecution_s.function(x);


            public
            TraversalHint beginExecution(final IVisualElement p0) {
                return x.beginExecution(p0);
            }

            public
            IAcceptor<IVisualElement> set(IVisualElement p) {
                a.set(p);
                return this;
            }

            public
            TraversalHint apply(IVisualElement p) {
                return (TraversalHint) f.apply(p);
            }

            public
            IUpdateable updateable(final IVisualElement p0) {
                return new IUpdateable() {
                    public
                    void update() {
                        beginExecution(p0);
                    }
                };
            }

            public
            IProvider<TraversalHint> bind(final IVisualElement p0) {
                return new IProvider() {
                    public
                    Object get() {
                        return beginExecution(p0);
                    }
                };
            }
        };

        deleted = new deleted_interface() {

            IAcceptor a = deleted_s.acceptor(x);

            IFunction f = deleted_s.function(x);


            public
            TraversalHint deleted(final IVisualElement p0) {
                return x.deleted(p0);
            }

            public
            IAcceptor<IVisualElement> set(IVisualElement p) {
                a.set(p);
                return this;
            }

            public
            TraversalHint apply(IVisualElement p) {
                return (TraversalHint) f.apply(p);
            }

            public
            IUpdateable updateable(final IVisualElement p0) {
                return new IUpdateable() {
                    public
                    void update() {
                        deleted(p0);
                    }
                };
            }

            public
            IProvider<TraversalHint> bind(final IVisualElement p0) {
                return new IProvider() {
                    public
                    Object get() {
                        return deleted(p0);
                    }
                };
            }
        };

        deleteProperty = new deleteProperty_interface() {

            IAcceptor a = deleteProperty_s.acceptor(x);

            IFunction f = deleteProperty_s.function(x);


            public
            TraversalHint deleteProperty(final IVisualElement p0, final IVisualElement.VisualElementProperty p1) {
                return x.deleteProperty(p0, p1);
            }

            public
            IAcceptor<Object[]> set(Object[] p) {
                a.set(p);
                return this;
            }

            public
            TraversalHint apply(Object[] p) {
                return (TraversalHint) f.apply(p);
            }

            public
            IUpdateable updateable(final IVisualElement p0, final IVisualElement.VisualElementProperty p1) {
                return new IUpdateable() {
                    public
                    void update() {
                        deleteProperty(p0, p1);
                    }
                };
            }

            public
            IProvider<TraversalHint> bind(final IVisualElement p0, final IVisualElement.VisualElementProperty p1) {
                return new IProvider() {
                    public
                    Object get() {
                        return deleteProperty(p0, p1);
                    }
                };
            }
        };

        endExecution = new endExecution_interface() {

            IAcceptor a = endExecution_s.acceptor(x);

            IFunction f = endExecution_s.function(x);


            public
            TraversalHint endExecution(final IVisualElement p0) {
                return x.endExecution(p0);
            }

            public
            IAcceptor<IVisualElement> set(IVisualElement p) {
                a.set(p);
                return this;
            }

            public
            TraversalHint apply(IVisualElement p) {
                return (TraversalHint) f.apply(p);
            }

            public
            IUpdateable updateable(final IVisualElement p0) {
                return new IUpdateable() {
                    public
                    void update() {
                        endExecution(p0);
                    }
                };
            }

            public
            IProvider<TraversalHint> bind(final IVisualElement p0) {
                return new IProvider() {
                    public
                    Object get() {
                        return endExecution(p0);
                    }
                };
            }
        };

        getProperty = new getProperty_interface() {

            IAcceptor a = getProperty_s.acceptor(x);

            IFunction f = getProperty_s.function(x);


            public
            TraversalHint getProperty(final IVisualElement p0,
                                      final IVisualElement.VisualElementProperty p1,
                                      final IVisualElementOverrides.Ref p2) {
                return x.getProperty(p0, p1, p2);
            }

            public
            IAcceptor<Object[]> set(Object[] p) {
                a.set(p);
                return this;
            }

            public
            TraversalHint apply(Object[] p) {
                return (TraversalHint) f.apply(p);
            }

            public
            IUpdateable updateable(final IVisualElement p0,
                                   final IVisualElement.VisualElementProperty p1,
                                   final IVisualElementOverrides.Ref p2) {
                return new IUpdateable() {
                    public
                    void update() {
                        getProperty(p0, p1, p2);
                    }
                };
            }

            public
            IProvider<TraversalHint> bind(final IVisualElement p0,
                                          final IVisualElement.VisualElementProperty p1,
                                          final IVisualElementOverrides.Ref p2) {
                return new IProvider() {
                    public
                    Object get() {
                        return getProperty(p0, p1, p2);
                    }
                };
            }
        };

        handleKeyboardEvent = new handleKeyboardEvent_interface() {

            IAcceptor a = handleKeyboardEvent_s.acceptor(x);

            IFunction f = handleKeyboardEvent_s.function(x);


            public
            TraversalHint handleKeyboardEvent(final IVisualElement p0, final org.eclipse.swt.widgets.Event p1) {
                return x.handleKeyboardEvent(p0, p1);
            }

            public
            IAcceptor<Object[]> set(Object[] p) {
                a.set(p);
                return this;
            }

            public
            TraversalHint apply(Object[] p) {
                return (TraversalHint) f.apply(p);
            }

            public
            IUpdateable updateable(final IVisualElement p0, final org.eclipse.swt.widgets.Event p1) {
                return new IUpdateable() {
                    public
                    void update() {
                        handleKeyboardEvent(p0, p1);
                    }
                };
            }

            public
            IProvider<TraversalHint> bind(final IVisualElement p0, final org.eclipse.swt.widgets.Event p1) {
                return new IProvider() {
                    public
                    Object get() {
                        return handleKeyboardEvent(p0, p1);
                    }
                };
            }
        };

        inspectablePropertiesFor = new inspectablePropertiesFor_interface() {

            IAcceptor a = inspectablePropertiesFor_s.acceptor(x);

            IFunction f = inspectablePropertiesFor_s.function(x);


            public
            TraversalHint inspectablePropertiesFor(final IVisualElement p0, final java.util.List p1) {
                return x.inspectablePropertiesFor(p0, p1);
            }

            public
            IAcceptor<Object[]> set(Object[] p) {
                a.set(p);
                return this;
            }

            public
            TraversalHint apply(Object[] p) {
                return (TraversalHint) f.apply(p);
            }

            public
            IUpdateable updateable(final IVisualElement p0, final java.util.List p1) {
                return new IUpdateable() {
                    public
                    void update() {
                        inspectablePropertiesFor(p0, p1);
                    }
                };
            }

            public
            IProvider<TraversalHint> bind(final IVisualElement p0, final java.util.List p1) {
                return new IProvider() {
                    public
                    Object get() {
                        return inspectablePropertiesFor(p0, p1);
                    }
                };
            }
        };

        isHit = new isHit_interface() {

            IAcceptor a = isHit_s.acceptor(x);

            IFunction f = isHit_s.function(x);


            public
            TraversalHint isHit(final IVisualElement p0,
                                final org.eclipse.swt.widgets.Event p1,
                                final IVisualElementOverrides.Ref p2) {
                return x.isHit(p0, p1, p2);
            }

            public
            IAcceptor<Object[]> set(Object[] p) {
                a.set(p);
                return this;
            }

            public
            TraversalHint apply(Object[] p) {
                return (TraversalHint) f.apply(p);
            }

            public
            IUpdateable updateable(final IVisualElement p0,
                                   final org.eclipse.swt.widgets.Event p1,
                                   final IVisualElementOverrides.Ref p2) {
                return new IUpdateable() {
                    public
                    void update() {
                        isHit(p0, p1, p2);
                    }
                };
            }

            public
            IProvider<TraversalHint> bind(final IVisualElement p0,
                                          final org.eclipse.swt.widgets.Event p1,
                                          final IVisualElementOverrides.Ref p2) {
                return new IProvider() {
                    public
                    Object get() {
                        return isHit(p0, p1, p2);
                    }
                };
            }
        };

        menuItemsFor = new menuItemsFor_interface() {

            IAcceptor a = menuItemsFor_s.acceptor(x);

            IFunction f = menuItemsFor_s.function(x);


            public
            TraversalHint menuItemsFor(final IVisualElement p0, final java.util.Map p1) {
                return x.menuItemsFor(p0, p1);
            }

            public
            IAcceptor<Object[]> set(Object[] p) {
                a.set(p);
                return this;
            }

            public
            TraversalHint apply(Object[] p) {
                return (TraversalHint) f.apply(p);
            }

            public
            IUpdateable updateable(final IVisualElement p0, final java.util.Map p1) {
                return new IUpdateable() {
                    public
                    void update() {
                        menuItemsFor(p0, p1);
                    }
                };
            }

            public
            IProvider<TraversalHint> bind(final IVisualElement p0, final java.util.Map p1) {
                return new IProvider() {
                    public
                    Object get() {
                        return menuItemsFor(p0, p1);
                    }
                };
            }
        };

        paintNow = new paintNow_interface() {

            IAcceptor a = paintNow_s.acceptor(x);

            IFunction f = paintNow_s.function(x);


            public
            TraversalHint paintNow(final IVisualElement p0, final IVisualElement.Rect p1, final boolean p2) {
                return x.paintNow(p0, p1, p2);
            }

            public
            IAcceptor<Object[]> set(Object[] p) {
                a.set(p);
                return this;
            }

            public
            TraversalHint apply(Object[] p) {
                return (TraversalHint) f.apply(p);
            }

            public
            IUpdateable updateable(final IVisualElement p0, final IVisualElement.Rect p1, final boolean p2) {
                return new IUpdateable() {
                    public
                    void update() {
                        paintNow(p0, p1, p2);
                    }
                };
            }

            public
            IProvider<TraversalHint> bind(final IVisualElement p0, final IVisualElement.Rect p1, final boolean p2) {
                return new IProvider() {
                    public
                    Object get() {
                        return paintNow(p0, p1, p2);
                    }
                };
            }
        };

        prepareForSave = prepareForSave_s.bind(x);
        setProperty = new setProperty_interface() {

            IAcceptor a = setProperty_s.acceptor(x);

            IFunction f = setProperty_s.function(x);


            public
            TraversalHint setProperty(final IVisualElement p0,
                                      final IVisualElement.VisualElementProperty p1,
                                      final IVisualElementOverrides.Ref p2) {
                return x.setProperty(p0, p1, p2);
            }

            public
            IAcceptor<Object[]> set(Object[] p) {
                a.set(p);
                return this;
            }

            public
            TraversalHint apply(Object[] p) {
                return (TraversalHint) f.apply(p);
            }

            public
            IUpdateable updateable(final IVisualElement p0,
                                   final IVisualElement.VisualElementProperty p1,
                                   final IVisualElementOverrides.Ref p2) {
                return new IUpdateable() {
                    public
                    void update() {
                        setProperty(p0, p1, p2);
                    }
                };
            }

            public
            IProvider<TraversalHint> bind(final IVisualElement p0,
                                          final IVisualElement.VisualElementProperty p1,
                                          final IVisualElementOverrides.Ref p2) {
                return new IProvider() {
                    public
                    Object get() {
                        return setProperty(p0, p1, p2);
                    }
                };
            }
        };

        shouldChangeFrame = new shouldChangeFrame_interface() {

            IAcceptor a = shouldChangeFrame_s.acceptor(x);

            IFunction f = shouldChangeFrame_s.function(x);


            public
            TraversalHint shouldChangeFrame(final IVisualElement p0,
                                            final IVisualElement.Rect p1,
                                            final IVisualElement.Rect p2,
                                            final boolean p3) {
                return x.shouldChangeFrame(p0, p1, p2, p3);
            }

            public
            IAcceptor<Object[]> set(Object[] p) {
                a.set(p);
                return this;
            }

            public
            TraversalHint apply(Object[] p) {
                return (TraversalHint) f.apply(p);
            }

            public
            IUpdateable updateable(final IVisualElement p0,
                                   final IVisualElement.Rect p1,
                                   final IVisualElement.Rect p2,
                                   final boolean p3) {
                return new IUpdateable() {
                    public
                    void update() {
                        shouldChangeFrame(p0, p1, p2, p3);
                    }
                };
            }

            public
            IProvider<TraversalHint> bind(final IVisualElement p0,
                                          final IVisualElement.Rect p1,
                                          final IVisualElement.Rect p2,
                                          final boolean p3) {
                return new IProvider() {
                    public
                    Object get() {
                        return shouldChangeFrame(p0, p1, p2, p3);
                    }
                };
            }
        };


    }

    public
    interface added_interface extends IAcceptor<IVisualElement>, IFunction<IVisualElement, TraversalHint> {
        public
        TraversalHint added(final IVisualElement p0);

        public
        IUpdateable updateable(final IVisualElement p0);

        public
        IProvider<TraversalHint> bind(final IVisualElement p0);
    }

    public
    interface beginExecution_interface extends IAcceptor<IVisualElement>, IFunction<IVisualElement, TraversalHint> {
        public
        TraversalHint beginExecution(final IVisualElement p0);

        public
        IUpdateable updateable(final IVisualElement p0);

        public
        IProvider<TraversalHint> bind(final IVisualElement p0);
    }

    public
    interface deleted_interface extends IAcceptor<IVisualElement>, IFunction<IVisualElement, TraversalHint> {
        public
        TraversalHint deleted(final IVisualElement p0);

        public
        IUpdateable updateable(final IVisualElement p0);

        public
        IProvider<TraversalHint> bind(final IVisualElement p0);
    }

    public
    interface deleteProperty_interface extends IAcceptor<Object[]>, IFunction<Object[], TraversalHint> {
        public
        TraversalHint deleteProperty(final IVisualElement p0, final IVisualElement.VisualElementProperty p1);

        public
        IUpdateable updateable(final IVisualElement p0, final IVisualElement.VisualElementProperty p1);

        public
        IProvider<TraversalHint> bind(final IVisualElement p0, final IVisualElement.VisualElementProperty p1);
    }

    public
    interface endExecution_interface extends IAcceptor<IVisualElement>, IFunction<IVisualElement, TraversalHint> {
        public
        TraversalHint endExecution(final IVisualElement p0);

        public
        IUpdateable updateable(final IVisualElement p0);

        public
        IProvider<TraversalHint> bind(final IVisualElement p0);
    }

    public
    interface getProperty_interface extends IAcceptor<Object[]>, IFunction<Object[], TraversalHint> {
        public
        TraversalHint getProperty(final IVisualElement p0,
                                  final IVisualElement.VisualElementProperty p1,
                                  final IVisualElementOverrides.Ref p2);

        public
        IUpdateable updateable(final IVisualElement p0,
                               final IVisualElement.VisualElementProperty p1,
                               final IVisualElementOverrides.Ref p2);

        public
        IProvider<TraversalHint> bind(final IVisualElement p0,
                                      final IVisualElement.VisualElementProperty p1,
                                      final IVisualElementOverrides.Ref p2);
    }

    public
    interface handleKeyboardEvent_interface extends IAcceptor<Object[]>, IFunction<Object[], TraversalHint> {
        public
        TraversalHint handleKeyboardEvent(final IVisualElement p0, final org.eclipse.swt.widgets.Event p1);

        public
        IUpdateable updateable(final IVisualElement p0, final org.eclipse.swt.widgets.Event p1);

        public
        IProvider<TraversalHint> bind(final IVisualElement p0, final org.eclipse.swt.widgets.Event p1);
    }

    public
    interface inspectablePropertiesFor_interface extends IAcceptor<Object[]>, IFunction<Object[], TraversalHint> {
        public
        TraversalHint inspectablePropertiesFor(final IVisualElement p0, final java.util.List p1);

        public
        IUpdateable updateable(final IVisualElement p0, final java.util.List p1);

        public
        IProvider<TraversalHint> bind(final IVisualElement p0, final java.util.List p1);
    }

    public
    interface isHit_interface extends IAcceptor<Object[]>, IFunction<Object[], TraversalHint> {
        public
        TraversalHint isHit(final IVisualElement p0,
                            final org.eclipse.swt.widgets.Event p1,
                            final IVisualElementOverrides.Ref p2);

        public
        IUpdateable updateable(final IVisualElement p0,
                               final org.eclipse.swt.widgets.Event p1,
                               final IVisualElementOverrides.Ref p2);

        public
        IProvider<TraversalHint> bind(final IVisualElement p0,
                                      final org.eclipse.swt.widgets.Event p1,
                                      final IVisualElementOverrides.Ref p2);
    }

    public
    interface menuItemsFor_interface extends IAcceptor<Object[]>, IFunction<Object[], TraversalHint> {
        public
        TraversalHint menuItemsFor(final IVisualElement p0, final java.util.Map p1);

        public
        IUpdateable updateable(final IVisualElement p0, final java.util.Map p1);

        public
        IProvider<TraversalHint> bind(final IVisualElement p0, final java.util.Map p1);
    }

    public
    interface paintNow_interface extends IAcceptor<Object[]>, IFunction<Object[], TraversalHint> {
        public
        TraversalHint paintNow(final IVisualElement p0, final IVisualElement.Rect p1, final boolean p2);

        public
        IUpdateable updateable(final IVisualElement p0, final IVisualElement.Rect p1, final boolean p2);

        public
        IProvider<TraversalHint> bind(final IVisualElement p0, final IVisualElement.Rect p1, final boolean p2);
    }

    public
    interface setProperty_interface extends IAcceptor<Object[]>, IFunction<Object[], TraversalHint> {
        public
        TraversalHint setProperty(final IVisualElement p0,
                                  final IVisualElement.VisualElementProperty p1,
                                  final IVisualElementOverrides.Ref p2);

        public
        IUpdateable updateable(final IVisualElement p0,
                               final IVisualElement.VisualElementProperty p1,
                               final IVisualElementOverrides.Ref p2);

        public
        IProvider<TraversalHint> bind(final IVisualElement p0,
                                      final IVisualElement.VisualElementProperty p1,
                                      final IVisualElementOverrides.Ref p2);
    }

    public
    interface shouldChangeFrame_interface extends IAcceptor<Object[]>, IFunction<Object[], TraversalHint> {
        public
        TraversalHint shouldChangeFrame(final IVisualElement p0,
                                        final IVisualElement.Rect p1,
                                        final IVisualElement.Rect p2,
                                        final boolean p3);

        public
        IUpdateable updateable(final IVisualElement p0,
                               final IVisualElement.Rect p1,
                               final IVisualElement.Rect p2,
                               final boolean p3);

        public
        IProvider<TraversalHint> bind(final IVisualElement p0,
                                      final IVisualElement.Rect p1,
                                      final IVisualElement.Rect p2,
                                      final boolean p3);
    }
}

