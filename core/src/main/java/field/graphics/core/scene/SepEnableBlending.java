package field.graphics.core.scene;

import field.graphics.core.pass.StandardPass;
import field.math.abstraction.IFloatProvider;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.glBlendColor;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;

/**
 * Created by jason on 8/2/14.
 */
public
class SepEnableBlending extends OnePassElement {
    private final IFloatProvider alpha;

    public
    SepEnableBlending(IFloatProvider alpha) {
        super(StandardPass.preRender);
        this.alpha = alpha;
    }

    @Override
    public
    void performPass() {
        if (BasicUtilities.thinState)
            return;

        glEnable(GL_BLEND);
        glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_CONSTANT_ALPHA, GL_ONE);
        glBlendColor(0, 0, 0, alpha.evaluate());
    }
}
