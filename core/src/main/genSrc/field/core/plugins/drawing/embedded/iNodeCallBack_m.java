package field.core.plugins.drawing.embedded;

import field.bytecode.mirror.impl.*;
import field.core.plugins.drawing.opengl.CachedLine;
import field.core.plugins.drawing.opengl.CachedLine.Event;
import field.launch.IUpdateable;
import field.math.abstraction.IAcceptor;
import field.math.abstraction.IProvider;
import field.math.linalg.Vector2;
import field.namespace.generic.IFunction;
import field.namespace.generic.ReflectionTools;
import java.awt.event.MouseEvent;
import java.lang.reflect.Method;
import java.util.Map;
public class iNodeCallBack_m {
  public static final Method mouseDown_m = ReflectionTools.methodOf("mouseDown",field.core.plugins.drawing.embedded.iNodeCallBack.class, field.core.plugins.drawing.opengl.CachedLine.class, field.core.plugins.drawing.opengl.CachedLine.Event.class, field.math.linalg.Vector2.class, java.awt.event.MouseEvent.class);
  public static final Method mouseDragged_m = ReflectionTools.methodOf("mouseDragged",field.core.plugins.drawing.embedded.iNodeCallBack.class, field.core.plugins.drawing.opengl.CachedLine.class, field.core.plugins.drawing.opengl.CachedLine.Event.class, field.math.linalg.Vector2.class, java.awt.event.MouseEvent.class);
  public static final Method mouseUp_m = ReflectionTools.methodOf("mouseUp",field.core.plugins.drawing.embedded.iNodeCallBack.class, field.core.plugins.drawing.opengl.CachedLine.class, field.core.plugins.drawing.opengl.CachedLine.Event.class, field.math.linalg.Vector2.class, java.awt.event.MouseEvent.class);
  public static final Method mouseClicked_m = ReflectionTools.methodOf("mouseClicked",field.core.plugins.drawing.embedded.iNodeCallBack.class, field.core.plugins.drawing.opengl.CachedLine.class, field.core.plugins.drawing.opengl.CachedLine.Event.class, field.math.linalg.Vector2.class, java.awt.event.MouseEvent.class);
  public static final Method menu_m = ReflectionTools.methodOf("menu",field.core.plugins.drawing.embedded.iNodeCallBack.class, field.core.plugins.drawing.opengl.CachedLine.class, field.core.plugins.drawing.opengl.CachedLine.Event.class, field.math.linalg.Vector2.class, java.awt.event.MouseEvent.class);
  // --------------------------------------------------------------------------------
  public static final MirrorMethod<iNodeCallBack,Object[],Void> mouseDown_s = new MirrorMethod<field.core.plugins.drawing.embedded.iNodeCallBack,Object[],java.lang.Void>(mouseDown_m);
  public static final MirrorMethod<iNodeCallBack,Object[],Void> mouseDragged_s = new MirrorMethod<field.core.plugins.drawing.embedded.iNodeCallBack,Object[],java.lang.Void>(mouseDragged_m);
  public static final MirrorMethod<iNodeCallBack,Object[],Void> mouseUp_s = new MirrorMethod<field.core.plugins.drawing.embedded.iNodeCallBack,Object[],java.lang.Void>(mouseUp_m);
  public static final MirrorMethod<iNodeCallBack,Object[],Void> mouseClicked_s = new MirrorMethod<field.core.plugins.drawing.embedded.iNodeCallBack,Object[],java.lang.Void>(mouseClicked_m);
  public static final MirrorMethod<iNodeCallBack,Object[],Map> menu_s = new MirrorMethod<field.core.plugins.drawing.embedded.iNodeCallBack,Object[],java.util.Map>(menu_m);
  // --------------------------------------------------------------------------------
  public static interface mouseDown_interface
      extends IAcceptor<Object[]>, IFunction<Object[],Void> {
    Void mouseDown(final CachedLine l, final Event e, final Vector2 at, final MouseEvent ev);
    IUpdateable updateable(final CachedLine l, final Event e, final Vector2 at, final MouseEvent ev);
    IProvider<Void> bind(final CachedLine l, final Event e, final Vector2 at, final MouseEvent ev);
  }
  static class mouseDown_impl
      implements mouseDown_interface {
    final iNodeCallBack x;
    final IAcceptor<Object[]> a;
    final IFunction<Object[],Void> f;
    mouseDown_impl(iNodeCallBack x) {
      this.x=x;
      this.a=mouseDown_s.acceptor(x);
      this.f=mouseDown_s.function(x);
    }
    @Override
    public Void mouseDown(final CachedLine l, final Event e, final Vector2 at, final MouseEvent ev) {
       this.x.mouseDown(l,e,at,ev);
      return null;
    }
    @Override
    public IUpdateable updateable(final CachedLine l, final Event e, final Vector2 at, final MouseEvent ev) {
      return new IUpdateable(){
             public void update(){
                mouseDown(l,e,at,ev);
             }
          };
    }
    @Override
    public IProvider<Void> bind(final CachedLine l, final Event e, final Vector2 at, final MouseEvent ev) {
      return new IProvider<java.lang.Void>(){
              public java.lang.Void get(){
                  mouseDown(l,e,at,ev);
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
  public static interface mouseDragged_interface
      extends IAcceptor<Object[]>, IFunction<Object[],Void> {
    Void mouseDragged(final CachedLine l, final Event e, final Vector2 at, final MouseEvent ev);
    IUpdateable updateable(final CachedLine l, final Event e, final Vector2 at, final MouseEvent ev);
    IProvider<Void> bind(final CachedLine l, final Event e, final Vector2 at, final MouseEvent ev);
  }
  static class mouseDragged_impl
      implements mouseDragged_interface {
    final iNodeCallBack x;
    final IAcceptor<Object[]> a;
    final IFunction<Object[],Void> f;
    mouseDragged_impl(iNodeCallBack x) {
      this.x=x;
      this.a=mouseDragged_s.acceptor(x);
      this.f=mouseDragged_s.function(x);
    }
    @Override
    public Void mouseDragged(final CachedLine l, final Event e, final Vector2 at, final MouseEvent ev) {
       this.x.mouseDragged(l,e,at,ev);
      return null;
    }
    @Override
    public IUpdateable updateable(final CachedLine l, final Event e, final Vector2 at, final MouseEvent ev) {
      return new IUpdateable(){
             public void update(){
                mouseDragged(l,e,at,ev);
             }
          };
    }
    @Override
    public IProvider<Void> bind(final CachedLine l, final Event e, final Vector2 at, final MouseEvent ev) {
      return new IProvider<java.lang.Void>(){
              public java.lang.Void get(){
                  mouseDragged(l,e,at,ev);
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
  public static interface mouseUp_interface
      extends IAcceptor<Object[]>, IFunction<Object[],Void> {
    Void mouseUp(final CachedLine l, final Event e, final Vector2 at, final MouseEvent ev);
    IUpdateable updateable(final CachedLine l, final Event e, final Vector2 at, final MouseEvent ev);
    IProvider<Void> bind(final CachedLine l, final Event e, final Vector2 at, final MouseEvent ev);
  }
  static class mouseUp_impl
      implements mouseUp_interface {
    final iNodeCallBack x;
    final IAcceptor<Object[]> a;
    final IFunction<Object[],Void> f;
    mouseUp_impl(iNodeCallBack x) {
      this.x=x;
      this.a=mouseUp_s.acceptor(x);
      this.f=mouseUp_s.function(x);
    }
    @Override
    public Void mouseUp(final CachedLine l, final Event e, final Vector2 at, final MouseEvent ev) {
       this.x.mouseUp(l,e,at,ev);
      return null;
    }
    @Override
    public IUpdateable updateable(final CachedLine l, final Event e, final Vector2 at, final MouseEvent ev) {
      return new IUpdateable(){
             public void update(){
                mouseUp(l,e,at,ev);
             }
          };
    }
    @Override
    public IProvider<Void> bind(final CachedLine l, final Event e, final Vector2 at, final MouseEvent ev) {
      return new IProvider<java.lang.Void>(){
              public java.lang.Void get(){
                  mouseUp(l,e,at,ev);
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
  public static interface mouseClicked_interface
      extends IAcceptor<Object[]>, IFunction<Object[],Void> {
    Void mouseClicked(final CachedLine l, final Event e, final Vector2 at, final MouseEvent ev);
    IUpdateable updateable(final CachedLine l, final Event e, final Vector2 at, final MouseEvent ev);
    IProvider<Void> bind(final CachedLine l, final Event e, final Vector2 at, final MouseEvent ev);
  }
  static class mouseClicked_impl
      implements mouseClicked_interface {
    final iNodeCallBack x;
    final IAcceptor<Object[]> a;
    final IFunction<Object[],Void> f;
    mouseClicked_impl(iNodeCallBack x) {
      this.x=x;
      this.a=mouseClicked_s.acceptor(x);
      this.f=mouseClicked_s.function(x);
    }
    @Override
    public Void mouseClicked(final CachedLine l, final Event e, final Vector2 at, final MouseEvent ev) {
       this.x.mouseClicked(l,e,at,ev);
      return null;
    }
    @Override
    public IUpdateable updateable(final CachedLine l, final Event e, final Vector2 at, final MouseEvent ev) {
      return new IUpdateable(){
             public void update(){
                mouseClicked(l,e,at,ev);
             }
          };
    }
    @Override
    public IProvider<Void> bind(final CachedLine l, final Event e, final Vector2 at, final MouseEvent ev) {
      return new IProvider<java.lang.Void>(){
              public java.lang.Void get(){
                  mouseClicked(l,e,at,ev);
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
  public static interface menu_interface
      extends IAcceptor<Object[]>, IFunction<Object[],Map> {
    Map menu(final CachedLine l, final Event e, final Vector2 at, final MouseEvent ev);
    IUpdateable updateable(final CachedLine l, final Event e, final Vector2 at, final MouseEvent ev);
    IProvider<Map> bind(final CachedLine l, final Event e, final Vector2 at, final MouseEvent ev);
  }
  static class menu_impl
      implements menu_interface {
    final iNodeCallBack x;
    final IAcceptor<Object[]> a;
    final IFunction<Object[],Map> f;
    menu_impl(iNodeCallBack x) {
      this.x=x;
      this.a=menu_s.acceptor(x);
      this.f=menu_s.function(x);
    }
    @Override
    public Map menu(final CachedLine l, final Event e, final Vector2 at, final MouseEvent ev) {
      return this.x.menu(l,e,at,ev);
    }
    @Override
    public IUpdateable updateable(final CachedLine l, final Event e, final Vector2 at, final MouseEvent ev) {
      return new IUpdateable(){
             public void update(){
                menu(l,e,at,ev);
             }
          };
    }
    @Override
    public IProvider<Map> bind(final CachedLine l, final Event e, final Vector2 at, final MouseEvent ev) {
      return new IProvider<java.util.Map>(){
              public java.util.Map get(){
                  return menu(l,e,at,ev);
                  }
          };
    }
    @Override
    public Map apply(Object[] p) {
      return f.apply(p);
    }
    @Override
    public IAcceptor<Object[]> set(Object[] p) {
      this.a.set(p);
      return this;
    }
  }
  // --------------------------------------------------------------------------------
  public final mouseDown_interface mouseDown;
  public final mouseDragged_interface mouseDragged;
  public final mouseUp_interface mouseUp;
  public final mouseClicked_interface mouseClicked;
  public final menu_interface menu;
  // --------------------------------------------------------------------------------

  public iNodeCallBack_m(iNodeCallBack x) {
    mouseDown=new mouseDown_impl(x);
    mouseDragged=new mouseDragged_impl(x);
    mouseUp=new mouseUp_impl(x);
    mouseClicked=new mouseClicked_impl(x);
    menu=new menu_impl(x);
  }
}
