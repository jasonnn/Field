package field.core.plugins.drawing;

import field.core.Constants;
import field.core.plugins.selection.ToolBarFolder;
import field.launch.IUpdateable;
import field.launch.Launcher;
import field.util.AutoPersist;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolItem;

import java.util.ArrayList;
import java.util.List;

public
class ToolPalette2 {

    public
    interface iTool {
        public
        void begin();

        public
        void end();

        public
        Image getIcon();

        public
        Image getSelectedIcon();

        public
        String getToolTip();

        public
        String getName();

        public
        String getDescription();
    }

    public static Rectangle defaultRect = AutoPersist.persist("modalMousePosition", new Rectangle(700, 150, 300, 200));

    private final ToolBarFolder toolBarPaletteInspector;

    public
    ToolPalette2() {

        toolBarPaletteInspector = new ToolBarFolder(defaultRect);
        toolBarPaletteInspector.getContainer().setBackground(ToolBarFolder.firstLineBackground);

        if (toolBarPaletteInspector.getShell() != null) {
            toolBarPaletteInspector.getShell().setText("Mouse tools");
            toolBarPaletteInspector.getShell().open();
            //System.out.println(" tool palette 2 is open and on screen? <" + toolBarPaletteInspector.getShell().getBounds() + ">");
        }

    }

    List<iTool> tools = new ArrayList<iTool>();

    iTool current = null;

    public
    ToolItem addTool(final iTool tool) {

        tools.add(tool);

        Label description = new Label(toolBarPaletteInspector.getContainer(), SWT.WRAP);
        description.setFont(new Font(Launcher.display, Constants.defaultFont, 10, 0));

//		description.setText(tool.getDescription());


        if (toolBarPaletteInspector.getShell() != null) toolBarPaletteInspector.getShell().setMinimumSize(300, 200);

        ToolItem a = toolBarPaletteInspector.add(tool.getIcon(), description, new IUpdateable() {

            @Override
            public
            void update() {
                if (current != null) current.end();
                tool.begin();
                current = tool;

                // toolBarPaletteInspector.getShell().pack();

            }
        });

        toolBarPaletteInspector.select(0);
        return a;
    }

    public
    void setTool(int i) {
        toolBarPaletteInspector.select(i);

        // toolBarPaletteInspector.moveOut(toolBarPaletteInspector.current);
        // toolBarPaletteInspector.moveIn(i);
        // toolBarPaletteInspector.group.setSelected(toolBarPaletteInspector.toolbarButtons.get(i).getModel(),
        // true);

    }

    public
    int getTool() {
        return toolBarPaletteInspector.get();
    }
}
