package field.graphics.core.scene;

import field.graphics.core.pass.StandardPass;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by jason on 8/2/14.
 */
public
class ClearAlpha extends OnePassElement implements ISceneListElement {

    private final float to;

    public
    ClearAlpha(float to) {
        super(StandardPass.preRender);
        this.to = to;
    }

    public
    ClearAlpha(StandardPass pass, float to) {
        super(pass);
        this.to = to;
    }

    @Override
    public
    void performPass() {
        // glClear(GL_COLOR_BUFFER_BIT
        // | GL_DEPTH_BUFFER_BIT |
        // GL_STENCIL_BUFFER_BIT);

        glClearColor(0, 0, 0, to);
        glColorMask(false, false, false, true);
        glClear(GL_COLOR_BUFFER_BIT);
        glColorMask(true, true, true, true);
    }
}
