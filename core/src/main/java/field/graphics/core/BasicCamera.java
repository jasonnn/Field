package field.graphics.core;

import field.bytecode.protect.annotations.HiddenInAutocomplete;
import field.core.util.FieldPyObjectAdaptor.iExtensible;
import field.graphics.core.Base.StandardPass;
import field.graphics.core.Base.iSceneListElement;
import field.math.abstraction.iBlendable;
import field.math.abstraction.iProvider;
import field.math.linalg.*;
import field.math.util.CubicTools;
import field.util.Dict;
import org.lwjgl.util.glu.GLU;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;

public class BasicCamera extends BasicUtilities.OnePassListElement implements iBasicCamera {
    
	@HiddenInAutocomplete
	public class FakeCamera extends BasicUtilities.OnePassElement {
        
		public FakeCamera() {
			super(StandardPass.preDisplay);
		}
        
		@Override
		public void performPass() {
			CoreHelpers.glMatrixMode(GL_PROJECTION);
			CoreHelpers.glPushMatrix();
			CoreHelpers.glLoadIdentity();
			CoreHelpers.glMatrixMode(GL_MODELVIEW);
			CoreHelpers.glPushMatrix();
			CoreHelpers.glLoadIdentity();
            
			BasicCamera.this.gl = gl;
			BasicCamera.this.glu = glu;
            
			BasicCamera.this.performPass();
			CoreHelpers.glMatrixMode(GL_PROJECTION);
			CoreHelpers.glPopMatrix();
			CoreHelpers.glMatrixMode(GL_MODELVIEW);
			CoreHelpers.glPopMatrix();
            
		}
        
	}
    
	public static
    class Projector implements Serializable {
		private static final long serialVersionUID = 1L;
		private final Matrix4 matrix;
		private final Matrix4 m1;
		private final Matrix4 p1;
        
		private final float width;
		private final float height;
        
		/**
		 * creates a Projector object --- useful for converting to and
		 * from world and screen spaces
		 */
		public Projector(State p) {
			m1 = p.modelViewMatrix();
			p1 = p.projectionMatrix();
			matrix = new Matrix4().mul(p1, m1);
			width = p.width;
			height = p.height;
		}
        
		@Override
		@HiddenInAutocomplete
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((m1 == null) ? 0 : m1.hashCode());
			result = prime * result + ((p1 == null) ? 0 : p1.hashCode());
			return result;
		}
        
		@Override
		@HiddenInAutocomplete
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Projector other = (Projector) obj;
			if (m1 == null) {
				if (other.m1 != null)
					return false;
			} else if (!m1.equals(other.m1))
				return false;
			if (p1 == null) {
				if (other.p1 != null)
					return false;
			} else if (!p1.equals(other.p1))
				return false;
			return true;
		}
        
		@HiddenInAutocomplete
		Vector4 tmp = new Vector4();
        
		/**
		 * converts from a world space to pixel for a canvas of width
		 * and height
		 */
		public Vector3 toPixel(Vector3 m, float width, float height) {
			tmp.x = m.x;
			tmp.y = m.y;
			tmp.z = m.z;
			tmp.w = 1.0f;
            
			tmp = matrix.transform(tmp);
            
			return new Vector3(width * (tmp.x / tmp.w / 2 + 0.5f), height * (tmp.y / tmp.w / 2 + 0.5f), tmp.z / tmp.w / 2 + 0.5f);
		}
        
		/**
		 * converts from a world space to pixel
		 */
		public Vector3 toPixel(Vector3 m) {
			return toPixel(m, width, height);
		}
        
		/**
		 * converts from a world space to
		 * "normalized device coordinates" --- this is a space that goes
		 * from -1, -1 (bottom left) to 1,1 (top right) regardless of
		 * the number of pixels in the display
		 */
		public Vector3 toPixelNDC(Vector3 m) {
			m = toPixel(m, 1, 1);
			return new Vector3(m.x - 0.5, m.y - 0.5, m.z * 0.5).scale(2);
		}
        
		int[] viewPort = new int[4];
        
		transient FloatBuffer model;
		transient FloatBuffer object1;
		transient FloatBuffer object2;
		transient FloatBuffer projection;
		transient IntBuffer viewport;
        
		/**
		 * creates a pair of world space positions. The line through
		 * these points are all the points that fall on pixel 'x,y'.
		 */
		public Vector3[] createIntersectionRay(float x, float y) {
			Vector3 o1 = new Vector3();
			Vector3 o2 = new Vector3();
			createIntersectionRay(x, y, o1, o2, width, height);
			return new Vector3[] { o1, o2 };
		}
        
		/**
		 * converts from pixel 'x,y' to world space, picking a depth
		 * such that the point is near 'near'.
		 */
		public Vector3 fromPixel(float x, float y, Vector3 near) {
			Vector3[] o = createIntersectionRay(x, y);
			Vector3 centerNext = IntersectionPrimatives.lineToPoint(o[0], new Vector3(o[1]).sub(o[0]).normalize(), near).closestPoint;
			return centerNext;
		}
        
		Object huh = new Object();
        
		@HiddenInAutocomplete
		public void createIntersectionRay(float x, float y, Vector3 rayIntersection1, Vector3 rayIntersection2, float width, float height) {
			if (model == null) {
				model = ByteBuffer.allocateDirect(4 * 16).order(ByteOrder.nativeOrder()).asFloatBuffer();
				object1 = ByteBuffer.allocateDirect(4 * 16).order(ByteOrder.nativeOrder()).asFloatBuffer();
				object2 = ByteBuffer.allocateDirect(4 * 16).order(ByteOrder.nativeOrder()).asFloatBuffer();
				projection = ByteBuffer.allocateDirect(4 * 16).order(ByteOrder.nativeOrder()).asFloatBuffer();
				viewport = ByteBuffer.allocateDirect(4 * 16).order(ByteOrder.nativeOrder()).asIntBuffer();
			}
			model.rewind();
			projection.rewind();
			for (int i = 0; i < 16; i++)
				model.put(m1.getElement(i % 4, i / 4));
			double[] pm = new double[16];
			for (int i = 0; i < 16; i++)
				projection.put(p1.getElement(i % 4, i / 4));
            
			double[] nearPtX = new double[3];
			double[] farPtX = new double[3];
			model.rewind();
			projection.rewind();
            
			viewPort[0] = 0;
			viewPort[1] = 0;
			viewPort[2] = (int) width;
			viewPort[3] = (int) height;
            
			viewport.rewind();
			viewport.put(0).put(0).put((int) width).put((int) height);
			viewport.rewind();
            
			object1.rewind();
			object2.rewind();
            
			synchronized (BasicContextManager.gluLock) {
                GLU.gluUnProject(x, y, 0.0f, model, projection, viewport, object1);
                GLU.gluUnProject(x, y, 1.0f, model, projection, viewport, object2);
            }
            rayIntersection1.set(object1.get(0), object1.get(1), object1.get(2));
            rayIntersection2.set(object2.get(0), object2.get(1), object2.get(2));
        }
        
	}
    
	public static
    class State implements Serializable, iBlendable<State>, iExtensible {
		private static final long serialVersionUID = 1L;
        
		/**
		 * returns a new camera state = a*w+to
		 */
		public static State add(State a, double w, State to) {
			State o = new State();
            
			o.position = Vector3.add(a.position, (float) w, to.position, null);
			o.target = Vector3.add(a.target, (float) w, to.target, null);
			o.up = Vector3.add(a.up, (float) w, to.up, null);
            
			o.fov = (float) (a.fov * w + to.fov);
			o.near = (float) (a.near * w + to.near);
			o.far = (float) ((a.far * w) + to.far);
			o.sx = (float) ((a.sx * w) + to.sx);
			o.sy = (float) ((a.sy * w) + to.sy);
			o.rx = (float) (a.rx * w + to.rx);
			o.aspect = (float) (a.aspect * w + to.aspect);
			o.width = (float) (a.width * w + to.width);
			o.height = (float) (a.height * w + to.height);
			return o;
		}
        
		boolean fancyBlend = false;
        
		/**
		 * turns quaternionic rotation blending on for this state. This
		 * changes the behavior of 'bend(a,b,alpha)'. Sometimes it's
		 * exactly what you want, sometimes, it's just too "fancy".
		 */
		public State setFancyBlend(boolean fancyBlend) {
			this.fancyBlend = fancyBlend;
			return this;
		}
        
		/**
		 * returns the State that is alpha between 'a' and 'b'. If
		 * alpha==0 return 'a', alpha==0.5 half way between 'a' and 'b'
		 * and so on.
		 */
		static public State blend(State a, State b, double alpha) {
			State o = new State();
			o.position = new Vector3().lerp(a.position, b.position, (float) alpha);
			o.target = new Vector3().lerp(a.target, b.target, (float) alpha);
			o.up = new Vector3().lerp(a.up, b.up, (float) alpha);
            
			o.fov = (float) (a.fov * (1 - alpha) + alpha * b.fov);
			o.near = (float) (a.near * (1 - alpha) + alpha * b.near);
			o.far = (float) (a.far * (1 - alpha) + alpha * b.far);
			o.sx = (float) (a.sx * (1 - alpha) + alpha * b.sx);
			o.sy = (float) (a.sy * (1 - alpha) + alpha * b.sy);
			o.rx = (float) (a.rx * (1 - alpha) + alpha * b.rx);
			o.aspect = (float) (a.aspect * (1 - alpha) + alpha * b.aspect);
            
			o.width = (float) (a.width * (1 - alpha) + alpha * b.width);
			o.height = (float) (a.height * (1 - alpha) + alpha * b.height);
            
			if (a.fancyBlend || b.fancyBlend) {
				Quaternion q1 = new Quaternion();
				Vector3 v1 = new Vector3();
				Vector3 s1 = new Vector3();
                
				Quaternion q2 = new Quaternion();
				Vector3 v2 = new Vector3();
				Vector3 s2 = new Vector3();
                
				Quaternion q3 = new Quaternion();
				Vector3 v3 = new Vector3();
				Vector3 s3 = new Vector3();
                
				Matrix4 m1 = a.modelViewMatrix();
				Matrix4 m2 = b.modelViewMatrix();
                
				m1.get(q1, v1, s1);
				m2.get(q2, v2, s2);
                
				// Quaternion.powerSlerp(q1, q2, alpha, q3);
                
				double da = Quaternion.distAngular(q1, q2);
                
				// ;//System.out.println(" DA :" + da);
                
				// q3 = Quaternion.blend(Arrays.asList(q1, q2),
				// (List)Arrays.asList((1-alpha), alpha));
                
				q3.interpolate(q1, q2, (float) alpha);
                
				v3.lerp(v1, v2, (float) alpha);
				s3.lerp(s1, s2, (float) alpha);
                
				double distance = a.position.distanceFrom(a.target) * (1 - alpha) + alpha * b.position.distanceFrom(b.target);
				q3 = q3.inverse();
				o.position = q3.rotateVector(v3).scale(-1);
				o.target = q3.rotateVector(new Vector3(0, 0, -1)).normalize().scale((float) distance).add(o.position);
				o.up = q3.rotateVector(new Vector3(0, 1, 0)).normalize();
			}
			return o;
		}
        
		static public State blend2(State a, State b, double alpha) {
			State o = new State();
			o.position = new Vector3().lerp(a.position, b.position, (float) alpha);
			o.target = new Vector3().lerp(a.target, b.target, (float) alpha);
			o.up = new Vector3().lerp(a.up, b.up, (float) alpha);
            
			o.fov = (float) (a.fov * (1 - alpha) + alpha * b.fov);
			o.near = (float) (a.near * (1 - alpha) + alpha * b.near);
			o.far = (float) (a.far * (1 - alpha) + alpha * b.far);
			o.sx = (float) (a.sx * (1 - alpha) + alpha * b.sx);
			o.sy = (float) (a.sy * (1 - alpha) + alpha * b.sy);
			o.rx = (float) (a.rx * (1 - alpha) + alpha * b.rx);
			o.aspect = (float) (a.aspect * (1 - alpha) + alpha * b.aspect);
            
			o.width = (float) (a.width * (1 - alpha) + alpha * b.width);
			o.height = (float) (a.height * (1 - alpha) + alpha * b.height);
            
			if (a.fancyBlend || b.fancyBlend) {
				Vector3 d1 = new Vector3(a.position).sub(a.target);
				Vector3 d2 = new Vector3(b.position).sub(b.target);
                
				{
					Quaternion q3 = new Quaternion().interpolate(new Quaternion(), new Quaternion(d2, d1), (float) alpha);
					float distance = (float) (a.position.distanceFrom(a.target) * (1 - alpha) + alpha * b.position.distanceFrom(b.target));
                    
					o.target = new Vector3().lerp(a.target, b.target, (float) alpha);
					o.position = q3.rotateVector(d1, new Vector3()).scale(distance).add(o.target);
				}
                
				{
					Quaternion q3 = new Quaternion().interpolate(new Quaternion(), new Quaternion(b.up, a.up), (float) alpha);
					o.up = q3.rotateVector(d1, new Vector3()).normalize();
				}
				o.reorthogonalizeUp();
			}
			return o;
		}
        
		/** the position of the camera */
		public Vector3 position = new Vector3(0, 0, -10);
        
		/** the target or lookAt position of the camera */
		public Vector3 target = new Vector3(0, 0, 0);
        
		/** the up direction of the camera */
		public Vector3 up = new Vector3(0, 1, 0);
        
		/** the field of view of the camera (in degrees) */
		public float fov = 45;
		/** the near clipping plane of the camera */
		public float near = 0.1f;
		/** the far clipping plane of the camera */
		public float far = 1000;
		/** the horizontal shift of the camera */
		public float sx = 0;
		/** the vertical shift of the camera */
		public float sy = 0;
		/** the fustrum scale of the camera */
		public float rx = 1;
		/** the aspect ratio of the camera */
		public float aspect = 1;
        
		/** the width of the canvas (read only) */
		public float width = 0;
		/** the height of the canvas (read only) */
		public float height = 0;
        
		/** duplicates this state */
		public State duplicate() {
			State o = new State();
			o.position = new Vector3(position);
			o.target = new Vector3(target);
			o.up = new Vector3(up);
            
			o.fov = fov;
			o.near = near;
			o.far = far;
			o.sx = sx;
			o.sy = sy;
			o.rx = rx;
			o.aspect = aspect;
			o.width = width;
			o.height = height;
			return o;
		}
        
		/**
		 * returns the look at point for this camera
		 */
		@HiddenInAutocomplete
		public Vector3 getLookAt(Vector3 out) {
			if (out == null)
				out = new Vector3();
			return out.setValue(target);
		}
        
		/**
		 * returns the look at point for this camera
		 */
		public Vector3 getLookAt() {
			return getLookAt(null);
		}
        
		/**
		 * returns the location of the camera
		 */
		@HiddenInAutocomplete
		public Vector3 getPosition(Vector3 object) {
			if (object == null)
				object = new Vector3();
			return object.setValue(position);
		}
        
		@HiddenInAutocomplete
		public Vector3 getUp(Vector3 object) {
			if (object == null)
				object = new Vector3();
			return object.setValue(up);
		}
        
		/**
		 * retursn the up direction for the camera
		 *
		 * @return
		 */
		public Vector3 getUp() {
			return getUp(null);
		}
        
		/**
		 * returns a direction that's perpendicular to both the viewing
		 * direction and up, thus pointing in the left direction
		 */
		@HiddenInAutocomplete
		public Vector3 getLeft(Vector3 object) {
			if (object == null)
				object = new Vector3();
			return object.setValue(new Vector3().cross(up, getView()));
		}
        
		/**
		 * returns a direction that's perpendicular to both the viewing
		 * direction and up, thus pointing in the left direction
		 */
		public Vector3 getLeft() {
			return getLeft(null);
		}
        
		/**
		 * makes sure that 'up' is in fact perpendicular to the viewing
		 * direction
		 */
		public void reorthogonalizeUp() {
			// if (true) return;
			up = new Vector3().cross(new Vector3().cross(getView(), up), getView());
			up.normalize();
		}
        
		/**
		 * sets the look at point for this camera
		 */
		public void setLookAt(Vector3 o) {
			target.setValue(o);
		}
        
		/**
		 * sets the position for this camera
		 */
		public void setPosition(Vector3 o) {
			position.setValue(o);
		}
        
		/**
		 * sets the up direction for this camera
		 */
		public void setUp(Vector3 o) {
			up.setValue(o);
		}
        
		/**
		 * returns (target-position).normalize()
		 */
		public Vector3 getView() {
			return new Vector3(target).sub(position).normalize();
		}
        
		/**
		 * returns the OpenGL ModelView Matrix for this state
		 *
		 * @return
		 */
		public Matrix4 projectionMatrix() {
            
			float right = (float) (near * Math.tan((Math.PI * fov / 180f) / 2) * aspect) * rx;
			float top = (float) (near * Math.tan((Math.PI * fov / 180f) / 2)) * rx;
            
			float left = -right + (right * sx);
			right = right + right * sx;
			float bottom = -top + (top * sy);
			top = top + top * sy;
            
			float x, y, a, b, c, d;
			Matrix4 m = new Matrix4();
            
			x = (2.0f * near) / (right - left);
			y = (2.0f * near) / (top - bottom);
			a = (right + left) / (right - left);
			b = (top + bottom) / (top - bottom);
			c = -(far + near) / (far - near);
			d = -(2.0f * far * near) / (far - near);
            
			m.m00 = x;
			m.m01 = 0.0F;
			m.m02 = a;
			m.m03 = 0.0F;
			m.m10 = 0.0F;
			m.m11 = y;
			m.m12 = b;
			m.m13 = 0.0F;
			m.m20 = 0.0F;
			m.m21 = 0.0F;
			m.m22 = c;
			m.m23 = d;
			m.m30 = 0.0F;
			m.m31 = 0.0F;
			m.m32 = -1.0F;
			m.m33 = 0.0F;
            
			// m.transpose();
            
			return m;
		}
        
		/**
		 * returns the OpenGL ModelView Matrix for this state
		 *
		 * @return
		 */
		public Matrix4 modelViewMatrix() {
			Matrix4 m = new Matrix4(BasicCamera.computeModelViewNow(position.x, position.y, position.z, target.x, target.y, target.z, up.x, up.y, up.z));
			m.transpose();
			return m;
		}
        
		/**
		 * returns a projector for this state
		 *
		 * @return
		 */
		public Projector getProjector() {
			return new Projector(this);
		}
        
		@Override
		@HiddenInAutocomplete
		public String toString() {
			return position + " " + target + ' ' + up;
		}
        
		/**
		 * returns a new 'zero' state
		 */
		public State blendRepresentation_newZero() {
			return new State();
		}
        
		/**
		 * copies the value of state 'to'
		 */
		public State setValue(State to) {
			this.position = new Vector3(to.position);
			this.target = new Vector3(to.target);
			this.up = new Vector3(to.up);
            
			this.fov = to.fov;
			this.near = to.near;
			this.far = to.far;
			this.sx = to.sx;
			this.sy = to.sy;
			this.rx = to.rx;
			this.aspect = to.aspect;
			return this;
		}
        
		/**
		 * linear interpolation between states. a=0 => 'before'; a=1 =>
		 * 'now'
		 */
		public State lerp(State before, State now, float a) {
			return this.setValue(State.blend(before, now, a));
		}
        
		/**
		 * cubic interpolation between states
		 */
		public State cerp(State before, float beforeTime, State now, float nowTime, State next, float nextTime, State after, float afterTime, float a) {
            
			State r = new State();
            
			CubicTools.cubic(a, before.position, beforeTime, now.position, nowTime, next.position, nextTime, after.position, afterTime, r.position);
			CubicTools.cubic(a, before.target, beforeTime, now.target, nowTime, next.target, nextTime, after.target, afterTime, r.target);
			CubicTools.cubic(a, before.up, beforeTime, now.up, nowTime, next.up, nextTime, after.up, afterTime, r.up);
            
			r.fov = (float) CubicTools.cubic(a, before.fov, beforeTime, now.fov, nowTime, next.fov, nextTime, after.fov, afterTime);
			r.near = (float) CubicTools.cubic(a, before.near, beforeTime, now.near, nowTime, next.near, nextTime, after.near, afterTime);
			r.far = (float) CubicTools.cubic(a, before.far, beforeTime, now.far, nowTime, next.far, nextTime, after.far, afterTime);
			r.sx = (float) CubicTools.cubic(a, before.sx, beforeTime, now.sx, nowTime, next.sx, nextTime, after.sx, afterTime);
			r.sy = (float) CubicTools.cubic(a, before.sy, beforeTime, now.sy, nowTime, next.sy, nextTime, after.sy, afterTime);
			r.rx = (float) CubicTools.cubic(a, before.rx, beforeTime, now.rx, nowTime, next.rx, nextTime, after.rx, afterTime);
			r.aspect = (float) CubicTools.cubic(a, before.aspect, beforeTime, now.aspect, nowTime, next.aspect, nextTime, after.aspect, afterTime);
            
			return this.setValue(r);
		}
        
		transient Dict d = null;
        
		@HiddenInAutocomplete
		public Dict getDict() {
			return d == null ? (d = new Dict()) : d;
		}
        
		/**
		 * returns point between position and lookat target, towards = 1
		 * => lookat, towards = 0 => position
		 */
        
		public Vector3 getPointAlongLookAt(float towards) {
			return getPointAlongLookAt(null, towards);
		}
        
		@HiddenInAutocomplete
		public Vector3 getPointAlongLookAt(Vector3 output, float towards) {
			if (output == null)
				output = new Vector3();
            
			output.lerp(position, target, towards);
            
			return output;
		}
        
		@Override
		@HiddenInAutocomplete
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Float.floatToIntBits(aspect);
			result = prime * result + Float.floatToIntBits(far);
			result = prime * result + Float.floatToIntBits(fov);
			result = prime * result + Float.floatToIntBits(near);
			result = prime * result + ((position == null) ? 0 : position.hashCode());
			result = prime * result + Float.floatToIntBits(rx);
			result = prime * result + Float.floatToIntBits(sx);
			result = prime * result + Float.floatToIntBits(sy);
			result = prime * result + ((target == null) ? 0 : target.hashCode());
			result = prime * result + ((up == null) ? 0 : up.hashCode());
			return result;
		}
        
		@Override
		@HiddenInAutocomplete
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			State other = (State) obj;
			if (cmp(aspect, other.aspect))
				return false;
			if (cmp(far, other.far))
				return false;
			if (cmp(fov, other.fov))
				return false;
			if (cmp(near, other.near))
				return false;
			if (cmp(position, other.position))
				return false;
			if (cmp(rx, other.rx))
				return false;
			if (cmp(sx, other.sx))
				return false;
			if (cmp(sy, other.sy))
				return false;
			if (cmp(target, other.target))
				return false;
			if (cmp(up, other.up))
				return false;
			return true;
		}

        private static
        boolean cmp(float a, float b) {
            return Math.abs(a - b) > 1e-4;
		}

        private static
        boolean cmp(Vector3 a, Vector3 b) {
            if (a == null)
				return b != null;
			if (b == null)
				return true;
			return a.distanceFrom(b) > 1e-4;
		}
        
		/**
		 * returns the location of the camera
		 */
		public Vector3 getPosition() {
			return new Vector3(position);
		}
        
		/**
		 * returns target-position
		 */
		public Vector3 getForward() {
			return new Vector3(target).sub(position);
		}
	}
    
	/**
	 * the default starting position for the camera
	 */
	@HiddenInAutocomplete
	public static final Vector3 DEFAULT_POSITION = new Vector3(0, 0, 10);
    
	/**
	 * the default starting lookAt
	 */
	@HiddenInAutocomplete
	public static final Vector3 DEFAULT_LOOKAT = new Vector3(0, 0, 0);
    
	/**
	 * the "current camera" --- set only while renderering
	 */
	static public BasicCamera currentCamera = null;
    
	/**
	 * the default starting up vector
	 */
	@HiddenInAutocomplete
	private static final Vector3 DEFAULT_UP = new Vector3(0, 1, 0);
    
	@HiddenInAutocomplete
	public float[] modelView;
    
	@HiddenInAutocomplete
	public float[] projection;
    
	// These are strictly for internal use to avoid
	// newing up stuff
	// all the time
	@HiddenInAutocomplete
	private final double[] modelMatrix = new double[16];
    
	@HiddenInAutocomplete
	private final double[] projMatrix = new double[16];
    
	@HiddenInAutocomplete
	private final int[] viewPort = new int[4];
    
	@HiddenInAutocomplete
	public int oX = 0; // lowerleft
    
	@HiddenInAutocomplete
	public int oY = 0; // lowerleft
    
	@HiddenInAutocomplete
	public int width = 1024;
    
	@HiddenInAutocomplete
	public int height = 768;
    
	@HiddenInAutocomplete
	public float fov = 45; // THIS IS IN
    
	@HiddenInAutocomplete
	public float aspect = 1024 / 768f; // (width/height)
    
	@HiddenInAutocomplete
	public float near = 1f;
    
	@HiddenInAutocomplete
	public float far = 1000;
    
	@HiddenInAutocomplete
	Vector3 position;
    
	@HiddenInAutocomplete
	Vector3 lookAt;
    
	@HiddenInAutocomplete
	Vector3 up;
    
	@HiddenInAutocomplete
	boolean projectionDirty = true;
    
	@HiddenInAutocomplete
	boolean modelViewDirty = true;
    
	@HiddenInAutocomplete
	public float rshift;
    
	@HiddenInAutocomplete
	public float tshift;
    
	@HiddenInAutocomplete
	public float frustrumMul = 1;
    
	@HiddenInAutocomplete
	double[] nearPtX = new double[3];
    
	@HiddenInAutocomplete
	double[] screenPt = new double[3];
    
	@HiddenInAutocomplete
	double[] outX = new double[3];
    
	@HiddenInAutocomplete
	double[] mm = new double[16];
    
	@HiddenInAutocomplete
	double[] pm = new double[16];
    
	public BasicCamera() {
		super(StandardPass.preTransform, StandardPass.preTransform);
		position = new Vector3(DEFAULT_POSITION);
		lookAt = new Vector3(DEFAULT_LOOKAT);
		up = new Vector3(DEFAULT_UP);
	}
    
	@HiddenInAutocomplete
	public BasicCamera(StandardPass at) {
		super(at, at);
		position = new Vector3(DEFAULT_POSITION);
		lookAt = new Vector3(DEFAULT_LOOKAT);
		up = new Vector3(DEFAULT_UP);
	}
    
	/**
	 * copies one camear to another
	 */
	public void copyTo(BasicCamera target) {
		Vector3 t = new Vector3();
		target.setPosition(getPosition(t));
		target.setLookAt(getLookAt(t));
		target.setUp(getUp(t));
        
		target.setAspect(getAspect());
		target.setFOV(getFov());
		target.setFrustrumMul(getFrustrumMul());
		target.setFrustrumShift(getFrustrumShiftX(), getFrustrumShiftY());
		target.setPerspective(getFov(), getAspect(), getNear(), getFar());
        
		int[] v = new int[4];
		target.getViewport(v);
		target.setViewport(v[0], v[1], v[2], v[3]);
	}
    
	FloatBuffer model = ByteBuffer.allocateDirect(4 * 16).order(ByteOrder.nativeOrder()).asFloatBuffer();
	FloatBuffer object1 = ByteBuffer.allocateDirect(4 * 16).order(ByteOrder.nativeOrder()).asFloatBuffer();
	FloatBuffer object2 = ByteBuffer.allocateDirect(4 * 16).order(ByteOrder.nativeOrder()).asFloatBuffer();
	FloatBuffer projectionn = ByteBuffer.allocateDirect(4 * 16).order(ByteOrder.nativeOrder()).asFloatBuffer();
	IntBuffer viewport = ByteBuffer.allocateDirect(4 * 16).order(ByteOrder.nativeOrder()).asIntBuffer();
    
	@HiddenInAutocomplete
	public void createIntersectionRay(double x, double y, Vector3 rayIntersection1, Vector3 rayIntersection2, int width, int height) {
		model.rewind();
		projectionn.rewind();
		for (int i = 0; i < 16; i++)
			model.put(modelView[i]);
		double[] pm = new double[16];
		for (int i = 0; i < 16; i++)
			projectionn.put(projection[i]);
        
		double[] nearPtX = new double[3];
		double[] farPtX = new double[3];
		model.rewind();
		projectionn.rewind();
        
		viewPort[0] = 0;
		viewPort[1] = 0;
		viewPort[2] = width;
		viewPort[3] = height;
        
		viewport.rewind();
		viewport.put(0).put(0).put(width).put(height);
		viewport.rewind();
        
		object1.rewind();
		object2.rewind();
        
		synchronized (BasicContextManager.gluLock) {
            
			GLU.gluUnProject((float) x, (float) y, 0.0f, model, projectionn, viewport, object1);
			GLU.gluUnProject((float) x, (float) y, 1.0f, model, projectionn, viewport, object2);
		}
        rayIntersection1.set(object1.get(0), object1.get(1), object1.get(2));
        rayIntersection2.set(object2.get(0), object2.get(1), object2.get(2));
    }
    
	/*
	 * get aspect ratio for this camera
	 */
	public float getAspect() {
		return aspect;
	}
    
	@HiddenInAutocomplete
	public BasicCamera getCameraAlignedAxes(Vector3 upVec, Vector3 viewVec, Vector3 rightVec) {
		getUp(upVec);
		getViewRay(viewVec);
		viewVec.normalize();
        
		rightVec.cross(viewVec, upVec);
		upVec.cross(rightVec, viewVec);
        
		return this;
	}
    
	@HiddenInAutocomplete
	public double[] getCurrentModelViewMatrix(double[] mm) {
        
		if (modelView == null)
			return null;
        
		if (mm == null)
			mm = new double[16];
		for (int i = 0; i < 16; i++)
			mm[i] = modelView[i];
		return mm;
	}
    
	@HiddenInAutocomplete
	public float[] getCurrentModelViewMatrix(float[] p) {
		if (p == null)
			p = new float[16];
		if (modelView == null) {
            // System.out.println(ANSIColorUtils.red(" warning: no modelView matrix"));
            return p;
		}
		System.arraycopy(modelView, 0, p, 0, p.length);
        
		return p;
	}
    
	@HiddenInAutocomplete
	public float[] getCurrentModelViewMatrixNow(float[] p) {
		if (p == null)
			p = new float[16];
		model.rewind();
		// glGetFloat(GL_MODELVIEW_MATRIX, model);
		CoreHelpers.getModelView(model);
		model.rewind();
		model.get(p);
		model.rewind();
        
		return p;
	}
    
	@HiddenInAutocomplete
	public double[] getCurrentProjectionMatrix(double[] mm) {
		if (projection == null)
			return null;
        
		if (mm == null)
			mm = new double[16];
		for (int i = 0; i < 16; i++)
			mm[i] = projection[i];
		return mm;
	}
    
	// boolean noLoadIdentity = true;
    
	@HiddenInAutocomplete
	public float[] getCurrentProjectionMatrix(float[] p) {
		if (p == null)
			p = new float[16];
        
		if (projection == null) {
            // System.out.println(ANSIColorUtils.red(" warning: no projection matrix"));
            return p;
		}
		System.arraycopy(projection, 0, p, 0, p.length);
        
		return p;
	}
    
	@HiddenInAutocomplete
	public float[] getCurrentProjectionMatrixNow(float[] p) {
		if (p == null)
			p = new float[16];
        
		projectionn.rewind();
		// glGetFloat(GL_PROJECTION_MATRIX, projectionn);
		CoreHelpers.getProjection(projectionn);
		projectionn.rewind();
		projectionn.get(p);
		projectionn.rewind();
        
		return p;
	}
    
	/**
	 * returns the far clipping plane for this camera
	 */
	public float getFar() {
		return far;
	}
    
	/**
	 * returns the field of view in degrees
	 */
	public float getFov() {
		return fov;
	}
    
	@HiddenInAutocomplete
	public float getFrustrumMul() {
		return frustrumMul;
	}
    
	/**
	 * returns the horizontal frustrum shift for this camera
	 */
	public float getFrustrumShiftX() {
		return rshift;
	}
    
	/**
	 * returns the vertical frustrum shift for this camera
	 */
	public float getFrustrumShiftY() {
		return tshift;
	}
    
	@HiddenInAutocomplete
	public Vector3 getLookAt(Vector3 inplace) {
		if (inplace == null)
			inplace = new Vector3();
		inplace.set(lookAt);
		return inplace;
	}
    
	/**
	 * returns the target that this camera is looking at.
	 */
	public Vector3 getLookAt() {
		return getLookAt(null);
	}
    
	@HiddenInAutocomplete
	public void getModelMatrix(FloatBuffer model) {
		model.put(modelView);
	}
    
	@HiddenInAutocomplete
	public CoordinateFrame getModelViewMatrix() {
		Matrix4 m4 = new Matrix4(modelView);
		m4.transpose();
        
		CoordinateFrame cf = new CoordinateFrame(m4);
		return cf;
	}
    
	/**
	 * returns the near clipping plane of the camera
	 */
	public float getNear() {
		return near;
	}
    
	@HiddenInAutocomplete
	public Vector3 getPosition(Vector3 inplace) {
		if (inplace == null)
			inplace = new Vector3();
		inplace.set(position);
		return inplace;
	}
    
	public Vector3 getPosition() {
		return getPosition(null);
	}
    
	@HiddenInAutocomplete
	public iProvider<Vector3> getPositionProvider() {
		return new iProvider<Vector3>() {
			public Vector3 get() {
				return getPosition(null);
			}
		};
	}
    
	@HiddenInAutocomplete
	public void getProjectionMatrix(float[] o) {
        System.arraycopy(projection, 0, o, 0, o.length);
    }
    
	@HiddenInAutocomplete
	public void getProjectionMatrix(FloatBuffer model) {
		model.put(projection);
	}
    
	/**
	 * returns a Projector for this camera (at this point in time -- changes
	 * to this camera are not reflected in the Projector)
	 *
	 * This object can be used to convert from world space to screen space
	 * and back again
	 */
	public Projector getProjector() {
		return getState().getProjector();
	}
    
	/**
	 * returns a State object which encapsulates all of the mutable state of
	 * a camera --- it's position, target, field of view and so on. States
	 * can be saved, blended, inspected etc.
	 *
	 * @return
	 */
	public State getState() {
		State s = new State();
		s.position = new Vector3(this.position);
		s.target = new Vector3(this.lookAt);
		s.up = new Vector3(this.up);
		s.fov = this.fov;
		s.near = this.near;
		s.far = this.far;
		s.sx = this.rshift;
		s.sy = this.tshift;
		s.rx = this.frustrumMul;
		s.aspect = this.aspect;
        
		this.getViewport(viewPort);
        
		s.width = this.viewPort[2];
		s.height = this.viewPort[3];
        
		return s;
	}
    
	public Vector3 getUp(Vector3 inplace) {
		if (inplace == null)
			inplace = new Vector3();
		inplace.set(up);
		return inplace;
	}
    
	/**
	 * returns [ox,oy,width,height]
	 */
	public int[] getViewport(int[] viewport) {
		if (viewport == null)
			viewport = new int[4];
		viewport[0] = oX;
		viewport[1] = oY;
		viewport[2] = width;
		viewport[3] = height;
		return viewport;
	}
    
	/**
	 * returns lookAt-position
	 */
	public Vector3 getViewRay(Vector3 inplace) {
		inplace = getLookAt(inplace);
		inplace.sub(inplace, getPosition(null));
		return inplace;
	}
    
	@HiddenInAutocomplete
	String name;
    
	@HiddenInAutocomplete
	public float[] previousModelView;
    
	@HiddenInAutocomplete
	static boolean dropFrame = false;
    
	Vector3 vCenter = new Vector3(0, 0, 0);
	Vector3 vEye = new Vector3(0, 0, -1);
	float pull = 1;
	float vWidth = 10;
    
	/**
	 * Does the work. if projection or modelview is dirty it will reset the
	 * appropriate transforms on the matrix stack.
	 */
	@Override
	@HiddenInAutocomplete
	public void performPass() {
        
		pre();
        
		synchronized (BasicContextManager.gluLock) {
            
			boolean wasDirty = projectionDirty || modelViewDirty;
			{
				glViewport(oX, oY, width, height);
				CoreHelpers.glMatrixMode(GL_PROJECTION);
				CoreHelpers.glLoadIdentity();
                
				float right = (float) (near * Math.tan((Math.PI * fov / 180f) / 2) * aspect) * frustrumMul;
				float top = (float) (near * Math.tan((Math.PI * fov / 180f) / 2)) * frustrumMul;
                
				CoreHelpers.glFrustum(-right + (right * (rshift)), right + right * (rshift), -top + top * tshift, top + top * tshift, near, far);
                
				// CoreHelpers.glFrustum_advanced(pull, near,
				// far, vWidth*aspect, vWidth, vCenter, vEye,
				// 0);
                
				CoreHelpers.glMatrixMode(GL_MODELVIEW);
				projectionDirty = false;
			}
            
			{
				CoreHelpers.glMatrixMode(GL_MODELVIEW);
				CoreHelpers.glLoadIdentity();
                
				CoreHelpers.gluLookAt(position.x, position.y, position.z, lookAt.x, lookAt.y, lookAt.z, up.x, up.y, up.z);
                
				CoreHelpers.glActiveTexture(GL_TEXTURE0);
				CoreHelpers.glMatrixMode(GL_TEXTURE);
				CoreHelpers.glLoadIdentity();
                
				CoreHelpers.gluLookAt(position.x, position.y, position.z, lookAt.x, lookAt.y, lookAt.z, up.x, up.y, up.z);
                
				CoreHelpers.glMatrixMode(GL_MODELVIEW);
                
				modelViewDirty = false;
			}
            
			if (Base.trace)
				;// System.out.println(" --------- applied camera <"
            // + this + "> (" + name + ")");
            
			post();
            
			previousModelView = modelView;
			projection = getCurrentProjectionMatrixNow(null);
			modelView = getCurrentModelViewMatrixNow(null);
            
			if (dropFrame) {
				previousModelView = modelView;
			}
            
			currentCamera = this;
            
		}
        
		finalPost();
	}
    
	protected void finalPost() {
        
	}
    
	@HiddenInAutocomplete
	public Vector2 project(Vector3 worldPt) {
		Vector3 v = project3(worldPt);
		return new Vector2(v.get(0), v.get(1));
	}
    
	@HiddenInAutocomplete
	public Vector3 project3(Vector3 worldPt) {
        
		model.rewind();
		projectionn.rewind();
		for (int i = 0; i < 16; i++)
			model.put(modelView[i]);
		double[] pm = new double[16];
		for (int i = 0; i < 16; i++)
			projectionn.put(projection[i]);
        
		double[] nearPtX = new double[3];
		double[] farPtX = new double[3];
		model.rewind();
		projectionn.rewind();
        
		viewPort[0] = 0;
		viewPort[1] = 0;
		viewPort[2] = width;
		viewPort[3] = height;
        
		viewport.rewind();
		viewport.put(0).put(0).put(width).put(height);
		viewport.rewind();
        
		object1.rewind();
		object2.rewind();
		synchronized (BasicContextManager.gluLock) {
			GLU.gluProject(worldPt.x, worldPt.y, worldPt.z, model, projectionn, viewport, object1);
		}
		// return result
        return new Vector3(object1.get(0), object1.get(1), object1.get(2));
    }
    
	/**
	 * rotates the camera around the lookat point by rad radians. Rotatation
	 * is around the up axis.
	 */
	public void rotateAroundCenter(float rad) {
		Vector3 nv = new Vector3();
		// get vector
		// from lookAt
		// to position
		Vector3 v = new Vector3().sub(position, lookAt);
		// get a
		// rotation
		// around Z
		Quaternion q = new Quaternion();
		q.set(new Vector3(up), rad);
		q.rotateVector(v, nv);
		// get new
		// location for
		// position
		position.setValue(new Vector3().add(lookAt, nv));
		modelViewDirty = true;
	}
    
	/**
	 * sets the aspect ratio of the camera
	 */
	public iSceneListElement setAspect(float f) {
		aspect = f;
		return this;
	}
    
	/**
	 * Sets the camera at position, looking at lookAt, with an up vector of
	 * up for use by gluLookAt.
	 */
	public void setCamera(Vector3 position, Vector3 lookAt, Vector3 up) {
		this.lookAt.set(lookAt);
		this.position.set(position);
		this.up.set(up);
		modelViewDirty = true;
	}
    
	/**
	 * sets the Field of view of this camera (in degrees)
	 */
	public void setFOV(float f) {
		this.fov = f;
	}
    
	@HiddenInAutocomplete
	public void setFrustrumMul(float frustrumMul) {
        
		if (frustrumMul < 0.02)
			frustrumMul = 0.02f;
		if (frustrumMul > 30)
			frustrumMul = 30;
        
		if (!Float.isNaN(frustrumMul) && !Float.isInfinite(frustrumMul) && frustrumMul > 1e-3) {
			this.frustrumMul = frustrumMul;
			modelViewDirty = true;
			projectionDirty = true;
		} else {
			this.frustrumMul = 1;
			modelViewDirty = true;
			projectionDirty = true;
		}
	}
    
	/**
	 * sets the vertical and horizontal frustrum shift. This is an off axis
	 * view transformation (sometimes used in stereo-projection)
	 */
	public void setFrustrumShift(float rightShift, float topShift) {
        
		if (rightShift > 30)
			rightShift = 30;
		if (rightShift < -30)
			rightShift = -30;
		if (topShift > 30)
			topShift = 30;
		if (topShift < -30)
			topShift = -30;
        
		if (!Float.isNaN(rightShift) && !Float.isInfinite(rightShift) && !Float.isNaN(topShift) && !Float.isInfinite(topShift)) {
			this.rshift = rightShift;
			this.tshift = topShift;
			modelViewDirty = true;
			projectionDirty = true;
		} else {
			this.rshift = 0;
			this.tshift = 0;
			modelViewDirty = true;
			projectionDirty = true;
		}
	}
    
	/**
	 * sets the point to look at
	 */
	public void setLookAt(Vector3 lookAt) {
		// if
		// (lookAt.mag()
		// < 1e3)
		{
			modelViewDirty = this.lookAt.distanceFrom(lookAt) > 1e-10;
			this.lookAt.set(lookAt);
		}
	}
    
	/**
	 * sets the field of view, aspect ratio and near and far clipping planes
	 * for this camera
	 */
	public void setPerspective(float fov, float aspect, double near, float far) {
		projectionDirty = true;
		this.fov = fov;
		this.aspect = aspect;
		this.near = (float) near;
		this.far = far;
	}
    
	public void setPosition(Vector3 position) {
		// if
		// (position.mag()
		// < 1e6)
		{
			modelViewDirty = this.position.distanceFrom(position) > 1e-10;
			this.position.set(position);
		}
	}
    
	public void setState(State s) {
		this.position.setValue(s.position);
		this.lookAt.setValue(s.target);
		this.up.setValue(s.up);
		this.fov = s.fov;
		this.near = s.near;
		this.far = s.far;
		this.rshift = s.sx;
		this.tshift = s.sy;
		this.frustrumMul = s.rx;
	}
    
	public void setUp(Vector3 up) {
		// if (up.mag()
		// > 1e-3)
		{
			this.up.set(up);
		}
	}
    
	/**
	 * sets the viewport parameters for use by glViewPort()
	 */
	public void setViewport(int oX, int oY, int width, int height) {
		projectionDirty = this.oX != oX || this.oY != oY || this.width != width || this.height != height;
		this.oX = oX;
		this.oY = oY;
		this.width = width;
		this.height = height;
	}
    
	@Override
	@HiddenInAutocomplete
	public String toString() {
		return "camera position<" + this.getPosition(null) + "> look at <" + this.getLookAt(null) + "> up <" + this.getUp(null) + '>'
               + (rshift != 0 ? "rshift <" + rshift + "> <" + tshift + "> <" + frustrumMul + '>' : "");
	}
    
	@HiddenInAutocomplete
	public Vector3 unProject(double x, double y, double distance) {
		projectionn.rewind();
		for (int i = 0; i < 16; i++)
			model.put(modelView[i]);
		double[] pm = new double[16];
		for (int i = 0; i < 16; i++)
			projectionn.put(projection[i]);
        
		double[] nearPtX = new double[3];
		double[] farPtX = new double[3];
		model.rewind();
		projectionn.rewind();
        
		viewPort[0] = 0;
		viewPort[1] = 0;
		viewPort[2] = width;
		viewPort[3] = height;
        
		viewport.rewind();
		viewport.put(0).put(0).put(width).put(height);
		viewport.rewind();
        
		object1.rewind();
		object2.rewind();
		synchronized (BasicContextManager.gluLock) {
			GLU.gluProject((float) x, (float) y, (float) distance, model, projectionn, viewport, object1);
		}
		// return result
        return new Vector3(object1.get(0), object1.get(1), object1.get(2));
    }
    
	/**
	 * given a screen point and a plane that passes through the origin
	 * (e.g., the ground plane) will return the 3D world pt on the plane
	 * that corresponds to the screen point. Uses gluUnProject(). NOTE:
	 * assumes that the modelviewMatrix and the projectionMatrix on the
	 * matrix stack are valid.
	 *
	 * NOTE: plane is a four vector. the fourth element is the distance from
	 * the origin, positive, along the normal. This fully specifies any
	 * plane
	 *
	 */
	@HiddenInAutocomplete
	public Vector3 unProject(double x, double y, double[] plane) {
        
		Vector3 nearPtX = new Vector3();
		Vector3 farPtX = new Vector3();
		createIntersectionRay(x, y, nearPtX, farPtX, width, height);

        Vector3 lu = new Vector3(nearPtX.x, nearPtX.y, nearPtX.z);
        Vector3 ld = (new Vector3(farPtX.x, farPtX.y, farPtX.z)).sub(lu).normalize();
        Vector3 jn = new Vector3(plane[0], plane[1], plane[2]);
		float t = (float) -(plane[3] + lu.dot(jn)) / (ld.dot(jn));
		ld.scale(t);
		Vector3 ip = lu.add(ld);
		return ip;
	}

    private static
    String arrayToString(float[] p) {
        StringBuilder s = new StringBuilder();
        for (float aP : p) s.append(aP).append(' ');
		return s.toString();
	}
    
	/**
	 * set the near and far clipping planes of this camera
	 */
	public BasicCamera setNearFar(double near, double far) {
		this.near = (float) near;
		this.far = (float) far;
		projectionDirty = true;
		modelViewDirty = true;
		return this;
	}
    
	@HiddenInAutocomplete
	public float[] computeModelViewNow() {
        
		if (modelView == null)
			return new Matrix4().get((float[]) null);
		// ;//System.out.println(" model view is <"+new
		// Matrix4(modelView)+">");
        
		float[] c = computeModelViewNow(position.x, position.y, position.z, lookAt.x, lookAt.y, lookAt.z, up.x, up.y, up.z);
		modelView = c;
		// ;//System.out.println(" model view now <"+new
		// Matrix4(modelView)+">");
		return c;
	}
    
	@HiddenInAutocomplete
	static public float[] computeModelViewNow(float eyex, float eyey, float eyez, float centerx, float centery, float centerz, float upx, float upy, float upz) {
        
		Vector3 forward = new Vector3(centerx - eyex, centery - eyey, centerz - eyez);
		Vector3 up = new Vector3(upx, upy, upz);
        
		forward.normalize();
        
		/* Side = forward x up */
		Vector3 side = new Vector3().cross(forward, up);
		side.normalize();
        
		/* Recompute up as: up = side x forward */
		up.cross(side, forward);
        
		float[] ret = new float[16];
        
		ret[0 * 4 + 0] = side.get(0);
		ret[1 * 4 + 0] = side.get(1);
		ret[2 * 4 + 0] = side.get(2);
        
		ret[0 * 4 + 1] = up.get(0);
		ret[1 * 4 + 1] = up.get(1);
		ret[2 * 4 + 1] = up.get(2);
        
		ret[0 * 4 + 2] = -forward.get(0);
		ret[1 * 4 + 2] = -forward.get(1);
		ret[2 * 4 + 2] = -forward.get(2);
        
		ret[3 * 4 + 3] = 1;
        
		Matrix4 m = new Matrix4(ret);
		m.invert();
		Vector3 e = m.transformPosition(new Vector3(eyex, eyey, eyez));
		ret[3 * 4 + 0] = -e.x;
		ret[3 * 4 + 1] = -e.y;
		ret[3 * 4 + 2] = -e.z;
        
		return ret;
        
	}
    
}