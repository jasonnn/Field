package field.graphics.core;

import field.math.abstraction.IInplaceProvider;
import field.math.graph.IMutable;
import field.math.linalg.CoordinateFrame;
import field.math.linalg.Quaternion;
import field.math.linalg.Vector3;
import field.math.linalg.iCoordinateFrame;
import field.namespace.change.IChangable;
import field.namespace.change.IModCount;

/**
 * A iTransform computes and maintains a current LocalToWorld transform. This way we can do all kinds of things (like skinning, proceedural lines, collisions) that repeatedly need local to world information. It's not like this information wasn't being computed before. Its just that it was buried in the native side of the graphics system
 * <p/>
 * this is an interface, because its important that these things are interfaces, _and_ its unclear right now whether we want to lock into an implementation - (for example, sometimes we might want scales to propogate through, othertimes we'd just as soon save the cycles and use quat-vec)
 * <p/>
 * we also use modification counters so that people can cache things about this object inteligently
 *
 * @see innards.graphics.basic.BasicFrameTransform
 */

public
interface ITransform extends IMutable<ITransform>, IInplaceProvider<iCoordinateFrame.iMutable>, IChangable {
    /**
     * this will not reflect non-committed changes to the transform controllers, or any other transform controller further up the chain. hence the name, 'committed' it may, however, change during the rendering pass process (this is from iCoordinateFrameProvider
     */
    public
    CoordinateFrame getLocal(CoordinateFrame out);

    /**
     * get should always return localToWorld
     */
    public
    CoordinateFrame get(CoordinateFrame out);

    /**
     * This method sets the current value of the rotation buffer to r with no blend
     */
    public
    void setRotation(Quaternion r);

    public
    void setTranslation(Vector3 v);

    /**
     * This method blends r using a weight of w with other rotations in the buffer
     */
    public
    void blendRotation(Quaternion r, float w);

    public
    void blendTranslation(Vector3 v, float w);

    /**
     * Returns the current contents of the rotation buffer after any blends.
     */

    public
    Quaternion getCurrentRotation(Quaternion r);

    /**
     * Returns the current contents of the translation buffer.
     */
    public
    Vector3 getCurrentTranslation(Vector3 t);

    /**
     * returns an object that you can ask about if this thing has changed or not
     */
    public
    IModCount getModCount(Object withRespectTo);

}
