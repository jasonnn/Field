package field.graphics.core.scene;

import field.graphics.core.BasicContextManager;
import field.graphics.core.pass.StandardPass;

import static org.lwjgl.opengl.GL11.glDepthMask;

/**
 * Created by jason on 8/2/14.
 */
public
class DepthMask extends TwoPassElement {

    boolean enable = false;

    public
    DepthMask() {
        super("", StandardPass.preRender, StandardPass.postRender);
    }

    public
    DepthMask(StandardPass pre, StandardPass post) {
        super("", pre, post);
    }

    @Override
    protected
    void post() {
        glDepthMask(true);
    }

    @Override
    protected
    void pre() {
        glDepthMask(false);
    }

    @Override
    protected
    void setup() {
        BasicContextManager.putId(this, 0);
    }
}
