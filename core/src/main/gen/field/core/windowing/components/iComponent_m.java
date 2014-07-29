package field.core.windowing.components;

import field.bytecode.mirror.*;
import field.bytecode.mirror.impl.MirrorMethod;
import field.bytecode.mirror.impl.MirrorNoArgsMethod;
import field.bytecode.mirror.impl.MirrorNoReturnMethod;
import field.core.dispatch.IVisualElement;
import field.core.windowing.GLComponentWindow;
import field.launch.IUpdateable;
import field.math.abstraction.IAcceptor;
import field.math.abstraction.IProvider;
import field.namespace.generic.IFunction;
import field.namespace.generic.ReflectionTools;
import org.eclipse.swt.widgets.Event;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public
class iComponent_m {
    public static final Method isHit_m = ReflectionTools.methodOf("isHit",
                                                                  field.core.windowing.components.iComponent.class,
                                                                  org.eclipse.swt.widgets.Event.class);
    public static final MirrorMethod<iComponent, Float, Event>
            isHit_s =
            new MirrorMethod<iComponent, Float, Event>(field.core.windowing.components.iComponent.class,
                                                                                                                         "isHit",
                                                                                                                         new Class[]{org.eclipse.swt.widgets.Event.class});

    public
    interface isHit_interface extends IAcceptor<Event>, IFunction<Event, Float> {
        public
        float isHit(final org.eclipse.swt.widgets.Event p0);

        public
        IUpdateable updateable(final org.eclipse.swt.widgets.Event p0);

        public
        IProvider<Float> bind(final org.eclipse.swt.widgets.Event p0);
    }

    public final isHit_interface isHit;

    public static final Method hit_m = ReflectionTools.methodOf("hit",
                                                                field.core.windowing.components.iComponent.class,
                                                                org.eclipse.swt.widgets.Event.class);
    public static final MirrorMethod<iComponent, iComponent, Event>
            hit_s =
            new MirrorMethod<iComponent, iComponent, Event>(field.core.windowing.components.iComponent.class,
                                                                                                                                                              "hit",
                                                                                                                                                              new Class[]{org.eclipse.swt.widgets.Event.class});

    public
    interface hit_interface extends IAcceptor<Event>, IFunction<Event, iComponent> {
        public
        field.core.windowing.components.iComponent hit(final org.eclipse.swt.widgets.Event p0);

        public
        IUpdateable updateable(final org.eclipse.swt.widgets.Event p0);

        public
        IProvider<iComponent> bind(final org.eclipse.swt.widgets.Event p0);
    }

    public final hit_interface hit;

    public static final Method getBounds_m =
            ReflectionTools.methodOf("getBounds", field.core.windowing.components.iComponent.class);
    public static final MirrorNoArgsMethod<iComponent, IVisualElement.Rect>
            getBounds_s =
            new MirrorNoArgsMethod<iComponent, IVisualElement.Rect>(field.core.windowing.components.iComponent.class,
                                                                                                                                  "getBounds");

    public final IBoundNoArgsMethod<IVisualElement.Rect> getBounds;
    public static final Method setBounds_m = ReflectionTools.methodOf("setBounds",
                                                                      field.core.windowing.components.iComponent.class,
                                                                      IVisualElement.Rect.class);
    public static final MirrorNoReturnMethod<iComponent, IVisualElement.Rect>
            setBounds_s =
            new MirrorNoReturnMethod<iComponent, IVisualElement.Rect>(field.core.windowing.components.iComponent.class,
                                                                                                                                    "setBounds",
                                                                                                                                    new Class[]{IVisualElement.Rect.class});

    public
    interface setBounds_interface extends IAcceptor<IVisualElement.Rect>,
                                          IFunction<IVisualElement.Rect, Object> {
        public
        void setBounds(final IVisualElement.Rect p0);

        public
        IUpdateable updateable(final IVisualElement.Rect p0);
    }

    public final setBounds_interface setBounds;

    public static final Method keyTyped_m = ReflectionTools.methodOf("keyTyped",
                                                                     field.core.windowing.components.iComponent.class,
                                                                     field.core.windowing.GLComponentWindow.ComponentContainer.class,
                                                                     org.eclipse.swt.widgets.Event.class);
    public static final MirrorNoReturnMethod<iComponent, Object[]>
            keyTyped_s =
            new MirrorNoReturnMethod<iComponent, Object[]>(field.core.windowing.components.iComponent.class,
                                                                                                     "keyTyped",
                                                                                                     new Class[]{field.core.windowing.GLComponentWindow.ComponentContainer.class,
                                                                                                                 org.eclipse.swt.widgets.Event.class});

    public
    interface keyTyped_interface extends IAcceptor<Object[]>, IFunction<Object[], Object> {
        public
        void keyTyped(final field.core.windowing.GLComponentWindow.ComponentContainer p0,
                      final org.eclipse.swt.widgets.Event p1);

        public
        IUpdateable updateable(final field.core.windowing.GLComponentWindow.ComponentContainer p0,
                               final org.eclipse.swt.widgets.Event p1);
    }

    public final keyTyped_interface keyTyped;

    public static final Method keyPressed_m = ReflectionTools.methodOf("keyPressed",
                                                                       field.core.windowing.components.iComponent.class,
                                                                       field.core.windowing.GLComponentWindow.ComponentContainer.class,
                                                                       org.eclipse.swt.widgets.Event.class);
    public static final MirrorNoReturnMethod<iComponent, Object[]>
            keyPressed_s =
            new MirrorNoReturnMethod<iComponent, Object[]>(field.core.windowing.components.iComponent.class,
                                                                                                     "keyPressed",
                                                                                                     new Class[]{field.core.windowing.GLComponentWindow.ComponentContainer.class,
                                                                                                                 org.eclipse.swt.widgets.Event.class});

    public
    interface keyPressed_interface extends IAcceptor<Object[]>, IFunction<Object[], Object> {
        public
        void keyPressed(final field.core.windowing.GLComponentWindow.ComponentContainer p0,
                        final org.eclipse.swt.widgets.Event p1);

        public
        IUpdateable updateable(final field.core.windowing.GLComponentWindow.ComponentContainer p0,
                               final org.eclipse.swt.widgets.Event p1);
    }

    public final keyPressed_interface keyPressed;

    public static final Method keyReleased_m = ReflectionTools.methodOf("keyReleased",
                                                                        field.core.windowing.components.iComponent.class,
                                                                        field.core.windowing.GLComponentWindow.ComponentContainer.class,
                                                                        org.eclipse.swt.widgets.Event.class);
    public static final MirrorNoReturnMethod<iComponent, Object[]>
            keyReleased_s =
            new MirrorNoReturnMethod<iComponent, Object[]>(field.core.windowing.components.iComponent.class,
                                                                                                     "keyReleased",
                                                                                                     new Class[]{field.core.windowing.GLComponentWindow.ComponentContainer.class,
                                                                                                                 org.eclipse.swt.widgets.Event.class});

    public
    interface keyReleased_interface extends IAcceptor<Object[]>, IFunction<Object[], Object> {
        public
        void keyReleased(final field.core.windowing.GLComponentWindow.ComponentContainer p0,
                         final org.eclipse.swt.widgets.Event p1);

        public
        IUpdateable updateable(final field.core.windowing.GLComponentWindow.ComponentContainer p0,
                               final org.eclipse.swt.widgets.Event p1);
    }

    public final keyReleased_interface keyReleased;

    public static final Method mouseClicked_m = ReflectionTools.methodOf("mouseClicked",
                                                                         field.core.windowing.components.iComponent.class,
                                                                         field.core.windowing.GLComponentWindow.ComponentContainer.class,
                                                                         org.eclipse.swt.widgets.Event.class);
    public static final MirrorNoReturnMethod<iComponent, Object[]>
            mouseClicked_s =
            new MirrorNoReturnMethod<iComponent, Object[]>(field.core.windowing.components.iComponent.class,
                                                                                                     "mouseClicked",
                                                                                                     new Class[]{field.core.windowing.GLComponentWindow.ComponentContainer.class,
                                                                                                                 org.eclipse.swt.widgets.Event.class});

    public
    interface mouseClicked_interface extends IAcceptor<Object[]>, IFunction<Object[], Object> {
        public
        void mouseClicked(final field.core.windowing.GLComponentWindow.ComponentContainer p0,
                          final org.eclipse.swt.widgets.Event p1);

        public
        IUpdateable updateable(final field.core.windowing.GLComponentWindow.ComponentContainer p0,
                               final org.eclipse.swt.widgets.Event p1);
    }

    public final mouseClicked_interface mouseClicked;

    public static final Method mousePressed_m = ReflectionTools.methodOf("mousePressed",
                                                                         field.core.windowing.components.iComponent.class,
                                                                         field.core.windowing.GLComponentWindow.ComponentContainer.class,
                                                                         org.eclipse.swt.widgets.Event.class);
    public static final MirrorNoReturnMethod<iComponent, Object[]>
            mousePressed_s =
            new MirrorNoReturnMethod<iComponent, Object[]>(field.core.windowing.components.iComponent.class,
                                                                                                     "mousePressed",
                                                                                                     new Class[]{field.core.windowing.GLComponentWindow.ComponentContainer.class,
                                                                                                                 org.eclipse.swt.widgets.Event.class});

    public
    interface mousePressed_interface extends IAcceptor<Object[]>, IFunction<Object[], Object> {
        public
        void mousePressed(final field.core.windowing.GLComponentWindow.ComponentContainer p0,
                          final org.eclipse.swt.widgets.Event p1);

        public
        IUpdateable updateable(final field.core.windowing.GLComponentWindow.ComponentContainer p0,
                               final org.eclipse.swt.widgets.Event p1);
    }

    public final mousePressed_interface mousePressed;

    public static final Method mouseReleased_m = ReflectionTools.methodOf("mouseReleased",
                                                                          field.core.windowing.components.iComponent.class,
                                                                          field.core.windowing.GLComponentWindow.ComponentContainer.class,
                                                                          org.eclipse.swt.widgets.Event.class);
    public static final MirrorNoReturnMethod<iComponent, Object[]>
            mouseReleased_s =
            new MirrorNoReturnMethod<iComponent, Object[]>(field.core.windowing.components.iComponent.class,
                                                                                                     "mouseReleased",
                                                                                                     new Class[]{field.core.windowing.GLComponentWindow.ComponentContainer.class,
                                                                                                                 org.eclipse.swt.widgets.Event.class});

    public
    interface mouseReleased_interface extends IAcceptor<Object[]>, IFunction<Object[], Object> {
        public
        void mouseReleased(final field.core.windowing.GLComponentWindow.ComponentContainer p0,
                           final org.eclipse.swt.widgets.Event p1);

        public
        IUpdateable updateable(final field.core.windowing.GLComponentWindow.ComponentContainer p0,
                               final org.eclipse.swt.widgets.Event p1);
    }

    public final mouseReleased_interface mouseReleased;

    public static final Method mouseEntered_m = ReflectionTools.methodOf("mouseEntered",
                                                                         field.core.windowing.components.iComponent.class,
                                                                         field.core.windowing.GLComponentWindow.ComponentContainer.class,
                                                                         org.eclipse.swt.widgets.Event.class);
    public static final MirrorNoReturnMethod<iComponent, Object[]>
            mouseEntered_s =
            new MirrorNoReturnMethod<iComponent, Object[]>(field.core.windowing.components.iComponent.class,
                                                                                                     "mouseEntered",
                                                                                                     new Class[]{field.core.windowing.GLComponentWindow.ComponentContainer.class,
                                                                                                                 org.eclipse.swt.widgets.Event.class});

    public
    interface mouseEntered_interface extends IAcceptor<Object[]>, IFunction<Object[], Object> {
        public
        void mouseEntered(final field.core.windowing.GLComponentWindow.ComponentContainer p0,
                          final org.eclipse.swt.widgets.Event p1);

        public
        IUpdateable updateable(final field.core.windowing.GLComponentWindow.ComponentContainer p0,
                               final org.eclipse.swt.widgets.Event p1);
    }

    public final mouseEntered_interface mouseEntered;

    public static final Method mouseExited_m = ReflectionTools.methodOf("mouseExited",
                                                                        field.core.windowing.components.iComponent.class,
                                                                        field.core.windowing.GLComponentWindow.ComponentContainer.class,
                                                                        org.eclipse.swt.widgets.Event.class);
    public static final MirrorNoReturnMethod<iComponent, Object[]>
            mouseExited_s =
            new MirrorNoReturnMethod<iComponent, Object[]>(field.core.windowing.components.iComponent.class,
                                                                                                     "mouseExited",
                                                                                                     new Class[]{field.core.windowing.GLComponentWindow.ComponentContainer.class,
                                                                                                                 org.eclipse.swt.widgets.Event.class});

    public
    interface mouseExited_interface extends IAcceptor<Object[]>, IFunction<Object[], Object> {
        public
        void mouseExited(final field.core.windowing.GLComponentWindow.ComponentContainer p0,
                         final org.eclipse.swt.widgets.Event p1);

        public
        IUpdateable updateable(final field.core.windowing.GLComponentWindow.ComponentContainer p0,
                               final org.eclipse.swt.widgets.Event p1);
    }

    public final mouseExited_interface mouseExited;

    public static final Method mouseDragged_m = ReflectionTools.methodOf("mouseDragged",
                                                                         field.core.windowing.components.iComponent.class,
                                                                         field.core.windowing.GLComponentWindow.ComponentContainer.class,
                                                                         org.eclipse.swt.widgets.Event.class);
    public static final MirrorNoReturnMethod<iComponent, Object[]>
            mouseDragged_s =
            new MirrorNoReturnMethod<iComponent, Object[]>(field.core.windowing.components.iComponent.class,
                                                                                                     "mouseDragged",
                                                                                                     new Class[]{field.core.windowing.GLComponentWindow.ComponentContainer.class,
                                                                                                                 org.eclipse.swt.widgets.Event.class});

    public
    interface mouseDragged_interface extends IAcceptor<Object[]>, IFunction<Object[], Object> {
        public
        void mouseDragged(final field.core.windowing.GLComponentWindow.ComponentContainer p0,
                          final org.eclipse.swt.widgets.Event p1);

        public
        IUpdateable updateable(final field.core.windowing.GLComponentWindow.ComponentContainer p0,
                               final org.eclipse.swt.widgets.Event p1);
    }

    public final mouseDragged_interface mouseDragged;

    public static final Method mouseMoved_m = ReflectionTools.methodOf("mouseMoved",
                                                                       field.core.windowing.components.iComponent.class,
                                                                       field.core.windowing.GLComponentWindow.ComponentContainer.class,
                                                                       org.eclipse.swt.widgets.Event.class);
    public static final MirrorNoReturnMethod<iComponent, Object[]>
            mouseMoved_s =
            new MirrorNoReturnMethod<iComponent, Object[]>(field.core.windowing.components.iComponent.class,
                                                                                                     "mouseMoved",
                                                                                                     new Class[]{field.core.windowing.GLComponentWindow.ComponentContainer.class,
                                                                                                                 org.eclipse.swt.widgets.Event.class});

    public
    interface mouseMoved_interface extends IAcceptor<Object[]>, IFunction<Object[], Object> {
        public
        void mouseMoved(final field.core.windowing.GLComponentWindow.ComponentContainer p0,
                        final org.eclipse.swt.widgets.Event p1);

        public
        IUpdateable updateable(final field.core.windowing.GLComponentWindow.ComponentContainer p0,
                               final org.eclipse.swt.widgets.Event p1);
    }

    public final mouseMoved_interface mouseMoved;

    public static final Method beginMouseFocus_m = ReflectionTools.methodOf("beginMouseFocus",
                                                                            field.core.windowing.components.iComponent.class,
                                                                            field.core.windowing.GLComponentWindow.ComponentContainer.class);
    public static final MirrorNoReturnMethod<iComponent, GLComponentWindow.ComponentContainer>
            beginMouseFocus_s =
            new MirrorNoReturnMethod<iComponent, GLComponentWindow.ComponentContainer>(field.core.windowing.components.iComponent.class,
                                                                                                                                                      "beginMouseFocus",
                                                                                                                                                      new Class[]{field.core.windowing.GLComponentWindow.ComponentContainer.class});

    public
    interface beginMouseFocus_interface extends IAcceptor<GLComponentWindow.ComponentContainer>,
                                                IFunction<GLComponentWindow.ComponentContainer, Object> {
        public
        void beginMouseFocus(final field.core.windowing.GLComponentWindow.ComponentContainer p0);

        public
        IUpdateable updateable(final field.core.windowing.GLComponentWindow.ComponentContainer p0);
    }

    public final beginMouseFocus_interface beginMouseFocus;

    public static final Method endMouseFocus_m = ReflectionTools.methodOf("endMouseFocus",
                                                                          field.core.windowing.components.iComponent.class,
                                                                          field.core.windowing.GLComponentWindow.ComponentContainer.class);
    public static final MirrorNoReturnMethod<iComponent, GLComponentWindow.ComponentContainer>
            endMouseFocus_s =
            new MirrorNoReturnMethod<iComponent, GLComponentWindow.ComponentContainer>(field.core.windowing.components.iComponent.class,
                                                                                                                                                      "endMouseFocus",
                                                                                                                                                      new Class[]{field.core.windowing.GLComponentWindow.ComponentContainer.class});

    public
    interface endMouseFocus_interface extends IAcceptor<GLComponentWindow.ComponentContainer>,
                                              IFunction<GLComponentWindow.ComponentContainer, Object> {
        public
        void endMouseFocus(final field.core.windowing.GLComponentWindow.ComponentContainer p0);

        public
        IUpdateable updateable(final field.core.windowing.GLComponentWindow.ComponentContainer p0);
    }

    public final endMouseFocus_interface endMouseFocus;

    public static final Method paint_m = ReflectionTools.methodOf("paint",
                                                                  field.core.windowing.components.iComponent.class,
                                                                  field.core.windowing.GLComponentWindow.ComponentContainer.class,
                                                                  field.math.linalg.iCoordinateFrame.class,
                                                                  boolean.class);
    public static final MirrorNoReturnMethod<iComponent, Object[]> paint_s =
            new MirrorNoReturnMethod<iComponent, Object[]>(field.core.windowing.components.iComponent.class,
                                                                                                     "paint",
                                                                                                     new Class[]{field.core.windowing.GLComponentWindow.ComponentContainer.class,
                                                                                                                 field.math.linalg.iCoordinateFrame.class,
                                                                                                                 boolean.class});

    public
    interface paint_interface extends IAcceptor<Object[]>, IFunction<Object[], Object> {
        public
        void paint(final field.core.windowing.GLComponentWindow.ComponentContainer p0,
                   final field.math.linalg.iCoordinateFrame p1,
                   final boolean p2);

        public
        IUpdateable updateable(final field.core.windowing.GLComponentWindow.ComponentContainer p0,
                               final field.math.linalg.iCoordinateFrame p1,
                               final boolean p2);
    }

    public final paint_interface paint;

    public static final Method handleResize_m = ReflectionTools.methodOf("handleResize",
                                                                         field.core.windowing.components.iComponent.class,
                                                                         java.util.Set.class,
                                                                         float.class,
                                                                         float.class);
    public static final MirrorNoReturnMethod<iComponent, Object[]>
            handleResize_s =
            new MirrorNoReturnMethod<iComponent, Object[]>(field.core.windowing.components.iComponent.class,
                                                                                                     "handleResize",
                                                                                                     new Class[]{java.util.Set.class,
                                                                                                                 float.class,
                                                                                                                 float.class});

    public
    interface handleResize_interface extends IAcceptor<Object[]>, IFunction<Object[], Object> {
        public
        void handleResize(final java.util.Set p0, final float p1, final float p2);

        public
        IUpdateable updateable(final java.util.Set p0, final float p1, final float p2);
    }

    public final handleResize_interface handleResize;

    public static final Method getVisualElement_m =
            ReflectionTools.methodOf("getVisualElement", field.core.windowing.components.iComponent.class);
    public static final MirrorNoArgsMethod<iComponent, IVisualElement>
            getVisualElement_s =
            new MirrorNoArgsMethod<iComponent, IVisualElement>(field.core.windowing.components.iComponent.class,
                                                                                                                             "getVisualElement");

    public
    interface getVisualElement_interface extends IVisualElement,
                                                 IBoundNoArgsMethod<IVisualElement> {
        public
        IVisualElement getVisualElement();
    }

    public final getVisualElement_interface getVisualElement;

    public static final Method setVisualElement_m = ReflectionTools.methodOf("setVisualElement",
                                                                             field.core.windowing.components.iComponent.class,
                                                                             IVisualElement.class);
    public static final MirrorMethod<iComponent, iComponent, IVisualElement>
            setVisualElement_s =
            new MirrorMethod<iComponent, iComponent, IVisualElement>(field.core.windowing.components.iComponent.class,
                                                                                                                                                                   "setVisualElement",
                                                                                                                                                                   new Class[]{IVisualElement.class});

    public
    interface setVisualElement_interface extends IAcceptor<IVisualElement>,
                                                 IFunction<IVisualElement, iComponent> {
        public
        field.core.windowing.components.iComponent setVisualElement(final IVisualElement p0);

        public
        IUpdateable updateable(final IVisualElement p0);

        public
        IProvider<iComponent> bind(final IVisualElement p0);
    }

    public final setVisualElement_interface setVisualElement;

    public
    iComponent_m(final iComponent x) {
        isHit = new isHit_interface() {

            IAcceptor a = isHit_s.acceptor(x);
            IFunction f = isHit_s.function(x);


            public
            float isHit(final org.eclipse.swt.widgets.Event p0) {
                return x.isHit(p0);
            }

            public
            IAcceptor<Event> set(org.eclipse.swt.widgets.Event p) {
                a.set(p);
                return this;
            }

            public
            Float apply(org.eclipse.swt.widgets.Event p) {
                return (Float) f.apply(p);
            }

            public
            IUpdateable updateable(final org.eclipse.swt.widgets.Event p0) {
                return new IUpdateable() {
                    public
                    void update() {
                        isHit(p0);
                    }
                };
            }

            public
            IProvider<Float> bind(final org.eclipse.swt.widgets.Event p0) {
                return new IProvider() {
                    public
                    Object get() {return isHit(p0);}
                };
            }
        };

        hit = new hit_interface() {

            IAcceptor a = hit_s.acceptor(x);
            IFunction f = hit_s.function(x);


            public
            field.core.windowing.components.iComponent hit(final org.eclipse.swt.widgets.Event p0) {
                return x.hit(p0);
            }

            public
            IAcceptor<Event> set(org.eclipse.swt.widgets.Event p) {
                a.set(p);
                return this;
            }

            public
            field.core.windowing.components.iComponent apply(org.eclipse.swt.widgets.Event p) {
                return (field.core.windowing.components.iComponent) f.apply(p);
            }

            public
            IUpdateable updateable(final org.eclipse.swt.widgets.Event p0) {
                return new IUpdateable() {
                    public
                    void update() {
                        hit(p0);
                    }
                };
            }

            public
            IProvider<iComponent> bind(final org.eclipse.swt.widgets.Event p0) {
                return new IProvider() {
                    public
                    Object get() {return hit(p0);}
                };
            }
        };

        getBounds = getBounds_s.bind(x);
        setBounds = new setBounds_interface() {

            IAcceptor a = setBounds_s.acceptor(x);
            IFunction f = setBounds_s.function(x);


            public
            void setBounds(final IVisualElement.Rect p0) {
                x.setBounds(p0);
            }

            public
            IAcceptor<IVisualElement.Rect> set(IVisualElement.Rect p) {
                a.set(p);
                return this;
            }

            public
            Object apply(IVisualElement.Rect p) {
                return f.apply(p);
            }

            public
            IUpdateable updateable(final IVisualElement.Rect p0) {
                return new IUpdateable() {
                    public
                    void update() {
                        setBounds(p0);
                    }
                };
            }
        };

        keyTyped = new keyTyped_interface() {

            IAcceptor a = keyTyped_s.acceptor(x);
            IFunction f = keyTyped_s.function(x);


            public
            void keyTyped(final field.core.windowing.GLComponentWindow.ComponentContainer p0,
                          final org.eclipse.swt.widgets.Event p1) {
                x.keyTyped(p0, p1);
            }

            public
            IAcceptor<Object[]> set(Object[] p) {
                a.set(p);
                return this;
            }

            public
            Object apply(Object[] p) {
                return f.apply(p);
            }

            public
            IUpdateable updateable(final field.core.windowing.GLComponentWindow.ComponentContainer p0,
                                   final org.eclipse.swt.widgets.Event p1) {
                return new IUpdateable() {
                    public
                    void update() {
                        keyTyped(p0, p1);
                    }
                };
            }
        };

        keyPressed = new keyPressed_interface() {

            IAcceptor a = keyPressed_s.acceptor(x);
            IFunction f = keyPressed_s.function(x);


            public
            void keyPressed(final field.core.windowing.GLComponentWindow.ComponentContainer p0,
                            final org.eclipse.swt.widgets.Event p1) {
                x.keyPressed(p0, p1);
            }

            public
            IAcceptor<Object[]> set(Object[] p) {
                a.set(p);
                return this;
            }

            public
            Object apply(Object[] p) {
                return f.apply(p);
            }

            public
            IUpdateable updateable(final field.core.windowing.GLComponentWindow.ComponentContainer p0,
                                   final org.eclipse.swt.widgets.Event p1) {
                return new IUpdateable() {
                    public
                    void update() {
                        keyPressed(p0, p1);
                    }
                };
            }
        };

        keyReleased = new keyReleased_interface() {

            IAcceptor a = keyReleased_s.acceptor(x);
            IFunction f = keyReleased_s.function(x);


            public
            void keyReleased(final field.core.windowing.GLComponentWindow.ComponentContainer p0,
                             final org.eclipse.swt.widgets.Event p1) {
                x.keyReleased(p0, p1);
            }

            public
            IAcceptor<Object[]> set(Object[] p) {
                a.set(p);
                return this;
            }

            public
            Object apply(Object[] p) {
                return f.apply(p);
            }

            public
            IUpdateable updateable(final field.core.windowing.GLComponentWindow.ComponentContainer p0,
                                   final org.eclipse.swt.widgets.Event p1) {
                return new IUpdateable() {
                    public
                    void update() {
                        keyReleased(p0, p1);
                    }
                };
            }
        };

        mouseClicked = new mouseClicked_interface() {

            IAcceptor a = mouseClicked_s.acceptor(x);
            IFunction f = mouseClicked_s.function(x);


            public
            void mouseClicked(final field.core.windowing.GLComponentWindow.ComponentContainer p0,
                              final org.eclipse.swt.widgets.Event p1) {
                x.mouseClicked(p0, p1);
            }

            public
            IAcceptor<Object[]> set(Object[] p) {
                a.set(p);
                return this;
            }

            public
            Object apply(Object[] p) {
                return f.apply(p);
            }

            public
            IUpdateable updateable(final field.core.windowing.GLComponentWindow.ComponentContainer p0,
                                   final org.eclipse.swt.widgets.Event p1) {
                return new IUpdateable() {
                    public
                    void update() {
                        mouseClicked(p0, p1);
                    }
                };
            }
        };

        mousePressed = new mousePressed_interface() {

            IAcceptor a = mousePressed_s.acceptor(x);
            IFunction f = mousePressed_s.function(x);


            public
            void mousePressed(final field.core.windowing.GLComponentWindow.ComponentContainer p0,
                              final org.eclipse.swt.widgets.Event p1) {
                x.mousePressed(p0, p1);
            }

            public
            IAcceptor<Object[]> set(Object[] p) {
                a.set(p);
                return this;
            }

            public
            Object apply(Object[] p) {
                return f.apply(p);
            }

            public
            IUpdateable updateable(final field.core.windowing.GLComponentWindow.ComponentContainer p0,
                                   final org.eclipse.swt.widgets.Event p1) {
                return new IUpdateable() {
                    public
                    void update() {
                        mousePressed(p0, p1);
                    }
                };
            }
        };

        mouseReleased = new mouseReleased_interface() {

            IAcceptor a = mouseReleased_s.acceptor(x);
            IFunction f = mouseReleased_s.function(x);


            public
            void mouseReleased(final field.core.windowing.GLComponentWindow.ComponentContainer p0,
                               final org.eclipse.swt.widgets.Event p1) {
                x.mouseReleased(p0, p1);
            }

            public
            IAcceptor<Object[]> set(Object[] p) {
                a.set(p);
                return this;
            }

            public
            Object apply(Object[] p) {
                return f.apply(p);
            }

            public
            IUpdateable updateable(final field.core.windowing.GLComponentWindow.ComponentContainer p0,
                                   final org.eclipse.swt.widgets.Event p1) {
                return new IUpdateable() {
                    public
                    void update() {
                        mouseReleased(p0, p1);
                    }
                };
            }
        };

        mouseEntered = new mouseEntered_interface() {

            IAcceptor a = mouseEntered_s.acceptor(x);
            IFunction f = mouseEntered_s.function(x);


            public
            void mouseEntered(final field.core.windowing.GLComponentWindow.ComponentContainer p0,
                              final org.eclipse.swt.widgets.Event p1) {
                x.mouseEntered(p0, p1);
            }

            public
            IAcceptor<Object[]> set(Object[] p) {
                a.set(p);
                return this;
            }

            public
            Object apply(Object[] p) {
                return f.apply(p);
            }

            public
            IUpdateable updateable(final field.core.windowing.GLComponentWindow.ComponentContainer p0,
                                   final org.eclipse.swt.widgets.Event p1) {
                return new IUpdateable() {
                    public
                    void update() {
                        mouseEntered(p0, p1);
                    }
                };
            }
        };

        mouseExited = new mouseExited_interface() {

            IAcceptor a = mouseExited_s.acceptor(x);
            IFunction f = mouseExited_s.function(x);


            public
            void mouseExited(final field.core.windowing.GLComponentWindow.ComponentContainer p0,
                             final org.eclipse.swt.widgets.Event p1) {
                x.mouseExited(p0, p1);
            }

            public
            IAcceptor<Object[]> set(Object[] p) {
                a.set(p);
                return this;
            }

            public
            Object apply(Object[] p) {
                return f.apply(p);
            }

            public
            IUpdateable updateable(final field.core.windowing.GLComponentWindow.ComponentContainer p0,
                                   final org.eclipse.swt.widgets.Event p1) {
                return new IUpdateable() {
                    public
                    void update() {
                        mouseExited(p0, p1);
                    }
                };
            }
        };

        mouseDragged = new mouseDragged_interface() {

            IAcceptor a = mouseDragged_s.acceptor(x);
            IFunction f = mouseDragged_s.function(x);


            public
            void mouseDragged(final field.core.windowing.GLComponentWindow.ComponentContainer p0,
                              final org.eclipse.swt.widgets.Event p1) {
                x.mouseDragged(p0, p1);
            }

            public
            IAcceptor<Object[]> set(Object[] p) {
                a.set(p);
                return this;
            }

            public
            Object apply(Object[] p) {
                return f.apply(p);
            }

            public
            IUpdateable updateable(final field.core.windowing.GLComponentWindow.ComponentContainer p0,
                                   final org.eclipse.swt.widgets.Event p1) {
                return new IUpdateable() {
                    public
                    void update() {
                        mouseDragged(p0, p1);
                    }
                };
            }
        };

        mouseMoved = new mouseMoved_interface() {

            IAcceptor a = mouseMoved_s.acceptor(x);
            IFunction f = mouseMoved_s.function(x);


            public
            void mouseMoved(final field.core.windowing.GLComponentWindow.ComponentContainer p0,
                            final org.eclipse.swt.widgets.Event p1) {
                x.mouseMoved(p0, p1);
            }

            public
            IAcceptor<Object[]> set(Object[] p) {
                a.set(p);
                return this;
            }

            public
            Object apply(Object[] p) {
                return f.apply(p);
            }

            public
            IUpdateable updateable(final field.core.windowing.GLComponentWindow.ComponentContainer p0,
                                   final org.eclipse.swt.widgets.Event p1) {
                return new IUpdateable() {
                    public
                    void update() {
                        mouseMoved(p0, p1);
                    }
                };
            }
        };

        beginMouseFocus = new beginMouseFocus_interface() {

            IAcceptor a = beginMouseFocus_s.acceptor(x);
            IFunction f = beginMouseFocus_s.function(x);


            public
            void beginMouseFocus(final field.core.windowing.GLComponentWindow.ComponentContainer p0) {
                x.beginMouseFocus(p0);
            }

            public
            IAcceptor<GLComponentWindow.ComponentContainer> set(field.core.windowing.GLComponentWindow.ComponentContainer p) {
                a.set(p);
                return this;
            }

            public
            Object apply(field.core.windowing.GLComponentWindow.ComponentContainer p) {
                return f.apply(p);
            }

            public
            IUpdateable updateable(final field.core.windowing.GLComponentWindow.ComponentContainer p0) {
                return new IUpdateable() {
                    public
                    void update() {
                        beginMouseFocus(p0);
                    }
                };
            }
        };

        endMouseFocus = new endMouseFocus_interface() {

            IAcceptor a = endMouseFocus_s.acceptor(x);
            IFunction f = endMouseFocus_s.function(x);


            public
            void endMouseFocus(final field.core.windowing.GLComponentWindow.ComponentContainer p0) {
                x.endMouseFocus(p0);
            }

            public
            IAcceptor<GLComponentWindow.ComponentContainer> set(field.core.windowing.GLComponentWindow.ComponentContainer p) {
                a.set(p);
                return this;
            }

            public
            Object apply(field.core.windowing.GLComponentWindow.ComponentContainer p) {
                return f.apply(p);
            }

            public
            IUpdateable updateable(final field.core.windowing.GLComponentWindow.ComponentContainer p0) {
                return new IUpdateable() {
                    public
                    void update() {
                        endMouseFocus(p0);
                    }
                };
            }
        };

        paint = new paint_interface() {

            IAcceptor a = paint_s.acceptor(x);
            IFunction f = paint_s.function(x);


            public
            void paint(final field.core.windowing.GLComponentWindow.ComponentContainer p0,
                       final field.math.linalg.iCoordinateFrame p1,
                       final boolean p2) {
                x.paint(p0, p1, p2);
            }

            public
            IAcceptor<Object[]> set(Object[] p) {
                a.set(p);
                return this;
            }

            public
            Object apply(Object[] p) {
                return f.apply(p);
            }

            public
            IUpdateable updateable(final field.core.windowing.GLComponentWindow.ComponentContainer p0,
                                   final field.math.linalg.iCoordinateFrame p1,
                                   final boolean p2) {
                return new IUpdateable() {
                    public
                    void update() {
                        paint(p0, p1, p2);
                    }
                };
            }
        };

        handleResize = new handleResize_interface() {

            IAcceptor a = handleResize_s.acceptor(x);
            IFunction f = handleResize_s.function(x);


            public
            void handleResize(final java.util.Set p0, final float p1, final float p2) {
                x.handleResize(p0, p1, p2);
            }

            public
            IAcceptor<Object[]> set(Object[] p) {
                a.set(p);
                return this;
            }

            public
            Object apply(Object[] p) {
                return f.apply(p);
            }

            public
            IUpdateable updateable(final java.util.Set p0, final float p1, final float p2) {
                return new IUpdateable() {
                    public
                    void update() {
                        handleResize(p0, p1, p2);
                    }
                };
            }
        };

        {
            final IBoundNoArgsMethod<IVisualElement> bound = getVisualElement_s.bind(x);
            getVisualElement = (getVisualElement_interface) Proxy.newProxyInstance(x.getClass().getClassLoader(),
                                                                                   new Class[]{getVisualElement_interface.class},
                                                                                   new InvocationHandler() {
                                                                                       public
                                                                                       Object invoke(Object proxy,
                                                                                                     Method method,
                                                                                                     Object[] args)
                                                                                               throws Throwable {

                                                                                           if ("get".equals(method.getName())
                                                                                               && (args.length == 0))
                                                                                               return bound.get();

                                                                                           return method.invoke(bound.get(),
                                                                                                                args);
                                                                                       }
                                                                                   });
        }
        setVisualElement = new setVisualElement_interface() {

            IAcceptor a = setVisualElement_s.acceptor(x);
            IFunction f = setVisualElement_s.function(x);


            public
            field.core.windowing.components.iComponent setVisualElement(final IVisualElement p0) {
                return x.setVisualElement(p0);
            }

            public
            IAcceptor<IVisualElement> set(IVisualElement p) {
                a.set(p);
                return this;
            }

            public
            field.core.windowing.components.iComponent apply(IVisualElement p) {
                return (field.core.windowing.components.iComponent) f.apply(p);
            }

            public
            IUpdateable updateable(final IVisualElement p0) {
                return new IUpdateable() {
                    public
                    void update() {
                        setVisualElement(p0);
                    }
                };
            }

            public
            IProvider<iComponent> bind(final IVisualElement p0) {
                return new IProvider() {
                    public
                    Object get() {return setVisualElement(p0);}
                };
            }
        };


    }
}

