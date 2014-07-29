package field.core.plugins.constrain;

import field.bytecode.mirror.impl.MirrorNoReturnMethod;
import field.core.dispatch.IVisualElement;
import field.launch.IUpdateable;
import field.math.abstraction.IAcceptor;
import field.namespace.generic.IFunction;
import field.namespace.generic.ReflectionTools;

import java.lang.reflect.Method;

public
class ComplexConstraints_m {
    public static final Method addEditFor_m = ReflectionTools.methodOf("addEditFor",
                                                                       field.core.plugins.constrain.ComplexConstraints.class,
                                                                       field.core.plugins.constrain.ComplexConstraints.VariablesForRect.class,
                                                                       IVisualElement.Rect.class);
    public static final MirrorNoReturnMethod<ComplexConstraints, Object[]>
            addEditFor_s =
            new MirrorNoReturnMethod<ComplexConstraints, Object[]>(field.core.plugins.constrain.ComplexConstraints.class,
                                                                                                          "addEditFor",
                                                                                                          new Class[]{field.core.plugins.constrain.ComplexConstraints.VariablesForRect.class,
                                                                                                                      IVisualElement.Rect.class});

    public
    interface addEditFor_interface extends IAcceptor<Object[]>, IFunction<Object[], Object> {
        public
        void addEditFor(final field.core.plugins.constrain.ComplexConstraints.VariablesForRect p0,
                        final IVisualElement.Rect p1);

        public
        IUpdateable updateable(final field.core.plugins.constrain.ComplexConstraints.VariablesForRect p0,
                               final IVisualElement.Rect p1);
    }

    public final addEditFor_interface addEditFor;

    public static final Method addSuggestionFor_m = ReflectionTools.methodOf("addSuggestionFor",
                                                                             field.core.plugins.constrain.ComplexConstraints.class,
                                                                             field.core.plugins.constrain.ComplexConstraints.VariablesForRect.class,
                                                                             IVisualElement.Rect.class);
    public static final MirrorNoReturnMethod<ComplexConstraints, Object[]>
            addSuggestionFor_s =
            new MirrorNoReturnMethod<ComplexConstraints, Object[]>(field.core.plugins.constrain.ComplexConstraints.class,
                                                                                                          "addSuggestionFor",
                                                                                                          new Class[]{field.core.plugins.constrain.ComplexConstraints.VariablesForRect.class,
                                                                                                                      IVisualElement.Rect.class});

    public
    interface addSuggestionFor_interface extends IAcceptor<Object[]>, IFunction<Object[], Object> {
        public
        void addSuggestionFor(final field.core.plugins.constrain.ComplexConstraints.VariablesForRect p0,
                              final IVisualElement.Rect p1);

        public
        IUpdateable updateable(final field.core.plugins.constrain.ComplexConstraints.VariablesForRect p0,
                               final IVisualElement.Rect p1);
    }

    public final addSuggestionFor_interface addSuggestionFor;

    public static final Method updateFrameFromVariables_m = ReflectionTools.methodOf("updateFrameFromVariables",
                                                                                     field.core.plugins.constrain.ComplexConstraints.class,
                                                                                     field.core.plugins.constrain.ComplexConstraints.VariablesForRect.class);
    public static final MirrorNoReturnMethod<ComplexConstraints, ComplexConstraints.VariablesForRect>
            updateFrameFromVariables_s =
            new MirrorNoReturnMethod<ComplexConstraints, ComplexConstraints.VariablesForRect>(field.core.plugins.constrain.ComplexConstraints.class,
                                                                                                                                                                  "updateFrameFromVariables",
                                                                                                                                                                  new Class[]{field.core.plugins.constrain.ComplexConstraints.VariablesForRect.class});

    public
    interface updateFrameFromVariables_interface extends IAcceptor<ComplexConstraints.VariablesForRect>,
                                                         IFunction<ComplexConstraints.VariablesForRect, Object> {
        public
        void updateFrameFromVariables(final field.core.plugins.constrain.ComplexConstraints.VariablesForRect p0);

        public
        IUpdateable updateable(final field.core.plugins.constrain.ComplexConstraints.VariablesForRect p0);
    }

    public final updateFrameFromVariables_interface updateFrameFromVariables;

    public static final Method updateVariablesFromFrame_m = ReflectionTools.methodOf("updateVariablesFromFrame",
                                                                                     field.core.plugins.constrain.ComplexConstraints.class,
                                                                                     field.core.plugins.constrain.ComplexConstraints.VariablesForRect.class,
                                                                                     IVisualElement.Rect.class);
    public static final MirrorNoReturnMethod<ComplexConstraints, Object[]>
            updateVariablesFromFrame_s =
            new MirrorNoReturnMethod<ComplexConstraints, Object[]>(field.core.plugins.constrain.ComplexConstraints.class,
                                                                                                          "updateVariablesFromFrame",
                                                                                                          new Class[]{field.core.plugins.constrain.ComplexConstraints.VariablesForRect.class,
                                                                                                                      IVisualElement.Rect.class});

    public
    interface updateVariablesFromFrame_interface extends IAcceptor<Object[]>, IFunction<Object[], Object> {
        public
        void updateVariablesFromFrame(final field.core.plugins.constrain.ComplexConstraints.VariablesForRect p0,
                                      final IVisualElement.Rect p1);

        public
        IUpdateable updateable(final field.core.plugins.constrain.ComplexConstraints.VariablesForRect p0,
                               final IVisualElement.Rect p1);
    }

    public final updateVariablesFromFrame_interface updateVariablesFromFrame;

    public
    ComplexConstraints_m(final ComplexConstraints x) {
        addEditFor = new addEditFor_interface() {

            IAcceptor a = addEditFor_s.acceptor(x);
            IFunction f = addEditFor_s.function(x);


            public
            void addEditFor(final field.core.plugins.constrain.ComplexConstraints.VariablesForRect p0,
                            final IVisualElement.Rect p1) {
                x.addEditFor(p0, p1);
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
            IUpdateable updateable(final field.core.plugins.constrain.ComplexConstraints.VariablesForRect p0,
                                   final IVisualElement.Rect p1) {
                return new IUpdateable() {
                    public
                    void update() {
                        addEditFor(p0, p1);
                    }
                };
            }
        };

        addSuggestionFor = new addSuggestionFor_interface() {

            IAcceptor a = addSuggestionFor_s.acceptor(x);
            IFunction f = addSuggestionFor_s.function(x);


            public
            void addSuggestionFor(final field.core.plugins.constrain.ComplexConstraints.VariablesForRect p0,
                                  final IVisualElement.Rect p1) {
                x.addSuggestionFor(p0, p1);
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
            IUpdateable updateable(final field.core.plugins.constrain.ComplexConstraints.VariablesForRect p0,
                                   final IVisualElement.Rect p1) {
                return new IUpdateable() {
                    public
                    void update() {
                        addSuggestionFor(p0, p1);
                    }
                };
            }
        };

        updateFrameFromVariables = new updateFrameFromVariables_interface() {

            IAcceptor a = updateFrameFromVariables_s.acceptor(x);
            IFunction f = updateFrameFromVariables_s.function(x);


            public
            void updateFrameFromVariables(final field.core.plugins.constrain.ComplexConstraints.VariablesForRect p0) {
                x.updateFrameFromVariables(p0);
            }

            public
            IAcceptor<ComplexConstraints.VariablesForRect> set(field.core.plugins.constrain.ComplexConstraints.VariablesForRect p) {
                a.set(p);
                return this;
            }

            public
            Object apply(field.core.plugins.constrain.ComplexConstraints.VariablesForRect p) {
                return f.apply(p);
            }

            public
            IUpdateable updateable(final field.core.plugins.constrain.ComplexConstraints.VariablesForRect p0) {
                return new IUpdateable() {
                    public
                    void update() {
                        updateFrameFromVariables(p0);
                    }
                };
            }
        };

        updateVariablesFromFrame = new updateVariablesFromFrame_interface() {

            IAcceptor a = updateVariablesFromFrame_s.acceptor(x);
            IFunction f = updateVariablesFromFrame_s.function(x);


            public
            void updateVariablesFromFrame(final field.core.plugins.constrain.ComplexConstraints.VariablesForRect p0,
                                          final IVisualElement.Rect p1) {
                x.updateVariablesFromFrame(p0, p1);
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
            IUpdateable updateable(final field.core.plugins.constrain.ComplexConstraints.VariablesForRect p0,
                                   final IVisualElement.Rect p1) {
                return new IUpdateable() {
                    public
                    void update() {
                        updateVariablesFromFrame(p0, p1);
                    }
                };
            }
        };


    }
}

