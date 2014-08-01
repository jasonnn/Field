package field.core.windowing.components;

import field.bytecode.mirror.impl.*;
import field.core.dispatch.IVisualElement;
import field.core.dispatch.Rect;
import field.core.windowing.GLComponentWindow.ComponentContainer;
import field.launch.IUpdateable;
import field.math.abstraction.IAcceptor;
import field.math.abstraction.IProvider;
import field.math.linalg.iCoordinateFrame;
import field.namespace.generic.IFunction;
import field.namespace.generic.ReflectionTools;
import java.lang.reflect.Method;
import java.util.Set;
import org.eclipse.swt.widgets.Event;
public class iComponent_m {
  public static final Method isHit_m = ReflectionTools.methodOf("isHit",field.core.windowing.components.iComponent.class, org.eclipse.swt.widgets.Event.class);
  public static final Method hit_m = ReflectionTools.methodOf("hit",field.core.windowing.components.iComponent.class, org.eclipse.swt.widgets.Event.class);
  public static final Method getBounds_m = ReflectionTools.methodOf("getBounds",field.core.windowing.components.iComponent.class);
  public static final Method setBounds_m = ReflectionTools.methodOf("setBounds",field.core.windowing.components.iComponent.class, field.core.dispatch.Rect.class);
  public static final Method keyTyped_m = ReflectionTools.methodOf("keyTyped",field.core.windowing.components.iComponent.class, field.core.windowing.GLComponentWindow.ComponentContainer.class, org.eclipse.swt.widgets.Event.class);
  public static final Method keyPressed_m = ReflectionTools.methodOf("keyPressed",field.core.windowing.components.iComponent.class, field.core.windowing.GLComponentWindow.ComponentContainer.class, org.eclipse.swt.widgets.Event.class);
  public static final Method keyReleased_m = ReflectionTools.methodOf("keyReleased",field.core.windowing.components.iComponent.class, field.core.windowing.GLComponentWindow.ComponentContainer.class, org.eclipse.swt.widgets.Event.class);
  public static final Method mouseClicked_m = ReflectionTools.methodOf("mouseClicked",field.core.windowing.components.iComponent.class, field.core.windowing.GLComponentWindow.ComponentContainer.class, org.eclipse.swt.widgets.Event.class);
  public static final Method mousePressed_m = ReflectionTools.methodOf("mousePressed",field.core.windowing.components.iComponent.class, field.core.windowing.GLComponentWindow.ComponentContainer.class, org.eclipse.swt.widgets.Event.class);
  public static final Method mouseReleased_m = ReflectionTools.methodOf("mouseReleased",field.core.windowing.components.iComponent.class, field.core.windowing.GLComponentWindow.ComponentContainer.class, org.eclipse.swt.widgets.Event.class);
  public static final Method mouseEntered_m = ReflectionTools.methodOf("mouseEntered",field.core.windowing.components.iComponent.class, field.core.windowing.GLComponentWindow.ComponentContainer.class, org.eclipse.swt.widgets.Event.class);
  public static final Method mouseExited_m = ReflectionTools.methodOf("mouseExited",field.core.windowing.components.iComponent.class, field.core.windowing.GLComponentWindow.ComponentContainer.class, org.eclipse.swt.widgets.Event.class);
  public static final Method mouseDragged_m = ReflectionTools.methodOf("mouseDragged",field.core.windowing.components.iComponent.class, field.core.windowing.GLComponentWindow.ComponentContainer.class, org.eclipse.swt.widgets.Event.class);
  public static final Method mouseMoved_m = ReflectionTools.methodOf("mouseMoved",field.core.windowing.components.iComponent.class, field.core.windowing.GLComponentWindow.ComponentContainer.class, org.eclipse.swt.widgets.Event.class);
  public static final Method beginMouseFocus_m = ReflectionTools.methodOf("beginMouseFocus",field.core.windowing.components.iComponent.class, field.core.windowing.GLComponentWindow.ComponentContainer.class);
  public static final Method endMouseFocus_m = ReflectionTools.methodOf("endMouseFocus",field.core.windowing.components.iComponent.class, field.core.windowing.GLComponentWindow.ComponentContainer.class);
  public static final Method paint_m = ReflectionTools.methodOf("paint",field.core.windowing.components.iComponent.class, field.core.windowing.GLComponentWindow.ComponentContainer.class, field.math.linalg.iCoordinateFrame.class, boolean.class);
  public static final Method handleResize_m = ReflectionTools.methodOf("handleResize",field.core.windowing.components.iComponent.class, java.util.Set.class, float.class, float.class);
  public static final Method getVisualElement_m = ReflectionTools.methodOf("getVisualElement",field.core.windowing.components.iComponent.class);
  public static final Method setVisualElement_m = ReflectionTools.methodOf("setVisualElement",field.core.windowing.components.iComponent.class, field.core.dispatch.IVisualElement.class);
  // --------------------------------------------------------------------------------
  public static final MirrorMethod<iComponent,Event,Float> isHit_s = new MirrorMethod<field.core.windowing.components.iComponent,org.eclipse.swt.widgets.Event,java.lang.Float>(isHit_m);
  public static final MirrorMethod<iComponent,Event,iComponent> hit_s = new MirrorMethod<field.core.windowing.components.iComponent,org.eclipse.swt.widgets.Event,field.core.windowing.components.iComponent>(hit_m);
  public static final MirrorMethod<iComponent,Void,Rect> getBounds_s = new MirrorMethod<field.core.windowing.components.iComponent,Void,field.core.dispatch.Rect>(getBounds_m);
  public static final MirrorMethod<iComponent,Rect,Void> setBounds_s = new MirrorMethod<field.core.windowing.components.iComponent,field.core.dispatch.Rect,java.lang.Void>(setBounds_m);
  public static final MirrorMethod<iComponent,Object[],Void> keyTyped_s = new MirrorMethod<field.core.windowing.components.iComponent,Object[],java.lang.Void>(keyTyped_m);
  public static final MirrorMethod<iComponent,Object[],Void> keyPressed_s = new MirrorMethod<field.core.windowing.components.iComponent,Object[],java.lang.Void>(keyPressed_m);
  public static final MirrorMethod<iComponent,Object[],Void> keyReleased_s = new MirrorMethod<field.core.windowing.components.iComponent,Object[],java.lang.Void>(keyReleased_m);
  public static final MirrorMethod<iComponent,Object[],Void> mouseClicked_s = new MirrorMethod<field.core.windowing.components.iComponent,Object[],java.lang.Void>(mouseClicked_m);
  public static final MirrorMethod<iComponent,Object[],Void> mousePressed_s = new MirrorMethod<field.core.windowing.components.iComponent,Object[],java.lang.Void>(mousePressed_m);
  public static final MirrorMethod<iComponent,Object[],Void> mouseReleased_s = new MirrorMethod<field.core.windowing.components.iComponent,Object[],java.lang.Void>(mouseReleased_m);
  public static final MirrorMethod<iComponent,Object[],Void> mouseEntered_s = new MirrorMethod<field.core.windowing.components.iComponent,Object[],java.lang.Void>(mouseEntered_m);
  public static final MirrorMethod<iComponent,Object[],Void> mouseExited_s = new MirrorMethod<field.core.windowing.components.iComponent,Object[],java.lang.Void>(mouseExited_m);
  public static final MirrorMethod<iComponent,Object[],Void> mouseDragged_s = new MirrorMethod<field.core.windowing.components.iComponent,Object[],java.lang.Void>(mouseDragged_m);
  public static final MirrorMethod<iComponent,Object[],Void> mouseMoved_s = new MirrorMethod<field.core.windowing.components.iComponent,Object[],java.lang.Void>(mouseMoved_m);
  public static final MirrorMethod<iComponent,ComponentContainer,Void> beginMouseFocus_s = new MirrorMethod<field.core.windowing.components.iComponent,field.core.windowing.GLComponentWindow.ComponentContainer,java.lang.Void>(beginMouseFocus_m);
  public static final MirrorMethod<iComponent,ComponentContainer,Void> endMouseFocus_s = new MirrorMethod<field.core.windowing.components.iComponent,field.core.windowing.GLComponentWindow.ComponentContainer,java.lang.Void>(endMouseFocus_m);
  public static final MirrorMethod<iComponent,Object[],Void> paint_s = new MirrorMethod<field.core.windowing.components.iComponent,Object[],java.lang.Void>(paint_m);
  public static final MirrorMethod<iComponent,Object[],Void> handleResize_s = new MirrorMethod<field.core.windowing.components.iComponent,Object[],java.lang.Void>(handleResize_m);
  public static final MirrorMethod<iComponent,Void,IVisualElement> getVisualElement_s = new MirrorMethod<field.core.windowing.components.iComponent,Void,field.core.dispatch.IVisualElement>(getVisualElement_m);
  public static final MirrorMethod<iComponent,IVisualElement,iComponent> setVisualElement_s = new MirrorMethod<field.core.windowing.components.iComponent,field.core.dispatch.IVisualElement,field.core.windowing.components.iComponent>(setVisualElement_m);
  // --------------------------------------------------------------------------------
  public static interface isHit_interface
      extends IAcceptor<Event>, IFunction<Event,Float> {
    float isHit(final Event event);
    IUpdateable updateable(final Event event);
    IProvider<Float> bind(final Event event);
  }
  static class isHit_impl
      implements isHit_interface {
    final iComponent x;
    final IAcceptor<Event> a;
    final IFunction<Event,Float> f;
    isHit_impl(iComponent x) {
      this.x=x;
      this.a=isHit_s.acceptor(x);
      this.f=isHit_s.function(x);
    }
    @Override
    public float isHit(final Event event) {
      return this.x.isHit(event);
    }
    @Override
    public IUpdateable updateable(final Event event) {
      return new IUpdateable(){
             public void update(){
                isHit(event);
             }
          };
    }
    @Override
    public IProvider<Float> bind(final Event event) {
      return new IProvider<java.lang.Float>(){
              public java.lang.Float get(){
                  return isHit(event);
                  }
          };
    }
    @Override
    public Float apply(Event p) {
      return f.apply(p);
    }
    @Override
    public IAcceptor<Event> set(Event p) {
      this.a.set(p);
      return this;
    }
  }
  public static interface hit_interface
      extends IAcceptor<Event>, IFunction<Event,iComponent> {
    iComponent hit(final Event event);
    IUpdateable updateable(final Event event);
    IProvider<iComponent> bind(final Event event);
  }
  static class hit_impl
      implements hit_interface {
    final iComponent x;
    final IAcceptor<Event> a;
    final IFunction<Event,iComponent> f;
    hit_impl(iComponent x) {
      this.x=x;
      this.a=hit_s.acceptor(x);
      this.f=hit_s.function(x);
    }
    @Override
    public iComponent hit(final Event event) {
      return this.x.hit(event);
    }
    @Override
    public IUpdateable updateable(final Event event) {
      return new IUpdateable(){
             public void update(){
                hit(event);
             }
          };
    }
    @Override
    public IProvider<iComponent> bind(final Event event) {
      return new IProvider<field.core.windowing.components.iComponent>(){
              public field.core.windowing.components.iComponent get(){
                  return hit(event);
                  }
          };
    }
    @Override
    public iComponent apply(Event p) {
      return f.apply(p);
    }
    @Override
    public IAcceptor<Event> set(Event p) {
      this.a.set(p);
      return this;
    }
  }
  public static interface getBounds_interface
      extends IAcceptor<Void>, IFunction<Void,Rect>, IUpdateable {
    Rect getBounds();
    IUpdateable updateable();
    IProvider<Rect> bind();
  }
  static class getBounds_impl
      implements getBounds_interface {
    final iComponent x;
    final IAcceptor<Void> a;
    final IFunction<Void,Rect> f;
    getBounds_impl(iComponent x) {
      this.x=x;
      this.a=getBounds_s.acceptor(x);
      this.f=getBounds_s.function(x);
    }
    @Override
    public Rect getBounds() {
      return this.x.getBounds();
    }
    @Override
    public void update() {
      updateable().update();
    }
    @Override
    public IUpdateable updateable() {
      return new IUpdateable(){
             public void update(){
                getBounds();
             }
          };
    }
    @Override
    public IProvider<Rect> bind() {
      return new IProvider<field.core.dispatch.Rect>(){
              public field.core.dispatch.Rect get(){
                  return getBounds();
                  }
          };
    }
    @Override
    public Rect apply(Void p) {
      return f.apply(p);
    }
    @Override
    public IAcceptor<Void> set(Void p) {
      this.a.set(p);
      return this;
    }
  }
  public static interface setBounds_interface
      extends IAcceptor<Rect>, IFunction<Rect,Void> {
    Void setBounds(final Rect r);
    IUpdateable updateable(final Rect r);
    IProvider<Void> bind(final Rect r);
  }
  static class setBounds_impl
      implements setBounds_interface {
    final iComponent x;
    final IAcceptor<Rect> a;
    final IFunction<Rect,Void> f;
    setBounds_impl(iComponent x) {
      this.x=x;
      this.a=setBounds_s.acceptor(x);
      this.f=setBounds_s.function(x);
    }
    @Override
    public Void setBounds(final Rect r) {
       this.x.setBounds(r);
      return null;
    }
    @Override
    public IUpdateable updateable(final Rect r) {
      return new IUpdateable(){
             public void update(){
                setBounds(r);
             }
          };
    }
    @Override
    public IProvider<Void> bind(final Rect r) {
      return new IProvider<java.lang.Void>(){
              public java.lang.Void get(){
                  setBounds(r);
          return null;
                  }
          };
    }
    @Override
    public Void apply(Rect p) {
      return f.apply(p);
    }
    @Override
    public IAcceptor<Rect> set(Rect p) {
      this.a.set(p);
      return this;
    }
  }
  public static interface keyTyped_interface
      extends IAcceptor<Object[]>, IFunction<Object[],Void> {
    Void keyTyped(final ComponentContainer inside, final Event arg0);
    IUpdateable updateable(final ComponentContainer inside, final Event arg0);
    IProvider<Void> bind(final ComponentContainer inside, final Event arg0);
  }
  static class keyTyped_impl
      implements keyTyped_interface {
    final iComponent x;
    final IAcceptor<Object[]> a;
    final IFunction<Object[],Void> f;
    keyTyped_impl(iComponent x) {
      this.x=x;
      this.a=keyTyped_s.acceptor(x);
      this.f=keyTyped_s.function(x);
    }
    @Override
    public Void keyTyped(final ComponentContainer inside, final Event arg0) {
       this.x.keyTyped(inside,arg0);
      return null;
    }
    @Override
    public IUpdateable updateable(final ComponentContainer inside, final Event arg0) {
      return new IUpdateable(){
             public void update(){
                keyTyped(inside,arg0);
             }
          };
    }
    @Override
    public IProvider<Void> bind(final ComponentContainer inside, final Event arg0) {
      return new IProvider<java.lang.Void>(){
              public java.lang.Void get(){
                  keyTyped(inside,arg0);
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
  public static interface keyPressed_interface
      extends IAcceptor<Object[]>, IFunction<Object[],Void> {
    Void keyPressed(final ComponentContainer inside, final Event arg0);
    IUpdateable updateable(final ComponentContainer inside, final Event arg0);
    IProvider<Void> bind(final ComponentContainer inside, final Event arg0);
  }
  static class keyPressed_impl
      implements keyPressed_interface {
    final iComponent x;
    final IAcceptor<Object[]> a;
    final IFunction<Object[],Void> f;
    keyPressed_impl(iComponent x) {
      this.x=x;
      this.a=keyPressed_s.acceptor(x);
      this.f=keyPressed_s.function(x);
    }
    @Override
    public Void keyPressed(final ComponentContainer inside, final Event arg0) {
       this.x.keyPressed(inside,arg0);
      return null;
    }
    @Override
    public IUpdateable updateable(final ComponentContainer inside, final Event arg0) {
      return new IUpdateable(){
             public void update(){
                keyPressed(inside,arg0);
             }
          };
    }
    @Override
    public IProvider<Void> bind(final ComponentContainer inside, final Event arg0) {
      return new IProvider<java.lang.Void>(){
              public java.lang.Void get(){
                  keyPressed(inside,arg0);
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
  public static interface keyReleased_interface
      extends IAcceptor<Object[]>, IFunction<Object[],Void> {
    Void keyReleased(final ComponentContainer inside, final Event arg0);
    IUpdateable updateable(final ComponentContainer inside, final Event arg0);
    IProvider<Void> bind(final ComponentContainer inside, final Event arg0);
  }
  static class keyReleased_impl
      implements keyReleased_interface {
    final iComponent x;
    final IAcceptor<Object[]> a;
    final IFunction<Object[],Void> f;
    keyReleased_impl(iComponent x) {
      this.x=x;
      this.a=keyReleased_s.acceptor(x);
      this.f=keyReleased_s.function(x);
    }
    @Override
    public Void keyReleased(final ComponentContainer inside, final Event arg0) {
       this.x.keyReleased(inside,arg0);
      return null;
    }
    @Override
    public IUpdateable updateable(final ComponentContainer inside, final Event arg0) {
      return new IUpdateable(){
             public void update(){
                keyReleased(inside,arg0);
             }
          };
    }
    @Override
    public IProvider<Void> bind(final ComponentContainer inside, final Event arg0) {
      return new IProvider<java.lang.Void>(){
              public java.lang.Void get(){
                  keyReleased(inside,arg0);
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
    Void mouseClicked(final ComponentContainer inside, final Event arg0);
    IUpdateable updateable(final ComponentContainer inside, final Event arg0);
    IProvider<Void> bind(final ComponentContainer inside, final Event arg0);
  }
  static class mouseClicked_impl
      implements mouseClicked_interface {
    final iComponent x;
    final IAcceptor<Object[]> a;
    final IFunction<Object[],Void> f;
    mouseClicked_impl(iComponent x) {
      this.x=x;
      this.a=mouseClicked_s.acceptor(x);
      this.f=mouseClicked_s.function(x);
    }
    @Override
    public Void mouseClicked(final ComponentContainer inside, final Event arg0) {
       this.x.mouseClicked(inside,arg0);
      return null;
    }
    @Override
    public IUpdateable updateable(final ComponentContainer inside, final Event arg0) {
      return new IUpdateable(){
             public void update(){
                mouseClicked(inside,arg0);
             }
          };
    }
    @Override
    public IProvider<Void> bind(final ComponentContainer inside, final Event arg0) {
      return new IProvider<java.lang.Void>(){
              public java.lang.Void get(){
                  mouseClicked(inside,arg0);
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
  public static interface mousePressed_interface
      extends IAcceptor<Object[]>, IFunction<Object[],Void> {
    Void mousePressed(final ComponentContainer inside, final Event arg0);
    IUpdateable updateable(final ComponentContainer inside, final Event arg0);
    IProvider<Void> bind(final ComponentContainer inside, final Event arg0);
  }
  static class mousePressed_impl
      implements mousePressed_interface {
    final iComponent x;
    final IAcceptor<Object[]> a;
    final IFunction<Object[],Void> f;
    mousePressed_impl(iComponent x) {
      this.x=x;
      this.a=mousePressed_s.acceptor(x);
      this.f=mousePressed_s.function(x);
    }
    @Override
    public Void mousePressed(final ComponentContainer inside, final Event arg0) {
       this.x.mousePressed(inside,arg0);
      return null;
    }
    @Override
    public IUpdateable updateable(final ComponentContainer inside, final Event arg0) {
      return new IUpdateable(){
             public void update(){
                mousePressed(inside,arg0);
             }
          };
    }
    @Override
    public IProvider<Void> bind(final ComponentContainer inside, final Event arg0) {
      return new IProvider<java.lang.Void>(){
              public java.lang.Void get(){
                  mousePressed(inside,arg0);
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
  public static interface mouseReleased_interface
      extends IAcceptor<Object[]>, IFunction<Object[],Void> {
    Void mouseReleased(final ComponentContainer inside, final Event arg0);
    IUpdateable updateable(final ComponentContainer inside, final Event arg0);
    IProvider<Void> bind(final ComponentContainer inside, final Event arg0);
  }
  static class mouseReleased_impl
      implements mouseReleased_interface {
    final iComponent x;
    final IAcceptor<Object[]> a;
    final IFunction<Object[],Void> f;
    mouseReleased_impl(iComponent x) {
      this.x=x;
      this.a=mouseReleased_s.acceptor(x);
      this.f=mouseReleased_s.function(x);
    }
    @Override
    public Void mouseReleased(final ComponentContainer inside, final Event arg0) {
       this.x.mouseReleased(inside,arg0);
      return null;
    }
    @Override
    public IUpdateable updateable(final ComponentContainer inside, final Event arg0) {
      return new IUpdateable(){
             public void update(){
                mouseReleased(inside,arg0);
             }
          };
    }
    @Override
    public IProvider<Void> bind(final ComponentContainer inside, final Event arg0) {
      return new IProvider<java.lang.Void>(){
              public java.lang.Void get(){
                  mouseReleased(inside,arg0);
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
  public static interface mouseEntered_interface
      extends IAcceptor<Object[]>, IFunction<Object[],Void> {
    Void mouseEntered(final ComponentContainer inside, final Event arg0);
    IUpdateable updateable(final ComponentContainer inside, final Event arg0);
    IProvider<Void> bind(final ComponentContainer inside, final Event arg0);
  }
  static class mouseEntered_impl
      implements mouseEntered_interface {
    final iComponent x;
    final IAcceptor<Object[]> a;
    final IFunction<Object[],Void> f;
    mouseEntered_impl(iComponent x) {
      this.x=x;
      this.a=mouseEntered_s.acceptor(x);
      this.f=mouseEntered_s.function(x);
    }
    @Override
    public Void mouseEntered(final ComponentContainer inside, final Event arg0) {
       this.x.mouseEntered(inside,arg0);
      return null;
    }
    @Override
    public IUpdateable updateable(final ComponentContainer inside, final Event arg0) {
      return new IUpdateable(){
             public void update(){
                mouseEntered(inside,arg0);
             }
          };
    }
    @Override
    public IProvider<Void> bind(final ComponentContainer inside, final Event arg0) {
      return new IProvider<java.lang.Void>(){
              public java.lang.Void get(){
                  mouseEntered(inside,arg0);
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
  public static interface mouseExited_interface
      extends IAcceptor<Object[]>, IFunction<Object[],Void> {
    Void mouseExited(final ComponentContainer inside, final Event arg0);
    IUpdateable updateable(final ComponentContainer inside, final Event arg0);
    IProvider<Void> bind(final ComponentContainer inside, final Event arg0);
  }
  static class mouseExited_impl
      implements mouseExited_interface {
    final iComponent x;
    final IAcceptor<Object[]> a;
    final IFunction<Object[],Void> f;
    mouseExited_impl(iComponent x) {
      this.x=x;
      this.a=mouseExited_s.acceptor(x);
      this.f=mouseExited_s.function(x);
    }
    @Override
    public Void mouseExited(final ComponentContainer inside, final Event arg0) {
       this.x.mouseExited(inside,arg0);
      return null;
    }
    @Override
    public IUpdateable updateable(final ComponentContainer inside, final Event arg0) {
      return new IUpdateable(){
             public void update(){
                mouseExited(inside,arg0);
             }
          };
    }
    @Override
    public IProvider<Void> bind(final ComponentContainer inside, final Event arg0) {
      return new IProvider<java.lang.Void>(){
              public java.lang.Void get(){
                  mouseExited(inside,arg0);
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
    Void mouseDragged(final ComponentContainer inside, final Event arg0);
    IUpdateable updateable(final ComponentContainer inside, final Event arg0);
    IProvider<Void> bind(final ComponentContainer inside, final Event arg0);
  }
  static class mouseDragged_impl
      implements mouseDragged_interface {
    final iComponent x;
    final IAcceptor<Object[]> a;
    final IFunction<Object[],Void> f;
    mouseDragged_impl(iComponent x) {
      this.x=x;
      this.a=mouseDragged_s.acceptor(x);
      this.f=mouseDragged_s.function(x);
    }
    @Override
    public Void mouseDragged(final ComponentContainer inside, final Event arg0) {
       this.x.mouseDragged(inside,arg0);
      return null;
    }
    @Override
    public IUpdateable updateable(final ComponentContainer inside, final Event arg0) {
      return new IUpdateable(){
             public void update(){
                mouseDragged(inside,arg0);
             }
          };
    }
    @Override
    public IProvider<Void> bind(final ComponentContainer inside, final Event arg0) {
      return new IProvider<java.lang.Void>(){
              public java.lang.Void get(){
                  mouseDragged(inside,arg0);
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
  public static interface mouseMoved_interface
      extends IAcceptor<Object[]>, IFunction<Object[],Void> {
    Void mouseMoved(final ComponentContainer inside, final Event arg0);
    IUpdateable updateable(final ComponentContainer inside, final Event arg0);
    IProvider<Void> bind(final ComponentContainer inside, final Event arg0);
  }
  static class mouseMoved_impl
      implements mouseMoved_interface {
    final iComponent x;
    final IAcceptor<Object[]> a;
    final IFunction<Object[],Void> f;
    mouseMoved_impl(iComponent x) {
      this.x=x;
      this.a=mouseMoved_s.acceptor(x);
      this.f=mouseMoved_s.function(x);
    }
    @Override
    public Void mouseMoved(final ComponentContainer inside, final Event arg0) {
       this.x.mouseMoved(inside,arg0);
      return null;
    }
    @Override
    public IUpdateable updateable(final ComponentContainer inside, final Event arg0) {
      return new IUpdateable(){
             public void update(){
                mouseMoved(inside,arg0);
             }
          };
    }
    @Override
    public IProvider<Void> bind(final ComponentContainer inside, final Event arg0) {
      return new IProvider<java.lang.Void>(){
              public java.lang.Void get(){
                  mouseMoved(inside,arg0);
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
  public static interface beginMouseFocus_interface
      extends IAcceptor<ComponentContainer>, IFunction<ComponentContainer,Void> {
    Void beginMouseFocus(final ComponentContainer inside);
    IUpdateable updateable(final ComponentContainer inside);
    IProvider<Void> bind(final ComponentContainer inside);
  }
  static class beginMouseFocus_impl
      implements beginMouseFocus_interface {
    final iComponent x;
    final IAcceptor<ComponentContainer> a;
    final IFunction<ComponentContainer,Void> f;
    beginMouseFocus_impl(iComponent x) {
      this.x=x;
      this.a=beginMouseFocus_s.acceptor(x);
      this.f=beginMouseFocus_s.function(x);
    }
    @Override
    public Void beginMouseFocus(final ComponentContainer inside) {
       this.x.beginMouseFocus(inside);
      return null;
    }
    @Override
    public IUpdateable updateable(final ComponentContainer inside) {
      return new IUpdateable(){
             public void update(){
                beginMouseFocus(inside);
             }
          };
    }
    @Override
    public IProvider<Void> bind(final ComponentContainer inside) {
      return new IProvider<java.lang.Void>(){
              public java.lang.Void get(){
                  beginMouseFocus(inside);
          return null;
                  }
          };
    }
    @Override
    public Void apply(ComponentContainer p) {
      return f.apply(p);
    }
    @Override
    public IAcceptor<ComponentContainer> set(ComponentContainer p) {
      this.a.set(p);
      return this;
    }
  }
  public static interface endMouseFocus_interface
      extends IAcceptor<ComponentContainer>, IFunction<ComponentContainer,Void> {
    Void endMouseFocus(final ComponentContainer inside);
    IUpdateable updateable(final ComponentContainer inside);
    IProvider<Void> bind(final ComponentContainer inside);
  }
  static class endMouseFocus_impl
      implements endMouseFocus_interface {
    final iComponent x;
    final IAcceptor<ComponentContainer> a;
    final IFunction<ComponentContainer,Void> f;
    endMouseFocus_impl(iComponent x) {
      this.x=x;
      this.a=endMouseFocus_s.acceptor(x);
      this.f=endMouseFocus_s.function(x);
    }
    @Override
    public Void endMouseFocus(final ComponentContainer inside) {
       this.x.endMouseFocus(inside);
      return null;
    }
    @Override
    public IUpdateable updateable(final ComponentContainer inside) {
      return new IUpdateable(){
             public void update(){
                endMouseFocus(inside);
             }
          };
    }
    @Override
    public IProvider<Void> bind(final ComponentContainer inside) {
      return new IProvider<java.lang.Void>(){
              public java.lang.Void get(){
                  endMouseFocus(inside);
          return null;
                  }
          };
    }
    @Override
    public Void apply(ComponentContainer p) {
      return f.apply(p);
    }
    @Override
    public IAcceptor<ComponentContainer> set(ComponentContainer p) {
      this.a.set(p);
      return this;
    }
  }
  public static interface paint_interface
      extends IAcceptor<Object[]>, IFunction<Object[],Void> {
    Void paint(final ComponentContainer inside, final iCoordinateFrame frameSoFar, final boolean visible);
    IUpdateable updateable(final ComponentContainer inside, final iCoordinateFrame frameSoFar, final boolean visible);
    IProvider<Void> bind(final ComponentContainer inside, final iCoordinateFrame frameSoFar, final boolean visible);
  }
  static class paint_impl
      implements paint_interface {
    final iComponent x;
    final IAcceptor<Object[]> a;
    final IFunction<Object[],Void> f;
    paint_impl(iComponent x) {
      this.x=x;
      this.a=paint_s.acceptor(x);
      this.f=paint_s.function(x);
    }
    @Override
    public Void paint(final ComponentContainer inside, final iCoordinateFrame frameSoFar, final boolean visible) {
       this.x.paint(inside,frameSoFar,visible);
      return null;
    }
    @Override
    public IUpdateable updateable(final ComponentContainer inside, final iCoordinateFrame frameSoFar, final boolean visible) {
      return new IUpdateable(){
             public void update(){
                paint(inside,frameSoFar,visible);
             }
          };
    }
    @Override
    public IProvider<Void> bind(final ComponentContainer inside, final iCoordinateFrame frameSoFar, final boolean visible) {
      return new IProvider<java.lang.Void>(){
              public java.lang.Void get(){
                  paint(inside,frameSoFar,visible);
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
  public static interface handleResize_interface
      extends IAcceptor<Object[]>, IFunction<Object[],Void> {
    Void handleResize(final Set<DraggableComponent.Resize> currentResize, final float dx, final float dy);
    IUpdateable updateable(final Set<DraggableComponent.Resize> currentResize, final float dx, final float dy);
    IProvider<Void> bind(final Set<DraggableComponent.Resize> currentResize, final float dx, final float dy);
  }
  static class handleResize_impl
      implements handleResize_interface {
    final iComponent x;
    final IAcceptor<Object[]> a;
    final IFunction<Object[],Void> f;
    handleResize_impl(iComponent x) {
      this.x=x;
      this.a=handleResize_s.acceptor(x);
      this.f=handleResize_s.function(x);
    }
    @Override
    public Void handleResize(final Set<DraggableComponent.Resize> currentResize, final float dx, final float dy) {
       this.x.handleResize(currentResize,dx,dy);
      return null;
    }
    @Override
    public IUpdateable updateable(final Set<DraggableComponent.Resize> currentResize, final float dx, final float dy) {
      return new IUpdateable(){
             public void update(){
                handleResize(currentResize,dx,dy);
             }
          };
    }
    @Override
    public IProvider<Void> bind(final Set<DraggableComponent.Resize> currentResize, final float dx, final float dy) {
      return new IProvider<java.lang.Void>(){
              public java.lang.Void get(){
                  handleResize(currentResize,dx,dy);
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
  public static interface getVisualElement_interface
      extends IAcceptor<Void>, IFunction<Void,IVisualElement>, IUpdateable {
    IVisualElement getVisualElement();
    IUpdateable updateable();
    IProvider<IVisualElement> bind();
  }
  static class getVisualElement_impl
      implements getVisualElement_interface {
    final iComponent x;
    final IAcceptor<Void> a;
    final IFunction<Void,IVisualElement> f;
    getVisualElement_impl(iComponent x) {
      this.x=x;
      this.a=getVisualElement_s.acceptor(x);
      this.f=getVisualElement_s.function(x);
    }
    @Override
    public IVisualElement getVisualElement() {
      return this.x.getVisualElement();
    }
    @Override
    public void update() {
      updateable().update();
    }
    @Override
    public IUpdateable updateable() {
      return new IUpdateable(){
             public void update(){
                getVisualElement();
             }
          };
    }
    @Override
    public IProvider<IVisualElement> bind() {
      return new IProvider<field.core.dispatch.IVisualElement>(){
              public field.core.dispatch.IVisualElement get(){
                  return getVisualElement();
                  }
          };
    }
    @Override
    public IVisualElement apply(Void p) {
      return f.apply(p);
    }
    @Override
    public IAcceptor<Void> set(Void p) {
      this.a.set(p);
      return this;
    }
  }
  public static interface setVisualElement_interface
      extends IAcceptor<IVisualElement>, IFunction<IVisualElement,iComponent> {
    iComponent setVisualElement(final IVisualElement ve);
    IUpdateable updateable(final IVisualElement ve);
    IProvider<iComponent> bind(final IVisualElement ve);
  }
  static class setVisualElement_impl
      implements setVisualElement_interface {
    final iComponent x;
    final IAcceptor<IVisualElement> a;
    final IFunction<IVisualElement,iComponent> f;
    setVisualElement_impl(iComponent x) {
      this.x=x;
      this.a=setVisualElement_s.acceptor(x);
      this.f=setVisualElement_s.function(x);
    }
    @Override
    public iComponent setVisualElement(final IVisualElement ve) {
      return this.x.setVisualElement(ve);
    }
    @Override
    public IUpdateable updateable(final IVisualElement ve) {
      return new IUpdateable(){
             public void update(){
                setVisualElement(ve);
             }
          };
    }
    @Override
    public IProvider<iComponent> bind(final IVisualElement ve) {
      return new IProvider<field.core.windowing.components.iComponent>(){
              public field.core.windowing.components.iComponent get(){
                  return setVisualElement(ve);
                  }
          };
    }
    @Override
    public iComponent apply(IVisualElement p) {
      return f.apply(p);
    }
    @Override
    public IAcceptor<IVisualElement> set(IVisualElement p) {
      this.a.set(p);
      return this;
    }
  }
  // --------------------------------------------------------------------------------
  public final isHit_interface isHit;
  public final hit_interface hit;
  public final getBounds_interface getBounds;
  public final setBounds_interface setBounds;
  public final keyTyped_interface keyTyped;
  public final keyPressed_interface keyPressed;
  public final keyReleased_interface keyReleased;
  public final mouseClicked_interface mouseClicked;
  public final mousePressed_interface mousePressed;
  public final mouseReleased_interface mouseReleased;
  public final mouseEntered_interface mouseEntered;
  public final mouseExited_interface mouseExited;
  public final mouseDragged_interface mouseDragged;
  public final mouseMoved_interface mouseMoved;
  public final beginMouseFocus_interface beginMouseFocus;
  public final endMouseFocus_interface endMouseFocus;
  public final paint_interface paint;
  public final handleResize_interface handleResize;
  public final getVisualElement_interface getVisualElement;
  public final setVisualElement_interface setVisualElement;
  // --------------------------------------------------------------------------------

  public iComponent_m(iComponent x) {
    isHit=new isHit_impl(x);
    hit=new hit_impl(x);
    getBounds=new getBounds_impl(x);
    setBounds=new setBounds_impl(x);
    keyTyped=new keyTyped_impl(x);
    keyPressed=new keyPressed_impl(x);
    keyReleased=new keyReleased_impl(x);
    mouseClicked=new mouseClicked_impl(x);
    mousePressed=new mousePressed_impl(x);
    mouseReleased=new mouseReleased_impl(x);
    mouseEntered=new mouseEntered_impl(x);
    mouseExited=new mouseExited_impl(x);
    mouseDragged=new mouseDragged_impl(x);
    mouseMoved=new mouseMoved_impl(x);
    beginMouseFocus=new beginMouseFocus_impl(x);
    endMouseFocus=new endMouseFocus_impl(x);
    paint=new paint_impl(x);
    handleResize=new handleResize_impl(x);
    getVisualElement=new getVisualElement_impl(x);
    setVisualElement=new setVisualElement_impl(x);
  }
}
