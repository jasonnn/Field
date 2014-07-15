package field.core.plugins.constrain.constraints;

import field.core.dispatch.iVisualElement;
import field.core.dispatch.iVisualElement.Rect;
import field.core.plugins.constrain.BaseConstraintOverrides;
import field.core.plugins.constrain.ComplexConstraints;
import field.core.plugins.constrain.ComplexConstraints.VariablesForRect;
import field.core.plugins.constrain.cassowary.ClConstraint;
import field.core.plugins.constrain.cassowary.ClLinearEquation;
import field.core.plugins.constrain.cassowary.ClLinearExpression;
import field.core.plugins.drawing.opengl.CachedLine;
import field.core.plugins.drawing.opengl.LineUtils;
import field.core.plugins.drawing.opengl.iLine;
import field.core.plugins.drawing.opengl.iLinearGraphicsContext;
import field.core.windowing.GLComponentWindow;
import field.core.windowing.components.iComponent;
import field.math.graph.GraphNodeSearching.VisitCode;
import field.math.linalg.Vector2;
import field.math.linalg.Vector3;
import field.math.linalg.Vector4;
import org.eclipse.swt.widgets.Event;

import java.util.Map;

public class VerticalLeftToLeftConstraint extends BaseConstraintOverrides {

	private CachedLine geometry;

	@Override
	public VisitCode isHit(iVisualElement source, Event event, Ref<Boolean> is) {
		if (source == forElement) {
			if (geometry != null) {
				boolean m = LineUtils.hitTest(geometry, new Vector2(event.x, event.y), 15);
				if (m)
					is.set(true);
				else
					is.set(false);
			}
		}
		return VisitCode.cont;
	}

	private Boolean isSelected() {
		iComponent v = forElement.getProperty(iVisualElement.localView);
		if (v != null)
			return v.isSelected();
		return false;
	}

	@Override
	protected ClConstraint createConstraint(Map<String, iVisualElement> property) {

		ComplexConstraints cc = getComplexConstraintsPlugin();
		assert cc != null;
		if (cc == null)
			return null;
		iVisualElement left = property.get("left");
		iVisualElement right = property.get("right");

		assert left != null : property;
		assert right != null : property;
		if (left == null || right == null)
			return null;
		VariablesForRect vLeft = cc.getVariablesFor(left);
		VariablesForRect vRight = cc.getVariablesFor(right);

		return createConstraint(vLeft, vRight);
	}

	protected ClLinearEquation createConstraint(VariablesForRect vLeft, VariablesForRect vRight) {
		return new ClLinearEquation(vLeft.variableX, new ClLinearExpression(vRight.variableX));
	}

	@Override
	protected void paint(Rect bounds, boolean visible) {
		ComplexConstraints cc = getComplexConstraintsPlugin();
		assert cc != null;
		if (cc == null)
			return;

		CachedLine cl = new CachedLine();
		iLine line = cl.getInput();

		geometry = cl;

		Map<String, iVisualElement> parameters = getConstraintParameters();
		iVisualElement left = parameters.get("left");
		iVisualElement right = parameters.get("right");
		assert left != null : parameters;
		assert right != null : parameters;

		if (left == null || right == null)
			return;
		VariablesForRect vLeft = cc.getVariablesFor(left);
		VariablesForRect vRight = cc.getVariablesFor(right);

		Vector3 p1 = point1(vLeft);
		Vector3 p2 = point2(vRight);

		line.moveTo(p1.x, p1.y);
		line.lineTo(p1.x - 5, p1.y + (p2.y - p1.y) * 0.1f);
		line.lineTo(p1.x - 5, p1.y + (p2.y - p1.y) * 0.9f);
		line.lineTo(p2.x, p2.y);
		line.lineTo(p1.x + 5, p1.y + (p2.y - p1.y) * 0.9f);
		line.lineTo(p1.x + 5, p1.y + (p2.y - p1.y) * 0.1f);
		line.lineTo(p1.x, p1.y);

		cl.getProperties().put(iLinearGraphicsContext.color, new Vector4(0.25, 0, 0, 0.15f));
		cl.getProperties().put(iLinearGraphicsContext.thickness, 0.5f);
		cl.getProperties().put(iLinearGraphicsContext.filled, true);

		cl.getProperties().put(iLinearGraphicsContext.shouldHighlight, isSelected());

		iLinearGraphicsContext context = GLComponentWindow.currentContext;
		context.submitLine(cl, cl.getProperties());

	}

	protected Vector3 point1(VariablesForRect vLeft) {
		return new Vector3(vLeft.variableX.value(), vLeft.variableY.value() + vLeft.variableH.value() / 2, 0);
	}

	protected Vector3 point2(VariablesForRect vRight) {
		return new Vector3(vRight.variableX.value(), vRight.variableY.value() + vRight.variableH.value() / 2, 0);
	}

}