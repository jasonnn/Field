package field.graphics.core;

import field.graphics.core.Base.StandardPass;
import field.graphics.core.BasicUtilities.OnePassElement;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;


/**
 * debugging speed issues on ATI graphics cards
*/

public class DebugPointList extends OnePassElement{

	private final int numPoints;
	private FloatBuffer vertex;

	public DebugPointList(int numPoints)
	{
		super(StandardPass.render);
		this.numPoints = numPoints;
		
		vertex = ByteBuffer.allocateDirect(numPoints*4*3).order(ByteOrder.nativeOrder()).asFloatBuffer();
	}

	boolean first = false;
	
	@Override
	public void performPass() {

		if (first)
		{
			init();
			first = false;
		}
		
		glEnableClientState(GL_VERTEX_ARRAY);
		glVertexPointer(3, 12, vertex);
		glDrawArrays(GL_POINTS, 0, numPoints);
	}

	private void init() {
		
		glEnableClientState(GL_VERTEX_ARRAY);
		glVertexPointer(3, 12, vertex);
		
		
	}
	
}
