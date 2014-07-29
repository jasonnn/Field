package field.core.plugins.drawing.opengl;

//import java.awt.BasicStroke;
//import java.awt.Font;

import field.core.dispatch.IVisualElement;
import field.core.plugins.drawing.opengl.CachedLine.Event;
import field.core.ui.MarkingMenuBuilder;
import field.core.ui.text.protect.ClassDocumentationProtect.Divider;
import field.graphics.ci.CoreImageCanvasUtils;
import field.math.linalg.CoordinateFrame;
import field.math.linalg.Vector2;
import field.math.linalg.Vector3;
import field.math.linalg.Vector4;
import field.namespace.generic.IFunction;
import field.util.Dict;
import field.util.Dict.Prop;

import java.awt.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract
class iLinearGraphicsContext {

    /**
     * should the line be stroked ? true / false
     */
    public static final Prop<Boolean> stroked = new Prop<Boolean>("stroked");
    public static final Prop<Boolean> filled = new Prop<Boolean>("filled");
    public static final Prop<Boolean> pointed = new Prop<Boolean>("pointed");

    public static final Divider divider_0 = new Divider();

    public static final Prop<Float> thickness = new Prop<Float>("thickness");

    public static final Divider divider_1 = new Divider();

    public static final Prop<Vector4> color = new Prop<Vector4>("color");
    public static final Prop<Vector4> strokeColor = new Prop<Vector4>("strokeColor");
    public static final Prop<Vector4> pointColor = new Prop<Vector4>("pointColor");
    public static final Prop<Vector4> fillColor = new Prop<Vector4>("fillColor");

    public static final Divider divider_2 = new Divider();

    public static final Prop<Number> totalOpacity = new Prop<Number>("totalOpacity");

    public static final Divider divider_3 = new Divider();

    public static final Prop<BasicStroke> strokeType = new Prop<BasicStroke>("strokeType");
    public static final Prop<Number> windingRule = new Prop<Number>("Number");

    public static final Divider divider_4 = new Divider();

    public static final Prop<Float> pointSize = new Prop<Float>("pointSize");
    public static final Prop<Float> pointSize_v = new Prop<Float>("pointSize_v");
    public static final Prop<Vector4> pointColor_v = new Prop<Vector4>("pointColor_v");

    public static final Divider divider_5 = new Divider();

    // this is nominally a float, but can also be a Vector3 for control
    // points (c1.z, c2.z, b.z)
    public static final Prop<Object> z_v = new Prop<Object>("z_v");
    public static final Prop<Float> containsDepth = new Prop<Float>("containsDepth");
    public static final Prop<CoordinateFrame> fastTransform = new Prop<CoordinateFrame>("_transform");
    public static final Prop<IFunction<Vector3, Vector3>> camera = new Prop<IFunction<Vector3, Vector3>>("camera");

    public static final Prop<Vector4> needVertexShading = new Prop<Vector4>("needVertexShading");
    public static final Prop<Vector4> strokeColor_v = new Prop<Vector4>("strokeColor_v");
    public static final Prop<Vector4> fillColor_v = new Prop<Vector4>("fillColor_v");

    public static final Prop<Boolean> starConvex = new Prop<Boolean>("starConvex");

    public static final Divider divider_7 = new Divider();

    public static final Prop<Vector4> thicknessProperties = new Prop<Vector4>("thicknessProperties");

    public static final Prop<Float> strokeThicknessMul = new Prop<Float>("strokeThicknessMul");
    public static final Prop<Float> geometricScale = new Prop<Float>("geometricScale");
    public static final Prop<Float> flatnessScale = new Prop<Float>("flatnessScale");

    public static final Divider divider_9 = new Divider();

    public static final Prop<Float> derived = new Prop<Float>("derived");
    public static final Prop<Float> hiddenControls = new Prop<Float>("hiddenControls");
    public static final Prop<Float> ignoreInPreview = new Prop<Float>("ignoreInPreview");
    public static final Prop<Boolean> soloCache = new Prop<Boolean>("soloCache");
    public static final Prop<Boolean> lateRendering = new Prop<Boolean>("lateRendering");
    public static final Prop<String> outputOpacityType = new Prop<String>("outputOpacityType");

    public static final Divider divider_10 = new Divider();

    public static final Prop<Boolean> notForExport = new Prop<Boolean>("notForExport");
    public static final Prop<String> layer = new Prop<String>("layer");
    public static final Prop<Vector4> saturationColor = new Prop<Vector4>("saturationColor");
    public static final Prop<Boolean> noTransform = new Prop<Boolean>("noTransform");

    public static final Prop<Boolean> shouldHighlight = new Prop<Boolean>("shouldHighlight");
    public static final Prop<Boolean> shouldCache = new Prop<Boolean>("shouldCache");

    public static final Divider divider_11 = new Divider();
    /* warning: the pdf and svg contexts don't support text right now */
    public static final Prop<Boolean> containsText = new Prop<Boolean>("containsText");
    public static final Prop<Boolean> containsMultilineText = new Prop<Boolean>("containsMultilineText");
    public static final Prop<String> text_v = new Prop<String>("text_v");
    public static final Prop<Number> textScale_v = new Prop<Number>("textScale_v");
    public static final Prop<Number> textRotation_v = new Prop<Number>("textRotation_v");
    public static final Prop<Vector2> textOffset_v = new Prop<Vector2>("textOffset_v");
    public static final Prop<String> infoText_v = new Prop<String>("infoText_v");
    public static final Prop<List<CachedLine>> infoAnnotation_v = new Prop<List<CachedLine>>("infoAnnotation_v");
    public static final Prop<MarkingMenuBuilder> infoRightClick_v = new Prop<MarkingMenuBuilder>("infoRightClick_v");
    public static final Prop<MarkingMenuBuilder> infoDoubleClick_v = new Prop<MarkingMenuBuilder>("infoDoubleClick_v");

    public static final Prop<Number> noTweak_v = new Prop<Number>("noTweak_v");
    public static final Prop<java.awt.Font> font_v = new Prop<java.awt.Font>("font_v");
    public static final Prop<Number> multilineWidth_v = new Prop<Number>("multilineWidth_v");
    public static final Prop<Boolean> textIsBlured_v = new Prop<Boolean>("textIsBlured_v");
    // -1 is rightAligned, 0 is centered, and 1 is left aligned
    public static final Prop<Float> alignment_v = new Prop<Float>("alignment_v");

    public static final Prop<Boolean> noHit = new Prop<Boolean>("noHit");

    public static final Prop<Integer> defaultMoveLock = new Prop<Integer>("defaultMoveLock");

    public static final Divider divider_12 = new Divider();
    public static final Prop<Boolean> isText_info = new Prop<Boolean>("isText_info");
    public static final Prop<Boolean> isArrow_info = new Prop<Boolean>("isArrow_info");

    public static final Divider divider_13 = new Divider();
    public static final Prop<IVisualElement> source = new Prop<IVisualElement>("source");

    public static final Divider divider_14 = new Divider();
    public static final Prop<Boolean> containsCode = new Prop<Boolean>("containsCode");
    public static final Prop<Collection<CachedLine>> codeDependsTo = new Prop<Collection<CachedLine>>("codeDependsTo");
    public static final Prop<Object> code_v = new Prop<Object>("code_v");
    public static final Prop<Object> name_v = new Prop<Object>("name_v");
    public static final Prop<Object> name = new Prop<Object>("name");
    public static final Prop<Object> internalName = new Prop<Object>("id");

    public static final Divider divider_15 = new Divider();
    public static final Prop<Vector3> paperColor = new Prop<Vector3>("paperColor");

    public static final Divider divider_16 = new Divider();
    public static final Prop<Boolean> containsImages = new Prop<Boolean>("containsImages");
    public static final Prop<CoreImageCanvasUtils.Image> image_v = new Prop<CoreImageCanvasUtils.Image>("image_v");
    public static final Prop<Number> imageDrawScale_v = new Prop<Number>("imageDrawScale_v");

    public static final Divider divider_17 = new Divider();
    public static final Prop<iLinearGraphicsContext> context = new Prop<iLinearGraphicsContext>("context");

    public static final Prop<Vector2> offsetFromSource = new Prop<Vector2>("offsetFromSource");
    public static final Prop<Vector2> offsetFromSource_v = new Prop<Vector2>("offsetFromSource_v");
    public static Prop<Boolean> usesAdjacency = new Prop<Boolean>("usesAdjacency");
    public static Prop<? extends Number> constantDistanceResampling = new Prop<Number>("constantDistanceResampling");
    public static Prop<Map<String, Number>> shaderAttributes = new Prop<Map<String, Number>>("shaderAttributes");
    public static Prop<Boolean> slow = new Prop<Boolean>("slow");

    public static Prop<Boolean> onSourceSelectedOnly = new Prop<Boolean>("onSourceSelectedOnly");
    public static Prop<CachedLine> offsetedLine = new Prop<CachedLine>("__offsetedLine");

    public static Prop<Integer> forceNew = new Prop<Integer>("forceNew");
    protected static Prop<CoordinateFrame> transform = new Prop<CoordinateFrame>("transform");
    public static Prop<Number> bleedsOntoTextEditor = new Prop<Number>("bleedsOntoTextEditor");

    public abstract
    Dict getGlobalProperties();

    public abstract
    void resubmitLine(CachedLine line, Dict properties);

    public abstract
    void submitLine(CachedLine line, Dict properties);

    public static
    String getClassDocumentation(String right, Object on) {
        return "class documentation";
    }

    public
    interface iTransformingContext<T> {
        public
        void convertDrawingSpaceToIntermediate(Vector2 drawing, T intermediate);

        public
        boolean convertIntermediateSpaceToDrawingSpace(T intermediate, Vector2 drawing);

        public
        boolean shouldClip(T intermediate);

        public
        T getIntermediateSpaceForEvent(CachedLine line, CachedLine.Event event, int index);

        public
        void setIntermediateSpaceForEvent(CachedLine onLine,
                                          Event vertex,
                                          int index,
                                          T currentIntermediate,
                                          Vector2 currentDrawing,
                                          Vector2 targetDrawing);

        public
        Object getTransformState();

        public
        void pushTransformState(Object state);

        public
        void popTransformState();
    }

}
