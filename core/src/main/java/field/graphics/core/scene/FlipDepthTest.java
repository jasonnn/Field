package field.graphics.core.scene;

import field.graphics.core.pass.StandardPass;

import static org.lwjgl.opengl.GL11.GL_GREATER;
import static org.lwjgl.opengl.GL11.glDepthFunc;

/**
 * Created by jason on 8/2/14.
 */
public
class FlipDepthTest extends OnePassElement {

    boolean enable = false;

    public
    FlipDepthTest() {
        super(StandardPass.preRender);

    }

    /**
     * @param b
     */
    public
    FlipDepthTest(boolean b) {
        super(StandardPass.preRender);
        enable = !b;
    }

    @Override
    public
    void performPass() {
        glDepthFunc(GL_GREATER);
    }
}
