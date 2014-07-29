package field.math.abstraction;

import java.util.Set;

public
interface IMetric<t_from, t_to> {

    public
    float distance(t_from from, t_to to);

    public static
    class Maximum<T> implements IMetric<Set<T>, Set<T>> {
        private final IMetric<T, T> t;

        public
        Maximum(IMetric<T, T> t) {
            this.t = t;
        }

        public
        float distance(Set<T> from, Set<T> to) {
            float m = Float.NEGATIVE_INFINITY;
            for (T f : from) {
                for (T f2 : to) {
                    float d = t.distance(f, f2);
                    if (d > m) {
                        m = d;
                    }
                }
            }
            return m;
        }

    }

    public static
    class Minimum<T> implements IMetric<Set<T>, Set<T>> {
        private final IMetric<T, T> t;

        public
        Minimum(IMetric<T, T> t) {
            this.t = t;
        }

        public
        float distance(Set<T> from, Set<T> to) {
            float m = Float.POSITIVE_INFINITY;
            for (T f : from) {
                for (T f2 : to) {
                    float d = t.distance(f, f2);
                    if (d < m) {
                        m = d;
                    }
                }
            }
            return m;
        }

    }

    public static
    class Average<T> implements IMetric<Set<T>, Set<T>> {
        private final IMetric<T, T> t;

        public
        Average(IMetric<T, T> t) {
            this.t = t;
        }

        public
        float distance(Set<T> from, Set<T> to) {
            float m = 0;
            int n = 0;
            for (T f : from) {
                for (T f2 : to) {
                    float d = t.distance(f, f2);
                    m += d;
                    n += 1;
                }
            }
            return m / n;
        }

    }

}
