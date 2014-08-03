package field.graphics.core.scene;

import field.graphics.core.pass.StandardPass;
import field.graphics.windowing.FullScreenCanvasSWT;
import field.math.linalg.Vector3;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by jason on 8/2/14.
 */
public
class Clear extends OnePassElement implements ISceneListElement {

    Vector3 background = new Vector3(0.1, 0.1, 0.1);

    float alpha = 0;

    boolean disable = false;

    int x;

    public
    Clear(Vector3 colour) {
        super(StandardPass.preRender);
        background = colour;
    }

    public
    Clear(Vector3 colour, float alpha) {
        super(StandardPass.preRender);
        background = colour;
        this.alpha = alpha;
    }

    @Override
    public
    void performPass() {

        glColorMask(true, true, true, true);

        assert glGetError() == 0;
        if (disable)
            return;
        x++;

        if (FullScreenCanvasSWT.currentCanvas == null || (!FullScreenCanvasSWT.currentCanvas.passiveStereo
                                                          || FullScreenCanvasSWT.getSide()
                                                             == FullScreenCanvasSWT.StereoSide.right)) {
            glClearColor(background.get(0), background.get(1), background.get(2), alpha);
            assert glGetError() == 0;
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            assert glGetError() == 0;
        }
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
