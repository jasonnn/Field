package field.graphics.dynamic;

import field.core.plugins.drawing.opengl.HashedCopy;
import field.graphics.core.Base.iGeometry;
import field.graphics.core.BasicGeometry;
import field.graphics.core.BasicGeometry.TriangleMesh_long;
import field.graphics.core.ResourceMonitor;
import field.math.linalg.Vector3;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;


public class SubMesh_long implements iDynamicMesh {

	BasicGeometry.TriangleMesh_long dummyMesh = new TriangleMesh_long();
	public DynamicMesh_long delegate;

	HashedCopy copy;
	
	public SubMesh_long() {
		dummyMesh.setNative(false);
		dummyMesh.rebuildTriangle(0);
		dummyMesh.rebuildVertex(0);
		delegate = new DynamicMesh_long(dummyMesh);
	}

	public HashedCopy getCopy()
	{
		return copy;
	}
	
	public void makeCopy()
	{
		copy = new HashedCopy();
	}
	
	protected SubMesh_long(boolean b) {
	}


	public void close() {
		delegate.close();
	}

	public void copyPositionToAux(int aux)
	{

		FloatBuffer vv = dummyMesh.vertex(false);

		dummyMesh.aux(aux, 3).put(vv);
		delegate.cachedAuxBuffer[aux] = dummyMesh.aux(aux,3);
		delegate.cachedAuxBufferWidth[aux] = 3;
	}


	@Override
	public boolean equals(Object obj) {
		return delegate.equals(obj);
	}

	public void fastDispose()
	{
		dummyMesh.deallocate(ResourceMonitor.resourceMonitor.getQueue());
	}

	public Vector3 getPositionOfVertex(int v1, Vector3 v) {
		return delegate.getPositionOfVertex(v1, v);
	}

	public iGeometry getUnderlyingGeometry() {
		return delegate.getUnderlyingGeometry();
	}

	public int getVertexCursor() {
		return delegate.getVertexCursor();
	}

	@Override
	public int hashCode() {
		return delegate.hashCode();
	}

	public void hintPrimitiveType(int primType) {
		delegate.hintPrimitiveType(primType);
	}

	public void inject(FloatBuffer vertex, IntBuffer triangle, int[] auxs, FloatBuffer[] auxBuffers, int oldVertexCursor) {
		delegate.inject(vertex, triangle, auxs, auxBuffers, oldVertexCursor);
	}

	public int nextFace(int v1, int v2, int v3) {
		return delegate.nextFace(v1, v2, v3);
	}

	public int nextFace(Vector3 v1, Vector3 v2, Vector3 v3) {
		return delegate.nextFace(v1, v2, v3);
	}

	public int nextVertex(Vector3 v1) {
		return delegate.nextVertex(v1);
	}

	public void open() {
		delegate.open();
	}

	public void remove() {
		delegate.remove();
	}

	public void setAux(int vertex, int id, float a) {
		delegate.setAux(vertex, id, a);
	}

	public void setAux(int vertex, int id, float a, float b) {
		delegate.setAux(vertex, id, a, b);
	}

	public void setAux(int vertex, int id, float a, float b, float c) {
		delegate.setAux(vertex, id, a, b, c);
	}

	public void setAux(int vertex, int id, float a, float b, float c, float d) {
		delegate.setAux(vertex, id, a, b, c, d);
	}

	public void setPositionOfVertex(int n, Vector3 v1) {
		delegate.setPositionOfVertex(n, v1);
	}

	public iDynamicMesh setShrinkPolicy(int shrink_policy_tries, float shrink_policy_min_fraction) {
		return delegate.setShrinkPolicy(shrink_policy_tries, shrink_policy_min_fraction);
	}

	@Override
	public String toString() {
		return delegate.toString();
	}

	public int[] vertexForFace(int face) {
		return delegate.vertexForFace(face);
	}
}
