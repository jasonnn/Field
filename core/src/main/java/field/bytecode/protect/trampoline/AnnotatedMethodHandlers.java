package field.bytecode.protect.trampoline;

import field.bytecode.protect.*;
import field.bytecode.protect.annotations.*;
import field.bytecode.protect.cache.DeferedCached;
import field.bytecode.protect.cache.DeferedDiskCached;
import field.bytecode.protect.cache.DeferedFixedDuringUpdate;
import field.bytecode.protect.cache.DeferredTrace;
import field.bytecode.protect.dispatch.DispatchSupport;
import field.bytecode.protect.dispatch.InsideSupport;
import field.bytecode.protect.instrumentation.CallOnEntryAndExit_exceptionAware;
import field.bytecode.protect.instrumentation.Yield2;
import field.bytecode.protect.yield.YieldSupport;
import field.namespace.context.ContextTopology;
import field.namespace.context.iStorage;
import field.namespace.generic.Generics;
import field.protect.asm.ASMMethod;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by jason on 7/14/14.
 */
public enum AnnotatedMethodHandlers implements HandlesAnnontatedMethod {

    WOVEN(Woven.class) {
        @Override
        public MethodVisitor handleEnd(int access, String methodName, String methodDesc, String signature, ClassVisitor classDelegate, MethodVisitor delegate, HashMap<String, Object> paramters, byte[] originalByteCode, String className) {
            return delegate;
        }
    },
    DISPATCH(DispatchOverTopology.class) {
        @Override
        public MethodVisitor handleEnd(int access, String methodName, String methodDesc, String signature, ClassVisitor classDelegate, MethodVisitor delegate, HashMap<String, Object> paramters, byte[] originalByteCode, final String className) {
            return new CallOnEntryAndExit_exceptionAware("dispatchOverTopology+" + methodName + "+" + methodDesc + "+" + signature + "+" + counter.getAndIncrement(), access, new ASMMethod(methodName, methodDesc), delegate, paramters) {

                DispatchSupport support = new DispatchSupport();

                @Override
                public Object handle(Object returningThis, String fromName, Object fromThis, String methodName, Map<String, Object> parameterName, String methodReturnName) {
                    try {
                        return support.exit(this.name, fromThis, returningThis, parameterName, className);
                    } catch (Throwable t) {
                        t.printStackTrace();
                        throw new IllegalArgumentException(t);
                    }
                }

                @Override
                public void handle(String fromName, Object fromThis, String methodName, Map<String, Object> parameterName, Object[] argArray) {
                    try {
                        support.enter(this.name, fromName, fromThis, methodName, parameterName, argArray, className);
                    } catch (Throwable t) {
                        t.printStackTrace();
                        throw new IllegalArgumentException(t);
                    }
                }
            };
        }

    },
    INSIDE(Inside.class) {
        @Override
        public MethodVisitor handleEnd(int access, String methodName, String methodDesc, String signature, ClassVisitor classDelegate, MethodVisitor delegate, HashMap<String, Object> paramters, byte[] originalByteCode, String className) {
            return new CallOnEntryAndExit_exceptionAware("inside+" + methodName + "+" + methodDesc + "+" + signature + "+" + counter.getAndIncrement(), access, new ASMMethod(methodName, methodDesc), delegate, paramters) {

                @Override
                public Object handle(Object returningThis, String fromName, Object fromThis, String methodName, Map<String, Object> parameterName, String methodReturnName) {
                    try {
                        InsideSupport.exit(fromThis, (String) parameterName.get("group"));
                        return returningThis;
                    } catch (Throwable t) {
                        t.printStackTrace();
                        throw new IllegalArgumentException(t);
                    }
                }

                @Override
                public void handle(String fromName, Object fromThis, String methodName, Map<String, Object> parameterName, Object[] argArray) {
                    try {
                        InsideSupport.enter(fromThis, (String) parameterName.get("group"));
                    } catch (Throwable t) {
                        t.printStackTrace();
                        throw new IllegalArgumentException(t);
                    }
                }
            };
        }
    },
    FAST_DISPATCH(FastDispatch.class) {
        @Override
        public MethodVisitor handleEnd(int access, String methodName, String methodDesc, String signature, ClassVisitor classDelegate, MethodVisitor delegate, HashMap<String, Object> paramters, byte[] originalByteCode, String className) {
            return new FastEntry("testmethodannotation2", access, new ASMMethod(methodName, methodDesc), delegate, paramters, className);
        }
    },
    YIELD(Yield.class) {
        @Override
        public MethodVisitor handleEnd(int access, String methodName, String methodDesc, String signature, ClassVisitor classDelegate, MethodVisitor delegate, HashMap<String, Object> paramters, byte[] originalByteCode, String className) {
            return new Yield2("yield+" + methodName + "+" + methodDesc + "+" + counter.getAndIncrement(), access, new ASMMethod(methodName, methodDesc), delegate, paramters, originalByteCode, className) {
                final YieldSupport support = new YieldSupport();

                @Override
                public int yieldIndexFor(String fromName, Object fromThis, String methodName) {

                    return support.yieldIndexFor(this.name, fromThis, parameters);
                }

                @Override
                public Object[] yieldLoad(String fromName, Object fromThis, String methodName) {
                    return support.yieldLoad(fromThis);
                }

                @Override
                public Object yieldStore(Object wasReturn, Object[] localStorage, String fromName, Object fromThis, String methodName, int resumeLabel) {
                    return support.yieldStore(wasReturn, localStorage, this.name, fromThis, resumeLabel);
                }
            };
        }
    },
    CACHED(Cached.class) {
        @Override
        public MethodVisitor handleEnd(int access, String methodName, String methodDesc, String signature, ClassVisitor classDelegate, MethodVisitor delegate, HashMap<String, Object> paramters, byte[] originalByteCode, String className) {
            return new DeferedCached("cancel", access, new ASMMethod(methodName, methodDesc), classDelegate, delegate, signature, paramters);
        }
    },
    TRACED(Traced.class) {
        @Override
        public MethodVisitor handleEnd(int access, String methodName, String methodDesc, String signature, ClassVisitor classDelegate, MethodVisitor delegate, HashMap<String, Object> paramters, byte[] originalByteCode, String className) {
            return new DeferredTrace("cancel", access, new ASMMethod(methodName, methodDesc), classDelegate, delegate, signature, paramters);
        }
    },
    CACHED_PER_UPDATE(CachedPerUpdate.class) {
        @Override
        public MethodVisitor handleEnd(int access, String methodName, String methodDesc, String signature, ClassVisitor classDelegate, MethodVisitor delegate, HashMap<String, Object> paramters, byte[] originalByteCode, String className) {
            return new DeferedFixedDuringUpdate("cancelfix", access, new ASMMethod(methodName, methodDesc), classDelegate, delegate, signature, paramters);
        }
    },
    DISK_CACHED(DiskCached.class) {
        @Override
        public MethodVisitor handleEnd(int access, String methodName, String methodDesc, String signature, ClassVisitor classDelegate, MethodVisitor delegate, HashMap<String, Object> paramters, byte[] originalByteCode, String className) {
            return new DeferedDiskCached("cancel", access, new ASMMethod(methodName, methodDesc), classDelegate, delegate, signature, paramters);
        }
    },
    NEW_THREAD(NewThread.class) {
        @Override
        public MethodVisitor handleEnd(int access, String methodName, String methodDesc, String signature, ClassVisitor classDelegate, MethodVisitor delegate, HashMap<String, Object> paramters, byte[] originalByteCode, String className) {
            return new DeferedNewThread("nt", access, new ASMMethod(methodName, methodDesc), classDelegate, delegate, signature, paramters);
        }
    },
    IN_QUEUE(InQueue.class) {
        @Override
        public MethodVisitor handleEnd(int access, String methodName, String methodDesc, String signature, ClassVisitor classDelegate, MethodVisitor delegate, HashMap<String, Object> paramters, byte[] originalByteCode, String className) {
            return new DeferedInQueue("nt", access, new ASMMethod(methodName, methodDesc), classDelegate, delegate, signature, paramters);
        }
    },
    IN_QUEUE_THROUGH(InQueueThrough.class) {
        @Override
        public MethodVisitor handleEnd(int access, String methodName, String methodDesc, String signature, ClassVisitor classDelegate, MethodVisitor delegate, HashMap<String, Object> paramters, byte[] originalByteCode, String className) {
            return new DeferedInQueue("nt", access, new ASMMethod(methodName, methodDesc), classDelegate, delegate, signature, paramters, true);
        }
    },
    SIMPLY_WRAPPED(SimplyWrapped.class) {
        @Override
        public MethodVisitor handleEnd(int access, String methodName, String methodDesc, String signature, ClassVisitor classDelegate, MethodVisitor delegate, HashMap<String, Object> paramters, byte[] originalByteCode, String className) {
            return new SimplyWrappedInQueue("nt", access, new ASMMethod(methodName, methodDesc), classDelegate, delegate, signature, paramters);
        }
    },
    NEXT_UPDATE(NextUpdate.class) {
        @Override
        public MethodVisitor handleEnd(int access, String methodName, String methodDesc, String signature, ClassVisitor classDelegate, MethodVisitor delegate, HashMap<String, Object> paramters, byte[] originalByteCode, String className) {
            return new DeferedNextUpdate("nu", access, new ASMMethod(methodName, methodDesc), classDelegate, delegate, signature, paramters);
        }
    },
    NON_SWING(NonSwing.class) {
        @Override
        public MethodVisitor handleEnd(int access, String methodName, String methodDesc, String signature, ClassVisitor classDelegate, MethodVisitor delegate, HashMap<String, Object> paramters, byte[] originalByteCode, String className) {
            return new DeferedNonSwing("nu", access, new ASMMethod(methodName, methodDesc), classDelegate, delegate, signature, paramters);
        }
    },
    CONSTANT_CONTEXT(ConstantContext.class) {
        @Override
        public MethodVisitor handleEnd(int access, String methodName, String methodDesc, String signature, ClassVisitor classDelegate, MethodVisitor delegate, HashMap<String, Object> paramters, byte[] originalByteCode, String className) {
            return new CallOnEntryAndExit_exceptionAware("dispatchOverTopology+" + methodName + "+" + methodDesc + "+" + signature + "+" + counter.getAndIncrement(), access, new ASMMethod(methodName, methodDesc), delegate, paramters) {

                @Override
                public Object handle(Object returningThis, String fromName, Object fromThis, String methodName, Map<String, Object> parameterName, String methodReturnName) {
                    Cc.handle_exit(fromThis, name, parameterName);
                    return returningThis;
                }

                @Override
                public void handle(String fromName, Object fromThis, String methodName, Map<String, Object> parameterName, Object[] argArray) {
                    Cc.handle_entry(fromThis, name, parameterName, aliasedParameterSet, argArray);
                }
            };
        }
    },
    CONTEXT_BEGIN(Context_begin.class) {
        @Override
        public MethodVisitor handleEnd(int access, String methodName, String methodDesc, String signature, ClassVisitor classDelegate, MethodVisitor delegate, HashMap<String, Object> paramters, byte[] originalByteCode, String className) {
            return new CallOnEntryAndExit_exceptionAware("dispatchOverTopology+" + methodName + "+" + methodDesc + "+" + signature + "+" + counter.getAndIncrement(), access, new ASMMethod(methodName, methodDesc), delegate, paramters) {
                Stack<Generics.Pair<ContextTopology, Object>> stack = new Stack<Generics.Pair<ContextTopology, Object>>();

                @Override
                public Object handle(Object returningThis, String fromName, Object fromThis, String methodName, Map<String, Object> parameterName, String methodReturnName) {
                    if (stack.size() > 0) {
                        Generics.Pair<ContextTopology, Object> q = stack.pop();
                        ContextAnnotationTools.end(q.left, q.right);
                    }
                    return returningThis;
                }

                @Override
                public void handle(String fromName, Object fromThis, String methodName, Map<String, Object> parameterName, Object[] argArray) {
                    ContextTopology<?, ?> context;
                    try {
                        context = ContextAnnotationTools.contextFor(fromThis, parameterName, this.aliasedParameterSet, argArray);
                    } catch (Exception e) {
                        Error er = new Error(" exception thrown in finding context for Context_begin");
                        er.initCause(e);
                        throw er;
                    }
                    Object value = ContextAnnotationTools.valueFor(fromThis, parameterName, this.aliasedParameterSet, argArray);
                    if (value == null || (value instanceof String && value.equals("")))
                        value = fromThis;

                    ContextAnnotationTools.begin(context, value);
                    stack.push(new Generics.Pair<ContextTopology, Object>(context, value));

                    ContextAnnotationTools.populateContexted(context, fromThis);
                }

            };
        }
    },
    CONTEXT_SET(Context_set.class) {
        @Override
        public MethodVisitor handleEnd(int access, String methodName, String methodDesc, String signature, ClassVisitor classDelegate, MethodVisitor delegate, HashMap<String, Object> paramters, byte[] originalByteCode, String className) {
            return new CallOnEntryAndExit_exceptionAware("dispatchOverTopology+" + methodName + "+" + methodDesc + "+" + signature + "+" + counter.getAndIncrement(), access, new ASMMethod(methodName, methodDesc), delegate, paramters) {

                @Override
                public Object handle(Object returningThis, String fromName, Object fromThis, String methodName, Map<String, Object> parameterName, String methodReturnName) {
                    return returningThis;
                }

                @Override
                public void handle(String fromName, Object fromThis, String methodName, Map<String, Object> parameterName, Object[] argArray) {
                    ContextTopology context;
                    try {
                        context = ContextAnnotationTools.contextFor(fromThis, parameterName, this.aliasedParameterSet, argArray);
                    } catch (Exception e) {
                        Error er = new Error(" exception thrown in finding context for Context_begin");
                        er.initCause(e);
                        Logger.getLogger(AnnotatedMethodHandlers.class.getName() + '.' + CONTEXT_SET.name())
                                .log(Level.WARNING, " exception thrown <" + er + ">");
                        throw er;
                    }
                    Object value = ContextAnnotationTools.valueFor(fromThis, parameterName, this.aliasedParameterSet, argArray);
                    if (value == null || (value instanceof String && value.equals("")))
                        value = fromThis;

                    String name = (String) parameterName.get("name");
                    iStorage storage = (iStorage) context.storage.get(context.getAt(), null);
                    storage.set(name, new BaseRef<Object>(value));


                    ContextAnnotationTools.populateContexted(context, fromThis);
                }

            };

        }
    },
    TIMING_STATS(TimingStatistics.class) {
        @Override
        public MethodVisitor handleEnd(int access, String methodName, String methodDesc, String signature, ClassVisitor classDelegate, MethodVisitor delegate, HashMap<String, Object> paramters, byte[] originalByteCode, String className) {
            return new CallOnEntryAndExit_exceptionAware("dispatchOverTopology+" + methodName + "+" + methodDesc + "+" + signature + "+" + counter.getAndIncrement(), access, new ASMMethod(methodName, methodDesc), delegate, paramters) {

                TimingSupport support = new TimingSupport();

                @Override
                public Object handle(Object returningThis, String fromName, Object fromThis, String methodName, Map<String, Object> parameterName, String methodReturnName) {
                    support.handle_exit(fromThis, name, parameterName);
                    return returningThis;
                }

                @Override
                public void handle(String fromName, Object fromThis, String methodName, Map<String, Object> parameterName, Object[] argArray) {
                    support.handle_entry(fromThis, name, parameterName);
                }
            };
        }
    };
    private static final AtomicInteger counter=new AtomicInteger(0);

    final String internalName;

    AnnotatedMethodHandlers(Class annotation) {
        this(Type.getDescriptor(annotation));
    }

    AnnotatedMethodHandlers(String internalName) {
        this.internalName = internalName;
    }

    public static Map<String, HandlesAnnontatedMethod> getHandlers() {
        HashMap<String, HandlesAnnontatedMethod> ret = new HashMap<String, HandlesAnnontatedMethod>(values().length);
        for (AnnotatedMethodHandlers handler : values()) {
            ret.put(handler.internalName, handler);
        }
        return ret;
    }
}
