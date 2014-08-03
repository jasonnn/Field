package field.graphics.core.scene;

import field.graphics.core.pass.StandardPass;

import static org.lwjgl.opengl.GL11.GL_ONE;

/**
 * Created by jason on 8/2/14.
 */
public
class SetBlendStraightAdd extends SetBlendMode {

    public
    SetBlendStraightAdd() {
        super(StandardPass.preRender, GL_ONE, GL_ONE);
    }
}
