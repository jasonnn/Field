package field.core.dispatch;

import field.core.StandardFluidSheet;
import field.core.dispatch.override.IVisualElementOverrides;
import field.core.execution.BasicRunner;
import field.core.persistance.FluidCopyPastePersistence;
import field.core.plugins.drawing.ToolPalette2;
import field.core.plugins.drawing.opengl.iLinearGraphicsContext;
import field.core.ui.MarkingMenuBuilder;
import field.core.windowing.GLComponentWindow;
import field.core.windowing.components.GlassComponent;
import field.core.windowing.components.RootComponent;
import field.core.windowing.components.SelectionGroup;
import field.core.windowing.components.iComponent;
import field.math.graph.IMutableContainer;
import field.math.linalg.Vector4;

import java.util.*;

public
interface IVisualElement extends IMutableContainer<Map<Object, Object>, IVisualElement> {

    public static final VisualElementProperty<String> name = new VisualElementProperty<String>("name");

    public static final VisualElementProperty<IVisualElementOverrides> overrides =
            new VisualElementProperty<IVisualElementOverrides>("overrides");

    public static final VisualElementProperty<SelectionGroup<iComponent>> selectionGroup =
            new VisualElementProperty<SelectionGroup<iComponent>>("selectionGroup");

    public static final VisualElementProperty<SelectionGroup<iComponent>> markingGroup =
            new VisualElementProperty<SelectionGroup<iComponent>>("markingGroup");

    public static final VisualElementProperty<GLComponentWindow> enclosingFrame =
            new VisualElementProperty<GLComponentWindow>("enclosingFrame");

    public static final VisualElementProperty<RootComponent> rootComponent =
            new VisualElementProperty<RootComponent>("rootComponent");

    public static final VisualElementProperty<GlassComponent> glassComponent =
            new VisualElementProperty<GlassComponent>("glassComponent");

    public static final VisualElementProperty<ToolPalette2> toolPalette2 =
            new VisualElementProperty<ToolPalette2>("toolPalette2");

    public static final VisualElementProperty<StandardFluidSheet> sheetView =
            new VisualElementProperty<StandardFluidSheet>("sheetView");

    public static final VisualElementProperty<iComponent> localView =
            new VisualElementProperty<iComponent>("localView");

    public static final VisualElementProperty<Boolean> dirty = new VisualElementProperty<Boolean>("dirty");

    public static final VisualElementProperty<Boolean> hidden = new VisualElementProperty<Boolean>("hidden");

    public static final VisualElementProperty<Object> creationToken =
            new VisualElementProperty<Object>("creationToken");

    public static final VisualElementProperty<Boolean> doNotSave = new VisualElementProperty<Boolean>("doNotSave");
    public static final VisualElementProperty<IVisualElement> timeSlider =
            new VisualElementProperty<IVisualElement>("timeSlider");

    public static final VisualElementProperty<FluidCopyPastePersistence> copyPaste =
            new VisualElementProperty<FluidCopyPastePersistence>("copyPaste");

    public static final VisualElementProperty<Boolean> hasFocusLock =
            new VisualElementProperty<Boolean>("hasFocusLock_");

    public static final VisualElementProperty<iLinearGraphicsContext> fastContext =
            new VisualElementProperty<iLinearGraphicsContext>("fastContext");

    public static final VisualElementProperty<MarkingMenuBuilder> spaceMenu =
            new VisualElementProperty<MarkingMenuBuilder>("spaceMenu_");

    public static final VisualElementProperty<Number> isRenderer = new VisualElementProperty<Number>("isRenderer");
    public static final VisualElementProperty<BasicRunner> multithreadedRunner =
            new VisualElementProperty<BasicRunner>("multithreadedRunner");


    public static final VisualElementProperty<Vector4> color1 = new VisualElementProperty<Vector4>("color1");
    public static final VisualElementProperty<Vector4> color2 = new VisualElementProperty<Vector4>("color2");
    public static final VisualElementProperty<Object> visibleInPreview =
            new VisualElementProperty<Object>("visibleInPreview");

    public static final VisualElementProperty<String> boundTo = new VisualElementProperty<String>("boundTo");

    public
    <T> void deleteProperty(VisualElementProperty<T> p);

    public
    void dispose();

    public
    Rect getFrame(Rect out);

    public
    <T> T getProperty(VisualElementProperty<T> p);

    public
    String getUniqueID();

    public
    void setFrame(Rect out);

    public
    <T> IVisualElement setProperty(VisualElementProperty<T> p, T to);

    public
    void setUniqueID(String uid);

}
