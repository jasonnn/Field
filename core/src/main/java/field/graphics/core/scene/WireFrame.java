package field.graphics.core.scene;

import field.graphics.core.pass.StandardPass;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by jason on 8/2/14.
 */
public
class WireFrame extends TwoPassElement implements ISceneListElement {

    boolean b = true;

    int width = -1;

    public
    WireFrame() {
        super("wireframe", StandardPass.preRender, StandardPass.postRender);
    }

    public
    WireFrame(boolean b) {
        super("wireframe", StandardPass.preRender, StandardPass.postRender);
        this.b = b;
    }

    public
    WireFrame(int i) {
        super("wireframe", StandardPass.preRender, StandardPass.postRender);
        width = i;
    }

    @Override
    protected
    void post() {
        // if (b)
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        // else
        // glPolygonMode(GL_FRONT_AND_BACK,
        // GL_LINE);
    }

    @Override
    protected
    void pre() {

        if (b) {
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
            // glPointSize(1f);
        }
        else
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        if (width != -1) {
            glLineWidth(width);
        }
        // glEnable(GL_LINE_SMOOTH);
        // glHint(GL_LINE_SMOOTH_HINT,
        // GL_NICEST);
        // glLineWidth(0.5f);

    }

    @Override
    protected
    void setup() {
    }
}
