package field.extras.plugins.hierarchy;

import java.awt.Font;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import field.core.dispatch.IVisualElement;
import field.core.dispatch.Rect;
import field.core.dispatch.override.IVisualElementOverrides;
import field.launch.IUpdateable;
import field.math.graph.visitors.GraphNodeSearching;
import field.math.graph.visitors.hint.StandardTraversalHint;
import field.math.graph.visitors.hint.TraversalHint;
import field.util.collect.tuple.Pair;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;

import field.core.Constants;
import field.core.StandardFluidSheet;
import field.core.dispatch.VisualElement;

import field.core.plugins.drawing.SimpleArrows;
import field.core.plugins.drawing.opengl.CachedLine;
import field.core.plugins.drawing.opengl.iLinearGraphicsContext;
import field.core.plugins.selection.DrawTopology;
import field.core.plugins.selection.DrawTopology.Compass;
import field.core.windowing.GLComponentWindow;
import field.core.windowing.components.iComponent;
import field.core.windowing.overlay.OverlayAnimationManager;
import field.extras.plugins.hierarchy.HierarchyPlugin.Mode;
import field.extras.plugins.hierarchy.HierarchyPlugin.iEventHandler;
import field.launch.Launcher;

import field.math.linalg.Vector2;
import field.math.linalg.Vector3;
import field.math.linalg.Vector4;


public class HierarchyHandler {

	public class Hover implements iEventHandler {

		Rect highlightRect = null;

		public Hover() {
			all = StandardFluidSheet.allVisualElements(root);
			wasOver = null;
		}

		public iEventHandler idle() {

			// ;//;//System.out.println(" inside idle <"+highlightRect+">");

			if (highlightRect != null) {
				Rect h = new Rect(0, 0, 0, 0).setValue(highlightRect);
				h = h.insetAbsolute(-5);
				CachedLine l = new CachedLine();
				l.getInput().moveTo((float) h.x, (float) h.y);
				l.getInput().lineTo((float) (h.x + h.w), (float) (h.y));
				l.getInput().lineTo((float) (h.x + h.w), (float) (h.y + h.h));
				l.getInput().lineTo((float) (h.x), (float) (h.y + h.h));
				l.getInput().lineTo((float) (h.x), (float) (h.y));

				float sin = (float) Math.sin(System.currentTimeMillis() / 200.0);

				l.getProperties().put(iLinearGraphicsContext.color, new Vector4(0.25, 0, 0, 0.25f));
				l.getProperties().put(iLinearGraphicsContext.filled, true);

				plugin.fastContext.submitLine(l, l.getProperties());
				GLComponentWindow.getCurrentWindow(null).requestRepaint();
			}
			return this;
		}

		public iEventHandler key(char character, int modifiers) {
			return this;
		}

		public iEventHandler mouse(Vector2 at, int buttons) {
			IVisualElement over = over(at);
			;//;//System.out.println(" hover mouse <" + over + ">");
			if (over != null) {

				if (wasOver == null) {
					// change cursor, set highlight
					// TODO: 64 \u2014\u2014confront cusor
					// setting in pure java
					// NSCursor.openHandCursor().set();
					GLComponentWindow.getCurrentWindow(null).getCanvas().setCursor(Launcher.display.getSystemCursor(SWT.CURSOR_HAND));
				}
				wasOver = over;
				highlightRect = over.getFrame(null);
			} else if (over == null && wasOver != null) {
				wasOver = null;
				// change cursor, clear highlight
				// TODO: 64 \u2014\u2014confront cusor setting
				// in pure java
				// NSCursor.arrowCursor().set();

				GLComponentWindow.getCurrentWindow(null).getCanvas().setCursor(Launcher.display.getSystemCursor(SWT.CURSOR_ARROW));

				highlightRect = null;
				GLComponentWindow.getCurrentWindow(null).requestRepaint();
			}
			return this;
		}

		public iEventHandler transition(Mode from, Mode to, Vector2 at, int buttons) {
			;//;//System.out.println(" hover <" + from + " -> " + to + "> <" + wasOver + ">");
			if (to == Mode.drag || to == Mode.clicked) {
				if (wasOver != null) {
					GLComponentWindow.getCurrentWindow(null).requestRepaint();
					return new Start(wasOver);
				}
			}
			return this;
		}

		public void paintNow() {
		}
	}

	public class Start implements iEventHandler {

		private final IVisualElement origin;
		private Vector2 mouseAt;
		private int buttons;
		// Arrows a = new Arrows();
		int arrowSize = 35;

		public Start(IVisualElement wasOver) {
			origin = wasOver;
			// TODO: 64 \u2014\u2014confront cusor setting in pure
			// java
			// NSCursor.arrowCursor().set();
			GLComponentWindow.getCurrentWindow(null).getCanvas().setCursor(Launcher.display.getSystemCursor(SWT.CURSOR_ARROW));
		}

		public iEventHandler idle() {
			GLComponentWindow.getCurrentWindow(null).requestRepaint();
			{
				Rect highlightRect = origin.getFrame(null);
				Rect h = new Rect(0, 0, 0, 0).setValue(highlightRect);
				h = h.insetAbsolute(-5);
				CachedLine l = new CachedLine();
				l.getInput().moveTo((float) h.x, (float) h.y);
				l.getInput().lineTo((float) (h.x + h.w), (float) (h.y));
				l.getInput().lineTo((float) (h.x + h.w), (float) (h.y + h.h));
				l.getInput().lineTo((float) (h.x), (float) (h.y + h.h));
				l.getInput().lineTo((float) (h.x), (float) (h.y));

				float sin = (float) Math.sin(System.currentTimeMillis() / 200.0);

				l.getProperties().put(iLinearGraphicsContext.color, new Vector4(0.25 + sin, 0, 0, 0.25f));
				l.getProperties().put(iLinearGraphicsContext.filled, true);

				plugin.fastContext.submitLine(l, l.getProperties());
			}

			if (target != null) {
				Rect highlightRect = target.getFrame(null);
				Rect h = new Rect(0, 0, 0, 0).setValue(highlightRect);
				h = h.insetAbsolute(-5);
				CachedLine l = new CachedLine();
				l.getInput().moveTo((float) h.x, (float) h.y);
				l.getInput().lineTo((float) (h.x + h.w), (float) (h.y));
				l.getInput().lineTo((float) (h.x + h.w), (float) (h.y + h.h));
				l.getInput().lineTo((float) (h.x), (float) (h.y + h.h));
				l.getInput().lineTo((float) (h.x), (float) (h.y));

				float sin = (float) Math.sin(System.currentTimeMillis() / 200.0) * 0.25f;

				l.getProperties().put(iLinearGraphicsContext.color, new Vector4(0.25 + sin, 0, 0, 0.25f));
				l.getProperties().put(iLinearGraphicsContext.filled, true);

				plugin.fastContext.submitLine(l, l.getProperties());
				GLComponentWindow.getCurrentWindow(null).requestRepaint();

				Pair<Compass, Compass> to = DrawTopology.findMinimumRectangleConnection(origin.getFrame(null), target.getFrame(null));
				Vector3 m1 = origin.getFrame(null).relativize(to.left.point);
				Vector3 m2 = target.getFrame(null).relativize(to.right.point);

				CachedLine c = new CachedLine();

				c.getInput().moveTo(m1.x, m1.y);
				c.getInput().lineTo(m2.x, m2.y);
				Vector4 color = new Vector4(0.25 + sin, 0, 0, 0.25f);

				CachedLine a = new SimpleArrows().arrowForEnd(c, 2, 8, 0.5f);
				a.getProperties().put(iLinearGraphicsContext.thickness, 1.0f);
				a.getProperties().put(iLinearGraphicsContext.filled, true);
				a.getProperties().put(iLinearGraphicsContext.color, new Vector4(0, 0, 0.0, 0.5f));
				plugin.fastContext.submitLine(a, a.getProperties());


				CachedLine cText = new CachedLine();
				cText.getInput().moveTo(m2.x + 20, m2.y - 20);

				String message = getMessage(origin, target, buttons);

				cText.getInput().setPointAttribute(iLinearGraphicsContext.text_v, message + " ");
				cText.getInput().setPointAttribute(iLinearGraphicsContext.font_v, new Font(Constants.defaultFont, 0, 13));
				cText.getProperties().put(iLinearGraphicsContext.containsText, true);
				cText.getProperties().put(iLinearGraphicsContext.color, new Vector4(0.25f, 0, 0, 0.85f));

				plugin.fastContext.submitLine(c, c.getProperties());
				// plugin.fastContext.submitLine(a1,
				// a1.getProperties());
				// plugin.fastContext.submitLine(a2,
				// a2.getProperties());
				plugin.fastContext.submitLine(cText, cText.getProperties());
				GLComponentWindow.getCurrentWindow(null).requestRepaint();
				GLComponentWindow.getCurrentWindow(null).requestRepaint();
			} else if (mouseAt != null) {
				Compass to = DrawTopology.findMinimumRectangleConnection(origin.getFrame(null), mouseAt);
				Vector3 m = origin.getFrame(null).relativize(to.point);

				CachedLine c = new CachedLine();

				c.getInput().moveTo(m.x, m.y);
				c.getInput().lineTo(mouseAt.x, mouseAt.y);
				float sin = (float) Math.sin(System.currentTimeMillis() / 200.0);
				Vector4 color = new Vector4(0.25 + sin, 0, 0, 0.25f);
				// CachedLine a1 = a.getArrowForMiddle(c,
				// arrowSize, 3);
				// CachedLine a2 = a.getArrowForStart(c,
				// arrowSize / 2, 10);
				c.getProperties().put(iLinearGraphicsContext.color, color);
				// a1.getProperties().put(iLinearGraphicsContext.color,
				// color);
				// a2.getProperties().put(iLinearGraphicsContext.color,
				// color);
				// a1.getProperties().put(iLinearGraphicsContext.filled,
				// true);
				// a2.getProperties().put(iLinearGraphicsContext.filled,
				// true);

				plugin.fastContext.submitLine(c, c.getProperties());
				// plugin.fastContext.submitLine(a1,
				// a1.getProperties());
				// plugin.fastContext.submitLine(a2,
				// a2.getProperties());
				GLComponentWindow.getCurrentWindow(null).requestRepaint();

			}

			return this;
		}

		public iEventHandler key(char character, int modifiers) {
			return this;
		}

		public iEventHandler mouse(Vector2 at, int buttons) {
			IVisualElement over = over(at);
			this.buttons = buttons;
			;//;//System.out.println(" start mouse <" + at + "> <" + over + "> <" + origin + ">");
			if (over != null && over != origin) {
				target = over;
				// draw line, change pointer
				// TODO: 64 \u2014\u2014confront cusor setting
				// in pure java
				// NSCursor.closedHandCursor().set();
				GLComponentWindow.getCurrentWindow(null).getCanvas().setCursor(Launcher.display.getSystemCursor(SWT.CURSOR_HAND));
			} else {
				// TODO: 64 \u2014\u2014confront cusor setting
				// in pure java
				// NSCursor.arrowCursor().set();
				GLComponentWindow.getCurrentWindow(null).getCanvas().setCursor(Launcher.display.getSystemCursor(SWT.CURSOR_ARROW));

				target = null;
			}
			mouseAt = at;
			return this;
		}

		public iEventHandler transition(Mode from, Mode to, Vector2 at, int buttons) {
			;//;//System.out.println(" start transition <" + from + " -> " + to + ">");
			this.buttons = buttons;
			if (to == Mode.up || to == Mode.clicked) {
				if (target != null) {
					HierarchyHandler.this.finalizeConnection(origin, target, buttons);

					// TODO: 64 \u2014\u2014confront cusor
					// setting in pure java
					// NSCursor.arrowCursor().set();
					GLComponentWindow.getCurrentWindow(null).getCanvas().setCursor(Launcher.display.getSystemCursor(SWT.CURSOR_ARROW));

				}
				Hover h = new Hover();
				h.mouse(at, buttons);
				GLComponentWindow.getCurrentWindow(null).requestRepaint();
				return h;
			}
			return this;
		}

		public void paintNow() {
		}

	}

	public List<IVisualElement> all;

	public IVisualElement target;

	protected final IVisualElement root;

	IVisualElement wasOver = null;

	protected HierarchyPlugin plugin;

	public HierarchyHandler(IVisualElement root) {
		this.root = root;
	}

	public void finalizeConnection(IVisualElement origin, IVisualElement target, int buttons) {

		;//;//System.out.println(" finalizing conection <" + origin + " " + target + " " + buttons + ">");

		List<IVisualElement> c1 = origin.getChildren();
		List<IVisualElement> c2 = target.getChildren();

		if (buttons == MouseEvent.BUTTON1) {
			if (c1.contains(target)) {
				OverlayAnimationManager.notifyAsText(root, IVisualElement.name.get(origin) + " already connected to " + IVisualElement.name.get(target), null);
			} else {
				((VisualElement) origin).addChild(target);
				OverlayAnimationManager.notifyAsText(root, IVisualElement.name.get(origin) + " will now delegate to " + IVisualElement.name.get(target), null);
			}
		} else if (buttons == MouseEvent.BUTTON3) {
			if (c1.contains(target)) {
				if (canDisconnect(origin, target)) {
					((VisualElement) origin).removeChild(target);
					OverlayAnimationManager.notifyAsText(root, IVisualElement.name.get(origin) + " already connected to " + IVisualElement.name.get(target), null);
				} else {
					((VisualElement) origin).removeChild(target);
					((VisualElement) origin).addChild(root);
					OverlayAnimationManager.notifyAsText(root, IVisualElement.name.get(origin) + " will now delegate to root directly", null);
				}
			} else {
				OverlayAnimationManager.notifyAsText(root, IVisualElement.name.get(origin) + " will no longer delegate to " + IVisualElement.name.get(target), null);
			}
		} else if (buttons == MouseEvent.BUTTON2) {
			if (c1.contains(target)) {
				if (c1.size() > 1) {
					for (IVisualElement cc : new ArrayList<IVisualElement>(c1)) {
						if (cc != target)
							((VisualElement) origin).removeChild(cc);
					}
					OverlayAnimationManager.notifyAsText(root, IVisualElement.name.get(origin) + " will now only delegates to " + IVisualElement.name.get(target), null);
				} else {
					OverlayAnimationManager.notifyAsText(root, IVisualElement.name.get(origin) + " already only delegates to " + IVisualElement.name.get(target), null);
				}
			} else {
				for (IVisualElement cc : new ArrayList<IVisualElement>(c1)) {
					((VisualElement) origin).removeChild(cc);
				}
				((VisualElement) origin).addChild(target);

				OverlayAnimationManager.notifyAsText(root, IVisualElement.name.get(origin) + " will now only delegates to " + IVisualElement.name.get(target), null);
			}
		}

		IVisualElementOverrides.topology.begin(target);
		Rect tf = target.getFrame(null);
		tf.x += 1;
		IVisualElementOverrides.forward.shouldChangeFrame.shouldChangeFrame(target, tf, target.getFrame(null), true);
		IVisualElementOverrides.topology.end(target);

		IVisualElementOverrides.topology.begin(origin);
		Rect of = origin.getFrame(null);
		of.x += 1;
		IVisualElementOverrides.forward.shouldChangeFrame.shouldChangeFrame(origin, of, origin.getFrame(null), true);
		IVisualElementOverrides.topology.end(origin);

		GLComponentWindow.getCurrentWindow(null).requestRepaint();

	}

	public String getMessage(IVisualElement origin, IVisualElement target, int buttons) {
		List<IVisualElement> c1 = origin.getChildren();
		List<IVisualElement> c2 = target.getChildren();

		String no = IVisualElement.name.get(origin);
		String nt = IVisualElement.name.get(target);
		if (buttons == MouseEvent.BUTTON1) {
			if (c1.contains(target)) {
				return "\u2014\u2014 already connected \u2014\u2014";
			} else {
				return no + " will delegate to " + nt;
			}
		} else if (buttons == MouseEvent.BUTTON3) {
			if (c1.contains(target)) {
				if (canDisconnect(origin, target)) {
					return "will disconnect " + no + " from " + nt;
				} else {
					return "will disconnect " + no + " from " + nt + " and connect to root";
				}
			} else {
				return "\u2014\u2014 " + no + " will no longer connect to " + nt + " \u2014\u2014";
			}
		} else if (buttons == MouseEvent.BUTTON2) {
			if (c1.contains(target)) {
				if (c1.size() > 1) {
					return no + " will delegate exclusively to " + nt;
				} else {
					return no + " already delegates exclusively to " + nt;
				}
			} else {
				return no + " will delegate exclusively to " + nt;
			}
		}
		return "\u2014\u2014 error ? \u2014\u2014";
	}

	public void install(String icon, HierarchyPlugin h, String info, String name, String description) {
		h.addTool(icon, new Hover(), new IUpdateable() {

			public void update() {
				all = StandardFluidSheet.allVisualElements(root);
			}
		}, info, name, description);
		plugin = h;
	}

	public void install(HierarchyPlugin h) {
		h.addTool("icons/fork_16x16.png", new Hover(), new IUpdateable() {

			public void update() {
				all = StandardFluidSheet.allVisualElements(root);
			}
		}, "Modify dispatch hierarchy tool", "Modify dispatch", "Modify dispatch tool (D)\n" + "========================\n" + "\n" + "This tool manipulates the dispatch graph of elements. Specifically you can use this tool to create new connections between elements in the *dispatch graph*. Drag between parent and child to create a new connection (you can delete these later by selecting them). After a connection has been made `_self.something` in the parent element will be visible in the child. To quickly select this tool without going to the trouble of mousing down to its toolbar, hold down the D key.\n" + "");

		plugin = h;

	}

	public void installNow(HierarchyPlugin h) {
		h.setCurrentHandler(new Hover());
	}

	public IVisualElement over(Vector2 at) {

		Rect f = new Rect(0, 0, 0, 0);
		float d = Float.NEGATIVE_INFINITY;
		IVisualElement best = null;

		GLComponentWindow r = IVisualElement.enclosingFrame.get(root);

		// MouseEvent ev = new MouseEvent(r.getFrame(),
		// MouseEvent.MOUSE_MOVED, 0, 0, (int)at.x, (int)at.y,
		// (int)at.x, (int)at.y, 0, false, 1);

		Event ev = new Event() {
		};
		ev.x = (int) at.x;
		ev.y = (int) at.y;
		ev.type = SWT.MouseMove;
		ev.button = 1;
		ev.stateMask = 0;

		for (IVisualElement e : all) {
			// e.getFrame(f);
			// if (f.isInside(at)) {
			// float dd = (float) (at.distanceFrom(f.midpoint2()) *
			// (f.w + f.h));
			// if (dd < d) {
			// best = e;
			// d = dd;
			// }
			// }
			iComponent v = e.getProperty(IVisualElement.localView);
			if (v != null) {
				float dd = v.isHit(ev);
				if (dd > d) {
					best = e;
					d = dd;
				}
			}
		}
		return best;
	}

	private boolean canDisconnect(IVisualElement p, IVisualElement c) {
		((VisualElement) p).removeChild(c);
		{
			final HashSet<IVisualElement> visited = new HashSet<IVisualElement>();
			new GraphNodeSearching.GraphNodeVisitor_depthFirst<IVisualElement>(true) {
				@Override
				protected
                TraversalHint visit(IVisualElement n) {
					visited.add(n);
					return StandardTraversalHint.CONTINUE;
				}

			}.apply(p);
			if (!visited.contains(root)) {
				((VisualElement) p).addChild(c);
				return false;
			}
		}
		{
			final HashSet<IVisualElement> visited = new HashSet<IVisualElement>();
			new GraphNodeSearching.GraphNodeVisitor_depthFirst<IVisualElement>(true) {
				@Override
				protected TraversalHint visit(IVisualElement n) {
					visited.add(n);
					return StandardTraversalHint.CONTINUE;
				}

			}.apply(c);
			if (!visited.contains(root)) {
				((VisualElement) p).addChild(c);
				return false;
			}
		}
		return true;
	}

}
