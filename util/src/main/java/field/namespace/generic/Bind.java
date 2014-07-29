package field.namespace.generic;

import field.util.collect.tuple.Pair;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

/**
 * monads, marc style
 *
 * @author marc
 */
public
class Bind {

    // will have to wait until we can refactor this into filter

    public static
    <A, B, C> IFunction<C, A> bind(final IFunction<B, A> two, final IFunction<C, B> one) {
        return new IFunction<C, A>() {
            public
            A apply(C in) {
                B b = one.apply(in);
                A a = two.apply(b);
                return a;
            }
        };
    }

    public static
    <A, B> IOutput<A> bind(final IFunction<B, A> on, final IOutput<B> in) {
        return new IOutput<A>() {
            public
            A get() {
                return on.apply(in.get());
            }
        };
    }

    public static
    <A, B, C> IFunction<C, A> bind(final IOutput<IFunction<B, A>> two, final IOutput<IFunction<C, B>> one) {
        return new IFunction<C, A>() {
            public
            A apply(C in) {
                return two.get().apply(one.get().apply(in));
            }
        };
    }

    public static
    <A, B, C> IFunction<C, A> bind(final IOutput<IFunction<B, A>> two, final IFunction<C, B> one) {
        return new IFunction<C, A>() {
            public
            A apply(C in) {
                return two.get().apply(one.apply(in));
            }
        };
    }

    public static
    <A> IOutput<A> offset(final A a) {
        return new IOutput<A>() {
            public
            A get() {
                return a;
            }
        };
    }

    public static
    <A, B> IFunction<B, A> call(Class<? extends A> out, final Method m, final Object on, Class<? extends B> ini) {
        return new IFunction<B, A>() {
            @SuppressWarnings("unchecked")
            public
            A apply(B in) {
                try {
                    return (A) m.invoke(on, in);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    throw (IllegalArgumentException) (new IllegalArgumentException().initCause(e));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    throw (IllegalArgumentException) (new IllegalArgumentException().initCause(e));
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                    throw (IllegalArgumentException) (new IllegalArgumentException().initCause(e));
                }
            }
        };
    }

    public static
    <A, B, C> IFunction<Pair<B, C>, A> call(Class<? extends A> out,
                                            final Method m,
                                            Class<? extends B> onClass,
                                            Class<? extends C> paramClass) {
        return new IFunction<Pair<B, C>, A>() {
            @SuppressWarnings("unchecked")
            public
            A apply(Pair<B, C> in) {
                try {
                    return (A) m.invoke(in.left, in.right);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    throw (IllegalArgumentException) (new IllegalArgumentException().initCause(e));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    throw (IllegalArgumentException) (new IllegalArgumentException().initCause(e));
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                    throw (IllegalArgumentException) (new IllegalArgumentException().initCause(e));
                }
            }
        };
    }

    public static
    <B, C> IFunction<B, Pair<B, C>> wrap(final IFunction<B, C> w) {
        return new IFunction<B, Pair<B, C>>() {
            public
            Pair<B, C> apply(B in) {
                C c = w.apply(in);
                return new Pair<B, C>(in, c);
            }
        };
    }

    public static
    <A, B> IFunction<B, A> call(Class<? extends A> out, final Method m, Class<? extends B> ini) {
        return new IFunction<B, A>() {
            @SuppressWarnings("unchecked")
            public
            A apply(B in) {
                try {
                    return (A) m.invoke(in);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    throw (IllegalArgumentException) (new IllegalArgumentException().initCause(e));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    throw (IllegalArgumentException) (new IllegalArgumentException().initCause(e));
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                    throw (IllegalArgumentException) (new IllegalArgumentException().initCause(e));
                }
            }
        };
    }

    public static
    <A, B> IFunction<B, A> multipleApply(final IFunction<B, A> function, final int num) {
        return new IFunction<B, A>() {
            public
            A apply(B in) {
                A a = null;
                for (int i = 0; i < num; i++) {
                    A l = function.apply(in);
                    if (l != null) {
                        a = l;
                    }
                }
                return a;
            }
        };
    }

    public static
    <A, B> IFunction<B, A> callAll(final List<IFunction<B, A>> of) {
        return new IFunction<B, A>() {
            public
            A apply(B in) {
                A r = null;
                for (IFunction<B, A> f : of) {
                    r = f.apply(in);
                }
                return r;
            }
        };
    }

    public static
    <A, B> IFunction<B, A> collapseAll(final List<IFunction<B, A>> of, final IFunction<Pair<A, A>, A> over) {
        return new IFunction<B, A>() {
            public
            A apply(B in) {
                A r = null;
                for (IFunction<B, A> f : of) {
                    A r2 = f.apply(in);
                    r = over.apply(new Pair<A, A>(r, r2));
                }
                return r;
            }
        };
    }

    // odd monads

    public static
    <T> IOutput<T> randomOf(final List<T> of) {
        return new IOutput<T>() {
            public
            T get() {
                int index = (int) (Math.random() * of.size());
                return of.get(index);
            }
        };
    }

    // util

    public static
    <T> T argMax(Collection<T> t, IFunction<T, ? extends Number> f) {
        double v = Double.NEGATIVE_INFINITY;
        T best = null;

        for (T tt : t) {
            Number m = f.apply(tt);
            if (m.doubleValue() > v) {
                v = m.doubleValue();
                best = tt;
            }
        }

        return best;
    }

    public static
    <T> T argMin(Collection<T> t, IFunction<T, ? extends Number> f) {
        double v = Double.POSITIVE_INFINITY;
        T best = null;

        for (T tt : t) {
            Number m = f.apply(tt);
            if (m.doubleValue() < v) {
                v = m.doubleValue();
                best = tt;
            }
        }

        return best;
    }

    public static
    <T> Number max(Collection<T> t, IFunction<T, ? extends Number> f) {
        double v = Double.NEGATIVE_INFINITY;
        Number best = null;

        for (T tt : t) {
            Number m = f.apply(tt);
            if (m.doubleValue() > v) {
                v = m.doubleValue();
                best = m;
            }
        }

        return best;
    }

    public static
    <T> Number min(Collection<T> t, IFunction<T, ? extends Number> f) {
        double v = Double.POSITIVE_INFINITY;
        Number best = null;

        for (T tt : t) {
            Number m = f.apply(tt);
            if (m.doubleValue() < v) {
                v = m.doubleValue();
                best = m;
            }
        }

        return best;
    }

    public static
    <T> int indexMin(Collection<T> t, IFunction<T, ? extends Number> f) {

        double v = Double.POSITIVE_INFINITY;
        int best = 0;

        int i = 0;
        for (T tt : t) {

            Number m = f.apply(tt);
            if (m.doubleValue() < v) {
                v = m.doubleValue();
                best = i;
            }
            i++;
        }

        return best;
    }

}
