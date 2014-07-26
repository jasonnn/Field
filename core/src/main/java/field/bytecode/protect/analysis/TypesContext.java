/*
 * Copyright 2001-2005 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Distributed under the terms of either:
 * - the common development and distribution license (CDDL), v1.0; or
 * - the GNU Lesser General Public License, v2.1 or later
 * $Id: TypesContext.java 2098 2005-07-06 17:53:19Z gbevin $
 */
package field.bytecode.protect.analysis;

import org.objectweb.asm.Type;

import java.util.*;

public class TypesContext implements Cloneable {
	public static final String CAT1_BOOLEAN = "1Z";

	public static final String CAT1_CHAR = "1C";

	public static final String CAT1_FLOAT = "1F";

	public static final String CAT1_BYTE = "1B";

	public static final String CAT1_SHORT = "1S";

	public static final String CAT1_INT = "1I";

	public static final String CAT1_ADDRESS = "1A";

	public static final String CAT2_DOUBLE = "2D";

	public static final String CAT2_LONG = "2J";

	public static final String ARRAY_BOOLEAN = "[Z";

	public static final String ARRAY_CHAR = "[C";

	public static final String ARRAY_FLOAT = "[F";

	public static final String ARRAY_BYTE = "[B";

	public static final String ARRAY_SHORT = "[S";

	public static final String ARRAY_INT = "[I";

	public static final String ARRAY_DOUBLE = "[D";

	public static final String ARRAY_LONG = "[J";

	public static final String NULL = "NULL";

	private Map<Integer, String> mVars = null;

	private Deque<String> mStack = null;

	private int mSort = TypesNode.REGULAR;

	//private String mDebugIndent = null;

	TypesContext() {
		mVars = new HashMap<Integer, String>();
		mStack = new ArrayDeque<String>();
	}

	TypesContext(Map<Integer, String> vars, Deque<String> stack) {
		mVars = vars;
		mStack = stack;
	}

	public Map<Integer, String> getVars() {
//		return mVars;
		
		return new TreeMap<Integer, String>(mVars);
		
	}

	public Deque<String> getStack() {
		return mStack;
	}

	public boolean hasVar(int var) {
		return mVars.containsKey(var);
	}

	public String getVar(int var) {
		return mVars.get(var);
	}

	public void setVar(int var, String type) {
		mVars.put(var, type);
	}

	public int getVarType(int var) {
		String type = getVar(var);
		if (CAT1_INT.equals(type)) {
			return Type.INT;
		} else if (CAT1_FLOAT.equals(type)) {
			return Type.FLOAT;
		} else if (CAT2_LONG.equals(type)) {
			return Type.LONG;
		} else if (CAT2_DOUBLE.equals(type)) {
			return Type.DOUBLE;
		} else {
			return Type.OBJECT;
		}
	}

	public String peek() {
		return mStack.peek();
	}

	public String pop() {
		String result = null;
		if (!mStack.isEmpty()) {
			result = mStack.pop();
		}
		printStack();
		return result;
	}

	public void push(String type) {
		mStack.push(type);
		printStack();
	}

	public Deque<String> getStackCopy() {
        
		return new ArrayDeque<String>(mStack);
	}

	public void cloneVars() {
		mVars = new HashMap<Integer, String>(mVars);
	}

	public void setSort(int type) {
		mSort = type;
	}

	public int getSort() {
		return mSort;
	}

	void printStack() {
	}

//	void setDebugIndent(String debugIndent) {
//		mDebugIndent = debugIndent;
//	}

	TypesContext clone(TypesNode node) {
		TypesContext new_context = new TypesContext(new HashMap<Integer, String>(mVars), getStackCopy());
		new_context.setSort(node.getSort());
		return new_context;
	}

	public Object clone() {
		TypesContext new_context = null;
		try {
			new_context = (TypesContext) super.clone();
		} catch (CloneNotSupportedException e) {
			// this should never happen
			e.printStackTrace();
		}

        assert new_context != null;
        new_context.mVars = new HashMap<Integer, String>(mVars);
		new_context.mStack = getStackCopy();

		return new_context;
	}
}
