package field.graphics.core.scene;

/**
 * an advanced piece of geometry, that is the results of processing one mesh into another mesh
 * <p/>
 * calls like myInputOutputGeometry.aux(3,3) and .vertex().put(...) get piped through to input. skinning is one example of a transformative geometry, proceedural splines will be another.
 */
public
interface IInputOuputGeometry extends IGeometry {
    public
    IGeometry getInput();

    public
    IGeometry getOutput();

    // just copies an aux channel (if it exists in the inpu);
    public
    void copyAux(int auxId);
}
