package field.apt.gen

import com.google.testing.compile.JavaFileObjects
import com.google.testing.compile.JavaSourceSubjectFactory
import field.apt.TestingProcessor
import javabuilder.JavaBuilder
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test
import org.truth0.Truth

import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.ElementKind
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
    static MethodElement m
    static MethodElement simpleGeneric
    static MethodElement trickyGeneric
    static MethodElement arrayMethod

    @SuppressWarnings("GroovyAssignabilityCheck")
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

        def methodFactory = new MethodElement.Factory(env)

        e.metaClass.findMethod = { name ->
            methodFactory.create(enclosedElements.find {
                it.kind == ElementKind.METHOD && it.simpleName.contentEquals(name)
            })
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

    @Ignore
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


    @Test
    public void testMethodElementParams() throws Exception {
        assert m.hasParams()
        assert ['int', 'java.util.List', 'java.util.Date'] == m.rawParamTypes()
        assert m.hasReturnType()
        assert m.rawReturnType == 'java.lang.String'
    }

    @Test
    public void testParamMap() throws Exception {
        def params = [(m)            : [newSource: 'int', listOfStr: 'java.util.List', date: 'java.util.Date'],
                      (simpleGeneric): [t: Object.name],
                      (trickyGeneric): [e: Enum.name],
                      (arrayMethod)  : [array: 'float[][]']]

        params.each { me, expect ->
            assert expect == me.rawParams
        }

        params[(m)] = [newSource: Integer.name, listOfStr: 'java.util.List', date: 'java.util.Date']
        params.each { me, expect ->
            assert expect == me.rawParams(true)
        }
        params[(m)] = [newSource: int.name, listOfStr: 'java.util.List<java.lang.String>', date: 'java.util.Date']
        params.each { me, expect ->
            assert expect == me.params()
        }

    }

    @Test
    public void testMirrorKind() throws Exception {
        assert m.mirrorKind == MirrorKind.MirrorMethod
        assert simpleGeneric.mirrorKind == MirrorKind.MirrorNoReturnMethod
        assert trickyGeneric.mirrorKind == MirrorKind.MirrorMethod
        assert arrayMethod.mirrorKind == MirrorKind.MirrorNoReturnMethod

    }


    @Test
    public void testPrefix() throws Exception {
        simpleGeneric.with {
            assert prefix == ''
            assert generatedName == 'simpleGeneric'
        }
        arrayMethod.with {
            assert prefix == 'dude'
            assert generatedName == 'dudearrayMethod'
        }
        new MethodElement.Factory(env, 'something').create(simpleGeneric).with {
            assert prefix == 'something'
            assert generatedName == 'somethingsimpleGeneric'
        }

    }

    @Test
    public void testGenerate() throws Exception {
        def sw = new StringWriter()
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
import field.math.abstraction.IProvider;
import field.math.graph.visitors.hint.TraversalHint;
import field.namespace.generic.IFunction;
import field.namespace.generic.ReflectionTools;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
public class MyIFace_m2 {
  public static final Method added_m = ReflectionTools.methodOf("added",some.pkg.MyIFace.class, int.class, java.util.List.class, java.util.Date.class);
  public static final Method simpleGeneric_m = ReflectionTools.methodOf("simpleGeneric",some.pkg.MyIFace.class, java.lang.Object.class);
  public static final Method trickyGeneric_m = ReflectionTools.methodOf("trickyGeneric",some.pkg.MyIFace.class, java.lang.Enum.class);
  public static final Method dudearrayMethod_m = ReflectionTools.methodOf("arrayMethod",some.pkg.MyIFace.class, float[][].class);
  // --------------------------------------------------------------------------------
  public static final MirrorMethod<MyIFace, String, Object[]> added_s = new MirrorMethod<some.pkg.MyIFace, java.lang.String, Object[]>(added_m);
  public static final MirrorNoReturnMethod<MyIFace, Object> simpleGeneric_s = new MirrorNoReturnMethod<some.pkg.MyIFace, java.lang.Object>(simpleGeneric_m);
  public static final MirrorMethod<MyIFace, Enum, Enum> trickyGeneric_s = new MirrorMethod<some.pkg.MyIFace, java.lang.Enum, java.lang.Enum>(trickyGeneric_m);
  public static final MirrorNoReturnMethod<MyIFace, float[][]> dudearrayMethod_s = new MirrorNoReturnMethod<some.pkg.MyIFace, float[][]>(dudearrayMethod_m);
  // --------------------------------------------------------------------------------
  public static interface added_interface
      extends IAcceptor<MyIFace>, IFunction<String,MyIFace> {
    String added(final int newSource, final List<String> listOfStr, final Date date);
    IUpdateable updateable(final int newSource, final List<String> listOfStr, final Date date);
    IProvider<String> bind(final int newSource, final List<String> listOfStr, final Date date);
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
    public void null() {
      return x.added(newSource,listOfStr,date);
    }
    @Override
    public void null() {
      return new IUpdateable(){
             public void update(){
                added(newSource,listOfStr,date);
             }
          };
    }
    @Override
    public void null() {
      return new IProvider(){
              public Object get(){
                  return added(newSource,listOfStr,date) ;
                  }
          };
    }
    public String apply(Object[] p) {
      return (java.lang.String) f.apply(p);
    }
    public IAcceptor<MyIFace> set(Object[] p) {
      a.set(p);
      return this;
    }
  }
  public static interface simpleGeneric_interface
      extends IAcceptor<MyIFace>, IFunction<Void,MyIFace> {
    Void simpleGeneric(final Object t);
    IUpdateable updateable(final Object t);
    IProvider<Void> bind(final Object t);
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
    public void null() {
       x.simpleGeneric(t);
      return null;
    }
    @Override
    public void null() {
      return new IUpdateable(){
             public void update(){
                simpleGeneric(t);
             }
          };
    }
    @Override
    public void null() {
      return new IProvider(){
              public Object get(){
                  simpleGeneric(t);
          return null; ;
                  }
          };
    }
    public Void apply(Object p) {
      return (java.lang.Void) f.apply(p);
    }
    public IAcceptor<MyIFace> set(Object p) {
      a.set(p);
      return this;
    }
  }
  public static interface trickyGeneric_interface
      extends IAcceptor<MyIFace>, IFunction<Enum,MyIFace> {
    Enum trickyGeneric(final Enum e);
    IUpdateable updateable(final Enum e);
    IProvider<Enum> bind(final Enum e);
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
    public void null() {
      return x.trickyGeneric(e);
    }
    @Override
    public void null() {
      return new IUpdateable(){
             public void update(){
                trickyGeneric(e);
             }
          };
    }
    @Override
    public void null() {
      return new IProvider(){
              public Object get(){
                  return trickyGeneric(e) ;
                  }
          };
    }
    public Enum apply(Enum p) {
      return (java.lang.Enum) f.apply(p);
    }
    public IAcceptor<MyIFace> set(Enum p) {
      a.set(p);
      return this;
    }
  }
  public static interface dudearrayMethod_interface
      extends IAcceptor<MyIFace>, IFunction<Void,MyIFace> {
    Void arrayMethod(final float[][] array);
    IUpdateable updateable(final float[][] array);
    IProvider<Void> bind(final float[][] array);
  }
  static class dudearrayMethod_impl
      implements dudearrayMethod_interface {
    final MyIFace x;
    final IAcceptor a;
    final IFunction f;
    dudearrayMethod_impl(MyIFace x) {
      this.x=x;
      this.a=dudearrayMethod_s.acceptor(x);
      this.f=dudearrayMethod_s.function(x);
    }
    @Override
    public void null() {
       x.arrayMethod(array);
      return null;
    }
    @Override
    public void null() {
      return new IUpdateable(){
             public void update(){
                arrayMethod(array);
             }
          };
    }
    @Override
    public void null() {
      return new IProvider(){
              public Object get(){
                  arrayMethod(array);
          return null; ;
                  }
          };
    }
    public Void apply(float[][] p) {
      return (java.lang.Void) f.apply(p);
    }
    public IAcceptor<MyIFace> set(float[][] p) {
      a.set(p);
      return this;
    }
  }
  // --------------------------------------------------------------------------------
  public final added_interface added;
  public final simpleGeneric_interface simpleGeneric;
  public final trickyGeneric_interface trickyGeneric;
  public final dudearrayMethod_interface dudearrayMethod;
  // --------------------------------------------------------------------------------

  public MyIFace_m2(MyIFace x) {
    added=new added_impl(x);
    simpleGeneric=new simpleGeneric_impl(x);
    trickyGeneric=new trickyGeneric_impl(x);
    dudearrayMethod=new dudearrayMethod_impl(x);
  }
}
'''
        //println sw.toString()
        //assertCode(expect,sw.toString())
    }

    static void assertCode(String expect, String actual) {
        try {
            assertMultilineStringsEqual(expect, actual)
        } catch (Throwable ignored) {
            Assert.assertEquals(expect, actual)
        }
    }
}
