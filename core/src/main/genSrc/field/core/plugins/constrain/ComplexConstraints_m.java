package field.core.plugins.constrain;

import field.bytecode.mirror.impl.*;
import field.core.dispatch.IVisualElement;
import field.core.dispatch.Rect;
import field.core.dispatch.VisualElementProperty;
import field.core.dispatch.override.IVisualElementOverrides;
import field.core.dispatch.override.Ref;
import field.core.plugins.constrain.ComplexConstraints.LocalVisualElement;
import field.core.plugins.constrain.ComplexConstraints.Overrides;
import field.core.plugins.constrain.ComplexConstraints.VariablesForRect;
import field.core.plugins.constrain.cassowary.ClConstraint;
import field.core.plugins.constrain.cassowary.ClSimplexSolver;
import field.core.plugins.constrain.cassowary.ClVariable;
import field.core.plugins.drawing.opengl.CachedLine;
import field.core.windowing.components.SelectionGroup;
import field.launch.IUpdateable;
import field.math.abstraction.IAcceptor;
import field.math.abstraction.IProvider;
import field.math.graph.IMutableContainer;
import field.math.graph.visitors.hint.TraversalHint;
import field.namespace.generic.IFunction;
import field.namespace.generic.ReflectionTools;
import field.util.SimpleHashQueue;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
public class ComplexConstraints_m {
  public static final Method addEditFor_m = ReflectionTools.methodOf("addEditFor",field.core.plugins.constrain.ComplexConstraints.class, field.core.plugins.constrain.ComplexConstraints.VariablesForRect.class, field.core.dispatch.Rect.class);
  public static final Method addSuggestionFor_m = ReflectionTools.methodOf("addSuggestionFor",field.core.plugins.constrain.ComplexConstraints.class, field.core.plugins.constrain.ComplexConstraints.VariablesForRect.class, field.core.dispatch.Rect.class);
  public static final Method updateFrameFromVariables_m = ReflectionTools.methodOf("updateFrameFromVariables",field.core.plugins.constrain.ComplexConstraints.class, field.core.plugins.constrain.ComplexConstraints.VariablesForRect.class);
  public static final Method updateVariablesFromFrame_m = ReflectionTools.methodOf("updateVariablesFromFrame",field.core.plugins.constrain.ComplexConstraints.class, field.core.plugins.constrain.ComplexConstraints.VariablesForRect.class, field.core.dispatch.Rect.class);
  // --------------------------------------------------------------------------------
  public static final MirrorMethod<ComplexConstraints,Object[],Void> addEditFor_s = new MirrorMethod<field.core.plugins.constrain.ComplexConstraints,Object[],java.lang.Void>(addEditFor_m);
  public static final MirrorMethod<ComplexConstraints,Object[],Void> addSuggestionFor_s = new MirrorMethod<field.core.plugins.constrain.ComplexConstraints,Object[],java.lang.Void>(addSuggestionFor_m);
  public static final MirrorMethod<ComplexConstraints,VariablesForRect,Void> updateFrameFromVariables_s = new MirrorMethod<field.core.plugins.constrain.ComplexConstraints,field.core.plugins.constrain.ComplexConstraints.VariablesForRect,java.lang.Void>(updateFrameFromVariables_m);
  public static final MirrorMethod<ComplexConstraints,Object[],Void> updateVariablesFromFrame_s = new MirrorMethod<field.core.plugins.constrain.ComplexConstraints,Object[],java.lang.Void>(updateVariablesFromFrame_m);
  // --------------------------------------------------------------------------------
  public static interface addEditFor_interface
      extends IAcceptor<Object[]>, IFunction<Object[],Void> {
    Void addEditFor(final VariablesForRect r, final Rect f);
    IUpdateable updateable(final VariablesForRect r, final Rect f);
    IProvider<Void> bind(final VariablesForRect r, final Rect f);
  }
  static class addEditFor_impl
      implements addEditFor_interface {
    final ComplexConstraints x;
    final IAcceptor<Object[]> a;
    final IFunction<Object[],Void> f;
    addEditFor_impl(ComplexConstraints x) {
      this.x=x;
      this.a=addEditFor_s.acceptor(x);
      this.f=addEditFor_s.function(x);
    }
    @Override
    public Void addEditFor(final VariablesForRect r, final Rect f) {
       this.x.addEditFor(r,f);
      return null;
    }
    @Override
    public IUpdateable updateable(final VariablesForRect r, final Rect f) {
      return new IUpdateable(){
             public void update(){
                addEditFor(r,f);
             }
          };
    }
    @Override
    public IProvider<Void> bind(final VariablesForRect r, final Rect f) {
      return new IProvider<java.lang.Void>(){
              public java.lang.Void get(){
                  addEditFor(r,f);
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
  public static interface addSuggestionFor_interface
      extends IAcceptor<Object[]>, IFunction<Object[],Void> {
    Void addSuggestionFor(final VariablesForRect r, final Rect rr);
    IUpdateable updateable(final VariablesForRect r, final Rect rr);
    IProvider<Void> bind(final VariablesForRect r, final Rect rr);
  }
  static class addSuggestionFor_impl
      implements addSuggestionFor_interface {
    final ComplexConstraints x;
    final IAcceptor<Object[]> a;
    final IFunction<Object[],Void> f;
    addSuggestionFor_impl(ComplexConstraints x) {
      this.x=x;
      this.a=addSuggestionFor_s.acceptor(x);
      this.f=addSuggestionFor_s.function(x);
    }
    @Override
    public Void addSuggestionFor(final VariablesForRect r, final Rect rr) {
       this.x.addSuggestionFor(r,rr);
      return null;
    }
    @Override
    public IUpdateable updateable(final VariablesForRect r, final Rect rr) {
      return new IUpdateable(){
             public void update(){
                addSuggestionFor(r,rr);
             }
          };
    }
    @Override
    public IProvider<Void> bind(final VariablesForRect r, final Rect rr) {
      return new IProvider<java.lang.Void>(){
              public java.lang.Void get(){
                  addSuggestionFor(r,rr);
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
  public static interface updateFrameFromVariables_interface
      extends IAcceptor<VariablesForRect>, IFunction<VariablesForRect,Void> {
    Void updateFrameFromVariables(final VariablesForRect r);
    IUpdateable updateable(final VariablesForRect r);
    IProvider<Void> bind(final VariablesForRect r);
  }
  static class updateFrameFromVariables_impl
      implements updateFrameFromVariables_interface {
    final ComplexConstraints x;
    final IAcceptor<VariablesForRect> a;
    final IFunction<VariablesForRect,Void> f;
    updateFrameFromVariables_impl(ComplexConstraints x) {
      this.x=x;
      this.a=updateFrameFromVariables_s.acceptor(x);
      this.f=updateFrameFromVariables_s.function(x);
    }
    @Override
    public Void updateFrameFromVariables(final VariablesForRect r) {
       this.x.updateFrameFromVariables(r);
      return null;
    }
    @Override
    public IUpdateable updateable(final VariablesForRect r) {
      return new IUpdateable(){
             public void update(){
                updateFrameFromVariables(r);
             }
          };
    }
    @Override
    public IProvider<Void> bind(final VariablesForRect r) {
      return new IProvider<java.lang.Void>(){
              public java.lang.Void get(){
                  updateFrameFromVariables(r);
          return null;
                  }
          };
    }
    @Override
    public Void apply(VariablesForRect p) {
      return f.apply(p);
    }
    @Override
    public IAcceptor<VariablesForRect> set(VariablesForRect p) {
      this.a.set(p);
      return this;
    }
  }
  public static interface updateVariablesFromFrame_interface
      extends IAcceptor<Object[]>, IFunction<Object[],Void> {
    Void updateVariablesFromFrame(final VariablesForRect r, final Rect q);
    IUpdateable updateable(final VariablesForRect r, final Rect q);
    IProvider<Void> bind(final VariablesForRect r, final Rect q);
  }
  static class updateVariablesFromFrame_impl
      implements updateVariablesFromFrame_interface {
    final ComplexConstraints x;
    final IAcceptor<Object[]> a;
    final IFunction<Object[],Void> f;
    updateVariablesFromFrame_impl(ComplexConstraints x) {
      this.x=x;
      this.a=updateVariablesFromFrame_s.acceptor(x);
      this.f=updateVariablesFromFrame_s.function(x);
    }
    @Override
    public Void updateVariablesFromFrame(final VariablesForRect r, final Rect q) {
       this.x.updateVariablesFromFrame(r,q);
      return null;
    }
    @Override
    public IUpdateable updateable(final VariablesForRect r, final Rect q) {
      return new IUpdateable(){
             public void update(){
                updateVariablesFromFrame(r,q);
             }
          };
    }
    @Override
    public IProvider<Void> bind(final VariablesForRect r, final Rect q) {
      return new IProvider<java.lang.Void>(){
              public java.lang.Void get(){
                  updateVariablesFromFrame(r,q);
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
  // --------------------------------------------------------------------------------
  public final addEditFor_interface addEditFor;
  public final addSuggestionFor_interface addSuggestionFor;
  public final updateFrameFromVariables_interface updateFrameFromVariables;
  public final updateVariablesFromFrame_interface updateVariablesFromFrame;
  // --------------------------------------------------------------------------------

  public ComplexConstraints_m(ComplexConstraints x) {
    addEditFor=new addEditFor_impl(x);
    addSuggestionFor=new addSuggestionFor_impl(x);
    updateFrameFromVariables=new updateFrameFromVariables_impl(x);
    updateVariablesFromFrame=new updateVariablesFromFrame_impl(x);
  }
}
