package tst;
import field.bytecode.protect.annotations.GenerateMethods;
import field.bytecode.protect.annotations.Mirror;

import java.util.Collection;
import java.util.List;

@GenerateMethods
public
interface ILine {

    @Mirror
    public
    void moveTo(float x, float y);

    @Mirror
    public
    void lineTo(float x, float y);

    @Mirror
    public
    void cubicTo(float cx1, float cy1, float cx2, float cy2, float x, float y);

    @Mirror
    public
    <T> void setPointAttribute(List<T> p, T t);

    @Mirror
    public <T extends Collection<T>> void something(T t);

    @Mirror
    public
    void close();
}
