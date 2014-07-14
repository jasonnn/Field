package field.math.graph;

import field.math.graph.iTopology.iMutableTopology;

import java.util.List;



public interface iSynchronizedTopology<T> extends iMutableTopology<T> {
	public void added(T t);

	public void removed(T t);

	public void update(T t);
	
	public List<T> getAll();
		
}