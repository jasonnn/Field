package field.graphics.core.scene;

import field.graphics.core.pass.StandardPass;

import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

/**
 * Created by jason on 8/2/14.
 */
public
class SetBlendAdd extends SetBlendMode {

    public
    SetBlendAdd() {
        super(StandardPass.preRender, GL_SRC_ALPHA, GL_ONE);
    }
}
