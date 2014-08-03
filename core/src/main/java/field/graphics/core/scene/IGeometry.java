package field.graphics.core.scene;

import field.math.abstraction.IInplaceProvider;
import field.math.linalg.iCoordinateFrame;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Map;

/**
 * an interface for geometry
 */
public
interface IGeometry extends ISceneListElement {
    /**
     * for manipulating geometry, here's the rule. don't hang onto these buffers, we'll update modification count so we know to resend these buffers
     */
    public
    FloatBuffer vertex();

    public
    ShortBuffer triangle();

    /**
     * this will lazily create an aux buffer with id 'auxID', this will also include normal and texture coordinate info , refer to the standard shader library for things like normals and texture coordinates
     * <p/>
     * if you pass in 0 you will still get the FloatBuffer if it currently exists, otherwise you will get null. i.e. no aux buffer will be created for you
     */
    public
    FloatBuffer aux(int auxId, int elementSize);

    public
    boolean hasAux(int auxId);

    /**
     * for initializing, and reinitializing geometry, typicaly, these just return 'this'
     */
    public
    IGeometry rebuildTriangle(int numTriangles);

    /**
     * this call will typically throw out all the normal information and aux information
     */
    public
    IGeometry rebuildVertex(int numVertex);

    public
    IGeometry setVertexLimit(int numVertex);

    public
    IGeometry setTriangleLimit(int numVertex);

    public
    int numVertex();

    public
    int numTriangle();

    /**
     * map of Integer (id) vs buffer
     */
    public
    Map auxBuffers();

    public
    IInplaceProvider<iCoordinateFrame.iMutable> getCoordinateProvider();
}
