package field.core.plugins;

import field.bytecode.protect.Woven;
import field.bytecode.protect.annotations.NextUpdate;
import field.core.dispatch.IVisualElement;
import field.core.dispatch.Rect;
import field.core.dispatch.VisualElementProperty;
import field.core.dispatch.override.IVisualElementOverridesAdaptor;
import field.core.dispatch.override.Ref;
import field.core.plugins.help.ContextualHelp;
import field.core.plugins.help.HelpBrowser;
import field.core.plugins.python.PythonPlugin;
import field.core.plugins.python.PythonPluginEditor;
import field.core.ui.NewInspector2;
import field.core.ui.NewInspector2.BaseControl;
import field.core.ui.NewInspector2.BooleanControl;
import field.core.ui.NewInspector2.ColorControl;
import field.core.ui.NewInspector2.SpinnerControl;
import field.core.ui.NewInspectorFromProperties;
import field.core.windowing.GLComponentWindow;
import field.core.windowing.components.SelectionGroup;
import field.core.windowing.components.SelectionGroup.iSelectionChanged;
import field.core.windowing.components.iComponent;
import field.launch.IUpdateable;
import field.launch.Launcher;
import field.math.graph.IMutableContainer;
import field.math.graph.NodeImpl;
import field.math.graph.visitors.hint.TraversalHint;
import field.util.collect.tuple.Triple;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import java.util.*;

@Woven
public
class NewInspectorPlugin implements iPlugin {

    public
    class LocalVisualElement extends NodeImpl<IVisualElement> implements IVisualElement {

        public
        <T> void deleteProperty(VisualElementProperty<T> p) {
        }

        public
        void dispose() {
        }

        public
        Rect getFrame(Rect out) {
            return null;
        }

        public
        <T> T getProperty(VisualElementProperty<T> p) {
            if (p == overrides) return (T) elementOverride;
            Object o = properties.get(p);
            return (T) o;
        }

        public
        String getUniqueID() {
            return pluginId;
        }

        public
        Map<Object, Object> payload() {
            return properties;
        }

        public
        void setFrame(Rect out) {
        }

        public
        IMutableContainer<Map<Object, Object>, IVisualElement> setPayload(Map<Object, Object> t) {
            properties = t;
            return this;
        }

        public
        <T> IVisualElement setProperty(VisualElementProperty<T> p, T to) {
            properties.put(p, to);
            return this;
        }

        public
        void setUniqueID(String uid) {
        }
    }

    public
    class Overrides extends IVisualElementOverridesAdaptor {

        @Override
        public
        <T> TraversalHint getProperty(IVisualElement source, VisualElementProperty<T> prop, Ref<T> ref) {
            if (prop.equals(inspectorPlugin)) {
                ref.set((T) NewInspectorPlugin.this);
            }
            return super.getProperty(source, prop, ref);
        }

        @Override
        public
        <T> TraversalHint setProperty(IVisualElement source, VisualElementProperty<T> prop, Ref<T> to) {
            if ((source == currentInspection)
                && !prop.equals(PythonPlugin.python_areas)
                && !prop.equals(PythonPlugin.python_source)
                && !prop.equals(PythonPluginEditor.python_customInsertPersistanceInfo)) {
                needsInspection = 30;
                // live update is commented out right now for
                // performance reasons
                // needsInspection = 0;
            }

            if (prop.equals(IVisualElement.name)) {
                source.setProperty(IVisualElement.dirty, true);
            }
            return super.setProperty(source, prop, to);
        }

    }

    public static HashMap<String, String> inspectableProperties = new HashMap<String, String>();

    public static final VisualElementProperty<NewInspectorPlugin> inspectorPlugin =
            new VisualElementProperty<NewInspectorPlugin>("inspectorPlugin_");

    public static
    void addInspectableProperty(String propertyName, String displayName) {
        inspectableProperties.put(propertyName, displayName);
    }

    private NewInspector2 inspector;
    private NewInspectorFromProperties helper;

    private SelectionGroup<iComponent> group;

    private IVisualElement currentInspection;

    protected static final String pluginId = "//inspector_python";

    protected LocalVisualElement lve;

    protected IVisualElement root;

    protected Overrides elementOverride;

    boolean[] ex = new boolean[0];

    int needsInspection = 0;

    Map<Object, Object> properties = new HashMap<Object, Object>();

    public
    NewInspectorPlugin() {
        lve = new LocalVisualElement();

        // TODO swt color well
        {
            LinkedHashMap<String, Class<? extends BaseControl>> decorationSet =
                    new LinkedHashMap<String, Class<? extends BaseControl>>();
            decorationSet.put("color1", ColorControl.class);
            decorationSet.put("color2", ColorControl.class);
            decorationSet.put("isWindowSpace", BooleanControl.class);

            NewInspectorFromProperties.activeSets.add(new Triple<String, LinkedHashMap<String, Class<? extends BaseControl>>, Boolean>("Decoration",
                                                                                                                                       decorationSet,
                                                                                                                                       false));
        }
        {
            LinkedHashMap<String, Class<? extends BaseControl>> decorationSet =
                    new LinkedHashMap<String, Class<? extends BaseControl>>();
            decorationSet.put("autoExecuteDelay", SpinnerControl.class);

            NewInspectorFromProperties.activeSets.add(new Triple<String, LinkedHashMap<String, Class<? extends BaseControl>>, Boolean>("Execution (advanced)",
                                                                                                                                       decorationSet,
                                                                                                                                       false));
        }
    }

    public
    void close() {
    }

    public static
    String formatName(String name) {

        if (inspectableProperties.containsKey(name)) return inspectableProperties.get(name);

        int li = name.lastIndexOf("_");
        if (li == -1) return name;
        if (li < name.length() - 3) return name;
        return "<b>" + name.substring(0, li + 1) + "</b><font size=-3><i>" + name.substring(li + 1) + "</i>";
    }

    public
    Object getPersistanceInformation() {
        return null;
    }

    public
    IVisualElement getWellKnownVisualElement(String id) {
        if (id.equals(pluginId)) return lve;
        return null;
    }

    public
    void registeredWith(final IVisualElement root) {
        this.root = root;

        inspector = new NewInspector2() {
            protected
            java.util.LinkedHashMap<String, IUpdateable> getMenuItems() {
                return helper.getMenuItems(new IUpdateable() {

                    @Override
                    public
                    void update() {
                        changeSelection(root.getProperty(IVisualElement.selectionGroup).getSelection());
                    }
                });
            }
        };

        helper = new NewInspectorFromProperties(inspector);

        // UbiquitousLinks.links.install(inspector.tree);

        elementOverride = createElementOverrides();
        root.addChild(lve);
        group = root.getProperty(IVisualElement.selectionGroup);

        group.registerNotification(new iSelectionChanged<iComponent>() {
            public
            void selectionChanged(Set<iComponent> selected) {
                changeSelection(selected);
            }
        });

        final GLComponentWindow window = root.getProperty(IVisualElement.enclosingFrame);

        installHelpBrowser(root);
    }

    @NextUpdate(delay = 3)
    private
    void installHelpBrowser(final IVisualElement root) {
        HelpBrowser h = HelpBrowser.helpBrowser.get(root);
        ContextualHelp ch = h.getContextualHelp();
        ch.addContextualHelpForWidget("inspector",
                                      inspector.getContents(),
                                      ContextualHelp.providerForStaticMarkdownResource("contextual/inspector.md"),
                                      50);
    }

    public
    void setPersistanceInformation(Object o) {
    }

    int consecutiveUpdates = 0;

    int suppressChangeSelection = 0;

    boolean updatedLast = false;

    private ArrayList<IVisualElement> sel = new ArrayList<IVisualElement>();

    public
    void update() {
        needsInspection--;
        if (needsInspection == 0) changeSelection(root.getProperty(IVisualElement.selectionGroup).getSelection());

        if (needsInspection < 0) needsInspection = 0;
    }

    @NextUpdate(delay = 15)
    protected
    void changeSelection(Set<iComponent> selected) {

        // if (FocusManager.getCurrentManager().getFocusOwner()
        // instanceof JTextField) {
        // needsInspection = 15;
        // return;
        // }

        Control focusControl = Launcher.display.getFocusControl();

        try {
            if (Launcher.display.getFocusControl().getShell() == inspector.getShell()) {
                if (focusControl instanceof Text || focusControl instanceof Spinner) {
                    needsInspection = 15;
                    return;
                }
            }
        } catch (NullPointerException e) {
        }
        if (selected.isEmpty()) inspector.clear();

        sel = new ArrayList<IVisualElement>();
        for (iComponent c : selected) {
            IVisualElement m = c.getVisualElement();
            if (m != null) sel.add(m);
        }
        inspector.clear();
        helper.rebuild(sel);

        // inspector.setMenu(helper.getMenu(new iUpdateable() {
        //
        // @Override
        // public void update() {
        // changeSelection(root.getProperty(iVisualElement.selectionGroup).getSelection());
        // }
        // }));
    }

    protected
    Overrides createElementOverrides() {
        return new Overrides() {
            @Override
            public
            <T> TraversalHint setProperty(IVisualElement source, VisualElementProperty<T> prop, Ref<T> to) {
                if (sel.contains(source)
                    && !prop.equals(PythonPlugin.python_areas)
                    && !prop.equals(PythonPlugin.python_source)
                    && !prop.equals(PythonPluginEditor.python_customInsertPersistanceInfo)) {
                    needsInspection = 30;
                }

                if (prop.equals(IVisualElement.name)) {
                    source.setProperty(IVisualElement.dirty, true);
                }
                return super.setProperty(source, prop, to);
            }

            @Override
            public
            TraversalHint shouldChangeFrame(IVisualElement source, Rect newFrame, Rect oldFrame, boolean now) {
                if (sel.contains(source)) needsInspection = 30;
                return super.shouldChangeFrame(source, newFrame, oldFrame, now);
            }
        };
    }

}
