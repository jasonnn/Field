package field.graphics.ci;


import field.core.windowing.GLComponentWindow;
import field.graphics.ci.CoreImageCanvasUtils.Accumulator;
import field.graphics.ci.CoreImageCanvasUtils.Image;
import field.graphics.core.BasicFrameBuffers.SingleFrameBuffer;
import field.graphics.core.BasicFrameBuffers.iDisplayable;
import field.graphics.core.BasicGLSLangProgram;
import field.graphics.core.CoreHelpers;
import field.graphics.windowing.FullScreenCanvasSWT;

import static org.lwjgl.opengl.ARBShaderObjects.glUseProgramObjectARB;
import static org.lwjgl.opengl.GL11.*;

public
class Destination2 implements iDisplayable {

    public SingleFrameBuffer fbo;
    private final int width;
    private final int height;

    Image drawn;
    Accumulator drawnAccumulator;

    public
    Destination2(int width, int height) {
        this.width = width;
        this.height = height;
        fbo = new SingleFrameBuffer(width, height, true, false, false);
    }

    public
    void setImage(Image i) {
        drawn = i;
        drawnAccumulator = null;
        dirty();
    }

    public
    void setAccumulator(Accumulator i) {
        drawn = null;
        drawnAccumulator = i;
        dirty();
    }

    boolean isDirty = false;

    public
    void dirty() {
        isDirty = true;
    }

    protected
    void clean() {
        if ((drawn == null) || !isDirty) return;

        fbo.enter();

        glViewport(0, 0, this.width, this.height);
        CoreHelpers.glMatrixMode(GL_PROJECTION);
        CoreHelpers.glPushMatrix();
        CoreHelpers.glLoadIdentity();
        CoreHelpers.glOrtho(0, this.width, 0, this.height, -1, 1);
        CoreHelpers.glMatrixMode(GL_MODELVIEW);
        CoreHelpers.glPushMatrix();
        CoreHelpers.glLoadIdentity();
        glBlendFunc(GL_ONE, GL_ZERO);
        glEnable(GL_BLEND);

        glClearColor(1, 1, 1, 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glUseProgramObjectARB(0);

        new CoreImage().context_drawImageNow(FullScreenCanvasSWT.currentCanvas.getOnCanvasPLine()
                                                                              .getCoreGraphicsContext()
                                                                              .getContext(),
                                             drawn.coreimage,
                                             0,
                                             0,
                                             width,
                                             height,
                                             0,
                                             0,
                                             width,
                                             height);
        glUseProgramObjectARB(BasicGLSLangProgram.currentProgram.getShader());
        CoreHelpers.glPopMatrix();
        CoreHelpers.glMatrixMode(GL_PROJECTION);
        CoreHelpers.glPopMatrix();
        CoreHelpers.glMatrixMode(GL_MODELVIEW);

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);

        fbo.exit();
        glViewport(0,
                   0,
                   GLComponentWindow.getCurrentWindow(null).getCanvas().getSize().x,
                   GLComponentWindow.getCurrentWindow(null).getCanvas().getSize().y);

        isDirty = false;
    }

    protected
    void cleanAccumulator() {
        if ((drawnAccumulator == null) || !isDirty) return;

        //System.out.println(" cleaning acumulator ");

        fbo.enter();

        glViewport(0, 0, this.width, this.height);
        CoreHelpers.glMatrixMode(GL_PROJECTION);
        CoreHelpers.glPushMatrix();
        CoreHelpers.glLoadIdentity();
        CoreHelpers.glOrtho(0, this.width, 0, this.height, -1, 1);
        CoreHelpers.glMatrixMode(GL_MODELVIEW);
        CoreHelpers.glPushMatrix();
        CoreHelpers.glLoadIdentity();
        glBlendFunc(GL_ONE, GL_ZERO);
        glEnable(GL_BLEND);

        glClearColor(1, 1, 1, 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glUseProgramObjectARB(0);

        new CoreImage().context_drawImageNow(FullScreenCanvasSWT.currentCanvas.getOnCanvasPLine()
                                                                              .getCoreGraphicsContext()
                                                                              .getContext(),
                                             drawnAccumulator.getOutputImage().coreimage,
                                             0,
                                             0,
                                             width,
                                             height,
                                             0,
                                             0,
                                             width,
                                             height);
        glUseProgramObjectARB(BasicGLSLangProgram.currentProgram.getShader());
        CoreHelpers.glPopMatrix();
        CoreHelpers.glMatrixMode(GL_PROJECTION);
        CoreHelpers.glPopMatrix();
        CoreHelpers.glMatrixMode(GL_MODELVIEW);

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);

        fbo.exit();
        glViewport(0,
                   0,
                   GLComponentWindow.getCurrentWindow(null).getCanvas().getSize().x,
                   GLComponentWindow.getCurrentWindow(null).getCanvas().getSize().y);

        isDirty = false;
    }

    public
    void display() {

        //	;//System.out.println("inside display <"+drawn+" "+drawnAccumulator+">");

        if (drawn != null) clean();
        else if (drawnAccumulator != null) cleanAccumulator();
    }

}
