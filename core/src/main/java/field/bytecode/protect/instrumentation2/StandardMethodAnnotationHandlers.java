package field.bytecode.protect.instrumentation2;

import field.bytecode.protect.Cc;
import field.bytecode.protect.Woven;
import field.bytecode.protect.annotations.*;
import field.bytecode.protect.cache.DeferedCached;
import field.bytecode.protect.instrumentation.CallOnEntryAndExit_exceptionAware;
import field.protect.asm.ASMMethod;
import field.protect.asm.ASMType;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by jason on 7/21/14.
 */
public enum StandardMethodAnnotationHandlers implements AnnotatedMethodHandler2 {
    ALIASES(Aliases.class) {
        @NotNull
        @Override
        public MethodVisitor handleMethod(ASMAnnotatedMethodCtx ctx) {
            System.err.println("TODO: " + name());
            return NOOP(ctx);
        }
    },
    ALIASING(Aliasing.class) {
        @NotNull
        @Override
        public MethodVisitor handleMethod(ASMAnnotatedMethodCtx ctx) {
            System.err.println("TODO: " + name());
            return NOOP(ctx);
        }
    },
    CACHED(Cached.class) {
        @NotNull
        @Override
        public MethodVisitor handleMethod(ASMAnnotatedMethodCtx ctx) {
            return DeferedCached.fromCtx(ctx, "cancel");
        }
    },
    CACHED_PER_UPDATE(CachedPerUpdate.class) {
        @NotNull
        @Override
        public MethodVisitor handleMethod(ASMAnnotatedMethodCtx ctx) {
            System.err.println("TODO: " + name());
            return NOOP(ctx);
        }
    },
    CONSTANT_CONTEXT(ConstantContext.class) {
        @NotNull
        @Override
        public MethodVisitor handleMethod(ASMAnnotatedMethodCtx ctx) {
            String name = "dispatchOverTopology" +
                    "+" + ctx.name +
                    "+" + ctx.desc +
                    "+" + ctx.signature +
                    "+" + counter.getAndIncrement();
            return new CallOnEntryAndExit_exceptionAware(name, ctx.access, new ASMMethod(ctx.name, ctx.desc), ctx.delegate, ctx.params) {

                @Override
                public Object handleExit(Object returningThis,
                                         String fromName,
                                         Object fromThis,
                                         String methodName,
                                         Map<String, Object> parameterName,
                                         String methodReturnName) {
                    Cc.handle_exit(fromThis, name, parameterName);
                    return returningThis;
                }

                @Override
                public void handleEntry(String fromName,
                                        Object fromThis,
                                        String methodName,
                                        Map<String, Object> parameterName,
                                        Object[] argArray) {
                    Cc.handle_entry(fromThis, name, parameterName, aliasedParameterSet, argArray);
                }
            };
        }
    },
    CONTEXT_BEGIN(Context_begin.class) {
        @NotNull
        @Override
        public MethodVisitor handleMethod(ASMAnnotatedMethodCtx ctx) {
            System.err.println("TODO: " + name());
            return NOOP(ctx);
        }
    },
    CONTEXT_SET(Context_set.class) {
        @NotNull
        @Override
        public MethodVisitor handleMethod(ASMAnnotatedMethodCtx ctx) {
            System.err.println("TODO: " + name());
            return NOOP(ctx);
        }
    },
    DISK_CACHED(DiskCached.class) {
        @NotNull
        @Override
        public MethodVisitor handleMethod(ASMAnnotatedMethodCtx ctx) {
            System.err.println("TODO: " + name());
            return NOOP(ctx);
        }
    },
    DISPATCH_OVER_TOPOLOGY(DispatchOverTopology.class) {
        @NotNull
        @Override
        public MethodVisitor handleMethod(ASMAnnotatedMethodCtx ctx) {
            System.err.println("TODO: " + name());
            return NOOP(ctx);
        }
    },
    FAST_DISPATCH(FastDispatch.class) {
        @NotNull
        @Override
        public MethodVisitor handleMethod(ASMAnnotatedMethodCtx ctx) {
            System.err.println("TODO: " + name());
            return NOOP(ctx);
        }
    },
    IN_QUEUE(InQueue.class) {
        @NotNull
        @Override
        public MethodVisitor handleMethod(ASMAnnotatedMethodCtx ctx) {
            System.err.println("TODO: " + name());
            return NOOP(ctx);
        }
    },
    IN_QUEUE_THROUGH(InQueueThrough.class) {
        @NotNull
        @Override
        public MethodVisitor handleMethod(ASMAnnotatedMethodCtx ctx) {
            System.err.println("TODO: " + name());
            return NOOP(ctx);
        }
    },
    INSIDE(Inside.class) {
        @NotNull
        @Override
        public MethodVisitor handleMethod(ASMAnnotatedMethodCtx ctx) {
            System.err.println("TODO: " + name());
            return NOOP(ctx);
        }
    },
    MIRROR(Mirror.class) {
        @NotNull
        @Override
        public MethodVisitor handleMethod(ASMAnnotatedMethodCtx ctx) {
            System.err.println("TODO: " + name());
            return NOOP(ctx);
        }
    },
    NEW_THREAD(NewThread.class) {
        @NotNull
        @Override
        public MethodVisitor handleMethod(ASMAnnotatedMethodCtx ctx) {
            System.err.println("TODO: " + name());
            return NOOP(ctx);
        }
    },
    NEXT_UPDATE(NextUpdate.class) {
        @NotNull
        @Override
        public MethodVisitor handleMethod(ASMAnnotatedMethodCtx ctx) {
            System.err.println("TODO: " + name());
            return NOOP(ctx);
        }
    },
    NON_SWING(NonSwing.class) {
        @NotNull
        @Override
        public MethodVisitor handleMethod(ASMAnnotatedMethodCtx ctx) {
            System.err.println("TODO: " + name());
            return NOOP(ctx);
        }
    },
    SIMPLY_WRAPPED(SimplyWrapped.class) {
        @NotNull
        @Override
        public MethodVisitor handleMethod(ASMAnnotatedMethodCtx ctx) {
            System.err.println("TODO: " + name());
            return NOOP(ctx);
        }
    },
    TIMING_STATISTICS(TimingStatistics.class) {
        @NotNull
        @Override
        public MethodVisitor handleMethod(ASMAnnotatedMethodCtx ctx) {
            System.err.println("TODO: " + name());
            return NOOP(ctx);
        }
    },
    TRACED(Traced.class) {
        @NotNull
        @Override
        public MethodVisitor handleMethod(ASMAnnotatedMethodCtx ctx) {
            System.err.println("TODO: " + name());
            return NOOP(ctx);
        }
    },
    YIELD(Yield.class) {
        @NotNull
        @Override
        public MethodVisitor handleMethod(ASMAnnotatedMethodCtx ctx) {
            System.err.println("TODO: " + name());
            return NOOP(ctx);
        }
    },

    WOVEN(Woven.class) {
        @NotNull
        @Override
        public MethodVisitor handleMethod(ASMAnnotatedMethodCtx ctx) {
            return NOOP(ctx);
        }
    };
    private static AtomicInteger counter = new AtomicInteger(0);

    public final String desc;

    StandardMethodAnnotationHandlers(Class<? extends Annotation> annotationClass) {
        Target target = annotationClass.getAnnotation(Target.class);
        boolean hasMethodTarget = false;
        for (ElementType type : target.value()) {
            if (type == ElementType.METHOD) {
                hasMethodTarget = true;
                break;
            }
        }
        assert hasMethodTarget;
        desc = ASMType.getDescriptor(annotationClass);
    }

    public static Map<String, AnnotatedMethodHandler2> getHandlers() {
        Map<String, AnnotatedMethodHandler2> handlers = new HashMap<String, AnnotatedMethodHandler2>(values().length);
        for (StandardMethodAnnotationHandlers handler : values()) {
            handlers.put(handler.desc, handler);
        }
        return handlers;
    }

    private static MethodVisitor NOOP(ASMAnnotatedMethodCtx ctx) {
        return ctx.delegate;
    }
}
