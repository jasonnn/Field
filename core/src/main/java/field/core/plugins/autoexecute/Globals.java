package field.core.plugins.autoexecute;

import field.core.dispatch.IVisualElement;
import field.core.execution.PythonInterface;
import field.core.execution.ScriptingInterface.iGlobalTrap;
import field.core.plugins.drawing.SplineComputingOverride;
import field.core.plugins.log.ElementInvocationLogging;
import field.core.plugins.log.Logging;
import field.core.plugins.python.PythonPlugin;
import field.util.collect.tuple.Pair;
import field.util.HashMapOfLists;
import org.python.core.Py;
import org.python.core.PyObject;

import java.util.*;
import java.util.Map.Entry;

public
class Globals {

    HashMap<String, IVisualElement> potentialDeclarations = new HashMap<String, IVisualElement>() {
        protected
        java.util.Collection<IVisualElement> newList() {
            return new LinkedHashSet();
        }
    };
    LinkedHashSet<String> knownMultipleDeclarations = new LinkedHashSet<String>();

    HashMap<String, IVisualElement> knownDeclarations = new HashMap<String, IVisualElement>() {
        protected
        java.util.Collection<IVisualElement> newList() {
            return new LinkedHashSet();
        }
    };

    HashMapOfLists<String, IVisualElement> recordedUses = new HashMapOfLists<String, IVisualElement>() {
        protected
        java.util.Collection<IVisualElement> newList() {
            return new LinkedHashSet();
        }
    };

    Object nothing = new Object();

    Stack<IVisualElement> running = new Stack<IVisualElement>();

    public
    Globals() {
    }

    public
    void declare(IVisualElement e, String name) {
        knownDeclarations.put(name, e);
    }

    public
    List<String> getPotentialDefinedBy(IVisualElement oneThing) {
        List<String> r = new ArrayList<String>();
        Set<Entry<String, IVisualElement>> es = potentialDeclarations.entrySet();
        for (Entry<String, IVisualElement> e : es) {
            if (e.getValue().equals(oneThing)) {
                r.add(e.getKey());
            }
        }
        return r;
    }

    public
    List<Pair<String, IVisualElement>> getUsedBy(IVisualElement oneThing) {
        Collection<Pair<String, IVisualElement>> r = new LinkedHashSet<Pair<String, IVisualElement>>();
        Set<Entry<String, Collection<IVisualElement>>> m = recordedUses.entrySet();
        for (Entry<String, Collection<IVisualElement>> e : m) {
            for (IVisualElement v : e.getValue()) {
                if (v.equals(oneThing)) {
                    r.add(new Pair<String, IVisualElement>(e.getKey(), knownDeclarations.get(e.getKey())));
                }
            }
        }
        return new ArrayList<Pair<String, IVisualElement>>(r);
    }

    public
    HashMapOfLists<String, IVisualElement> getUsedDefinedBy(IVisualElement oneThing) {
        HashMapOfLists<String, IVisualElement> r = new HashMapOfLists<String, IVisualElement>() {
            @Override
            protected
            Collection<IVisualElement> newList() {
                return new LinkedHashSet<IVisualElement>();
            }
        };

        Set<Entry<String, IVisualElement>> es = knownDeclarations.entrySet();
        for (Entry<String, IVisualElement> e : es) {
            if (e.getValue().equals(oneThing)) {
                String m = e.getKey();

                Collection<IVisualElement> q = recordedUses.get(m);
                if (q == null) q = Collections.EMPTY_LIST;

                r.addAllToList(m, q);
            }
        }
        return r;
    }

    public
    IVisualElement getExistingDefinitionFor(String name) {
        return knownDeclarations.get(name);
    }

    public
    IVisualElement getPotentialDefinitionFor(String name) {
        return potentialDeclarations.get(name);
    }

    public
    List<IVisualElement> getUses(String name) {
        Collection<IVisualElement> rr = recordedUses.get(name);
        if (rr == null) return null;
        return new ArrayList<IVisualElement>(rr);
    }

    public
    iGlobalTrap globalTrapFor(final IVisualElement e) {
        return new iGlobalTrap() {

            public
            Object findItem(String name, Object actuallyIs) {

                if (name.startsWith("_")) {
                    if (Logging.enabled())
                        Logging.logging.addEvent(new ElementInvocationLogging.DidGetLocalVariable(name, actuallyIs));
                    return actuallyIs;
                }
                if (actuallyIs == null) {
                    IVisualElement willExec = knownDeclarations.get(name);
                    if (willExec != null) {
                        if (Logging.enabled())
                            Logging.logging.addEvent(new ElementInvocationLogging.WillGetLocalVariableByAutoExecution(name,
                                                                                                                      willExec));

                        Object o = trapAutoExec(e, name);
                        if (o == nothing) {
                            Logging.logging.addEvent(new ElementInvocationLogging.DidGetLocalVariableByAutoExecution(name,
                                                                                                                     willExec,
                                                                                                                     null));
                            return actuallyIs;
                        }
                        if (Logging.enabled())
                            Logging.logging.addEvent(new ElementInvocationLogging.DidGetLocalVariableByAutoExecution(name,
                                                                                                                     willExec,
                                                                                                                     o));
                        recordedUses.addToList(name, e);
                        return o;
                    }
                    if (Logging.enabled())
                        Logging.logging.addEvent(new ElementInvocationLogging.DidGetLocalVariable(name, null));
                    return actuallyIs;
                }
                else if (potentialDeclarations.containsKey(name)) {
                    trapFinalizeDeclaration(e, name, actuallyIs);
                }

                if (Logging.enabled())
                    Logging.logging.addEvent(new ElementInvocationLogging.DidGetLocalVariable(name, actuallyIs));
                return actuallyIs;
            }

            public
            Object setItem(String name, Object was, Object to) {

                if (Logging.enabled())
                    Logging.logging.addEvent(new ElementInvocationLogging.DidSetLocalVariable(name, to, was));
                if (name.startsWith("_")) return to;
                if (((was == null) || (was == Py.None)) && ((to != null) && (to != Py.None))) {
                    trapDeclareInside(e, name, to);
                }
                return to;
            }
        };
    }

    private static
    String declForName(String name) {
        return "_a.python_globals_.declare(_self, \"" + name + "\")\n";
    }

    private static
    String declForNameNew(String name) {
        return "_self.python_globals_.declare(_self, \"" + name + "\")\n";
    }

    protected
    Object trapAutoExec(IVisualElement e, String name) {
        IVisualElement known = knownDeclarations.get(name);
        if (known != null) {
            if (!running.contains(known)) {
                running.add(known);
                try {

                    // PythonInterface
                    // .
                    // getPythonInterface
                    // (
                    // )
                    // .
                    // execString
                    // (
                    // "print 'trapAutoExec:"
                    // +
                    // known
                    // .
                    // getProperty
                    // (
                    // iVisualElement
                    // .
                    // name
                    // )
                    // +
                    // " for "
                    // +
                    // name
                    // +
                    // "'"
                    // )
                    // ;

                    PythonInterface.getPythonInterface()
                                   .print("Automatically executing "
                                          + known.getProperty(IVisualElement.name)
                                          + "' to resolve '"
                                          + name
                                          + "'\n");

                    //System.out.println("auto executing '" + known.getProperty(iVisualElement.name) + "' to resolve '" + name + "'");
                    // OutputInserts.printFoldStart("auto executing '"
                    // +
                    // known.getProperty(iVisualElement.name)
                    // + "' to resolve '" + name + "'", e,
                    // new Vector4(0, 0, 0.5f, 0.25f));
                    SplineComputingOverride.executeMain(known);
                    // OutputInserts.printFoldEnd("auto executing '"
                    // +
                    // known.getProperty(iVisualElement.name)
                    // + "' to resolve '" + name + "'", e);

                    // PythonInterface
                    // .
                    // getPythonInterface
                    // (
                    // )
                    // .
                    // execString
                    // (
                    // "print 'trapAutoExec:"
                    // +
                    // known
                    // .
                    // getProperty
                    // (
                    // iVisualElement
                    // .
                    // name
                    // )
                    // +
                    // " complete'"
                    // )
                    // ;

                    PythonInterface.getPythonInterface().setVariable("_self", e);
                    PythonPlugin.toolsModule.__dict__.__setitem__("_self", Py.java2py(e));

                    PyObject now = PythonInterface.getPythonInterface().getLocalDictionary().__superfinditem__(name);
                    //System.out.println(" executed and now we get <" + now + ">");
                    if (now == null) {
                        System.err.println(" warning: tried to execute <"
                                           + IVisualElement.name.get(known)
                                           + "> in order to declare a <"
                                           + name
                                           + "> but got nothing");
                        // remove
                        // the
                        // autodecl
                        String current = AutoExecutePythonPlugin.python_autoExec.get(e);
                        if (current == null) return now;

                        current = current.replaceAll(declForName(name), "\n");
                        current = current.replaceAll(declForNameNew(name), "\n");
                        AutoExecutePythonPlugin.python_autoExec.set(e, e, current);
                        knownDeclarations.remove(name);
                    }
                    return now;
                } finally {
                    IVisualElement a = running.pop();
                    assert a == known;
                }
            }
        }

        // need to look up result

        return nothing;
    }

    protected
    void trapDeclareInside(IVisualElement e, String name, Object to) {
        if (!knownMultipleDeclarations.contains(name)) {
            IVisualElement x = potentialDeclarations.put(name, e);
            if ((x != e) && (x != null)) {
                potentialDeclarations.remove(name);
                knownMultipleDeclarations.add(name);
            }
        }
    }

    protected
    void trapFinalizeDeclaration(IVisualElement e, String name, Object actuallyIs) {
        if (knownMultipleDeclarations.contains(name)) return;

        IVisualElement declaredBy = potentialDeclarations.get(name);
        if (declaredBy != e) {
            IVisualElement allready = knownDeclarations.get(name);
            if (allready == null) {

                if (Logging.enabled())
                    Logging.logging.addEvent(new ElementInvocationLogging.MakeAutoExecutionTarget(name,
                                                                                                  declaredBy,
                                                                                                  actuallyIs));

                String current = AutoExecutePythonPlugin.python_autoExec.get(declaredBy);
                if (current == null) current = "\n";
                if (!current.endsWith("\n")) current += "\n";
                current += declForNameNew(name);
                AutoExecutePythonPlugin.python_autoExec.set(declaredBy, declaredBy, current);

                knownDeclarations.put(name, declaredBy);
            }
            recordedUses.addToList(name, e);
        }
    }

}
