package field.apt.gen

import com.google.testing.compile.JavaFileObjects
import com.google.testing.compile.JavaSourceSubjectFactory
import field.apt.TestingProcessor
import javabuilder.JavaBuilder
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import org.truth0.Truth

import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

import static groovy.util.StringTestUtil.assertMultilineStringsEqual

/**
 * Created by jason on 7/11/14.
 */
class GCodeGeneratorTest {

    static Types types
    static Elements elements
    static ProcessingEnvironment env
    static TypeElement e
    static ExecutableElement m
    static ExecutableElement simpleGeneric
    static ExecutableElement trickyGeneric
    static ExecutableElement arrayMethod

    @BeforeClass
    static void initStatic() {
        def jfo = JavaFileObjects.forSourceString('some.pkg.MyIFace',
                                                  '''
package some.pkg;
import java.util.List;
import java.util.Date;
import field.bytecode.protect.annotations.GenerateMethods;
import field.bytecode.protect.annotations.Mirror;

@GenerateMethods
public interface MyIFace{
@Mirror public String added(int newSource,List<String> listOfStr,Date date);
@Mirror <T> void simpleGeneric(T t);
@Mirror <E extends Enum<E>> E trickyGeneric(E e);
@Mirror(prefix=\"dude\") void arrayMethod(float[][] array);
}
''')

        def p = new TestingProcessor()

        Truth.ASSERT
             .about(JavaSourceSubjectFactory.javaSource())
             .that(jfo)
             .processedWith(p)
             .compilesWithoutError()

        env = p.env
        types = p.types
        elements = p.elements
        e = p.element

        e.metaClass.findMethod = { name ->
            enclosedElements.find { it.kind == ElementKind.METHOD && it.simpleName.contentEquals(name) }
        }

        m = e.findMethod('added')
        simpleGeneric = e.findMethod('simpleGeneric')
        trickyGeneric = e.findMethod('trickyGeneric')
        arrayMethod = e.findMethod('arrayMethod')

        assert env
        assert types
        assert elements
        assert e
        assert m
        assert simpleGeneric
        assert trickyGeneric
        assert arrayMethod
    }

    @Test
    public void testGeneratorBase() throws Exception {
        def gen = new GeneratorBase(env, e)
        assert gen.isInterface
        assert gen.methods.size() == 4
        assert gen.fields.isEmpty()
        assert gen.generatedSimpleName == 'MyIFace_m'
        assert gen.packageName == 'some.pkg'
        assert gen.generatedFQN == 'some.pkg.MyIFace_m'
    }

    def methodFactory = new MethodElement.Factory(env)

    MethodElement enhance(ExecutableElement ex) {
        return methodFactory.create(ex)
    }

    @Test
    public void testMethodElementParams() throws Exception {
        def me = methodFactory.create(m)
        assert me.hasParams()
        assert ['int', 'java.util.List', 'java.util.Date'] == me.paramClassNames()
        assert me.hasReturnType()
        assert me.returnTypeName == 'String'
    }

    @Test
    public void testSimpleGenericMethod() throws Exception {
        def me = enhance(simpleGeneric)
        assert ['Object'] == me.paramClassNames()
    }

    @Test
    public void testTrickyGenericMethod() throws Exception {
        def me = enhance(trickyGeneric)
        assert ['Enum'] == me.paramClassNames()
    }

    @Test
    public void testArrayMethod() throws Exception {
        def me = enhance(arrayMethod)
        assert ['float[][]'] == me.paramClassNames()
    }

    @Test
    public void testPrefix() throws Exception {
        enhance(simpleGeneric).with {
            assert prefix == ''
            assert reflectionFieldName == 'simpleGeneric'
        }
        enhance(arrayMethod).with {
            assert prefix == 'dude'
            assert reflectionFieldName == 'dudearrayMethod'
        }
        new MethodElement.Factory(env, 'something').create(simpleGeneric).with {
            assert prefix == 'something'
            assert reflectionFieldName == 'somethingsimpleGeneric'
        }

    }

    @Test
    public void testGenerate() throws Exception {
        def sw = new StringWriter()
        //  def jw = new JavaWriterEx(sw)
        def jb = new JavaBuilder(sw)
        def gen = new GCodeGenerator(env, e)
        gen.javaBuilder = jb
        gen.javaWriter = jb.javaWriter

        gen.generate()

        def expect = '''
package some.pkg;

import field.bytecode.mirror.impl.*;
import field.launch.IUpdateable;
import field.math.abstraction.IAcceptor;
import field.namespace.generic.IFunction;
import field.namespace.generic.ReflectionTools;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
public class MyIFace_m {
  public static final Method added_m = ReflectionUtils.methodOf("added",some.pkg.MyIFace.class, int.class, java.util.List.class, java.util.Date.class);
  public static final Method simpleGeneric_m = ReflectionUtils.methodOf("simpleGeneric",some.pkg.MyIFace.class, Object.class);
  public static final Method trickyGeneric_m = ReflectionUtils.methodOf("trickyGeneric",some.pkg.MyIFace.class, Enum.class);
  public static final Method dudearrayMethod_m = ReflectionUtils.methodOf("arrayMethod",some.pkg.MyIFace.class, float[][].class);

  public static final MirrorMethod<MyIFace, String, Object[]> added_s = new MirrorMethod<some.pkg.MyIFace, java.lang.String, Object[]>(added_m);
  public static final MirrorNoReturnMethod<MyIFace, Object> simpleGeneric_s = new MirrorNoReturnMethod<some.pkg.MyIFace, java.lang.Object>(simpleGeneric_m);
  public static final MirrorMethod<MyIFace, Enum, Enum> trickyGeneric_s = new MirrorMethod<some.pkg.MyIFace, java.lang.Enum, java.lang.Enum>(trickyGeneric_m);
  public static final MirrorNoReturnMethod<MyIFace, Float[][]> dudearrayMethod_s = new MirrorNoReturnMethod<some.pkg.MyIFace, java.lang.Float[][]>(dudearrayMethod_m);

  public static interface added_interface
      implements IAcceptor<MyIFace>, IFunction<field.math.graph.visitors.hint.TraversalHint,MyIFace> {
    field.math.graph.visitors.hint.TraversalHint added(final int newSource, final List listOfStr, final Date date);
    IUpdateable updateable(final int newSource, final List listOfStr, final Date date);
    IProvider<field.math.graph.visitors.hint.TraversalHint> bind(final int newSource, final List listOfStr, final Date date);
  }
  public static interface simpleGeneric_interface
      implements IAcceptor<MyIFace>, IFunction<field.math.graph.visitors.hint.TraversalHint,MyIFace> {
    field.math.graph.visitors.hint.TraversalHint simpleGeneric(final Object t);
    IUpdateable updateable(final Object t);
    IProvider<field.math.graph.visitors.hint.TraversalHint> bind(final Object t);
  }
  public static interface trickyGeneric_interface
      implements IAcceptor<MyIFace>, IFunction<field.math.graph.visitors.hint.TraversalHint,MyIFace> {
    field.math.graph.visitors.hint.TraversalHint trickyGeneric(final Enum e);
    IUpdateable updateable(final Enum e);
    IProvider<field.math.graph.visitors.hint.TraversalHint> bind(final Enum e);
  }
  public static interface arrayMethod_interface
      implements IAcceptor<MyIFace>, IFunction<field.math.graph.visitors.hint.TraversalHint,MyIFace> {
    field.math.graph.visitors.hint.TraversalHint arrayMethod(final float[][] array);
    IUpdateable updateable(final float[][] array);
    IProvider<field.math.graph.visitors.hint.TraversalHint> bind(final float[][] array);
  }

  static class added_impl
      implements added_interface {
    final MyIFace x;
    final IAcceptor a;
    final IFunction f;
    added_impl(MyIFace x) {
      this.x=x;
      this.a=added_s.acceptor(x);
      this.f=added_s.function(x);
    }
    @Override
    public String added(final int newSource, final List listOfStr, final Date date) {
      return x.added(newSource,listOfStr,date);
    }
    @Override
    public IAcceptor<MyIFace> set(Object[] p) {
      a.set(p);
      return this;
    }
    @Override
    public field.math.graph.visitors.hint.TraversalHint apply(Object[] p) {
      return (field.math.graph.visitors.hint.TraversalHint) f.apply(p);
    }
    @Override
    public IUpdateable updateable(final int newSource, final List listOfStr, final Date date) {
      return new IUpdateable(){
             public void update(){
                added(newSource,listOfStr,date);
             }
          };
    }
    @Override
    public IProvider<field.math.graph.visitors.hint.TraversalHint> bind(final int newSource, final List listOfStr, final Date date) {
      return new IProvider(){
              public Object get(){
                  return added(newSource,listOfStr,date)
                  }
          };
    }
  }
  static class simpleGeneric_impl
      implements simpleGeneric_interface {
    final MyIFace x;
    final IAcceptor a;
    final IFunction f;
    simpleGeneric_impl(MyIFace x) {
      this.x=x;
      this.a=simpleGeneric_s.acceptor(x);
      this.f=simpleGeneric_s.function(x);
    }
    @Override
    public void simpleGeneric(final Object t) {
       x.simpleGeneric(t);
    }
    @Override
    public IAcceptor<MyIFace> set(Object p) {
      a.set(p);
      return this;
    }
    @Override
    public field.math.graph.visitors.hint.TraversalHint apply(Object p) {
      return (field.math.graph.visitors.hint.TraversalHint) f.apply(p);
    }
    @Override
    public IUpdateable updateable(final Object t) {
      return new IUpdateable(){
             public void update(){
                simpleGeneric(t);
             }
          };
    }
    @Override
    public IProvider<field.math.graph.visitors.hint.TraversalHint> bind(final Object t) {
      return new IProvider(){
              public Object get(){
                  simpleGeneric(t);
          return null;
                  }
          };
    }
  }
  static class trickyGeneric_impl
      implements trickyGeneric_interface {
    final MyIFace x;
    final IAcceptor a;
    final IFunction f;
    trickyGeneric_impl(MyIFace x) {
      this.x=x;
      this.a=trickyGeneric_s.acceptor(x);
      this.f=trickyGeneric_s.function(x);
    }
    @Override
    public Enum trickyGeneric(final Enum e) {
      return x.trickyGeneric(e);
    }
    @Override
    public IAcceptor<MyIFace> set(Enum p) {
      a.set(p);
      return this;
    }
    @Override
    public field.math.graph.visitors.hint.TraversalHint apply(Enum p) {
      return (field.math.graph.visitors.hint.TraversalHint) f.apply(p);
    }
    @Override
    public IUpdateable updateable(final Enum e) {
      return new IUpdateable(){
             public void update(){
                trickyGeneric(e);
             }
          };
    }
    @Override
    public IProvider<field.math.graph.visitors.hint.TraversalHint> bind(final Enum e) {
      return new IProvider(){
              public Object get(){
                  return trickyGeneric(e)
                  }
          };
    }
  }
  static class arrayMethod_impl
      implements arrayMethod_interface {
    final MyIFace x;
    final IAcceptor a;
    final IFunction f;
    arrayMethod_impl(MyIFace x) {
      this.x=x;
      this.a=arrayMethod_s.acceptor(x);
      this.f=arrayMethod_s.function(x);
    }
    @Override
    public void arrayMethod(final float[][] array) {
       x.arrayMethod(array);
    }
    @Override
    public IAcceptor<MyIFace> set(Float[][] p) {
      a.set(p);
      return this;
    }
    @Override
    public field.math.graph.visitors.hint.TraversalHint apply(Float[][] p) {
      return (field.math.graph.visitors.hint.TraversalHint) f.apply(p);
    }
    @Override
    public IUpdateable updateable(final float[][] array) {
      return new IUpdateable(){
             public void update(){
                arrayMethod(array);
             }
          };
    }
    @Override
    public IProvider<field.math.graph.visitors.hint.TraversalHint> bind(final float[][] array) {
      return new IProvider(){
              public Object get(){
                  arrayMethod(array);
          return null;
                  }
          };
    }
  }

  public final added_interface added;
  public final simpleGeneric_interface simpleGeneric;
  public final trickyGeneric_interface trickyGeneric;
  public final arrayMethod_interface arrayMethod;


  public MyIFace_m(MyIFace x) {
    added=new added_impl(x);
    simpleGeneric=new simpleGeneric_impl(x);
    trickyGeneric=new trickyGeneric_impl(x);
    arrayMethod=new arrayMethod_impl(x);
  }
}
'''
        try {
            assertMultilineStringsEqual(expect, sw.toString())
        } catch (Throwable ignored) {
            Assert.assertEquals(expect, sw.toString())
        }
    }
}
