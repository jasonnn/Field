package field.graphics.core.scene;

import field.graphics.core.pass.StandardPass;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by jason on 8/2/14.
 */
public
class NoMeshSmoothing extends TwoPassElement {
    private boolean b;

    public
    NoMeshSmoothing() {
        super("", StandardPass.preRender, StandardPass.postRender);
    }

    @Override
    protected
    void post() {
        if (b)
            glEnable(GL_POLYGON_SMOOTH);
    }

    @Override
    protected
    void pre() {
        b = glIsEnabled(GL_POLYGON_SMOOTH);
        glDisable(GL_POLYGON_SMOOTH);
    }

    @Override
    protected
    void setup() {
    }
}
