package marc.plugins.topology;

import java.util.LinkedHashMap;
import java.util.List;

import field.core.StandardFluidSheet;
import field.core.dispatch.IVisualElement;
import field.core.dispatch.Rect;
import field.core.dispatch.VisualElement;
import field.core.persistance.VisualElementReference;
import field.core.plugins.connection.LineDrawingOverride;
import field.math.graph.IMutableContainer;
import field.math.graph.ITopology;
import field.math.graph.NodeImpl;
import field.math.graph.visitors.TopologySearching;
import field.namespace.generic.IFunction;


public class TopologyOverElements {

	private final IVisualElement root;

	private final IFunction<IVisualElement,Boolean> f;

	public TopologyOverElements(IVisualElement root, IFunction<IVisualElement,Boolean> f) {
		this.root = root;
		this.f = f;

		reconstruct();
	}

	public class Node extends NodeImpl<Node> implements IMutableContainer<IVisualElement, Node> {

		IVisualElement p;

		public Node setPayload(IVisualElement t) {
			p = t;
			return this;
		}

		public IVisualElement payload() {
			return p;
		}

		@Override
		public String toString() {
			return "" + p;
		}
	}

	LinkedHashMap<IVisualElement, Node> all = new LinkedHashMap<IVisualElement, Node>();

	protected void reconstruct() {
		all.clear();

		List<IVisualElement> e = StandardFluidSheet.allVisualElements(root);
		for (IVisualElement ee : e) {
			VisualElementReference a = ee.getProperty(LineDrawingOverride.lineDrawing_to);
			VisualElementReference b = ee.getProperty(LineDrawingOverride.lineDrawing_from);
			if (a != null && b != null) {
				IVisualElement ae = a.get(root);
				IVisualElement be = b.get(root);

				if (ae != null && be != null) {
					Boolean m = f == null ? true : interpret(f.apply(ee));
					if (m) {
						Node nae = getNode(ae, true);
						Node nbe = getNode(be, true);
						nae.addChild(nbe);
					}
				}
			}
		}
	}

	private boolean interpret(Object f) {
		if (f == null)
			return false;
		if (f instanceof Boolean)
			return ((Boolean) f);
		if (f instanceof Number)
			return ((Number) f).intValue() > 0;
		return true;
	}

	public LinkedHashMap<IVisualElement, Node> getAll() {
		return all;
	}

	public
    ITopology<IVisualElement> getTopology() {
		return new TopologyImpl(this);
	}
	
	public ITopology<IVisualElement> getTopology(IVisualElement d) {
		return new TopologyImpl(this, d);
	}

	Node getNode(IVisualElement ae, boolean create) {
		Node n = all.get(ae);
		if (n == null && create) {
			all.put(ae, n = new Node().setPayload(ae));
		}
		return n;
	}

	public List<IVisualElement> findPath(IVisualElement from, IVisualElement to) {
		return new TopologySearching.TopologyAStarSearch<IVisualElement>(getTopology(), new TopologySearching.AStarMetric<IVisualElement>() {

			Rect r1 = new Rect(0, 0, 0, 0);
			Rect r2 = new Rect(0, 0, 0, 0);

			public double distance(IVisualElement from, IVisualElement to) {
				from.getFrame(r1);
				to.getFrame(r2);
				return r1.midpoint2().distanceFrom(r2.midpoint2());
			}
		}).search(from, to);
	}

}
