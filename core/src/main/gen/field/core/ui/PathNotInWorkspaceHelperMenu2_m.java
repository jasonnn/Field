package field.core.ui;

import field.bytecode.apt.Mirroring;
import field.launch.iUpdateable;
import field.math.abstraction.iAcceptor;
import field.namespace.generic.Bind.iFunction;
import field.namespace.generic.ReflectionTools;

import java.lang.reflect.Method;
public class PathNotInWorkspaceHelperMenu2_m {
public static final Method move_m = ReflectionTools.methodOf("move", field.core.ui.PathNotInWorkspaceHelperMenu2.class, java.io.File.class);
public static final Mirroring.MirrorNoReturnMethod<field.core.ui.PathNotInWorkspaceHelperMenu2, java.io.File> move_s = new Mirroring.MirrorNoReturnMethod<field.core.ui.PathNotInWorkspaceHelperMenu2, java.io.File>(field.core.ui.PathNotInWorkspaceHelperMenu2.class, "move", new Class[]{java.io.File.class});

public interface move_interface extends iAcceptor<java.io.File>, iFunction<Object ,java.io.File >
	{
		public void move( final java.io.File p0);
	public iUpdateable updateable(final java.io.File p0);}

public final move_interface move;

public static final Method copy_m = ReflectionTools.methodOf("copy", field.core.ui.PathNotInWorkspaceHelperMenu2.class, java.io.File.class);
public static final Mirroring.MirrorNoReturnMethod<field.core.ui.PathNotInWorkspaceHelperMenu2, java.io.File> copy_s = new Mirroring.MirrorNoReturnMethod<field.core.ui.PathNotInWorkspaceHelperMenu2, java.io.File>(field.core.ui.PathNotInWorkspaceHelperMenu2.class, "copy", new Class[]{java.io.File.class});

public interface copy_interface extends iAcceptor<java.io.File>, iFunction<Object ,java.io.File >
	{
		public void copy( final java.io.File p0);
	public iUpdateable updateable(final java.io.File p0);}

public final copy_interface copy;

public PathNotInWorkspaceHelperMenu2_m(final PathNotInWorkspaceHelperMenu2 x) {
		move = new move_interface()
		{
			
			iAcceptor a = move_s.acceptor(x);
			iFunction f = move_s.function(x);

			
			public void move (final java.io.File p0)
			{
				 x.move(p0 );
			}
			
			public iAcceptor<java.io.File> set(java.io.File p)
			{
				a.set(p);
				return this;
			}
			
			public Object f(java.io.File p)
			{
                return f.f(p);
            }
			
		public iUpdateable updateable(final java.io.File p0)
	{
		return new iUpdateable()
		{
			public void update()
			{
				move(p0);
			}
		};
	}
		};

		copy = new copy_interface()
		{
			
			iAcceptor a = copy_s.acceptor(x);
			iFunction f = copy_s.function(x);

			
			public void copy (final java.io.File p0)
			{
				 x.copy(p0 );
			}
			
			public iAcceptor<java.io.File> set(java.io.File p)
			{
				a.set(p);
				return this;
			}
			
			public Object f(java.io.File p)
			{
                return f.f(p);
            }
			
		public iUpdateable updateable(final java.io.File p0)
	{
		return new iUpdateable()
		{
			public void update()
			{
				copy(p0);
			}
		};
	}
		};


}
}

