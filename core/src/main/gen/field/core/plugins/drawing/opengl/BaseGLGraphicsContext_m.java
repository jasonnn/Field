package field.core.plugins.drawing.opengl;

import field.bytecode.apt.Mirroring;
import field.launch.iUpdateable;
import field.namespace.generic.ReflectionTools;

import java.lang.reflect.Method;

public class BaseGLGraphicsContext_m {
public static final Method windowDisplayEnter_m = ReflectionTools.methodOf("windowDisplayEnter", field.core.plugins.drawing.opengl.BaseGLGraphicsContext.class);
public static final Mirroring.MirrorNoReturnNoArgsMethod<field.core.plugins.drawing.opengl.BaseGLGraphicsContext> windowDisplayEnter_s = new Mirroring.MirrorNoReturnNoArgsMethod<field.core.plugins.drawing.opengl.BaseGLGraphicsContext>(field.core.plugins.drawing.opengl.BaseGLGraphicsContext.class, "windowDisplayEnter");

public final iUpdateable windowDisplayEnter;
public static final Method windowDisplayExit_m = ReflectionTools.methodOf("windowDisplayExit", field.core.plugins.drawing.opengl.BaseGLGraphicsContext.class);
public static final Mirroring.MirrorNoReturnNoArgsMethod<field.core.plugins.drawing.opengl.BaseGLGraphicsContext> windowDisplayExit_s = new Mirroring.MirrorNoReturnNoArgsMethod<field.core.plugins.drawing.opengl.BaseGLGraphicsContext>(field.core.plugins.drawing.opengl.BaseGLGraphicsContext.class, "windowDisplayExit");

public final iUpdateable windowDisplayExit;
public BaseGLGraphicsContext_m(final BaseGLGraphicsContext x) {
windowDisplayEnter = windowDisplayEnter_s.updateable(x);
windowDisplayExit = windowDisplayExit_s.updateable(x);

}
}

