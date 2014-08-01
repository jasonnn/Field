package field.core.dispatch;

import field.math.linalg.Vector2;
import field.math.linalg.Vector3;
import field.util.collect.tuple.Pair;

import java.io.Serializable;

/**
* Created by jason on 7/31/14.
*/
public
class Rect implements Serializable {
    public static
    Rect slowUnion(Rect r, Rect rect, Vector3 cameraPosition) {
        return union(r, rect);
    }

    public
    Rect() {
        this(0, 0, 0, 0);
    }

    public
    Rect(Rect r) {
        this(0, 0, 0, 0);
        setValue(r);
    }

    public static
    Rect union(Rect r, Rect rect) {
        if (r == null) return new Rect(0, 0, 0, 0).setValue(rect);
        if (rect == null) return new Rect(0, 0, 0, 0).setValue(r);
        return r.union(rect);
    }

    public double x;

    public double y;

    public double w;

    public double h;

    public
    Rect(double x2, double y2, double w2, double h2) {
        this.x = x2;
        this.y = y2;
        this.w = w2;
        this.h = h2;
    }

    public
    Rect(float x2, float y2, float w2, float h2) {
        this.x = x2;
        this.y = y2;
        this.w = w2;
        this.h = h2;
    }

    public
    Rect(Vector2 a, Vector2 b) {
        this.x = Math.min(a.x, b.x);
        this.y = Math.min(a.y, b.y);
        this.w = Math.max(a.x, b.x) - this.x;
        this.h = Math.max(a.y, b.y) - this.y;
    }

    public
    Rect alignLeftTo(Rect here) {
        this.x = here.x;
        return this;
    }

    public
    Rect alignTopTo(Rect here) {
        this.y = here.y;
        return this;
    }

    public
    float area() {
        return (float) (w * h);
    }

    public
    Rect blendTowards(float d, Rect a) {
        return new Rect(x * (1 - d) + d * a.x, y * (1 - d) + d * a.y, w * (1 - d) + d * a.w, h * (1 - d) + d * a.h);
    }

    public
    Vector3 bottomLeft() {
        return new Vector3(x, y + h, 0);
    }

    public
    Vector3 bottomRight() {
        return new Vector3(x + w, y + h, 0);
    }

    public
    Rect convertFromNDC(Rect input) {
        return new Rect(input.x + x * input.w, input.y + y * input.h, input.w * w, input.h * h);
    }

    public
    Vector3 convertFromNDC(Vector3 v2) {
        return new Vector3(v2.x * this.w + this.x, v2.y * this.h + this.y, 0);
    }

    public
    Vector2 convertFromNDC(Vector2 v2) {
        return new Vector2(v2.x * this.w + this.x, v2.y * this.h + this.y);
    }

    public
    Vector3 convertToNDC(Vector3 v2) {
        return new Vector3((v2.x - this.x) / this.w, (v2.y - this.y) / this.h, 0);
    }

    public
    float distanceFrom(Rect currentRect) {
        return (float) (Math.abs(x - currentRect.x)
                        + Math.abs(y - currentRect.y)
                        + Math.abs(w - currentRect.w)
                        + Math.abs(h - currentRect.h));
    }

    @Override
    public
    int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(h);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(w);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(x);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public
    boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Rect other = (Rect) obj;
        if (Double.doubleToLongBits(h) != Double.doubleToLongBits(other.h)) return false;
        if (Double.doubleToLongBits(w) != Double.doubleToLongBits(other.w)) return false;
        if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x)) return false;
        if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y)) return false;
        return true;
    }

    public
    Vector3[] fourCorners() {
        return new Vector3[]{topLeft(), topRight(), bottomRight(), bottomLeft()};
    }

    public
    Rect includePoint(float cx, float cy) {
        double minx = Math.min(cx, x);
        double miny = Math.min(cy, y);

        double maxx = Math.max(cx, x + w);
        double maxy = Math.max(cy, y + h);

        return new Rect(minx, miny, maxx - minx, maxy - miny);

    }

    public
    Rect inset(float f) {
        x = x + w * f;
        y = y + h * f;
        w = w - 2 * w * f;
        h = h - 2 * h * f;
        return this;
    }

    public
    Rect insetAbsolute(float f) {
        x = x + f;
        y = y + f;
        w = w - 2 * f;
        h = h - 2 * f;
        return this;
    }

    public
    void insetByMin(float f) {
        double am = Math.min(w, h) * f;
        x = x + am;
        y = y + am;
        w = w - 2 * am;
        h = h - 2 * am;
    }

    public
    Rect intersect(Rect r) {
        double minx = Math.max(r.x, x);
        double miny = Math.max(r.y, y);

        double maxx = Math.min(r.x + r.w, x + w);
        double maxy = Math.min(r.y + r.h, y + h);

        return new Rect(minx, miny, Math.max(0, maxx - minx), Math.max(0, maxy - miny));
    }

    public
    boolean isInside(Vector2 v2) {
        if (v2.x >= x && v2.y >= y && v2.x < x + w && v2.y < y + h) {
            return true;
        }
        else return false;
    }

    public
    Vector3 midPoint() {
        return new Vector3(x + w / 2, y + h / 2, 0);
    }

    public
    Vector2 midpoint2() {
        return new Vector2(x + w / 2, y + h / 2);
    }

    public
    Vector3 midPointLeftEdge() {
        return new Vector3(x, y + h / 2, 0);
    }

    public
    Vector3 midPointRightEdge() {
        return new Vector3(x + w, y + h / 2, 0);
    }

    public
    Vector2 midPointTopEdge() {
        return new Vector2(x + w / 2, y + h);
    }

    public
    Rect moveToInclude(Vector2 v) {
        if (v.x > x + w) {
            x = v.x - w;
        }
        else if (v.x < x) {
            x = v.x;
        }
        if (v.y > y + h) {
            y = v.y - h;
        }
        else if (v.y < y) {
            y = v.y;
        }
        return this;
    }

    public
    boolean overlaps(Rect rect) {

        if (Math.min(rect.x, rect.x + rect.w) <= Math.max(x, x + w)
            && Math.max(rect.x, rect.x + rect.w) >= Math.min(x, x + w)
            && Math.min(rect.y, rect.y + rect.h) < Math.max(y, y + h)
            && Math.max(rect.y, rect.y + rect.h) >= Math.min(y, y + h)) return true;

        // if (rect.x <= x + w && (rect.x + rect.w) >= x &&
        // rect.y <= y + h && (rect.y + rect.h) >= y) return
        // true;
        return false;
    }

    public
    Vector3 relativize(Vector3 v) {
        return new Vector3(v.x * w + x, v.y * h + y, 0);
    }

    public
    Rect setSize(float w, float h) {
        this.w = w;
        this.h = h;
        return this;
    }

    public
    Rect setValue(Rect newFrame) {
        this.x = newFrame.x;
        this.y = newFrame.y;
        this.w = newFrame.w;
        this.h = newFrame.h;
        return this;
    }

    public
    float size() {
        return (float) (w * w + h * h);
    }

    public
    Vector3 topLeft() {
        return new Vector3(x, y, 0);
    }

    public
    Vector3 topRight() {
        return new Vector3(x + w, y, 0);
    }

    @Override
    public
    String toString() {
        return "r:" + x + ' ' + y + ' ' + w + ' ' + h;
    }

    public
    Rect union(Rect r) {
        if (r == null) return new Rect(x, y, w, h);

        double minx = Math.min(r.x, x);
        double miny = Math.min(r.y, y);

        double maxx = Math.max(r.x + r.w, x + w);
        double maxy = Math.max(r.y + r.h, y + h);

        return new Rect(minx, miny, maxx - minx, maxy - miny);
    }

    public
    Pair<Vector2, Vector2> connectOver(Rect other) {
        return connectOver(other,
                           new Vector2(0.5, 0),
                           new Vector2(0, 0.5),
                           new Vector2(1, 0.5),
                           new Vector2(0.5, 1));
    }

    public
    Pair<Vector2, Vector2> connectOver(Rect other, Vector2... options) {
        float best = Float.POSITIVE_INFINITY;
        Pair<Vector2, Vector2> bestIs = null;
        for (int i = 0; i < options.length; i++) {
            Vector3 a = this.convertFromNDC(options[i].toVector3());
            for (int j = 0; j < options.length; j++) {
                Vector3 b = other.convertFromNDC(options[j].toVector3());
                float d = a.distanceFrom(b);
                if (d < best) {
                    best = d;
                    bestIs = new Pair<Vector2, Vector2>(a.toVector2(), b.toVector2());
                }
            }
        }
        return bestIs;
    }

}
