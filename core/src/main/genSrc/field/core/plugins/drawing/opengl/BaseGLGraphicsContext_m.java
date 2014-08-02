package field.core.plugins.drawing.opengl;

import com.google.common.collect.BiMap;
import field.bytecode.mirror.impl.*;
import field.core.plugins.drawing.opengl.BaseGLGraphicsContext.DrawingResult;
import field.core.plugins.drawing.opengl.BaseGLGraphicsContext.DrawingResultCode;
import field.core.plugins.drawing.opengl.BaseGLGraphicsContext.InternalLine;
import field.core.plugins.drawing.opengl.BaseGLGraphicsContext.iDrawingAcceptor;
import field.core.plugins.drawing.opengl.CachedLine;
import field.core.plugins.drawing.opengl.LateExecutingDrawing;
import field.core.plugins.drawing.opengl.LineInteraction;
import field.core.plugins.drawing.opengl.iLinearGraphicsContext;
import field.core.windowing.GLComponentWindow;
import field.core.windowing.overlay.OverlayAnimationManager;
import field.graphics.core.Base.iAcceptsSceneListElement;
import field.graphics.core.BasicGLSLangProgram;
import field.graphics.dynamic.iDynamicMesh;
import field.launch.IUpdateable;
import field.math.abstraction.IAcceptor;
import field.math.abstraction.IProvider;
import field.namespace.generic.IFunction;
import field.namespace.generic.ReflectionTools;
import field.util.Dict;
import field.util.TaskQueue;
import field.util.collect.tuple.Pair;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
public class BaseGLGraphicsContext_m {
  public static final Method windowDisplayEnter_m = ReflectionTools.methodOf("windowDisplayEnter",field.core.plugins.drawing.opengl.BaseGLGraphicsContext.class);
  public static final Method windowDisplayExit_m = ReflectionTools.methodOf("windowDisplayExit",field.core.plugins.drawing.opengl.BaseGLGraphicsContext.class);
  // --------------------------------------------------------------------------------
  public static final MirrorMethod<BaseGLGraphicsContext,Void,Void> windowDisplayEnter_s = new MirrorMethod<field.core.plugins.drawing.opengl.BaseGLGraphicsContext,Void,java.lang.Void>(windowDisplayEnter_m);
  public static final MirrorMethod<BaseGLGraphicsContext,Void,Void> windowDisplayExit_s = new MirrorMethod<field.core.plugins.drawing.opengl.BaseGLGraphicsContext,Void,java.lang.Void>(windowDisplayExit_m);
  // --------------------------------------------------------------------------------
  public static interface windowDisplayEnter_interface
      extends IAcceptor<Void>, IFunction<Void,Void>, IUpdateable {
    Void windowDisplayEnter();
    IUpdateable updateable();
    IProvider<Void> bind();
  }
  static class windowDisplayEnter_impl
      implements windowDisplayEnter_interface {
    final BaseGLGraphicsContext x;
    final IAcceptor<Void> a;
    final IFunction<Void,Void> f;
    windowDisplayEnter_impl(BaseGLGraphicsContext x) {
      this.x=x;
      this.a=windowDisplayEnter_s.acceptor(x);
      this.f=windowDisplayEnter_s.function(x);
    }
    @Override
    public Void windowDisplayEnter() {
       this.x.windowDisplayEnter();
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
                windowDisplayEnter();
             }
          };
    }
    @Override
    public IProvider<Void> bind() {
      return new IProvider<java.lang.Void>(){
              public java.lang.Void get(){
                  windowDisplayEnter();
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
  public static interface windowDisplayExit_interface
      extends IAcceptor<Void>, IFunction<Void,Void>, IUpdateable {
    Void windowDisplayExit();
    IUpdateable updateable();
    IProvider<Void> bind();
  }
  static class windowDisplayExit_impl
      implements windowDisplayExit_interface {
    final BaseGLGraphicsContext x;
    final IAcceptor<Void> a;
    final IFunction<Void,Void> f;
    windowDisplayExit_impl(BaseGLGraphicsContext x) {
      this.x=x;
      this.a=windowDisplayExit_s.acceptor(x);
      this.f=windowDisplayExit_s.function(x);
    }
    @Override
    public Void windowDisplayExit() {
       this.x.windowDisplayExit();
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
                windowDisplayExit();
             }
          };
    }
    @Override
    public IProvider<Void> bind() {
      return new IProvider<java.lang.Void>(){
              public java.lang.Void get(){
                  windowDisplayExit();
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
  public final windowDisplayEnter_interface windowDisplayEnter;
  public final windowDisplayExit_interface windowDisplayExit;
  // --------------------------------------------------------------------------------

  public BaseGLGraphicsContext_m(BaseGLGraphicsContext x) {
    windowDisplayEnter=new windowDisplayEnter_impl(x);
    windowDisplayExit=new windowDisplayExit_impl(x);
  }
}