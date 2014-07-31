package field.core.plugins.drawing.embedded;

import field.bytecode.mirror.impl.MirrorMethod;
import field.bytecode.mirror.impl.MirrorNoReturnMethod;
import field.launch.IUpdateable;
import field.math.abstraction.IAcceptor;
import field.math.abstraction.IProvider;
import field.namespace.generic.IFunction;
import field.namespace.generic.ReflectionTools;

import java.lang.reflect.Method;
import java.util.Map;

public
class iNodeCallBack_m {
    public static final Method mouseDown_m = ReflectionTools.methodOf("mouseDown",
                                                                      field.core.plugins.drawing.embedded.iNodeCallBack.class,
                                                                      field.core.plugins.drawing.opengl.CachedLine.class,
                                                                      field.core.plugins.drawing.opengl.CachedLine.Event.class,
                                                                      field.math.linalg.Vector2.class,
                                                                      java.awt.event.MouseEvent.class);
    public static final MirrorNoReturnMethod<iNodeCallBack, Object[]>
            mouseDown_s =
            new MirrorNoReturnMethod<iNodeCallBack, Object[]>(field.core.plugins.drawing.embedded.iNodeCallBack.class,
                                                                                                            "mouseDown",
                                                                                                            new Class[]{field.core.plugins.drawing.opengl.CachedLine.class,
                                                                                                                        field.core.plugins.drawing.opengl.CachedLine.Event.class,
                                                                                                                        field.math.linalg.Vector2.class,
                                                                                                                        java.awt.event.MouseEvent.class});

    public
    interface mouseDown_interface extends IAcceptor<Object[]>, IFunction<Object[], Object> {
        public
        void mouseDown(final field.core.plugins.drawing.opengl.CachedLine p0,
                       final field.core.plugins.drawing.opengl.CachedLine.Event p1,
                       final field.math.linalg.Vector2 p2,
                       final java.awt.event.MouseEvent p3);

        public
        IUpdateable updateable(final field.core.plugins.drawing.opengl.CachedLine p0,
                               final field.core.plugins.drawing.opengl.CachedLine.Event p1,
                               final field.math.linalg.Vector2 p2,
                               final java.awt.event.MouseEvent p3);
    }

    public final mouseDown_interface mouseDown;

    public static final Method mouseDragged_m = ReflectionTools.methodOf("mouseDragged",
                                                                         field.core.plugins.drawing.embedded.iNodeCallBack.class,
                                                                         field.core.plugins.drawing.opengl.CachedLine.class,
                                                                         field.core.plugins.drawing.opengl.CachedLine.Event.class,
                                                                         field.math.linalg.Vector2.class,
                                                                         java.awt.event.MouseEvent.class);
    public static final MirrorNoReturnMethod<iNodeCallBack, Object[]>
            mouseDragged_s =
            new MirrorNoReturnMethod<iNodeCallBack, Object[]>(field.core.plugins.drawing.embedded.iNodeCallBack.class,
                                                                                                            "mouseDragged",
                                                                                                            new Class[]{field.core.plugins.drawing.opengl.CachedLine.class,
                                                                                                                        field.core.plugins.drawing.opengl.CachedLine.Event.class,
                                                                                                                        field.math.linalg.Vector2.class,
                                                                                                                        java.awt.event.MouseEvent.class});

    public
    interface mouseDragged_interface extends IAcceptor<Object[]>, IFunction<Object[], Object> {
        public
        void mouseDragged(final field.core.plugins.drawing.opengl.CachedLine p0,
                          final field.core.plugins.drawing.opengl.CachedLine.Event p1,
                          final field.math.linalg.Vector2 p2,
                          final java.awt.event.MouseEvent p3);

        public
        IUpdateable updateable(final field.core.plugins.drawing.opengl.CachedLine p0,
                               final field.core.plugins.drawing.opengl.CachedLine.Event p1,
                               final field.math.linalg.Vector2 p2,
                               final java.awt.event.MouseEvent p3);
    }

    public final mouseDragged_interface mouseDragged;

    public static final Method mouseUp_m = ReflectionTools.methodOf("mouseUp",
                                                                    field.core.plugins.drawing.embedded.iNodeCallBack.class,
                                                                    field.core.plugins.drawing.opengl.CachedLine.class,
                                                                    field.core.plugins.drawing.opengl.CachedLine.Event.class,
                                                                    field.math.linalg.Vector2.class,
                                                                    java.awt.event.MouseEvent.class);
    public static final MirrorNoReturnMethod<iNodeCallBack, Object[]>
            mouseUp_s =
            new MirrorNoReturnMethod<iNodeCallBack, Object[]>(field.core.plugins.drawing.embedded.iNodeCallBack.class,
                                                                                                            "mouseUp",
                                                                                                            new Class[]{field.core.plugins.drawing.opengl.CachedLine.class,
                                                                                                                        field.core.plugins.drawing.opengl.CachedLine.Event.class,
                                                                                                                        field.math.linalg.Vector2.class,
                                                                                                                        java.awt.event.MouseEvent.class});

    public
    interface mouseUp_interface extends IAcceptor<Object[]>, IFunction<Object[], Object> {
        public
        void mouseUp(final field.core.plugins.drawing.opengl.CachedLine p0,
                     final field.core.plugins.drawing.opengl.CachedLine.Event p1,
                     final field.math.linalg.Vector2 p2,
                     final java.awt.event.MouseEvent p3);

        public
        IUpdateable updateable(final field.core.plugins.drawing.opengl.CachedLine p0,
                               final field.core.plugins.drawing.opengl.CachedLine.Event p1,
                               final field.math.linalg.Vector2 p2,
                               final java.awt.event.MouseEvent p3);
    }

    public final mouseUp_interface mouseUp;

    public static final Method mouseClicked_m = ReflectionTools.methodOf("mouseClicked",
                                                                         field.core.plugins.drawing.embedded.iNodeCallBack.class,
                                                                         field.core.plugins.drawing.opengl.CachedLine.class,
                                                                         field.core.plugins.drawing.opengl.CachedLine.Event.class,
                                                                         field.math.linalg.Vector2.class,
                                                                         java.awt.event.MouseEvent.class);
    public static final MirrorNoReturnMethod<iNodeCallBack, Object[]>
            mouseClicked_s =
            new MirrorNoReturnMethod<iNodeCallBack, Object[]>(field.core.plugins.drawing.embedded.iNodeCallBack.class,
                                                                                                            "mouseClicked",
                                                                                                            new Class[]{field.core.plugins.drawing.opengl.CachedLine.class,
                                                                                                                        field.core.plugins.drawing.opengl.CachedLine.Event.class,
                                                                                                                        field.math.linalg.Vector2.class,
                                                                                                                        java.awt.event.MouseEvent.class});

    public
    interface mouseClicked_interface extends IAcceptor<Object[]>, IFunction<Object[], Object> {
        public
        void mouseClicked(final field.core.plugins.drawing.opengl.CachedLine p0,
                          final field.core.plugins.drawing.opengl.CachedLine.Event p1,
                          final field.math.linalg.Vector2 p2,
                          final java.awt.event.MouseEvent p3);

        public
        IUpdateable updateable(final field.core.plugins.drawing.opengl.CachedLine p0,
                               final field.core.plugins.drawing.opengl.CachedLine.Event p1,
                               final field.math.linalg.Vector2 p2,
                               final java.awt.event.MouseEvent p3);
    }

    public final mouseClicked_interface mouseClicked;

    public static final Method menu_m = ReflectionTools.methodOf("menu",
                                                                 field.core.plugins.drawing.embedded.iNodeCallBack.class,
                                                                 field.core.plugins.drawing.opengl.CachedLine.class,
                                                                 field.core.plugins.drawing.opengl.CachedLine.Event.class,
                                                                 field.math.linalg.Vector2.class,
                                                                 java.awt.event.MouseEvent.class);
    public static final MirrorMethod<iNodeCallBack, Object[], Map>
            menu_s =
            new MirrorMethod<iNodeCallBack, Object[], Map>(field.core.plugins.drawing.embedded.iNodeCallBack.class,
                                                                                                                   "menu",
                                                                                                                   new Class[]{field.core.plugins.drawing.opengl.CachedLine.class,
                                                                                                                               field.core.plugins.drawing.opengl.CachedLine.Event.class,
                                                                                                                               field.math.linalg.Vector2.class,
                                                                                                                               java.awt.event.MouseEvent.class});

    public
    interface menu_interface extends IAcceptor<Object[]>, IFunction<Object[], Map> {
        public
        java.util.Map<java.lang.String, java.lang.Object> menu(final field.core.plugins.drawing.opengl.CachedLine p0,
                                                               final field.core.plugins.drawing.opengl.CachedLine.Event p1,
                                                               final field.math.linalg.Vector2 p2,
                                                               final java.awt.event.MouseEvent p3);

        public
        IUpdateable updateable(final field.core.plugins.drawing.opengl.CachedLine p0,
                               final field.core.plugins.drawing.opengl.CachedLine.Event p1,
                               final field.math.linalg.Vector2 p2,
                               final java.awt.event.MouseEvent p3);

        public
        IProvider<Map> bind(final field.core.plugins.drawing.opengl.CachedLine p0,
                                      final field.core.plugins.drawing.opengl.CachedLine.Event p1,
                                      final field.math.linalg.Vector2 p2,
                                      final java.awt.event.MouseEvent p3);
    }

    public final menu_interface menu;

    public
    iNodeCallBack_m(final iNodeCallBack x) {
        mouseDown = new mouseDown_interface() {

            IAcceptor a = mouseDown_s.acceptor(x);
            IFunction f = mouseDown_s.function(x);


            public
            void mouseDown(final field.core.plugins.drawing.opengl.CachedLine p0,
                           final field.core.plugins.drawing.opengl.CachedLine.Event p1,
                           final field.math.linalg.Vector2 p2,
                           final java.awt.event.MouseEvent p3) {
                x.mouseDown(p0, p1, p2, p3);
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
            IUpdateable updateable(final field.core.plugins.drawing.opengl.CachedLine p0,
                                   final field.core.plugins.drawing.opengl.CachedLine.Event p1,
                                   final field.math.linalg.Vector2 p2,
                                   final java.awt.event.MouseEvent p3) {
                return new IUpdateable() {
                    public
                    void update() {
                        mouseDown(p0, p1, p2, p3);
                    }
                };
            }
        };

        mouseDragged = new mouseDragged_interface() {

            IAcceptor a = mouseDragged_s.acceptor(x);
            IFunction f = mouseDragged_s.function(x);


            public
            void mouseDragged(final field.core.plugins.drawing.opengl.CachedLine p0,
                              final field.core.plugins.drawing.opengl.CachedLine.Event p1,
                              final field.math.linalg.Vector2 p2,
                              final java.awt.event.MouseEvent p3) {
                x.mouseDragged(p0, p1, p2, p3);
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
            IUpdateable updateable(final field.core.plugins.drawing.opengl.CachedLine p0,
                                   final field.core.plugins.drawing.opengl.CachedLine.Event p1,
                                   final field.math.linalg.Vector2 p2,
                                   final java.awt.event.MouseEvent p3) {
                return new IUpdateable() {
                    public
                    void update() {
                        mouseDragged(p0, p1, p2, p3);
                    }
                };
            }
        };

        mouseUp = new mouseUp_interface() {

            IAcceptor a = mouseUp_s.acceptor(x);
            IFunction f = mouseUp_s.function(x);


            public
            void mouseUp(final field.core.plugins.drawing.opengl.CachedLine p0,
                         final field.core.plugins.drawing.opengl.CachedLine.Event p1,
                         final field.math.linalg.Vector2 p2,
                         final java.awt.event.MouseEvent p3) {
                x.mouseUp(p0, p1, p2, p3);
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
            IUpdateable updateable(final field.core.plugins.drawing.opengl.CachedLine p0,
                                   final field.core.plugins.drawing.opengl.CachedLine.Event p1,
                                   final field.math.linalg.Vector2 p2,
                                   final java.awt.event.MouseEvent p3) {
                return new IUpdateable() {
                    public
                    void update() {
                        mouseUp(p0, p1, p2, p3);
                    }
                };
            }
        };

        mouseClicked = new mouseClicked_interface() {

            IAcceptor a = mouseClicked_s.acceptor(x);
            IFunction f = mouseClicked_s.function(x);


            public
            void mouseClicked(final field.core.plugins.drawing.opengl.CachedLine p0,
                              final field.core.plugins.drawing.opengl.CachedLine.Event p1,
                              final field.math.linalg.Vector2 p2,
                              final java.awt.event.MouseEvent p3) {
                x.mouseClicked(p0, p1, p2, p3);
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
            IUpdateable updateable(final field.core.plugins.drawing.opengl.CachedLine p0,
                                   final field.core.plugins.drawing.opengl.CachedLine.Event p1,
                                   final field.math.linalg.Vector2 p2,
                                   final java.awt.event.MouseEvent p3) {
                return new IUpdateable() {
                    public
                    void update() {
                        mouseClicked(p0, p1, p2, p3);
                    }
                };
            }
        };

        menu = new menu_interface() {

            IAcceptor a = menu_s.acceptor(x);
            IFunction f = menu_s.function(x);


            public
            java.util.Map<java.lang.String, java.lang.Object> menu(final field.core.plugins.drawing.opengl.CachedLine p0,
                                                                   final field.core.plugins.drawing.opengl.CachedLine.Event p1,
                                                                   final field.math.linalg.Vector2 p2,
                                                                   final java.awt.event.MouseEvent p3) {
                return x.menu(p0, p1, p2, p3);
            }

            public
            IAcceptor<Object[]> set(Object[] p) {
                a.set(p);
                return this;
            }

            public
            java.util.Map apply(Object[] p) {
                return (java.util.Map) f.apply(p);
            }

            public
            IUpdateable updateable(final field.core.plugins.drawing.opengl.CachedLine p0,
                                   final field.core.plugins.drawing.opengl.CachedLine.Event p1,
                                   final field.math.linalg.Vector2 p2,
                                   final java.awt.event.MouseEvent p3) {
                return new IUpdateable() {
                    public
                    void update() {
                        menu(p0, p1, p2, p3);
                    }
                };
            }

            public
            IProvider<Map> bind(final field.core.plugins.drawing.opengl.CachedLine p0,
                                          final field.core.plugins.drawing.opengl.CachedLine.Event p1,
                                          final field.math.linalg.Vector2 p2,
                                          final java.awt.event.MouseEvent p3) {
                return new IProvider() {
                    public
                    Object get() {return menu(p0, p1, p2, p3);}
                };
            }
        };


    }
}

