package field.graphics.core.scene;

import field.graphics.core.pass.StandardPass;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by jason on 8/2/14.
 */
public
class PolygonOffset extends TwoPassElement {

    float factor;

    float units;

    boolean doLine = false;

    public
    PolygonOffset(float factor, float units) {
        super("polygonoffset", StandardPass.preRender, StandardPass.postRender);
        this.factor = factor;
        this.units = units;
    }

    /**
     * @param i
     * @param j
     * @param b
     */
    public
    PolygonOffset(float factor, float units, boolean b) {
        super("polygonoffset", StandardPass.preRender, StandardPass.postRender);
        this.factor = factor;
        this.units = units;
        doLine = b;
    }

    @Override
    protected
    void post() {
        if (BasicUtilities.thinState)
            return;
        glDisable(GL_POLYGON_OFFSET_FILL);
        if (doLine)
            glDisable(GL_POLYGON_OFFSET_LINE);
    }

    @Override
    protected
    void pre() {
        if (BasicUtilities.thinState)
            return;
        glPolygonOffset(factor, units);
        glEnable(GL_POLYGON_OFFSET_FILL);
        if (doLine)
            glEnable(GL_POLYGON_OFFSET_LINE);
    }

    @Override
    protected
    void setup() {
    }

}
