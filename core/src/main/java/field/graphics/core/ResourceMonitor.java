package field.graphics.core;

import field.graphics.core.BasicUtilities.OnePassElement;
import field.launch.SystemProperties;
import field.util.TaskQueue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class ResourceMonitor extends OnePassElement {

	public static boolean checkResources = SystemProperties.getIntProperty("glResources", 0) == 1;

	public static ResourceMonitor resourceMonitor = null;

	int t = 0;

//	Method[] methods = { ReflectionTools.findFirstMethodCalled(GL.class, "glIsFramebufferEXT"), ReflectionTools.findFirstMethodCalled(GL.class, "glIsTexture"), ReflectionTools.findFirstMethodCalled(GL.class, "glIsBuffer") };
//
//	String[] titles = { "frame buffers", "textures", "vertex/element buffers" };

	TaskQueue q = new TaskQueue();

	int up = 100;

	public ResourceMonitor() {
		super(Base.StandardPass.preTransform);
	}

	public TaskQueue getQueue() {
		return q;
	}

	@Override
	public void performPass() {
		// this is real
		// time
		// consuming
		if (checkResources) {

//			try {
//				for (int i = 0; i < methods.length; i++) {
//					check(titles[i], methods[i], gl);
//				}
//			} catch (IllegalArgumentException e) {
//				e.printStackTrace();
//			} catch (IllegalAccessException e) {
//				e.printStackTrace();
//			} catch (InvocationTargetException e) {
//				e.printStackTrace();
//			}
		}
		if (q.getNumTasks() > 0) {
			System.err.println("RESOURCE: queue contains: <" + q.getNumTasks() + '>');
			q.update();
		}
		
		resourceMonitor = this;
	}

	private void check(String string, Method m, Object gl) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
//		int c = 0;
//		for (int i = 0; i < up; i++) {
//			if (((Boolean) m.invoke(gl, i))) {
//				c++;
//				up = Math.max(up, i + 1000);
//			}
//		}
//
//		System.err.println("RESOURCE: " + string + " " + c + " / " + up);
	}

}
