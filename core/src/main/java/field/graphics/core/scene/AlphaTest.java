package field.graphics.core.scene;

import field.graphics.core.pass.StandardPass;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by jason on 8/2/14.
 */
public
class AlphaTest extends TwoPassElement {
    public
    AlphaTest() {
        super("", StandardPass.preRender, StandardPass.postRender);

    }

    int func = GL_LESS;

    float val = 0.5f;

    @Override
    public
    void pre() {
        glEnable(GL_ALPHA_TEST);
        glAlphaFunc(func, val);
    }

    @Override
    public
    void post() {
        glDisable(GL_ALPHA_TEST);
    }

    protected
    void setup() {
    }
}
