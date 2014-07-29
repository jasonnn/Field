package field.core.plugins.drawing.opengl;

import field.bytecode.mirror.impl.MirrorNoReturnNoArgsMethod;
import field.launch.IUpdateable;
import field.namespace.generic.ReflectionTools;

import java.lang.reflect.Method;

public
class BaseGLGraphicsContext_m {
    public static final Method windowDisplayEnter_m = ReflectionTools.methodOf("windowDisplayEnter",
                                                                               field.core.plugins.drawing.opengl.BaseGLGraphicsContext.class);
    public static final MirrorNoReturnNoArgsMethod<BaseGLGraphicsContext>
            windowDisplayEnter_s =
            new MirrorNoReturnNoArgsMethod<BaseGLGraphicsContext>(field.core.plugins.drawing.opengl.BaseGLGraphicsContext.class,
                                                                                                              "windowDisplayEnter");

    public final IUpdateable windowDisplayEnter;
    public static final Method windowDisplayExit_m = ReflectionTools.methodOf("windowDisplayExit",
                                                                              field.core.plugins.drawing.opengl.BaseGLGraphicsContext.class);
    public static final MirrorNoReturnNoArgsMethod<BaseGLGraphicsContext>
            windowDisplayExit_s =
            new MirrorNoReturnNoArgsMethod<BaseGLGraphicsContext>(field.core.plugins.drawing.opengl.BaseGLGraphicsContext.class,
                                                                                                              "windowDisplayExit");

    public final IUpdateable windowDisplayExit;

    public
    BaseGLGraphicsContext_m(final BaseGLGraphicsContext x) {
        windowDisplayEnter = windowDisplayEnter_s.updateable(x);
        windowDisplayExit = windowDisplayExit_s.updateable(x);

    }
}

