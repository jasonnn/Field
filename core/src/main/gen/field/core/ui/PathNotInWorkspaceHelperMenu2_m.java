package field.core.ui;

import field.bytecode.mirror.impl.MirrorNoReturnMethod;
import field.launch.IUpdateable;
import field.math.abstraction.IAcceptor;
import field.namespace.generic.IFunction;
import field.namespace.generic.ReflectionTools;

import java.io.File;
import java.lang.reflect.Method;

public
class PathNotInWorkspaceHelperMenu2_m {
    public static final Method move_m =
            ReflectionTools.methodOf("move", field.core.ui.PathNotInWorkspaceHelperMenu2.class, java.io.File.class);
    public static final MirrorNoReturnMethod<PathNotInWorkspaceHelperMenu2, File>
            move_s =
            new MirrorNoReturnMethod<PathNotInWorkspaceHelperMenu2, File>(field.core.ui.PathNotInWorkspaceHelperMenu2.class,
                                                                                                          "move",
                                                                                                          new Class[]{java.io.File.class});

    public
    interface move_interface extends IAcceptor<File>, IFunction<File, Object> {
        public
        void move(final java.io.File p0);

        public
        IUpdateable updateable(final java.io.File p0);
    }

    public final move_interface move;

    public static final Method copy_m =
            ReflectionTools.methodOf("copy", field.core.ui.PathNotInWorkspaceHelperMenu2.class, java.io.File.class);
    public static final MirrorNoReturnMethod<PathNotInWorkspaceHelperMenu2, File>
            copy_s =
            new MirrorNoReturnMethod<PathNotInWorkspaceHelperMenu2, File>(field.core.ui.PathNotInWorkspaceHelperMenu2.class,
                                                                                                          "copy",
                                                                                                          new Class[]{java.io.File.class});

    public
    interface copy_interface extends IAcceptor<File>, IFunction<File, Object> {
        public
        void copy(final java.io.File p0);

        public
        IUpdateable updateable(final java.io.File p0);
    }

    public final copy_interface copy;

    public
    PathNotInWorkspaceHelperMenu2_m(final PathNotInWorkspaceHelperMenu2 x) {
        move = new move_interface() {

            IAcceptor a = move_s.acceptor(x);
            IFunction f = move_s.function(x);


            public
            void move(final java.io.File p0) {
                x.move(p0);
            }

            public
            IAcceptor<File> set(java.io.File p) {
                a.set(p);
                return this;
            }

            public
            Object apply(java.io.File p) {
                return f.apply(p);
            }

            public
            IUpdateable updateable(final java.io.File p0) {
                return new IUpdateable() {
                    public
                    void update() {
                        move(p0);
                    }
                };
            }
        };

        copy = new copy_interface() {

            IAcceptor a = copy_s.acceptor(x);
            IFunction f = copy_s.function(x);


            public
            void copy(final java.io.File p0) {
                x.copy(p0);
            }

            public
            IAcceptor<File> set(java.io.File p) {
                a.set(p);
                return this;
            }

            public
            Object apply(java.io.File p) {
                return f.apply(p);
            }

            public
            IUpdateable updateable(final java.io.File p0) {
                return new IUpdateable() {
                    public
                    void update() {
                        copy(p0);
                    }
                };
            }
        };


    }
}

