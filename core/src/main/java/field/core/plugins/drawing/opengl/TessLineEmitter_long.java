package field.core.plugins.drawing.opengl;

import field.graphics.core.BasicGeometry.TriangleMesh;
import field.graphics.dynamic.SubMesh_long;
import field.math.linalg.Vector2;
import field.math.linalg.Vector4;
import field.util.Dict;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.GLUtessellator;
import org.lwjgl.util.glu.GLUtessellatorCallbackAdapter;
import org.lwjgl.util.glu.tessellation.GLUtessellatorImpl;

import java.util.ArrayList;
import java.util.List;

public
class TessLineEmitter_long extends SmallLineEmitter_long {

    public static
    class VInfo {
        int vertex = -1;

        Vector2 position = new Vector2();

        List<Object> properties;

        public
        VInfo(int vertex, Vector2 position, List<Object> properties) {
            super();
            this.vertex = vertex;
            this.position = position;
            this.properties = properties;
        }

        @Override
        public
        String toString() {
            return vertex + "@ " + position;
        }
    }

    public SubMesh_long mesh;

    private final GLUtessellator tess;

    private boolean contourFirst;

    TriangleMesh m;

    List<VInfo> ongoingTriangle = new ArrayList<VInfo>();

    public
    TessLineEmitter_long() {
        this(GLU.GLU_TESS_WINDING_ODD);
    }

    public
    TessLineEmitter_long(int windingRule) {
        tess = new GLUtessellatorImpl();
        mesh = new SubMesh_long();

        tess.gluTessCallback(GLU.GLU_TESS_VERTEX_DATA, new GLUtessellatorCallbackAdapter() {
            @Override
            public
            void vertexData(Object arg0, Object arg1) {
                VInfo i = (VInfo) arg0;

                nextTriangle(i);
            }
        });

        tess.gluTessCallback(GLU.GLU_TESS_COMBINE_DATA, new GLUtessellatorCallbackAdapter() {

            @Override
            public
            void combineData(double[] coords, Object[] data, float[] weight, Object[] outdata, Object arg4) {

                // todo, interpolate properties correctly
                outdata[0] = new VInfo(-1, new Vector2(coords[0], coords[1]), interpolateProperites(data, weight));
            }
        });
        tess.gluTessCallback(GLU.GLU_EDGE_FLAG, new GLUtessellatorCallbackAdapter() {
        });

        // GLU.gluTessProperty( GLU.GLU_TESS_WINDING_RULE,
        // GLU.GLU_TESS_WINDING_NONZERO);
        tess.gluTessProperty(GLU.GLU_TESS_WINDING_RULE, windingRule);

    }

    @Override
    public
    void begin() {
        super.begin();
        mesh.open();

        tess.gluTessBeginPolygon(null);
    }

    @Override
    public
    void beginContour() {
        super.beginContour();
        contourFirst = true;
        tess.gluTessBeginContour();
    }

    @Override
    public
    void emitLinearFrame(Vector2 a,
                         Vector2 b,
                         List<Object> name,
                         List<Object> name2,
                         Dict properties,
                         iLinearGraphicsContext context) {

        if (contourFirst) {
            VInfo info = new VInfo(-1, a, name);
            nextVertex(info);
            tess.gluTessVertex(new double[]{a.x, a.y, 0}, 0, info);
        }

        VInfo info = new VInfo(-1, b, name2);
        nextVertex(info);

        tess.gluTessVertex(new double[]{b.x, b.y, 0}, 0, info);

        contourFirst = false;

    }

    @Override
    public
    void end() {
        super.end();

        tess.gluTessEndPolygon();
        mesh.close();

    }

    @Override
    public
    void endContour() {
        super.endContour();
        tess.gluTessEndContour();
    }

    public
    SubMesh_long getMesh() {
        return mesh;
    }

    protected
    void decorateVertex(int v, List<Object> properties) {
    }

    protected static
    List<Object> interpolateProperites(Object[] data, float[] weight) {
        List<Object> o = new ArrayList<Object>();

        int l = (((VInfo) data[0]).properties).size();
        for (int i = 0; i < l; i++) {
            Object ty = (((VInfo) data[0]).properties).get(i);
            if (ty instanceof Vector4) {
                Vector4 z = new Vector4();
                float m = 0;
                for (int q = 0; q < data.length; q++) {
                    if (data[q] == null) {
                    }
                    else {
                        z.x += (((Vector4) (((VInfo) data[q]).properties).get(i))).x * weight[q];
                        z.y += (((Vector4) (((VInfo) data[q]).properties).get(i))).y * weight[q];
                        z.z += (((Vector4) (((VInfo) data[q]).properties).get(i))).z * weight[q];
                        z.w += (((Vector4) (((VInfo) data[q]).properties).get(i))).w * weight[q];
                        m += weight[q];
                    }
                }
                z.x /= m;
                z.y /= m;
                z.z /= m;
                z.w /= m;
                o.add(z);
            }
            else {
                o.add(null);
            }
        }

        return o;
    }

    protected
    void nextTriangle(VInfo i) {
        if (i.vertex == -1) nextVertex(i);

        if (ongoingTriangle.size() == 2) {
            nextTriangle(ongoingTriangle.get(0), ongoingTriangle.get(1), i);
            ongoingTriangle.clear();
        }
        else ongoingTriangle.add(i);
    }

    protected
    void nextTriangle(VInfo a, VInfo b, VInfo c) {
        mesh.nextFace(a.vertex, b.vertex, c.vertex);
    }

    protected
    void nextVertex(VInfo info) {
        if (info.vertex == -1) {
            info.vertex = mesh.nextVertex(info.position.toVector3());
            decorateVertex(info.vertex, info.properties);
        }
    }

    public
    void setWidingRule(float wr) {
        if (wr == 0) tess.gluTessProperty(GLU.GLU_TESS_WINDING_RULE, GLU.GLU_TESS_WINDING_ODD);
        else tess.gluTessProperty(GLU.GLU_TESS_WINDING_RULE, GLU.GLU_TESS_WINDING_NONZERO);

    }

}
