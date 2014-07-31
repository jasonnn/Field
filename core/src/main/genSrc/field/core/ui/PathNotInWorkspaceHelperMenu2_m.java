package field.core.ui;

import field.bytecode.mirror.impl.*;
import field.core.ui.FieldMenus2;
import field.launch.IUpdateable;
import field.math.abstraction.IAcceptor;
import field.math.abstraction.IProvider;
import field.namespace.generic.IFunction;
import field.namespace.generic.ReflectionTools;
import java.io.File;
import java.lang.reflect.Method;
import org.eclipse.swt.widgets.Shell;
public class PathNotInWorkspaceHelperMenu2_m {
  public static final Method move_m = ReflectionTools.methodOf("move",field.core.ui.PathNotInWorkspaceHelperMenu2.class, java.io.File.class);
  public static final Method copy_m = ReflectionTools.methodOf("copy",field.core.ui.PathNotInWorkspaceHelperMenu2.class, java.io.File.class);
  // --------------------------------------------------------------------------------
  public static final MirrorMethod<PathNotInWorkspaceHelperMenu2,File,Void> move_s = new MirrorMethod<field.core.ui.PathNotInWorkspaceHelperMenu2,java.io.File,java.lang.Void>(move_m);
  public static final MirrorMethod<PathNotInWorkspaceHelperMenu2,File,Void> copy_s = new MirrorMethod<field.core.ui.PathNotInWorkspaceHelperMenu2,java.io.File,java.lang.Void>(copy_m);
  // --------------------------------------------------------------------------------
  public static interface move_interface
      extends IAcceptor<File>, IFunction<File,Void> {
    Void move(final File to);
    IUpdateable updateable(final File to);
    IProvider<Void> bind(final File to);
  }
  static class move_impl
      implements move_interface {
    final PathNotInWorkspaceHelperMenu2 x;
    final IAcceptor<File> a;
    final IFunction<File,Void> f;
    move_impl(PathNotInWorkspaceHelperMenu2 x) {
      this.x=x;
      this.a=move_s.acceptor(x);
      this.f=move_s.function(x);
    }
    @Override
    public Void move(final File to) {
       this.x.move(to);
      return null;
    }
    @Override
    public IUpdateable updateable(final File to) {
      return new IUpdateable(){
             public void update(){
                move(to);
             }
          };
    }
    @Override
    public IProvider<Void> bind(final File to) {
      return new IProvider<java.lang.Void>(){
              public java.lang.Void get(){
                  move(to);
          return null;
                  }
          };
    }
    @Override
    public Void apply(File p) {
      return f.apply(p);
    }
    @Override
    public IAcceptor<File> set(File p) {
      this.a.set(p);
      return this;
    }
  }
  public static interface copy_interface
      extends IAcceptor<File>, IFunction<File,Void> {
    Void copy(final File to);
    IUpdateable updateable(final File to);
    IProvider<Void> bind(final File to);
  }
  static class copy_impl
      implements copy_interface {
    final PathNotInWorkspaceHelperMenu2 x;
    final IAcceptor<File> a;
    final IFunction<File,Void> f;
    copy_impl(PathNotInWorkspaceHelperMenu2 x) {
      this.x=x;
      this.a=copy_s.acceptor(x);
      this.f=copy_s.function(x);
    }
    @Override
    public Void copy(final File to) {
       this.x.copy(to);
      return null;
    }
    @Override
    public IUpdateable updateable(final File to) {
      return new IUpdateable(){
             public void update(){
                copy(to);
             }
          };
    }
    @Override
    public IProvider<Void> bind(final File to) {
      return new IProvider<java.lang.Void>(){
              public java.lang.Void get(){
                  copy(to);
          return null;
                  }
          };
    }
    @Override
    public Void apply(File p) {
      return f.apply(p);
    }
    @Override
    public IAcceptor<File> set(File p) {
      this.a.set(p);
      return this;
    }
  }
  // --------------------------------------------------------------------------------
  public final move_interface move;
  public final copy_interface copy;
  // --------------------------------------------------------------------------------

  public PathNotInWorkspaceHelperMenu2_m(PathNotInWorkspaceHelperMenu2 x) {
    move=new move_impl(x);
    copy=new copy_impl(x);
  }
}
