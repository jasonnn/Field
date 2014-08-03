package field.graphics.core.scene;

import field.graphics.core.pass.StandardPass;
import field.math.linalg.Vector4;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.*;

/**
 * Created by jason on 8/2/14.
 */
public
class SetBlendMode extends OnePassElement {

    private final int src;

    private final int dest;

    private Vector4 constant;

    static public Vector4 constantMul = new Vector4(1, 1, 1, 1);

    public
    SetBlendMode(StandardPass requestPass, int src, int dest) {
        super(requestPass);
        this.src = src;
        this.dest = dest;
    }

    public
    SetBlendMode(StandardPass preRender, int gl_constant_alpha, int gl_one_minus_constant_alpha, Vector4 vector4) {
        this(preRender, gl_constant_alpha, gl_one_minus_constant_alpha);
        this.constant = vector4;
    }

    @Override
    public
    void performPass() {
        if (BasicUtilities.thinState)
            return;

        if (constant != null)
            glBlendColor(constant.x, constant.y, constant.z, constant.w);

        glBlendFunc(src, dest);
        glBlendEquation(GL_FUNC_ADD);
        glEnable(GL_BLEND);
    }

}
