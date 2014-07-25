package asm.util;

import asm.handlers.ctx.ASMMethodCtx;

import java.lang.annotation.Annotation;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by jason on 7/22/14.
 */
public
class Namer {
    private static final AtomicInteger counter = new AtomicInteger(0);
    private static final AtomicInteger paramCounter = new AtomicInteger(0);

    public static
    String createName(Class<? extends Annotation> cls, ASMMethodCtx ctx) {
        return createName(cls.getSimpleName(), ctx);
    }

    public static
    String uniqueParamID() {
        return "parameter:" + paramCounter.incrementAndGet();
    }


    @SuppressWarnings("StringBufferReplaceableByString")
    public static
    String createName(String prefix, ASMMethodCtx ctx) {
        return new StringBuilder().append(prefix)
                                  .append('+')
                                  .append(ctx.name)
                                  .append('+')
                                  .append(ctx.desc)
                                  .append('+')
                                  .append(ctx.signature)
                                  .append('+')
                                  .append(counter.incrementAndGet())
                                  .toString();

    }
}
