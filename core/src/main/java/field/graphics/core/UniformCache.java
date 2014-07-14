package field.graphics.core;

import java.util.HashMap;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;

public class UniformCache {

	public HashMap<String, Integer> id = new HashMap<String, Integer>();
	public HashMap<String, Object> value = new HashMap<String, Object>();

	boolean lastWasNew = false;

	public int find(Object gl, int program, String name) {
		name = BasicGLSLangProgram.demungeArrayName(name);
		lastWasNew = false;

		Integer n = id.get(name);
		if (n != null /* && n>-1 */)
			return n;

		n = glGetUniformLocation(program, name);
		id.put(name, n);

		lastWasNew = true;

		return n;
	}

	public void set(String name, Object v) {
		name = BasicGLSLangProgram.demungeArrayName(name);
		value.put(name, v);
	}

	public Object get(String name) {
		name = BasicGLSLangProgram.demungeArrayName(name);
		return value.get(name);
	}

	public void clear() {
		id.clear();
	}

}
