package field.graphics.core.scene;

import field.graphics.core.pass.StandardPass;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by jason on 8/2/14.
 */
public
class ClearDepth extends OnePassElement implements ISceneListElement {
    public
    ClearDepth() {
        super(StandardPass.preRender);
    }

    public
    ClearDepth(StandardPass pass) {
        super(pass);
    }

    @Override
    public
    void performPass() {
        // glClear(GL_COLOR_BUFFER_BIT
        // | GL_DEPTH_BUFFER_BIT |
        // GL_STENCIL_BUFFER_BIT);

        glClearDepth(1);
        glClear(GL_DEPTH_BUFFER_BIT);
    }
}
