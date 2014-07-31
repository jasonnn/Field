package field.core;

import com.thoughtworks.xstream.io.StreamException;
import field.bytecode.protect.Woven;
import field.bytecode.protect.annotations.DispatchOverTopology;
import field.bytecode.protect.annotations.NextUpdate;
import field.bytecode.protect.dispatch.Cont;
import field.bytecode.protect.trampoline.Trampoline2;
import field.core.dispatch.FastVisualElementOverridesPropertyCombiner;
import field.core.dispatch.FastVisualElementOverridesPropertyCombiner.iCombiner;
import field.core.dispatch.IVisualElement;
import field.core.dispatch.IVisualElementOverrides;
import field.core.dispatch.VisualElement;
import field.core.dispatch.IVisualElement.Rect;
import field.core.dispatch.IVisualElement.VisualElementProperty;
import field.core.execution.*;
import field.core.execution.PythonScriptingSystem.Promise;
import field.core.persistance.FluidCopyPastePersistence;
import field.core.persistance.FluidCopyPastePersistence.iNotifyDuplication;
import field.core.persistance.FluidPersistence;
import field.core.persistance.PackageTools;
import field.core.plugins.BindingPlugin;
import field.core.plugins.NewInspectorPlugin;
import field.core.plugins.SimpleConstraints;
import field.core.plugins.autoexecute.AutoExecutePythonPlugin;
import field.core.plugins.connection.Connections;
import field.core.plugins.drawing.BasicDrawingPlugin;
import field.core.plugins.drawing.SplineComputingOverride;
import field.core.plugins.drawing.ToolPalette2;
import field.core.plugins.drawing.opengl.CachedLine;
import field.core.plugins.drawing.opengl.iLinearGraphicsContext;
import field.core.plugins.help.HelpBrowser;
import field.core.plugins.history.ElementFileSystemTreePlugin;
import field.core.plugins.history.HGVersioningSystem;
import field.core.plugins.history.VersioningSystem;
import field.core.plugins.iPlugin;
import field.core.plugins.pseudo.PseudoPropertiesPlugin;
import field.core.plugins.pseudo.PseudoPropertiesPlugin.Beginner;
import field.core.plugins.pseudo.PseudoPropertiesPlugin.Ender;
import field.core.plugins.python.PythonPlugin;
import field.core.plugins.python.PythonPluginEditor;
import field.core.plugins.selection.ToolBarFolder;
import field.core.plugins.snip.SnippetsPlugin;
import field.core.plugins.snip.TreeBrowserPlugin;
import field.core.ui.*;
import field.core.ui.FieldMenus2.Sheet;
import field.core.ui.text.GlobalKeyboardShortcuts;
import field.core.windowing.BetterSash;
import field.core.windowing.GLComponentWindow;
import field.core.windowing.WindowSpaceBox;
import field.core.windowing.components.*;
import field.core.windowing.overlay.OverlayAnimationManager;
import field.launch.IUpdateable;
import field.launch.Launcher;
import field.launch.SystemProperties;
import field.math.abstraction.IAcceptor;
import field.math.abstraction.IFloatProvider;
import field.math.graph.NodeImpl;
import field.math.graph.TopologyViewOfGraphNodes;
import field.math.graph.IMutableContainer;
import field.math.graph.visitors.hint.StandardTraversalHint;
import field.math.graph.visitors.hint.TraversalHint;
import field.math.graph.visitors.TopologyVisitor_breadthFirst;
import field.math.linalg.Vector2;
import field.math.linalg.Vector4;
import field.namespace.context.Dispatch;
import field.namespace.context.SimpleContextTopology;
import field.util.collect.tuple.Triple;
import field.util.Dict.Prop;
import field.util.PythonUtils;
import field.util.TaskQueue;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.*;
import java.rmi.server.UID;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Pattern;

@Woven
public
class StandardFluidSheet implements IVisualElementOverrides, IUpdateable, iHasVisualElementRoot {

    public static final VisualElementProperty<String> keyboardShortcut =
            new VisualElementProperty<String>("keyboardShortcut");

    public
    class RootSheetElement extends NodeImpl<IVisualElement> implements IVisualElement {

        public
        <T> void deleteProperty(VisualElementProperty<T> p) {
            rootProperties.remove(p);
        }

        public
        void dispose() {
        }

        public
        Rect getFrame(Rect out) {
            return null;
        }

        public
        <T> T getProperty(IVisualElement.VisualElementProperty<T> p) {
            if (p == overrides) return (T) StandardFluidSheet.this;
            Object o = rootProperties.get(p);
            return (T) o;
        }

        public
        String getUniqueID() {
            return rootSheetElement_uid;
        }

        public
        Map<Object, Object> payload() {
            return rootProperties;
        }

        public
        void setFrame(Rect out) {
        }

        public
        IMutableContainer<Map<Object, Object>, IVisualElement> setPayload(Map<Object, Object> t) {
            return this;
        }

        public
        <T> IVisualElement setProperty(IVisualElement.VisualElementProperty<T> p, T to) {
            rootProperties.put(p, to);
            return this;
        }

        public
        void setUniqueID(String uid) {
        }

        @Override
        public
        String toString() {
            return "root <" + System.identityHashCode(this) + '>';
        }

    }

    public static final SimpleContextTopology context = SimpleContextTopology.newInstance();

    public static final VisualElementProperty<VersioningSystem> versioningSystem =
            new VisualElementProperty<VersioningSystem>("versioningSystem_");

    public static String rootSheetElement_uid = "//rootSheetElement";

    protected static int uniq = 0;

    public static
    List<IVisualElement> allVisualElements(IVisualElement root) {
        final List<IVisualElement> ret = new ArrayList<IVisualElement>();
        new TopologyVisitor_breadthFirst<IVisualElement>(true) {
            @Override
            protected
            TraversalHint visit(IVisualElement n) {
                ret.add(n);
                return StandardTraversalHint.CONTINUE;
            }

        }.apply(new TopologyViewOfGraphNodes<IVisualElement>(false).setEverything(true), root);
        return ret;
    }

    public static
    IVisualElement findVisualElement(IVisualElement root, final String s) {
        final IVisualElement[] ans = new IVisualElement[1];

        TopologyVisitor_breadthFirst<IVisualElement> search = new TopologyVisitor_breadthFirst<IVisualElement>(true) {
            @Override
            protected
            TraversalHint visit(IVisualElement n) {
                if (n.getUniqueID().equals(s)) {
                    ans[0] = n;
                    return StandardTraversalHint.STOP;
                }
                return StandardTraversalHint.CONTINUE;
            }

        };

        search.apply(new TopologyViewOfGraphNodes<IVisualElement>(false).setEverything(true), root);
        return ans[0];
    }

    public static
    IVisualElement findVisualElementWithName(IVisualElement root, final String pattern) {

        final Pattern p = Pattern.compile(pattern);

        final IVisualElement[] ans = new IVisualElement[1];

        TopologyVisitor_breadthFirst<IVisualElement> search = new TopologyVisitor_breadthFirst<IVisualElement>(true) {
            @Override
            protected
            TraversalHint visit(IVisualElement n) {
                String name = n.getProperty(IVisualElement.name);
                if ((name != null) && p.matcher(name).matches()) {
                    ans[0] = n;
                    return StandardTraversalHint.STOP;
                }
                return StandardTraversalHint.CONTINUE;
            }

        };

        search.apply(new TopologyViewOfGraphNodes<IVisualElement>(false).setEverything(true), root);
        return ans[0];
    }

    public static
    List<IVisualElement> findVisualElementWithNameExpression(IVisualElement root, final String pattern) {

        final Pattern p = Pattern.compile(pattern);

        final List<IVisualElement> ans = new ArrayList<IVisualElement>();

        TopologyVisitor_breadthFirst<IVisualElement> search = new TopologyVisitor_breadthFirst<IVisualElement>(true) {
            @Override
            protected
            TraversalHint visit(IVisualElement n) {
                String name = n.getProperty(IVisualElement.name);
                if ((name != null) && p.matcher(name).matches()) {
                    ans.add(n);
                }
                return StandardTraversalHint.CONTINUE;
            }

        };

        search.apply(new TopologyViewOfGraphNodes<IVisualElement>(false).setEverything(true), root);
        return ans;
    }

    public static
    StandardFluidSheet scratchBegin(VersioningSystem system) {
        return scratchBegin(system,
                            SystemProperties.getProperty("fluid.scratch",
                                                         SystemProperties.getProperty("main.class") + ".xml"));
    }

    public static
    StandardFluidSheet scratchBegin(VersioningSystem system, String filename) {
        final StandardFluidSheet sheet = new StandardFluidSheet(filename, system);
        sheet.setFilename(filename);

        Launcher.getLauncher().registerUpdateable(sheet);

        sheet.registerPlugin(new PythonPluginEditor(SystemProperties.getDirProperty("versioning.dir"), filename));

        ToolBarFolder folder = new ToolBarFolder();
        ToolBarFolder.currentFolder = folder;
        sheet.registerPlugin(new NewInspectorPlugin());
        sheet.registerPlugin(new BindingPlugin());
        // sheet.registerPlugin(new HelpBrowser());

        Connections connections = new Connections(sheet, sheet.rootSheetElement);
        SimpleConstraints constraints = new SimpleConstraints();
        sheet.registerPlugin(connections);
        sheet.registerPlugin(constraints);
        sheet.registerPlugin(new PseudoPropertiesPlugin());
        sheet.registerPlugin(new AutoExecutePythonPlugin());
        //
        // sheet.registerPlugin(new LoggingPlugin(sheet));
        // sheet.registerPlugin(new HistoryPlugin(sheet,
        // SystemProperties.getDirProperty("versioning.dir"), filename,
        // system));
        // sheet.registerPlugin(new ComplexConstraints());

        sheet.registerPlugin(new BasicDrawingPlugin());

        sheet.registerPlugin(new SnippetsPlugin());

        sheet.registerPlugin(new ElementFileSystemTreePlugin());

        sheet.registerPlugin(new HelpBrowser());
        ToolBarFolder.helpFolder.select(0);

        sheet.registerPlugin(new TreeBrowserPlugin());

        sheet.rootSheetElement.setProperty(IVisualElement.toolPalette2, new ToolPalette2());
        registerExtendedPlugins(sheet);

        ((SashForm) sheet.window.leftComp1).setWeights(new int[]{4, 4, 1});
        new BetterSash((SashForm) sheet.window.leftComp1, false);

        PythonInterface.getPythonInterface().setVariable("T", Launcher.mainInstance);
        PythonInterface.getPythonInterface().setVariable("S", sheet);

        new PythonUtils().install();

        folder.selectFirst();

        return sheet;
    }

    public static
    void scratchEnd(final StandardFluidSheet sheet, VersioningSystem system) {

        String filename = SystemProperties.getDirProperty("versioning.dir") + sheet.getFilename() + "/sheet.xml";
        try {
            sheet.load(new BufferedReader(new FileReader(filename), 1024 * 1 * 1024));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Launcher.getLauncher().addShutdown(sheet.shutdownhook = new IUpdateable() {

            @Override
            public
            void update() {
                // System.out.println(" inside shutdown hook ");

                singleThreadedSave(sheet);
            }
        });

        Triple<VisualElement, PlainDraggableComponent, TemporalSliderOverrides> created =
                TemporalSliderOverrides.newTemporalSlider("time", sheet.getRoot());

        sheet.deferredRequestRepaint();

    }

    @NextUpdate(delay = 2)
    protected
    void deferredRequestRepaint() {
        window.requestRepaint();
    }

    public static
    StandardFluidSheet versionedScratch(String filenameInWorkspace) {
        VersioningSystem vs = VersioningSystem.newDefault(filenameInWorkspace);
        StandardFluidSheet sheet = StandardFluidSheet.scratchBegin(vs, filenameInWorkspace);
        StandardFluidSheet.scratchEnd(sheet, vs);
        return sheet;
    }

    private static
    void registerExtendedPlugins(final StandardFluidSheet sheet) {
        HashSet<String> p = Trampoline2.plugins;
        // System.out.println(" extended plugins are <" + p + ">");
        for (String s : p) {
            // System.out.println("   loading plugin <" + s + ">");
            try {
                Class<?> loaded = sheet.getClass().getClassLoader().loadClass(s);
                iPlugin instance = (iPlugin) loaded.newInstance();
                sheet.registerPlugin(instance);
            } catch (ClassNotFoundException e) {
                // System.out.println("   error loading plugin <"
                // + s + ">, continuing");
                e.printStackTrace();
            } catch (InstantiationException e) {
                // System.out.println("   error loading plugin <"
                // + s + ">, continuing");
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // System.out.println("   error loading plugin <"
                // + s + ">, continuing");
                e.printStackTrace();
            } catch (Throwable t) {
                // System.out.println("   error loading plugin <"
                // + s + ">, continuing");
                t.printStackTrace();
            }
        }
    }

    private final GLComponentWindow window;

    private final MainSelectionGroup group;

    private FluidPersistence persistence;

    private final PythonScriptingSystem pss;

    private BasicRunner basicRunner;

    private final MainSelectionGroup markingGroup;

    private final FluidCopyPastePersistence copyPastePersisence;

    private IUpdateable shutdownhook;

    private String filename;

    protected IVisualElement rootSheetElement;

    protected HashMap<Object, Object> rootProperties = new HashMap<Object, Object>();

    protected VersioningSystem vs;

    // implementation of iVisualElementOverrides

    String name = null;

    TaskQueue eventProcessingQueue = new TaskQueue();

    boolean tick = false;
    boolean drawTick = false;

    List<iPlugin> plugins = new ArrayList<iPlugin>();

    private final BasicRunner multiThreadedRunner;

    private final DragDuplicator dragDuplicator;

    public
    StandardFluidSheet() {
        this("sheet:" + (uniq++), null);
    }

    public
    StandardFluidSheet(String name, VersioningSystem vs) {

        context.begin(name);
        this.name = name;

        // FluidSubstance.init();

        window = new GLComponentWindow(name, eventProcessingQueue);
        // SavedFramePositions.doFrame(window.getFrame(), "Canvas");

        Launcher.getLauncher().registerUpdateable(window);

        group = new MainSelectionGroup();
        markingGroup = new MainSelectionGroup();

        RootComponent r1 = new RootComponent(window.getFrame());
        window.getRoot().addComponent(r1);

        r1.addToSelectionGroup(group);

        rootSheetElement = new RootSheetElement();
        window.setEditorSpaceHelper(this.rootSheetElement);

        r1.setOverrides(new Dispatch<IVisualElement, IVisualElementOverrides>(IVisualElementOverrides.topology).getOverrideProxyFor(rootSheetElement,
                                                                                                                                    IVisualElementOverrides.class));

        rootSheetElement.setProperty(IVisualElement.enclosingFrame, window);
        rootSheetElement.setProperty(IVisualElement.rootComponent, r1);
        rootSheetElement.setProperty(IVisualElement.localView, null);
        rootSheetElement.setProperty(IVisualElement.sheetView, this);
        GlobalKeyboardShortcuts gks = new GlobalKeyboardShortcuts();
        rootSheetElement.setProperty(GlobalKeyboardShortcuts.shortcuts, gks);
        gks.add(new GlobalKeyboardShortcuts.Shortcut('s', Platform.getCommandModifier(), Platform.getCommandModifier()),
                new IUpdateable() {

                    @Override
                    public
                    void update() {
                        saveNow();
                    }
                });

        gks.add(new GlobalKeyboardShortcuts.Shortcut(0, 0, 0) {
            @Override
            public
            boolean matches(char c, int code, int state) {
                if ((state & Platform.getCommandModifier()) != 0) {
                    List<IVisualElement> e = allVisualElements(getRoot());
                    String match = String.valueOf(Character.toLowerCase(c));
                    for (IVisualElement ee : e) {
                        String s = ee.getProperty(keyboardShortcut);

                        if (s != null) {
                            if (s.equals(match)) {
                                if ((state & SWT.SHIFT) == 0) beginExecution(ee);
                                else endExecution(ee);
                                return true;
                            }
                        }

                    }
                }
                return false;
            }
        }, new IUpdateable() {

            @Override
            public
            void update() {

            }
        });

        // rootSheetElement.setProperty(iVisualElement.
        // toolPalette,
        // new ToolPalette());
        rootSheetElement.setProperty(IVisualElement.selectionGroup, group);
        rootSheetElement.setProperty(IVisualElement.markingGroup, markingGroup);
        rootSheetElement.setProperty(IVisualElement.name, "((sheet root))");

        copyPastePersisence = new FluidCopyPastePersistence(new FluidPersistence.iWellKnownElementResolver() {
            public
            IVisualElement getWellKnownElement(String uid) {
                if (uid.equals(rootSheetElement_uid)) return rootSheetElement;
                for (iPlugin p : plugins) {
                    IVisualElement ve = p.getWellKnownVisualElement(uid);
                    if (ve != null) {
                        return ve;
                    }
                }
                // System.out.println(" WARNING: not well known in copySource <"
                // + uid + ">");
                return null;
            }
        }, new iNotifyDuplication() {
            public
            String beginNewUID(String uidToCopy) {
                String target = "__" + new UID().toString();
                // System.out.println(" copied uid <" +
                // uidToCopy + "> to <" + target + ">");
                return target;
            }

            public
            void endCopy(IVisualElement newCopy, IVisualElement old) {
                StandardFluidSheet.this.endCopy(newCopy, old);
            }
        });

        IVisualElement.copyPaste.set(rootSheetElement, rootSheetElement, copyPastePersisence);

        pss = new PythonScriptingSystem() {
            @Override
            protected
            void filterIntersections(LinkedHashSet ret) {
                Iterator n = ret.iterator();
                while (n.hasNext()) {
                    Promise nn = (Promise) n.next();
                    IVisualElement elem = (IVisualElement) pss.keyForPromise(nn);
                    Boolean m = elem.getProperty(WindowSpaceBox.isWindowSpace);
                    if ((m != null) && m) {
                        n.remove();
                    }
                }
            }
        };
        basicRunner = new BasicRunner(pss, 0) {
            @Override
            protected
            boolean filter(Promise p) {
                IVisualElement v = (IVisualElement) system.keyForPromise(p);
                if (v == null) return false;
                return iExecutesPromise.promiseExecution.get(v) == this;
            }
        };

        multiThreadedRunner = /*
                 * ThreadedLauncher.getLauncher() != null ? new
				 * BasicRunner(pss, 0) {
				 * 
				 * @Override protected boolean filter(Promise p)
				 * { iVisualElement v = (iVisualElement)
				 * system.keyForPromise(p); if (v == null)
				 * return false; return
				 * iExecutesPromise.promiseExecution.get(v) ==
				 * this; } } :
				 */basicRunner;

        // if (ThreadedLauncher.getLauncher() != null) {
        // ThreadedLauncher.addThreadedUpdatable(new iUpdateable() {
        //
        // public void update() {
        // TimeSystem ts =
        // rootSheetElement.getProperty(TemporalSliderOverrides.currentTimeSystem);
        // // ;//System.out.println(" inside multithreaded runner <"
        // // + ts + ">");
        // if (ts != null) {
        // ts.update();
        // double tsTimeNow = ts.evaluate();
        // multiThreadedRunner.update((float) tsTimeNow);
        // } else
        // multiThreadedRunner.update(-1);
        // }
        // });
        // }

        rootSheetElement.setProperty(PythonScriptingSystem.pythonScriptingSystem, pss);
        rootSheetElement.setProperty(iExecutesPromise.promiseExecution, basicRunner);
        rootSheetElement.setProperty(BasicRunner.basicRunner, basicRunner);
        rootSheetElement.setProperty(IVisualElement.multithreadedRunner, multiThreadedRunner);

        this.vs = vs;
        rootSheetElement.setProperty(versioningSystem, vs);

        UbiquitousLinks.sheets.add(this);

        GlassComponent g1 = new GlassComponent(r1, dragDuplicator = new DragDuplicator(group, rootSheetElement));
        rootSheetElement.setProperty(IVisualElement.glassComponent, g1);
        window.getRoot().addComponent(g1);

        rootSheetElement.setProperty(IVisualElement.name, "root");

        HashMap<String, Object> c1 = SystemProperties.getProperties();
        for (Entry<String, Object> e : c1.entrySet()) {
            rootSheetElement.setProperty(new VisualElementProperty(e.getKey()), e.getValue());
        }

    }

    public
    TraversalHint added(IVisualElement newSource) {
        iComponent component = newSource.getProperty(IVisualElement.localView);

        if (component != null) window.getRoot().addComponent(component);
        else {
            System.err.println(" !!!!!!!!! no component for <" + newSource + "> !!!!!!!!!!!!");
        }

        if (IVisualElement.isRenderer.getBoolean(newSource, false))
            iExecutesPromise.promiseExecution.set(newSource, newSource, multiThreadedRunner);

        window.getRoot().requestRedisplay();
        return StandardTraversalHint.CONTINUE;
    }

    public
    void addToSheet(IVisualElement newSource) {
        newSource.addChild(rootSheetElement);
        new IVisualElementOverrides.MakeDispatchProxy().getBackwardsOverrideProxyFor(newSource).added(newSource);
        new IVisualElementOverrides.MakeDispatchProxy().getOverrideProxyFor(newSource).added(newSource);
    }

    ThreadLocal<LinkedHashSet<IVisualElement>> inprogress = new ThreadLocal<LinkedHashSet<IVisualElement>>() {
        @Override
        public
        LinkedHashSet<IVisualElement> get() {
            return new LinkedHashSet<IVisualElement>();
        }
    };

    public
    TraversalHint beginExecution(final IVisualElement source) {

        if (inprogress.get().contains(source)) return StandardTraversalHint.STOP;

        System.out.println(" inprogress <" + inprogress.get() + '>');
        inprogress.get().add(source);

        try {

            // should be
            // lookup to
            // support

            // System.out.println(" begin exec <" + source + ">");

            PythonPlugin p = PythonPlugin.python_plugin.get(source);
            if (p instanceof PythonPluginEditor) try {
                ((PythonPluginEditor) p).getEditor()
                                        .getInput()
                                        .append("Running '")
                                        .append(source.getProperty(IVisualElement.name))
                                        .append('\'');
            } catch (IOException e) {
                e.printStackTrace();
            }

            PythonScriptingSystem pss = PythonScriptingSystem.pythonScriptingSystem.get(source);
            iExecutesPromise runner = iExecutesPromise.promiseExecution.get(source);

            Promise promise = pss.promiseForKey(source);

            Vector2 currentMousePosition = GLComponentWindow.getCurrentWindow(null).getCurrentMousePosition();
            PythonInterface.getPythonInterface().setVariable("_y", new Float(currentMousePosition.y));

            // todo: execute
            // in correct
            // context (this
            // is handled
            // for us
            // automatically,
            // if we are
            // using the
            // main runner
            // (which we
            // probably
            // aren't)

            if (promise != null) {
                runner.addActive(new IFloatProvider() {

                    public
                    float evaluate() {
                        Vector2 v = window.getCurrentMouseInWindowCoordinates();

                        Rect o = new Rect(0, 0, 0, 0);
                        source.getFrame(o);

                        return v.x;
                    }

                }, promise);
            }

            SnippetsPlugin.addText(source,
                                   "_self.find[\""
                                   + source.getProperty(IVisualElement.name)
                                   + "\"].begin()\n_self.begin()\n_self.end()\n_self.find[\""
                                   + source.getProperty(IVisualElement.name)
                                   + "\"].end()",
                                   "element started",
                                   new String[]{"start running an element",
                                                "start running </i>this<i> element",
                                                "STOP running </i>this<i> element'",
                                                "STOP running an element"},
                                   "alternative form");

            return StandardTraversalHint.CONTINUE;
        } finally {
            inprogress.get().remove(source);
        }
    }

    public
    void close() {

        for (iPlugin p : plugins)
            p.close();

        Launcher.getLauncher().registerUpdateable(this);
        Launcher.getLauncher().deregisterUpdateable(window);
        window.getFrame().setVisible(false);
        window.getFrame().dispose();

        if (shutdownhook != null) Launcher.getLauncher().removeShutdownHook(shutdownhook);
    }

    public
    TraversalHint deleted(IVisualElement source) {
        iComponent component = source.getProperty(IVisualElement.localView);
        if (component != null) window.getRoot().removeComponent(component);
        window.getRoot().requestRedisplay();

        if (vs != null) {
            vs.notifyElementDeleted(source);
        }

        group.removeFromSelection(source.getProperty(IVisualElement.localView));
        markingGroup.removeFromSelection(source.getProperty(IVisualElement.localView));

        return StandardTraversalHint.CONTINUE;
    }

    public
    <T> TraversalHint deleteProperty(IVisualElement source, VisualElementProperty<T> prop) {
        if (source == rootSheetElement) {
            VisualElementProperty<T> a = prop.getAliasedTo();
            while (a != null) {
                prop = a;
                a = a.getAliasedTo();
            }

            rootSheetElement.deleteProperty(prop);
        }
        if (prop.containsSuffix("v")) {
            if (vs != null) {
                vs.notifyPropertyDeleted(prop, source);
            }
        }

        return StandardTraversalHint.CONTINUE;
    }

    public
    TraversalHint endExecution(IVisualElement source) {

        Ref<PythonScriptingSystem> refPss = new Ref<PythonScriptingSystem>(null);
        new IVisualElementOverrides.MakeDispatchProxy().getOverrideProxyFor(source)
                                                       .getProperty(source,
                                                                    PythonScriptingSystem.pythonScriptingSystem,
                                                                    refPss);
        assert refPss.get() != null;

        Ref<iExecutesPromise> refRunner = new Ref<iExecutesPromise>(null);
        new IVisualElementOverrides.MakeDispatchProxy().getOverrideProxyFor(source)
                                                       .getProperty(source,
                                                                    iExecutesPromise.promiseExecution,
                                                                    refRunner);
        assert refRunner.get() != null;

        Promise p = refPss.get().promiseForKey(source);

        if (p != null) {
            refRunner.get().removeActive(p);
        }

        return StandardTraversalHint.CONTINUE;
    }

    public
    BasicRunner getBasicRunner() {
        return basicRunner;
    }

    public
    <T> TraversalHint getProperty(IVisualElement source, IVisualElement.VisualElementProperty<T> property, Ref<T> ref) {
        if (rootProperties.containsKey(property)) {
            VisualElementProperty<T> a = property.getAliasedTo();
            while (a != null) {
                property = a;
                a = a.getAliasedTo();
            }

            if (ref.get() == null) ref.set((T) rootProperties.get(property), rootSheetElement);

        }

        return StandardTraversalHint.CONTINUE;
    }

    public
    IVisualElement getRoot() {
        return rootSheetElement;
    }

    public
    GLComponentWindow getWindow() {
        return window;
    }

    public
    TraversalHint handleKeyboardEvent(IVisualElement newSource, Event event) {

        if (event == null) return StandardTraversalHint.CONTINUE;

        if (!event.doit) return StandardTraversalHint.CONTINUE;

        if (tick && (event.type == SWT.KeyDown) && (event.character == 'n')) {

            tick = false;
            List<IVisualElement> all = StandardFluidSheet.allVisualElements(getRoot());
            IVisualElement ee = getRoot();
            boolean exclusive = false;
            for (IVisualElement a : all) {
                Boolean f = a.getProperty(PythonPluginEditor.python_isDefaultGroup);
                if ((f != null) && f) {
                    Boolean ex = a.getProperty(PythonPluginEditor.python_isDefaultGroupExclusive);
                    if ((ex != null) && ex) exclusive = true;
                    ee = a;
                    break;
                }
            }

            GLComponentWindow frame = IVisualElement.enclosingFrame.get(getRoot());

            Rect bounds = new Rect(30, 30, 60, 60);
            if (frame != null) {
                Vector2 cmp = frame.getCurrentMouseInWindowCoordinates();
                bounds.x = cmp.x - 25;
                bounds.y = cmp.y + 25;
            }

            Triple<VisualElement, DraggableComponent, DefaultOverride> created = VisualElement.createAddAndName(bounds,
                                                                                                                ee,
                                                                                                                "untitled",
                                                                                                                VisualElement.class,
                                                                                                                DraggableComponent.class,
                                                                                                                DefaultOverride.class,
                                                                                                                null);

            if ((ee != getRoot()) && !exclusive) {
                created.left.addChild(getRoot());
            }
        }
        if (tick && (event.type == SWT.KeyDown) && (event.keyCode == 13)) {

            tick = false;

            boolean success = ((PythonPluginEditor) PythonPluginEditor.python_plugin.get(rootSheetElement)).getEditor()
                                                                                                           .getInputEditor()
                                                                                                           .forceFocus();
            // System.out.println(" forcing focus " + success);

        }
        else if (tick && (event.type == SWT.KeyDown) && (event.character == 'p')) {
            tick = false;
            List<IVisualElement> all = StandardFluidSheet.allVisualElements(getRoot());
            IVisualElement ee = getRoot();
            boolean exclusive = false;
            for (IVisualElement a : all) {
                Boolean f = a.getProperty(PythonPluginEditor.python_isDefaultGroup);
                if ((f != null) && f) {
                    Boolean ex = a.getProperty(PythonPluginEditor.python_isDefaultGroupExclusive);
                    if ((ex != null) && ex) exclusive = true;
                    ee = a;
                    break;
                }
            }
            GLComponentWindow frame = IVisualElement.enclosingFrame.get(getRoot());

            Rect bounds = new Rect(30, 30, 50, 50);
            if (frame != null) {
                Vector2 cmp = frame.getCurrentMouseInWindowCoordinates();
                bounds.x = cmp.x - 25;
                bounds.y = cmp.y + 25;
            }

            Triple<VisualElement, PlainDraggableComponent, SplineComputingOverride> created =
                    VisualElement.createAddAndName(bounds,
                                                   ee,
                                                   "untitled",
                                                   VisualElement.class,
                                                   PlainDraggableComponent.class,
                                                   SplineComputingOverride.class,
                                                   null);

            if ((ee != getRoot()) && !exclusive) {
                created.left.addChild(getRoot());
            }
        }
        else if (tick && (event.type == SWT.KeyDown) && (event.keyCode == 'c') && ((event.stateMask
                                                                                    & Platform.getCommandModifier())
                                                                                   != 0)) {
            // System.out.println(" copying file reference to clipboard ");
            tick = false;
            File tmp = PackageTools.newTempFileWithSelected(rootSheetElement, "copied");
            PackageTools.copyFileReferenceToClipboard(tmp.getAbsolutePath());

            OverlayAnimationManager.notifyAsText(getRoot(), "Copied to clipboard", null);

            // OverlayAnimationManager m =
            // window.getOverlayAnimationManager();
            // if (m!=null)
            // {
            // m.no
            // }

        }
        else if (tick && (event.type == SWT.KeyDown) && (event.keyCode == 'v') && ((event.stateMask
                                                                                    & Platform.getCommandModifier())
                                                                                   != 0)) {
            try {
                tick = false;

                Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
                Transferable t = c.getContents(null);
                Object data = c.getData(DataFlavor.javaFileListFlavor);
                if (((List) data).get(0) instanceof File) {
                    if (((File) ((List) data).get(0)).getName().endsWith(".fieldpackage")) {
                        OverlayAnimationManager.notifyAsText(getRoot(), "Pasted from clipboard", null);
                        PackageTools.importFieldPackage(rootSheetElement,
                                                        ((File) ((List) data).get(0)).getAbsolutePath());
                    }
                }
                else {
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (tick && (event.type == SWT.KeyDown) && (event.character == 's') && ((event.stateMask
                                                                                      & Platform.getCommandModifier())
                                                                                     != 0)) {
            saveNow();
        }
        else if (tick
                 && (event.type == SWT.KeyDown)
                 && ((event.character == SWT.BS) || (event.character == SWT.DEL))
                 && ((event.stateMask & Platform.getCommandModifier()) != 0)) {
            Set<iComponent> c = group.getSelection();
            HashSet<IVisualElement> toDelete = new HashSet<IVisualElement>();
            for (iComponent cc : c) {
                IVisualElement v = cc.getVisualElement();
                if (v != null) toDelete.add(v);
            }
            for (IVisualElement v : toDelete) {
                VisualElement.delete(this.getRoot(), v);
            }
        }
        else if ((event.type == SWT.KeyDown) && (event.character == ' ') && tick) {

            // System.out.println(" opening space menu ...");

            HashSet<IVisualElement> sel = selectionOrOver();

            if (sel.isEmpty()) return StandardTraversalHint.CONTINUE;

            iComponent c = IVisualElement.localView.get(sel.iterator().next());

            // iComponent c = window.getRoot().hit(window, new
            // Vector2(locationInScreenp.x, locationInScreenp.y));

            // System.out.println(" comp is <" + c + ">");

            if (c != null) {
                final IVisualElement v = c.getVisualElement();

                // System.out.println(" v is <" + v + ">");
                if (newSource == v) {
                    tick = false;
                    // todo, should auto select

                    FastVisualElementOverridesPropertyCombiner<MarkingMenuBuilder, MarkingMenuBuilder> combiner =
                            new FastVisualElementOverridesPropertyCombiner<MarkingMenuBuilder, MarkingMenuBuilder>(false);
                    MarkingMenuBuilder marker = combiner.getProperty(newSource,
                                                                     IVisualElement.spaceMenu,
                                                                     new iCombiner<MarkingMenuBuilder, MarkingMenuBuilder>() {

                                                                         public
                                                                         MarkingMenuBuilder bind(MarkingMenuBuilder t,
                                                                                                 MarkingMenuBuilder u) {

                                                                             // System.out.println("t : "
                                                                             // + t + " " +
                                                                             // u);

                                                                             if (t == null) return u;
                                                                             if (u == null) return t;
                                                                             return t.mergeWith(u);
                                                                         }

                                                                         public
                                                                         MarkingMenuBuilder unit() {
                                                                             return null;
                                                                         }
                                                                     });

                    // ;//System.out.println(" marker is <"
                    // +
                    // marker + "> at <" + locationInScreeno
                    // + ">");

                    if (marker != null) {
                        if (marker.insertCopyPasteItems) {
                            group.deselectAll();
                            IVisualElement.localView.get(v).setSelected(true);
                            group.addToSelection(IVisualElement.localView.get(v));
                            insertCopyPasteMenuItems(rootSheetElement, group, marker.getMap());
                        }

                        if (marker.insertDeleteItem) {
                            Map<String, IUpdateable> m = marker.getMap();
                            m.put("   \u232b  <b>delete</b> element ///meta BACK_SPACE///", new IUpdateable() {
                                public
                                void update() {
                                    PythonPluginEditor.delete(v, rootSheetElement);
                                }
                            });

                        }

                        // TODO swt
                        // marker.getMenu(this.getWindow().getCanvas(),
                        // locationInScreeno);

                        // Vector2 currentMousePosition
                        // =
                        // GLComponentWindow.getCurrentWindow(null).getCurrentMousePosition();

                        Point locationInScreenp = Launcher.display.getCursorLocation();

                        // locationInScreenp =
                        // Launcher.display.map(null,
                        // window.getFrame(),
                        // locationInScreenp);

                        // System.out.println(" location on screen mapped is <"
                        // + locationInScreenp +
                        // ">");

                        marker.getMenu(window.getCanvas(), locationInScreenp);

                    }
                }
            }
        }
        else if ((event.type == SWT.KeyDown) && (event.character == 'y') && tick) {
            tick = false;
            createFromTemplate();
        }
        else if ((event.type == SWT.KeyDown) && (event.keyCode == SWT.PAGE_UP) && tick) {
            tick = false;
            HashSet<IVisualElement> s = selectionOrOver();

            // System.out.println(" selection or over is <" + s +
            // ">");

            if (!s.isEmpty()) {
                for (IVisualElement ss : s) {
                    Beginner beginner = PseudoPropertiesPlugin.begin.get(ss);
                    beginner.call(new Object[]{});
                }
            }
        }
        else if ((event.type == SWT.KeyDown) && (event.keyCode == SWT.PAGE_DOWN) && tick) {
            tick = false;
            HashSet<IVisualElement> s = selectionOrOver();

            // System.out.println(" selection or over is <" + s +
            // ">");

            if (!s.isEmpty()) {
                for (IVisualElement ss : s) {
                    Ender beginner = PseudoPropertiesPlugin.end.get(ss);
                    beginner.call(new Object[]{});
                }
            }
        }
        else if (tick) {

            if (((event.stateMask & Platform.getCommandModifier()) != 0) && (event.type == SWT.KeyDown)) {
                {
                    String match = String.valueOf(Character.toLowerCase(event.character));
                    String s = keyboardShortcut.get(newSource);
                    if (s != null) {
                        if (s.equals(match)) {
                            tick = false;
                            if ((event.stateMask & SWT.SHIFT) == 0) beginExecution(newSource);
                            else endExecution(newSource);
                        }
                    }
                }
                String match = String.valueOf(Character.toLowerCase((char) event.keyCode));
                String s = keyboardShortcut.get(newSource);
                if (s != null) {
                    if (s.equals(match)) {
                        tick = false;
                        if ((event.stateMask & SWT.SHIFT) == 0) beginExecution(newSource);
                        else endExecution(newSource);
                    }
                }

            }
        }

        else if (tick) {

            String m = "";
            String c = (String.valueOf(event.character)).toLowerCase();

            String match = (m + c).trim().toLowerCase();

            String s = keyboardShortcut.get(newSource);
            if (s != null) {
                if (s.equals(match)) {
                    tick = false;
                    if ((event.stateMask & SWT.SHIFT) == 0) beginExecution(newSource);
                    else endExecution(newSource);
                }
            }
        }

        return StandardTraversalHint.CONTINUE;
    }

    private
    HashSet<IVisualElement> selectionOrOver() {

        // System.out.println(" inside selection or over ");

        HashSet<IVisualElement> sel = new HashSet<IVisualElement>();

        Point locationInScreenp = Launcher.display.getCursorLocation();

        // System.out.println(" cursor location on the screen is <" +
        // locationInScreenp + ">");

        locationInScreenp = Launcher.display.map(null, window.getCanvas(), locationInScreenp);
        // locationInScreenp.x -=
        // window.getCanvas().getParent().getLocation().x;

        // System.out.println(" cursor location in canvas is <" +
        // locationInScreenp + ">");

        Vector2 t = new Vector2(locationInScreenp.x, locationInScreenp.y);
        window.transformWindowToDrawing(t);

        t.y += 25;

        // System.out.println(" cursor location in drawing coords <" +
        // t + ">");

        iComponent cc = window.getRoot().hit(window, t);

        // System.out.println(" hit :" + cc);

        if (cc != null) {
            IVisualElement v = cc.getVisualElement();
            if (v != null) sel.add(v);
        }

        Set<iComponent> c = group.getSelection();
        HashSet<IVisualElement> sel2 = new HashSet<IVisualElement>();
        for (iComponent ccc : c) {
            IVisualElement v = ccc.getVisualElement();
            if (v != null) sel2.add(v);
        }

        if (sel.isEmpty()) return sel2;
        if (sel2.containsAll(sel)) return sel2;

        return sel;
    }

    public
    TraversalHint inspectablePropertiesFor(IVisualElement source, List<Prop> properties) {
        return StandardTraversalHint.CONTINUE;
    }

    public
    TraversalHint isHit(IVisualElement source, Event event, Ref<Boolean> is) {
        //
        //
        // ;//System.out.println(" \n\n is hit "+event+" "+event.type+" \n\n");
        // if (event.doit && (event.stateMask &
        // Platform.getCommandModifier()) != 0
        // && (event.stateMask & SWT.SHIFT) != 0
        // && !Platform.isPopupTrigger(event)) {
        //
        // if (event.type == SWT.MouseDown) {
        // dragDuplicator.begin(event);
        // event.doit = false;
        // }
        //
        // } else if (event.type == SWT.MouseMove)
        // dragDuplicator.drag(event);
        // else if (event.type == SWT.MouseUp)
        // dragDuplicator.end(event);
        //
        return StandardTraversalHint.CONTINUE;
    }

    public
    void load(Reader reader) {
        LinkedHashSet<IVisualElement> created = new LinkedHashSet<IVisualElement>();
        synchronized (Launcher.lock) {

            try {
                reader.mark(500);
                int defaultVersion = 1;
                ObjectInputStream objectInputStream =
                        getPersistence(defaultVersion).getObjectInputStream(reader, created);
                String version = (String) objectInputStream.readObject();
                int versionToLoad = 0;
                if ("version_1".equals(version)) {
                    versionToLoad = 0;
                }
                else if ("version_2".equals(version)) {
                    versionToLoad = 1;
                }
                else assert false : version;

                if (versionToLoad != defaultVersion) {
                    reader.reset();
                    objectInputStream = getPersistence(versionToLoad).getObjectInputStream(reader, created);
                    objectInputStream.readObject();
                }

                IVisualElement oldRoot = (IVisualElement) objectInputStream.readObject();

                VersioningSystem system = vs;
                if (system != null) {
                    for (IVisualElement ve : created) {
                        if (SystemProperties.getIntProperty("noCommit", 0) == 0)
                            system.synchronizeElementWithFileStructure(ve);
                    }
                }

                assert oldRoot == rootSheetElement : oldRoot;

                // System.out.println(" -- reading persistance information for plugins --");

                while (true) {
                    // System.out.println(" -- reading --");
                    try {
                        Object persistanceInformation = objectInputStream.readObject();
                        // System.out.println(" -- read :"
                        // +
                        // persistanceInformation);
                        for (iPlugin p : plugins) {
                            p.setPersistanceInformation(persistanceInformation);
                        }
                    } catch (com.thoughtworks.xstream.converters.ConversionException conv) {
                        // System.out.println(" got a conversion exception on reading persistance information for plugin. This is probably caused by the plugin storing something that we can't find unless we load the plugin. This is usually recoverable");
                    }
                }

            } catch (StreamException e) {
                if (e.getMessage().endsWith("input contained no data")) {
                }
                else e.printStackTrace();
            } catch (EOFException e) {
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            // was:
            // for
            // (iVisualElement
            // ve : created)
            // {
            // this.added(ve);
            // }

            for (IVisualElement ve : created) {
                IVisualElementOverrides.topology.begin(ve);
                IVisualElementOverrides.backward.added.apply(ve);
                IVisualElementOverrides.forward.added.apply(ve);
                IVisualElementOverrides.topology.end(ve);
            }
        }
    }

    public static
    boolean canPaste() {

        // TODO sometimes Ubuntu just deadlocks during this call. We
        // need to do this with SWT instead

        if (!Platform.isMac()) return false;

        try {
            Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable t = c.getContents(null);
            Object data = c.getData(DataFlavor.javaFileListFlavor);
            if (((List) data).get(0) instanceof File) return true;
        } catch (Exception e) {
        }

        return false;
    }

    public
    TraversalHint menuItemsFor(IVisualElement source, Map<String, IUpdateable> items) {

        if (Platform.isLinux()) insertFileMenuItems(rootSheetElement, group, items);

        insertCopyPasteMenuItems(rootSheetElement, group, items);

        final HashSet<IVisualElement> o = selectionOrOver();
        if (!o.isEmpty()) {
            items.put("Templating", null);
            // System.out.println(" selection or over is <" + o +
            // ">");
            items.put("\u1d40 <b>Make element" + ((o.size() > 1) ? "s" : "") + " into template</b>", new IUpdateable() {

                public
                void update() {
                    final NewTemplates templates = new NewTemplates(rootSheetElement);

                    final Point x = Launcher.display.getCursorLocation();

                    // x.x -=
                    // window.getFrame().getLocation().x;
                    // x.y -=
                    // window.getFrame().getLocation().y;

                    PopupTextBox.Modal.getStringOrCancel(new java.awt.Point(x.x, x.y),
                                                         "Template name",
                                                         "personal.something",
                                                         new IAcceptor<String>() {
                                                             public
                                                             IAcceptor<String> set(final String to) {

                                                                 PopupTextBox.Modal.getStringOrCancel(new java.awt.Point(x.x,
                                                                                                                         x.y),
                                                                                                      "Template description",
                                                                                                      "",
                                                                                                      new IAcceptor<String>() {
                                                                                                          public
                                                                                                          IAcceptor<String> set(String to2) {

                                                                                                              // System.out.println(" here is the make <"
                                                                                                              // +
                                                                                                              // to
                                                                                                              // +
                                                                                                              // "> <"
                                                                                                              // +
                                                                                                              // to2
                                                                                                              // +
                                                                                                              // ">");

                                                                                                              File tmp =
                                                                                                                      PackageTools
                                                                                                                              .newTempFileWithSet(to2,
                                                                                                                                                  copyPastePersisence,
                                                                                                                                                  o);
                                                                                                              String
                                                                                                                      ff =
                                                                                                                      templates.templateFolder
                                                                                                                      + to
                                                                                                                      + templates.suffix;
                                                                                                              // System.out.println(" renaming file to <"
                                                                                                              // +
                                                                                                              // ff
                                                                                                              // +
                                                                                                              // ">");
                                                                                                              tmp.renameTo(new File(ff));

                                                                                                              OverlayAnimationManager
                                                                                                                      .notifyAsText(getRoot(),
                                                                                                                                    "Element"
                                                                                                                                    + ((o.size()
                                                                                                                                        > 1)
                                                                                                                                       ? "s are"
                                                                                                                                       : " is")
                                                                                                                                    + " now '"
                                                                                                                                    + to
                                                                                                                                    + "'",
                                                                                                                                    null);

                                                                                                              return this;
                                                                                                          }
                                                                                                      });
                                                                 return this;
                                                             }
                                                         });
                }
            });
        }

        return StandardTraversalHint.CONTINUE;
    }

    public
    void insertFileMenuItems(final IVisualElement rootSheetElement,
                             MainSelectionGroup group,
                             Map<String, IUpdateable> items) {
        items.put("File", null);
        items.put("\t<b>New File...</b>", new IUpdateable() {

            @Override
            public
            void update() {
                Sheet s = FieldMenus2.fieldMenus.sheetForSheet(StandardFluidSheet.this);
                FieldMenus2.fieldMenus.doNewFile();
            }
        });
        items.put("\t<b>Save</b>", new IUpdateable() {

            @Override
            public
            void update() {
                saveNow();
            }
        });
        items.put("\t<b>Save As...</b>", new IUpdateable() {

            @Override
            public
            void update() {
                Sheet s = FieldMenus2.fieldMenus.sheetForSheet(StandardFluidSheet.this);
                FieldMenus2.fieldMenus.doSaveAs(s, window.getFrame());
            }
        });
    }

    public static
    void insertCopyPasteMenuItems(final IVisualElement rootSheetElement,
                                  MainSelectionGroup group,
                                  Map<String, IUpdateable> items) {
        boolean header = false;
        if (!group.getSelection().isEmpty()) {
            if (!header) {
                items.put("Clipboard", null);
                header = true;
            }
            items.put(" \u2397 <b>Copy</b> elements ///meta C///", new IUpdateable() {

                public
                void update() {
                    File tmp = PackageTools.newTempFileWithSelected(rootSheetElement, "copied");
                    PackageTools.copyFileReferenceToClipboard(tmp.getAbsolutePath());
                    OverlayAnimationManager.notifyTextOnWindow(IVisualElement.enclosingFrame.get(rootSheetElement),
                                                               "Copied to clipboard",
                                                               null,
                                                               1,
                                                               new Vector4(1, 1, 1, 0.15f));
                }
            });

        }

        if (canPaste()) {
            if (!header) {
                items.put("Clipboard", null);
                header = true;
            }

            items.put(" \u2398 <b>Paste</b> elements ///meta V///", new IUpdateable() {

                public
                void update() {
                    try {
                        // System.out.println(" pasting ");
                        Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
                        Transferable t = c.getContents(null);
                        Object data = c.getData(DataFlavor.javaFileListFlavor);
                        if (((List) data).get(0) instanceof File) {
                            if (((File) ((List) data).get(0)).getName().endsWith(".fieldpackage")) {
                                PackageTools.importFieldPackage(rootSheetElement,
                                                                ((File) ((List) data).get(0)).getAbsolutePath());
                            }
                        }
                        else {
                        }

                        OverlayAnimationManager.notifyTextOnWindow(IVisualElement.enclosingFrame.get(rootSheetElement),
                                                                   "Pasted from clipboard",
                                                                   null,
                                                                   1,
                                                                   new Vector4(1, 1, 1, 0.15f));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public
    TraversalHint paintNow(IVisualElement source, Rect bounds, boolean visible) {
        if ((getRoot().getParents().size() <= 1) && drawTick) {
            drawTick = false;

            {
                CachedLine text = new CachedLine();
                Vector2 upper = window.transformWindowToCanvas(new Vector2(0.5f, 0.5f));

                // System.out.println(" transform window to canvas got <"+upper+">");

                text.getInput().moveTo(upper.x, upper.y);

                text.getInput()
                    .setPointAttribute(iLinearGraphicsContext.text_v, "right-click, or type N to create a new element");
                text.getInput()
                    .setPointAttribute(iLinearGraphicsContext.font_v, new java.awt.Font(Constants.defaultFont, 0, 30));
                text.getInput().setPointAttribute(iLinearGraphicsContext.alignment_v, 0f);
                text.getProperties().put(iLinearGraphicsContext.containsText, true);
                text.getProperties().put(iLinearGraphicsContext.pointed, false);
                text.getProperties().put(iLinearGraphicsContext.color, new Vector4(0.0f, 0, 0, 0.25f));
                GLComponentWindow.currentContext.submitLine(text, text.getProperties());
            }
            {
                CachedLine text = new CachedLine();
                Vector2 upper = window.transformWindowToCanvas(new Vector2(0.5f, 0.5f));

                text.getInput().moveTo(upper.x, upper.y + 30);

                text.getInput()
                    .setPointAttribute(iLinearGraphicsContext.text_v,
                                       "shift-T will make elements from templates, P makes a drawing element");
                text.getInput()
                    .setPointAttribute(iLinearGraphicsContext.font_v, new java.awt.Font(Constants.defaultFont, 0, 15));
                text.getInput().setPointAttribute(iLinearGraphicsContext.alignment_v, 0f);
                text.getProperties().put(iLinearGraphicsContext.containsText, true);
                text.getProperties().put(iLinearGraphicsContext.pointed, false);
                text.getProperties().put(iLinearGraphicsContext.color, new Vector4(0.0f, 0, 0, 0.25f));
                GLComponentWindow.currentContext.submitLine(text, text.getProperties());
            }

        }

        return StandardTraversalHint.CONTINUE;
    }

    public
    TraversalHint prepareForSave() {
        return StandardTraversalHint.CONTINUE;
    }

    public
    StandardFluidSheet registerPlugin(iPlugin plugin) {

        plugins.add(plugin);

        plugin.registeredWith(this.rootSheetElement);

        return this;
    }

    public
    List<String> save(Writer writer) {

        new Exception().printStackTrace();

        // System.out.println(" a ");

        window.hasReset = false;
        window.resetViewParameters();

        // System.out.println(" b ");

        IVisualElementOverrides.topology.begin(rootSheetElement);
        IVisualElementOverrides.forward.prepareForSave.updateable().update();
        IVisualElementOverrides.backward.prepareForSave.updateable().update();
        IVisualElementOverrides.topology.end(rootSheetElement);

        // System.out.println(" c ");

        try {
            Set<IVisualElement> saved = new HashSet<IVisualElement>();
            FluidPersistence pp = getPersistence(1);
            // System.out.println(" -- e");
            ObjectOutputStream objectOutputStream = pp.getObjectOutputStream(writer, saved);
            // System.out.println(" d ");

            try {
                objectOutputStream.writeObject("version_2");
                // System.out.println(" writing root -------");
                // System.out.println(" total is <" +
                // allVisualElements(rootSheetElement) +
                // ">");
                objectOutputStream.writeObject(rootSheetElement);
                for (iPlugin p : plugins) {
                    // System.out.println(" writing plugin <"
                    // + p + ">");
                    Object persistanceInformation = p.getPersistanceInformation();
                    objectOutputStream.writeObject(persistanceInformation);
                }

                // System.out.println(" save finished ");
                objectOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            VersioningSystem system = vs;
            if (system != null) {
                if (SystemProperties.getIntProperty("noCommit", 0) == 0) {
                    system.commitAll(saved);
                }
            }

            List<String> w = pp.getWarnings();
            if (!w.isEmpty()) {
                System.err.println(" warning while saving :" + w);
            }

            window.resetViewParameters();

            return pp.getWarnings();
        } catch (Throwable t) {
            t.printStackTrace();
            return null;

        }

    }

    public
    List<String> saveTwoPart(String filename) {

        // System.out.println(" a ");

        window.hasReset = false;
        window.resetViewParameters();

        // System.out.println(" b ");

        IVisualElementOverrides.topology.begin(rootSheetElement);
        IVisualElementOverrides.forward.prepareForSave.updateable().update();
        IVisualElementOverrides.backward.prepareForSave.updateable().update();
        IVisualElementOverrides.topology.end(rootSheetElement);

        // System.out.println(" c ");

        try {
            Set<IVisualElement> saved = new HashSet<IVisualElement>();
            FluidPersistence pp = getPersistence(1);
            // System.out.println(" -- e");
            ObjectOutputStream objectOutputStream =
                    pp.getObjectOutputStream(new BufferedWriter(new FileWriter(filename + "_next"), 1024 * 1024 * 4),
                                             saved);
            // System.out.println(" d ");

            try {
                objectOutputStream.writeObject("version_2");
                // System.out.println(" writing root -------");
                // System.out.println(" total is <" +
                // allVisualElements(rootSheetElement) +
                // ">");
                objectOutputStream.writeObject(rootSheetElement);
                for (iPlugin p : plugins) {
                    // System.out.println(" writing plugin <"
                    // + p + ">");
                    Object persistanceInformation = p.getPersistanceInformation();
                    objectOutputStream.writeObject(persistanceInformation);
                }

                // System.out.println(" save finished ");
                objectOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // System.out.println(" renaming ");
            new File(filename + "_next").renameTo(new File(filename));
            // System.out.println(" renaming complete ");
            VersioningSystem system = vs;
            if (system != null) {
                if (SystemProperties.getIntProperty("noCommit", 0) == 0) {
                    system.commitAll(saved);
                }
            }

            List<String> w = pp.getWarnings();
            if (!w.isEmpty()) {
                System.err.println(" warning while saving :" + w);
            }

            window.resetViewParameters();

            return pp.getWarnings();
        } catch (Throwable t) {
            t.printStackTrace();
            return null;

        }

    }

    public
    void saveNow() {
        saveNowPart1();
        Launcher.getLauncher().registerUpdateable(new IUpdateable() {

            int m = 0;

            public
            void update() {
                m++;
                if (m == 10) {
                    saveNowPart2();
                    Launcher.getLauncher().deregisterUpdateable(this);
                }
            }
        });
    }

    public
    void setBasicRunner(BasicRunner basicRunner) {
        this.basicRunner = basicRunner;
    }

    public
    void setFilename(String f) {
        this.filename = f;
    }

    public
    <T> TraversalHint setProperty(IVisualElement source, IVisualElement.VisualElementProperty<T> property, Ref<T> to) {

        if (/* rootProperties.containsKey(property) || */source == getRoot()) {
            VisualElementProperty<T> a = property.getAliasedTo();
            while (a != null) {
                property = a;
                a = a.getAliasedTo();
            }

            rootProperties.put(property, to.get());
            // return
            // VisitCode.STOP;
        }
        if (property.getName().endsWith(".+")) {
            if (vs != null) {
                vs.notifyPropertySet(property, to, source);
            }
        }

        return StandardTraversalHint.CONTINUE;
    }

    public
    TraversalHint shouldChangeFrame(IVisualElement source, Rect newFrame, Rect oldFrame, boolean now) {
        return StandardTraversalHint.CONTINUE;
    }

    @DispatchOverTopology(topology = Cont.class)
    public
    void update() {

        eventProcessingQueue.update();

        for (iPlugin p : plugins)
            p.update();

        // TimeSystem ts =
        // TemporalSliderOverrides.currentTimeSystem.get(
        // this.rootSheetElement);

        TimeSystem ts = this.rootSheetElement.getProperty(TemporalSliderOverrides.currentTimeSystem);
        if (ts != null) {
            ts.update();
            double tsTimeNow = ts.evaluate();
            basicRunner.update((float) tsTimeNow);
        }
        else basicRunner.update(-1);

        tick = true;
        drawTick = true;
    }

    public static
    void _debugPrintGraph(IVisualElement newCopy, String indent, HashSet<IVisualElement> seen) {
        // System.out.println(indent + "<" + newCopy + " / " +
        // newCopy.hashCode() + ">");
        if (seen.contains(newCopy)) return;
        seen.add(newCopy);
        List<IVisualElement> cc = newCopy.getChildren();
        if (!cc.isEmpty()) {
            // System.out.println(indent + "  children:");
            for (IVisualElement c : cc) {
                _debugPrintGraph(c, indent + "     ", seen);
            }
        }

        List<IVisualElement> pp = (List<IVisualElement>) newCopy.getParents();
        if (!pp.isEmpty()) {
            // System.out.println(indent + "  parents:");
            for (IVisualElement c : pp) {
                _debugPrintGraph(c, indent + "     ", seen);
            }
        }
    }

    public static
    void debugPrintGraph(IVisualElement newCopy) {

        HashSet<IVisualElement> seen = new HashSet<IVisualElement>();
        _debugPrintGraph(newCopy, "  ", seen);
    }

    private
    FluidPersistence getPersistence(int version) {
        return new FluidPersistence(new FluidPersistence.iWellKnownElementResolver() {
            public
            IVisualElement getWellKnownElement(String uid) {
                if (uid.equals(rootSheetElement_uid)) return rootSheetElement;
                for (iPlugin p : plugins) {
                    IVisualElement ve = p.getWellKnownVisualElement(uid);
                    if (ve != null) {
                        return ve;
                    }
                }
                return null;
            }
        }, version);
    }

    private
    void saveNowPart1() {
        tick = false;
        OverlayAnimationManager.notifyAsText(getRoot(), "Saving...", null);
    }

    public
    void saveNowPart2() {
        List<String> warnings = null;
        try {

            String file = SystemProperties.getDirProperty("versioning.dir") + filename + "/sheet.xml_next";

            if (SystemProperties.getIntProperty("paranoidSave", 0) == 1) {
                int n = 0;
                while (new File(filename + n).exists()) {
                    n++;
                }
                new File(filename).renameTo(new File(filename + n));
            }
            warnings = this.save(new BufferedWriter(new FileWriter(new File(file)), 1024 * 16 * 1024));

            new File(file).renameTo(new File(file.replace("_next", "")));

            vs.commitAll(allVisualElements(getRoot()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (vs instanceof HGVersioningSystem) {
            int v = ((HGVersioningSystem) vs).getLastVersion();

            // OverlayAnimationManager.notifyAsText(getRoot(),
            // "Saved Sheet, revision '" + v + "'", null);
            if ((warnings != null) && !warnings.isEmpty()) for (String w : warnings)
                OverlayAnimationManager.warnAsText(getRoot(), w, null);
        }
        else {
            // OverlayAnimationManager.notifyAsText(getRoot(),
            // "Saved Sheet", null);
            if ((warnings != null) && !warnings.isEmpty()) for (String w : warnings)
                OverlayAnimationManager.warnAsText(getRoot(), w, null);
        }
    }

    private
    void setPersistence(FluidPersistence persistence) {
        this.persistence = persistence;
    }

    protected
    void endCopy(IVisualElement newCopy, IVisualElement old) {

        // debugPrintGraph(newCopy);

        IVisualElementOverrides.topology.begin(newCopy);
        try {
            IVisualElementOverrides.forward.added.added(newCopy);
            IVisualElementOverrides.backward.added.added(newCopy);
        } finally {
            IVisualElementOverrides.topology.end(newCopy);
        }

        if (vs == null) {
            // System.out.println(" warning: no versioning system for copy <"
            // + newCopy + " <- " + old + ">");
            return;
        }

        Rect f = newCopy.getFrame(null);
        f.x += 10;
        f.y += 10;
        newCopy.setFrame(f);
        vs.notifyElementCopied(old, newCopy);

    }

    protected
    String getFilename() {
        return filename;
    }

    private
    void createFromTemplate() {
        // TODO swt
        // final Vector2 x =
        // window.getCurrentMouseInWindowCoordinates();
        final Vector2 x2 = window.getCurrentMouseInWindowCoordinates();
        x2.y += 20;

        final NewTemplates templates = new NewTemplates(rootSheetElement);

        Point x = Launcher.display.getCursorLocation();
        x.x -= window.getFrame().getLocation().x;
        x.y -= window.getFrame().getLocation().y + 20;

        // System.out.println(" about to go templates !");

        templates.getTemplateName(new Point(x.x, x.y), new IAcceptor<String>() {

            public
            IAcceptor<String> set(String to) {
                // System.out.println(" importing <" + to +
                // ">");
                // x.x +=
                // window.getFrame().getLocation().x;
                // x.y +=
                // window.getFrame().getLocation().y;

                PackageTools.importFieldPackage(rootSheetElement, templates.templateFolder + to + templates.suffix, x2);

                OverlayAnimationManager.notifyAsText(getRoot(), "Instantiated '" + to + "'", null);

                return this;
            }
        });
    }

    protected static synchronized
    void singleThreadedSave(final StandardFluidSheet sheet) {

        // System.out.println(" inside shutdown save ");

        synchronized (Launcher.lock) {
            // System.out.println(" got lock");

            // ThreadedLauncher.lock2.lock();
            try {
                // System.out.println(" got lock2");

                if (!(SystemProperties.getIntProperty("noSave", 0) == 1)) try {
                    String file =
                            SystemProperties.getDirProperty("versioning.dir") + sheet.getFilename() + "/sheet.xml";

                    if (SystemProperties.getIntProperty("paranoidSave", 0) == 1) {
                        int n = 0;
                        while (new File(file + n).exists()) {
                            n++;
                        }
                        new File(file).renameTo(new File(file + n));
                    }
                    // System.out.println(" saving to <"
                    // + sheet.getFilename()
                    // + ">");
                    sheet.saveTwoPart(file);
                    // System.out.println(" saving to <"
                    // + sheet.getFilename()
                    // + "> complete");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } finally {
                // ThreadedLauncher.lock2.unlock();
            }
        }
    }

    @NextUpdate(delay = 200)
    public static
    void quitLater() {
        // System.out.println(" attempting to exit in thread <" +
        // Thread.currentThread() + ">");
        Runtime.getRuntime().halt(0);
        // System.exit(0);
    }
}
