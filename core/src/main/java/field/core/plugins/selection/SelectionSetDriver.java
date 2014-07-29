package field.core.plugins.selection;

import field.bytecode.protect.Woven;
import field.bytecode.protect.annotations.DispatchOverTopology;
import field.bytecode.protect.annotations.NextUpdate;
import field.bytecode.protect.dispatch.Cont;
import field.bytecode.protect.dispatch.ReturnCode;
import field.bytecode.protect.dispatch.aRun;
import field.core.Constants;
import field.core.Platform;
import field.core.Platform.OS;
import field.core.StandardFluidSheet;
import field.core.dispatch.IVisualElement;
import field.core.dispatch.IVisualElementOverrides;
import field.core.dispatch.Mixins.iMixinProxy;
import field.core.dispatch.IVisualElementOverrides.MakeDispatchProxy;
import field.core.persistance.FluidCopyPastePersistence;
import field.core.persistance.PackageTools;
import field.core.plugins.autoexecute.Globals;
import field.core.plugins.help.ContextualHelp;
import field.core.plugins.help.HelpBrowser;
import field.core.plugins.python.PythonPlugin;
import field.core.plugins.selection.DrawTopology.DefaultDrawer;
import field.core.ui.BetterComboBox;
import field.core.ui.GraphNodeToTree;
import field.core.windowing.GLComponentWindow;
import field.core.windowing.components.*;
import field.core.windowing.components.SelectionGroup.iSelectionChanged;
import field.launch.IUpdateable;
import field.launch.Launcher;
import field.math.graph.IMutableContainer;
import field.math.graph.ITopology;
import field.math.linalg.Vector2;
import field.math.util.CubicTools;
import field.namespace.generic.ReflectionTools;
import field.util.collect.tuple.Pair;
import field.util.HashMapOfLists;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TreeItem;

import java.io.*;
import java.lang.reflect.Method;
import java.util.*;

@Woven
public
class SelectionSetDriver {

    public static
    class TravelTo implements IUpdateable {
        private final GLComponentWindow window;
        private final Vector2 center;
        private final Vector2 scale;
        long start = System.currentTimeMillis();
        float tx;
        float ty;
        float sx;
        float sy;

        public
        TravelTo(GLComponentWindow window, Vector2 center) {
            this.window = window;
            this.center = center;
            tx = window.getXTranslation();
            ty = window.getYTranslation();
            sx = window.getXScale();
            sy = window.getYScale();
            this.scale = new Vector2(sx, sy);
        }

        public
        TravelTo(GLComponentWindow window, SavedView view) {
            this.window = window;
            this.center = new Vector2(view.tx, view.ty);
            this.scale = new Vector2(view.sx, view.sy);
            tx = window.getXTranslation();
            ty = window.getYTranslation();
            sx = window.getXScale();
            sy = window.getYScale();

        }

        public
        void update() {

            float alpha = (float) ((System.currentTimeMillis() - start) / 1000.0);
            if (alpha > 1) {
                alpha = 1;
                Launcher.getLauncher().deregisterUpdateable(this);
            }

            float x, y;

            alpha = CubicTools.smoothStep(alpha);

            x = tx * (1 - alpha) + (alpha) * center.x;
            y = ty * (1 - alpha) + (alpha) * center.y;

            window.setXTranslation(x);
            window.setYTranslation(y);

            x = sx * (1 - alpha) + (alpha) * scale.x;
            y = sy * (1 - alpha) + (alpha) * scale.y;

            window.setXScale(x);
            window.setYScale(y);

            window.requestRepaint();

        }
    }

    public static
    class SavedView {
        float sx;
        float sy;
        float tx;
        float ty;
        protected String name;
    }

    public
    interface iExtendedSelectionAxis extends iSelectionAxis {
        public
        void selectionChanged(Set<IVisualElement> s);

        public
        void start();

        public
        void stop();
    }

    public
    interface iSelectionAxis {
        public
        void constructNodesForSelected(Set<IVisualElement> everything,
                                       IVisualElement oneThning,
                                       SelectionSetNode parent);

        public
        String getName();
    }

    public
    interface iSelectionPredicate {
        public
        void begin(Set<IVisualElement> everything,
                   Set<IVisualElement> currentlySelected,
                   Set<IVisualElement> previousCache);

        public
        boolean is(IVisualElement e);
    }

    public static
    class SelectionSet {
        private transient Set<IVisualElement> cached;

        Set<String> cachedUID = new HashSet<String>();

        iSelectionPredicate predicate;

        String name;

        public
        SelectionSet(iSelectionPredicate predicate, String name) {
            super();
            this.predicate = predicate;
            this.name = name;
        }

        public
        Set<IVisualElement> getCached(IVisualElement root) {
            if (cached == null) {
                cached = new HashSet<IVisualElement>();
                for (String s : cachedUID) {
                    cached.add(StandardFluidSheet.findVisualElement(root, s));
                }
            }
            return cached;
        }

        public
        void setCached(Set<IVisualElement> cached) {
            this.cached = cached;
            cachedUID.clear();
            for (IVisualElement e : cached) {
                cachedUID.add(e.getUniqueID());
            }
        }

        @Override
        public
        String toString() {
            return name;
        }
    }

    public
    enum SelectionSetModifer {
        or, and, orNot
    }

    public static
    class SelectionSetNode extends field.math.graph.NodeImpl<SelectionSetNode> implements IMutableContainer<SelectionSet, SelectionSetNode> {
        public SelectionSetModifer modifier = SelectionSetModifer.or;

        private SelectionSet set;

        private String label;

        private final SelectionSetNodeType type;

        public
        SelectionSetNode(SelectionSet set, String label, SelectionSetNodeType type) {
            this.setLabel(label);
            this.type = type;
            setPayload(set);
        }

        public
        String getLabel() {
            return label;
        }

        public
        SelectionSet payload() {
            return set;
        }

        public
        void setLabel(String label) {
            this.label = label;
        }

        public
        SelectionSetNode setModifier(SelectionSetModifer modifier) {
            this.modifier = modifier;
            return this;
        }

        public
        SelectionSetNode setPayload(SelectionSet t) {
            this.set = t;
            return this;
        }

        @Override
        public
        String toString() {
            if (type == SelectionSetNodeType.ruler) return "_____________________________";
            if (type == SelectionSetNodeType.label) return getLabel() + ' ' + smaller("("
                                                                                      + this.getChildren().size()
                                                                                      + ' '
                                                                                      + getContentsDescription()
                                                                                      + (this.getChildren().size() == 1
                                                                                         ? ""
                                                                                         : "s")
                                                                                      + ')');
            if (type == SelectionSetNodeType.computed)
                return (getLabel() != null ? (getLabel()) : (String.valueOf(payload()))) + ' ' + smaller("("
                                                                                                         + this.getChildren()
                                                                                                               .size()
                                                                                                         + ' '
                                                                                                         + getContentsDescription()
                                                                                                         + (this.getChildren()
                                                                                                                .size()
                                                                                                            == 1
                                                                                                            ? ""
                                                                                                            : "s")
                                                                                                         + ')');

            return (modifier != SelectionSetModifer.or ? ("(" + modifier + ") ") : "") + (getLabel() != null
                                                                                          ? (getLabel())
                                                                                          : (String.valueOf(payload())));
        }

        protected
        String getContentsDescription() {
            return "element";
        }
    }

    public static
    class SelectionSetNodeView extends SelectionSetNode {

        private final SavedView view;

        public
        SelectionSetNodeView(SelectionSet set, String label, SavedView view) {
            super(set, label, SelectionSetNodeType.view);
            this.view = view;
        }

        public
        SavedView getView() {
            return view;
        }

    }

    public
    enum SelectionSetNodeType {
        computed, label, root, saved, ruler, view
    }

    public static
    String nameFor(IVisualElement e) {

        // if (e == root)
        // return "sheet";

        try {

            String name = e.getProperty(IVisualElement.name);
            IVisualElementOverrides overrides = e.getProperty(IVisualElement.overrides);
            String overname;
            if (overrides instanceof iMixinProxy) {
                List m = ((iMixinProxy) overrides).getCallList();
                overname = "";
                for (Object mm : m) {
                    overname += mm.getClass().getSimpleName() + ' ';
                }
                overname = "[ " + overname + ']';
            }
            else overname = overrides.getClass().getSimpleName();

            iComponent view = e.getProperty(IVisualElement.localView);
            if (view == null) {
            }
            else if (view.getClass().equals(DraggableComponent.class) || view.getClass()
                                                                             .equals(PlainDraggableComponent.class)) {
            }
            else {
                overname = '[' + overname + " : " + view.getClass().getSimpleName() + ']';
            }

            return (name == null ? "unnamed" : "<b>" + name + "</b>") + ' ' + smaller(overname);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            return "(npe thrown in nameFor)";
        }
    }

    static protected
    String smaller(String text) {
        return "<font size=-3 color='#" + Constants.defaultTreeColorDim + "'>" + text + "</font>";
    }

    private final IVisualElement root;

    private final SelectionSetUI selectionUI;

    private final SelectionGroup<iComponent> group;

    private final Button left;
    private final Button right;

    private final BetterComboBox selectionAxesButton;

    protected TreeItem[] currentPathIs;

    List<iSelectionAxis> selectionAxes = new ArrayList<iSelectionAxis>();

    SelectionSetNode rootModel = new SelectionSetNode(null, "root", SelectionSetNodeType.root);

    FluidCopyPastePersistence copyPaste;

    iSelectionAxis currentAxis;

    SelectionSetNode currentSelectionNode;

    SelectionSetNode savedSelectionNode;

    SelectionSetNode classSelectionNode;

    Set<SelectionSet> saved = new HashSet<SelectionSet>();

    Set<SavedView> savedViews = new HashSet<SavedView>();

    Method method_reconstructRootModel = ReflectionTools.methodOf("reconstructRootModel", SelectionSetDriver.class);

    HashSet<String> open = new LinkedHashSet<String>();

    HashSet<String> closed = new LinkedHashSet<String>();

    boolean suspendSelectionProcessing = false;

    ArrayList<Set<IVisualElement>> previousSelectionSet = new ArrayList<Set<IVisualElement>>();

    Set<IVisualElement> currentSelectionSet = new HashSet<IVisualElement>();

    Set<IVisualElement> everyElement = new HashSet<IVisualElement>();

    private SelectionSetNode viewSelectionNode;

    private CTabItem item;

    private GraphNodeToTree graphNodeToTree;

    public
    SelectionSetDriver(final IVisualElement root, ToolBarFolder parent) {
        this.root = root;

        selectionUI = new SelectionSetUI("icons/move_32x32.png", parent) {
            // TODO selectionSet
            // @Override
            @Woven
            @NextUpdate(delay = 2)
            protected
            void doubleClickRow(TreeItem p) {

                currentPathIs = new TreeItem[]{p};
                // markedSelectedPaths(currentPathIs);
                axisForwards();
            }

            protected
            void selectionChanged(TreeItem[] selection) {
                currentPathIs = selection;
                markedSelectedPaths(currentPathIs);
            }

            @Override
            protected
            LinkedHashMap<String, IUpdateable> getMenuItems() {

                LinkedHashMap<String, IUpdateable> ret = new LinkedHashMap<String, IUpdateable>();
                if (currentSelectionSet.size() > 0) {
                    ret.put("Operations on currently selected element" + (currentSelectionSet.size() > 1 ? "s" : ""),
                            null);

                    ret.put("make a <b>new selection set</b>", new IUpdateable() {
                        public
                        void update() {

                            // TODO swt modal popup
                            // text box
                            // Point top = new
                            // Point(50, 150);
                            // SwingUtilities.convertPointToScreen(top,
                            // currentInspector.getWindow());
                            //
                            // PopupTextBox.Modal.getString(top,
                            // "name :",
                            // "unnamed selection set",
                            // new
                            // iAcceptor<String>() {
                            // public
                            // iAcceptor<String>
                            // set(
                            // String to) {
                            // OverlayAnimationManager
                            // .notifyAsText(
                            // root,
                            // "created new selection set",
                            // null);
                            // Saved saved = new
                            // ComputedSelectionSets.Saved(
                            // new
                            // HashSet<iVisualElement>(
                            // currentSelectionSet));
                            // SelectionSet ss = new
                            // SelectionSet(
                            // saved, to.trim());
                            // new SelectionSetNode(
                            // ss,
                            // null,
                            // SelectionSetNodeType.saved);
                            // SelectionSetDriver.this.saved
                            // .add(ss);
                            // reconstructRootModel();
                            // return this;
                            // }
                            //
                            // });

                        }
                    });

                    ret.put("<b>duplicate</b> these elements", new IUpdateable() {
                        public
                        void update() {

                            FluidCopyPastePersistence copier = IVisualElement.copyPaste.get(root);

                            StringWriter temp = new StringWriter();
                            HashSet<IVisualElement> savedOut = new HashSet<IVisualElement>();
                            ObjectOutputStream oos = copier.getObjectOutputStream(temp, savedOut, currentSelectionSet);
                            try {
                                oos.writeObject(currentSelectionSet);
                                oos.close();

                                everyElement = new HashSet<IVisualElement>(StandardFluidSheet.allVisualElements(root));
                                ObjectInputStream ois =
                                        copier.getObjectInputStream(new StringReader(temp.getBuffer().toString()),
                                                                    savedOut,
                                                                    everyElement);
                                Object in = ois.readObject();
                                ois.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    ret.put("<b>save</b> these elements as a new sheet", new IUpdateable() {
                        public
                        void update() {
                            // TODO swt file chooser
                            // final JFileChooser fc
                            // = new JFileChooser(
                            // new File(
                            // SystemProperties
                            // .getDirProperty("versioning.dir")))
                            // {
                            // @Override
                            // protected JDialog
                            // createDialog(
                            // Component parent)
                            // throws
                            // HeadlessException {
                            // JDialog d = super
                            // .createDialog(parent);
                            // d.setAlwaysOnTop(true);
                            // return d;
                            // }
                            // };
                            // Quaqua15TigerLookAndFeelField.deRound(fc);
                            // fc.setDialogType(JFileChooser.SAVE_DIALOG);
                            // int r =
                            // fc.showDialog(null,
                            // "Save Selection As");
                            // if (r ==
                            // JFileChooser.APPROVE_OPTION)
                            // {
                            // File f =
                            // fc.getSelectedFile();
                            //
                            // try {
                            // String p =
                            // f.getCanonicalPath();
                            // if
                            // (!p.endsWith(".field"))
                            // p = p + ".field";
                            // if
                            // (!p.startsWith(FieldMenus
                            // .getCanonicalVersioningDir()))
                            // {
                            // JOptionPane
                            // .showMessageDialog(
                            // fc,
                            // "Can't save outside repostiory\n'"
                            // + FieldMenus
                            // .getCanonicalVersioningDir()
                            // + "'");
                            // return;
                            // }
                            //
                            // f = new File(p);
                            // } catch
                            // (HeadlessException e)
                            // {
                            // e.printStackTrace();
                            // } catch (IOException
                            // e) {
                            // e.printStackTrace();
                            // }
                            //
                            // if (f.exists()) {
                            // JOptionPane.showMessageDialog(fc,
                            // "Sheet already exiss");
                            // } else {
                            // iVisualElement rr =
                            // root;
                            // SelectionGroup<iComponent>
                            // group =
                            // iVisualElement.selectionGroup
                            // .get(rr);
                            // HashSet<iVisualElement>
                            // currentSelection
                            // = new
                            // HashSet<iVisualElement>();
                            // for (iComponent c :
                            // group
                            // .getSelection()) {
                            // iVisualElement v = c
                            // .getVisualElement();
                            // if (v != null) {
                            // currentSelection.add(v);
                            // }
                            // }
                            // FieldMenus.fieldMenus.saveSubsetAs(
                            // currentSelection,
                            // root, f);
                            // }
                            //
                            // } else if (r ==
                            // JFileChooser.CANCEL_OPTION)
                            // {
                            //
                            // }
                        }
                    });

                    ret.put("create a <b>clipboard package</b>", new IUpdateable() {

                        public
                        void update() {

                            IVisualElement element = currentSelectionSet.iterator().next();
                            String name = element.getProperty(IVisualElement.name);
                            File tmp = PackageTools.newTempFileWithSelected(root, name);
                            PackageTools.copyFileReferenceToClipboard(tmp.getAbsolutePath());

                            // TODO set flash window
                            // with message
                            // selectionUI.setFlash("copied \u279d");

                        }
                    });

                }
                ret.put("Operations on view", null);

                ret.put("Save view (zoom and translation)", new IUpdateable() {

                    public
                    void update() {
                        // TODO swt modal text prompt
                        // Point top = new Point(50,
                        // 150);
                        // SwingUtilities.convertPointToScreen(top,
                        // currentInspector.getWindow());
                        //
                        // PopupTextBox.Modal.getString(top,
                        // "name :",
                        // "unnamed view", new
                        // iAcceptor<String>() {
                        // public iAcceptor<String>
                        // set(String to) {
                        // OverlayAnimationManager.notifyAsText(
                        // root, "created new view",
                        // null);
                        //
                        // SavedView s = new
                        // SavedView();
                        // GLComponentWindow w =
                        // iVisualElement.enclosingFrame
                        // .get(root);
                        // s.tx = w.getXTranslation();
                        // s.ty = w.getYTranslation();
                        // s.sx = w.getXScale();
                        // s.sy = w.getYScale();
                        // s.name = to;
                        //
                        // SelectionSetDriver.this.savedViews
                        // .add(s);
                        // reconstructRootModel();
                        // return this;
                        // }
                        //
                        // });

                    }
                });

                return ret;
            }

            @Override
            protected
            void popUpMenu(Event ev, LinkedHashMap<String, IUpdateable> items) {

                TreeItem[] path = tree.getSelection();

                if (path != null) if (path.length > 0) {
                    Set<IVisualElement> newSelectionSet = new HashSet<IVisualElement>();
                    everyElement = new HashSet<IVisualElement>(StandardFluidSheet.allVisualElements(root));
                    if (path != null) {

                        HashSet<IVisualElement> here = new HashSet<IVisualElement>();
                        for (TreeItem p : path) {
                            SelectionSet n = (SelectionSet) p.getData();

                            if (n != null) {
                                n.predicate.begin(everyElement, currentSelectionSet, n.getCached(root));
                                for (IVisualElement e : everyElement) {
                                    if (n.predicate.is(e)) {
                                        here.add(e);
                                    }
                                }

                            }
                        }
                        for (IVisualElement e : here)
                            new MakeDispatchProxy().getOverrideProxyFor(e).menuItemsFor(e, items);

                    }
                }

            }

        };

        selectionUI.setWaterText("Selection");
        group = root.getProperty(IVisualElement.selectionGroup);

        selectionAxes.add(new iSelectionAxis() {
            public
            void constructNodesForSelected(Set<IVisualElement> everything,
                                           IVisualElement oneThning,
                                           SelectionSetNode parent) {
                return;
            }

            public
            String getName() {
                return "<b>Default</b> selection axes";
            }
        });
        // selectionAxes.add(new iSelectionAxis() {
        // public void constructNodesForSelected(Set<iVisualElement>
        // everything, iVisualElement oneThing, SelectionSetNode parent)
        // {
        //
        // VEList e =
        // oneThing.getProperty(SplineComputingOverride.computed_elaborates);
        // if (e == null)
        // return;
        // if (e.size() == 0)
        // return;
        // for (final iVisualElement element : e) {
        // parent.addChild(new SelectionSetNode(new SelectionSet(new
        // iSelectionPredicate() {
        // public void begin(Set<iVisualElement> everything,
        // Set<iVisualElement> currentlySelected, Set<iVisualElement>
        // previousCache) {
        // }
        //
        // public boolean is(iVisualElement ex) {
        // return element == ex;
        // };
        // }, "  \u21e4 \u2014 " + nameFor(element)), null,
        // SelectionSetNodeType.saved));
        // }
        // }
        //
        // public String getName() {
        // return
        // "<html>Spline \u2014 <b>Elaborates</b> ... (or \"input\")";
        // }
        // });
        // selectionAxes.add(new iSelectionAxis() {
        // public void constructNodesForSelected(Set<iVisualElement>
        // everything, iVisualElement oneThing, SelectionSetNode parent)
        // {
        //
        // VEList e =
        // oneThing.getProperty(SplineComputingOverride.computed_elaboratedBy);
        // if (e == null)
        // return;
        // if (e.size() == 0)
        // return;
        // for (final iVisualElement element : e) {
        // parent.addChild(new SelectionSetNode(new SelectionSet(new
        // iSelectionPredicate() {
        // public void begin(Set<iVisualElement> everything,
        // Set<iVisualElement> currentlySelected, Set<iVisualElement>
        // previousCache) {
        // }
        //
        // public boolean is(iVisualElement ex) {
        // return element == ex;
        // };
        // }, "  \u21e2 \u2014 " + nameFor(element)), null,
        // SelectionSetNodeType.saved));
        // }
        // }
        //
        // public String getName() {
        // return
        // "<html>Spline \u2014 <b>Elaborated</b> by ... (or \"output\")";
        // }
        // });

        selectionAxes.add(new iSelectionAxis() {
            public
            void constructNodesForSelected(Set<IVisualElement> everything,
                                           IVisualElement oneThing,
                                           SelectionSetNode parent) {

                Globals globals = PythonPlugin.python_globals.get(oneThing);
                if (globals != null) {
                    List<String> potential = globals.getPotentialDefinedBy(oneThing);
                    HashMapOfLists<String, IVisualElement> used = globals.getUsedDefinedBy(oneThing);
                    List<Pair<String, IVisualElement>> usedInside = globals.getUsedBy(oneThing);
                    for (String q : used.keySet()) {
                        potential.remove(q);
                    }

                    SelectionSetNode p1 =
                            new SelectionSetNode(null, "Declared but not used", SelectionSetNodeType.label);
                    SelectionSetNode p2 = new SelectionSetNode(null, "Declared and used", SelectionSetNodeType.label);
                    SelectionSetNode p3 =
                            new SelectionSetNode(null, "Used and declared elsewhere", SelectionSetNodeType.label);
                    parent.addChild(p1);
                    parent.addChild(p2);
                    parent.addChild(p3);

                    for (String q : potential) {
                        p1.addChild(new SelectionSetNode(null, q, SelectionSetNodeType.label));
                    }

                    for (Map.Entry<String, Collection<IVisualElement>> q : used.entrySet()) {
                        SelectionSetNode ll = new SelectionSetNode(null, q.getKey(), SelectionSetNodeType.label);
                        p2.addChild(ll);

                        for (final IVisualElement eq : q.getValue()) {
                            ll.addChild(new SelectionSetNode(new SelectionSet(new iSelectionPredicate() {

                                public
                                void begin(Set<IVisualElement> everything,
                                           Set<IVisualElement> currentlySelected,
                                           Set<IVisualElement> previousCache) {
                                }

                                public
                                boolean is(IVisualElement e) {
                                    return e == eq;
                                }
                            }, " \u21e2 used variable \u2014 " + nameFor(eq)), null, SelectionSetNodeType.saved));
                        }
                    }
                    for (final Pair<String, IVisualElement> q : usedInside) {
                        SelectionSetNode ll = new SelectionSetNode(null, q.left, SelectionSetNodeType.label);
                        p3.addChild(ll);

                        ll.addChild(new SelectionSetNode(new SelectionSet(new iSelectionPredicate() {

                            public
                            void begin(Set<IVisualElement> everything,
                                       Set<IVisualElement> currentlySelected,
                                       Set<IVisualElement> previousCache) {
                            }

                            public
                            boolean is(IVisualElement e) {
                                return e == q.right;
                            }
                        }, " \u21e2 declared by \u2014 " + nameFor(q.right)), null, SelectionSetNodeType.saved));
                    }
                }
            }

            public
            String getName() {
                return "Offers <b>global variables</b>";
            }
        });

        currentAxis = selectionAxes.get(0);

        ArrayList<String> vp = new ArrayList<String>();
        for (iSelectionAxis ax : selectionAxes) {
            vp.add(ax.getName());
        }

        BetterComboBox button = new BetterComboBox(selectionUI.toolbar, vp.toArray(new String[vp.size()])) {

            @Override
            public
            void updateLabels() {
            }

            @Override
            public
            void updateSelection(int index, String text) {

                if (currentAxis instanceof iExtendedSelectionAxis) ((iExtendedSelectionAxis) currentAxis).stop();

                currentAxis = selectionAxes.get(index);

                if (currentAxis instanceof iExtendedSelectionAxis) {
                    ((iExtendedSelectionAxis) currentAxis).start();
                    ((iExtendedSelectionAxis) currentAxis).selectionChanged(currentSelectionSet);
                }

                reconstructRootModel();
            }
        };
        button.combo.setBackground(button.combo.getParent().getBackground());

        button.combo.setLayoutData(new RowData(150, (Platform.getOS() == OS.mac) ? 20 : 25));

        selectionAxesButton = button;

        left = new Button(selectionUI.toolbar, SWT.PUSH | SWT.FLAT);

        left.addSelectionListener(new SelectionListener() {
            @Override
            public
            void widgetSelected(SelectionEvent e) {
                ((MainSelectionGroup) group).popSelection();
            }

            @Override
            public
            void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        left.setBackground(button.combo.getParent().getBackground());
        left.setLayoutData(new RowData(20, (Platform.getOS() == OS.mac) ? 20 : 25));

        left.addPaintListener(new PaintListener() {

            @Override
            public
            void paintControl(PaintEvent e) {
                left.setEnabled(((MainSelectionGroup) group).canGoBack());
            }
        });
        left.setText("<");
        // left.setImage(new Image(Launcher.display,
        // "icons/arrow_left_12x12.png"));

        right = new Button(selectionUI.toolbar, SWT.PUSH | SWT.FLAT);

        right.addSelectionListener(new SelectionListener() {
            @Override
            public
            void widgetSelected(SelectionEvent e) {
                ((MainSelectionGroup) group).moveForwardSelection();
            }

            @Override
            public
            void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        right.setBackground(button.combo.getParent().getBackground());
        right.setLayoutData(new RowData(20, Platform.getOS() == OS.mac ? 20 : 25));

        right.addPaintListener(new PaintListener() {

            @Override
            public
            void paintControl(PaintEvent e) {
                right.setEnabled(((MainSelectionGroup) group).canGoForward());
            }
        });
        // right.setImage(new Image(Launcher.display,
        // "icons/arrow_right_12x12.png"));
        right.setText(">");

        right.setToolTipText("Selection history forward");
        left.setToolTipText("Selection history backward");

        group.registerNotification(new iSelectionChanged<iComponent>() {
            public
            void selectionChanged(Set<iComponent> selected) {
                LinkedHashSet<iComponent> c = new LinkedHashSet<iComponent>();
                for (iComponent cc : c) {
                    if (cc.getVisualElement() == null) group.removeFromSelection(cc);
                }
                changeSelection(selected);

                left.redraw();
                right.redraw();

            }
        });

        // JButton lock = new JButton("") {
        // ImageIcon unlocked = new ImageIcon("unlock.png");
        // ImageIcon locked = new ImageIcon("lock.png");
        //
        // @Override
        // public void paint(Graphics g) {
        // if (!isEnabled())
        // return;
        // if (selectionUI.getRevTree().isEnabled())
        // setIcon(unlocked);
        // else
        // setIcon(locked);
        // if (currentSelectionSet.size() == 0)
        // return;
        //
        // super.paint(g);
        //
        // if (!selectionUI.getRevTree().isEnabled()) {
        // Rectangle b = this.getBounds();
        // g.setColor(new Color(0.5f, 0f, 0, 0.2f));
        // g.fillRect(0, 0, b.width, b.height);
        // }
        // }
        //
        // @Override
        // protected void fireActionPerformed(ActionEvent event) {
        // super.fireActionPerformed(event);
        // if (selectionUI.getRevTree().isEnabled())
        // doLockSelection();
        // else
        // doUnlockSelection();
        // }
        // };
        // lock.setFont(new Font(Constants.defaultFont, Font.BOLD, 6));
        // lock.putClientProperty("Quaqua.Button.style", "square");
        // lock.setMinimumSize(new Dimension(25, 25));
        // lock.setMaximumSize(new Dimension(25, 25));
        // lock.setPreferredSize(new Dimension(25, 25));
        // lock.setIcon(new ImageIcon("unlock.png"));
        // selectionUI.getToolbarPanel().add(lock);
        //
        // SpringUtilities.makeCompactGrid(selectionUI.getToolbarPanel(),
        // 1,
        // selectionUI.getToolbarPanel().getComponentCount(), 2, 0, 0,
        // 0);

        addNotibleOverridesClass(iMixinProxy.class, "Blended Type (from Mixins)");

        DrawTopology.addTopologyAsSelectionAxis(this,
                                                "<html><b>Super / Sub-element</b> ",
                                                root,
                                                new ITopology<IVisualElement>() {

                                                    public
                                                    List<IVisualElement> getChildrenOf(IVisualElement of) {
                                                        return of.getChildren();
                                                    }

                                                    public
                                                    List<IVisualElement> getParentsOf(IVisualElement of) {
                                                        return (List<IVisualElement>) of.getParents();
                                                    }
                                                },
                                                new DefaultDrawer(),
                                                " \u21e2 members",
                                                " \u21e2 enclosing group",
                                                "member ",
                                                "next ");

        reconstructRootModel();

        installHelpBrowser(root);
    }

    @NextUpdate(delay = 3)
    private
    void installHelpBrowser(final IVisualElement root) {
        HelpBrowser h = HelpBrowser.helpBrowser.get(root);
        ContextualHelp ch = h.getContextualHelp();
        ch.addContextualHelpForWidget("selection",
                                      selectionUI.tree,
                                      ContextualHelp.providerForStaticMarkdownResource("contextual/selection.md"),
                                      50);
    }

    public
    void addNotibleOverridesClass(final Class/*
                                                     * <? extends
                                                     * iVisualElementOverrides
                                                     * .DefaultOverride>
                                                     */c, final String name) {

        aRun arun = new aRun() {
            @Override
            public
            ReturnCode tail(Object calledOn, Object[] args, Object returnWas) {
                ComputedSelectionSets.ByClass select = new ComputedSelectionSets.ByClass(c);
                if (selectsAny(everyElement, select)) {
                    SelectionSetNode node =
                            new SelectionSetNode(new SelectionSet(select, name), null, SelectionSetNodeType.computed);
                    classSelectionNode.addChild(node);

                    ArrayList<IVisualElement> q = new ArrayList<IVisualElement>();
                    q.addAll(everyElement);
                    sortByName(q);

                    for (final IVisualElement e : q) {
                        if (select.is(e))
                            node.addChild(new SelectionSetNode(new SelectionSet(new iSelectionPredicate() {
                                public
                                void begin(Set<IVisualElement> everything,
                                           Set<IVisualElement> currentlySelected,
                                           Set<IVisualElement> previousCache) {
                                }

                                public
                                boolean is(IVisualElement ex) {
                                    return e == ex;
                                }
                            }, nameFor(e)), null, SelectionSetNodeType.saved));
                    }

                }
                return super.tail(calledOn, args, returnWas);
            }
        };
        Cont.linkWith(this, this.method_reconstructRootModel, arun);
    }

    public
    void addNotibleComponentClass(final Class/*
                                                     * <? extends
                                                     * iVisualElementOverrides
                                                     * .DefaultOverride>
                                                     */c, final String name) {

        aRun arun = new aRun() {
            @Override
            public
            ReturnCode tail(Object calledOn, Object[] args, Object returnWas) {
                ComputedSelectionSets.ByComponentClass select = new ComputedSelectionSets.ByComponentClass(c);
                if (selectsAny(everyElement, select)) {
                    SelectionSetNode node =
                            new SelectionSetNode(new SelectionSet(select, name), null, SelectionSetNodeType.computed);
                    classSelectionNode.addChild(node);

                    ArrayList<IVisualElement> q = new ArrayList<IVisualElement>();
                    q.addAll(everyElement);
                    sortByName(q);

                    for (final IVisualElement e : q) {
                        if (select.is(e))
                            node.addChild(new SelectionSetNode(new SelectionSet(new iSelectionPredicate() {
                                public
                                void begin(Set<IVisualElement> everything,
                                           Set<IVisualElement> currentlySelected,
                                           Set<IVisualElement> previousCache) {
                                }

                                public
                                boolean is(IVisualElement ex) {
                                    return e == ex;
                                }
                            }, nameFor(e)), null, SelectionSetNodeType.saved));
                    }

                }
                return super.tail(calledOn, args, returnWas);
            }
        };
        Cont.linkWith(this, this.method_reconstructRootModel, arun);
    }

    public
    void addNotibleOverridesClass(final Set<Class<?>> c, final String name) {

        aRun arun = new aRun() {
            @Override
            public
            ReturnCode tail(Object calledOn, Object[] args, Object returnWas) {
                ComputedSelectionSets.ByClass select = new ComputedSelectionSets.ByClass(c);
                if (selectsAny(everyElement, select)) {
                    SelectionSetNode node =
                            new SelectionSetNode(new SelectionSet(select, name), null, SelectionSetNodeType.computed);
                    classSelectionNode.addChild(node);

                    ArrayList<IVisualElement> q = new ArrayList<IVisualElement>();
                    q.addAll(everyElement);
                    sortByName(q);

                    for (final IVisualElement e : q) {
                        if (select.is(e))
                            node.addChild(new SelectionSetNode(new SelectionSet(new iSelectionPredicate() {
                                public
                                void begin(Set<IVisualElement> everything,
                                           Set<IVisualElement> currentlySelected,
                                           Set<IVisualElement> previousCache) {
                                }

                                public
                                boolean is(IVisualElement ex) {
                                    return e == ex;
                                }
                            }, nameFor(e)), null, SelectionSetNodeType.saved));
                    }

                }
                return super.tail(calledOn, args, returnWas);
            }
        };
        Cont.linkWith(this, this.method_reconstructRootModel, arun);

    }

    public
    void addSavedSelectionSets(Set<SelectionSet> right) {
        saved.addAll(right);
        reconstructRootModel();
    }

    public
    void addSavedViews(Set<SavedView> right) {
        savedViews.addAll(right);
        reconstructRootModel();
    }

    public
    void addSelectionAxes(String name, iExtendedSelectionAxis extendedSelectionAxis) {
        selectionAxes.add(extendedSelectionAxis);
        selectionAxesButton.addOption(name);
    }

    public
    Set<SelectionSet> getSavedSelectionSets() {
        return saved;
    }

    public
    Set<SavedView> getSavedViews() {
        return savedViews;
    }

    public
    ArrayList<IVisualElement> getSelectionByName(String expression) {
        List<SelectionSetNode> c = savedSelectionNode.getChildren();
        Set<IVisualElement> r = new HashSet<IVisualElement>();
        for (int i = 0; i < c.size(); i++) {

            if (c.get(i).set.name.matches(expression)) {
                c.get(i).set.predicate.begin(everyElement, currentSelectionSet, c.get(i).set.cached);
                for (IVisualElement e : everyElement) {
                    if (c.get(i).set.predicate.is(e)) {
                        r.add(e);
                    }
                }
            }
        }
        return new ArrayList<IVisualElement>(r);
    }

    @NextUpdate(delay = 5)
    public
    void loadState(TreeState t) {
        t.load(selectionUI.getTree());
        // ArrayList<Boolean> bb = new ArrayList<Boolean>(ex);
        // int j = 0;
        // while (bb.size() > 0 && j <
        // selectionUI.getTree().getItemCount()) {
        // boolean z = bb.remove(0);
        // selectionUI.getTree().getItem(j).setExpanded(z);
        // if (z) {
        // loadState(ex, selectionUI.getTree().getItem(j));
        // }
        // j++;
        // }
        // selectionUI.getTree().getItem(0).setExpanded(true);

    }

    public
    TreeState saveState() {

        // int i = selectionUI.getTree().getItemCount();
        // boolean[] b = new boolean[i];
        //
        // List<Boolean> bb = new ArrayList<Boolean>();
        //
        // for (int j = 0; j < b.length; j++) {
        // bb.add(selectionUI.getTree().getItem(j).getExpanded());
        // if (selectionUI.getTree().getItem(j).getExpanded()) {
        // saveState(selectionUI.getTree().getItem(j), bb);
        // }
        // }

        return TreeState.save(selectionUI.getTree());
    }

    private static
    void saveState(TreeItem item, List<Boolean> bb) {
        for (int j = 0; j < item.getItemCount(); j++) {
            bb.add(item.getItem(j).getExpanded());
            if (item.getItem(j).getExpanded()) {
                saveState(item.getItem(j), bb);
            }
        }
    }

    private static
    String describeSelection(Set<iComponent> selected) {
        try {
            if (selected.size() == 0) {
                return "nothing selected";
            }
            if (selected.size() == 1) {
                IVisualElement v = selected.iterator().next().getVisualElement();
                if (v != null) {
                    String name = v.getProperty(IVisualElement.name);
                    if (name == null) return "unnamed element";
                    return "element '" + shorter(name) + "'";
                }
                else {
                    return "nothing";
                }
            }
            else {
                LinkedHashSet<String> names = new LinkedHashSet<String>();
                LinkedHashSet<iComponent> toRemove = new LinkedHashSet<iComponent>();

                for (iComponent c : selected) {
                    IVisualElement v = c.getVisualElement();
                    if (v != null) {
                        String name = v.getProperty(IVisualElement.name);
                        if (name != null && !"untitled".equals(name)) names.add(name);
                    }
                    else {
                        c.setSelected(false);
                        toRemove.add(c);
                    }
                }
                selected.remove(toRemove);

                if (names.size() < 4) {
                    String e = "'";
                    List<String> snames = new ArrayList<String>(names);
                    for (int i = 0; i < snames.size(); i++) {
                        e += shorter(snames.get(i)) + "'" + ((i != snames.size() - 1) ? ",'" : "");
                    }

                    if (names.size() < selected.size()) e += " and "
                                                             + (selected.size() - names.size())
                                                             + " other"
                                                             + ((names.size() - selected.size() > 1) ? "s" : "");

                    return e;
                }
                else {
                    String e = "'";
                    List<String> snames = new ArrayList<String>(names);
                    for (int i = 0; i < 3; i++) {
                        e += shorter(snames.get(i)) + "'" + ((i != snames.size() - 1) ? ",'" : "");
                    }

                    if (selected.size() > 3)
                        e += " and " + (selected.size() - 3) + " other" + ((selected.size() - 3 > 1) ? "s" : "");
                    return e;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "(unknown selection)";
        }
    }

    @NextUpdate(delay = 2)
    protected static
    void markOnly(Set<IVisualElement> currentSet, Set<IVisualElement> newSet) {
        // suspendSelectionProcessing = true;
        try {
            for (IVisualElement c : currentSet) {
                iComponent component = IVisualElement.localView.get(c);
                SelectionGroup<iComponent> group = IVisualElement.markingGroup.get(c);
                if (group != null) {
                    group.deselectAll();
                }
            }

            for (IVisualElement n : newSet) {
                iComponent component = IVisualElement.localView.get(n);
                SelectionGroup<iComponent> group = IVisualElement.markingGroup.get(n);
                if (component != null && group != null) {
                    group.addToSelection(component);

                    if (component instanceof DraggableComponent) ((DraggableComponent) component).setMarked(true);
                    if (component instanceof PlainDraggableComponent)
                        ((PlainDraggableComponent) component).setMarked(true);
                }
            }
        } finally {
        }
    }

    @DispatchOverTopology(topology = Cont.class)
    private
    void reconstructRootModel() {

        TreeState savedTree = saveState();
        for (SelectionSetNode n : new ArrayList<SelectionSetNode>(rootModel.getChildren()))
            rootModel.removeChild(n);

        everyElement = new HashSet<IVisualElement>(StandardFluidSheet.allVisualElements(root));

        // add current selection (if selected), this is
        // special, and
        // needs to be noted in a member var
        // then saved
        // then "by class"
        // then "by position"
        // then "by history"
        // then misc: "connected to", "referenced
        // (locally, in
        // properties)", "changed" (by listening to prop
        // changes)

        if (currentSelectionSet.size() == 0) {
            currentSelectionNode = null;
        }
        else {
            ComputedSelectionSets.CurrentlySelected selected = new ComputedSelectionSets.CurrentlySelected();
            rootModel.addChild(currentSelectionNode =
                                       new SelectionSetNode(new SelectionSet(selected, "<i>Currently selected</i>"),
                                                            null,
                                                            SelectionSetNodeType.computed));

            for (final IVisualElement ce : currentSelectionSet) {
                SelectionSetNode p = new SelectionSetNode(new SelectionSet(new iSelectionPredicate() {
                    public
                    void begin(Set<IVisualElement> everything,
                               Set<IVisualElement> currentlySelected,
                               Set<IVisualElement> previousCache) {
                    }

                    public
                    boolean is(IVisualElement ex) {
                        return ce == ex;
                    }
                }, nameFor(ce)), null, SelectionSetNodeType.saved);
                currentSelectionNode.addChild(p);
                currentAxis.constructNodesForSelected(everyElement, ce, p);
            }

        }
        savedSelectionNode = new SelectionSetNode(null, "<hr>", SelectionSetNodeType.ruler) {
            @Override
            protected
            String getContentsDescription() {
                return "(seperator)";
            }
        };
        rootModel.addChild(savedSelectionNode);

        savedSelectionNode = new SelectionSetNode(null, "<i>Saved selection sets</i>", SelectionSetNodeType.label) {
            @Override
            protected
            String getContentsDescription() {
                return "set";
            }
        };
        rootModel.addChild(savedSelectionNode);

        for (SelectionSet s : saved) {
            savedSelectionNode.addChild(new SelectionSetNode(s, null, SelectionSetNodeType.saved));
        }

        viewSelectionNode = new SelectionSetNode(null, "<i>Saved view configurations</i>", SelectionSetNodeType.label) {
            @Override
            protected
            String getContentsDescription() {
                return "view";
            }
        };
        rootModel.addChild(viewSelectionNode);

        for (SavedView s : savedViews) {
            SelectionSetNodeView n = new SelectionSetNodeView(null, s.name, s);
            viewSelectionNode.addChild(n);
        }

        classSelectionNode = new SelectionSetNode(null, "<i>Ordered by class...</i>", SelectionSetNodeType.label) {
            @Override
            protected
            String getContentsDescription() {
                return "group";
            }
        };
        rootModel.addChild(classSelectionNode);

        ComputedSelectionSets.ByClass_plainPython plainPython = new ComputedSelectionSets.ByClass_plainPython();
        if (selectsAny(everyElement, plainPython)) {
            SelectionSetNode node = new SelectionSetNode(new SelectionSet(plainPython, "Plain Python elements"),
                                                         null,
                                                         SelectionSetNodeType.computed);
            classSelectionNode.addChild(node);
            ArrayList<IVisualElement> q = new ArrayList<IVisualElement>();
            q.addAll(everyElement);
            sortByName(q);

            for (final IVisualElement e : q) {
                if (plainPython.is(e)) node.addChild(new SelectionSetNode(new SelectionSet(new iSelectionPredicate() {
                    public
                    void begin(Set<IVisualElement> everything,
                               Set<IVisualElement> currentlySelected,
                               Set<IVisualElement> previousCache) {
                    }

                    public
                    boolean is(IVisualElement ex) {
                        return e == ex;
                    }
                }, nameFor(e)), null, SelectionSetNodeType.saved));
            }

        }

        ComputedSelectionSets.ByClass_computedSpline computedSpline =
                new ComputedSelectionSets.ByClass_computedSpline();
        if (selectsAny(everyElement, computedSpline)) {
            SelectionSetNode node = new SelectionSetNode(new SelectionSet(computedSpline, "Spline elements"),
                                                         null,
                                                         SelectionSetNodeType.computed);
            classSelectionNode.addChild(node);

            ArrayList<IVisualElement> q = new ArrayList<IVisualElement>();
            q.addAll(everyElement);
            sortByName(q);

            for (final IVisualElement e : q) {
                if (computedSpline.is(e))
                    node.addChild(new SelectionSetNode(new SelectionSet(new iSelectionPredicate() {
                        public
                        void begin(Set<IVisualElement> everything,
                                   Set<IVisualElement> currentlySelected,
                                   Set<IVisualElement> previousCache) {
                        }

                        public
                        boolean is(IVisualElement ex) {
                            return e == ex;
                        }
                    }, nameFor(e)), null, SelectionSetNodeType.saved));
            }

        }

        currentPathIs = null;

        graphNodeToTree = new GraphNodeToTree(selectionUI.getTree());
        graphNodeToTree.reset(rootModel);

        // selectionUI.getRevTree().setModel(new
        // JTreeModelForGraphNodes<SelectionSetNode>(rootModel));

        loadState(savedTree);

        selectionUI.getTree().getItem(0).setExpanded(true);

        // selectionUI.getRevTree().expandPath(new TreePath(new Object[]
        // {
        // rootModel, classSelectionNode }));
        // for (SelectionSetNode s : classSelectionNode.getChildren()) {
        // if (s.getChildren().size() < 10) {
        // selectionUI.getRevTree().expandPath(new TreePath(new Object[]
        // {
        // rootModel, classSelectionNode, s }));
        // }
        // }
        //
        // if (currentSelectionSet.size() != 0) {
        // selectionUI.getRevTree().expandPath(new TreePath(new Object[]
        // {
        // rootModel, currentSelectionNode }));
        // }

    }

    private
    void selectOnly(Set<IVisualElement> currentSet, Set<IVisualElement> newSet) {
        // suspendSelectionProcessing = true;
        try {

            previousSelectionSet.add(new HashSet<IVisualElement>(currentSet));
            if (previousSelectionSet.size() > 10) previousSelectionSet.remove(0);

            for (IVisualElement c : currentSet) {
                iComponent component = IVisualElement.localView.get(c);
                SelectionGroup<iComponent> group = IVisualElement.selectionGroup.get(c);
                if (group != null) group.deselectAll();
            }

            currentSet.clear();
            /*
			 * for (iVisualElement c : currentSet) { if
			 * (!newSet.contains(c)) { iComponent component =
			 * iVisualElement.localView.get(c);
			 * SelectionGroup<iComponent> group =
			 * iVisualElement.selectionGroup.get(c); if (component
			 * != null && group != null) {
			 * group.removeFromSelection(component);
			 * component.setSelected(false); } } }
			 */

            final Vector2 center = new Vector2();
            int num = 0;

            for (IVisualElement n : newSet) {
                if (!currentSet.contains(n)) {
                    iComponent component = IVisualElement.localView.get(n);
                    SelectionGroup<iComponent> group = IVisualElement.selectionGroup.get(n);
                    if (component != null && group != null) {
                        group.addToSelection(component);
                        component.setSelected(true);
                        center.add(n.getFrame(null).midpoint2());
                        num++;
                    }
                }
            }
            if (num > 0) {
                center.scale(1f / num);
                IVisualElement a = newSet.iterator().next();
                final GLComponentWindow window = IVisualElement.enclosingFrame.get(a);
                Point size = window.getFrame().getSize();

                center.x -= size.x / 2;
                center.y -= size.y / 2;

                if (window != null) {
                    Launcher.getLauncher().registerUpdateable(new TravelTo(window, center));
                }
            }
        } finally {
        }
    }

    static public
    void travelTo(Set<IVisualElement> e) {
        final Vector2 center = new Vector2();
        int num = 0;
        for (IVisualElement c : e) {
            SelectionGroup<iComponent> group = IVisualElement.selectionGroup.get(c);
            if (group != null) group.deselectAll();
        }

        for (IVisualElement n : e) {
            iComponent component = IVisualElement.localView.get(n);
            SelectionGroup<iComponent> group = IVisualElement.selectionGroup.get(n);
            if (component != null && group != null) {
                group.addToSelection(component);
                component.setSelected(true);
                center.add(n.getFrame(null).midpoint2());
                num++;
            }
        }
        if (num > 0) {
            center.scale(1f / num);
            IVisualElement a = e.iterator().next();
            final GLComponentWindow window = IVisualElement.enclosingFrame.get(a);
            Point size = window.getFrame().getSize();

            center.x -= size.x / 2;
            center.y -= size.y / 2;

            if (window != null) {
                Launcher.getLauncher().registerUpdateable(new TravelTo(window, center));
            }
        }
    }

    private
    void selectOnly_old(Set<IVisualElement> currentSet, Set<IVisualElement> newSet) {
        // suspendSelectionProcessing = true;
        try {

            previousSelectionSet.add(new HashSet<IVisualElement>(currentSet));
            if (previousSelectionSet.size() > 10) previousSelectionSet.remove(0);

            for (IVisualElement c : currentSet) {
                iComponent component = IVisualElement.localView.get(c);
                SelectionGroup<iComponent> group = IVisualElement.selectionGroup.get(c);
                if (group != null) group.deselectAll();
            }

            currentSet.clear();
			/*
			 * for (iVisualElement c : currentSet) { if
			 * (!newSet.contains(c)) { iComponent component =
			 * iVisualElement.localView.get(c);
			 * SelectionGroup<iComponent> group =
			 * iVisualElement.selectionGroup.get(c); if (component
			 * != null && group != null) {
			 * group.removeFromSelection(component);
			 * component.setSelected(false); } } }
			 */
            for (IVisualElement n : newSet) {
                if (!currentSet.contains(n)) {
                    iComponent component = IVisualElement.localView.get(n);
                    SelectionGroup<iComponent> group = IVisualElement.selectionGroup.get(n);
                    if (component != null && group != null) {
                        group.addToSelection(component);
                        component.setSelected(true);
                    }

                }
            }
        } finally {
        }
    }

    private
    boolean selectsAny(Set<IVisualElement> set, iSelectionPredicate pred) {

        pred.begin(set, currentSelectionSet, null);
        for (IVisualElement e : set) {
            if (pred.is(e)) return true;
        }
        return false;
    }

    private static
    String shorter(String name) {
        if (name.length() < 20) return name;
        return name.substring(0, 17) + " ... " + name.substring(name.length() - 3, name.length());
    }

    private static
    void sortByName(ArrayList<IVisualElement> q) {

        Collections.sort(q, new Comparator<IVisualElement>() {
            public
            int compare(IVisualElement o1, IVisualElement o2) {

                // System.out.println(" compare <"+o1+" , "+o2+">");

                String n1 = o1.getProperty(IVisualElement.name);
                String n2 = o2.getProperty(IVisualElement.name);
                if (n1 == null && n2 == null) return 0;
                if (n1 == null) return 1;
                if (n2 == null) return -1;
                return n1.compareTo(n2);
            }
        });
    }

    protected
    void axisBackwards() {
        if (previousSelectionSet.size() > 0) {
            Set<IVisualElement> old = previousSelectionSet.remove(previousSelectionSet.size() - 1);
            selectOnly(currentSelectionSet, old);
            previousSelectionSet.remove(previousSelectionSet.size() - 1);
        }
    }

    @NextUpdate
    protected
    void axisForwards() {
        changeSelectedPaths(currentPathIs);
    }

    protected
    void changeSelectedPaths(TreeItem[] path) {

        Set<IVisualElement> newSelectionSet = new HashSet<IVisualElement>();
        everyElement = new HashSet<IVisualElement>(StandardFluidSheet.allVisualElements(root));
        if (path != null) {

            for (TreeItem p : path) {
                if (p != null) {
                    SelectionSet n = (SelectionSet) p.getData();

                    // if (n.predicate instanceof
                    // SelectionSetNodeView) {
                    // final GLComponentWindow window =
                    // iVisualElement.enclosingFrame
                    // .get(root);
                    //
                    // Launcher.getLauncher().registerUpdateable(
                    // new TravelTo(window,
                    // ((SelectionSetNodeView) n).view));
                    // } else

                    if (n != null) {
                        n.predicate.begin(everyElement, currentSelectionSet, n.getCached(root));
                        HashSet<IVisualElement> here = new HashSet<IVisualElement>();
                        for (IVisualElement e : everyElement) {
                            if (n.predicate.is(e)) {
                                here.add(e);
                            }
                        }

                        // todo,
                        // different
                        // conjunctions
                        n.setCached(here);
                        newSelectionSet.addAll(here);
                    }
                }
            }
        }
        if (!newSelectionSet.equals(currentSelectionSet)) {
            selectOnly(currentSelectionSet, newSelectionSet);
            reconstructRootModel();
        }
    }

    @NextUpdate(delay = 2)
    protected
    void changeSelection(Set<iComponent> selected) {
        if (suspendSelectionProcessing) {
            suspendSelectionProcessing = false;
            return;
        }

        Set<IVisualElement> newSelectionSet = new HashSet<IVisualElement>();
        for (iComponent c : selected) {
            IVisualElement ve = c.getVisualElement();
            if (ve != null) newSelectionSet.add(ve);
        }

        if (!newSelectionSet.equals(currentSelectionSet)) {
            currentSelectionSet.clear();
            currentSelectionSet.addAll(newSelectionSet);
            reconstructRootModel();
        }

        selectionUI.setLabel("<html><font face='"
                             + Constants.defaultFont
                             + "'><b>Currently selected \u2014 </b> <i>"
                             + describeSelection(selected)
                             + "</i>");

        if (currentAxis instanceof iExtendedSelectionAxis)
            ((iExtendedSelectionAxis) currentAxis).selectionChanged(currentSelectionSet);

    }

    @NextUpdate
    protected
    void markedSelectedPaths(TreeItem[] pp) {

        if (true) return;

        // System.out.println(" mark selected paths <" +
        // Arrays.asList(pp) + ">");

        Set<IVisualElement> newSelectionSet = new HashSet<IVisualElement>();
        everyElement = new HashSet<IVisualElement>(StandardFluidSheet.allVisualElements(root));

        // System.out.println(" all <" + everyElement + ">");

        for (TreeItem path : pp) {
            // System.out.println(" path :" + path);
            if (path != null) {
                SelectionSet n = (SelectionSet) path.getData();

                // System.out.println(" node <" + n + ">");
                if (n != null) {
                    n.predicate.begin(everyElement, currentSelectionSet, n.getCached(root));
                    HashSet<IVisualElement> here = new HashSet<IVisualElement>();
                    for (IVisualElement e : everyElement) {
                        if (n.predicate.is(e)) {
                            here.add(e);
                        }
                    }

                    // todo,
                    // different
                    // conjunctions
                    n.setCached(here);
                    newSelectionSet.addAll(here);
                }
            }
        }
        // System.out.println(" mark only <" + currentSelectionSet +
        // " " + newSelectionSet + ">");
        markOnly(currentSelectionSet, newSelectionSet);
    }

}
