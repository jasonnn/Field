package field.core.execution;

import field.bytecode.protect.trampoline.Trampoline2;
import field.core.StandardFluidSheet;
import field.core.dispatch.IVisualElement;
import field.core.dispatch.Rect;
import field.core.dispatch.VisualElementProperty;
import field.core.dispatch.override.IVisualElementOverrides;
import field.core.dispatch.override.Ref;
import field.core.execution.PythonScriptingSystem.Promise;
import field.core.persistance.FluidPersistence;
import field.core.plugins.SimpleConstraints;
import field.core.plugins.autoexecute.AutoExecutePythonPlugin;
import field.core.plugins.history.VersioningSystem;
import field.core.plugins.iPlugin;
import field.core.plugins.pseudo.PseudoPropertiesPlugin;
import field.core.plugins.python.PythonPlugin;
import field.launch.IUpdateable;
import field.launch.Launcher;
import field.math.abstraction.IFloatProvider;
import field.math.graph.IMutableContainer;
import field.math.graph.NodeImpl;
import field.math.graph.visitors.hint.StandardTraversalHint;
import field.math.graph.visitors.hint.TraversalHint;
import field.util.Dict.Prop;
import field.util.PythonUtils;
import org.eclipse.swt.widgets.Event;

import java.io.*;
import java.util.*;

public
class FacelessFluidSheet implements IVisualElementOverrides, IUpdateable {

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
        <T> T getProperty(VisualElementProperty<T> p) {
            if (p == overrides)
                return (T) FacelessFluidSheet.this;

            Object o = rootProperties.get(p);
            return (T) o;
        }

        public
        String getUniqueID() {
            return StandardFluidSheet.rootSheetElement_uid;
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
        <T> IVisualElement setProperty(VisualElementProperty<T> p, T to) {
            rootProperties.put(p, to);
            return this;
        }

        public
        void setUniqueID(String uid) {
        }

        @Override
        public
        String toString() {
            return "standardFluidSheet root element";
        }

    }

    private final RootSheetElement rootSheetElement;

    private final FluidPersistence persistence;

    private final PythonScriptingSystem pss;

    private final BasicRunner basicRunner;

    protected HashMap<Object, Object> rootProperties = new HashMap<Object, Object>();

    protected VersioningSystem vs;


    List<iPlugin> plugins = new ArrayList<iPlugin>();

    public
    FacelessFluidSheet() {

        rootSheetElement = new RootSheetElement();

        rootSheetElement.setProperty(IVisualElement.enclosingFrame, null);
        rootSheetElement.setProperty(IVisualElement.localView, null);
        rootSheetElement.setProperty(IVisualElement.sheetView, null);
        rootSheetElement.setProperty(IVisualElement.selectionGroup, null);

        persistence = new FluidPersistence(new FluidPersistence.iWellKnownElementResolver() {
            public
            IVisualElement getWellKnownElement(String uid) {
                if (uid.equals(StandardFluidSheet.rootSheetElement_uid))
                    return rootSheetElement;
                for (iPlugin p : plugins) {
                    IVisualElement ve = p.getWellKnownVisualElement(uid);
                    if (ve != null) {
                        return ve;
                    }
                }
                return null;
            }
        }, 1);

        pss = new PythonScriptingSystem();
        basicRunner = new BasicRunner(pss, 0) {
            @Override
            protected
            boolean filter(Promise p) {
                IVisualElement v = (IVisualElement) system.keyForPromise(p);

                return IExecutesPromise.promiseExecution.get(v) == this;
            }
        };


        rootSheetElement.setProperty(PythonScriptingSystem.pythonScriptingSystem, pss);
        rootSheetElement.setProperty(IExecutesPromise.promiseExecution, basicRunner);
        rootSheetElement.setProperty(BasicRunner.basicRunner, basicRunner);

    }

    public
    TraversalHint added(IVisualElement newSource) {
        return StandardTraversalHint.CONTINUE;
    }

    public
    void addToSheet(IVisualElement newSource) {
        newSource.addChild(rootSheetElement);
        IVisualElementOverrides.MakeDispatchProxy.getBackwardsOverrideProxyFor(newSource).added(newSource);
        IVisualElementOverrides.MakeDispatchProxy.getOverrideProxyFor(newSource).added(newSource);
    }

    public
    TraversalHint beginExecution(final IVisualElement source) {

        // should be lookup to support remoting
        Ref<PythonScriptingSystem> refPss = new Ref<PythonScriptingSystem>(null);
        IVisualElementOverrides.MakeDispatchProxy.getOverrideProxyFor(source).getProperty(source,
                                                                                          PythonScriptingSystem.pythonScriptingSystem,
                                                                                          refPss);
        assert refPss.get() != null;

        Ref<IExecutesPromise> refRunner = new Ref<IExecutesPromise>(null);
        IVisualElementOverrides.MakeDispatchProxy.getOverrideProxyFor(source).getProperty(source,
                                                                                          IExecutesPromise.promiseExecution,
                                                                                          refRunner);
        assert refRunner.get() != null;
        System.err.println(" runner in faceless is is <" + refRunner.get() + '>');

        Promise promise = refPss.get().promiseForKey(source);
        if (promise != null) {
            System.err.println(" promise isn't null <" + promise + '>');
            refRunner.get().addActive(new IFloatProvider() {

                public
                float evaluate() {
                    return 0;
                }

            }, promise);
        }
        return StandardTraversalHint.CONTINUE;
    }

    public
    TraversalHint deleted(IVisualElement source) {
        source.getProperty(IVisualElement.localView);
        return StandardTraversalHint.CONTINUE;
    }

//	 implementation of iVisualElementOverrides

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
        return StandardTraversalHint.CONTINUE;
    }

    public
    TraversalHint endExecution(IVisualElement source) {

        Ref<PythonScriptingSystem> refPss = new Ref<PythonScriptingSystem>(null);
        IVisualElementOverrides.MakeDispatchProxy.getOverrideProxyFor(source).getProperty(source,
                                                                                          PythonScriptingSystem.pythonScriptingSystem,
                                                                                          refPss);
        assert refPss.get() != null;

        Ref<IExecutesPromise> refRunner = new Ref<IExecutesPromise>(null);
        IVisualElementOverrides.MakeDispatchProxy.getOverrideProxyFor(source).getProperty(source,
                                                                                          IExecutesPromise.promiseExecution,
                                                                                          refRunner);
        assert refRunner.get() != null;

        Promise p = refPss.get().promiseForKey(source);
        if (p != null) {
            refRunner.get().removeActive(p);
        }

        return StandardTraversalHint.CONTINUE;
    }

    public
    <T> TraversalHint getProperty(IVisualElement source, VisualElementProperty<T> property, Ref<T> ref) {

//		;//System.out.println(" root prop faceless <"+rootProperties+"> / <"+property+">");

        if (rootProperties.containsKey(property)) {
            VisualElementProperty<T> a = property.getAliasedTo();
            while (a != null) {
                property = a;
                a = a.getAliasedTo();
            }

            //System.err.println(" ref. get< "+ref.get()+"> <"+rootProperties.get(property)+">");
            // major change

            if (ref.get() == null)
                ref.set((T) rootProperties.get(property));

            // return VisitCode.STOP;
        }
        return StandardTraversalHint.CONTINUE;
    }

    public
    IVisualElement getRoot() {
        return rootSheetElement;
    }

    public
    TraversalHint handleKeyboardEvent(IVisualElement newSource, Event event) {
        return StandardTraversalHint.CONTINUE;
    }

    public
    TraversalHint inspectablePropertiesFor(IVisualElement source, List<Prop> properties) {
        return StandardTraversalHint.CONTINUE;
    }

    public
    TraversalHint isHit(IVisualElement source, Event event, Ref<Boolean> is) {
        return StandardTraversalHint.CONTINUE;
    }

    public
    void load(Reader reader) {
        LinkedHashSet<IVisualElement> created = new LinkedHashSet<IVisualElement>();

        ObjectInputStream objectInputStream = persistence.getObjectInputStream(reader, created);
        try {
            String version = (String) objectInputStream.readObject();
            //assert version.equals("version_1") : version;
            IVisualElement oldRoot = (IVisualElement) objectInputStream.readObject();


            assert oldRoot == rootSheetElement : oldRoot;
            while (true) {
                Object persistanceInformation = objectInputStream.readObject();
                for (iPlugin p : plugins) {
                    p.setPersistanceInformation(persistanceInformation);
                }
            }

        } catch (EOFException e) {
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // was:
        // for (iVisualElement ve : created) {
        // this.added(ve);
        // }

        for (IVisualElement ve : created) {
            IVisualElementOverrides.MakeDispatchProxy.getBackwardsOverrideProxyFor(ve).added(ve);
            IVisualElementOverrides.MakeDispatchProxy.getOverrideProxyFor(ve).added(ve);
        }
    }

    public
    TraversalHint menuItemsFor(IVisualElement source, Map<String, IUpdateable> items) {
        return StandardTraversalHint.CONTINUE;
    }

    public
    TraversalHint paintNow(IVisualElement source, Rect bounds, boolean visible) {
        return StandardTraversalHint.CONTINUE;
    }

    public
    TraversalHint prepareForSave() {
        return StandardTraversalHint.CONTINUE;
    }

    public
    FacelessFluidSheet registerPlugin(iPlugin plugin) {

        plugins.add(plugin);

        plugin.registeredWith(this.rootSheetElement);

        return this;
    }

    public
    void save(Writer writer) {

        IVisualElementOverrides.MakeDispatchProxy.getOverrideProxyFor(rootSheetElement).prepareForSave();
        IVisualElementOverrides.MakeDispatchProxy.getBackwardsOverrideProxyFor(rootSheetElement).prepareForSave();

        Set<IVisualElement> saved = new HashSet<IVisualElement>();
        ObjectOutputStream objectOutputStream = persistence.getObjectOutputStream(writer, saved);

        try {
            objectOutputStream.writeObject("version_1");
            objectOutputStream.writeObject(rootSheetElement);
            for (iPlugin p : plugins) {
                Object persistanceInformation = p.getPersistanceInformation();
                objectOutputStream.writeObject(persistanceInformation);
            }
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static
    void registerExtendedPlugins(final FacelessFluidSheet sheet) {
        HashSet<String> p = Trampoline2.plugins;
        //System.out.println(" extended plugins are <" + p + ">");
        for (String s : p) {
            //System.out.println("   loading plugin <" + s + ">");
            try {
                Class<?> loaded = sheet.getClass().getClassLoader().loadClass(s);
                iPlugin instance = (iPlugin) loaded.newInstance();
                sheet.registerPlugin(instance);
            } catch (ClassNotFoundException e) {
                //System.out.println("   error loading plugin <" + s + ">, continuing");
                e.printStackTrace();
            } catch (InstantiationException e) {
                //System.out.println("   error loading plugin <" + s + ">, continuing");
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                //System.out.println("   error loading plugin <" + s + ">, continuing");
                e.printStackTrace();
            } catch (Throwable t) {
                //System.out.println("   error loading plugin <" + s + ">, continuing");
                t.printStackTrace();
            }
        }
    }

    public
    <T> TraversalHint setProperty(IVisualElement source, VisualElementProperty<T> property, Ref<T> to) {
        if (rootProperties.containsKey(property) || (source == getRoot())) {
            VisualElementProperty<T> a = property.getAliasedTo();
            while (a != null) {
                property = a;
                a = a.getAliasedTo();
            }
            rootProperties.put(property, to.get());
            // return VisitCode.STOP;
        }

        return StandardTraversalHint.CONTINUE;
    }

    public
    TraversalHint shouldChangeFrame(IVisualElement source, Rect newFrame, Rect oldFrame, boolean now) {
        return StandardTraversalHint.CONTINUE;
    }

    public
    void standard(String filename) {

        this.registerPlugin(new PythonPlugin());
        SimpleConstraints constraints = new SimpleConstraints();
        this.registerPlugin(constraints);
        this.registerPlugin(new AutoExecutePythonPlugin());

        this.registerPlugin(new PseudoPropertiesPlugin());

        //this.registerPlugin(new RemotePlugin());
        //this.registerPlugin(new ReferencePlugin());

        PythonInterface.getPythonInterface().setVariable("T", Launcher.mainInstance);

        new PythonUtils().install();

        registerExtendedPlugins(this);

        try {
            this.load(new BufferedReader(new FileReader(filename)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public
    void update() {

        for (iPlugin p : plugins)
            p.update();

        basicRunner.update(0);

    }
}
