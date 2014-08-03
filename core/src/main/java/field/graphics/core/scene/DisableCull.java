package field.graphics.core.scene;

import field.graphics.core.pass.StandardPass;

import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.glDisable;

/**
 * @author marc created on Jul 20, 2003
 */
public
class DisableCull extends OnePassElement {

    public
    DisableCull() {
        super(StandardPass.render);

    }

    @Override
    public
    void performPass() {
        glDisable(GL_CULL_FACE);
    }

}
