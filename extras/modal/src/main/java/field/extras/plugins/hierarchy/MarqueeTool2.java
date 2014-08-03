package field.extras.plugins.hierarchy;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import field.core.StandardFluidSheet;
import field.core.dispatch.Rect;
import field.core.dispatch.VisualElement;
import field.core.dispatch.IVisualElement;
import field.core.plugins.drawing.opengl.CachedLine;
import field.core.plugins.drawing.opengl.iLinearGraphicsContext;
import field.core.windowing.components.SelectionGroup;
import field.core.windowing.components.iComponent;
import field.extras.plugins.hierarchy.HierarchyPlugin.Mode;
import field.extras.plugins.hierarchy.HierarchyPlugin.iEventHandler;
import field.launch.IUpdateable;
import field.math.linalg.Vector2;
import field.math.linalg.Vector4;

public class MarqueeTool2 implements iEventHandler {

	private Mode mode;
	private Vector2 transitionAt;
	private final HierarchyPlugin plugin;
	private final IVisualElement root;

	public MarqueeTool2(HierarchyPlugin plugin, IVisualElement root) {
		this.plugin = plugin;
		this.root = root;

		plugin.addTool("icons/marq_16x16.png", this, new IUpdateable() {
			public void update() {
			}
		}, "Marquee Tool", "Marquee Tool", "The Marquee tool allows you to select multiple elements with a single drag");
	}

	public iEventHandler idle() {
		if (marque != null) {
			plugin.fastContext.submitLine(marque, marque.getProperties());

			//IVisualElement.enclosingFrame.get(root).getOverlayAnimationManager().requestRepaint();
		}
		return this;
	}

	public iEventHandler key(char character, int modifiers) {
		return this;
	}

	Set<IVisualElement> currentSelectedSet = new HashSet<IVisualElement>();
	private List<IVisualElement> allElements;
	private CachedLine marque;

	public iEventHandler mouse(Vector2 at, int buttons) {
		if (mode == Mode.drag) {
			Rect r = new Rect(Math.min(at.x, transitionAt.x), Math.min(at.y, transitionAt.y), Math.max(at.x, transitionAt.x) - Math.min(at.x, transitionAt.x), Math.max(at.y, transitionAt.y) - Math.min(at.y, transitionAt.y));

			Rect r2 = new Rect();

			for (IVisualElement e : allElements) {
				e.getFrame(r2);

				if (r2.overlaps(r)) {
					if (!currentSelectedSet.contains(e)) {
						SelectionGroup<iComponent> group = VisualElement.selectionGroup.get(e);
						iComponent localView = VisualElement.localView.get(e);
						if (localView != null) {
							group.addToSelection(localView);
							currentSelectedSet.add(e);
							localView.setSelected(true);
						}
					}
				} else {
					if (currentSelectedSet.contains(e)) {
						SelectionGroup<iComponent> group = VisualElement.selectionGroup.get(e);
						iComponent localView = VisualElement.localView.get(e);
						if (localView != null) {
							group.removeFromSelection(localView);
							localView.setSelected(false);
						}
						currentSelectedSet.remove(e);
					}
				}

			}
			marque = new CachedLine();
			marque.getInput().moveTo((float) r.x, (float) r.y);
			marque.getInput().setPointAttribute(iLinearGraphicsContext.fillColor_v, new Vector4(0,0,0,0.1f));
			marque.getInput().lineTo((float) (r.x + r.w), (float) r.y);
			marque.getInput().setPointAttribute(iLinearGraphicsContext.fillColor_v, new Vector4(0,0,0,0.13f));
			marque.getInput().lineTo((float) (r.x + r.w), (float) (r.y + r.h));
			marque.getInput().setPointAttribute(iLinearGraphicsContext.fillColor_v, new Vector4(0,0,0,0.14f));
			marque.getInput().lineTo((float) (r.x), (float) (r.y + r.h));
			marque.getInput().setPointAttribute(iLinearGraphicsContext.fillColor_v, new Vector4(0,0,0,0.12f));
			marque.getInput().lineTo((float) (r.x), (float) (r.y));
			marque.getInput().setPointAttribute(iLinearGraphicsContext.fillColor_v, new Vector4(0,0,0,0.1f));
			

			marque.getProperties().put(iLinearGraphicsContext.color, new Vector4(1,1,1,0.3f));
			marque.getProperties().put(iLinearGraphicsContext.filled, true);

			
			plugin.fastContext.submitLine(marque, marque.getProperties());
		}
		else
			marque = null;
		
		IVisualElement.enclosingFrame.get(root).requestRepaint();
		
		return this;
	}

	public void paintNow() {
	}
	
	public iEventHandler transition(Mode from, Mode to, Vector2 at, int buttons) {
		allElements = StandardFluidSheet.allVisualElements(root);
		mode = to;
		transitionAt = at;
		if (mode == Mode.up)
		{
			marque = null;
			IVisualElement.enclosingFrame.get(root).requestRepaint();
		}
		
		return this;
	}

}
