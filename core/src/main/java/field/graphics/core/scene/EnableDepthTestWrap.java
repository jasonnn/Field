package field.graphics.core.scene;

import field.graphics.core.pass.StandardPass;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by jason on 8/2/14.
 */
public
class EnableDepthTestWrap extends TwoPassElement {

    boolean enable = false;

    public
    EnableDepthTestWrap() {
        super("standard", StandardPass.postRender, StandardPass.preDisplay);
    }

    @Override
    protected
    void post() {
        glDepthFunc(GL_ALWAYS);
    }

    @Override
    protected
    void pre() {
        glDepthFunc(GL_LESS);
    }

    @Override
    protected
    void setup() {
    }

}
