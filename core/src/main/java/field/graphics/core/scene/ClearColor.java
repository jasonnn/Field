package field.graphics.core.scene;

import field.graphics.core.pass.StandardPass;
import field.math.linalg.Vector3;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by jason on 8/2/14.
 */
public
class ClearColor extends OnePassElement implements ISceneListElement {

    Vector3 background = new Vector3(0.1, 0.1, 0.1);

    float alpha = 0;

    boolean disable = false;

    int x;

    public
    ClearColor(Vector3 colour) {
        super(StandardPass.preRender);
        background = colour;
    }

    public
    ClearColor(Vector3 colour, float alpha) {
        super(StandardPass.preRender);
        background = colour;
        this.alpha = alpha;
    }

    @Override
    public
    void performPass() {
        if (disable)
            return;
        x++;
        glClearColor(background.get(0), background.get(1), background.get(2), alpha);
        glClear(GL_COLOR_BUFFER_BIT);
        // glClear(
        // GL_DEPTH_BUFFER_BIT |
        // GL_STENCIL_BUFFER_BIT);
    }

    public
    void setClearColor(Vector3 c) {
        background = c;
    }

    public
    void setClearColor(Vector3 c, float alpha) {
        background = c;
        this.alpha = alpha;
    }

    public
    void setDisable(boolean disable) {
        this.disable = disable;
    }
}
