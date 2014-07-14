package field.core.plugins.drawing.opengl;

import field.core.plugins.drawing.SplineComputingOverride;
import field.core.util.FieldPyObjectAdaptor.iHandlesAttributes;

import java.util.HashMap;
import java.util.Map;

public class Layers implements iHandlesAttributes {

	private SplineComputingOverride o;

	Map<String, OnCanvasLines> known = new HashMap<String, OnCanvasLines>();

	public Layers(SplineComputingOverride o) {
		this.o = o;
	}

	@Override
	public Object getAttribute(String name) {
		OnCanvasLines n = known.get(name);
		if (n == null)
			known.put(name, n = new OnCanvasLines(o.forElement));

		return n.getLayer(name);
	}

	@Override
	public void setAttribute(String name, Object value) {
		throw new IllegalArgumentException();
	}

}
