package field.core.plugins.selection;

import field.core.StandardFluidSheet;
import field.core.dispatch.IVisualElement;
import field.core.dispatch.IVisualElement.Rect;
import field.core.plugins.drawing.BasicDrawingPlugin;
import field.core.plugins.drawing.SimpleArrows;
import field.core.plugins.drawing.opengl.CachedLine;
import field.core.plugins.drawing.opengl.LineUtils;
import field.core.plugins.drawing.opengl.iLinearGraphicsContext;
import field.core.plugins.selection.SelectionSetDriver.SelectionSet;
import field.core.plugins.selection.SelectionSetDriver.SelectionSetNode;
import field.core.plugins.selection.SelectionSetDriver.SelectionSetNodeType;
import field.core.plugins.selection.SelectionSetDriver.iSelectionPredicate;
import field.core.windowing.GLComponentWindow;
import field.core.windowing.components.RootComponent;
import field.core.windowing.components.RootComponent.iPaintPeer;
import field.core.windowing.components.SelectionGroup;
import field.core.windowing.components.iComponent;
import field.math.graph.ITopology;
import field.math.linalg.Vector2;
import field.math.linalg.Vector3;
import field.math.linalg.Vector4;
import field.namespace.generic.ReflectionTools;
import field.util.collect.tuple.Pair;

import java.awt.*;
import java.util.*;
import java.util.List;

public
class DrawTopology implements iPaintPeer {

    public static
    enum Compass {
        left(0, 0.5f, -1, 0, 0, -1), right(1, 0.5f, 1, 0, 0, -1), up(0.5f, 0, 0, -1, 1, 0), down(0.5f, 1, 0, 1, 1, 0);

        public final float x;

        public final float y;

        public final float dx;

        public final float dy;

        public Vector3 point;

        public Vector3 normal;

        public Vector3 tangent;

        Compass(float x, float y, float dx, float dy, float tx, float ty) {
            this.x = x;
            this.y = y;
            point = new Vector3(x, y, 0);
            this.dx = dx;
            this.dy = dy;
            normal = new Vector3(dx, dy, 0);
            tangent = new Vector3(tx, ty, 0);
        }
    }

    public static
    class DefaultDrawer implements iTopologyDrawer {
        SimpleArrows a;
        int arrowSize = 10;
        Vector4 color = new Vector4(0, 0, 0, 0.25f);

        public
        List<CachedLine> connect(IVisualElement from, IVisualElement to, boolean isParent) {

            if (a == null) a = new SimpleArrows();

            if (isParent) {
                IVisualElement t = to;
                to = from;
                from = t;
            }

            Rect f1 = from.getFrame(null);
            Rect f2 = to.getFrame(null);

            if (f1 == null || f2 == null) return Collections.EMPTY_LIST;

            Pair<Compass, Compass> x = findMinimumRectangleConnection(f1, f2);

            CachedLine c = new CachedLine();
            Vector3 p1 = f1.relativize(x.left.point);
            Vector3 p2 = f2.relativize(x.right.point);

            c.getInput().moveTo(p1.x, p1.y);
            c.getInput().lineTo(p2.x, p2.y);

            CachedLine a1 = a.arrowForMiddle(c, arrowSize, 0.5f);
            CachedLine a2 = SimpleArrows.circleFor(c, 0, arrowSize / 5f);
            if (isParent) {
                CachedLine cc =
                        new LineUtils().lineAsStroked(c, new BasicStroke(1, 0, 0, 1, new float[]{1, 3}, 0), false);
                c = cc;
                c.getProperties().put(iLinearGraphicsContext.filled, true);
                c.getProperties().put(iLinearGraphicsContext.stroked, true);
            }
            c.getProperties().put(iLinearGraphicsContext.color, color);
            a1.getProperties().put(iLinearGraphicsContext.color, color);
            a2.getProperties().put(iLinearGraphicsContext.color, color);
            a1.getProperties().put(iLinearGraphicsContext.filled, true);
            a2.getProperties().put(iLinearGraphicsContext.filled, true);

            return Arrays.asList(c, a1, a2);
        }

    }

    public
    interface iTopologyDrawer {
        public
        List<CachedLine> connect(IVisualElement from, IVisualElement to, boolean isParent);
    }

    static List<Pair<Compass, Compass>> options = new ArrayList<Pair<Compass, Compass>>();

    static {
        Compass[] c = Compass.values();
        for (Compass c1 : c) {
            for (Compass c2 : c) {
                options.add(new Pair<Compass, Compass>(c1, c2));
            }
        }
    }

    public static
    void addTopologyAsSelectionAxis(final String name,
                                    final IVisualElement root,
                                    final ITopology<IVisualElement> t,
                                    iTopologyDrawer drawer) {
        addTopologyAsSelectionAxis(name,
                                   root,
                                   t,
                                   drawer,
                                   "children",
                                   "parents",
                                   "\u21e2 child \u2014",
                                   "\u21e4 next \u2014");
    }

    public static
    void addTopologyAsSelectionAxis(String name,
                                    IVisualElement root,
                                    ITopology<IVisualElement> top,
                                    iTopologyDrawer drawer,
                                    String cng,
                                    String png,
                                    String cn,
                                    String pn) {
        BasicDrawingPlugin plugin = BasicDrawingPlugin.simpleConstraints_plugin.get(root);
        SelectionSetDriver driver = plugin.getSelectionSetDriver();
        addTopologyAsSelectionAxis(driver, name, root, top, drawer, cng, png, cn, pn);
    }

    public static
    void addTopologyAsSelectionAxis(SelectionSetDriver driver,
                                    final String name,
                                    final IVisualElement root,
                                    final ITopology<IVisualElement> t,
                                    iTopologyDrawer drawer,
                                    final String childrenNameGroup,
                                    final String parentsNameGroup,
                                    final String childName,
                                    final String parentName) {
        final DrawTopology d = new DrawTopology(root);
        d.add(t, drawer);
        d.setSelectedOnly(true);

        driver.addSelectionAxes(name, new SelectionSetDriver.iExtendedSelectionAxis() {

            iPaintPeer peer = new iPaintPeer() {
                public
                void paint(RootComponent inside) {
                    d.paint(inside);
                }
            };

            public
            void constructNodesForSelected(Set<IVisualElement> everything,
                                           IVisualElement oneThning,
                                           SelectionSetNode parent) {
                {
                    List<IVisualElement> c = t.getChildrenOf(oneThning);
                    if (c != null && c.size() > 0) {
                        SelectionSetNode to = parent;
                        if (c.size() > 1) {
                            SelectionSetNode p1 =
                                    new SelectionSetNode(null, childrenNameGroup, SelectionSetNodeType.label);
                            parent.addChild(p1);
                            to = p1;
                        }
                        for (final IVisualElement v : c) {
                            to.addChild(new SelectionSetNode(new SelectionSet(new iSelectionPredicate() {

                                public
                                void begin(Set<IVisualElement> everything,
                                           Set<IVisualElement> currentlySelected,
                                           Set<IVisualElement> previousCache) {
                                }

                                public
                                boolean is(IVisualElement e) {
                                    return e == v;
                                }
                            }, childName + SelectionSetDriver.nameFor(v)), null, SelectionSetNodeType.saved));
                        }
                    }
                }
                {
                    List<IVisualElement> c = t.getParentsOf(oneThning);
                    if (c != null && c.size() > 0) {
                        SelectionSetNode to = parent;
                        if (c.size() > 1) {
                            SelectionSetNode p1 =
                                    new SelectionSetNode(null, parentsNameGroup, SelectionSetNodeType.label);
                            parent.addChild(p1);
                            to = p1;
                        }
                        for (final IVisualElement v : c) {
                            to.addChild(new SelectionSetNode(new SelectionSet(new iSelectionPredicate() {

                                public
                                void begin(Set<IVisualElement> everything,
                                           Set<IVisualElement> currentlySelected,
                                           Set<IVisualElement> previousCache) {
                                }

                                public
                                boolean is(IVisualElement e) {
                                    return e == v;
                                }
                            }, parentName + SelectionSetDriver.nameFor(v)), null, SelectionSetNodeType.saved));
                        }
                    }

                }
            }

            public
            String getName() {
                return name;
            }

            public
            void selectionChanged(Set<IVisualElement> s) {
            }

            public
            void start() {
                RootComponent g = IVisualElement.rootComponent.get(root);
                g.addPaintPeer(peer);
            }

            public
            void stop() {
                RootComponent g = IVisualElement.rootComponent.get(root);
                g.removePaintPeer(peer);
            }
        });

    }

    public static
    Pair<Compass, Compass> findMinimumRectangleConnection(final Rect start, final Rect end) {

        Pair<Compass, Compass> option = ReflectionTools.argMin(options, new Object() {
            public
            float distance(Pair<Compass, Compass> option) {
                return start.relativize(option.left.point).distanceFrom(end.relativize(option.right.point));
            }
        });

        return option;
    }

    public static
    Compass findMinimumRectangleConnection(final Rect start, final Vector2 point) {

        Pair<Compass, Compass> option = ReflectionTools.argMin(options, new Object() {
            public
            float distance(Pair<Compass, Compass> option) {
                return start.relativize(option.left.point).distanceFrom(point.toVector3());
            }
        });

        return option.left;
    }

    public static
    <T> ITopology<T> topoologyFromList(final List<T> t) {
        return new ITopology<T>() {
            public
            java.util.List<T> getChildrenOf(T of) {
                int a = t.indexOf(of);
                if (a == -1 || a == t.size() - 1) return Collections.EMPTY_LIST;
                return Collections.singletonList(t.get(a + 1));
            }

            public
            java.util.List<T> getParentsOf(T of) {
                int a = t.indexOf(of);
                if (a == -1 || a == 0) return Collections.EMPTY_LIST;
                return Collections.singletonList(t.get(a - 1));
            }
        };
    }

    private final IVisualElement root;

    private final GLComponentWindow window;

    List<Pair<ITopology<IVisualElement>, iTopologyDrawer>> toDraw =
            new ArrayList<Pair<ITopology<IVisualElement>, iTopologyDrawer>>();

    boolean selectedOnly = false;

    public
    DrawTopology(IVisualElement root) {
        this.root = root;

        window = IVisualElement.enclosingFrame.get(root);
    }

    public
    void add(ITopology<IVisualElement> t, iTopologyDrawer drawer) {
        toDraw.add(new Pair<ITopology<IVisualElement>, iTopologyDrawer>(t, drawer));
    }

    public
    void paint(RootComponent inside) {

        List<IVisualElement> all = null;
        if (selectedOnly) {
            SelectionGroup<iComponent> selection = IVisualElement.selectionGroup.get(root);
            all = new ArrayList<IVisualElement>();
            for (iComponent c : selection.getSelection()) {
                IVisualElement ve = c.getVisualElement();
                if (ve != null) all.add(ve);
            }
        }
        else {
            all = StandardFluidSheet.allVisualElements(root);
        }

        List<CachedLine> cl = new ArrayList<CachedLine>();

        for (IVisualElement v : all) {
            for (Pair<ITopology<IVisualElement>, iTopologyDrawer> p : toDraw) {
                {
                    List<IVisualElement> c = p.left.getChildrenOf(v);
                    for (IVisualElement vv : c) {
                        List<CachedLine> cls = p.right.connect(v, vv, false);
                        if (cls != null) cl.addAll(cls);
                    }
                }
                {
                    List<IVisualElement> c = p.left.getParentsOf(v);
                    for (IVisualElement vv : c) {
                        List<CachedLine> cls = p.right.connect(v, vv, true);
                        if (cls != null) cl.addAll(cls);
                    }
                }
            }
        }

        iLinearGraphicsContext c = GLComponentWindow.fastContext == null
                                   ? GLComponentWindow.currentContext
                                   : GLComponentWindow.fastContext;
        c = GLComponentWindow.currentContext;

        for (CachedLine cc : cl) {
            c.submitLine(cc, cc.getProperties());
        }

//		if (window.fastContext != null) {
//			window.getOverlayAnimationManager().requestRepaint();
//		} else {
//			window.requestRepaint();
//		}
    }

    public
    DrawTopology setSelectedOnly(boolean selectedOnly) {
        this.selectedOnly = selectedOnly;
        return this;
    }

}
