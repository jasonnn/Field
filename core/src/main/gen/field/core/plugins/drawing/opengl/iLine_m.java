package field.core.plugins.drawing.opengl;

import field.bytecode.mirror.impl.MirrorNoReturnMethod;
import field.bytecode.mirror.impl.MirrorNoReturnNoArgsMethod;
import field.launch.IUpdateable;
import field.math.abstraction.IAcceptor;
import field.namespace.generic.IFunction;
import field.namespace.generic.ReflectionTools;

import java.lang.reflect.Method;

public
class iLine_m {
    public static final Method moveTo_m = ReflectionTools.methodOf("moveTo", iLine.class, float.class, float.class);

    public static final MirrorNoReturnMethod<iLine, Object[]> moveTo_s =
            new MirrorNoReturnMethod<iLine, Object[]>(iLine.class, "moveTo", float.class, float.class);

    public
    interface moveTo_interface extends IAcceptor<Object[]>, IFunction<Object[], Object> {
        public
        void moveTo(final float p0, final float p1);

        public
        IUpdateable updateable(final float p0, final float p1);
    }

    public final moveTo_interface moveTo;

    public static final Method lineTo_m = ReflectionTools.methodOf("lineTo", iLine.class, float.class, float.class);

    public static final MirrorNoReturnMethod<iLine, Object[]> lineTo_s =
            new MirrorNoReturnMethod<iLine, Object[]>(iLine.class, "lineTo", float.class, float.class);

    public
    interface lineTo_interface extends IAcceptor<Object[]>, IFunction<Object[], Object> {
        public
        void lineTo(final float p0, final float p1);

        public
        IUpdateable updateable(final float p0, final float p1);
    }

    public final lineTo_interface lineTo;

    public static final Method cubicTo_m = ReflectionTools.methodOf("cubicTo",
                                                                    iLine.class,
                                                                    float.class,
                                                                    float.class,
                                                                    float.class,
                                                                    float.class,
                                                                    float.class,
                                                                    float.class);

    public static final MirrorNoReturnMethod<iLine, Object[]> cubicTo_s =
            new MirrorNoReturnMethod<iLine, Object[]>(iLine.class,
                                                      "cubicTo",
                                                      float.class,
                                                      float.class,
                                                      float.class,
                                                      float.class,
                                                      float.class,
                                                      float.class);

    public
    interface cubicTo_interface extends IAcceptor<Object[]>, IFunction<Object[], Object> {
        public
        void cubicTo(final float p0, final float p1, final float p2, final float p3, final float p4, final float p5);

        public
        IUpdateable updateable(final float p0,
                               final float p1,
                               final float p2,
                               final float p3,
                               final float p4,
                               final float p5);
    }

    public final cubicTo_interface cubicTo;

    public static final Method setPointAttribute_m = ReflectionTools.methodOf("setPointAttribute",
                                                                              iLine.class,
                                                                              field.util.Dict.Prop.class,
                                                                              Object.class);

    public static final MirrorNoReturnMethod<iLine, Object[]> setPointAttribute_s =
            new MirrorNoReturnMethod<iLine, Object[]>(iLine.class,
                                                      "setPointAttribute",
                                                      field.util.Dict.Prop.class,
                                                      Object.class);

    public
    interface setPointAttribute_interface extends IAcceptor<Object[]>, IFunction<Object[], Object> {
        public
        void setPointAttribute(final field.util.Dict.Prop p0, final Object p1);

        public
        IUpdateable updateable(final field.util.Dict.Prop p0, final Object p1);
    }

    public final setPointAttribute_interface setPointAttribute;

    public static final Method close_m = ReflectionTools.methodOf("close", iLine.class);

    public static final MirrorNoReturnNoArgsMethod<iLine> close_s = new MirrorNoReturnNoArgsMethod<iLine>(iLine.class,
                                                                                                          "close");

    public final IUpdateable close;

    public
    iLine_m(final iLine x) {
        moveTo = new moveTo_interface() {

            IAcceptor a = moveTo_s.acceptor(x);

            IFunction f = moveTo_s.function(x);


            public
            void moveTo(final float p0, final float p1) {
                x.moveTo(p0, p1);
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
            IUpdateable updateable(final float p0, final float p1) {
                return new IUpdateable() {
                    public
                    void update() {
                        moveTo(p0, p1);
                    }
                };
            }
        };

        lineTo = new lineTo_interface() {

            IAcceptor a = lineTo_s.acceptor(x);

            IFunction f = lineTo_s.function(x);


            public
            void lineTo(final float p0, final float p1) {
                x.lineTo(p0, p1);
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
            IUpdateable updateable(final float p0, final float p1) {
                return new IUpdateable() {
                    public
                    void update() {
                        lineTo(p0, p1);
                    }
                };
            }
        };

        cubicTo = new cubicTo_interface() {

            IAcceptor a = cubicTo_s.acceptor(x);

            IFunction f = cubicTo_s.function(x);


            public
            void cubicTo(final float p0,
                         final float p1,
                         final float p2,
                         final float p3,
                         final float p4,
                         final float p5) {
                x.cubicTo(p0, p1, p2, p3, p4, p5);
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
            IUpdateable updateable(final float p0,
                                   final float p1,
                                   final float p2,
                                   final float p3,
                                   final float p4,
                                   final float p5) {
                return new IUpdateable() {
                    public
                    void update() {
                        cubicTo(p0, p1, p2, p3, p4, p5);
                    }
                };
            }
        };

        setPointAttribute = new setPointAttribute_interface() {

            IAcceptor a = setPointAttribute_s.acceptor(x);

            IFunction f = setPointAttribute_s.function(x);


            public
            void setPointAttribute(final field.util.Dict.Prop p0, final Object p1) {
                x.setPointAttribute(p0, p1);
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
            IUpdateable updateable(final field.util.Dict.Prop p0, final Object p1) {
                return new IUpdateable() {
                    public
                    void update() {
                        setPointAttribute(p0, p1);
                    }
                };
            }
        };

        close = close_s.updateable(x);

    }
}

