package field.math.abstraction;

/**
 * Interface for a class that provides a double
 */
public interface iDoubleProvider {
	public double evaluate();

	public static
    class Constant implements iDoubleProvider {

		private double constant;

		public Constant(double f) {
			constant = f;
		}

		public Constant setConstant(double c) {
			constant = c;
			return this;
		}

		public double evaluate() {
			return constant;
		}
	}

	public static
    class Monotonoic implements iDoubleProvider {

		private final iDoubleProvider in;

		double last = Double.NEGATIVE_INFINITY;

		public Monotonoic(iDoubleProvider in) {
			this.in = in;
		}

		public double evaluate() {
			double is = in.evaluate();
			return last = (is > last) ? is : last;
		}
	}

	public static
    class FromFilter implements iDoubleProvider {
		private final iFilter<Double, Double> filter;

		private final iDoubleProvider input;

		public FromFilter(iFilter<Double, Double> filter, iDoubleProvider input) {
			this.filter = filter;
			this.input = input;
		}

		public double evaluate() {
			return filter.filter(input.evaluate());
		}
	}

	public static
    class Offset implements iDoubleProvider {
		private final iDoubleProvider from;

		private final float amount;

		public Offset(iDoubleProvider from, float amount) {
			this.from = from;
			this.amount = amount;
		}

		public double evaluate() {
			return from.evaluate() - amount;
		}
	}

}
