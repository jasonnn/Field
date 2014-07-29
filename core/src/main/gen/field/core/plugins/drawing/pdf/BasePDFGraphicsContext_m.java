package field.core.plugins.drawing.pdf;

import field.bytecode.mirror.impl.MirrorNoReturnNoArgsMethod;
import field.launch.IUpdateable;
import field.namespace.generic.ReflectionTools;

import java.lang.reflect.Method;

public
class BasePDFGraphicsContext_m {
    public static final Method windowDisplayEnter_m =
            ReflectionTools.methodOf("windowDisplayEnter", field.core.plugins.drawing.pdf.BasePDFGraphicsContext.class);
    public static final MirrorNoReturnNoArgsMethod<BasePDFGraphicsContext>
            windowDisplayEnter_s =
            new MirrorNoReturnNoArgsMethod<BasePDFGraphicsContext>(field.core.plugins.drawing.pdf.BasePDFGraphicsContext.class,
                                                                                                            "windowDisplayEnter");

    public final IUpdateable windowDisplayEnter;
    public static final Method windowDisplayExit_m =
            ReflectionTools.methodOf("windowDisplayExit", field.core.plugins.drawing.pdf.BasePDFGraphicsContext.class);
    public static final MirrorNoReturnNoArgsMethod<BasePDFGraphicsContext>
            windowDisplayExit_s =
            new MirrorNoReturnNoArgsMethod<BasePDFGraphicsContext>(field.core.plugins.drawing.pdf.BasePDFGraphicsContext.class,
                                                                                                            "windowDisplayExit");

    public final IUpdateable windowDisplayExit;

    public
    BasePDFGraphicsContext_m(final BasePDFGraphicsContext x) {
        windowDisplayEnter = windowDisplayEnter_s.updateable(x);
        windowDisplayExit = windowDisplayExit_s.updateable(x);

    }
}

