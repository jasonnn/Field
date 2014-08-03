package field.graphics.imageprocessing;

import field.bytecode.protect.Woven;
import field.bytecode.protect.dispatch.Cont;
import field.bytecode.protect.dispatch.ReturnCode;
import field.bytecode.protect.dispatch.aRun;
import field.core.dispatch.Rect;
import field.graphics.core.BasicContextManager;
import field.graphics.core.BasicFrameBuffers.iHasFBO;
import field.graphics.core.BasicGLSLangProgram;
import field.graphics.core.BasicGeometry;
import field.graphics.core.BasicGeometry.QuadMesh;
import field.graphics.core.BasicGeometry.TriangleMesh;
import field.graphics.core.GLConstants;
import field.graphics.core.pass.StandardPass;
import field.graphics.core.scene.*;
import field.graphics.imageprocessing.ImageProcessing.TextureWrapper;
import field.graphics.imageprocessing.ImageProcessing.iProcessesMesh;
import field.graphics.windowing.FullScreenCanvasSWT;
import field.graphics.windowing.FullScreenCanvasSWT.StereoSide;
import field.math.abstraction.IAcceptor;
import field.math.abstraction.IProvider;
import field.math.linalg.Vector4;
import field.util.TaskQueue;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.Method;
import java.nio.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.GL_UNSIGNED_INT_8_8_8_8_REV;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.GL_TEXTURE_RECTANGLE;

/**
 * framebuffer in, framebuffer out
 *
 * @author marc
 */
@Woven
public
class ImageProcessingTwoOutput implements iImageProcessor {

    private final IProvider<Integer>[] fboInput;

    final int width;

    final int height;

    private final boolean useRect;

    private final boolean genMipmap;

    private final boolean useFloat;

    public
    ImageProcessingTwoOutput(IProvider<Integer>[] fboInput,
                             int width,
                             int height,
                             boolean useRect,
                             boolean genMipmap,
                             boolean useFloat) {
        this.fboInput = fboInput;
        this.width = width;
        this.height = height;
        this.useRect = useRect;
        this.genMipmap = genMipmap;
        this.useFloat = useFloat;
        if (genMipmap) assert !useFloat;

        initializeGeometry();

    }

    public
    ImageProcessingTwoOutput(final iHasFBO[] fboInput,
                             int width,
                             int height,
                             boolean useRect,
                             boolean genMipmap,
                             boolean useFloat) {
        this.fboInput = new IProvider[fboInput.length];
        for (int i = 0; i < fboInput.length; i++) {
            final int fi = i;
            this.fboInput[i] = new IProvider<Integer>() {
                @Override
                public
                Integer get() {
                    return fboInput[fi].getFBO();
                }
            };
        }
        this.width = width;
        this.height = height;
        this.useRect = useRect;
        this.genMipmap = genMipmap;
        this.useFloat = useFloat;
        if (genMipmap) assert !useFloat;

        initializeGeometry();

    }

    protected
    void initializeGeometry() {

        // geometry ------------------------------------
        mesh = new BasicGeometry.TriangleMesh(StandardPass.preRender);
        mesh.rebuildTriangle(2);
        mesh.rebuildVertex(4);

        mesh.vertex()
            .put(-1)
            .put(-1)
            .put(0.5f)
            .put(-1)
            .put(1)
            .put(0.5f)
            .put(1)
            .put(1)
            .put(0.5f)
            .put(1)
            .put(-1)
            .put(0.5f);
        mesh.triangle().put((short) 0).put((short) 1).put((short) 2).put((short) 0).put((short) 2).put((short) 3);
        if (useRect)
            mesh.aux(GLConstants.texture0_id, 2)
                .put(0)
                .put(0)
                .put(0)
                .put(height)
                .put(width)
                .put(height)
                .put(width)
                .put(0);
        else
            mesh.aux(GLConstants.texture0_id, 2).put(0).put(0).put(0).put(1).put(1).put(1).put(1).put(0);
    }

    boolean initialized = false;

    Vector4 clearColor = null;
    Vector4 clearColor2 = null;

    public
    ImageProcessingTwoOutput setClearColor(Vector4 clearColor, Vector4 clearColor2) {
        this.clearColor = clearColor;
        this.clearColor2 = clearColor2;
        return this;
    }

    IntBuffer color01 = ByteBuffer.allocateDirect(4 * 2)
                                  .order(ByteOrder.nativeOrder())
                                  .asIntBuffer()
                                  .put(new int[]{GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1});

    public
    void update() {

        if (!initialized) initialize();

        int current = getCurrentFBO();

        glBindFramebuffer(GL_FRAMEBUFFER, current);
        glViewport(0, 0, width, height);

        glDrawBuffer(GL_COLOR_ATTACHMENT0);

        if (clearColor != null) {
            glClearColor(clearColor.x, clearColor.y, clearColor.z, clearColor.w);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
        }
        else {
            glClear(GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
        }

        glDrawBuffer(GL_COLOR_ATTACHMENT1);
        if (clearColor2 != null) {
            glClearColor(clearColor2.x, clearColor2.y, clearColor2.z, clearColor2.w);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
        }
        else {
            glClear(GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
        }

        color01.rewind();
        glDrawBuffers(color01);

        for (int i = 0; i < fboInput.length; i++) {
            glActiveTexture(GL_TEXTURE0 + i);
            glBindTexture(useRect ? GL_TEXTURE_RECTANGLE : GL_TEXTURE_2D, fboInput[i].get());
        }

        queue.update();

        renderGeometry();

        for (int i = 0; i < fboInput.length; i++) {
            glActiveTexture(GL_TEXTURE0 + i);
            glBindTexture(useRect ? GL_TEXTURE_RECTANGLE : GL_TEXTURE_2D, 0);
        }
        glActiveTexture(GL_TEXTURE0);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        if (genMipmap) {
            glBindTexture(useRect ? GL_TEXTURE_RECTANGLE : GL_TEXTURE_2D, getOutput(0).get());
            glGenerateMipmap(useRect ? GL_TEXTURE_RECTANGLE : GL_TEXTURE_2D);
            glBindTexture(useRect ? GL_TEXTURE_RECTANGLE : GL_TEXTURE_2D, 0);
            glBindTexture(useRect ? GL_TEXTURE_RECTANGLE : GL_TEXTURE_2D, getOutput(1).get());
            glGenerateMipmap(useRect ? GL_TEXTURE_RECTANGLE : GL_TEXTURE_2D);
            glBindTexture(useRect ? GL_TEXTURE_RECTANGLE : GL_TEXTURE_2D, 0);
        }
        glDrawBuffer(GL_BACK);

    }

    public
    void addChild(final ISceneListElement e) {
        mesh.addChild(e);
        if (e instanceof iProcessesMesh) {
            queue.new Task() {
                @Override
                protected
                void run() {

                    ((iProcessesMesh) e).process(mesh);

                    recur();
                }
            };
        }

    }

    BasicSceneList stencilList = new BasicSceneList();

    protected
    void renderGeometry() {

        if (fadeMesh != null) {
            fadeMesh.performPass();
        }

        // if (stencilList.getChildren().size() > 0) {
        // glEnable(GL_STENCIL_TEST);
        // glStencilFunc(GL_ALWAYS, 0x1, 0x1);
        // stencilList.performPass(null);
        // glStencilFunc(GL_ALWAYS, 0x0, 0x1);
        // }

        mesh.performPass();

        // if (stencilList.getChildren().size() > 0) {
        //
        // }

    }

    TaskQueue queue = new TaskQueue();

    public
    TaskQueue getRenderQueue() {
        return queue;
    }

    public
    TriangleMesh getGeometry() {
        return mesh;
    }

    boolean pause = false;

    public
    void pause() {
        this.pause = true;
    }

    public
    void unpause() {
        this.pause = false;
    }

    public
    void join(FullScreenCanvasSWT c) {
        aRun arun = new aRun() {
            @Override
            public
            ReturnCode head(Object calledOn, Object[] args) {

                if (pause) return super.head(calledOn, args);

                update();
                return super.head(calledOn, args);
            }
        };
        if (c.getSceneListSide() == StereoSide.middle) Cont.linkWith(c, FullScreenCanvasSWT.method_beforeFlush, arun);
        else {
            Method attachMethod = (c.getSceneListSide() == StereoSide.left)
                                  ? FullScreenCanvasSWT.method_beforeLeftFlush
                                  : FullScreenCanvasSWT.method_beforeRightFlush;
            Cont.linkWith(c, attachMethod, arun);
        }

    }

    int[] fbo = {-1};

    int[] rb = {-1};

    int[] tex = {-1, -1};

    private TriangleMesh mesh;

    private QuadMesh fadeMesh;

    public
    void initialize() {
        assert glGetError() == 0;

        // fbo & texture -------------------------------------------

        fbo[0] = glGenFramebuffers();
        rb[0] = glGenRenderbuffers();
        tex[0] = glGenTextures();
        tex[1] = glGenTextures();
        glBindFramebuffer(GL_FRAMEBUFFER, fbo[0]);

        int gl_texture_min_filter = genMipmap ? GL_LINEAR_MIPMAP_LINEAR : GL_LINEAR;
        int gl_texture_mag_filter = GL_LINEAR;

        glBindTexture(useRect ? GL_TEXTURE_RECTANGLE : (useRect ? GL_TEXTURE_RECTANGLE : GL_TEXTURE_2D), tex[0]);
        // glPixelStorei(GL_UNPACK_CLIENT_STORAGE_APPLE, 0);
        glTexImage2D(useRect ? GL_TEXTURE_RECTANGLE : GL_TEXTURE_2D,
                     0,
                     useFloat ? GL_RGBA16F : GL_RGBA,
                     width,
                     height,
                     0,
                     GL_RGBA,
                     useFloat ? GL11.GL_FLOAT : GL_UNSIGNED_INT_8_8_8_8_REV,
                     (ByteBuffer) null);
        glFramebufferTexture2D(GL_FRAMEBUFFER,
                               GL_COLOR_ATTACHMENT0,
                               useRect ? GL_TEXTURE_RECTANGLE : GL_TEXTURE_2D,
                               tex[0],
                               0);
        glTexParameteri(useRect ? GL_TEXTURE_RECTANGLE : GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(useRect ? GL_TEXTURE_RECTANGLE : GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(useRect ? GL_TEXTURE_RECTANGLE : GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, gl_texture_mag_filter);
        glTexParameteri(useRect ? GL_TEXTURE_RECTANGLE : GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, gl_texture_min_filter);

        glBindTexture(useRect ? GL_TEXTURE_RECTANGLE : (useRect ? GL_TEXTURE_RECTANGLE : GL_TEXTURE_2D), tex[1]);
        // glPixelStorei(GL_UNPACK_CLIENT_STORAGE_APPLE, 0);
        glTexImage2D(useRect ? GL_TEXTURE_RECTANGLE : GL_TEXTURE_2D,
                     0,
                     useFloat ? GL_RGBA16F : GL_RGBA,
                     width,
                     height,
                     0,
                     GL_RGBA,
                     useFloat ? GL11.GL_FLOAT : GL_UNSIGNED_INT_8_8_8_8_REV,
                     (ByteBuffer) null);
        glFramebufferTexture2D(GL_FRAMEBUFFER,
                               GL_COLOR_ATTACHMENT1,
                               useRect ? GL_TEXTURE_RECTANGLE : GL_TEXTURE_2D,
                               tex[1],
                               0);
        glTexParameteri(useRect ? GL_TEXTURE_RECTANGLE : GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(useRect ? GL_TEXTURE_RECTANGLE : GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(useRect ? GL_TEXTURE_RECTANGLE : GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, gl_texture_mag_filter);
        glTexParameteri(useRect ? GL_TEXTURE_RECTANGLE : GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, gl_texture_min_filter);

        glBindRenderbuffer(GL_RENDERBUFFER, rb[0]);

        // glRenderbufferStorage(GL_RENDERBUFFER,
        // GL_DEPTH_COMPONENT24, width, height);
        // glFramebufferRenderbuffer(GL_FRAMEBUFFER,
        // GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rb[0]);

        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rb[0]);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_STENCIL_ATTACHMENT, GL_RENDERBUFFER, rb[0]);

        int status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
        assert status == GL_FRAMEBUFFER_COMPLETE : status;
        BasicContextManager.putId(this, fbo[0]);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        initialized = true;
    }

    public
    TwoPassElement getOutputElement(final int num) {
        return new TwoPassElement("", StandardPass.preRender, StandardPass.postRender) {

            @Override
            protected
            void setup() {
                BasicContextManager.putId(this, 1);
            }

            @Override
            protected
            void pre() {

                if (!initialized) initialize();

                glBindTexture(useRect ? GL_TEXTURE_RECTANGLE : GL_TEXTURE_2D, getOutput(num).get());
                glEnable(useRect ? GL_TEXTURE_RECTANGLE : GL_TEXTURE_2D);
            }

            @Override
            protected
            void post() {
                glBindTexture(useRect ? GL_TEXTURE_RECTANGLE : GL_TEXTURE_2D, 0);
                glDisable(useRect ? GL_TEXTURE_RECTANGLE : GL_TEXTURE_2D);
            }
        };
    }

    public
    TwoPassElement getOutputElement(final int num, final boolean leftOnly) {
        return new TwoPassElement("", StandardPass.preRender, StandardPass.postRender) {

            @Override
            protected
            void setup() {
                BasicContextManager.putId(this, 1);
            }

            @Override
            protected
            void pre() {

                if (!initialized) initialize();

                if (leftOnly == (FullScreenCanvasSWT.getSide() == FullScreenCanvasSWT.StereoSide.left)) {
                    glBindTexture(useRect ? GL_TEXTURE_RECTANGLE : GL_TEXTURE_2D, getOutput(num).get());
                    glEnable(useRect ? GL_TEXTURE_RECTANGLE : GL_TEXTURE_2D);
                }
            }

            @Override
            protected
            void post() {
                if (leftOnly == (FullScreenCanvasSWT.getSide() == FullScreenCanvasSWT.StereoSide.left)) {
                    glBindTexture(useRect ? GL_TEXTURE_RECTANGLE : GL_TEXTURE_2D, 0);
                    glDisable(useRect ? GL_TEXTURE_RECTANGLE : GL_TEXTURE_2D);
                }
            }
        };
    }

    private
    int getCurrentFBO() {
        assert fbo[0] != -1;
        return fbo[0];
    }

    public
    IProvider<Integer> getOutput(final int num) {
        return new IProvider<Integer>() {
            public
            Integer get() {
                if (tex[0] == -1) initialize();
                return tex[num];
            }
        };
    }

    public
    void useHighResolutionMesh(int devision) {
        mesh = new BasicGeometry.QuadMesh(StandardPass.preRender);
        mesh.rebuildTriangle(devision * devision);
        mesh.rebuildVertex((1 + devision) * (1 + devision));

        FloatBuffer v = mesh.vertex();

        FloatBuffer tex = mesh.aux(GLConstants.texture0_id, 2);

        for (int x = 0; x < (devision + 1); x++) {
            for (int y = 0; y < (devision + 1); y++) {
                v.put(-1 + ((2 * x) / (float) devision)).put(-1 + ((2 * y) / (float) devision)).put(0.5f);
                tex.put(((useRect ? width : 1) * x) / (float) devision)
                   .put(((useRect ? width : 1) * y) / (float) devision);
            }
        }

        ShortBuffer s = mesh.triangle();

        for (int x = 0; x < devision; x++) {
            for (int y = 0; y < devision; y++) {
                s.put((short) ((y * (devision + 1)) + x + 1))
                 .put((short) (((y + 1) * (devision + 1)) + x + 1))
                 .put((short) (((y + 1) * (devision + 1)) + x))
                 .put((short) ((y * (devision + 1)) + x));
            }
        }

    }

    public
    ISceneListElement getOnscreenList(final Rect r, int n) {
        return getOnscreenList(n, r, new Vector4(0, 0, 0, 0), new Vector4(1, 1, 1, 1), false);
    }

    public
    ISceneListElement getOnscreenList(int output, final Rect r, Vector4 offset, Vector4 mul, final boolean genMip) {
        final TriangleMesh mesh = new BasicGeometry.QuadMesh(StandardPass.render);
        mesh.rebuildTriangle(1);
        mesh.rebuildVertex(4);

        mesh.vertex()
            .put((float) (r.x + r.w))
            .put((float) r.y)
            .put(0.5f)
            .put((float) (r.x + r.w))
            .put((float) (r.y + r.h))
            .put(0.5f)
            .put((float) (r.x))
            .put((float) (r.y + r.h))
            .put(0.5f)
            .put((float) (r.x))
            .put((float) (r.y))
            .put(0.5f);
        mesh.triangle().put((short) 0).put((short) 1).put((short) 2).put((short) 3);
        mesh.aux(GLConstants.texture0_id, 2)
            .put(useRect ? width : 1)
            .put(0)
            .put(useRect ? width : 1)
            .put(useRect ? height : 1)
            .put(0)
            .put(useRect ? height : 1)
            .put(0)
            .put(0);
        mesh.aux(GLConstants.color0_id, 4)
            .put(1)
            .put(1)
            .put(1)
            .put(1)
            .put(1)
            .put(1)
            .put(1)
            .put(1)
            .put(1)
            .put(1)
            .put(1)
            .put(1)
            .put(1)
            .put(1)
            .put(1)
            .put(1);

        // onscreen program
        BasicGLSLangProgram onscreenProgram = (!useRect
                                               ? new BasicGLSLangProgram("content/shaders/NDCvertex.glslang",
                                                                         "content/shaders/PutImageProcessingOnscreenFragmentSquare.glslang")
                                               : new BasicGLSLangProgram("content/shaders/NDCvertex.glslang",
                                                                         "content/shaders/PutImageProcessingOnscreenFragmentRect.glslang"));
        onscreenProgram.new SetIntegerUniform("depthTexture", 0);
        onscreenProgram.new SetUniform("offset", offset);
        onscreenProgram.new SetUniform("mul", mul);
        onscreenProgram.addChild(mesh);
        onscreenProgram.addChild(new TextureWrapper(genMip, useRect, getOutput(output), 0));
        onscreenProgram.addChild(new DisableDepthTest(true));

        return onscreenProgram;
    }

    public
    IAcceptor<Vector4[]> addFadePlane() {
        fadeMesh = new BasicGeometry.QuadMesh(StandardPass.render);
        fadeMesh.rebuildTriangle(1);
        fadeMesh.rebuildVertex(4);

        fadeMesh.vertex()
                .put(-1f)
                .put(-1f)
                .put(0f)
                .put(-1f)
                .put(1f)
                .put(0f)
                .put(1f)
                .put(1f)
                .put(0f)
                .put(1f)
                .put(-1f)
                .put(0f);
        fadeMesh.triangle().put((short) 0).put((short) 1).put((short) 2).put((short) 3);
        fadeMesh.addChild(new BasicGLSLangProgram("content/shaders/NDC2ColorVertex.glslang",
                                                  "content/shaders/VertexColor2Fragment.glslang"));
        fadeMesh.addChild(new DepthMask(StandardPass.transform, StandardPass.postRender));

        float colorAlpha = 0.1f;
        float alphaAlpha = 0.5f;
        fadeMesh.aux(GLConstants.color0_id, 4)
                .put(new float[]{0, 0, 0, colorAlpha, 0, 0, 0, colorAlpha, 0, 0, 0, colorAlpha, 0, 0, 0, colorAlpha});
        fadeMesh.aux(GLConstants.color0_id + 1, 4)
                .put(new float[]{0.5f,
                                 0.5f,
                                 0.5f,
                                 alphaAlpha,
                                 0.5f,
                                 0.5f,
                                 0.5f,
                                 alphaAlpha,
                                 0.5f,
                                 0.5f,
                                 0.5f,
                                 alphaAlpha,
                                 0.5f,
                                 0.5f,
                                 0.5f,
                                 alphaAlpha});

        final FloatBuffer root =
                ByteBuffer.allocate(fadeMesh.vertex().limit() * 4).asFloatBuffer().put(fadeMesh.vertex());

        // driver bug. Horrible seam
        // down middle of trianglulation
        fadeMesh.addChild(new OnePassElement(StandardPass.preRender) {
            boolean first = true;

            @Override
            public
            void performPass() {
                glEnable(GL_BLEND);
                glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ZERO, GL_ZERO);
            }
        });
        fadeMesh.addChild(new OnePassElement(StandardPass.postRender) {
            boolean first = true;

            @Override
            public
            void performPass() {
                glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            }
        });

        return new IAcceptor<Vector4[]>() {

            @Override
            public
            IAcceptor<Vector4[]> set(Vector4[] to) {
                fadeMesh.aux(GLConstants.color0_id, 4)
                        .put(new float[]{to[0].x,
                                         to[0].y,
                                         to[0].z,
                                         to[0].w,
                                         to[0].x,
                                         to[0].y,
                                         to[0].z,
                                         to[0].w,
                                         to[0].x,
                                         to[0].y,
                                         to[0].z,
                                         to[0].w,
                                         to[0].x,
                                         to[0].y,
                                         to[0].z,
                                         to[0].w});
                fadeMesh.aux(GLConstants.color0_id + 1, 4)
                        .put(new float[]{to[1].x,
                                         to[1].y,
                                         to[1].z,
                                         to[1].w,
                                         to[1].x,
                                         to[1].y,
                                         to[1].z,
                                         to[1].w,
                                         to[1].x,
                                         to[1].y,
                                         to[1].z,
                                         to[1].w,
                                         to[1].x,
                                         to[1].y,
                                         to[1].z,
                                         to[1].w});
                return this;
            }
        };
    }

}
