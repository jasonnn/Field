package field.graphics.core.scene;

import field.graphics.core.pass.StandardPass;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by jason on 8/2/14.
 */
public
class EnableBlending extends OnePassElement {
    public
    EnableBlending() {
        super(StandardPass.preRender);

    }

    @Override
    public
    void performPass() {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }
}
