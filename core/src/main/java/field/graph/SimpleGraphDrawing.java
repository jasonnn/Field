package field.graph;

import field.context.Context.Cobj;
import field.graph.MotionGraphs.iAxis;
import field.graph.MotionGraphs.iLayout;
import field.math.linalg.Vector2;
import field.namespace.generic.Bind.iFunction;
import field.util.Dict.Prop;
import org.python.core.PyObject;

import java.util.Collection;

public class SimpleGraphDrawing {

	// static public final Prop<iFunction

	public static final Prop<iFunction<Number, Cobj>> extract = new Prop<iFunction<Number, Cobj>>("extract");
	public static final Prop<iFunction<Number, Double>> filter = new Prop<iFunction<Number, Double>>("filter");
	public static final Prop<Boolean> clampToZero = new Prop<Boolean>("clampToZero");

	public static final Prop<Number> max = new Prop<Number>("max");
	public static final Prop<Number> min = new Prop<Number>("min");

	public static final Prop<Number> length = new Prop<Number>("length");
	public static final Prop<Number> offset = new Prop<Number>("offset");

	public static final Prop<Vector2> dimensions = new Prop<Vector2>("dimensions");

	public static final Prop<iAxis> axis = new Prop<iAxis>("axis");

	public static
    class Datum extends Cobj {
		public Datum() {
			init();
		}

		public Datum(PyObject[] a, String[] kw) {
			super(a, kw);
			init();
		}

		protected void init() {
			setAttribute("datum", this);
		}

	}

	public static
    class SimpleAxis extends Cobj implements iAxis, iLayout {

		protected Prop<Number> dimension;
		private final iFunction<Number, Cobj> defaultExtract;
		private final iFunction<Number, Double> defaultFilter;

		public SimpleAxis(String dimension) {
			this.dimension = new Prop<Number>(dimension);

			setProperty(axis, this);
			
			setProperty(length, 100);
			setProperty(offset, 0);
			setProperty(max, 0);
			setProperty(min, 0);
			setProperty(clampToZero, false);

			defaultExtract = new iFunction<Number, Cobj>() {
				@Override
				public Number f(Cobj in) {
					return in.getProperty(SimpleAxis.this.dimension);
				}
			};

			defaultFilter = new iFunction<Number, Double>() {
				@Override
				public Number f(Double in) {
					return in;
				}
			};
		}

		@Override
		public void layout() {
			Collection<Cobj> property = this.getProperty(MotionGraphs.dataSet);
			float min = Float.POSITIVE_INFINITY;
			float max = Float.NEGATIVE_INFINITY;

			iFunction<Number, Cobj> e = this.getProperty(extract);
			if (e == null)
				e = defaultExtract;

			for (Cobj c : property) {
                Number v = e.f(c);
                if (v.floatValue() < min)
					min = v.floatValue();
				if (v.floatValue() > max)
					max = v.floatValue();
			}

			if (this.isTrue(clampToZero, false)) {
				if (min > 0)
					min = 0;
			}
			if (min == max)
				max = min + 1;

			setProperty(SimpleGraphDrawing.max, max);
			setProperty(SimpleGraphDrawing.min, min);
		}

		protected boolean isTrue(Prop x, boolean b) {
			Object o = getProperty(x);
			if (o == null)
				return b;
			if (o instanceof Boolean)
				return ((Boolean) o);
			if (o instanceof Number)
				return ((Number) o).floatValue() != 0;
			throw new ClassCastException("cannot convert " + (o.getClass()) + " to boolean");
		}

		protected float getFloat(Prop<? extends Number> x, float b) {
			Object o = getProperty(x);
			if (o == null)
				return b;
			if (o instanceof Boolean)
				return (Boolean) o ? 1f : 0f;
			if (o instanceof Number)
				return ((Number) o).floatValue();
			throw new ClassCastException("cannot convert " + (o.getClass()) + " to number");
		}

		@Override
		public void begin() {
		}

		@Override
		public void end() {
		}

		@Override
		public float map(Cobj x) {

			iFunction<Number, Cobj> e = this.getProperty(extract);
			if (e == null)
				e = defaultExtract;

			Number n = e.f(x);
			return mapNumber(n.floatValue());

		}

		@Override
		public float mapNumber(float n) {
			iFunction<Number, Double> e = this.getProperty(filter);
			if (e == null)
				e = defaultFilter;

			float min = getFloat(SimpleGraphDrawing.min, 0);
			float max = getFloat(SimpleGraphDrawing.max, 1);

			float length = getFloat(SimpleGraphDrawing.length, 100);
			float offset = getFloat(SimpleGraphDrawing.offset, 0);

			double p = (n - min) / (max - min);

			float p2 = e.f(p).floatValue();

			return offset + length * p2;
		}

	}

}
