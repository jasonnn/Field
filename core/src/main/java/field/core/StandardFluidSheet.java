package field.core;

import com.thoughtworks.xstream.io.StreamException;
import field.bytecode.protect.trampoline.Trampoline2;
import field.bytecode.protect.Woven;
import field.bytecode.protect.annotations.DispatchOverTopology;
import field.bytecode.protect.annotations.NextUpdate;
import field.bytecode.protect.dispatch.Cont;
import field.core.dispatch.FastVisualElementOverridesPropertyCombiner;
import field.core.dispatch.FastVisualElementOverridesPropertyCombiner.iCombiner;
import field.core.dispatch.VisualElement;
import field.core.dispatch.iVisualElement;
import field.core.dispatch.iVisualElement.Rect;
import field.core.dispatch.iVisualElement.VisualElementProperty;
import field.core.dispatch.iVisualElementOverrides;
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
import field.launch.Launcher;
import field.launch.SystemProperties;
import field.launch.iUpdateable;
import field.math.abstraction.iAcceptor;
import field.math.abstraction.iFloatProvider;
import field.math.graph.visitors.GraphNodeSearching.VisitCode;
import field.math.graph.NodeImpl;
import field.math.graph.visitors.TopologyVisitor_breadthFirst;
import field.math.graph.TopologyViewOfGraphNodes;
import field.math.graph.iMutableContainer;
import field.math.linalg.Vector2;
import field.math.linalg.Vector4;
import field.namespace.context.Dispatch;
import field.namespace.context.SimpleContextTopology;
import field.namespace.generic.Generics.Triple;
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
public class StandardFluidSheet implements iVisualElementOverrides, iUpdateable, iHasVisualElementRoot {

	static public final VisualElementProperty<String> keyboardShortcut = new VisualElementProperty<String>("keyboardShortcut");

	public class RootSheetElement extends NodeImpl<iVisualElement> implements iVisualElement {

		public <T> void deleteProperty(VisualElementProperty<T> p) {
			rootProperties.remove(p);
		}

		public void dispose() {
		}

		public Rect getFrame(Rect out) {
			return null;
		}

		public <T> T getProperty(iVisualElement.VisualElementProperty<T> p) {
			if (p == overrides)
				return (T) StandardFluidSheet.this;
			Object o = rootProperties.get(p);
			return (T) o;
		}

		public String getUniqueID() {
			return rootSheetElement_uid;
		}

		public Map<Object, Object> payload() {
			return rootProperties;
		}

		public void setFrame(Rect out) {
		}

		public iMutableContainer<Map<Object, Object>, iVisualElement> setPayload(Map<Object, Object> t) {
			return this;
		}

		public <T> iVisualElement setProperty(iVisualElement.VisualElementProperty<T> p, T to) {
			rootProperties.put(p, to);
			return this;
		}

		public void setUniqueID(String uid) {
		}

		@Override
		public String toString() {
			return "root <" + System.identityHashCode(this) + ">";
		}

	}

	static public final SimpleContextTopology context =  SimpleContextTopology.newInstance();

	static public final VisualElementProperty<VersioningSystem> versioningSystem = new VisualElementProperty<VersioningSystem>("versioningSystem_");

	public static String rootSheetElement_uid = "//rootSheetElement";

	static protected int uniq = 0;

	static public List<iVisualElement> allVisualElements(iVisualElement root) {
		final List<iVisualElement> ret = new ArrayList<iVisualElement>();
		new TopologyVisitor_breadthFirst<iVisualElement>(true) {
			@Override
			protected VisitCode visit(iVisualElement n) {
				ret.add(n);
				return VisitCode.cont;
			}

		}.apply(new TopologyViewOfGraphNodes<iVisualElement>(false).setEverything(true), root);
		return ret;
	}

	static public iVisualElement findVisualElement(iVisualElement root, final String s) {
		final iVisualElement[] ans = new iVisualElement[1];

		TopologyVisitor_breadthFirst<iVisualElement> search = new TopologyVisitor_breadthFirst<iVisualElement>(true) {
			@Override
			protected VisitCode visit(iVisualElement n) {
				if (n.getUniqueID().equals(s)) {
					ans[0] = n;
					return VisitCode.stop;
				}
				return VisitCode.cont;
			}

		};

		search.apply(new TopologyViewOfGraphNodes<iVisualElement>(false).setEverything(true), root);
		return ans[0];
	}

	static public iVisualElement findVisualElementWithName(iVisualElement root, final String pattern) {

		final Pattern p = Pattern.compile(pattern);

		final iVisualElement[] ans = new iVisualElement[1];

		TopologyVisitor_breadthFirst<iVisualElement> search = new TopologyVisitor_breadthFirst<iVisualElement>(true) {
			@Override
			protected VisitCode visit(iVisualElement n) {
				String name = n.getProperty(iVisualElement.name);
				if (name != null && p.matcher(name).matches()) {
					ans[0] = n;
					return VisitCode.stop;
				}
				return VisitCode.cont;
			}

		};

		search.apply(new TopologyViewOfGraphNodes<iVisualElement>(false).setEverything(true), root);
		return ans[0];
	}

	static public List<iVisualElement> findVisualElementWithNameExpression(iVisualElement root, final String pattern) {

		final Pattern p = Pattern.compile(pattern);

		final List<iVisualElement> ans = new ArrayList<iVisualElement>();

		TopologyVisitor_breadthFirst<iVisualElement> search = new TopologyVisitor_breadthFirst<iVisualElement>(true) {
			@Override
			protected VisitCode visit(iVisualElement n) {
				String name = n.getProperty(iVisualElement.name);
				if (name != null && p.matcher(name).matches()) {
					ans.add(n);
				}
				return VisitCode.cont;
			}

		};

		search.apply(new TopologyViewOfGraphNodes<iVisualElement>(false).setEverything(true), root);
		return ans;
	}

	static public StandardFluidSheet scratchBegin(VersioningSystem system) {
		return scratchBegin(system, SystemProperties.getProperty("fluid.scratch", SystemProperties.getProperty("main.class") + ".xml"));
	}

	static public StandardFluidSheet scratchBegin(VersioningSystem system, String filename) {
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

		sheet.rootSheetElement.setProperty(iVisualElement.toolPalette2, new ToolPalette2());
		registerExtendedPlugins(sheet);

		((SashForm) sheet.window.leftComp1).setWeights(new int[] { 4, 4, 1 });
		new BetterSash((SashForm) sheet.window.leftComp1, false);

		PythonInterface.getPythonInterface().setVariable("T", Launcher.mainInstance);
		PythonInterface.getPythonInterface().setVariable("S", sheet);

		new PythonUtils().install();

		folder.selectFirst();

		return sheet;
	}

	static public void scratchEnd(final StandardFluidSheet sheet, VersioningSystem system) {

		String filename = SystemProperties.getDirProperty("versioning.dir") + sheet.getFilename() + "/sheet.xml";
		try {
			sheet.load(new BufferedReader(new FileReader(filename), 1024 * 1 * 1024));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Launcher.getLauncher().addShutdown(sheet.shutdownhook = new iUpdateable() {

			@Override
			public void update() {
                // System.out.println(" inside shutdown hook ");

				singleThreadedSave(sheet);
			}
		});

		Triple<VisualElement, PlainDraggableComponent, TemporalSliderOverrides> created = TemporalSliderOverrides.newTemporalSlider("time", sheet.getRoot());

		sheet.deferredRequestRepaint();

	}

	@NextUpdate(delay = 2)
	protected void deferredRequestRepaint() {
		window.requestRepaint();
	}

	static public StandardFluidSheet versionedScratch(String filenameInWorkspace) {
		VersioningSystem vs = VersioningSystem.newDefault(filenameInWorkspace);
		StandardFluidSheet sheet = StandardFluidSheet.scratchBegin(vs, filenameInWorkspace);
		StandardFluidSheet.scratchEnd(sheet, vs);
		return sheet;
	}

	private static void registerExtendedPlugins(final StandardFluidSheet sheet) {
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

	private iUpdateable shutdownhook;

	private String filename;

	protected iVisualElement rootSheetElement;

	protected HashMap<Object, Object> rootProperties = new HashMap<Object, Object>();

	protected VersioningSystem vs;

	// implementation of iVisualElementOverrides

	String name = null;

	TaskQueue eventProcessingQueue = new TaskQueue();

	boolean tick = false;
	boolean drawTick = false;

	List<iPlugin> plugins = new ArrayList<iPlugin>();

	private BasicRunner multiThreadedRunner;

	private DragDuplicator dragDuplicator;

	public StandardFluidSheet() {
		this("sheet:" + (uniq++), null);
	}

	public StandardFluidSheet(String name, VersioningSystem vs) {

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

		r1.setOverrides(new Dispatch<iVisualElement, iVisualElementOverrides>(iVisualElementOverrides.topology).getOverrideProxyFor(rootSheetElement, iVisualElementOverrides.class));

		rootSheetElement.setProperty(iVisualElement.enclosingFrame, window);
		rootSheetElement.setProperty(iVisualElement.rootComponent, r1);
		rootSheetElement.setProperty(iVisualElement.localView, null);
		rootSheetElement.setProperty(iVisualElement.sheetView, this);
		GlobalKeyboardShortcuts gks = new GlobalKeyboardShortcuts();
		rootSheetElement.setProperty(GlobalKeyboardShortcuts.shortcuts, gks);
		gks.add(gks.new Shortcut('s', Platform.getCommandModifier(), Platform.getCommandModifier()), new iUpdateable() {

			@Override
			public void update() {
				saveNow();
			}
		});

		gks.add(gks.new Shortcut(0, 0, 0) {
			@Override
			public boolean matches(char c, int code, int state) {
				if ((state & Platform.getCommandModifier()) != 0) {
					List<iVisualElement> e = allVisualElements(getRoot());
					String match = "" + Character.toLowerCase(c);
					for (iVisualElement ee : e) {
						String s = ee.getProperty(keyboardShortcut);

						if (s != null) {
							if (s.equals(match)) {
								if ((state & SWT.SHIFT) != 0)
									endExecution(ee);
								else
									beginExecution(ee);
								return true;
							}
						}

					}
				}
				return false;
			}
		}, new iUpdateable() {

			@Override
			public void update() {

			}
		});

		// rootSheetElement.setProperty(iVisualElement.
		// toolPalette,
		// new ToolPalette());
		rootSheetElement.setProperty(iVisualElement.selectionGroup, group);
		rootSheetElement.setProperty(iVisualElement.markingGroup, markingGroup);
		rootSheetElement.setProperty(iVisualElement.name, "((sheet root))");

		copyPastePersisence = new FluidCopyPastePersistence(new FluidPersistence.iWellKnownElementResolver() {
			public iVisualElement getWellKnownElement(String uid) {
				if (uid.equals(rootSheetElement_uid))
					return rootSheetElement;
				for (iPlugin p : plugins) {
					iVisualElement ve = p.getWellKnownVisualElement(uid);
					if (ve != null) {
						return ve;
					}
				}
                // System.out.println(" WARNING: not well known in copySource <"
                // + uid + ">");
				return null;
			}
		}, new iNotifyDuplication() {
			public String beginNewUID(String uidToCopy) {
				String target = "__" + new UID().toString();
                // System.out.println(" copied uid <" +
                // uidToCopy + "> to <" + target + ">");
				return target;
			}

			public void endCopy(iVisualElement newCopy, iVisualElement old) {
				StandardFluidSheet.this.endCopy(newCopy, old);
			}
		});

		iVisualElement.copyPaste.set(rootSheetElement, rootSheetElement, copyPastePersisence);

		pss = new PythonScriptingSystem() {
			@Override
			protected void filterIntersections(LinkedHashSet ret) {
				Iterator n = ret.iterator();
				while (n.hasNext()) {
					Promise nn = (Promise) n.next();
					iVisualElement elem = (iVisualElement) pss.keyForPromise(nn);
					Boolean m = elem.getProperty(WindowSpaceBox.isWindowSpace);
					if (m != null && m) {
						n.remove();
					}
				}
			}
		};
		basicRunner = new BasicRunner(pss, 0) {
			@Override
			protected boolean filter(Promise p) {
				iVisualElement v = (iVisualElement) system.keyForPromise(p);
				if (v == null)
					return false;
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
		rootSheetElement.setProperty(iVisualElement.multithreadedRunner, multiThreadedRunner);

		this.vs = vs;
		rootSheetElement.setProperty(versioningSystem, vs);

		UbiquitousLinks.sheets.add(this);

		GlassComponent g1 = new GlassComponent(r1, dragDuplicator = new DragDuplicator(group, rootSheetElement));
		rootSheetElement.setProperty(iVisualElement.glassComponent, g1);
		window.getRoot().addComponent(g1);

		rootSheetElement.setProperty(iVisualElement.name, "root");

		HashMap<String, Object> c1 = SystemProperties.getProperties();
		for (Entry<String, Object> e : c1.entrySet()) {
			rootSheetElement.setProperty(new VisualElementProperty(e.getKey()), e.getValue());
		}

	}

	public VisitCode added(iVisualElement newSource) {
		iComponent component = newSource.getProperty(iVisualElement.localView);

		if (component != null)
			window.getRoot().addComponent(component);
		else {
			System.err.println(" !!!!!!!!! no component for <" + newSource + "> !!!!!!!!!!!!");
		}

		if (iVisualElement.isRenderer.getBoolean(newSource, false))
			iExecutesPromise.promiseExecution.set(newSource, newSource, multiThreadedRunner);

		window.getRoot().requestRedisplay();
		return VisitCode.cont;
	}

	public void addToSheet(iVisualElement newSource) {
		newSource.addChild(rootSheetElement);
		new iVisualElementOverrides.MakeDispatchProxy().getBackwardsOverrideProxyFor(newSource).added(newSource);
		new iVisualElementOverrides.MakeDispatchProxy().getOverrideProxyFor(newSource).added(newSource);
	}

	ThreadLocal<LinkedHashSet<iVisualElement>> inprogress = new ThreadLocal<LinkedHashSet<iVisualElement>>() {
		@Override
		public LinkedHashSet<iVisualElement> get() {
			return new LinkedHashSet<iVisualElement>();
		}
	};

	public VisitCode beginExecution(final iVisualElement source) {

		if (inprogress.get().contains(source))
			return VisitCode.stop;

		System.out.println(" inprogress <" + inprogress.get() + ">");
		inprogress.get().add(source);

		try {

			// should be
			// lookup to
			// support

            // System.out.println(" begin exec <" + source + ">");

			PythonPlugin p = PythonPlugin.python_plugin.get(source);
			if (p instanceof PythonPluginEditor)
				try {
					((PythonPluginEditor) p).getEditor().getInput().append("Running '" + source.getProperty(iVisualElement.name) + "'");
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
				runner.addActive(new iFloatProvider() {

					public float evaluate() {
						Vector2 v = window.getCurrentMouseInWindowCoordinates();

						Rect o = new Rect(0, 0, 0, 0);
						source.getFrame(o);

						return v.x;
					}

				}, promise);
			}

			SnippetsPlugin.addText(source, "_self.find[\"" + source.getProperty(iVisualElement.name) + "\"].begin()\n_self.begin()\n_self.end()\n_self.find[\"" + source.getProperty(iVisualElement.name) + "\"].end()", "element started", new String[] { "start running an element", "start running </i>this<i> element", "STOP running </i>this<i> element'", "STOP running an element" }, "alternative form");

			return VisitCode.cont;
		} finally {
			inprogress.get().remove(source);
		}
	}

	public void close() {

		for (iPlugin p : plugins)
			p.close();

		Launcher.getLauncher().registerUpdateable(this);
		Launcher.getLauncher().deregisterUpdateable(window);
		window.getFrame().setVisible(false);
		window.getFrame().dispose();

		if (shutdownhook != null)
			Launcher.getLauncher().removeShutdownHook(shutdownhook);
	}

	public VisitCode deleted(iVisualElement source) {
		iComponent component = source.getProperty(iVisualElement.localView);
		if (component != null)
			window.getRoot().removeComponent(component);
		window.getRoot().requestRedisplay();

		if (vs != null) {
			vs.notifyElementDeleted(source);
		}

		group.removeFromSelection(source.getProperty(iVisualElement.localView));
		markingGroup.removeFromSelection(source.getProperty(iVisualElement.localView));

		return VisitCode.cont;
	}

	public <T> VisitCode deleteProperty(iVisualElement source, VisualElementProperty<T> prop) {
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

		return VisitCode.cont;
	}

	public VisitCode endExecution(iVisualElement source) {

		Ref<PythonScriptingSystem> refPss = new Ref<PythonScriptingSystem>(null);
		new iVisualElementOverrides.MakeDispatchProxy().getOverrideProxyFor(source).getProperty(source, PythonScriptingSystem.pythonScriptingSystem, refPss);
		assert refPss.get() != null;

		Ref<iExecutesPromise> refRunner = new Ref<iExecutesPromise>(null);
		new iVisualElementOverrides.MakeDispatchProxy().getOverrideProxyFor(source).getProperty(source, iExecutesPromise.promiseExecution, refRunner);
		assert refRunner.get() != null;

		Promise p = refPss.get().promiseForKey(source);

		if (p != null) {
			refRunner.get().removeActive(p);
		}

		return VisitCode.cont;
	}

	public BasicRunner getBasicRunner() {
		return basicRunner;
	}

	public <T> VisitCode getProperty(iVisualElement source, iVisualElement.VisualElementProperty<T> property, Ref<T> ref) {
		if (rootProperties.containsKey(property)) {
			VisualElementProperty<T> a = property.getAliasedTo();
			while (a != null) {
				property = a;
				a = a.getAliasedTo();
			}

			if (ref.get() == null)
				ref.set((T) rootProperties.get(property), rootSheetElement);

		}

		return VisitCode.cont;
	}

	public iVisualElement getRoot() {
		return rootSheetElement;
	}

	public GLComponentWindow getWindow() {
		return window;
	}

	public VisitCode handleKeyboardEvent(iVisualElement newSource, Event event) {

		if (event == null)
			return VisitCode.cont;

		if (!event.doit)
			return VisitCode.cont;

		if (tick && event.type == SWT.KeyDown && event.character == 'n') {

			tick = false;
			List<iVisualElement> all = StandardFluidSheet.allVisualElements(getRoot());
			iVisualElement ee = getRoot();
			boolean exclusive = false;
			for (iVisualElement a : all) {
				Boolean f = a.getProperty(PythonPluginEditor.python_isDefaultGroup);
				if (f != null && f) {
					Boolean ex = a.getProperty(PythonPluginEditor.python_isDefaultGroupExclusive);
					if (ex != null && ex)
						exclusive = true;
					ee = a;
					break;
				}
			}

			GLComponentWindow frame = iVisualElement.enclosingFrame.get(getRoot());

			Rect bounds = new Rect(30, 30, 60, 60);
			if (frame != null) {
				Vector2 cmp = frame.getCurrentMouseInWindowCoordinates();
				bounds.x = cmp.x - 25;
				bounds.y = cmp.y + 25;
			}

			Triple<VisualElement, DraggableComponent, DefaultOverride> created = VisualElement.createAddAndName(bounds, ee, "untitled", VisualElement.class, DraggableComponent.class, DefaultOverride.class, null);

			if (ee != getRoot() && !exclusive) {
				created.left.addChild(getRoot());
			}
		}
		if (tick && event.type == SWT.KeyDown && event.keyCode == 13) {

			tick = false;

			boolean success = ((PythonPluginEditor) PythonPluginEditor.python_plugin.get(rootSheetElement)).getEditor().getInputEditor().forceFocus();
            // System.out.println(" forcing focus " + success);

		} else if (tick && event.type == SWT.KeyDown && event.character == 'p') {
			tick = false;
			List<iVisualElement> all = StandardFluidSheet.allVisualElements(getRoot());
			iVisualElement ee = getRoot();
			boolean exclusive = false;
			for (iVisualElement a : all) {
				Boolean f = a.getProperty(PythonPluginEditor.python_isDefaultGroup);
				if (f != null && f) {
					Boolean ex = a.getProperty(PythonPluginEditor.python_isDefaultGroupExclusive);
					if (ex != null && ex)
						exclusive = true;
					ee = a;
					break;
				}
			}
			GLComponentWindow frame = iVisualElement.enclosingFrame.get(getRoot());

			Rect bounds = new Rect(30, 30, 50, 50);
			if (frame != null) {
				Vector2 cmp = frame.getCurrentMouseInWindowCoordinates();
				bounds.x = cmp.x - 25;
				bounds.y = cmp.y + 25;
			}

			Triple<VisualElement, PlainDraggableComponent, SplineComputingOverride> created = VisualElement.createAddAndName(bounds, ee, "untitled", VisualElement.class, PlainDraggableComponent.class, SplineComputingOverride.class, null);

			if (ee != getRoot() && !exclusive) {
				created.left.addChild(getRoot());
			}
		} else

		if (tick && event.type == SWT.KeyDown && event.keyCode == 'c' && (event.stateMask & Platform.getCommandModifier()) != 0) {
            // System.out.println(" copying file reference to clipboard ");
            tick = false;
			File tmp = new PackageTools().newTempFileWithSelected(rootSheetElement, "copied");
			new PackageTools().copyFileReferenceToClipboard(tmp.getAbsolutePath());

			OverlayAnimationManager.notifyAsText(getRoot(), "Copied to clipboard", null);

			// OverlayAnimationManager m =
			// window.getOverlayAnimationManager();
			// if (m!=null)
			// {
			// m.no
			// }

		} else if (tick && event.type == SWT.KeyDown && event.keyCode == 'v' && (event.stateMask & Platform.getCommandModifier()) != 0) {
			try {
				tick = false;

				Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
				Transferable t = c.getContents(null);
				Object data = c.getData(DataFlavor.javaFileListFlavor);
				if (((List) data).get(0) instanceof File) {
					if (((File) ((List) data).get(0)).getName().endsWith(".fieldpackage")) {
						OverlayAnimationManager.notifyAsText(getRoot(), "Pasted from clipboard", null);
						new PackageTools().importFieldPackage(rootSheetElement, ((File) ((List) data).get(0)).getAbsolutePath());
					}
				} else {
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (tick && event.type == SWT.KeyDown && event.character == 's' && (event.stateMask & Platform.getCommandModifier()) != 0) {
			saveNow();
		} else if (tick && event.type == SWT.KeyDown && (event.character == SWT.BS || event.character == SWT.DEL) && (event.stateMask & Platform.getCommandModifier()) != 0) {
			Set<iComponent> c = group.getSelection();
			HashSet<iVisualElement> toDelete = new HashSet<iVisualElement>();
			for (iComponent cc : c) {
				iVisualElement v = cc.getVisualElement();
				if (v != null)
					toDelete.add(v);
			}
			for (iVisualElement v : toDelete) {
				VisualElement.delete(this.getRoot(), v);
			}
		} else if (event.type == SWT.KeyDown && event.character == ' ' && tick) {

            // System.out.println(" opening space menu ...");

			HashSet<iVisualElement> sel = selectionOrOver();

			if (sel.size() == 0)
				return VisitCode.cont;

			iComponent c = iVisualElement.localView.get(sel.iterator().next());

			// iComponent c = window.getRoot().hit(window, new
			// Vector2(locationInScreenp.x, locationInScreenp.y));

            // System.out.println(" comp is <" + c + ">");

			if (c != null) {
				final iVisualElement v = c.getVisualElement();

                // System.out.println(" v is <" + v + ">");
                if (newSource == v) {
					tick = false;
					// todo, should auto select

					FastVisualElementOverridesPropertyCombiner<MarkingMenuBuilder, MarkingMenuBuilder> combiner = new FastVisualElementOverridesPropertyCombiner<MarkingMenuBuilder, MarkingMenuBuilder>(false);
					MarkingMenuBuilder marker = combiner.getProperty(newSource, iVisualElement.spaceMenu, new iCombiner<MarkingMenuBuilder, MarkingMenuBuilder>() {

						public MarkingMenuBuilder bind(MarkingMenuBuilder t, MarkingMenuBuilder u) {

                            // System.out.println("t : "
                            // + t + " " +
								// u);

							if (t == null)
								return u;
							if (u == null)
								return t;
							return t.mergeWith(u);
						}

						public MarkingMenuBuilder unit() {
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
							iVisualElement.localView.get(v).setSelected(true);
							group.addToSelection(iVisualElement.localView.get(v));
							insertCopyPasteMenuItems(rootSheetElement, group, marker.getMap());
						}

						if (marker.insertDeleteItem) {
							Map<String, iUpdateable> m = marker.getMap();
							m.put("   \u232b  <b>delete</b> element ///meta BACK_SPACE///", new iUpdateable() {
								public void update() {
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
		} else if (event.type == SWT.KeyDown && event.character == 'y' && tick) {
			tick = false;
			createFromTemplate();
		} else if (event.type == SWT.KeyDown && event.keyCode == SWT.PAGE_UP && tick) {
			tick = false;
			HashSet<iVisualElement> s = selectionOrOver();

            // System.out.println(" selection or over is <" + s +
            // ">");

			if (s.size() > 0) {
				for (iVisualElement ss : s) {
					Beginner beginner = PseudoPropertiesPlugin.begin.get(ss);
					beginner.call(new Object[] {});
				}
			}
		} else if (event.type == SWT.KeyDown && event.keyCode == SWT.PAGE_DOWN && tick) {
			tick = false;
			HashSet<iVisualElement> s = selectionOrOver();

            // System.out.println(" selection or over is <" + s +
            // ">");

			if (s.size() > 0) {
				for (iVisualElement ss : s) {
					Ender beginner = PseudoPropertiesPlugin.end.get(ss);
					beginner.call(new Object[] {});
				}
			}
		} else if (tick) {

			if ((event.stateMask & Platform.getCommandModifier()) != 0 && event.type == SWT.KeyDown) {
				{
					String match = "" + Character.toLowerCase(event.character);
					String s = keyboardShortcut.get(newSource);
					if (s != null) {
						if (s.equals(match)) {
							tick = false;
							if ((event.stateMask & SWT.SHIFT) != 0)
								endExecution(newSource);
							else
								beginExecution(newSource);
						}
					}
				}
				String match = "" + Character.toLowerCase((char)event.keyCode);
				String s = keyboardShortcut.get(newSource);
				if (s != null) {
					if (s.equals(match)) {
						tick = false;
						if ((event.stateMask & SWT.SHIFT) != 0)
							endExecution(newSource);
						else
							beginExecution(newSource);
					}
				}

			}
		}

		else if (tick) {

			String m = "";
			String c = ("" + event.character).toLowerCase();

			String match = (m + c).trim().toLowerCase();

			String s = keyboardShortcut.get(newSource);
			if (s != null) {
				if (s.equals(match)) {
					tick = false;
					if ((event.stateMask & SWT.SHIFT) != 0)
						endExecution(newSource);
					else
						beginExecution(newSource);
				}
			}
		}

		return VisitCode.cont;
	}

	private HashSet<iVisualElement> selectionOrOver() {

        // System.out.println(" inside selection or over ");

		HashSet<iVisualElement> sel = new HashSet<iVisualElement>();

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
			iVisualElement v = cc.getVisualElement();
			if (v != null)
				sel.add(v);
		}

		Set<iComponent> c = group.getSelection();
		HashSet<iVisualElement> sel2 = new HashSet<iVisualElement>();
		for (iComponent ccc : c) {
			iVisualElement v = ccc.getVisualElement();
			if (v != null)
				sel2.add(v);
		}

		if (sel.size() == 0)
			return sel2;
		if (sel2.containsAll(sel))
			return sel2;

		return sel;
	}

	public VisitCode inspectablePropertiesFor(iVisualElement source, List<Prop> properties) {
		return VisitCode.cont;
	}

	public VisitCode isHit(iVisualElement source, Event event, Ref<Boolean> is) {
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
		return VisitCode.cont;
	}

	public void load(Reader reader) {
		LinkedHashSet<iVisualElement> created = new LinkedHashSet<iVisualElement>();
		synchronized (Launcher.lock) {

			try {
				reader.mark(500);
				int defaultVersion = 1;
				ObjectInputStream objectInputStream = getPersistence(defaultVersion).getObjectInputStream(reader, created);
				String version = (String) objectInputStream.readObject();
				int versionToLoad = 0;
				if (version.equals("version_1")) {
					versionToLoad = 0;
				} else if (version.equals("version_2")) {
					versionToLoad = 1;
				} else
					assert false : version;

				if (versionToLoad != defaultVersion) {
					reader.reset();
					objectInputStream = getPersistence(versionToLoad).getObjectInputStream(reader, created);
					objectInputStream.readObject();
				}

				iVisualElement oldRoot = (iVisualElement) objectInputStream.readObject();

				VersioningSystem system = vs;
				if (system != null) {
					for (iVisualElement ve : created) {
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
				} else
					e.printStackTrace();
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

			for (iVisualElement ve : created) {
				iVisualElementOverrides.topology.begin(ve);
				iVisualElementOverrides.backward.added.f(ve);
				iVisualElementOverrides.forward.added.f(ve);
				iVisualElementOverrides.topology.end(ve);
			}
		}
	}

	static public boolean canPaste() {

		// TODO sometimes Ubuntu just deadlocks during this call. We
		// need to do this with SWT instead

		if (!Platform.isMac())
			return false;

		try {
			Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
			Transferable t = c.getContents(null);
			Object data = c.getData(DataFlavor.javaFileListFlavor);
			if (((List) data).get(0) instanceof File)
				return true;
		} catch (Exception e) {
		}

		return false;
	}

	public VisitCode menuItemsFor(iVisualElement source, Map<String, iUpdateable> items) {
		
		if (Platform.isLinux())
			insertFileMenuItems(rootSheetElement, group, items);
		
		insertCopyPasteMenuItems(rootSheetElement, group, items);

		final HashSet<iVisualElement> o = selectionOrOver();
		if (o.size() > 0) {
			items.put("Templating", null);
            // System.out.println(" selection or over is <" + o +
            // ">");
			items.put("\u1d40 <b>Make element" + (o.size() > 1 ? "s" : "") + " into template</b>", new iUpdateable() {

				public void update() {
					final NewTemplates templates = new NewTemplates(rootSheetElement);

					final Point x = Launcher.display.getCursorLocation();

					// x.x -=
					// window.getFrame().getLocation().x;
					// x.y -=
					// window.getFrame().getLocation().y;

                    PopupTextBox.Modal.getStringOrCancel(new java.awt.Point(x.x, x.y), "Template name", "personal.something", new iAcceptor<String>() {
                        public iAcceptor<String> set(final String to) {

                            PopupTextBox.Modal.getStringOrCancel(new java.awt.Point(x.x, x.y), "Template description", "", new iAcceptor<String>() {
                                public iAcceptor<String> set(String to2) {

                                    // System.out.println(" here is the make <"
                                    // +
										// to
										// +
										// "> <"
										// +
										// to2
										// +
										// ">");

									File tmp = new PackageTools().newTempFileWithSet(to2, copyPastePersisence, o);
									String ff = templates.templateFolder + to + templates.suffix;
                                    // System.out.println(" renaming file to <"
                                    // +
										// ff
										// +
										// ">");
									tmp.renameTo(new File(ff));

									OverlayAnimationManager.notifyAsText(getRoot(), "Element" + (o.size() > 1 ? "s are" : " is") + " now '" + to + "'", null);

									return this;
								}
							});
							return this;
						}
					});
				}
			});
		}

		return VisitCode.cont;
	}

	public void insertFileMenuItems(final iVisualElement rootSheetElement, MainSelectionGroup group, Map<String, iUpdateable> items) {
		items.put("File", null);
		items.put("\t<b>New File...</b>", new iUpdateable() {

			@Override
			public void update() {
				Sheet s = FieldMenus2.fieldMenus.sheetForSheet(StandardFluidSheet.this);
				FieldMenus2.fieldMenus.doNewFile();
			}
		});
		items.put("\t<b>Save</b>", new iUpdateable() {

			@Override
			public void update() {
				saveNow();
			}
		});
		items.put("\t<b>Save As...</b>", new iUpdateable() {

			@Override
			public void update() {
				Sheet s = FieldMenus2.fieldMenus.sheetForSheet(StandardFluidSheet.this);
				FieldMenus2.fieldMenus.doSaveAs(s, window.getFrame());
			}
		});
	}

	static public void insertCopyPasteMenuItems(final iVisualElement rootSheetElement, MainSelectionGroup group, Map<String, iUpdateable> items) {
		boolean header = false;
		if (group.getSelection().size() > 0) {
			if (!header) {
				items.put("Clipboard", null);
				header = true;
			}
			items.put(" \u2397 <b>Copy</b> elements ///meta C///", new iUpdateable() {

				public void update() {
					File tmp = new PackageTools().newTempFileWithSelected(rootSheetElement, "copied");
					new PackageTools().copyFileReferenceToClipboard(tmp.getAbsolutePath());
					OverlayAnimationManager.notifyTextOnWindow(iVisualElement.enclosingFrame.get(rootSheetElement), "Copied to clipboard", null, 1, new Vector4(1, 1, 1, 0.15f));
				}
			});

		}

		if (canPaste()) {
			if (!header) {
				items.put("Clipboard", null);
				header = true;
			}

			items.put(" \u2398 <b>Paste</b> elements ///meta V///", new iUpdateable() {

				public void update() {
					try {
                        // System.out.println(" pasting ");
                        Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
						Transferable t = c.getContents(null);
						Object data = c.getData(DataFlavor.javaFileListFlavor);
						if (((List) data).get(0) instanceof File) {
							if (((File) ((List) data).get(0)).getName().endsWith(".fieldpackage")) {
								new PackageTools().importFieldPackage(rootSheetElement, ((File) ((List) data).get(0)).getAbsolutePath());
							}
						} else {
						}

						OverlayAnimationManager.notifyTextOnWindow(iVisualElement.enclosingFrame.get(rootSheetElement), "Pasted from clipboard", null, 1, new Vector4(1, 1, 1, 0.15f));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

	public VisitCode paintNow(iVisualElement source, Rect bounds, boolean visible) {
		if (getRoot().getParents().size() <= 1 && drawTick) {
			drawTick = false;

			{
				CachedLine text = new CachedLine();
				Vector2 upper = window.transformWindowToCanvas(new Vector2(0.5f, 0.5f));

                // System.out.println(" transform window to canvas got <"+upper+">");

				text.getInput().moveTo(upper.x, upper.y);

				text.getInput().setPointAttribute(iLinearGraphicsContext.text_v, "right-click, or type N to create a new element");
				text.getInput().setPointAttribute(iLinearGraphicsContext.font_v, new java.awt.Font(Constants.defaultFont, 0, 30));
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

				text.getInput().setPointAttribute(iLinearGraphicsContext.text_v, "shift-T will make elements from templates, P makes a drawing element");
				text.getInput().setPointAttribute(iLinearGraphicsContext.font_v, new java.awt.Font(Constants.defaultFont, 0, 15));
				text.getInput().setPointAttribute(iLinearGraphicsContext.alignment_v, 0f);
				text.getProperties().put(iLinearGraphicsContext.containsText, true);
				text.getProperties().put(iLinearGraphicsContext.pointed, false);
				text.getProperties().put(iLinearGraphicsContext.color, new Vector4(0.0f, 0, 0, 0.25f));
                GLComponentWindow.currentContext.submitLine(text, text.getProperties());
            }

		}

		return VisitCode.cont;
	}

	public VisitCode prepareForSave() {
		return VisitCode.cont;
	}

	public StandardFluidSheet registerPlugin(iPlugin plugin) {

		plugins.add(plugin);

		plugin.registeredWith(this.rootSheetElement);

		return this;
	}

	public List<String> save(Writer writer) {

		new Exception().printStackTrace();

        // System.out.println(" a ");

		window.hasReset = false;
		window.resetViewParameters();

        // System.out.println(" b ");

		iVisualElementOverrides.topology.begin(rootSheetElement);
		iVisualElementOverrides.forward.prepareForSave.update();
		iVisualElementOverrides.backward.prepareForSave.update();
		iVisualElementOverrides.topology.end(rootSheetElement);

        // System.out.println(" c ");

		try {
			Set<iVisualElement> saved = new HashSet<iVisualElement>();
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
			if (w.size() > 0) {
				System.err.println(" warning while saving :" + w);
			}

			window.resetViewParameters();

			return pp.getWarnings();
		} catch (Throwable t) {
			t.printStackTrace();
			return null;

		}

	}

	public List<String> saveTwoPart(String filename) {

        // System.out.println(" a ");

		window.hasReset = false;
		window.resetViewParameters();

        // System.out.println(" b ");

		iVisualElementOverrides.topology.begin(rootSheetElement);
		iVisualElementOverrides.forward.prepareForSave.update();
		iVisualElementOverrides.backward.prepareForSave.update();
		iVisualElementOverrides.topology.end(rootSheetElement);

        // System.out.println(" c ");

		try {
			Set<iVisualElement> saved = new HashSet<iVisualElement>();
			FluidPersistence pp = getPersistence(1);
            // System.out.println(" -- e");
            ObjectOutputStream objectOutputStream = pp.getObjectOutputStream(new BufferedWriter(new FileWriter(filename + "_next"), 1024 * 1024 * 4), saved);
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
			if (w.size() > 0) {
				System.err.println(" warning while saving :" + w);
			}

			window.resetViewParameters();

			return pp.getWarnings();
		} catch (Throwable t) {
			t.printStackTrace();
			return null;

		}

	}

	public void saveNow() {
		saveNowPart1();
		Launcher.getLauncher().registerUpdateable(new iUpdateable() {

			int m = 0;

			public void update() {
				m++;
				if (m == 10) {
					saveNowPart2();
					Launcher.getLauncher().deregisterUpdateable(this);
				}
			}
		});
	}

	public void setBasicRunner(BasicRunner basicRunner) {
		this.basicRunner = basicRunner;
	}

	public void setFilename(String f) {
		this.filename = f;
	}

	public <T> VisitCode setProperty(iVisualElement source, iVisualElement.VisualElementProperty<T> property, Ref<T> to) {

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

		return VisitCode.cont;
	}

	public VisitCode shouldChangeFrame(iVisualElement source, Rect newFrame, Rect oldFrame, boolean now) {
		return VisitCode.cont;
	}

	@DispatchOverTopology(topology = Cont.class)
	public void update() {

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
		} else
			basicRunner.update(-1);

		tick = true;
		drawTick = true;
	}

	static public void _debugPrintGraph(iVisualElement newCopy, String indent, HashSet<iVisualElement> seen) {
        // System.out.println(indent + "<" + newCopy + " / " +
        // newCopy.hashCode() + ">");
		if (seen.contains(newCopy))
			return;
		seen.add(newCopy);
		List<iVisualElement> cc = newCopy.getChildren();
		if (cc.size() > 0) {
            // System.out.println(indent + "  children:");
            for (iVisualElement c : cc) {
				_debugPrintGraph(c, indent + "     ", seen);
			}
		}

		List<iVisualElement> pp = (List<iVisualElement>) newCopy.getParents();
		if (pp.size() > 0) {
            // System.out.println(indent + "  parents:");
            for (iVisualElement c : pp) {
				_debugPrintGraph(c, indent + "     ", seen);
			}
		}
	}

	static public void debugPrintGraph(iVisualElement newCopy) {

		HashSet<iVisualElement> seen = new HashSet<iVisualElement>();
		_debugPrintGraph(newCopy, "  ", seen);
	}

	private FluidPersistence getPersistence(int version) {
		return new FluidPersistence(new FluidPersistence.iWellKnownElementResolver() {
			public iVisualElement getWellKnownElement(String uid) {
				if (uid.equals(rootSheetElement_uid))
					return rootSheetElement;
				for (iPlugin p : plugins) {
					iVisualElement ve = p.getWellKnownVisualElement(uid);
					if (ve != null) {
						return ve;
					}
				}
				return null;
			}
		}, version);
	}

	private void saveNowPart1() {
		tick = false;
		OverlayAnimationManager.notifyAsText(getRoot(), "Saving...", null);
	}

	public void saveNowPart2() {
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
			if (warnings != null && warnings.size() > 0)
				for (String w : warnings)
					OverlayAnimationManager.warnAsText(getRoot(), w, null);
		} else {
			// OverlayAnimationManager.notifyAsText(getRoot(),
			// "Saved Sheet", null);
			if (warnings != null && warnings.size() > 0)
				for (String w : warnings)
					OverlayAnimationManager.warnAsText(getRoot(), w, null);
		}
	}

	private void setPersistence(FluidPersistence persistence) {
		this.persistence = persistence;
	}

	protected void endCopy(iVisualElement newCopy, iVisualElement old) {

		// debugPrintGraph(newCopy);

		iVisualElementOverrides.topology.begin(newCopy);
		try {
			iVisualElementOverrides.forward.added.added(newCopy);
			iVisualElementOverrides.backward.added.added(newCopy);
		} finally {
			iVisualElementOverrides.topology.end(newCopy);
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

	protected String getFilename() {
		return filename;
	}

	private void createFromTemplate() {
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

        templates.getTemplateName(new Point(x.x, x.y), new iAcceptor<String>() {

			public iAcceptor<String> set(String to) {
                // System.out.println(" importing <" + to +
                // ">");
					// x.x +=
					// window.getFrame().getLocation().x;
					// x.y +=
					// window.getFrame().getLocation().y;

				new PackageTools().importFieldPackage(rootSheetElement, templates.templateFolder + to + templates.suffix, x2);

				OverlayAnimationManager.notifyAsText(getRoot(), "Instantiated '" + to + "'", null);

				return this;
			}
		});
	}

	static synchronized protected void singleThreadedSave(final StandardFluidSheet sheet) {

        // System.out.println(" inside shutdown save ");

		synchronized (Launcher.lock) {
            // System.out.println(" got lock");

			// ThreadedLauncher.lock2.lock();
			try {
                // System.out.println(" got lock2");

				if (!(SystemProperties.getIntProperty("noSave", 0) == 1))
					try {
						String file = SystemProperties.getDirProperty("versioning.dir") + sheet.getFilename() + "/sheet.xml";

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
	public void quitLater() {
        // System.out.println(" attempting to exit in thread <" +
        // Thread.currentThread() + ">");
		Runtime.getRuntime().halt(0);
		// System.exit(0);
	}
}
