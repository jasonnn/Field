package field.core.dispatch.override;

import com.google.common.cache.LoadingCache;
import field.bytecode.mirror.impl.*;
import field.core.dispatch.IVisualElement;
import field.core.dispatch.Rect;
import field.core.dispatch.VisualElementContextTopology;
import field.core.dispatch.VisualElementProperty;
import field.core.dispatch.override.IVisualElementOverrides.MakeDispatchProxy;
import field.core.dispatch.override.IVisualElementOverrides.MakeDispatchProxy.BackwardsProxy;
import field.core.dispatch.override.IVisualElementOverrides.MakeDispatchProxy.OverrideProxy;
import field.core.dispatch.override.IVisualElementOverrides_m;
import field.core.dispatch.override.Ref;
import field.launch.IUpdateable;
import field.math.abstraction.IAcceptor;
import field.math.abstraction.IProvider;
import field.math.graph.visitors.hint.TraversalHint;
import field.namespace.dispatch.DispatchOverTopology.Raw;
import field.namespace.generic.IFunction;
import field.namespace.generic.ReflectionTools;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.eclipse.swt.widgets.Event;
public class IVisualElementOverrides_m {
  public static final Method added_m = ReflectionTools.methodOf("added",field.core.dispatch.override.IVisualElementOverrides.class, field.core.dispatch.IVisualElement.class);
  public static final Method beginExecution_m = ReflectionTools.methodOf("beginExecution",field.core.dispatch.override.IVisualElementOverrides.class, field.core.dispatch.IVisualElement.class);
  public static final Method deleted_m = ReflectionTools.methodOf("deleted",field.core.dispatch.override.IVisualElementOverrides.class, field.core.dispatch.IVisualElement.class);
  public static final Method deleteProperty_m = ReflectionTools.methodOf("deleteProperty",field.core.dispatch.override.IVisualElementOverrides.class, field.core.dispatch.IVisualElement.class, field.core.dispatch.VisualElementProperty.class);
  public static final Method endExecution_m = ReflectionTools.methodOf("endExecution",field.core.dispatch.override.IVisualElementOverrides.class, field.core.dispatch.IVisualElement.class);
  public static final Method getProperty_m = ReflectionTools.methodOf("getProperty",field.core.dispatch.override.IVisualElementOverrides.class, field.core.dispatch.IVisualElement.class, field.core.dispatch.VisualElementProperty.class, field.core.dispatch.override.Ref.class);
  public static final Method handleKeyboardEvent_m = ReflectionTools.methodOf("handleKeyboardEvent",field.core.dispatch.override.IVisualElementOverrides.class, field.core.dispatch.IVisualElement.class, org.eclipse.swt.widgets.Event.class);
  public static final Method inspectablePropertiesFor_m = ReflectionTools.methodOf("inspectablePropertiesFor",field.core.dispatch.override.IVisualElementOverrides.class, field.core.dispatch.IVisualElement.class, java.util.List.class);
  public static final Method isHit_m = ReflectionTools.methodOf("isHit",field.core.dispatch.override.IVisualElementOverrides.class, field.core.dispatch.IVisualElement.class, org.eclipse.swt.widgets.Event.class, field.core.dispatch.override.Ref.class);
  public static final Method menuItemsFor_m = ReflectionTools.methodOf("menuItemsFor",field.core.dispatch.override.IVisualElementOverrides.class, field.core.dispatch.IVisualElement.class, java.util.Map.class);
  public static final Method paintNow_m = ReflectionTools.methodOf("paintNow",field.core.dispatch.override.IVisualElementOverrides.class, field.core.dispatch.IVisualElement.class, field.core.dispatch.Rect.class, boolean.class);
  public static final Method prepareForSave_m = ReflectionTools.methodOf("prepareForSave",field.core.dispatch.override.IVisualElementOverrides.class);
  public static final Method setProperty_m = ReflectionTools.methodOf("setProperty",field.core.dispatch.override.IVisualElementOverrides.class, field.core.dispatch.IVisualElement.class, field.core.dispatch.VisualElementProperty.class, field.core.dispatch.override.Ref.class);
  public static final Method shouldChangeFrame_m = ReflectionTools.methodOf("shouldChangeFrame",field.core.dispatch.override.IVisualElementOverrides.class, field.core.dispatch.IVisualElement.class, field.core.dispatch.Rect.class, field.core.dispatch.Rect.class, boolean.class);
  // --------------------------------------------------------------------------------
  public static final MirrorMethod<IVisualElementOverrides,IVisualElement,TraversalHint> added_s = new MirrorMethod<field.core.dispatch.override.IVisualElementOverrides,field.core.dispatch.IVisualElement,field.math.graph.visitors.hint.TraversalHint>(added_m);
  public static final MirrorMethod<IVisualElementOverrides,IVisualElement,TraversalHint> beginExecution_s = new MirrorMethod<field.core.dispatch.override.IVisualElementOverrides,field.core.dispatch.IVisualElement,field.math.graph.visitors.hint.TraversalHint>(beginExecution_m);
  public static final MirrorMethod<IVisualElementOverrides,IVisualElement,TraversalHint> deleted_s = new MirrorMethod<field.core.dispatch.override.IVisualElementOverrides,field.core.dispatch.IVisualElement,field.math.graph.visitors.hint.TraversalHint>(deleted_m);
  public static final MirrorMethod<IVisualElementOverrides,Object[],TraversalHint> deleteProperty_s = new MirrorMethod<field.core.dispatch.override.IVisualElementOverrides,Object[],field.math.graph.visitors.hint.TraversalHint>(deleteProperty_m);
  public static final MirrorMethod<IVisualElementOverrides,IVisualElement,TraversalHint> endExecution_s = new MirrorMethod<field.core.dispatch.override.IVisualElementOverrides,field.core.dispatch.IVisualElement,field.math.graph.visitors.hint.TraversalHint>(endExecution_m);
  public static final MirrorMethod<IVisualElementOverrides,Object[],TraversalHint> getProperty_s = new MirrorMethod<field.core.dispatch.override.IVisualElementOverrides,Object[],field.math.graph.visitors.hint.TraversalHint>(getProperty_m);
  public static final MirrorMethod<IVisualElementOverrides,Object[],TraversalHint> handleKeyboardEvent_s = new MirrorMethod<field.core.dispatch.override.IVisualElementOverrides,Object[],field.math.graph.visitors.hint.TraversalHint>(handleKeyboardEvent_m);
  public static final MirrorMethod<IVisualElementOverrides,Object[],TraversalHint> inspectablePropertiesFor_s = new MirrorMethod<field.core.dispatch.override.IVisualElementOverrides,Object[],field.math.graph.visitors.hint.TraversalHint>(inspectablePropertiesFor_m);
  public static final MirrorMethod<IVisualElementOverrides,Object[],TraversalHint> isHit_s = new MirrorMethod<field.core.dispatch.override.IVisualElementOverrides,Object[],field.math.graph.visitors.hint.TraversalHint>(isHit_m);
  public static final MirrorMethod<IVisualElementOverrides,Object[],TraversalHint> menuItemsFor_s = new MirrorMethod<field.core.dispatch.override.IVisualElementOverrides,Object[],field.math.graph.visitors.hint.TraversalHint>(menuItemsFor_m);
  public static final MirrorMethod<IVisualElementOverrides,Object[],TraversalHint> paintNow_s = new MirrorMethod<field.core.dispatch.override.IVisualElementOverrides,Object[],field.math.graph.visitors.hint.TraversalHint>(paintNow_m);
  public static final MirrorMethod<IVisualElementOverrides,Void,TraversalHint> prepareForSave_s = new MirrorMethod<field.core.dispatch.override.IVisualElementOverrides,Void,field.math.graph.visitors.hint.TraversalHint>(prepareForSave_m);
  public static final MirrorMethod<IVisualElementOverrides,Object[],TraversalHint> setProperty_s = new MirrorMethod<field.core.dispatch.override.IVisualElementOverrides,Object[],field.math.graph.visitors.hint.TraversalHint>(setProperty_m);
  public static final MirrorMethod<IVisualElementOverrides,Object[],TraversalHint> shouldChangeFrame_s = new MirrorMethod<field.core.dispatch.override.IVisualElementOverrides,Object[],field.math.graph.visitors.hint.TraversalHint>(shouldChangeFrame_m);
  // --------------------------------------------------------------------------------
  public static interface added_interface
      extends IAcceptor<IVisualElement>, IFunction<IVisualElement,TraversalHint> {
    TraversalHint added(final IVisualElement newSource);
    IUpdateable updateable(final IVisualElement newSource);
    IProvider<TraversalHint> bind(final IVisualElement newSource);
  }
  static class added_impl
      implements added_interface {
    final IVisualElementOverrides x;
    final IAcceptor<IVisualElement> a;
    final IFunction<IVisualElement,TraversalHint> f;
    added_impl(IVisualElementOverrides x) {
      this.x=x;
      this.a=added_s.acceptor(x);
      this.f=added_s.function(x);
    }
    @Override
    public TraversalHint added(final IVisualElement newSource) {
      return this.x.added(newSource);
    }
    @Override
    public IUpdateable updateable(final IVisualElement newSource) {
      return new IUpdateable(){
             public void update(){
                added(newSource);
             }
          };
    }
    @Override
    public IProvider<TraversalHint> bind(final IVisualElement newSource) {
      return new IProvider<field.math.graph.visitors.hint.TraversalHint>(){
              public field.math.graph.visitors.hint.TraversalHint get(){
                  return added(newSource);
                  }
          };
    }
    @Override
    public TraversalHint apply(IVisualElement p) {
      return f.apply(p);
    }
    @Override
    public IAcceptor<IVisualElement> set(IVisualElement p) {
      this.a.set(p);
      return this;
    }
  }
  public static interface beginExecution_interface
      extends IAcceptor<IVisualElement>, IFunction<IVisualElement,TraversalHint> {
    TraversalHint beginExecution(final IVisualElement source);
    IUpdateable updateable(final IVisualElement source);
    IProvider<TraversalHint> bind(final IVisualElement source);
  }
  static class beginExecution_impl
      implements beginExecution_interface {
    final IVisualElementOverrides x;
    final IAcceptor<IVisualElement> a;
    final IFunction<IVisualElement,TraversalHint> f;
    beginExecution_impl(IVisualElementOverrides x) {
      this.x=x;
      this.a=beginExecution_s.acceptor(x);
      this.f=beginExecution_s.function(x);
    }
    @Override
    public TraversalHint beginExecution(final IVisualElement source) {
      return this.x.beginExecution(source);
    }
    @Override
    public IUpdateable updateable(final IVisualElement source) {
      return new IUpdateable(){
             public void update(){
                beginExecution(source);
             }
          };
    }
    @Override
    public IProvider<TraversalHint> bind(final IVisualElement source) {
      return new IProvider<field.math.graph.visitors.hint.TraversalHint>(){
              public field.math.graph.visitors.hint.TraversalHint get(){
                  return beginExecution(source);
                  }
          };
    }
    @Override
    public TraversalHint apply(IVisualElement p) {
      return f.apply(p);
    }
    @Override
    public IAcceptor<IVisualElement> set(IVisualElement p) {
      this.a.set(p);
      return this;
    }
  }
  public static interface deleted_interface
      extends IAcceptor<IVisualElement>, IFunction<IVisualElement,TraversalHint> {
    TraversalHint deleted(final IVisualElement source);
    IUpdateable updateable(final IVisualElement source);
    IProvider<TraversalHint> bind(final IVisualElement source);
  }
  static class deleted_impl
      implements deleted_interface {
    final IVisualElementOverrides x;
    final IAcceptor<IVisualElement> a;
    final IFunction<IVisualElement,TraversalHint> f;
    deleted_impl(IVisualElementOverrides x) {
      this.x=x;
      this.a=deleted_s.acceptor(x);
      this.f=deleted_s.function(x);
    }
    @Override
    public TraversalHint deleted(final IVisualElement source) {
      return this.x.deleted(source);
    }
    @Override
    public IUpdateable updateable(final IVisualElement source) {
      return new IUpdateable(){
             public void update(){
                deleted(source);
             }
          };
    }
    @Override
    public IProvider<TraversalHint> bind(final IVisualElement source) {
      return new IProvider<field.math.graph.visitors.hint.TraversalHint>(){
              public field.math.graph.visitors.hint.TraversalHint get(){
                  return deleted(source);
                  }
          };
    }
    @Override
    public TraversalHint apply(IVisualElement p) {
      return f.apply(p);
    }
    @Override
    public IAcceptor<IVisualElement> set(IVisualElement p) {
      this.a.set(p);
      return this;
    }
  }
  public static interface deleteProperty_interface
      extends IAcceptor<Object[]>, IFunction<Object[],TraversalHint> {
    TraversalHint deleteProperty(final IVisualElement source, final VisualElementProperty prop);
    IUpdateable updateable(final IVisualElement source, final VisualElementProperty prop);
    IProvider<TraversalHint> bind(final IVisualElement source, final VisualElementProperty prop);
  }
  static class deleteProperty_impl
      implements deleteProperty_interface {
    final IVisualElementOverrides x;
    final IAcceptor<Object[]> a;
    final IFunction<Object[],TraversalHint> f;
    deleteProperty_impl(IVisualElementOverrides x) {
      this.x=x;
      this.a=deleteProperty_s.acceptor(x);
      this.f=deleteProperty_s.function(x);
    }
    @Override
    public TraversalHint deleteProperty(final IVisualElement source, final VisualElementProperty prop) {
      return this.x.deleteProperty(source,prop);
    }
    @Override
    public IUpdateable updateable(final IVisualElement source, final VisualElementProperty prop) {
      return new IUpdateable(){
             public void update(){
                deleteProperty(source,prop);
             }
          };
    }
    @Override
    public IProvider<TraversalHint> bind(final IVisualElement source, final VisualElementProperty prop) {
      return new IProvider<field.math.graph.visitors.hint.TraversalHint>(){
              public field.math.graph.visitors.hint.TraversalHint get(){
                  return deleteProperty(source,prop);
                  }
          };
    }
    @Override
    public TraversalHint apply(Object[] p) {
      return f.apply(p);
    }
    @Override
    public IAcceptor<Object[]> set(Object[] p) {
      this.a.set(p);
      return this;
    }
  }
  public static interface endExecution_interface
      extends IAcceptor<IVisualElement>, IFunction<IVisualElement,TraversalHint> {
    TraversalHint endExecution(final IVisualElement source);
    IUpdateable updateable(final IVisualElement source);
    IProvider<TraversalHint> bind(final IVisualElement source);
  }
  static class endExecution_impl
      implements endExecution_interface {
    final IVisualElementOverrides x;
    final IAcceptor<IVisualElement> a;
    final IFunction<IVisualElement,TraversalHint> f;
    endExecution_impl(IVisualElementOverrides x) {
      this.x=x;
      this.a=endExecution_s.acceptor(x);
      this.f=endExecution_s.function(x);
    }
    @Override
    public TraversalHint endExecution(final IVisualElement source) {
      return this.x.endExecution(source);
    }
    @Override
    public IUpdateable updateable(final IVisualElement source) {
      return new IUpdateable(){
             public void update(){
                endExecution(source);
             }
          };
    }
    @Override
    public IProvider<TraversalHint> bind(final IVisualElement source) {
      return new IProvider<field.math.graph.visitors.hint.TraversalHint>(){
              public field.math.graph.visitors.hint.TraversalHint get(){
                  return endExecution(source);
                  }
          };
    }
    @Override
    public TraversalHint apply(IVisualElement p) {
      return f.apply(p);
    }
    @Override
    public IAcceptor<IVisualElement> set(IVisualElement p) {
      this.a.set(p);
      return this;
    }
  }
  public static interface getProperty_interface
      extends IAcceptor<Object[]>, IFunction<Object[],TraversalHint> {
    TraversalHint getProperty(final IVisualElement source, final VisualElementProperty prop, final Ref ref);
    IUpdateable updateable(final IVisualElement source, final VisualElementProperty prop, final Ref ref);
    IProvider<TraversalHint> bind(final IVisualElement source, final VisualElementProperty prop, final Ref ref);
  }
  static class getProperty_impl
      implements getProperty_interface {
    final IVisualElementOverrides x;
    final IAcceptor<Object[]> a;
    final IFunction<Object[],TraversalHint> f;
    getProperty_impl(IVisualElementOverrides x) {
      this.x=x;
      this.a=getProperty_s.acceptor(x);
      this.f=getProperty_s.function(x);
    }
    @Override
    public TraversalHint getProperty(final IVisualElement source, final VisualElementProperty prop, final Ref ref) {
      return this.x.getProperty(source,prop,ref);
    }
    @Override
    public IUpdateable updateable(final IVisualElement source, final VisualElementProperty prop, final Ref ref) {
      return new IUpdateable(){
             public void update(){
                getProperty(source,prop,ref);
             }
          };
    }
    @Override
    public IProvider<TraversalHint> bind(final IVisualElement source, final VisualElementProperty prop, final Ref ref) {
      return new IProvider<field.math.graph.visitors.hint.TraversalHint>(){
              public field.math.graph.visitors.hint.TraversalHint get(){
                  return getProperty(source,prop,ref);
                  }
          };
    }
    @Override
    public TraversalHint apply(Object[] p) {
      return f.apply(p);
    }
    @Override
    public IAcceptor<Object[]> set(Object[] p) {
      this.a.set(p);
      return this;
    }
  }
  public static interface handleKeyboardEvent_interface
      extends IAcceptor<Object[]>, IFunction<Object[],TraversalHint> {
    TraversalHint handleKeyboardEvent(final IVisualElement newSource, final Event event);
    IUpdateable updateable(final IVisualElement newSource, final Event event);
    IProvider<TraversalHint> bind(final IVisualElement newSource, final Event event);
  }
  static class handleKeyboardEvent_impl
      implements handleKeyboardEvent_interface {
    final IVisualElementOverrides x;
    final IAcceptor<Object[]> a;
    final IFunction<Object[],TraversalHint> f;
    handleKeyboardEvent_impl(IVisualElementOverrides x) {
      this.x=x;
      this.a=handleKeyboardEvent_s.acceptor(x);
      this.f=handleKeyboardEvent_s.function(x);
    }
    @Override
    public TraversalHint handleKeyboardEvent(final IVisualElement newSource, final Event event) {
      return this.x.handleKeyboardEvent(newSource,event);
    }
    @Override
    public IUpdateable updateable(final IVisualElement newSource, final Event event) {
      return new IUpdateable(){
             public void update(){
                handleKeyboardEvent(newSource,event);
             }
          };
    }
    @Override
    public IProvider<TraversalHint> bind(final IVisualElement newSource, final Event event) {
      return new IProvider<field.math.graph.visitors.hint.TraversalHint>(){
              public field.math.graph.visitors.hint.TraversalHint get(){
                  return handleKeyboardEvent(newSource,event);
                  }
          };
    }
    @Override
    public TraversalHint apply(Object[] p) {
      return f.apply(p);
    }
    @Override
    public IAcceptor<Object[]> set(Object[] p) {
      this.a.set(p);
      return this;
    }
  }
  public static interface inspectablePropertiesFor_interface
      extends IAcceptor<Object[]>, IFunction<Object[],TraversalHint> {
    TraversalHint inspectablePropertiesFor(final IVisualElement source, final List<field.util.Dict.Prop> properties);
    IUpdateable updateable(final IVisualElement source, final List<field.util.Dict.Prop> properties);
    IProvider<TraversalHint> bind(final IVisualElement source, final List<field.util.Dict.Prop> properties);
  }
  static class inspectablePropertiesFor_impl
      implements inspectablePropertiesFor_interface {
    final IVisualElementOverrides x;
    final IAcceptor<Object[]> a;
    final IFunction<Object[],TraversalHint> f;
    inspectablePropertiesFor_impl(IVisualElementOverrides x) {
      this.x=x;
      this.a=inspectablePropertiesFor_s.acceptor(x);
      this.f=inspectablePropertiesFor_s.function(x);
    }
    @Override
    public TraversalHint inspectablePropertiesFor(final IVisualElement source, final List<field.util.Dict.Prop> properties) {
      return this.x.inspectablePropertiesFor(source,properties);
    }
    @Override
    public IUpdateable updateable(final IVisualElement source, final List<field.util.Dict.Prop> properties) {
      return new IUpdateable(){
             public void update(){
                inspectablePropertiesFor(source,properties);
             }
          };
    }
    @Override
    public IProvider<TraversalHint> bind(final IVisualElement source, final List<field.util.Dict.Prop> properties) {
      return new IProvider<field.math.graph.visitors.hint.TraversalHint>(){
              public field.math.graph.visitors.hint.TraversalHint get(){
                  return inspectablePropertiesFor(source,properties);
                  }
          };
    }
    @Override
    public TraversalHint apply(Object[] p) {
      return f.apply(p);
    }
    @Override
    public IAcceptor<Object[]> set(Object[] p) {
      this.a.set(p);
      return this;
    }
  }
  public static interface isHit_interface
      extends IAcceptor<Object[]>, IFunction<Object[],TraversalHint> {
    TraversalHint isHit(final IVisualElement source, final Event event, final Ref<Boolean> is);
    IUpdateable updateable(final IVisualElement source, final Event event, final Ref<Boolean> is);
    IProvider<TraversalHint> bind(final IVisualElement source, final Event event, final Ref<Boolean> is);
  }
  static class isHit_impl
      implements isHit_interface {
    final IVisualElementOverrides x;
    final IAcceptor<Object[]> a;
    final IFunction<Object[],TraversalHint> f;
    isHit_impl(IVisualElementOverrides x) {
      this.x=x;
      this.a=isHit_s.acceptor(x);
      this.f=isHit_s.function(x);
    }
    @Override
    public TraversalHint isHit(final IVisualElement source, final Event event, final Ref<Boolean> is) {
      return this.x.isHit(source,event,is);
    }
    @Override
    public IUpdateable updateable(final IVisualElement source, final Event event, final Ref<Boolean> is) {
      return new IUpdateable(){
             public void update(){
                isHit(source,event,is);
             }
          };
    }
    @Override
    public IProvider<TraversalHint> bind(final IVisualElement source, final Event event, final Ref<Boolean> is) {
      return new IProvider<field.math.graph.visitors.hint.TraversalHint>(){
              public field.math.graph.visitors.hint.TraversalHint get(){
                  return isHit(source,event,is);
                  }
          };
    }
    @Override
    public TraversalHint apply(Object[] p) {
      return f.apply(p);
    }
    @Override
    public IAcceptor<Object[]> set(Object[] p) {
      this.a.set(p);
      return this;
    }
  }
  public static interface menuItemsFor_interface
      extends IAcceptor<Object[]>, IFunction<Object[],TraversalHint> {
    TraversalHint menuItemsFor(final IVisualElement source, final Map<String,IUpdateable> items);
    IUpdateable updateable(final IVisualElement source, final Map<String,IUpdateable> items);
    IProvider<TraversalHint> bind(final IVisualElement source, final Map<String,IUpdateable> items);
  }
  static class menuItemsFor_impl
      implements menuItemsFor_interface {
    final IVisualElementOverrides x;
    final IAcceptor<Object[]> a;
    final IFunction<Object[],TraversalHint> f;
    menuItemsFor_impl(IVisualElementOverrides x) {
      this.x=x;
      this.a=menuItemsFor_s.acceptor(x);
      this.f=menuItemsFor_s.function(x);
    }
    @Override
    public TraversalHint menuItemsFor(final IVisualElement source, final Map<String,IUpdateable> items) {
      return this.x.menuItemsFor(source,items);
    }
    @Override
    public IUpdateable updateable(final IVisualElement source, final Map<String,IUpdateable> items) {
      return new IUpdateable(){
             public void update(){
                menuItemsFor(source,items);
             }
          };
    }
    @Override
    public IProvider<TraversalHint> bind(final IVisualElement source, final Map<String,IUpdateable> items) {
      return new IProvider<field.math.graph.visitors.hint.TraversalHint>(){
              public field.math.graph.visitors.hint.TraversalHint get(){
                  return menuItemsFor(source,items);
                  }
          };
    }
    @Override
    public TraversalHint apply(Object[] p) {
      return f.apply(p);
    }
    @Override
    public IAcceptor<Object[]> set(Object[] p) {
      this.a.set(p);
      return this;
    }
  }
  public static interface paintNow_interface
      extends IAcceptor<Object[]>, IFunction<Object[],TraversalHint> {
    TraversalHint paintNow(final IVisualElement source, final Rect bounds, final boolean visible);
    IUpdateable updateable(final IVisualElement source, final Rect bounds, final boolean visible);
    IProvider<TraversalHint> bind(final IVisualElement source, final Rect bounds, final boolean visible);
  }
  static class paintNow_impl
      implements paintNow_interface {
    final IVisualElementOverrides x;
    final IAcceptor<Object[]> a;
    final IFunction<Object[],TraversalHint> f;
    paintNow_impl(IVisualElementOverrides x) {
      this.x=x;
      this.a=paintNow_s.acceptor(x);
      this.f=paintNow_s.function(x);
    }
    @Override
    public TraversalHint paintNow(final IVisualElement source, final Rect bounds, final boolean visible) {
      return this.x.paintNow(source,bounds,visible);
    }
    @Override
    public IUpdateable updateable(final IVisualElement source, final Rect bounds, final boolean visible) {
      return new IUpdateable(){
             public void update(){
                paintNow(source,bounds,visible);
             }
          };
    }
    @Override
    public IProvider<TraversalHint> bind(final IVisualElement source, final Rect bounds, final boolean visible) {
      return new IProvider<field.math.graph.visitors.hint.TraversalHint>(){
              public field.math.graph.visitors.hint.TraversalHint get(){
                  return paintNow(source,bounds,visible);
                  }
          };
    }
    @Override
    public TraversalHint apply(Object[] p) {
      return f.apply(p);
    }
    @Override
    public IAcceptor<Object[]> set(Object[] p) {
      this.a.set(p);
      return this;
    }
  }
  public static interface prepareForSave_interface
      extends IAcceptor<Void>, IFunction<Void,TraversalHint>, IUpdateable {
    TraversalHint prepareForSave();
    IUpdateable updateable();
    IProvider<TraversalHint> bind();
  }
  static class prepareForSave_impl
      implements prepareForSave_interface {
    final IVisualElementOverrides x;
    final IAcceptor<Void> a;
    final IFunction<Void,TraversalHint> f;
    prepareForSave_impl(IVisualElementOverrides x) {
      this.x=x;
      this.a=prepareForSave_s.acceptor(x);
      this.f=prepareForSave_s.function(x);
    }
    @Override
    public TraversalHint prepareForSave() {
      return this.x.prepareForSave();
    }
    @Override
    public void update() {
      updateable().update();
    }
    @Override
    public IUpdateable updateable() {
      return new IUpdateable(){
             public void update(){
                prepareForSave();
             }
          };
    }
    @Override
    public IProvider<TraversalHint> bind() {
      return new IProvider<field.math.graph.visitors.hint.TraversalHint>(){
              public field.math.graph.visitors.hint.TraversalHint get(){
                  return prepareForSave();
                  }
          };
    }
    @Override
    public TraversalHint apply(Void p) {
      return f.apply(p);
    }
    @Override
    public IAcceptor<Void> set(Void p) {
      this.a.set(p);
      return this;
    }
  }
  public static interface setProperty_interface
      extends IAcceptor<Object[]>, IFunction<Object[],TraversalHint> {
    TraversalHint setProperty(final IVisualElement source, final VisualElementProperty prop, final Ref to);
    IUpdateable updateable(final IVisualElement source, final VisualElementProperty prop, final Ref to);
    IProvider<TraversalHint> bind(final IVisualElement source, final VisualElementProperty prop, final Ref to);
  }
  static class setProperty_impl
      implements setProperty_interface {
    final IVisualElementOverrides x;
    final IAcceptor<Object[]> a;
    final IFunction<Object[],TraversalHint> f;
    setProperty_impl(IVisualElementOverrides x) {
      this.x=x;
      this.a=setProperty_s.acceptor(x);
      this.f=setProperty_s.function(x);
    }
    @Override
    public TraversalHint setProperty(final IVisualElement source, final VisualElementProperty prop, final Ref to) {
      return this.x.setProperty(source,prop,to);
    }
    @Override
    public IUpdateable updateable(final IVisualElement source, final VisualElementProperty prop, final Ref to) {
      return new IUpdateable(){
             public void update(){
                setProperty(source,prop,to);
             }
          };
    }
    @Override
    public IProvider<TraversalHint> bind(final IVisualElement source, final VisualElementProperty prop, final Ref to) {
      return new IProvider<field.math.graph.visitors.hint.TraversalHint>(){
              public field.math.graph.visitors.hint.TraversalHint get(){
                  return setProperty(source,prop,to);
                  }
          };
    }
    @Override
    public TraversalHint apply(Object[] p) {
      return f.apply(p);
    }
    @Override
    public IAcceptor<Object[]> set(Object[] p) {
      this.a.set(p);
      return this;
    }
  }
  public static interface shouldChangeFrame_interface
      extends IAcceptor<Object[]>, IFunction<Object[],TraversalHint> {
    TraversalHint shouldChangeFrame(final IVisualElement source, final Rect newFrame, final Rect oldFrame, final boolean now);
    IUpdateable updateable(final IVisualElement source, final Rect newFrame, final Rect oldFrame, final boolean now);
    IProvider<TraversalHint> bind(final IVisualElement source, final Rect newFrame, final Rect oldFrame, final boolean now);
  }
  static class shouldChangeFrame_impl
      implements shouldChangeFrame_interface {
    final IVisualElementOverrides x;
    final IAcceptor<Object[]> a;
    final IFunction<Object[],TraversalHint> f;
    shouldChangeFrame_impl(IVisualElementOverrides x) {
      this.x=x;
      this.a=shouldChangeFrame_s.acceptor(x);
      this.f=shouldChangeFrame_s.function(x);
    }
    @Override
    public TraversalHint shouldChangeFrame(final IVisualElement source, final Rect newFrame, final Rect oldFrame, final boolean now) {
      return this.x.shouldChangeFrame(source,newFrame,oldFrame,now);
    }
    @Override
    public IUpdateable updateable(final IVisualElement source, final Rect newFrame, final Rect oldFrame, final boolean now) {
      return new IUpdateable(){
             public void update(){
                shouldChangeFrame(source,newFrame,oldFrame,now);
             }
          };
    }
    @Override
    public IProvider<TraversalHint> bind(final IVisualElement source, final Rect newFrame, final Rect oldFrame, final boolean now) {
      return new IProvider<field.math.graph.visitors.hint.TraversalHint>(){
              public field.math.graph.visitors.hint.TraversalHint get(){
                  return shouldChangeFrame(source,newFrame,oldFrame,now);
                  }
          };
    }
    @Override
    public TraversalHint apply(Object[] p) {
      return f.apply(p);
    }
    @Override
    public IAcceptor<Object[]> set(Object[] p) {
      this.a.set(p);
      return this;
    }
  }
  // --------------------------------------------------------------------------------
  public final added_interface added;
  public final beginExecution_interface beginExecution;
  public final deleted_interface deleted;
  public final deleteProperty_interface deleteProperty;
  public final endExecution_interface endExecution;
  public final getProperty_interface getProperty;
  public final handleKeyboardEvent_interface handleKeyboardEvent;
  public final inspectablePropertiesFor_interface inspectablePropertiesFor;
  public final isHit_interface isHit;
  public final menuItemsFor_interface menuItemsFor;
  public final paintNow_interface paintNow;
  public final prepareForSave_interface prepareForSave;
  public final setProperty_interface setProperty;
  public final shouldChangeFrame_interface shouldChangeFrame;
  // --------------------------------------------------------------------------------

  public IVisualElementOverrides_m(IVisualElementOverrides x) {
    added=new added_impl(x);
    beginExecution=new beginExecution_impl(x);
    deleted=new deleted_impl(x);
    deleteProperty=new deleteProperty_impl(x);
    endExecution=new endExecution_impl(x);
    getProperty=new getProperty_impl(x);
    handleKeyboardEvent=new handleKeyboardEvent_impl(x);
    inspectablePropertiesFor=new inspectablePropertiesFor_impl(x);
    isHit=new isHit_impl(x);
    menuItemsFor=new menuItemsFor_impl(x);
    paintNow=new paintNow_impl(x);
    prepareForSave=new prepareForSave_impl(x);
    setProperty=new setProperty_impl(x);
    shouldChangeFrame=new shouldChangeFrame_impl(x);
  }
}
