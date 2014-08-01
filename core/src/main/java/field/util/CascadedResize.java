package field.util;

import field.core.dispatch.IVisualElement;
import field.core.dispatch.Rect;

import java.util.List;

public
class CascadedResize {

    private final List<IVisualElement> e;

    public
    CascadedResize(List<IVisualElement> e) {
        this.e = e;
    }

    public
    void cascadedResize(IVisualElement on, Rect from, Rect to, int capture) {
        // TODO: other directions and some fuzz on this
        if (from.x != to.x) return;
        if (from.y != to.y) return;
        if (from.w != to.w) return;

        for (IVisualElement ee : e) {
            if (ee == on) continue;

            Rect t = ee.getFrame(null);

            if (t.y > from.y + from.h && t.y < from.y + from.h + capture) {
                float d = (float) (to.h - from.h);
                Rect tt = new Rect(t);
                tt.y += d;

                resize(ee, t, tt, capture);
            }

        }
    }

    private
    void resize(IVisualElement ee, Rect to, Rect tt, int capture) {
        ee.getProperty(IVisualElement.overrides).shouldChangeFrame(ee, tt, to, true);
        cascadedResize(ee, to, tt, capture);
    }

}
