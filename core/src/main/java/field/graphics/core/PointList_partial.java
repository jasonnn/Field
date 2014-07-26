package field.graphics.core;

import field.graphics.core.BasicGeometry.VertexBuffer;

import java.util.Iterator;
import java.util.Map.Entry;

import static org.lwjgl.opengl.GL15.*;

public
class PointList_partial extends PointList {

    public static int vertexPerFrame = 5000;

    int uploadedVertex = 0;

    boolean uploadComplete = false;
    boolean uploadStarted = false;

    /**
     * call this when you've finished putting data into the buffers
     */
    public
    void beginUpload() {
        uploadStarted = true;
    }

    @Override
    protected
    void doPerformPass() {
        if (uploadComplete) super.doPerformPass();
        else if (uploadStarted) clean();
    }

    @Override
    protected
    void clean() {

        if (!uploadStarted) return;
        if (uploadComplete) return;

        Object context = BasicContextManager.getCurrentContext();

        if (uploadedVertex < vertexLimit) {
            glBindBuffer(GL_ARRAY_BUFFER, attributeBuffers[0]);

            //System.out.println(glGetError() == 0);

            int start = uploadedVertex * 4 * vertexStride;
            int end = Math.min(4 * vertexStride * vertexLimit, 4 * vertexStride * (uploadedVertex + vertexPerFrame));

            // int size = 4 * vertexStride * vertexLimit;

            vertexBuffer.bBuffer.position(4 * uploadedVertex * vertexStride);
            vertexBuffer.bBuffer.limit(end);

            glBufferSubData(GL_ARRAY_BUFFER, 0, vertexBuffer.bBuffer);

            //System.out.println(glGetError() == 0);

            Iterator<Entry<Integer, VertexBuffer>> aux = auxBuffers.entrySet().iterator();
            while (aux.hasNext()) {
                Entry<Integer, VertexBuffer> e = aux.next();

                VertexBuffer vbuffer = e.getValue();
                int aid = e.getKey();
                {

                    start = uploadedVertex * 4 * vbuffer.elementSize;
                    end = Math.min(4 * vbuffer.elementSize * vertexLimit,
                                   4 * vbuffer.elementSize * (uploadedVertex + vertexPerFrame));

                    glBindBuffer(GL_ARRAY_BUFFER, attributeBuffers[aid]);
                    vbuffer.bBuffer.position(4 * uploadedVertex * vbuffer.elementSize);
                    vbuffer.bBuffer.limit(vertexLimit * 4 * vbuffer.elementSize);
                    glBufferSubData(GL_ARRAY_BUFFER, 0, vbuffer.bBuffer);
                    vbuffer.clean(context);
                }
            }

        }
        else {
        }

        uploadedVertex = Math.min(vertexLimit, uploadedVertex + vertexPerFrame);

        if (uploadedVertex == vertexLimit) uploadComplete = true;

    }

}
