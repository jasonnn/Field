package field.core.plugins.history;

import field.core.dispatch.IVisualElement;
import field.core.dispatch.override.IVisualElementOverrides;
import field.core.dispatch.Mixins;
import field.core.dispatch.Mixins.iMixinProxy;
import field.core.dispatch.VisualElement;
import field.core.dispatch.Rect;
import field.core.dispatch.VisualElementProperty;
import field.core.dispatch.override.DefaultOverride;
import field.core.dispatch.override.Ref;
import field.core.dispatch.override.IDefaultOverride;
import field.core.execution.TemporalSliderOverrides;
import field.core.persistance.FluidCopyPastePersistence;
import field.core.plugins.drawing.SplineComputingOverride;
import field.core.plugins.python.OutputInsertsOnSheet;
import field.core.plugins.python.PythonPlugin;
import field.core.plugins.python.PythonPluginEditor;
import field.core.util.LoadInternalWorkspaceFile;
import field.core.windowing.components.iComponent;
import field.launch.SystemProperties;
import field.namespace.generic.IFunction;
import field.util.collect.tuple.Pair;
import field.util.collect.tuple.Triple;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public
class Templating {

    public static HashMap<String, VisualElementProperty<String>> shortForms =
            new HashMap<String, VisualElementProperty<String>>();

    static {
        shortForms.put("main", PythonPlugin.python_source);
        shortForms.put("update", SplineComputingOverride.onChange);
        shortForms.put("resize", SplineComputingOverride.onFrameChange);
        shortForms.put("select", SplineComputingOverride.onSelection);
        shortForms.put("tweak", SplineComputingOverride.tweak);
    }

    public static
    IVisualElement elementFromKnownTemplate(String name, IVisualElement root) throws IOException {
        File found = LoadInternalWorkspaceFile.findTemplateCalled(name);
        if (found == null) return null;

        String canonicalPath = found.getCanonicalPath();

        if (canonicalPath.endsWith("/")) canonicalPath = canonicalPath.substring(0, canonicalPath.length() - 1);
        String[] split = canonicalPath.split("/");

        String uid = split[split.length - 1];
        String sheetname = split[split.length - 2];

        HashSet<IVisualElement> loaded = FluidCopyPastePersistence.copyFromNonloaded(Collections.singleton(uid),
                                                                                     new File(canonicalPath).getParent()
                                                                                     + "/sheet.xml",
                                                                                     root,
                                                                                     IVisualElement.copyPaste.get(root));

        for (IVisualElement e : loaded) {
            PythonPluginEditor.python_isTemplateHead.delete(e, e);
        }

        return loaded.iterator().next();
    }

    public static
    ArrayList<IVisualElement> elementsFromKnownSheet(String name, IVisualElement root) throws IOException {
        File sheetName = new File(SystemProperties.getDirProperty("versioning.dir") + name);
        if (!sheetName.exists()) return null;

        HashSet<IVisualElement> loaded = FluidCopyPastePersistence.copyFromNonloaded(null,
                                                                                     sheetName.getCanonicalPath()
                                                                                     + "/sheet.xml",
                                                                                     root,
                                                                                     IVisualElement.copyPaste.get(root));

        return new ArrayList<IVisualElement>(loaded);
    }

    public static
    ArrayList<IVisualElement> elementsFromKnownSheetNoTimeslider(String name, IVisualElement root) throws IOException {
        File sheetName = new File(SystemProperties.getDirProperty("versioning.dir") + name);
        if (!sheetName.exists()) return null;

        HashSet<IVisualElement> loaded =
                FluidCopyPastePersistence.copyFromNonloadedPredicate(new IFunction<IVisualElement, Boolean>() {
                    public
                    Boolean apply(IVisualElement in) {

                        IVisualElementOverrides i = in.getProperty(IVisualElement.overrides);
                        if (i == null) return false;
                        if (i instanceof TemporalSliderOverrides) return false;
                        return true;

                    }
                }, sheetName.getCanonicalPath() + "/sheet.xml", root, IVisualElement.copyPaste.get(root));

        return new ArrayList<IVisualElement>(loaded);
    }

    public static
    Pair<String, String> findTemplateInstance(final String templateName) {
        File dir = new File(SystemProperties.getDirProperty("versioning.dir"));
        File[] files = dir.listFiles(new FileFilter() {
            public
            boolean accept(File pathname) {
                try {
                    if (pathname.isDirectory() && new File(pathname.getCanonicalPath() + "/sheet.xml").exists())
                        return true;
                } catch (IOException e) {
                }
                return false;
            }
        });

        final Pair<String, String> ret = new Pair<String, String>(null, null);

        for (File f : files) {
            f.listFiles(new FileFilter() {

                public
                boolean accept(File pathname) {
                    try {
                        File ff = new File(pathname.getCanonicalPath() + "/python_isTemplate.+.property");
                        if (pathname.isDirectory() && ff.exists()) {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(ff)));
                            StringBuilder read = new StringBuilder();
                            while (reader.ready()) {
                                read.append(reader.readLine()).append('\n');
                            }
                            reader.close();
                            String name = (String) VersioningSystem.objectRepresentationFor(read.toString());

                            if (name.trim().equals(templateName)) {

                                String cp = pathname.getCanonicalPath();
                                String[] split = cp.split("/");

                                String uid = split[split.length - 1];
                                String sheetname = split[split.length - 2];

                                ret.left = uid;
                                ret.right = pathname.getParentFile() + "/sheet.xml";
                            }
                        }
                    } catch (IOException e) {
                    }
                    return false;
                }
            });
            if (ret.left != null) return ret;
        }
        return null;
    }

    public static
    void merge(VisualElement source, IVisualElement copy, boolean preferTemplate, boolean becomeVisual)
            throws IOException {

        LoadInternalWorkspaceFile liwf = new LoadInternalWorkspaceFile();

        Map<Object, Object> sourceP = source.payload();
        Map<Object, Object> m = copy.payload();
        Set<Entry<Object, Object>> es = m.entrySet();
        for (Entry<Object, Object> e : es) {
            VisualElementProperty key = (VisualElementProperty) e.getKey();
            if (key.containsSuffix("v")) {
                Object originalText = LoadInternalWorkspaceFile.getOriginalText(copy, key);
                if (originalText instanceof String) {
                    boolean c = LoadInternalWorkspaceFile.diff3DoesConflict((String) e.getValue(),
                                                                            (String) originalText,
                                                                            (String) source.getProperty(key));
                    if (c) {
                        if (becomeVisual) {
                            String o = LoadInternalWorkspaceFile.performThreeWayMerge((String) e.getValue(),
                                                                                      (String) source.getProperty(key),
                                                                                      (String) originalText);
                            key.set(copy, copy, o);
                        }
                        else {
                            performCopy(source, (VisualElement) copy, key, source.getProperty(key));
                        }
                    }
                    else {
                        String o = LoadInternalWorkspaceFile.performThreeWayMergeNonVisually((String) e.getValue(),
                                                                                             (String) source.getProperty(key),
                                                                                             (String) originalText);
                        key.set(copy, copy, o);
                    }
                }
                else {
                    String o = LoadInternalWorkspaceFile.performThreeWayMergeNonVisually((String) e.getValue(),
                                                                                         (String) source.getProperty(key),
                                                                                         (String) originalText);
                    key.set(copy, copy, o);
                }
            }
            else if (preferTemplate && sourceP.containsKey(key)) {
                key.set(copy, copy, sourceP.get(key));
            }
        }

    }

    public static
    void newEditableProperty(String propertyName, IVisualElement inside) {
        VisualElementProperty p = new VisualElementProperty(propertyName);
        PythonPluginEditor.knownPythonProperties.put("Template \u2014 " + propertyName, p);
    }

    public static
    IVisualElement simpleCopy(VisualElement source, IVisualElement dispatchTo) {
        IVisualElementOverrides o = source.getProperty(IVisualElement.overrides);
        iComponent c = source.getProperty(IVisualElement.localView);

        Rect f = source.getFrame(null);
        f.x += 10;
        f.y += 10;

        Class oclass = o.getClass();
        List<IVisualElementOverrides> callList = null;

        if (o instanceof iMixinProxy) {
            //System.out.println(" o is <" + o + ">");

            callList = ((iMixinProxy) o).getCallList();
            oclass = DefaultOverride.class;
        }

        Triple<VisualElement, iComponent, DefaultOverride> created = VisualElement.createWithName(f,
                                                                                                  dispatchTo,
                                                                                                  (Class<VisualElement>) source.getClass(),
                                                                                                  (Class<iComponent>) c.getClass(),
                                                                                                  (Class<DefaultOverride>) oclass,
                                                                                                  IVisualElement.name.get(source)
                                                                                                  + " (copy)");

        if (callList != null) {
            IVisualElementOverrides[] over = new IVisualElementOverrides[callList.size()];
            for (int i = 0; i < over.length; i++) {
                try {
                    over[i] = callList.get(i).getClass().newInstance();
                } catch (InstantiationException e1) {
                    e1.printStackTrace();
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                }
            }
            IVisualElementOverrides newOver =
                    Mixins.make(IVisualElementOverrides.class, Mixins.visitCodeCombiner, over);

            ((IDefaultOverride) newOver).setVisualElement(created.left);
            created.left.setElementOverride(newOver);
        }

        Map<Object, Object> properties = source.payload();
        Set<Entry<Object, Object>> es = properties.entrySet();
        VisualElement newElement = created.left;
        for (Entry<Object, Object> e : es) {
            VisualElementProperty p = (VisualElementProperty) e.getKey();

            Object v = e.getValue();
            if (created.left.getProperty(p) == null && shouldCopy(p)) performCopy(source, newElement, p, v);
        }

        return newElement;
    }

    /**
     * TODO: this needs rearchitecting
     */
    public static
    boolean shouldCopy(VisualElementProperty p) {
        //System.out.println(" should copy ? "+p);
        if (p.equals(OutputInsertsOnSheet.outputInsertsOnSheet_knownComponents)) return false;
        if (p.equals(OutputInsertsOnSheet.outputInsertsOnSheet)) return false;

        return true;
    }

    public static
    void performCopy(VisualElement source, VisualElement newElement, VisualElementProperty p, Object v) {
        Ref<Object> r = new Ref<Object>(v);
        r.set(v, source);
        IVisualElementOverrides.MakeDispatchProxy.getBackwardsOverrideProxyFor(newElement)
                                                       .setProperty(newElement, p, r);
        IVisualElementOverrides.MakeDispatchProxy.getOverrideProxyFor(newElement).setProperty(newElement, p, r);
    }

}
