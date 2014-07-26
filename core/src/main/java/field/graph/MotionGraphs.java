package field.graph;

import field.context.Context.Cobj;
import field.core.plugins.drawing.opengl.CachedLine;
import field.util.Dict.Prop;

import java.util.Collection;

public
class MotionGraphs {

    public static final Prop<iAxis> x_axis = new Prop<iAxis>("x_axis");
    public static final Prop<iAxis> y_axis = new Prop<iAxis>("y_axis");

    public static final Prop<iLayout> mark = new Prop<iLayout>("mark");

    public static final Prop<Collection<Cobj>> dataSet = new Prop<Collection<Cobj>>("dataSet");

    public static final Prop<Cobj> datum = new Prop<Cobj>("datum");

    public static final Prop<iLayout> markMaker = new Prop<iLayout>("markMaker");

    public static final Prop<Collection<CachedLine>> geometry = new Prop<Collection<CachedLine>>("geometry");

    public static
    interface iLayout {
        public
        void begin();

        public
        void layout();

        public
        void end();
    }

    public static
    interface iDraw {
        public
        void draw();
    }

    public static
    interface iAxis {
        public
        float map(Cobj x);

        public
        float mapNumber(float n);
    }

    public static
    class Graph extends Cobj {

    }


}
