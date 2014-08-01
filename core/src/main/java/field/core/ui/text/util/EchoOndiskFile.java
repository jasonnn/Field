package field.core.ui.text.util;

import field.core.dispatch.IVisualElement;
import field.core.dispatch.VisualElement;
import field.core.dispatch.Rect;
import field.core.dispatch.VisualElementProperty;
import field.core.dispatch.override.DefaultOverride;
import field.core.plugins.python.PythonPluginEditor;
import field.core.windowing.components.DraggableComponent;
import field.graphics.core.BasicGLSLangProgram;
import field.graphics.core.BasicGLSLangProgram.BasicGLSLangElement;
import field.launch.IUpdateable;
import field.util.collect.tuple.Pair;
import field.util.collect.tuple.Triple;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public
class EchoOndiskFile {

    public static final VisualElementProperty<String> fileOnDisk = new VisualElementProperty<String>("contents_v");

    static {
        PythonPluginEditor.knownPythonProperties.put("<html><b>Contents</b> \u2014 <font size=-3>( fileOnDisk<i>_v</i> )</font>",
                                                     fileOnDisk);
    }

    public static
    void echoShaderFiles(IVisualElement root, String prefix, final BasicGLSLangProgram program, int startat) {
        List<BasicGLSLangElement> p = program.getPrograms();
        int n = startat;
        for (BasicGLSLangElement pp : p) {
            File[] f = pp.getFiles();

            //System.out.println(" files for program are <"+Arrays.asList(f)+">");

            if ((f != null) && (f.length != 0)) {
                final EchoOndiskFile[] ee = {null};
                EchoOndiskFile e = new EchoOndiskFile(root, f[0].getAbsolutePath(), n, new IUpdateable() {
                    public
                    void update() {
                        program.reload();
                        IVisualElement.dirty.set(ee[0].created.left, ee[0].created.left, true);
                    }
                });
                ee[0] = e;
                IVisualElement.name.set(e.created.left,
                                        e.created.left,
                                        prefix + '(' + pp.isFragment + ") :" + f[0].getName());
            }
            n++;
        }
    }

    private static
    String readFile(File code) {
        BufferedReader reader = null;
        String s = "";
        try {
            reader = new BufferedReader(new FileReader(code));
            while (reader.ready()) {
                s += reader.readLine() + '\n';
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

    private final Triple<VisualElement, DraggableComponent, DefaultOverride> created;
    private final String filename;

    private final IVisualElement root;

    @SuppressWarnings("unchecked")
    public
    EchoOndiskFile(IVisualElement root, String filename, int n, final IUpdateable onSync) {

        this.root = root;
        this.filename = filename;

        created = VisualElement.createWithToken(filename,
                                                root,
                                                new Rect(30, 100 + (n * 40), 30, 30),
                                                VisualElement.class,
                                                DraggableComponent.class,
                                                DefaultOverride.class);
        VisualElement.name.set(created.left, created.left, filename);
        PythonPluginEditor.python_customToolbar.addToList(ArrayList.class,
                                                          created.left,
                                                          new Pair<String, IUpdateable>("sync from filesystem",
                                                                                        new IUpdateable() {
                                                                                            public
                                                                                            void update() {
                                                                                                syncFromFilesystem();
                                                                                            }
                                                                                        }));

        PythonPluginEditor.python_customToolbar.addToList(ArrayList.class,
                                                          created.left,
                                                          new Pair<String, IUpdateable>("sync to filesystem and update",
                                                                                        new IUpdateable() {
                                                                                            public
                                                                                            void update() {
                                                                                                //System.out.println(" syncing to file system");
                                                                                                syncToFilesystem();
                                                                                                if (onSync != null)
                                                                                                    onSync.update();
                                                                                            }
                                                                                        }));

        syncFromFilesystem();
    }

    protected
    void syncFromFilesystem() {
        String f;
        if (!new File(filename).exists()) {
            f = "(no file)";
        }
        f = readFile(new File(filename));

        fileOnDisk.set(created.left, created.left, f);
    }

    protected
    void syncToFilesystem() {
        String m = fileOnDisk.get(created.left);
        //System.out.println(" syncing to file system");
        //System.out.println(m);
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(new File(filename)));
            writer.append(m);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
