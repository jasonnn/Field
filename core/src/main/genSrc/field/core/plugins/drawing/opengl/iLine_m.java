package field.core.plugins.drawing.opengl;

import field.bytecode.mirror.impl.*;
import field.launch.IUpdateable;
import field.math.abstraction.IAcceptor;
import field.math.abstraction.IProvider;
import field.namespace.generic.IFunction;
import field.namespace.generic.ReflectionTools;
import field.util.Dict.Prop;
import java.lang.reflect.Method;
public class iLine_m {
  public static final Method moveTo_m = ReflectionTools.methodOf("moveTo",field.core.plugins.drawing.opengl.iLine.class, float.class, float.class);
  public static final Method lineTo_m = ReflectionTools.methodOf("lineTo",field.core.plugins.drawing.opengl.iLine.class, float.class, float.class);
  public static final Method cubicTo_m = ReflectionTools.methodOf("cubicTo",field.core.plugins.drawing.opengl.iLine.class, float.class, float.class, float.class, float.class, float.class, float.class);
  public static final Method setPointAttribute_m = ReflectionTools.methodOf("setPointAttribute",field.core.plugins.drawing.opengl.iLine.class, field.util.Dict.Prop.class, java.lang.Object.class);
  public static final Method close_m = ReflectionTools.methodOf("close",field.core.plugins.drawing.opengl.iLine.class);
  // --------------------------------------------------------------------------------
  public static final MirrorMethod<iLine,Object[],Void> moveTo_s = new MirrorMethod<field.core.plugins.drawing.opengl.iLine,Object[],java.lang.Void>(moveTo_m);
  public static final MirrorMethod<iLine,Object[],Void> lineTo_s = new MirrorMethod<field.core.plugins.drawing.opengl.iLine,Object[],java.lang.Void>(lineTo_m);
  public static final MirrorMethod<iLine,Object[],Void> cubicTo_s = new MirrorMethod<field.core.plugins.drawing.opengl.iLine,Object[],java.lang.Void>(cubicTo_m);
  public static final MirrorMethod<iLine,Object[],Void> setPointAttribute_s = new MirrorMethod<field.core.plugins.drawing.opengl.iLine,Object[],java.lang.Void>(setPointAttribute_m);
  public static final MirrorMethod<iLine,Void,Void> close_s = new MirrorMethod<field.core.plugins.drawing.opengl.iLine,Void,java.lang.Void>(close_m);
  // --------------------------------------------------------------------------------
  public static interface moveTo_interface
      extends IAcceptor<Object[]>, IFunction<Object[],Void> {
    Void moveTo(final float x, final float y);
    IUpdateable updateable(final float x, final float y);
    IProvider<Void> bind(final float x, final float y);
  }
  static class moveTo_impl
      implements moveTo_interface {
    final iLine x;
    final IAcceptor<Object[]> a;
    final IFunction<Object[],Void> f;
    moveTo_impl(iLine x) {
      this.x=x;
      this.a=moveTo_s.acceptor(x);
      this.f=moveTo_s.function(x);
    }
    @Override
    public Void moveTo(final float x, final float y) {
       this.x.moveTo(x,y);
      return null;
    }
    @Override
    public IUpdateable updateable(final float x, final float y) {
      return new IUpdateable(){
             public void update(){
                moveTo(x,y);
             }
          };
    }
    @Override
    public IProvider<Void> bind(final float x, final float y) {
      return new IProvider<java.lang.Void>(){
              public java.lang.Void get(){
                  moveTo(x,y);
          return null;
                  }
          };
    }
    @Override
    public Void apply(Object[] p) {
      return f.apply(p);
    }
    @Override
    public IAcceptor<Object[]> set(Object[] p) {
      this.a.set(p);
      return this;
    }
  }
  public static interface lineTo_interface
      extends IAcceptor<Object[]>, IFunction<Object[],Void> {
    Void lineTo(final float x, final float y);
    IUpdateable updateable(final float x, final float y);
    IProvider<Void> bind(final float x, final float y);
  }
  static class lineTo_impl
      implements lineTo_interface {
    final iLine x;
    final IAcceptor<Object[]> a;
    final IFunction<Object[],Void> f;
    lineTo_impl(iLine x) {
      this.x=x;
      this.a=lineTo_s.acceptor(x);
      this.f=lineTo_s.function(x);
    }
    @Override
    public Void lineTo(final float x, final float y) {
       this.x.lineTo(x,y);
      return null;
    }
    @Override
    public IUpdateable updateable(final float x, final float y) {
      return new IUpdateable(){
             public void update(){
                lineTo(x,y);
             }
          };
    }
    @Override
    public IProvider<Void> bind(final float x, final float y) {
      return new IProvider<java.lang.Void>(){
              public java.lang.Void get(){
                  lineTo(x,y);
          return null;
                  }
          };
    }
    @Override
    public Void apply(Object[] p) {
      return f.apply(p);
    }
    @Override
    public IAcceptor<Object[]> set(Object[] p) {
      this.a.set(p);
      return this;
    }
  }
  public static interface cubicTo_interface
      extends IAcceptor<Object[]>, IFunction<Object[],Void> {
    Void cubicTo(final float cx1, final float cy1, final float cx2, final float cy2, final float x, final float y);
    IUpdateable updateable(final float cx1, final float cy1, final float cx2, final float cy2, final float x, final float y);
    IProvider<Void> bind(final float cx1, final float cy1, final float cx2, final float cy2, final float x, final float y);
  }
  static class cubicTo_impl
      implements cubicTo_interface {
    final iLine x;
    final IAcceptor<Object[]> a;
    final IFunction<Object[],Void> f;
    cubicTo_impl(iLine x) {
      this.x=x;
      this.a=cubicTo_s.acceptor(x);
      this.f=cubicTo_s.function(x);
    }
    @Override
    public Void cubicTo(final float cx1, final float cy1, final float cx2, final float cy2, final float x, final float y) {
       this.x.cubicTo(cx1,cy1,cx2,cy2,x,y);
      return null;
    }
    @Override
    public IUpdateable updateable(final float cx1, final float cy1, final float cx2, final float cy2, final float x, final float y) {
      return new IUpdateable(){
             public void update(){
                cubicTo(cx1,cy1,cx2,cy2,x,y);
             }
          };
    }
    @Override
    public IProvider<Void> bind(final float cx1, final float cy1, final float cx2, final float cy2, final float x, final float y) {
      return new IProvider<java.lang.Void>(){
              public java.lang.Void get(){
                  cubicTo(cx1,cy1,cx2,cy2,x,y);
          return null;
                  }
          };
    }
    @Override
    public Void apply(Object[] p) {
      return f.apply(p);
    }
    @Override
    public IAcceptor<Object[]> set(Object[] p) {
      this.a.set(p);
      return this;
    }
  }
  public static interface setPointAttribute_interface
      extends IAcceptor<Object[]>, IFunction<Object[],Void> {
    Void setPointAttribute(final Prop p, final Object t);
    IUpdateable updateable(final Prop p, final Object t);
    IProvider<Void> bind(final Prop p, final Object t);
  }
  static class setPointAttribute_impl
      implements setPointAttribute_interface {
    final iLine x;
    final IAcceptor<Object[]> a;
    final IFunction<Object[],Void> f;
    setPointAttribute_impl(iLine x) {
      this.x=x;
      this.a=setPointAttribute_s.acceptor(x);
      this.f=setPointAttribute_s.function(x);
    }
    @Override
    public Void setPointAttribute(final Prop p, final Object t) {
       this.x.setPointAttribute(p,t);
      return null;
    }
    @Override
    public IUpdateable updateable(final Prop p, final Object t) {
      return new IUpdateable(){
             public void update(){
                setPointAttribute(p,t);
             }
          };
    }
    @Override
    public IProvider<Void> bind(final Prop p, final Object t) {
      return new IProvider<java.lang.Void>(){
              public java.lang.Void get(){
                  setPointAttribute(p,t);
          return null;
                  }
          };
    }
    @Override
    public Void apply(Object[] p) {
      return f.apply(p);
    }
    @Override
    public IAcceptor<Object[]> set(Object[] p) {
      this.a.set(p);
      return this;
    }
  }
  public static interface close_interface
      extends IAcceptor<Void>, IFunction<Void,Void>, IUpdateable {
    Void close();
    IUpdateable updateable();
    IProvider<Void> bind();
  }
  static class close_impl
      implements close_interface {
    final iLine x;
    final IAcceptor<Void> a;
    final IFunction<Void,Void> f;
    close_impl(iLine x) {
      this.x=x;
      this.a=close_s.acceptor(x);
      this.f=close_s.function(x);
    }
    @Override
    public Void close() {
       this.x.close();
      return null;
    }
    @Override
    public void update() {
      updateable().update();
    }
    @Override
    public IUpdateable updateable() {
      return new IUpdateable(){
             public void update(){
                close();
             }
          };
    }
    @Override
    public IProvider<Void> bind() {
      return new IProvider<java.lang.Void>(){
              public java.lang.Void get(){
                  close();
          return null;
                  }
          };
    }
    @Override
    public Void apply(Void p) {
      return f.apply(p);
    }
    @Override
    public IAcceptor<Void> set(Void p) {
      this.a.set(p);
      return this;
    }
  }
  // --------------------------------------------------------------------------------
  public final moveTo_interface moveTo;
  public final lineTo_interface lineTo;
  public final cubicTo_interface cubicTo;
  public final setPointAttribute_interface setPointAttribute;
  public final close_interface close;
  // --------------------------------------------------------------------------------

  public iLine_m(iLine x) {
    moveTo=new moveTo_impl(x);
    lineTo=new lineTo_impl(x);
    cubicTo=new cubicTo_impl(x);
    setPointAttribute=new setPointAttribute_impl(x);
    close=new close_impl(x);
  }
}
