package field.core.plugins.drawing.text;

import field.core.plugins.drawing.opengl.CachedLine;
import field.math.linalg.Vector2;

import java.util.List;


public class TextBrackets {

	public static
    enum BracketElementType {
        line, foot, corner, fixedExternalPosition
    }

	public static
    class BracketElement {
		BracketElementType type;

		Vector2 start;

		Vector2 end;
	}

	public static
    class BracketConnection {
		BracketElementType from;

		float alphaFrom;

		BracketElementType to;

		float alphaTo;
	}

	public static
    interface iBracketFactory {
		public List<BracketElement> getBracketFor(AdvancedTextToCachedLine layout);

		public Vector2 getPositionForLink(AdvancedTextToCachedLine layout);

	}

	public static
    interface iBracketConnectionFactory {
		public BracketConnection getConnectionFor(AdvancedTextToCachedLine leftLayout, List<BracketElement> left, AdvancedTextToCachedLine rightLayout, List<BracketElement> right);
	}

	public static
    interface iBracketConnectionEvaluation {
		public float score(AdvancedTextToCachedLine leftLayout, List<BracketElement> left, AdvancedTextToCachedLine rightLayout, List<BracketElement> right, BracketConnection connection);
	}

	public static
    CachedLine drawBracketElements(List<BracketElement> elements) {
		CachedLine cl = new CachedLine();

		boolean f = true;

		for (BracketElement e : elements) {
			if (f)
				cl.getInput().moveTo(e.start.x, e.start.y);
			else
				cl.getInput().lineTo(e.start.x, e.start.y);
			cl.getInput().lineTo(e.end.x, e.end.y);
			f = false;
		}
		return cl;
	}

	public static
    CachedLine drawBracketElementsNoFeet(List<BracketElement> elements) {
		CachedLine cl = new CachedLine();

		boolean f = true;

		for (BracketElement e : elements) {
			if (e.type == BracketElementType.line) {
				if (f)
					cl.getInput().moveTo(e.start.x, e.start.y);
				cl.getInput().lineTo(e.end.x, e.end.y);
				f = false;
			}
		}
		return cl;
	}

}
