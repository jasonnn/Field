package field.graphics.core.scene;

import field.graphics.core.pass.StandardPass;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by jason on 8/2/14.
 */
public
class DisableBlending extends OnePassElement {
    public
    DisableBlending() {
        super(StandardPass.preRender);

    }

    @Override
    public
    void performPass() {
        glDisable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ZERO);
    }
}
