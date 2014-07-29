package field.math.abstraction;

/**
 * Interface for a class that provides a double
 */
public
interface IDoubleProvider {
    public
    double evaluate();

    public static
    class Constant implements IDoubleProvider {

        private double constant;

        public
        Constant(double f) {
            constant = f;
        }

        public
        Constant setConstant(double c) {
            constant = c;
            return this;
        }

        public
        double evaluate() {
            return constant;
        }
    }

    public static
    class Monotonoic implements IDoubleProvider {

        private final IDoubleProvider in;

        double last = Double.NEGATIVE_INFINITY;

        public
        Monotonoic(IDoubleProvider in) {
            this.in = in;
        }

        public
        double evaluate() {
            double is = in.evaluate();
            return last = (is > last) ? is : last;
        }
    }

    public static
    class FromFilter implements IDoubleProvider {
        private final IFilter<Double, Double> filter;

        private final IDoubleProvider input;

        public
        FromFilter(IFilter<Double, Double> filter, IDoubleProvider input) {
            this.filter = filter;
            this.input = input;
        }

        public
        double evaluate() {
            return filter.filter(input.evaluate());
        }
    }

    public static
    class Offset implements IDoubleProvider {
        private final IDoubleProvider from;

        private final float amount;

        public
        Offset(IDoubleProvider from, float amount) {
            this.from = from;
            this.amount = amount;
        }

        public
        double evaluate() {
            return from.evaluate() - amount;
        }
    }

}
