package field.core.dispatch;

import field.core.dispatch.IVisualElement.Rect;
import field.core.dispatch.IVisualElement.VisualElementProperty;
import field.core.plugins.drawing.opengl.CachedLine;
import field.core.plugins.drawing.opengl.iLine;
import field.core.plugins.drawing.opengl.iLinearGraphicsContext;
import field.core.windowing.GLComponentWindow;
import field.core.windowing.components.SelectionGroup;
import field.core.windowing.components.iComponent;
import field.launch.IUpdateable;
import field.math.graph.visitors.hint.StandardTraversalHint;
import field.math.graph.visitors.hint.TraversalHint;
import field.math.linalg.Vector4;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public
class DrawGroupMixin extends IVisualElementOverrides.DefaultOverride {

    public static VisualElementProperty<Vector4> groupFillColor = new VisualElementProperty<Vector4>("groupFillColor");
    public static VisualElementProperty<Vector4> groupStrokeColor =
            new VisualElementProperty<Vector4>("groupStrokeColor");

    public static
    void mixin(IVisualElement e) {
        new Mixins().mixInOverride(DrawGroupMixin.class, e);
    }

    transient CachedLine frameLine;

    @Override
    public
    TraversalHint menuItemsFor(IVisualElement source, Map<String, IUpdateable> items) {

        if (source == forElement) {

            final Ref<SelectionGroup<iComponent>> group = new Ref<SelectionGroup<iComponent>>(null);
            new IVisualElementOverrides.MakeDispatchProxy().getOverrideProxyFor(source)
                                                           .getProperty(source, IVisualElement.selectionGroup, group);

            items.put("Groups (" + forElement.getProperty(IVisualElement.name) + ')', null);

            items.put("   \u21e3  unpack selection from this group", new IUpdateable() {

                public
                void update() {
                    List<IVisualElement> parents = (List<IVisualElement>) forElement.getParents();

                    Set<iComponent> selection = group.get().getSelection();

                    for (iComponent c : selection) {
                        IVisualElement ve = c.getVisualElement();
                        if (ve != null) {
                            if (parents.contains(ve) && parents.size() > 1) {
                                ve.removeChild(forElement);
                            }
                        }
                    }

                    frameLine = null;
                    forElement.setProperty(IVisualElement.dirty, true);
                }
            });

            items.put("   \u21e3  put selection into this group", new IUpdateable() {

                public
                void update() {
                    List<IVisualElement> parents = (List<IVisualElement>) forElement.getParents();

                    Set<iComponent> selection = group.get().getSelection();

                    for (iComponent c : selection) {
                        IVisualElement ve = c.getVisualElement();
                        if (ve != null) {
                            if (!parents.contains(ve) && ve != forElement) {
                                ve.addChild(forElement);
                            }
                        }
                    }
                    frameLine = null;
                    forElement.setProperty(IVisualElement.dirty, true);
                }
            });

            items.put("   \u21e3  put selection into this group exclusively", new IUpdateable() {

                public
                void update() {
                    List<IVisualElement> parents = (List<IVisualElement>) forElement.getParents();

                    Set<iComponent> selection = group.get().getSelection();

                    for (iComponent c : selection) {
                        IVisualElement ve = c.getVisualElement();
                        if (ve != null) {
                            if (!parents.contains(ve) && ve != forElement) {
                                List<IVisualElement> cp = new ArrayList<IVisualElement>(ve.getChildren());
                                for (IVisualElement cc : cp) {
                                    ve.removeChild(cc);
                                }
                                ve.addChild(forElement);
                            }
                        }
                    }
                    frameLine = null;
                    forElement.setProperty(IVisualElement.dirty, true);
                }
            });

        }
        else {
            List<IVisualElement> c = (List<IVisualElement>) this.forElement.getParents();
            if (c.contains(source)) {

            }
        }

        return StandardTraversalHint.CONTINUE;
    }

    @Override
    public
    TraversalHint paintNow(IVisualElement source, Rect bounds, boolean visible) {
        if (source == forElement) {

            if (frameLine == null) {
                frameLine = computeFrameLine();
            }

            if (frameLine != null) GLComponentWindow.currentContext.submitLine(frameLine, frameLine.getProperties());
        }
        return super.paintNow(source, bounds, visible);
    }

    @Override
    public
    TraversalHint shouldChangeFrame(IVisualElement source, Rect newFrame, Rect oldFrame, boolean now) {
        if (isChild(source)) {
            frameLine = null;
        }
        return super.shouldChangeFrame(source, newFrame, oldFrame, now);
    }

    private
    boolean isChild(IVisualElement source) {
        return forElement.getParents().contains(source);
    }

    protected
    CachedLine computeFrameLine() {
        List<IVisualElement> q = (List<IVisualElement>) forElement.getParents();

        Rect u = null;
        for (IVisualElement e : q) {
            u = Rect.union(u, e.getFrame(null));
        }

        CachedLine c = new CachedLine();
        iLine in = c.getInput();
        in.moveTo((float) u.x, (float) u.y);
        in.lineTo((float) (u.x + u.w), (float) (u.y));
        in.lineTo((float) (u.x + u.w), (float) (u.y + u.h));
        in.lineTo((float) u.x, (float) (u.y + u.h));
        in.lineTo((float) u.x, (float) u.y);

        Vector4 f = groupFillColor.get(forElement);
        if (f == null) f = new Vector4(0, 0, 0, 0.1);
        Vector4 s = groupStrokeColor.get(forElement);
        if (s == null) s = new Vector4(0, 0, 0, 0.1);

        c.getProperties().put(iLinearGraphicsContext.color, s);
        c.getProperties().put(iLinearGraphicsContext.fillColor, f);
        c.getProperties().put(iLinearGraphicsContext.filled, true);

        return c;
    }

}
