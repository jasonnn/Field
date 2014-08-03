package field.graphics.core.scene;

import field.graphics.core.pass.StandardPass;
import field.math.linalg.Vector4;

import static org.lwjgl.opengl.GL14.glBlendColor;

/**
 * Created by jason on 8/2/14.
 */
public
class SetBlendColor extends OnePassElement {

    private final Vector4 constant;

    public
    SetBlendColor(StandardPass preRender, Vector4 vector4) {
        super(preRender);
        this.constant = vector4;
    }

    @Override
    public
    void performPass() {
        if (BasicUtilities.thinState)
            return;

        if (constant != null)
            glBlendColor(constant.x, constant.y, constant.z, constant.w);
    }

}
