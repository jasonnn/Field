package field.graphics.core;

import field.graphics.windowing.FullScreenCanvasSWT;
import field.launch.SystemProperties;
import field.math.linalg.Vector3;

import java.util.Random;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;

public class StereoCamera extends BasicCamera {

	// there are two ways of doing stereo, modifying the position of the
	// camera and angling the cameras in
	// or translating the frustum a little

	float io_frustra = 0.0f;

	Vector3 io_position = new Vector3();

	float io_lookat = 0.0f;

	boolean noStereo = SystemProperties.getIntProperty("zeroStereo", 0) == 1;

	float multiplyDisparity = (float) SystemProperties.getDoubleProperty("multiplyDisparity", 1);

	public StereoCamera setIOFrustra(float i) {
		this.io_frustra = i;
		return this;
	}

	public StereoCamera setIOPosition(Vector3 v) {
		this.io_position = new Vector3(v);
		return this;
	}

	public StereoCamera setIOPosition(float m) {
		this.io_position = new Vector3(m, m, m);
		return this;
	}

	public StereoCamera setIOLookAt(float x) {
		this.io_lookat = x;
		return this;
	}

	public float getIOLookAt() {
		return io_lookat;
	}

	static float flipped = (SystemProperties.getIntProperty("stereoEyeFlipped", 0) == 1 ? -1 : 1);
	static boolean passive = (SystemProperties.getIntProperty("passiveStereo", 0) == 1);

	double disparityPerDistance = SystemProperties.getDoubleProperty("defaultDisparityPerDistance", 0);

	boolean texture0IsRight = false;

	public float[] previousModelViewLeft;
	public float[] previousModelViewRight;

	float extraAmount = 1;

	@Override
	public void performPass() {
		pre();

		boolean wasDirty = projectionDirty || modelViewDirty;
		{
			// if (passive) {
			// if (FullScreenCanvasSWT.getSide() ==
			// FullScreenCanvasSWT.StereoSide.left) {
			// // glViewport(oX, oY, width / 2,
			// // height);
			// glViewport(oX + width / 2, oY, width / 2, height);
			// } else {
			// glViewport(oX, oY, width / 2, height);
			// }
			//
			// }
			// else
			// {
			glViewport(oX, oY, width, height);
			// }
			CoreHelpers.glMatrixMode(GL_PROJECTION);
			CoreHelpers.glLoadIdentity();

			float right = (float) (near * Math.tan((Math.PI * fov / 180f) / 2) * aspect) * frustrumMul;
			float top = (float) (near * Math.tan((Math.PI * fov / 180f) / 2)) * frustrumMul;

			float x = flipped * io_frustra * FullScreenCanvasSWT.getSide().x;

			if (noStereo)
				x = 0;

			CoreHelpers.glFrustum(-right + (right * (rshift + FullScreenCanvasSWT.currentCanvas.extraShiftX * extraAmount + x)), right + right * (rshift + FullScreenCanvasSWT.currentCanvas.extraShiftX * extraAmount + x), -top + top * tshift, top + top * tshift, near, far);

			CoreHelpers.glMatrixMode(GL_MODELVIEW);
			projectionDirty = false;
		}

		{
			CoreHelpers.glMatrixMode(GL_MODELVIEW);
			CoreHelpers.glLoadIdentity();

			Vector3 left = new Vector3().cross(getViewRay(null), getUp(null)).normalize();

			Vector3 io_position = new Vector3(this.io_position);

			io_position.x += disparityPerDistance * lookAt.distanceFrom(position);
			io_position.y += disparityPerDistance * lookAt.distanceFrom(position);
			io_position.z += disparityPerDistance * lookAt.distanceFrom(position);

			io_position.scale(multiplyDisparity);

			if (noStereo)
				left.scale(0);
			float x = flipped * io_frustra * FullScreenCanvasSWT.getSide().x;
			float right = (float) (near * Math.tan((Math.PI * fov / 180f) / 2) * aspect) * frustrumMul;

			CoreHelpers.gluLookAt(position.x + flipped * (io_position.x) * FullScreenCanvasSWT.getSide().x * left.x, position.y + flipped * io_position.y * FullScreenCanvasSWT.getSide().x * left.y, position.z + flipped * io_position.z * FullScreenCanvasSWT.getSide().x * left.z, lookAt.x + flipped * io_lookat * FullScreenCanvasSWT.getSide().x * left.x, lookAt.y + flipped * io_lookat * FullScreenCanvasSWT.getSide().x * left.y, lookAt.z + flipped * io_lookat * FullScreenCanvasSWT.getSide().x * left.z, up.x, up.y, up.z);

			CoreHelpers.glActiveTexture(GL_TEXTURE0);
			CoreHelpers.glMatrixMode(GL_TEXTURE);
			CoreHelpers.glLoadIdentity();
			if (!texture0IsRight)
				CoreHelpers.gluLookAt(position.x + flipped * io_position.x * FullScreenCanvasSWT.getSide().x * left.x, position.y + flipped * io_position.y * FullScreenCanvasSWT.getSide().x * left.y, position.z + flipped * io_position.z * FullScreenCanvasSWT.getSide().x * left.z, lookAt.x + flipped * io_lookat * FullScreenCanvasSWT.getSide().x * left.x, lookAt.y + flipped * io_lookat * FullScreenCanvasSWT.getSide().x * left.y, lookAt.z + flipped * io_lookat * FullScreenCanvasSWT.getSide().x * left.z, up.x, up.y, up.z);
			else
				CoreHelpers.gluLookAt(position.x + flipped * io_position.x * 1 * left.x, position.y + flipped * io_position.y * 1 * left.y, position.z + flipped * io_position.z * 1 * left.z, lookAt.x + flipped * io_lookat * 1 * left.x, lookAt.y + flipped * io_lookat * 1 * left.y, lookAt.z + flipped * io_lookat * 1 * left.z, up.x, up.y, up.z);

			CoreHelpers.glActiveTexture(GL_TEXTURE1);
			CoreHelpers.glMatrixMode(GL_TEXTURE);
			CoreHelpers.glLoadIdentity();

			float top = (float) (near * Math.tan((Math.PI * fov / 180f) / 2)) * frustrumMul;

			x = 0;
			CoreHelpers.glFrustum(-right + (right * (rshift + FullScreenCanvasSWT.currentCanvas.extraShiftX * extraAmount)), right + right * (rshift + FullScreenCanvasSWT.currentCanvas.extraShiftX * extraAmount), -top + top * tshift, top + top * tshift, near, far);

			CoreHelpers.gluLookAt(position.x, position.y, position.z, lookAt.x, lookAt.y, lookAt.z, up.x, up.y, up.z);

			CoreHelpers.glMatrixMode(GL_MODELVIEW);
			CoreHelpers.glActiveTexture(GL_TEXTURE0);

			modelViewDirty = false;
		}
		post();

		if (FullScreenCanvasSWT.getSide() == FullScreenCanvasSWT.StereoSide.left)
			randomSource.left();
		else if (FullScreenCanvasSWT.getSide() == FullScreenCanvasSWT.StereoSide.right)
			randomSource.right();

		if (FullScreenCanvasSWT.getSide() == FullScreenCanvasSWT.StereoSide.left) {
			previousModelViewRight = modelView;
			previousModelView = previousModelViewLeft;
		} else if (FullScreenCanvasSWT.getSide() == FullScreenCanvasSWT.StereoSide.right) {
			previousModelViewLeft = modelView;
			previousModelView = previousModelViewRight;
		} else {
			previousModelView = previousModelViewLeft;
			previousModelViewLeft = modelView;
		}
		projection = getCurrentProjectionMatrixNow(null);
		modelView = getCurrentModelViewMatrixNow(null);

		if (dropFrame) {
			if (FullScreenCanvasSWT.getSide() == FullScreenCanvasSWT.StereoSide.left) {
				previousModelViewLeft = modelView;
			} else {
				previousModelViewRight = modelView;
			}
		}

		currentCamera = this;
	}

	public void copyTo(BasicCamera shim) {
		super.copyTo(shim);
		if (shim instanceof StereoCamera) {
			((StereoCamera) shim).io_frustra = io_frustra;
			((StereoCamera) shim).io_position = io_position;
			((StereoCamera) shim).io_lookat = io_lookat;
		}
	}

	public StereoNoiseSource randomSource = new StereoNoiseSource();

	static public class StereoNoiseSource {

		private Random r;

		public StereoNoiseSource() {
			r = new Random();
		}

		public float get() {
			return r.nextFloat();
		}

		long leftseed = System.currentTimeMillis();

		public void left() {
			r = new Random(leftseed);
			r.nextFloat();
			r.nextFloat();
		}

		public void right() {
			leftseed = System.currentTimeMillis();

			r = new Random(leftseed);
			r.nextFloat();
			r.nextFloat();
		}
	}

	public float getIOFrustra() {
		return io_frustra;
	}

	public Vector3 getIOPosition() {
		return io_position;
	}

	public static double getRandomNumber() {

		return Math.random();
		//
		// if (BasicCamera.currentCamera instanceof StereoCamera)
		// {
		// return
		// ((StereoCamera)BasicCamera.currentCamera).randomSource.get();
		// }
		// else
		// {
		// ;//System.out.println(" warning: current camera is not a stereo camera ");
		// return Math.random();
		// }
	}

}
