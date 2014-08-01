package field.core;

import field.bytecode.protect.Woven;
import field.bytecode.protect.annotations.NextUpdate;
import field.core.dispatch.IVisualElement;
import field.core.dispatch.Rect;
import field.core.persistance.FluidCopyPastePersistence;
import field.core.windowing.GLComponentWindow;
import field.core.windowing.components.MainSelectionGroup;
import field.core.windowing.components.iComponent;
import field.launch.Launcher;
import field.math.linalg.Vector2;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;

import java.io.*;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import static java.lang.System.out;

@Woven
public
class DragDuplicator {

    private final MainSelectionGroup group;
    private final IVisualElement root;

    public
    DragDuplicator(MainSelectionGroup group, IVisualElement root) {
        this.group = group;
        this.root = root;
    }

    boolean isDragging = false;
    private HashSet<IVisualElement> ongoing;

    Vector2 at = new Vector2();

    @NextUpdate
    public
    void begin(Event event) {


        Set<iComponent> c = group.getSelection();
        Set<IVisualElement> v = new LinkedHashSet<IVisualElement>();
        for (iComponent cc : c) {
            IVisualElement vv = cc.getVisualElement();
            if (vv != null) v.add(vv);
        }

        isDragging = !v.isEmpty();

        //System.out.println(" begin drag <"+isDragging+">");

        if (isDragging) {
            GLComponentWindow.getCurrentWindow(null)
                             .getCanvas()
                             .setCursor(Launcher.display.getSystemCursor(SWT.CURSOR_HAND));


            FluidCopyPastePersistence copier = IVisualElement.copyPaste.get(root);

            StringWriter temp = new StringWriter();
            HashSet<IVisualElement> savedOut = new HashSet<IVisualElement>();
            ObjectOutputStream oos = copier.getObjectOutputStream(temp, savedOut, v);
            try {
                oos.writeObject(v);
                oos.close();

                HashSet<IVisualElement> all = new HashSet<IVisualElement>(StandardFluidSheet.allVisualElements(root));

                ongoing = new HashSet<IVisualElement>();
                ObjectInputStream ois =
                        copier.getObjectInputStream(new StringReader(temp.getBuffer().toString()), ongoing, all);
                Object in = ois.readObject();

                ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        at.x = event.x;
        at.y = event.y;

        out.println(" output <" + ongoing + '>');
    }

    public
    void drag(Event event) {

        if (!isDragging) return;

        float deltaX = event.x - at.x;
        float deltaY = event.y - at.y;

        out.println(" delta <" + deltaX + ", " + deltaY + "> ongoing <" + ongoing + '>');

        at.x = event.x;
        at.y = event.y;

        for (IVisualElement v : ongoing) {
            Rect f = v.getFrame(null);
            f.x += deltaX;
            f.y += deltaY;
            v.setFrame(f);
        }
    }

    public
    void end(Event event) {

        //TODO: 64 \u2014 confront mouse cursor setting in pure java
        //NSCursor.arrowCursor().set();
        //GLComponentWindow.getCurrentWindow(null).getCanvas().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        GLComponentWindow.getCurrentWindow(null)
                         .getCanvas()
                         .setCursor(Launcher.display.getSystemCursor(SWT.CURSOR_ARROW));

        if (!isDragging) return;

        isDragging = false;
        ongoing.clear();

    }

}
