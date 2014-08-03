package field.graphics.core.scene;

import field.graphics.core.pass.StandardPass;

import static org.lwjgl.opengl.GL11.*;

/**
 * sets up culling, blending and depthing just like we'd expect the
 * defaults to be for general 3d graphics
 */
public
class Standard extends OnePassElement {

    boolean cull = false;

    public
    Standard() {
        this(false);
    }

    public
    Standard(boolean cull) {
        super(StandardPass.render);
        this.cull = cull;
    }

    @Override
    public
    void performPass() {
        if (BasicUtilities.thinState)
            return;

        if (cull) {
            glEnable(GL_CULL_FACE);
            glCullFace(GL_BACK);
        }
        else {
            glDisable(GL_CULL_FACE);
        }
        // glEnable(GL_MULTISAMPLE_ARB);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_DEPTH_TEST);
        // glEnable(GL_POLYGON_SMOOTH);
        // glHint(GL_POLYGON_SMOOTH_HINT,
        // GL_NICEST);
    }
}
