package field.graphics.core.scene;

import field.graphics.core.pass.StandardPass;

import static org.lwjgl.opengl.GL11.glLineWidth;

/**
 * Created by jason on 8/2/14.
 */
public
class LineWidth extends OnePassElement {

    public float width = 1;

    public
    LineWidth() {
        super(StandardPass.preRender);
    }

    @Override
    public
    void performPass() {

        glLineWidth(width);
    }
}
