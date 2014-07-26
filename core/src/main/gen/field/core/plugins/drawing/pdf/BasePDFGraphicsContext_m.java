package field.core.plugins.drawing.pdf;

import field.bytecode.apt.Mirroring;
import field.launch.iUpdateable;
import field.namespace.generic.ReflectionTools;

import java.lang.reflect.Method;

public
class BasePDFGraphicsContext_m {
    public static final Method windowDisplayEnter_m =
            ReflectionTools.methodOf("windowDisplayEnter", field.core.plugins.drawing.pdf.BasePDFGraphicsContext.class);
    public static final Mirroring.MirrorNoReturnNoArgsMethod<field.core.plugins.drawing.pdf.BasePDFGraphicsContext>
            windowDisplayEnter_s =
            new Mirroring.MirrorNoReturnNoArgsMethod<field.core.plugins.drawing.pdf.BasePDFGraphicsContext>(field.core.plugins.drawing.pdf.BasePDFGraphicsContext.class,
                                                                                                            "windowDisplayEnter");

    public final iUpdateable windowDisplayEnter;
    public static final Method windowDisplayExit_m =
            ReflectionTools.methodOf("windowDisplayExit", field.core.plugins.drawing.pdf.BasePDFGraphicsContext.class);
    public static final Mirroring.MirrorNoReturnNoArgsMethod<field.core.plugins.drawing.pdf.BasePDFGraphicsContext>
            windowDisplayExit_s =
            new Mirroring.MirrorNoReturnNoArgsMethod<field.core.plugins.drawing.pdf.BasePDFGraphicsContext>(field.core.plugins.drawing.pdf.BasePDFGraphicsContext.class,
                                                                                                            "windowDisplayExit");

    public final iUpdateable windowDisplayExit;

    public
    BasePDFGraphicsContext_m(final BasePDFGraphicsContext x) {
        windowDisplayEnter = windowDisplayEnter_s.updateable(x);
        windowDisplayExit = windowDisplayExit_s.updateable(x);

    }
}

