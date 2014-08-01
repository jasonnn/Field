package field.core.plugins.python;

import field.core.Constants;
import field.core.dispatch.IVisualElement;
import field.core.dispatch.override.DefaultOverride;
import field.core.dispatch.override.IVisualElementOverrides;
import field.core.dispatch.Rect;
import field.core.dispatch.VisualElementProperty;
import field.core.dispatch.override.Ref;
import field.core.execution.PythonInterface;
import field.core.execution.PythonScriptingSystem;
import field.core.execution.PythonScriptingSystem.DerivativePromise;
import field.core.execution.PythonScriptingSystem.Promise;
import field.core.execution.ScriptingInterface.Language;
import field.core.execution.ScriptingInterface.iGlobalTrap;
import field.core.persistance.VisualElementReference;
import field.core.plugins.autoexecute.Globals;
import field.core.plugins.drawing.SplineComputingOverride;
import field.core.plugins.iPlugin;
import field.core.plugins.log.ElementInvocationLogging;
import field.core.plugins.log.ElementInvocationLogging.ElementExecutionBegin;
import field.core.plugins.log.ElementInvocationLogging.ElementExecutionEnd;
import field.core.plugins.log.ElementInvocationLogging.ElementExecutionFocusBegin;
import field.core.plugins.log.ElementInvocationLogging.ElementExecutionFocusEnd;
import field.core.plugins.log.Logging;
import field.core.windowing.GLComponentWindow;
import field.core.windowing.components.ComponentDrawingUtils;
import field.core.windowing.components.SelectionGroup;
import field.core.windowing.components.iComponent;
import field.graphics.core.BasicGeometry;
import field.graphics.core.BasicGeometry.TriangleMesh;
import field.graphics.dynamic.DynamicMesh;
import field.graphics.dynamic.iDynamicMesh;
import field.launch.IUpdateable;
import field.math.graph.IMutableContainer;
import field.math.graph.NodeImpl;
import field.math.graph.visitors.GraphNodeSearching;
import field.math.graph.visitors.hint.StandardTraversalHint;
import field.math.graph.visitors.hint.TraversalHint;
import field.namespace.generic.IFunction;
import field.util.collect.tuple.Pair;
import field.util.ANSIColorUtils;
import org.python.core.Py;
import org.python.core.PyModule;
import org.python.core.PyObject;

import java.util.*;

import static field.core.dispatch.override.IVisualElementOverrides.forward;
import static field.core.dispatch.override.IVisualElementOverrides.topology;

public
class PythonPlugin implements iPlugin {

    public
    class CapturedEnvironment {
        private final IVisualElement element;
        HashMap<String, IUpdateable> exitHandler = new HashMap<String, IUpdateable>();
        HashMap<String, IUpdateable> transientExitHandler = new HashMap<String, IUpdateable>();

        public
        CapturedEnvironment(IVisualElement element) {
            this.element = element;
        }

        public
        void addExitHandler(String key, IUpdateable updateable) {
            exitHandler.put(key, updateable);
        }

        public
        void addTransientHandler(String key, IUpdateable updateable) {
            transientExitHandler.put(key, updateable);
        }

        public
        boolean hasTransientHandler(String key) {
            return transientExitHandler.containsKey(key);
        }

        public
        void enter() {
            Promise p = promiseFor(element);
            p.beginExecute();
            configurePythonEnvironment(element);
        }

        public
        void exit() {
            for (IUpdateable e : exitHandler.values()) {
                e.update();
            }
            for (IUpdateable e : transientExitHandler.values()) {
                e.update();
            }
            transientExitHandler.clear();
            configurePythonPostEnvironment(element);
            Promise p = promiseFor(element);
            p.endExecute();
        }

        public
        boolean hasExitHandler(String h) {
            return exitHandler.containsKey(h);
        }

        public
        void runExit() {
            for (IUpdateable e : exitHandler.values()) {
                e.update();
            }
            for (IUpdateable e : transientExitHandler.values()) {
                e.update();
            }
            transientExitHandler.clear();

        }

        public
        boolean throwException(String when, Throwable t, CapturedEnvironment parent) {
            handleExceptionThrownDuringRunning(when, element, (parent == null) ? null : parent.element, t);

            return false;
        }

    }

    public
    class LocalPromise implements Promise, DerivativePromise {

        public final IVisualElement element;

        public Stack<CapturedEnvironment> ongoingEnvironments = new Stack<CapturedEnvironment>();

        private final IVisualElementOverrides forward;

        private final IVisualElementOverrides backward;

        int isExecutionCount = 0;

        VisualElementProperty<String> property = python_source;

        public
        LocalPromise(IVisualElement element) {
            this.element = element;
            forward = IVisualElementOverrides.MakeDispatchProxy.getOverrideProxyFor(element);
            backward = IVisualElementOverrides.MakeDispatchProxy.getBackwardsOverrideProxyFor(element);
        }

        public
        void beginExecute() {
            Logging.external();

            stackOfPythonPositionablesExecuting.push(element);
            CapturedEnvironment ce = configurePythonEnvironment(element);

            if (Logging.enabled()) Logging.logging.addEvent(new ElementExecutionFocusBegin(element));

            ongoingEnvironments.push(ce);
            ongoingEnvironment = ce;
        }

        public
        void endExecute() {
            ongoingEnvironments.pop();

            if (Logging.enabled()) Logging.logging.addEvent(new ElementExecutionFocusEnd(element));
            configurePythonPostEnvironment(element);
            stackOfPythonPositionablesExecuting.pop();

            ongoingEnvironment = !environments.isEmpty() ? environments.peek() : null;

            Logging.internal();
        }

        public
        PyObject getAttributes() {
            return (PyObject) getAttributesForElement(element);
        }

        public
        Promise getDerivativeWithText(final VisualElementProperty<String> prop) {
            LocalPromise p2 = new LocalPromise(element);
            p2.property = prop;
            return p2;
        }

        public
        float getEnd() {
            Rect o = new Rect(0, 0, 0, 0);
            element.getFrame(o);
            return (float) (o.x + o.w);
        }

        public
        Stack<CapturedEnvironment> getOngoingEnvironments() {
            return ongoingEnvironments;
        }

        public
        float getPriority() {
            Rect o = new Rect(0, 0, 0, 0);
            element.getFrame(o);
            return (float) (o.y + (o.x / 1000f));
        }

        public
        float getStart() {
            Rect o = new Rect(0, 0, 0, 0);
            element.getFrame(o);
            return (float) o.x;
        }

        public
        String getText() {
            String s = property.get(element);
            IFunction<String, String> f = python_sourceFilter.get(element);
            if (f != null) s = f.apply(s);

            if (s == null) s = "";
            return s;
        }

        @Override
        public
        String toString() {
            return element.getProperty(IVisualElement.name);
        }

        public
        void willExecute() {
            backward.setProperty(element, python_isExecuting, new Ref<Boolean>(true));
            backward.setProperty(element, IVisualElement.dirty, new Ref<Boolean>(true));
            isExecutionCount++;
            Logging.external();
            if (Logging.enabled()) Logging.logging.addEvent(new ElementExecutionBegin(element));
            Logging.internal();
        }

        public
        void willExecuteSubstring(String actualSubstring, int start, int end) {
            Logging.external();
            if (Logging.enabled())
                Logging.logging.addEvent(new ElementInvocationLogging.ElementTextFragmentWasExecuted(actualSubstring,
                                                                                                     element));
            Logging.internal();
        }

        public
        void wontExecute() {
            Logging.external();
            if (Logging.enabled()) Logging.logging.addEvent(new ElementExecutionEnd(element));
            Logging.internal();

            isExecutionCount = 0;
            if (isExecutionCount < 0) isExecutionCount = 0;
            if (isExecutionCount == 0) {
                backward.setProperty(element, python_isExecuting, new Ref<Boolean>(false));
                backward.setProperty(element, IVisualElement.dirty, new Ref<Boolean>(false));
            }
        }

        @Override
        public
        boolean isPaused() {
            Boolean n = element.getProperty(python_isPaused);
            return ((n != null) && n);
        }

    }

    public
    class LocalVisualElement extends NodeImpl<IVisualElement> implements IVisualElement {

        public
        <T> void deleteProperty(VisualElementProperty<T> p) {
            properties.remove(p);
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
    class Overrides extends DefaultOverride {
        private TriangleMesh triangles;

        private iDynamicMesh triangle;

        @Override
        public
        TraversalHint added(IVisualElement newSource) {

            informationFor(newSource);
            return StandardTraversalHint.CONTINUE;
        }

        @Override
        public
        TraversalHint deleted(IVisualElement source) {
            deleteElement(source);
            return StandardTraversalHint.CONTINUE;
        }

        @Override
        public
        TraversalHint paintNow(IVisualElement source, Rect bounds, boolean visible) {

            // is
            // it
            // executing?
            Boolean is = source.getProperty(python_isExecuting);
            if ((is != null) && is && GLComponentWindow.draft) {
                if (triangles == null) {
                    triangles = new BasicGeometry.TriangleMesh();
                    triangles.rebuildVertex(0).rebuildTriangle(0);
                    triangle = new DynamicMesh(triangles);
                }

                Boolean p = source.getProperty(python_isPaused);
                boolean paused = (p != null) && p;

                p = source.getProperty(SplineComputingOverride.noFrame);
                if ((p != null) && p) {
                    ComponentDrawingUtils.drawRectangle(triangle,
                                                        null,
                                                        null,
                                                        (float) bounds.x + 2,
                                                        (float) bounds.y + 2,
                                                        (float) 15,
                                                        (float) 15,
                                                        paused
                                                        ? Constants.paused_execution_color
                                                        : Constants.execution_color,
                                                        null);
                    triangles.performPass(null);
                }
                else {
                    ComponentDrawingUtils.drawRectangle(triangle,
                                                        null,
                                                        null,
                                                        (float) bounds.x,
                                                        (float) bounds.y,
                                                        (float) bounds.w,
                                                        (float) bounds.h,
                                                        paused
                                                        ? Constants.paused_execution_color
                                                        : Constants.execution_color,
                                                        null);
                    triangles.performPass(null);
                }
            }

            return super.paintNow(source, bounds, visible);
        }

    }

    public static
    class PythonTextualInformation {
        String uid;

        HashMap<String, Object> persistantVariables = new HashMap();
    }

    public static
    class UnacknowledgedError {
        transient Throwable cause;
        String when;
        VisualElementReference parent;
        VisualElementReference inside;
        VisualElementReference connectedTo;

        public
        UnacknowledgedError(String when, Throwable cause, IVisualElement inside, IVisualElement parent) {
            this.when = when;
            this.cause = cause;
            this.parent = parent == null ? null : new VisualElementReference(parent);
            this.inside = new VisualElementReference(inside);
        }

    }

    public static final String pluginId = "//plugin_python";

    public static final VisualElementProperty<String> python_source =
            new VisualElementProperty<String>("python_source_v");

    public static final VisualElementProperty<String> python_source_forExecution =
            new VisualElementProperty<String>("python_source_forExecution", python_source);

    public static final VisualElementProperty<Boolean> python_isExecuting =
            new VisualElementProperty<Boolean>("python_isExecuting_");
    public static final VisualElementProperty<Boolean> python_isPaused =
            new VisualElementProperty<Boolean>("python_isPaused_");

    public static final VisualElementProperty<Stack<UnacknowledgedError>> python_unacknowledgedError =
            new VisualElementProperty<Stack<UnacknowledgedError>>("python_unacknowledgedError");

    public static final VisualElementProperty<PythonPlugin> python_plugin =
            new VisualElementProperty<PythonPlugin>("python_plugin_");

    public static final VisualElementProperty<Globals> python_globals =
            new VisualElementProperty<Globals>("python_globals_");

    public static final VisualElementProperty<IFunction<String, String>> python_sourceFilter =
            new VisualElementProperty<IFunction<String, String>>("python_sourceFilter");

    public static final VisualElementProperty<Map<String, field.core.ui.text.rulers.ExecutedAreas.State>> python_areas =
            new VisualElementProperty<Map<String, field.core.ui.text.rulers.ExecutedAreas.State>>("python_areas");

    public static
    String externalPropertyNameToInternalName(String name) {
        return name;

        // if (name.endsWith("__")) {
        // name = name.substring(0, name.length() - 2) +
        // ".+";
        // } else if (name.endsWith("_") ||
        // name.startsWith("_menu"))
        // name = name + ".//";
        // else {
        // if (name.startsWith("_i"))
        // name = name.substring(2) + ".inspect";
        // }
        // return name;
    }

    public static
    Object getAttr(IVisualElement from, IVisualElement to, String name) {
        name = externalPropertyNameToInternalName(name);

        // ;//System.out.println(" get attr <"+from+" "+to+" "+name+">");

        VisualElementProperty<Object> n = new VisualElementProperty<Object>(name);
        Ref<Object> r = new Ref<Object>(null);

        topology.begin(to);
        forward.getProperty.getProperty(to, n, r);
        topology.end(to);

        // ;//System.out.println(" returning <"+r.get()+">");
        return r.get();
    }

    public static
    Object getAttr(IVisualElement from, String name) {
        return getAttr(from, from, name);
    }

    public static
    Object getLocalProperty(IVisualElement of, String name) {
        name = externalPropertyNameToInternalName(name);
        VisualElementProperty<Object> n = new VisualElementProperty<Object>(name);
        return of.getProperty(n);
    }

    public static
    String internalPropertyNameToExternalName(VisualElementProperty p) {
        String name = p.getName();
        // if (name.contains("."))
        // name = name.split("\\.")[0];
        //
        // if (p.containsSuffix("+")) {
        // name = name + "__";
        // }
        // if (p.containsSuffix("//")) {
        // name = name + "_";
        // }
        // if (p.containsSuffix("inspect")) {
        // name = "_i" + name;
        // }
        //
        return name;
    }

    public static
    List<String> listAttr(IVisualElement from, IVisualElement to) {

        final List<String> rr = new ArrayList<String>();

        new GraphNodeSearching.GraphNodeVisitor_depthFirst<IVisualElement>(true) {

            @Override
            protected
            TraversalHint visit(IVisualElement from) {
                Map<Object, Object> q = from.payload();
                for (Map.Entry<Object, Object> e : q.entrySet()) {
                    if (e.getKey() instanceof VisualElementProperty) {
                        String n = internalPropertyNameToExternalName(((VisualElementProperty) e.getKey()));
                        if (!rr.contains(n)) rr.add(n);
                    }

                }
                return StandardTraversalHint.CONTINUE;
            }

        }.apply(from);

        return rr;
    }

    public static
    void redraw(IVisualElement o) {
        o.setProperty(IVisualElement.dirty, true);
    }

    public static
    void setAttr(IVisualElement from, IVisualElement to, String name, Object value) {
        name = externalPropertyNameToInternalName(name);
        VisualElementProperty<Object> n = new VisualElementProperty<Object>(name);
        n.set(to, to, value);

        // topology.begin(from);
        // backward.setProperty.setProperty(to, n, new
        // Ref<Object>(value));
        // topology.end(from);
    }

    public static
    void setAttr(IVisualElement to, String name, Object value) {
        name = externalPropertyNameToInternalName(name);
        VisualElementProperty<Object> n = new VisualElementProperty<Object>(name);

        // topology.begin(to);
        // backward.setProperty.setProperty(to, n, new
        // Ref<Object>(value));
        // topology.end(to);

        n.set(to, to, value);

    }

    public static PyModule toolsModule;

    protected LocalVisualElement lve;

    protected SelectionGroup<iComponent> group;

    protected IVisualElement root;

    protected Stack<IVisualElement> stackOfPythonPositionablesExecuting = new Stack<IVisualElement>();

    HashMap<IVisualElement, Object> cachedAttributeAccess = new HashMap<IVisualElement, Object>();

    Stack<Object> attributeDicts = new Stack<Object>();

    Stack<CapturedEnvironment> environments = new Stack<CapturedEnvironment>();

    public static CapturedEnvironment ongoingEnvironment;

    Globals globals = new Globals();

    HashMap<String, PythonTextualInformation> database = new HashMap<String, PythonTextualInformation>();

    HashMap<IVisualElement, PythonScriptingSystem.Promise> promises =
            new HashMap<IVisualElement, PythonScriptingSystem.Promise>();

    Map<Object, Object> properties = new HashMap<Object, Object>();

    IVisualElementOverrides elementOverride;

    public
    PythonPlugin() {
        lve = new LocalVisualElement();
    }

    public
    void close() {
    }

    public
    Object getPersistanceInformation() {
        return new Pair<String, HashMap<String, PythonTextualInformation>>(pluginId + "version_1", database);
    }

    public
    IVisualElement getWellKnownVisualElement(String id) {
        if (id.equals(pluginId)) return lve;
        return null;
    }

    public
    void registeredWith(IVisualElement root) {

        this.root = root;

        // PythonInterface.getPythonInterface().execString("from FluidTools import *");
        PythonInterface.getPythonInterface().execString("import FluidTools");
        PyObject module = (PyObject) PythonInterface.getPythonInterface()
                                                    .executeStringReturnRawValue("_fluidTools_module=FluidTools",
                                                                                 "_fluidTools_module");
        toolsModule = (PyModule) module;
        lve.setProperty(python_plugin, this);
        lve.setProperty(python_globals, globals);
        // add a next
        // to root that
        // adds some
        // overrides
        root.addChild(lve);

        // register for
        // selection
        // updates? (no,
        // do it in
        // subclass)
        group = root.getProperty(IVisualElement.selectionGroup);

        elementOverride = createElementOverrides();

    }

    public
    void setPersistanceInformation(Object o) {
        if (o instanceof Pair) {
            Pair<String, HashMap<String, PythonTextualInformation>> p =
                    (Pair<String, HashMap<String, PythonTextualInformation>>) o;
            if (p.left.equals(pluginId + "version_1")) {
                // database
                // =
                // p.right;
            }
        }
    }

    public
    void update() {
    }

    private
    iGlobalTrap globalTrapFor(IVisualElement element) {
        return globals.globalTrapFor(element);
    }

    protected
    CapturedEnvironment configurePythonEnvironment(IVisualElement element) {
        final Object rwas = PythonInterface.getPythonInterface().getVariable("_r");
        final Object swas = PythonInterface.getPythonInterface().getVariable("_self");
        PythonInterface.getPythonInterface().setVariable("_self", element);
        if (PythonInterface.getPythonInterface().getLanguage() == Language.python) {

            Object object = getAttributesForElement(element);
            attributeDicts.push(object);
            PythonInterface.getPythonInterface().setVariable("_a", object);

            toolsModule.__dict__.__setitem__("_self", Py.java2py(element));
        }
        CapturedEnvironment capenv = new CapturedEnvironment(element);
        PythonInterface.getPythonInterface().setVariable("_environment", capenv);

        final String modWas = PythonInterface.getPythonInterface().getModuleName();
        PythonInterface.getPythonInterface()
                       .setModuleName(element.getProperty(IVisualElement.name) + '[' + element.getUniqueID());

        capenv.exitHandler.put("restore _r", new IUpdateable() {
            public
            void update() {
                PythonInterface.getPythonInterface().setVariable("_r", rwas);
                PythonInterface.getPythonInterface().setVariable("_self", swas);
                PythonInterface.getPythonInterface().setModuleName(modWas);
            }
        });

        PythonInterface.getPythonInterface().setVariable("_r", null);

        PythonInterface.getPythonInterface().pushGlobalTrap(globalTrapFor(element));
        environments.push(capenv);
        ongoingEnvironment = capenv;
        return capenv;
    }

    protected
    void configurePythonPostEnvironment(IVisualElement element) {
        PythonInterface.getPythonInterface().popGlobalTrap();

        if (environments.size() > 0) {
            CapturedEnvironment was = environments.pop();
            was.runExit();
        }

        if (attributeDicts.size() > 0) attributeDicts.pop();

        if (attributeDicts.size() > 0) PythonInterface.getPythonInterface().setVariable("_a", attributeDicts.peek());

        if (environments.size() > 0)
            PythonInterface.getPythonInterface().setVariable("_environment", environments.peek());
        ongoingEnvironment = environments.size() > 0 ? environments.peek() : null;
    }

    protected
    IVisualElementOverrides createElementOverrides() {
        return new Overrides().setVisualElement(lve);
    }

    protected
    void deleteElement(IVisualElement element) {

        //System.out.println(" delete element <" + element.getUniqueID() + "> database is <" + database + ">");

        database.remove(element.getUniqueID());
        Promise p = promises.get(element);
        Ref<PythonScriptingSystem> pss = new Ref<PythonScriptingSystem>(null);
        IVisualElementOverrides.MakeDispatchProxy.getOverrideProxyFor(element)
                                                       .getProperty(element,
                                                                    PythonScriptingSystem.pythonScriptingSystem,
                                                                    pss);
        if (pss.get() != null) pss.get().revokePromise(element);
    }

    protected
    Object getAttributesForElement(IVisualElement element) {
        Object object = cachedAttributeAccess.get(element);
        if (object == null) {
            PythonInterface.getPythonInterface().setVariable("__f", element);
            PythonInterface.getPythonInterface().setVariable("__t", element);

            Object a = PythonInterface.getPythonInterface().executeStringReturnRawValue("__a = _self", "__a");
            cachedAttributeAccess.put(element, object = a);
        }
        return object;
    }

    protected
    void handleExceptionThrownDuringRunning(String when,
                                            IVisualElement element,
                                            IVisualElement parentElement,
                                            Throwable t) {

    }

    protected
    PythonTextualInformation informationFor(IVisualElement element) {

        PythonTextualInformation information = database.get(element.getUniqueID());

        if (information == null || promises.get(element) == null) {
            information = newPythonTextualInformation(element);
            database.put(element.getUniqueID(), information);
        }

        return information;
    }

    protected
    Promise newPromiseFor(IVisualElement element) {
        return new LocalPromise(element);
    }

    protected
    PythonTextualInformation newPythonTextualInformation(IVisualElement element) {
        PythonTextualInformation info = new PythonTextualInformation();
        info.uid = element.getUniqueID();

        promises.put(element, newPromiseFor(element));

        // tell any
        // scripting
        // interfrace

        PythonScriptingSystem pss = PythonScriptingSystem.pythonScriptingSystem.get(element);

        if (pss != null) {
            pss.promisePythonScriptingElement(element, promises.get(element));
        }
        else {
            System.err.println(ANSIColorUtils.red(" warning: no pss for element <" + element + "? ??"));
        }

        return info;
    }

    protected
    Promise promiseFor(IVisualElement element) {
        Promise p = promises.get(element);
        if (p == null) {
            promises.put(element, p = newPromiseFor(element));
        }
        return p;
    }

}
