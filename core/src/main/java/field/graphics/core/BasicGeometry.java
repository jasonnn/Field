package field.graphics.core;

import field.bytecode.protect.Woven;
import field.bytecode.protect.annotations.ConstantContext;
import field.bytecode.protect.annotations.DispatchOverTopology;
import field.bytecode.protect.annotations.InheritWeave;
import field.bytecode.protect.dispatch.Cont;
import field.graphics.core.Base.StandardPass;
import field.graphics.core.Base.iGeometry;
import field.graphics.core.Base.iLongGeometry;
import field.graphics.windowing.FullScreenCanvasSWT;
import field.launch.SystemProperties;
import field.math.abstraction.IInplaceProvider;
import field.math.linalg.CoordinateFrame;
import field.math.linalg.Matrix4;
import field.math.linalg.Vector3;
import field.math.linalg.iCoordinateFrame.iMutable;
import field.namespace.generic.ReflectionTools;
import field.util.TaskQueue;
import org.lwjgl.opengl.GL40;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.*;
import java.util.*;
import java.util.Map.Entry;

import static org.lwjgl.opengl.ARBDrawInstanced.glDrawElementsInstancedARB;
import static org.lwjgl.opengl.ARBInstancedArrays.glVertexAttribDivisorARB;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.GL_LINES_ADJACENCY;

public
class BasicGeometry {

	/*
     *
	 * the path to 30:
	 * 
	 * compute shadow once \u2014 done load as quads not two triangles
	 * (done) load as quads short not quad long ? (maybe not, only if we can
	 * fit the single quaded leaves into 32k) \u2014 done create single
	 * quaded leaves for background at least \u2014 done use parsed
	 * everywhere, because that way we'll have 2 meshes not 30 \u2014 done
	 * develop four quad leaves for parser
	 * 
	 * only run skinning if figure visible
	 */

    /**
     * This is the base class for meshes of all sorts. You will never use
     * this class directly (in fact it is abstract so you can't) but will
     * use its subclasses such as TriMesh. It handles the push and pop of
     * the matrix prior to and after rendering. Subclasses should put their
     * rendering code in doPerformPass(), and their setup code in doSetup()
     */
    @Woven
    public abstract static
    class BasicMesh extends BasicUtilities.OnePassListElement {

        public static Method method_doPerformPass = ReflectionTools.methodOf("doPerformPass", BasicMesh.class);

        private float[] matrix = null;

        private final Matrix4 tmpStorage = new Matrix4();

        private boolean first = true;

        protected boolean isNative = true;

        IInplaceProvider<iMutable> transform;

        CoordinateFrame coordinateFrameNow = new CoordinateFrame();

        public float drawFraction = 1f;

        public
        BasicMesh(IInplaceProvider<iMutable> coordinateFrame) {
            super(StandardPass.render, StandardPass.render);
            this.transform = coordinateFrame;
        }

        public
        BasicMesh(IInplaceProvider<iMutable> coordinateFrame, Base.StandardPass pass) {
            super(pass, pass);
            this.transform = coordinateFrame;
        }

        public
        BasicMesh setDrawFraction(float drawFraction) {
            this.drawFraction = drawFraction;
            return this;
        }

        /**
         * mainly for the purposes of visualization, not in the
         * iGeometry contract
         *
         * @return iInplaceProvider<iCoordinateFrame.iMutable>
         */
        public
        IInplaceProvider<iMutable> getCoordindateFrameProvider() {
            return transform;
        }

        /**
         * do not override this method. Override doPerformPass instead
         */

        @Override
        @InheritWeave
        @DispatchOverTopology(topology = Cont.class)
        @ConstantContext(immediate = false, topology = Base.class)
        public
        void performPass() {

            int id = BasicContextManager.getId(this);
            if (/* first || */(id == BasicContextManager.ID_NOT_FOUND) || (!BasicContextManager.isValid(this))) {
                doSetup();
                first = false;
            }

            pre();
            if (Base.trace)
                ;//System.out.println(" ----------- drawing " + this + " <<" + System.identityHashCode(this) + ">>");
            if (enable) doPerformPass();
            post();
        }

        /**
         * This pops the matrix stack. No need to override this but if
         * you do, be sure to call super.pre() in your method.
         */
        @Override
        public
        void post() {
            super.post();

            CoreHelpers.glActiveTexture(GL_TEXTURE1);
            CoreHelpers.glMatrixMode(GL_TEXTURE);
            CoreHelpers.glPopMatrix();
            CoreHelpers.glActiveTexture(GL_TEXTURE0);
            CoreHelpers.glMatrixMode(GL_TEXTURE);
            CoreHelpers.glPopMatrix();
            CoreHelpers.glMatrixMode(GL_MODELVIEW);
            CoreHelpers.glPopMatrix();

        }

        boolean shaderNeedsUpdating = false;

        public
        BasicMesh setShaderNeedsUpdating(boolean shaderNeedsUpdating) {
            this.shaderNeedsUpdating = shaderNeedsUpdating;
            return this;
        }

        FloatBuffer matrixm = ByteBuffer.allocateDirect(16 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();

        /**
         * This calls super.pre() and then pushes the current matrix on
         * the stack and multiplies it by transform. No need to override
         * this but if you do, be sure to call super.pre() in your
         * method.
         */
        @Override
        public
        void pre() {
            transform.get(coordinateFrameNow);
            CoreHelpers.glPushMatrix();

            matrix = coordinateFrameNow.getMatrix(tmpStorage).getColumnMajor(matrix);
            matrixm.rewind();
            matrixm.put(matrix);
            matrixm.rewind();
            CoreHelpers.glMultMatrix(matrixm);

            CoreHelpers.glActiveTexture(GL_TEXTURE1);
            CoreHelpers.glMatrixMode(GL_TEXTURE);
            CoreHelpers.glPushMatrix();
            CoreHelpers.glMultMatrix(matrixm);
            CoreHelpers.glMatrixMode(GL_MODELVIEW);
            CoreHelpers.glActiveTexture(GL_TEXTURE0);
            CoreHelpers.glMatrixMode(GL_TEXTURE);
            CoreHelpers.glPushMatrix();
            CoreHelpers.glMultMatrix(matrixm);
            CoreHelpers.glMatrixMode(GL_MODELVIEW);

            assert checkNan(matrix) : "you got NAN";
            super.pre();

            if (shaderNeedsUpdating) {
                // ;//System.out.println(" updating current program");
                if (BasicGLSLangProgram.currentProgram != null)
                    BasicGLSLangProgram.currentProgram.updateParameterTaskQueue();
            }
        }

        /**
         * @param frame
         */
        public
        void setCoordindateFrameProvider(IInplaceProvider<iMutable> frame) {
            transform = frame;
        }

        public
        void setNative(boolean isNative) {
            this.isNative = isNative;
        }

        private
        boolean checkNan(float[] f) {
            for (int i = 0; i < f.length; i++) {
                if (Float.isNaN(f[i])) return false;
                if (Float.isInfinite(f[i])) return false;
            }
            return true;
        }

        /**
         * at a minimum subclasses must implement a doPerformPass()
         */

        @DispatchOverTopology(topology = Cont.class)
        protected
        void doPerformPass() {

        }

        /**
         * This gets called once so is a useful place to put set up code
         * for your mesh rendering
         */
        protected
        void doSetup() {
        }

        protected
        void markAsInvalidInAllContexts() {
            BasicContextManager.markAsInvalidInAllContexts(this);
        }

        protected
        void markAsValidInThisContext() {
            BasicContextManager.markAsValidInThisContext(this);
        }

        boolean enable = true;

        public
        void on() {
            enable = true;
        }

        public
        void off() {
            enable = false;
        }

        public
        boolean isOn() {
            return enable;
        }

    }

    @Woven
    public static
    class LineList extends TriangleMesh implements iGeometry {

        float width = 3;

        public
        LineList(IInplaceProvider<iMutable> coordinateFrame) {
            super(coordinateFrame);
        }

        public
        void checkLine() {
            for (int i = 0; i < triangleLimit * 2; i++) {
                int z = triangleBuffer.sBuffer.get(i);

                if (z >= vertexLimit || z < 0) {
                    System.err.println(" bad line -- element <"
                                       + i
                                       + "> which is <"
                                       + z
                                       + "> outside [0, "
                                       + vertexLimit
                                       + ']');
                    triangleBuffer.sBuffer.put(i, (short) 0);
                    assert false;
                }
            }
        }

        /**
         * for Triangles read line segments
         */
        @Override
        public
        iGeometry rebuildTriangle(int numTriangles) {
            triangleBuffer = new VertexBuffer((triangleLimit = triangleCount = numTriangles) * 2, isNative);
            triangleBuffer.elementSize = 2;
            markAsInvalidInAllContexts();
            elementBufferNeedsReconstruction = true;
            return this;
        }

        public
        LineList setWidth(float f) {
            width = f;
            return this;
        }

        /**
         * convience, makes the line segments read 0,1,1,2,2,3,3,4 etc.
         *
         * @see field.graphics.core.BasicGeometry.BasicMesh#doPerformPass()
         */

        public
        void singleLine() {
            int len = triangleBuffer.sBuffer.capacity() / 2;
            int numV = this.numVertex();
            for (int i = 0; i < len; i++) {
                triangleBuffer.sBuffer.put(i * 2, (short) i);
                triangleBuffer.sBuffer.put(i * 2 + 1, (short) ((i + 1) % numV));
            }
            triangleBuffer.dirty();
        }

        public
        void singleLine(int from, int to, int segment) {
            int len = (to - from);
            int numV = this.numVertex();
            // ;//System.out.println(from+"
            // "+to+"
            // "+triangleBuffer.capacity()+"
            // "+segment);
            for (int i = from; i < to - 1; i++) {
                triangleBuffer.sBuffer.put((i - segment) * 2, (short) i);
                triangleBuffer.sBuffer.put((i - segment) * 2 + 1, (short) (i + 1));
                // ;//System.out.println((i-segment)+"
                // "+i+"
                // "+(i+1));
            }
            triangleBuffer.dirty();
        }

        public
        void twoPointLines() {
            int len = triangleBuffer.sBuffer.capacity() / 2;
            for (int i = 0; i < len; i++) {
                triangleBuffer.sBuffer.put(i * 2, (short) (2 * i));
                triangleBuffer.sBuffer.put(i * 2 + 1, (short) (2 * i + 1));
            }
            triangleBuffer.dirty();
        }

        private static
        float clamp(float f) {
            if (f < 0.01f) f = 0.01f;
            if (f > 40) f = 40;
            return f;
        }

        boolean sendAdjacency = false;

        public
        void sendAdjacency() {
            this.sendAdjacency = true;
        }

        @Override
        @InheritWeave
        @DispatchOverTopology(topology = Cont.class)
        protected
        void doPerformPass() {
            assert isNative;

            // vertex(true);
            //
            // checkLine();
            //
            //

            CoreHelpers.glBindVertexArrayAPPLE(0);
            int vertexObjectID = BasicContextManager.getId(this);

            if (triangleLimit * 2 > triangleBuffer.bBuffer.capacity() / triangleBuffer.primitiveSizeof) {
                triangleLimit = triangleBuffer.bBuffer.capacity() / triangleBuffer.primitiveSizeof / 2;
            }
            else if (triangleLimit < 0) {
                triangleLimit = 0;
            }

            clean();
            CoreHelpers.glBindVertexArrayAPPLE(vertexObjectID);

            CoreHelpers.glLineWidth(clamp(width * globalLineScale));

            CoreHelpers.doCameraState();

            // ;//System.out.println(" draw <"+triangleLimit+">
            // lines");
            if (!sendAdjacency) {
                glDrawElements(GL_LINES,
                               ((int) (drawFraction * triangleLimit)) * 2,
                               triangleBuffer.primitiveSizeof == 2 ? GL_UNSIGNED_SHORT : GL_UNSIGNED_INT,
                               0);
            }
            else {
                glDrawElements(GL_LINES_ADJACENCY,
                               ((int) (drawFraction * triangleLimit)) * 2,
                               triangleBuffer.primitiveSizeof == 2 ? GL_UNSIGNED_SHORT : GL_UNSIGNED_INT,
                               0);
            }
            CoreHelpers.glBindVertexArrayAPPLE(0);

        }

        @Override
        protected
        void doSetup() {
            super.doSetup();
        }

    }

    @Woven
    public static
    class LineList_long extends TriangleMesh_long implements iGeometry {

        float width = 3;
        private boolean fakeAa;

        public
        LineList_long(IInplaceProvider<iMutable> coordinateFrame) {
            super(coordinateFrame);
        }

        public
        LineList_long() {
            super(new CoordinateFrame());
        }

        public
        void checkLine() {
            FloatBuffer vv = vertex();
            for (int i = 0; i < triangleLimit * 2; i++) {
                int z = triangleBuffer.iBuffer.get(i);

                vv.position(3 * z);
                Vector3 at = new Vector3(vv);

                if (z >= vertexLimit || z < 0) {
                    System.out.println(z + " " + vertexCount + ' ' + vertexLimit + " (" + at + ')');
                    System.err.println(" bad line -- element <"
                                       + i
                                       + "> which is <"
                                       + z
                                       + "> outside [0, "
                                       + vertexLimit
                                       + ']');
                    triangleBuffer.iBuffer.put(i, 0);
                    assert false;
                }
            }
        }

        @Override
        protected
        void clean() {
            super.clean();
        }

        /**
         * for Triangles read line segments
         */
        @Override
        public
        iGeometry rebuildTriangle(int numTriangles) {
            triangleBuffer = new VertexBuffer((triangleLimit = triangleCount = numTriangles) * 2, true, isNative);
            triangleBuffer.elementSize = 2;
            markAsInvalidInAllContexts();
            elementBufferNeedsReconstruction = true;
            return this;
        }

        public
        LineList_long setWidth(float f) {
            width = f;
            return this;
        }

        public
        void doFakeAntialias(boolean fakeAa) {
            this.fakeAa = fakeAa;
        }

        /**
         * convience, makes the line segments read 0,1,1,2,2,3,3,4 etc.
         *
         * @see field.graphics.core.BasicGeometry.BasicMesh#doPerformPass()
         */

        public
        void singleLine() {
            int len = triangleBuffer.sBuffer.capacity() / 2;
            int numV = this.numVertex();
            for (int i = 0; i < len; i++) {
                triangleBuffer.iBuffer.put(i * 2, i);
                triangleBuffer.iBuffer.put(i * 2 + 1, ((i + 1) % numV));
            }
            triangleBuffer.dirty();
        }

        public
        void singleLine(int from, int to, int segment) {
            int len = (to - from);
            int numV = this.numVertex();
            for (int i = from; i < to - 1; i++) {
                triangleBuffer.iBuffer.put((i - segment) * 2, i);
                triangleBuffer.iBuffer.put((i - segment) * 2 + 1, (i + 1));
            }
            triangleBuffer.dirty();
        }

        public
        void twoPointLines() {
            int len = triangleBuffer.sBuffer.capacity() / 2;
            for (int i = 0; i < len; i++) {
                triangleBuffer.iBuffer.put(i * 2, (2 * i));
                triangleBuffer.iBuffer.put(i * 2 + 1, (2 * i + 1));
            }
            triangleBuffer.dirty();
        }

        private static
        float clamp(float f) {
            if (f < 0.0001f) f = 0.0001f;
            if (f > 140) f = 140;
            return f;
        }

        @Override
        @InheritWeave
        @DispatchOverTopology(topology = Cont.class)
        protected
        void doPerformPass() {
            assert isNative;

//			;//System.out.println(" -- drawing :" + triangleLimit);
            if (triangleLimit == 0) {
//				;//System.out.println(" (skipping)");
                return;
            }

            CoreHelpers.glBindVertexArrayAPPLE(0);

            int vertexObjectID = BasicContextManager.getId(this);

            if (triangleLimit * 2 > triangleBuffer.bBuffer.capacity() / triangleBuffer.primitiveSizeof) {
                triangleLimit = triangleBuffer.bBuffer.capacity() / triangleBuffer.primitiveSizeof / 2;
            }
            else if (triangleLimit < 0) {
                triangleLimit = 0;
            }

            if (numInstances == 0) cleanNew();
            else clean();

            CoreHelpers.glBindVertexArrayAPPLE(vertexObjectID);
            float cw = clamp(width * globalLineScale);
            CoreHelpers.glLineWidth(cw);

//			 checkLine();

            // glUseProgramObjectARB(0);
            CoreHelpers.doCameraState();

            if (numInstances == 0) {
                glDrawElements(sendAdjacency ? GL_LINES_ADJACENCY : GL_LINES,
                               ((int) (drawFraction * triangleLimit)) * 2,
                               triangleBuffer.primitiveSizeof == 2 ? GL_UNSIGNED_SHORT : GL_UNSIGNED_INT,
                               0);
                if (fakeAa) {
                    CoreHelpers.glLineWidth(cw * 2);
                    glDrawElements(sendAdjacency ? GL_LINES_ADJACENCY : GL_LINES,
                                   ((int) (drawFraction * triangleLimit)) * 2,
                                   triangleBuffer.primitiveSizeof == 2 ? GL_UNSIGNED_SHORT : GL_UNSIGNED_INT,
                                   0);
                    CoreHelpers.glLineWidth(cw);
                }

            }
            else {
                if (subInstances > 0) glDrawElementsInstancedARB(sendAdjacency ? GL_LINES_ADJACENCY : GL_LINES,
                                                                 ((int) (drawFraction * triangleLimit)) * 2,
                                                                 triangleBuffer.primitiveSizeof == 2
                                                                 ? GL_UNSIGNED_SHORT
                                                                 : GL_UNSIGNED_INT,
                                                                 0,
                                                                 subInstances);
            }
            // glDrawElements(GL_LINES, Math.min(2,
            // triangleLimit * 2),
            // triangleBuffer.primitiveSizeof == 2 ?
            // GL_UNSIGNED_SHORT : GL_UNSIGNED_INT,
            // 0);
            CoreHelpers.glBindVertexArrayAPPLE(0);

//			;//System.out.println(" -- drawing :" + triangleLimit + " complete ");

        }

        boolean sendAdjacency = false;

        public
        void sendAdjacency() {
            this.sendAdjacency = true;
        }

        public
        boolean doesSendAdjecency() {
            return sendAdjacency;
        }
    }

    @Woven
    public static
    class QuadMesh extends TriangleMesh implements iGeometry {

        boolean ff = true;

        public
        QuadMesh(IInplaceProvider<iMutable> coordinateFrame) {
            super(coordinateFrame);
            if (CoreHelpers.isCore)
                throw new IllegalStateException(" quads are not supported on OpenGL 3.2 core, convert to triangles ");
        }

        public
        QuadMesh(StandardPass preRender) {
            super(preRender);
            if (CoreHelpers.isCore)
                throw new IllegalStateException(" quads are not supported on OpenGL 3.2 core, convert to triangles ");
        }

        /**
         * for Triangles read line segments
         */
        @Override
        public
        iGeometry rebuildTriangle(int numTriangles) {
            triangleBuffer = new VertexBuffer((triangleLimit = triangleCount = numTriangles) * 4, isNative);
            triangleBuffer.elementSize = 4;
            markAsInvalidInAllContexts();
            elementBufferNeedsReconstruction = true;
            return this;
        }

        @Override
        public
        FloatBuffer aux(int auxId, int elementSize) {
            FloatBuffer r = super.aux(auxId, elementSize);

            new Exception().printStackTrace();

            return r;
        }

        // int[] query = new int[1];
        // int[] available = new int[1];
        // int[] sampleCount = new int[1];
        //
        // @Override
        // protected void doSetup() {
        // super.doSetup();
        // glGenQueries(1, query, 0);
        //
        // }

        @Override
        @InheritWeave
        @DispatchOverTopology(topology = Cont.class)
        protected
        void doPerformPass() {
            assert isNative;
            CoreHelpers.glBindVertexArrayAPPLE(0);
            int vertexObjectID = BasicContextManager.getId(this);

            if (triangleLimit * 4 > triangleBuffer.bBuffer.capacity() / triangleBuffer.primitiveSizeof) {
                triangleLimit = triangleBuffer.bBuffer.capacity() / triangleBuffer.primitiveSizeof / 4;
            }
            else if (triangleLimit < 0) {
                triangleLimit = 0;
            }

            // if (!ff) {
            // glGetQueryObjectuiv(query[0],
            // GL_QUERY_RESULT_AVAILABLE_ARB, available, 0);
            // glGetQueryObjectuivARB(query[0],
            // GL_QUERY_RESULT_ARB, sampleCount, 0);
            // }
            // ff = false;
            //
            // glBeginQuery(GL_SAMPLES_PASSED, query[0]);

            clean();
            CoreHelpers.glBindVertexArrayAPPLE(vertexObjectID);

            // glDisable(GL_POLYGON_SMOOTH);
            // glDisable(GL_LINE_SMOOTH);
            // glDisable(GL_DEPTH_TEST);
            // glDisable(GL_CULL_FACE);
            CoreHelpers.doCameraState();

            glDrawElements(GL_QUADS, ((int) (drawFraction * triangleLimit)) * 4, GL_UNSIGNED_SHORT, 0);
            CoreHelpers.glBindVertexArrayAPPLE(0);

            // glEndQuery(GL_SAMPLES_PASSED);

        }

    }

    public static
    class Instance extends BasicUtilities.OnePassListElement {

        CoordinateFrame frame = new CoordinateFrame();
        private final List<BasicMesh> m;

        public
        Instance(BasicMesh m) {
            super(StandardPass.render, StandardPass.render);
            this.m = new ArrayList<BasicMesh>();
            this.m.add(m);
        }

        public
        Instance() {
            super(StandardPass.render, StandardPass.render);
            this.m = new ArrayList<BasicMesh>();
        }

        public
        Instance add(BasicMesh m) {
            this.m.add(m);
            return this;
        }

        public boolean off = false;

        private float[] matrix = null;

        private final Matrix4 tmpStorage = new Matrix4();
        FloatBuffer matrixm = ByteBuffer.allocateDirect(16 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();

        @Override
        public
        void performPass() {

            if (off) {
                visible = false;
                return;
            }
            // ;//System.out.println(" inside enabled instance <"+m+">");

            CoreHelpers.glPushMatrix();

            matrix = frame.getMatrix(tmpStorage).getColumnMajor(matrix);
            matrixm.rewind();
            matrixm.put(matrix);
            matrixm.rewind();
            CoreHelpers.glMultMatrix(matrixm);

            pre();
            for (BasicMesh mm : m)
                mm.performPass();
            post();

            CoreHelpers.glPopMatrix();

        }

        public
        void setFrame(CoordinateFrame frame) {
            this.frame = frame;
        }

        public
        List<BasicMesh> getMeshes() {
            return m;
        }

        public
        CoordinateFrame getFrame() {
            return frame;
        }

        public
        void addAll(List<BasicMesh> g) {
            for (BasicMesh m : g) {
                add(m);
            }
        }

        public
        void disableInstance() {
            off = true;
        }

        public
        void enableInstance() {
            off = false;
        }

    }

    /**
     * an interface for geometry
     */

    @Woven
    public static
    class TriangleMesh extends BasicMesh implements iGeometry {

        public Map<Integer, VertexBuffer> auxBuffers = new LinkedHashMap<Integer, VertexBuffer>();

        protected VertexBuffer vertexBuffer = null;

        protected VertexBuffer triangleBuffer = null;

        protected int vertexCount = 0;

        protected int triangleCount = 0;

        protected int vertexLimit = 0;

        protected int triangleLimit = 0;

        protected int vertexStride = 3;

        protected int[] attributeBuffers = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};

        protected int[] elementBuffer = {-1};

        protected boolean[] needsReconstruction =
                {true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true};

        protected boolean elementBufferNeedsReconstruction = true;

        boolean deallocated = false;

        public
        TriangleMesh() {
            super(new BasicUtilities.Position());
        }

        public
        TriangleMesh(Base.StandardPass pass) {
            super(new BasicUtilities.Position(), pass);
        }

        public
        TriangleMesh(IInplaceProvider<iMutable> coordinateFrame) {
            super(coordinateFrame);
        }

        /**
         * this will lazily create an aux buffer with id 'auxID', this
         * will also include normal and texture coordinate info , refer
         */
        public
        FloatBuffer aux(int auxId, int elementSize) {
            if (auxId == 0) return vertexBuffer.getBuffer(true);

            VertexBuffer b = auxBuffers.get(auxId);
            if ((b == null) && (elementSize > 0)) {

                if (divisors[auxId] != 0) {
                    auxBuffers.put(auxId,
                                   b = new VertexBuffer(auxId, numInstances / divisors[auxId], elementSize, isNative));
                    markAsInvalidInAllContexts();
                }
                else {
                    auxBuffers.put(auxId, b = new VertexBuffer(auxId, vertexCount, elementSize, isNative));
                    markAsInvalidInAllContexts();
                }
            }
            else if (b == null) return null;
            FloatBuffer bb = b.getBuffer(true);

            if (divisors[auxId] == 0) bb.limit(b.elementSize * vertexLimit);

            return bb;
        }

        public
        Map auxBuffers() {
            HashMap r = new HashMap(auxBuffers);
            r.put(0, vertexBuffer);
            r.put(-1, triangleBuffer);
            return r;
        }

        public
        void cleanVertex(Object wrt) {
            vertexBuffer.clean(wrt);
        }

        public
        void forceClean() {
            vertexBuffer.forceClean();
            triangleBuffer.forceClean();
            for (VertexBuffer v : auxBuffers.values()) {
                v.forceClean();
            }
        }

        public
        void deallocate(TaskQueue in) {

            in.new Task() {

                @Override
                public
                void run() {
                    if (vertexBuffer != null && auxBuffers != null)
                    // if (BasicContextManager.getId(this)
                    // != BasicContextManager.ID_NOT_FOUND
                    // && BasicContextManager.isValid(this))
                    {
                        // gl =
                        // BasicContextManager.getGl();
                        for (int i = 0; i < attributeBuffers.length; i++) {
                            if (attributeBuffers[i] != -1) glDeleteBuffers(attributeBuffers[i]);
                        }
                        glDeleteBuffers(elementBuffer[0]);
                        BasicContextManager.putId(this, BasicContextManager.ID_NOT_FOUND);
                        auxBuffers.clear();

                    }
                    vertexBuffer = null;
                    triangleBuffer = null;
                    // else
                    // {
                    // }
                    deallocated = true;

                    BasicContextManager.delete(this);
                }

            };
        }

        public
        IInplaceProvider<iMutable> getCoordinateProvider() {
            return transform;
        }

        public
        boolean hasAux(int auxId) {
            return auxBuffers.get(auxId) != null;
        }

        public
        boolean isVertexDirty(Object wrt) {
            return vertexBuffer.isDirty(wrt);
        }

        public
        int numTriangle() {
            return triangleLimit;
        }

        public
        int numVertex() {
            return vertexLimit;
        }

        /**
         * for initializing, and reinitializing geometry, typicaly,
         * these just return 'this'
         * <p/>
         * todo: release vertex object
         */
        public
        iGeometry rebuildTriangle(int numTriangles) {
            triangleBuffer = new VertexBuffer((triangleLimit = triangleCount = numTriangles) * 3, isNative);
            triangleBuffer.elementSize = 3;
            markAsInvalidInAllContexts();
            elementBufferNeedsReconstruction = true;
            return this;
        }

        /**
         * this call will typically throw out all the normal information
         * and aux information
         */
        public
        iGeometry rebuildVertex(int numVertex) {

            if (vertexBuffer != null) vertexBuffer.free();
            vertexBuffer =
                    new VertexBuffer(Base.vertex_id, (vertexLimit = vertexCount = numVertex), vertexStride, isNative);

            Iterator it = auxBuffers.entrySet().iterator();
            while (it.hasNext()) {
                // Map.Entry<Integer,
                // VertexBuffer>
                // entry =
                // it.next();
                Map.Entry entry = (Map.Entry) it.next();
                VertexBuffer buffer = (VertexBuffer) entry.getValue();
                buffer.free();
            }
            auxBuffers = new LinkedHashMap();

            // now we need to rebuild
            // openGL's state next time
            // through in the doPerformPass
            // method
            markAsInvalidInAllContexts();

            for (int i = 0; i < 16; i++)
                needsReconstruction[i] = true;

            return this;
        }

        public
        TriangleMesh setTriangleLimit(int triangleLimit) {
            this.triangleLimit = triangleLimit;
            return this;
        }

        public
        TriangleMesh setVertexLimit(int vertexLimit) {
            this.vertexLimit = vertexLimit;
            return this;
        }

        /**
         * purely for advanced use. If you have data that has a stride
         * != 3 that you want to use for the vertex position
         */
        public
        void setVertexStride(int i) {
            vertexStride = i;
        }

        // TODO, aparently glMapBuffer calls are slow, and I should
        // beusing glBufferData instead

        @Override
        public
        String toString() {
            return "triangleMesh(" + this.getClass() + ") with <" + numVertex() + "> <" + numTriangle() + '>';
        }

        public
        ShortBuffer triangle() {
            if (triangleBuffer == null) rebuildTriangle(0);
            return triangleBuffer.getShortBuffer(true);
        }

        /**
         * for manipulating geometry
         */
        public
        FloatBuffer vertex() {

            // if
            // (!vertexBuffer.isDirty(BasicContextManager.getCurrentContext()))
            // {
            // System.err.println(" mesh vertex data
            // <"+numVertex()+"> invalid at:");
            // new Exception().printStackTrace();
            // }
            //
            if (vertexBuffer == null) rebuildVertex(0);

            // ;//System.out.println(" vertex buffer <"+this+"> dirty at <"+(vertexBuffer.modCount+1)+">");

            FloatBuffer f = vertexBuffer.getBuffer(true);
            f.limit(vertexStride * vertexLimit);
            return f;
        }

        public
        FloatBuffer vertex(boolean d) {

            // if (!vertexBuffer.isDirty(this))
            // {
            // System.err.println(" mesh vertex data
            // <"+numVertex()+"> invalid at:");
            // new Exception().printStackTrace();
            // }
            //

            if (vertexBuffer == null) rebuildVertex(0);

            FloatBuffer f = vertexBuffer.getBuffer(d);
            f.limit(vertexStride * vertexLimit);
            return f;
        }

        protected
        void checkTriangle() {
            for (int i = 0; i < triangleLimit * 3; i++) {
                int z = triangleBuffer.sBuffer.get(i);
                if (z >= vertexLimit || z < 0) {
                    System.err.println(" bad triangle");
                    triangleBuffer.sBuffer.put(i, (short) 0);
                }
            }
        }

        protected
        void clean() {

            // if (gl == null)
            // assert !deallocated;

            if (disableUpload) return;

            Object context = BasicContextManager.getCurrentContext();
            // System.err.println(" cleaning <"+this.getClass()+"> vertex <"+vertexLimit+">");

            if (vertexBuffer.isDirty(context) && vertexLimit > 0) {
                glBindBuffer(GL_ARRAY_BUFFER, attributeBuffers[0]);
                glBufferData(GL_ARRAY_BUFFER, vertexBuffer.buffer.capacity() * 4, type);

                // ;//System.out.println("A");
                ByteBuffer buffer = glMapBuffer(GL_ARRAY_BUFFER, GL_WRITE_ONLY, null);
                assert vertexBuffer.bBuffer.capacity() == buffer.capacity() : vertexBuffer.bBuffer + " " + buffer;

                // if (this instanceof QuadMesh)

                // if (buffer == null) {
                // for (int i = 0; i <
                // needsReconstruction.length; i++)
                // needsReconstruction[i] = true;
                // elementBufferNeedsReconstruction = true;
                // doSetup();
                //
                // buffer = glMapBuffer(GL_ARRAY_BUFFER,
                // GL_WRITE_ONLY);
                // ;//System.out.println(" lost buffer, recovered :"
                // + buffer);
                // vertexBuffer.dirty();
                // triangleBuffer.dirty();
                // for (VertexBuffer v : auxBuffers.values())
                // v.dirty();
                // }

                buffer.rewind();
                vertexBuffer.bBuffer.rewind();

                vertexBuffer.bBuffer.limit(4 * vertexStride * vertexLimit);

                buffer.put(vertexBuffer.bBuffer);
                vertexBuffer.clean(context);
                glUnmapBuffer(GL_ARRAY_BUFFER);

            }
            else {
            }

            if (triangleBuffer.isDirty(context) && triangleLimit > 0) {
                glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, elementBuffer[0]);
                glBufferData(GL_ELEMENT_ARRAY_BUFFER,
                             triangleBuffer.primitiveSizeof * triangleCount * triangleBuffer.elementSize,
                             type);
                // LWJGL -- null is suspicious
                // ;//System.out.println("B");
                ByteBuffer buffer =
                        glMapBuffer(GL_ELEMENT_ARRAY_BUFFER, GL_WRITE_ONLY, null).order(ByteOrder.nativeOrder());
                assert triangleBuffer.bBuffer.capacity() == buffer.capacity() : triangleBuffer.bBuffer + " " + buffer;

                // if (this instanceof QuadMesh)
                // System.err.println(" cleaning triangle<"+triangleLimit+">");
                // System.err.println(" cleaning <"+this.getClass()+"> triangle <"+triangleLimit+">");

                buffer.rewind();
                triangleBuffer.bBuffer.rewind();

                triangleBuffer.bBuffer.limit(triangleBuffer.primitiveSizeof
                                             * triangleLimit
                                             * triangleBuffer.elementSize);

                buffer.put(triangleBuffer.bBuffer);

                buffer.rewind();
                // ShortBuffer q = buffer.asShortBuffer();
                // while(q.hasRemaining())
                // {
                // }

                triangleBuffer.clean(context);
                glUnmapBuffer(GL_ELEMENT_ARRAY_BUFFER);
            }
            else {
            }

            if (vertexLimit > 0) {
                Iterator<Entry<Integer, VertexBuffer>> aux = auxBuffers.entrySet().iterator();
                while (aux.hasNext()) {
                    Entry<Integer, VertexBuffer> e = aux.next();

                    VertexBuffer vbuffer = e.getValue();
                    int aid = e.getKey();

                    if (attributeBuffers[aid] == -1) {
                        //System.out.println(" warning <"+aid+" aux is being cleaned without having been setup");
                        continue;
                    }

                    if (vbuffer.isDirty(context)) {
                        glBindBuffer(GL_ARRAY_BUFFER, attributeBuffers[aid]);
                        if (divisors[aid] == 0) {
                            glBufferData(GL_ARRAY_BUFFER, vertexCount * 4 * vbuffer.elementSize, type);
                        }
                        else {

                            // ;//System.out.println(" cleaning per instance aux <"
                            // + numInstances /
                            // divisors[aid] * 4 *
                            // vbuffer.elementSize +
                            // "> (sub = " +
                            // subInstances + ")");
                            // ;//System.out.println("    num instance is :"
                            // + numInstances + " "
                            // + divisors[aid] + " "
                            // +
                            // vbuffer.elementSize);

                            if (subInstances == 0) {

                                // ;//System.out.println(" -- about to unmap buffer because subInstances is zero");

                                // glUnmapBuffer(GL_ARRAY_BUFFER);
                                continue;
                            }

                            glBufferData(GL_ARRAY_BUFFER, subInstances / divisors[aid] * 4 * vbuffer.elementSize, type);
                        }
                        // ;//System.out.println("C");
                        ByteBuffer buffer = glMapBuffer(GL_ARRAY_BUFFER, GL_WRITE_ONLY, null);
                        if (buffer != null) {
                            assert vbuffer.bBuffer.capacity() == buffer.capacity() : vbuffer.bBuffer
                                                                                     + " "
                                                                                     + buffer
                                                                                     + ' '
                                                                                     + vbuffer.elementSize
                                                                                     + "   "
                                                                                     + aid;

                            // if (this instanceof
                            // QuadMesh)
                            // System.err.println(" cleaning aux<"+e+" / "+vertexLimit+">");

                            buffer.rewind();
                            vbuffer.bBuffer.rewind();

                            if (divisors[aid] == 0) {
                                vbuffer.bBuffer.limit(vertexLimit * 4 * vbuffer.elementSize);
                            }
                            else {
                                vbuffer.bBuffer.limit(subInstances / divisors[aid] * 4 * vbuffer.elementSize);
                            }

                            buffer.put(vbuffer.bBuffer);
                        }
                        vbuffer.clean(context);
                        glUnmapBuffer(GL_ARRAY_BUFFER);
                    }
                }
            }

        }

        protected
        void cleanNew() {
            assert !deallocated;

            Object context = BasicContextManager.getCurrentContext();
            if (vertexBuffer.isDirty(context) && vertexLimit > 0) {
                glBindBuffer(GL_ARRAY_BUFFER, attributeBuffers[0]);

                //System.out.println(" sending vertex <" + vertexStride + " " + vertexLimit + ">");

                vertexBuffer.bBuffer.rewind();

                vertexBuffer.bBuffer.limit(4 * vertexStride * vertexLimit);
                //System.out.println(glGetError() == 0);
                glBufferSubData(GL_ARRAY_BUFFER, 0, vertexBuffer.bBuffer);
                //System.out.println(glGetError() == 0);

                vertexBuffer.clean(context);
            }

            if (triangleBuffer.isDirty(context) && triangleLimit > 0) {
                glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, elementBuffer[0]);

                triangleBuffer.bBuffer.rewind();

                triangleBuffer.bBuffer.limit(triangleBuffer.primitiveSizeof
                                             * triangleBuffer.elementSize
                                             * triangleLimit);
                glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, triangleBuffer.bBuffer);
                triangleBuffer.clean(context);
            }

            if (vertexLimit > 0) {
                Iterator<Entry<Integer, VertexBuffer>> aux = auxBuffers.entrySet().iterator();
                while (aux.hasNext()) {
                    Entry<Integer, VertexBuffer> e = aux.next();

                    VertexBuffer vbuffer = e.getValue();
                    int aid = e.getKey();
                    if (vbuffer.isDirty(context)) {
                        glBindBuffer(GL_ARRAY_BUFFER, attributeBuffers[aid]);
                        vbuffer.bBuffer.rewind();
                        vbuffer.bBuffer.limit(vertexLimit * 4 * vbuffer.elementSize);
                        glBufferSubData(GL_ARRAY_BUFFER, 0, vbuffer.bBuffer);
                        vbuffer.clean(context);
                    }
                }
            }

        }

        boolean disableDraw = false;

        int numInstances = 0;
        int subInstances = 0;

        public
        TriangleMesh setNumInstances(int subInstances) {

            if (subInstances > numInstances) {
                resizeInstances(subInstances);
            }

            this.subInstances = subInstances;

            // for (int i = 0; i < divisors.length; i++)
            // if (divisors[i] != 0) {
            // needsReconstruction[i] = true;
            // markAsInvalidInAllContexts();
            // VertexBuffer m = auxBuffers.remove(i);
            // if (m != null) {
            // m.free();
            // }
            // }

            return this;
        }

        public
        TriangleMesh setMaxInstances(int numInstances) {
            if (numInstances != this.numInstances) resizeInstances(numInstances);
            return this;
        }

        protected
        void resizeInstances(int numInstances) {

            this.numInstances = numInstances;

            for (int i = 0; i < divisors.length; i++)
                if (divisors[i] != 0) {
                    needsReconstruction[i] = true;
                    markAsInvalidInAllContexts();
                    VertexBuffer m = auxBuffers.remove(i);
                    if (m != null) {
                        m.free();
                    }
                }

        }

        boolean drawsAsPatches = false;

        public
        TriangleMesh setDrawsAsPatches(boolean drawsAsPatches) {
            this.drawsAsPatches = drawsAsPatches;
            return this;
        }

        /**
         * This method is responsible for doing the actual drawing and
         * is called by performPass() of BasicMesh. Because this is
         * called from the performPass() method of BasicMesh,
         * BasicContextIdManager.get() will always return a valid id.
         */
        @Override
        @InheritWeave
        @DispatchOverTopology(topology = Cont.class)
        protected
        void doPerformPass() {
            assert isNative;
            // this.gl = BasicContextManager.getGl();
            // this.glu = BasicContextManager.getGlu();

            assert !deallocated;
//			System.out.println(" --- drawing trangle mesh :"+triangleLimit+" "+BasicGLSLangProgram.currentProgram);
            if (triangleLimit == 0) return;

            CoreHelpers.glBindVertexArrayAPPLE(0);

            int vertexObjectID = BasicContextManager.getId(this);

            if (triangleLimit * 3 > triangleBuffer.bBuffer.capacity() / triangleBuffer.primitiveSizeof) {
                triangleLimit = triangleBuffer.bBuffer.capacity() / triangleBuffer.primitiveSizeof / 3;
            }
            else if (triangleLimit < 0) {
                triangleLimit = 0;
            }

            if (numInstances == 0) cleanNew();
            else clean();

            if (triangleLimit == 0) return;

            CoreHelpers.glBindVertexArrayAPPLE(vertexObjectID);

            // if (vertexLimit==9)
            // {
            // System.err.println(" drawing <" + triangleLimit +
            // "> triangle <" + vertexLimit + "> over <" +
            // vertexBuffer.bBuffer + "> from <" + this.getClass() +
            // "> is<" + System.identityHashCode(this) + "> <" +
            // BasicCamera.currentCamera + " " +
            // (BasicCamera.currentCamera == null ? null :
            // BasicCamera.currentCamera.getClass()));
            // }

            // ;//System.out.println(" drawing triangle");

            // if (insideDoubleFloatFrameBuffer)
            // glBlendFunc(GL_SRC_ALPHA_SATURATE, GL_ONE);

            // checkTriangle();
            // ;//System.out.println(" -- triangles about to docamerastate ");
            CoreHelpers.doCameraState();

            if (!disableDraw) {

                if (numInstances == 0) {
                    FullScreenCanvasSWT.triangleCount += triangleLimit;
                    FullScreenCanvasSWT.vertexCount += vertexLimit;

                    if (drawsAsPatches) {
                        GL40.glPatchParameteri(GL40.GL_PATCH_VERTICES, 3);
                        glDrawElements(GL40.GL_PATCHES,
                                       ((int) (drawFraction * triangleLimit)) * 3,
                                       triangleBuffer.primitiveSizeof == 2 ? GL_UNSIGNED_SHORT : GL_UNSIGNED_INT,
                                       0);
                    }
                    else {
                        glDrawElements(GL_TRIANGLES,
                                       ((int) (drawFraction * triangleLimit)) * 3,
                                       triangleBuffer.primitiveSizeof == 2 ? GL_UNSIGNED_SHORT : GL_UNSIGNED_INT,
                                       0);
                    }
                }
                else if (subInstances > 0) {

                    FullScreenCanvasSWT.triangleCount += triangleLimit * subInstances;
                    FullScreenCanvasSWT.vertexCount += vertexLimit * subInstances;
                    if (drawsAsPatches) {
                        GL40.glPatchParameteri(GL40.GL_PATCH_VERTICES, 3);
                        glDrawElementsInstancedARB(GL40.GL_PATCHES,
                                                   ((int) (drawFraction * triangleLimit)) * 3,
                                                   triangleBuffer.primitiveSizeof == 2
                                                   ? GL_UNSIGNED_SHORT
                                                   : GL_UNSIGNED_INT,
                                                   0,
                                                   subInstances);
                    }
                    else {
                        glDrawElementsInstancedARB(GL_TRIANGLES,
                                                   ((int) (drawFraction * triangleLimit)) * 3,
                                                   triangleBuffer.primitiveSizeof == 2
                                                   ? GL_UNSIGNED_SHORT
                                                   : GL_UNSIGNED_INT,
                                                   0,
                                                   subInstances);
                    }

                }
            }

            // if (insideDoubleFloatFrameBuffer) {
            // glColorMask(false, false, false, true);
            // glClearColor(0, 0, 0, 0);
            // glClear(GL_COLOR_BUFFER_BIT);
            // glColorMask(true, true, true, true);
            // }

            // glBegin(GL_TRIANGLES);
            // glVertex3f( -10, -10, 0);
            // glVertex3f(10, 10, 0);
            // glVertex3f(10, -10, 0);
            // glVertex3f(-10, -10, 0);
            // glVertex3f(-10,10,0);
            // glVertex3f(10, 10, 0);
            // glEnd();

            CoreHelpers.glBindVertexArrayAPPLE(0);

        }

        int type = GL_STATIC_DRAW;

        int[] divisors = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        public
        TriangleMesh setDivisor(int aux, int div) {
            if (divisors[aux] == div) return this;

            needsReconstruction[aux] = true;
            divisors[aux] = div;
            markAsInvalidInAllContexts();

            VertexBuffer a = auxBuffers.remove(aux);
            if (a != null) a.free();

            return this;
        }

        /**
         * This creates the vertex object
         */
        @Override
        protected
        void doSetup() {
            assert isNative;

            // this.gl = BasicContextManager.getGl();
            // this.glu = BasicContextManager.getGlu();
            // creating vertex object
            int vertexObjectID = BasicContextManager.getId(this);
            if (vertexObjectID == BasicContextManager.ID_NOT_FOUND) {
                int[] id = new int[1];

                id[0] = CoreHelpers.glGenVertexArraysApple();

                vertexObjectID = id[0];
                BasicContextManager.putId(this, vertexObjectID);
            }

            CoreHelpers.glBindVertexArrayAPPLE(vertexObjectID);

            if (attributeBuffers[0] == -1 || needsReconstruction[0]) {
                if (attributeBuffers[0] == -1) attributeBuffers[0] = glGenBuffers();

                glBindBuffer(GL_ARRAY_BUFFER, attributeBuffers[0]);
                glBufferData(GL_ARRAY_BUFFER, vertexBuffer.buffer.capacity() * 4, type);
                if (useAttr0ForVertexPosition) {

                    // ;//System.out.println(" position is ... ? "+GL20.glGetAttribLocation(BasicGLSLangProgram.currentProgram.getShader(),
                    // "position"));

                    glEnableVertexAttribArray(0);
                    glVertexAttribPointer(0, vertexStride, GL_FLOAT, false, 0, 0);
                }

                needsReconstruction[0] = false;
            }

            if (elementBuffer[0] == -1 || elementBufferNeedsReconstruction) {
                if (elementBuffer[0] == -1) elementBuffer[0] = glGenBuffers();
                glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, elementBuffer[0]);
                glBufferData(GL_ELEMENT_ARRAY_BUFFER, triangleBuffer.bBuffer.capacity(), type);

                elementBufferNeedsReconstruction = false;
            }

            Iterator<Entry<Integer, VertexBuffer>> aux = auxBuffers.entrySet().iterator();
            while (aux.hasNext()) {
                Entry<Integer, VertexBuffer> e = aux.next();

                VertexBuffer buffer = e.getValue();
                int aid = e.getKey();

                if (attributeBuffers[aid] == -1 || needsReconstruction[aid]) {
                    if (attributeBuffers[aid] == -1) attributeBuffers[aid] = glGenBuffers();

                    glBindBuffer(GL_ARRAY_BUFFER, attributeBuffers[aid]);

                    if (divisors[aid] != 0) {
                        glVertexAttribDivisorARB(aid, divisors[aid]);
                        glBufferData(GL_ARRAY_BUFFER,
                                     (numInstances / divisors[aid]) * 4 * buffer.getElementSize(),
                                     type);
                    }
                    else glBufferData(GL_ARRAY_BUFFER,
                                      (vertexBuffer.buffer.capacity() / vertexStride) * 4 * buffer.getElementSize(),
                                      type);

                    glEnableVertexAttribArray(aid);
                    glVertexAttribPointer(aid, buffer.getElementSize(), GL_FLOAT, false, 0, 0);

                    needsReconstruction[aid] = false;

                    assert vertexBuffer.buffer.capacity() / vertexStride
                           == buffer.buffer.capacity() / buffer.getElementSize();

                }
                else {
                }
            }

            // for (int i = 0; i < 16; i++)
            // if (attributeBuffers[i] ==
            // -1)
            // glDisableVertexAttribArray(i);

            // BasicGLSLangProgram.currentProgram.debugPrintUniforms();

            aux = auxBuffers.entrySet().iterator();
            HashSet<Integer> notSeen = new LinkedHashSet<Integer>();
            notSeen.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15));
            while (aux.hasNext()) {

                Entry<Integer, VertexBuffer> e = aux.next();

                VertexBuffer buffer = e.getValue();
                int aid = e.getKey();

                notSeen.remove(aid);
            }

            for (Integer i : notSeen) {
                glDisableVertexAttribArray(i);
            }

            if (useAttr0ForVertexPosition) {
            }
            else {
                glBindBuffer(GL_ARRAY_BUFFER, attributeBuffers[0]);
                glVertexPointer(vertexStride, GL_FLOAT, 0, 0);
                glEnableClientState(GL_VERTEX_ARRAY);
                glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, elementBuffer[0]);
            }

            markAsValidInThisContext();

        }

        // use 0 for verex,
        public
        int getOpenGLBufferName(int aux) {
            return attributeBuffers[aux];
        }
    }

    static public
    class TriangleMesh_long extends TriangleMesh implements iLongGeometry {

        public
        TriangleMesh_long() {
            super();
        }

        public
        TriangleMesh_long(IInplaceProvider<iMutable> coordinateFrame) {
            super(coordinateFrame);
        }

        public
        TriangleMesh_long(StandardPass pass) {
            super(pass);
        }

        public
        IntBuffer longTriangle() {

            if (triangleBuffer == null) rebuildTriangle(0);
            IntBuffer t = triangleBuffer.getInBuffer(true);
            t.limit(triangleBuffer.elementSize * triangleLimit);
            return t;
        }

        @Override
        public
        iGeometry rebuildTriangle(int numTriangles) {
            triangleBuffer = new VertexBuffer((triangleLimit = triangleCount = numTriangles) * 3, true, isNative);
            triangleBuffer.elementSize = 3;
            markAsInvalidInAllContexts();
            elementBufferNeedsReconstruction = true;
            return this;
        }

        @Override
        public
        ShortBuffer triangle() {
            assert false : "triangle called on long triangle mesh";
            return null;
        }

        @Override
        protected
        void checkTriangle() {
            IntBuffer x = triangleBuffer.getInBuffer(true);
            for (int i = 0; i < triangleLimit * 3; i++) {
                int z = x.get(i);
                // ;//System.out.println(i+" "+z);
                if (z >= vertexLimit || z < 0) {
                    System.err.println(" bad triangle");
                    x.put(i, 0);
                }
            }
        }

        @Override
        public
        String toString() {
            return super.toString() + '@' + System.identityHashCode(this);
        }
    }

    static public
    class QuadMesh_long extends TriangleMesh implements iGeometry, iLongGeometry {

        boolean ff = true;

        public
        QuadMesh_long(IInplaceProvider<iMutable> coordinateFrame) {
            super(coordinateFrame);

            if (CoreHelpers.isCore)
                throw new IllegalStateException(" quads are not supported on OpenGL 3.2 core, convert to triangles ");

        }

        public
        QuadMesh_long(StandardPass preRender) {
            super(preRender);

            if (CoreHelpers.isCore)
                throw new IllegalStateException(" quads are not supported on OpenGL 3.2 core, convert to triangles ");

        }

        /**
         * for Triangles read line segments
         */
        @Override
        public
        iGeometry rebuildTriangle(int numTriangles) {
            triangleBuffer = new VertexBuffer((triangleLimit = triangleCount = numTriangles) * 4, true, isNative);
            triangleBuffer.elementSize = 4;
            markAsInvalidInAllContexts();
            elementBufferNeedsReconstruction = true;
            return this;
        }

        public
        IntBuffer longTriangle() {
            if (triangleBuffer == null) rebuildTriangle(0);
            IntBuffer t = triangleBuffer.getInBuffer(true);
            t.limit(triangleLimit * 2);
            return t;
        }

        @Override
        public
        ShortBuffer triangle() {
            assert false : "triangle called on long quad mesh";
            throw new IllegalArgumentException("triangle called on long quad mesh");
        }

        int numInstances = 0;

        public
        QuadMesh_long drawInstances(int num) {
            numInstances = num;
            return this;
        }

        @Override
        @InheritWeave
        @DispatchOverTopology(topology = Cont.class)
        protected
        void doPerformPass() {
            assert isNative;
            CoreHelpers.glBindVertexArrayAPPLE(0);

            int vertexObjectID = BasicContextManager.getId(this);

            if (triangleLimit * 4 > triangleBuffer.bBuffer.capacity() / triangleBuffer.primitiveSizeof) {
                triangleLimit = triangleBuffer.bBuffer.capacity() / triangleBuffer.primitiveSizeof / 4;
            }
            else if (triangleLimit < 0) {
                triangleLimit = 0;
            }

            // if (!ff) {
            // glGetQueryObjectuiv(query[0],
            // GL_QUERY_RESULT_AVAILABLE_ARB, available, 0);
            // glGetQueryObjectuivARB(query[0],
            // GL_QUERY_RESULT_ARB, sampleCount, 0);
            // }
            // ff = false;
            //
            // glBeginQuery(GL_SAMPLES_PASSED, query[0]);

            clean();
            CoreHelpers.glBindVertexArrayAPPLE(vertexObjectID);

            // glDisable(GL_POLYGON_SMOOTH);
            // glDisable(GL_LINE_SMOOTH);
            // glDisable(GL_DEPTH_TEST);
            // glDisable(GL_CULL_FACE);

            FullScreenCanvasSWT.triangleCount += triangleLimit;
            FullScreenCanvasSWT.vertexCount += vertexLimit;

            // ;//System.out.println(" drawing :"+this);
            CoreHelpers.doCameraState();

            if (numInstances == 0)
                glDrawElements(GL_QUADS, ((int) (drawFraction * triangleLimit)) * 4, GL_UNSIGNED_INT, 0);
            else if (subInstances > 0) glDrawElementsInstancedARB(GL_QUADS,
                                                                  ((int) (drawFraction * triangleLimit)) * 4,
                                                                  GL_UNSIGNED_INT,
                                                                  0,
                                                                  subInstances);

            CoreHelpers.glBindVertexArrayAPPLE(0);

            // glEndQuery(GL_SAMPLES_PASSED);

        }

    }

    static public
    class VertexBuffer {

        public int primitiveSizeof = 2;

        public FloatBuffer buffer;

        public boolean dirty = false;

        public int attrib = 0;

        public int elementSize;

        public int modCount;

        public HashMap<Object, int[]> modCounts = new HashMap<Object, int[]>();

        public ByteBuffer bBuffer;

        public ShortBuffer sBuffer;

        public IntBuffer iBuffer;

        // for elements
        public
        VertexBuffer(int count, boolean isNative) {

            if (forceNative) isNative = true;

            if (isNative) bBuffer = ByteBuffer.allocateDirect(2 * count).order(ByteOrder.nativeOrder());
            else bBuffer = ByteBuffer.allocate(2 * count).order(ByteOrder.nativeOrder());
            sBuffer = bBuffer.asShortBuffer();
            primitiveSizeof = 2;
        }

        // for long elements
        public
        VertexBuffer(int count, boolean isLong, boolean isNative) {
            if (forceNative) isNative = true;

            if (isLong) {
                if (isNative) bBuffer = ByteBuffer.allocateDirect(4 * count).order(ByteOrder.nativeOrder());
                else bBuffer = ByteBuffer.allocate(4 * count).order(ByteOrder.nativeOrder());
                iBuffer = bBuffer.asIntBuffer();
                primitiveSizeof = 4;
            }
            else {
                if (isNative) bBuffer = ByteBuffer.allocateDirect(2 * count).order(ByteOrder.nativeOrder());
                else bBuffer = ByteBuffer.allocate(2 * count).order(ByteOrder.nativeOrder());
                sBuffer = bBuffer.asShortBuffer();
                primitiveSizeof = 2;
            }
        }

        public
        VertexBuffer(int attrib, int count, int elementSize, boolean isNative) {
            this.attrib = attrib;

            if (forceNative) isNative = true;

            try {
                if (isNative)
                    bBuffer = ByteBuffer.allocateDirect(4 * count * elementSize).order(ByteOrder.nativeOrder());
                else bBuffer = ByteBuffer.allocate(4 * count * elementSize).order(ByteOrder.nativeOrder());
                buffer = bBuffer.asFloatBuffer();
                sBuffer = bBuffer.asShortBuffer();
                iBuffer = bBuffer.asIntBuffer();
                this.elementSize = elementSize;
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                System.err.println(" tried to allocate <" + attrib + ' ' + count + ' ' + elementSize + '>');
                throw e;
            }
        }

        // when this is called it puts the current
        // modCount into a hashtable with "me" as the
        // key. Presumably,
        // this is referenced when isDirty(me) is
        // called
        public
        void clean(Object me) {
            int[] d = modCounts.get(me);
            if (d == null) {
                modCounts.put(me, new int[]{modCount});
            }
            else {
                d[0] = modCount;
            }
        }

        public
        void forceClean() {
            Set<Entry<Object, int[]>> es = modCounts.entrySet();
            for (Entry<Object, int[]> e : es) {
                e.setValue(new int[]{modCount});
            }
        }

        public
        void dirty() {
            modCount++;
        }

        public
        void free() {
            freeFloatBuffer(buffer);
        }

        // returns the underlying float buffer (sets
        // the position to 0) and increments modCount.
        public
        FloatBuffer getBuffer(boolean willWrite) {
            if (willWrite) modCount++;
            buffer.position(0);
            return buffer;
        }

        // number of floats per element
        public
        int getElementSize() {
            return elementSize;
        }

        public
        IntBuffer getInBuffer(boolean willWrite) {
            if (willWrite) modCount++;
            iBuffer.rewind();
            return iBuffer;
        }

        public
        ShortBuffer getShortBuffer(boolean willWrite) {
            if (willWrite) modCount++;
            sBuffer.rewind();
            return sBuffer;
        }

        // retrieves the value of modCount from the
        // hashtable and compares it to its current
        // value. If it is
        // different (which it would be if getBuffer
        // had been called after a clean(me)), it
        // returns true.
        public
        boolean isDirty(Object me) {
            int[] i = modCounts.get(me);
            if (i == null) return true;
            return modCount != i[0];
        }
    }

    static public boolean insideDoubleFloatFrameBuffer = false;

    static public boolean useAttr0ForVertexPosition = CoreHelpers.isCore;

    static public float globalLineScale = 1;

    static public boolean forceNative = SystemProperties.getIntProperty("forceNativeBuffers",
                                                                        System.getProperty("java.version")
                                                                              .indexOf("1.6") != -1 ? 1 : 0) == 1;

    static public boolean disableUpload = false;

    static protected Integer[] integers = {new Integer(0),
                                           new Integer(1),
                                           new Integer(2),
                                           new Integer(3),
                                           new Integer(4),
                                           new Integer(5),
                                           new Integer(6),
                                           new Integer(7),
                                           new Integer(8),
                                           new Integer(9),
                                           10,
                                           11,
                                           new Integer(12),
                                           new Integer(13),
                                           new Integer(14),
                                           new Integer(15),
                                           new Integer(16)};

    static public
    void freeFloatBuffer(FloatBuffer i) {
    }

    static public
    void freeIntBuffer(IntBuffer i) {
    }

    static public
    long getAddress(Object i) {
        try {
            Field f = Buffer.class.getDeclaredField("address");
            f.setAccessible(true);
            long address = f.getLong(i);
            //System.out.println(" address is <" + address + ">");
            return address;
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }

    static public
    FloatBuffer newFloatBuffer(int count) {
        return ByteBuffer.allocateDirect(4 * count).order(ByteOrder.nativeOrder()).asFloatBuffer();
    }

    static public
    IntBuffer newIntBuffer(int count) {
        return ByteBuffer.allocateDirect(4 * count).order(ByteOrder.nativeOrder()).asIntBuffer();
    }

    static public
    ShortBuffer newShortBuffer(int count) {
        return ByteBuffer.allocateDirect(2 * count).order(ByteOrder.nativeOrder()).asShortBuffer();
    }

    static public
    String printAddress(Object i) {
        try {
            Field f = Buffer.class.getDeclaredField("address");
            f.setAccessible(true);
            long address = f.getLong(i);
            //System.out.println(" address is <" + address + ">");
            return String.valueOf(address);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}