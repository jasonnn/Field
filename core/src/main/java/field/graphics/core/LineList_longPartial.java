package field.graphics.core;

import field.graphics.core.BasicGeometry.VertexBuffer;

import java.util.Iterator;
import java.util.Map.Entry;

import static org.lwjgl.opengl.GL15.*;

/**
 * a static long triangle mesh supporting trickle uploading
 */
public class LineList_longPartial extends BasicGeometry.LineList_long {

	public LineList_longPartial() {
		super(new BasicUtilities.Position());
	}

	public static int vertexPerFrame = 5000;

	int uploadedVertex = 0;
	int uploadedTriangle = 0;

	boolean uploadComplete = false;
	boolean uploadStarted = false;

	/**
	 * call this when you've finished putting data into the buffers
	 */

	public void beginUpload() {
		uploadStarted = true;
	}

	@Override
	protected void doPerformPass() {
		if (uploadComplete)
			super.doPerformPass();
		else if (uploadStarted)
			clean();
	}

	@Override
	protected void clean() {

		if (!uploadStarted)
			return;
		if (uploadComplete)
			return;

		Object context = BasicContextManager.getCurrentContext();

		if (uploadedVertex < vertexLimit) {
			glBindBuffer(GL_ARRAY_BUFFER, attributeBuffers[0]);

            //System.out.println(glGetError() == 0);

			int start = uploadedVertex * 4 * vertexStride;
			int end = Math.min(4 * vertexStride * vertexLimit, 4 * vertexStride * (uploadedVertex + vertexPerFrame));

			// int size = 4 * vertexStride * vertexLimit;

			vertexBuffer.bBuffer.position(4 * uploadedVertex * vertexStride);
			vertexBuffer.bBuffer.limit(end);

//			glBufferSubData(GL_ARRAY_BUFFER, start, end - start, vertexBuffer.bBuffer);

			// perhaps start should be 0 here?
			glBufferSubData(GL_ARRAY_BUFFER, start, vertexBuffer.bBuffer);

            //System.out.println(glGetError() == 0);

			Iterator<Entry<Integer, VertexBuffer>> aux = auxBuffers.entrySet().iterator();
			while (aux.hasNext()) {
				Entry<Integer, VertexBuffer> e = aux.next();

				VertexBuffer vbuffer = e.getValue();
				int aid = e.getKey();
				{

					start = uploadedVertex * 4 * vbuffer.elementSize;
					end = Math.min(4 * vbuffer.elementSize * vertexLimit, 4 * vbuffer.elementSize * (uploadedVertex + vertexPerFrame));

					glBindBuffer(GL_ARRAY_BUFFER, attributeBuffers[aid]);
					vbuffer.bBuffer.position(4 * uploadedVertex * vbuffer.elementSize);
					vbuffer.bBuffer.limit(vertexLimit * 4 * vbuffer.elementSize);
//					glBufferSubData(GL_ARRAY_BUFFER, start, end - start, vbuffer.bBuffer);
					glBufferSubData(GL_ARRAY_BUFFER, 0, vbuffer.bBuffer);
					vbuffer.clean(context);
				}
			}

		} else {
		}

		if (uploadedTriangle < triangleLimit) {
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, elementBuffer[0]);

			int start = uploadedTriangle * triangleBuffer.elementSize;
			int end = Math.min(triangleBuffer.elementSize * triangleLimit, triangleBuffer.elementSize * (uploadedTriangle + vertexPerFrame));

			triangleBuffer.bBuffer.position(triangleBuffer.elementSize * uploadedTriangle);
			triangleBuffer.bBuffer.limit(triangleBuffer.elementSize * triangleLimit);

//			glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, start, end - start, triangleBuffer.bBuffer);
			glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, triangleBuffer.bBuffer);
			triangleBuffer.clean(context);
		}

		uploadedVertex = Math.min(vertexLimit, uploadedVertex + vertexPerFrame);
		uploadedTriangle = Math.min(triangleLimit, uploadedTriangle + vertexPerFrame);

		if ((uploadedVertex == vertexLimit) && (uploadedTriangle == triangleLimit))
			uploadComplete = true;

	}

}
