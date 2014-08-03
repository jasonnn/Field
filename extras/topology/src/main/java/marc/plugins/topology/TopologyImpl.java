/**
 * 
 */
package marc.plugins.topology;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import field.math.graph.ITopology;
import marc.plugins.topology.TopologyOverElements.Node;
import field.core.dispatch.IVisualElement;

public class TopologyImpl implements ITopology<IVisualElement> {
	private final TopologyOverElements inside;
	private final IVisualElement e;

	TopologyImpl(TopologyOverElements topologyOverElements) {
		inside = topologyOverElements;
		e = null;
	}
	
	TopologyImpl(TopologyOverElements topologyOverElements, IVisualElement e) {
		inside = topologyOverElements;
		this.e = e;
	}
	
	
	public List<IVisualElement> up()
	{
		return getParentsOf(e);
	}

	public List<IVisualElement> down()
	{
		return getChildrenOf(e);
	}


	/**
	 * Returns the children of this element
	 */
	public List<IVisualElement> getChildrenOf(IVisualElement of) {
		Node n = inside.getNode(of, false);
		if (n == null)
			return Collections.EMPTY_LIST;
		List<IVisualElement> r = new ArrayList<IVisualElement>();
		for (Node nn : n.getChildren()) {
			r.add(nn.payload());
		}
		return r;
	}

	/**
	 * Returns the parents of this element
	 */
	public List<IVisualElement> getParentsOf(IVisualElement of) {
		Node n = inside.getNode(of, false);
		if (n == null)
			return Collections.EMPTY_LIST;
		List<IVisualElement> r = new ArrayList<IVisualElement>();
		for (Node nn : ((Collection<Node>) n.getParents())) {
			r.add(nn.payload());
		}
		return r;
	}
}