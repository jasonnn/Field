package field.apt.gen

import com.google.testing.compile.JavaFileObjects
import com.google.testing.compile.JavaSourceSubjectFactory
import field.apt.TestingProcessor
import javabuilder.JavaBuilder
import javabuilder.delegates.Handlers
import javabuilder.writer.JavaWriterEx
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
        def jw = new JavaWriterEx(sw)
        def jb = new JavaBuilder(new Handlers(jw))
        def gen = new GCodeGenerator(env, e)
        gen.javaBuilder = jb
        gen.javaWriter = jw

        gen.generate()

        def expect = '''
package some.pkg;

import field.bytecode.apt.*;
import field.launch.*;
import field.math.abstraction.*;
import field.namespace.generic.Bind.*;
import field.namespace.generic.ReflectionTools;
import java.lang.reflect.*;
import java.util.*;
public class MyIFace_m {

  public static final java.lang.reflect.Method added_m = ReflectionUtils.methodOf("added",some.pkg.MyIFace.class, int.class, java.util.List.class, java.util.Date.class);
  public static final Mirroring.MirrorMethod<MyIFace, field.math.graph.GraphNodeSearching.VisitCode, MyIFace> added_s = new Mirroring.MirrorMethod<some.pkg.MyIFace, field.math.graph.GraphNodeSearching.VisitCode, some.pkg.MyIFace>(some.pkg.MyIFace.class,"added",new Class[]{int.class, java.util.List.class, java.util.Date.class});
  public static interface added_interface
      implements iAcceptor<MyIFace>, iFunction<field.math.graph.GraphNodeSearching.VisitCode,MyIFace> {
    field.math.graph.GraphNodeSearching.VisitCode added(field.core.dispatch.iVisualElement p0);
    iUpdateable updateable(field.core.dispatch.iVisualElement p0);
    iProvider<field.math.graph.GraphNodeSearching.VisitCode> bind(field.core.dispatch.iVisualElement p0);
  }
  static class added_impl
      implements added_interface {
    final MyIFace x;
    final iAcceptor a;
    final iFunction f;
    added_impl(MyIFace x) {
      this.x=x;
      this.a=added_s.acceptor(x);
      this.f=added_s.function(x);
    }
    @Override
    public field.math.graph.GraphNodeSearching.VisitCode added(field.core.dispatch.iVisualElement p0) {
      return x.added(p0);
    }
    @Override
    public iAcceptor<field.core.dispatch.iVisualElement> set(field.core.dispatch.iVisualElement p) {
      a.set(p);
      return this;
    }
    @Override
    public field.math.graph.GraphNodeSearching.VisitCode f(field.core.dispatch.iVisualElement p) {
      return (field.math.graph.GraphNodeSearching.VisitCode) f.f(p);
    }
    @Override
    public iUpdateable updateable(field.core.dispatch.iVisualElement p) {

          return new iUpdateable(){
             public void update(){
                 added(p);
             }
          };
    }
    @Override
    public iProvider<field.math.graph.GraphNodeSearching.VisitCode> bind(field.core.dispatch.iVisualElement p) {
      return new iProvider(){
              public Object get(){
                  return added(p0);
                  }
          };
    }
  }

  public static final java.lang.reflect.Method simpleGeneric_m = ReflectionUtils.methodOf("simpleGeneric",some.pkg.MyIFace.class, Object.class);
  public static final Mirroring.MirrorMethod<MyIFace, field.math.graph.GraphNodeSearching.VisitCode, MyIFace> simpleGeneric_s = new Mirroring.MirrorMethod<some.pkg.MyIFace, field.math.graph.GraphNodeSearching.VisitCode, some.pkg.MyIFace>(some.pkg.MyIFace.class,"simpleGeneric",new Class[]{Object.class});
  public static interface simpleGeneric_interface
      implements iAcceptor<MyIFace>, iFunction<field.math.graph.GraphNodeSearching.VisitCode,MyIFace> {
    field.math.graph.GraphNodeSearching.VisitCode simpleGeneric(field.core.dispatch.iVisualElement p0);
    iUpdateable updateable(field.core.dispatch.iVisualElement p0);
    iProvider<field.math.graph.GraphNodeSearching.VisitCode> bind(field.core.dispatch.iVisualElement p0);
  }
  static class simpleGeneric_impl
      implements simpleGeneric_interface {
    final MyIFace x;
    final iAcceptor a;
    final iFunction f;
    simpleGeneric_impl(MyIFace x) {
      this.x=x;
      this.a=simpleGeneric_s.acceptor(x);
      this.f=simpleGeneric_s.function(x);
    }
    @Override
    public field.math.graph.GraphNodeSearching.VisitCode simpleGeneric(field.core.dispatch.iVisualElement p0) {
      return x.simpleGeneric(p0);
    }
    @Override
    public iAcceptor<field.core.dispatch.iVisualElement> set(field.core.dispatch.iVisualElement p) {
      a.set(p);
      return this;
    }
    @Override
    public field.math.graph.GraphNodeSearching.VisitCode f(field.core.dispatch.iVisualElement p) {
      return (field.math.graph.GraphNodeSearching.VisitCode) f.f(p);
    }
    @Override
    public iUpdateable updateable(field.core.dispatch.iVisualElement p) {

          return new iUpdateable(){
             public void update(){
                 simpleGeneric(p);
             }
          };
    }
    @Override
    public iProvider<field.math.graph.GraphNodeSearching.VisitCode> bind(field.core.dispatch.iVisualElement p) {
      return new iProvider(){
              public Object get(){
                  return added(p0);
                  }
          };
    }
  }

  public static final java.lang.reflect.Method trickyGeneric_m = ReflectionUtils.methodOf("trickyGeneric",some.pkg.MyIFace.class, Enum.class);
  public static final Mirroring.MirrorMethod<MyIFace, field.math.graph.GraphNodeSearching.VisitCode, MyIFace> trickyGeneric_s = new Mirroring.MirrorMethod<some.pkg.MyIFace, field.math.graph.GraphNodeSearching.VisitCode, some.pkg.MyIFace>(some.pkg.MyIFace.class,"trickyGeneric",new Class[]{Enum.class});
  public static interface trickyGeneric_interface
      implements iAcceptor<MyIFace>, iFunction<field.math.graph.GraphNodeSearching.VisitCode,MyIFace> {
    field.math.graph.GraphNodeSearching.VisitCode trickyGeneric(field.core.dispatch.iVisualElement p0);
    iUpdateable updateable(field.core.dispatch.iVisualElement p0);
    iProvider<field.math.graph.GraphNodeSearching.VisitCode> bind(field.core.dispatch.iVisualElement p0);
  }
  static class trickyGeneric_impl
      implements trickyGeneric_interface {
    final MyIFace x;
    final iAcceptor a;
    final iFunction f;
    trickyGeneric_impl(MyIFace x) {
      this.x=x;
      this.a=trickyGeneric_s.acceptor(x);
      this.f=trickyGeneric_s.function(x);
    }
    @Override
    public field.math.graph.GraphNodeSearching.VisitCode trickyGeneric(field.core.dispatch.iVisualElement p0) {
      return x.trickyGeneric(p0);
    }
    @Override
    public iAcceptor<field.core.dispatch.iVisualElement> set(field.core.dispatch.iVisualElement p) {
      a.set(p);
      return this;
    }
    @Override
    public field.math.graph.GraphNodeSearching.VisitCode f(field.core.dispatch.iVisualElement p) {
      return (field.math.graph.GraphNodeSearching.VisitCode) f.f(p);
    }
    @Override
    public iUpdateable updateable(field.core.dispatch.iVisualElement p) {

          return new iUpdateable(){
             public void update(){
                 trickyGeneric(p);
             }
          };
    }
    @Override
    public iProvider<field.math.graph.GraphNodeSearching.VisitCode> bind(field.core.dispatch.iVisualElement p) {
      return new iProvider(){
              public Object get(){
                  return added(p0);
                  }
          };
    }
  }

  public static final java.lang.reflect.Method dudearrayMethod_m = ReflectionUtils.methodOf("arrayMethod",some.pkg.MyIFace.class, float[][].class);
  public static final Mirroring.MirrorMethod<MyIFace, field.math.graph.GraphNodeSearching.VisitCode, MyIFace> dudearrayMethod_s = new Mirroring.MirrorMethod<some.pkg.MyIFace, field.math.graph.GraphNodeSearching.VisitCode, some.pkg.MyIFace>(some.pkg.MyIFace.class,"arrayMethod",new Class[]{float[][].class});
  public static interface arrayMethod_interface
      implements iAcceptor<MyIFace>, iFunction<field.math.graph.GraphNodeSearching.VisitCode,MyIFace> {
    field.math.graph.GraphNodeSearching.VisitCode arrayMethod(field.core.dispatch.iVisualElement p0);
    iUpdateable updateable(field.core.dispatch.iVisualElement p0);
    iProvider<field.math.graph.GraphNodeSearching.VisitCode> bind(field.core.dispatch.iVisualElement p0);
  }
  static class arrayMethod_impl
      implements arrayMethod_interface {
    final MyIFace x;
    final iAcceptor a;
    final iFunction f;
    arrayMethod_impl(MyIFace x) {
      this.x=x;
      this.a=arrayMethod_s.acceptor(x);
      this.f=arrayMethod_s.function(x);
    }
    @Override
    public field.math.graph.GraphNodeSearching.VisitCode arrayMethod(field.core.dispatch.iVisualElement p0) {
      return x.arrayMethod(p0);
    }
    @Override
    public iAcceptor<field.core.dispatch.iVisualElement> set(field.core.dispatch.iVisualElement p) {
      a.set(p);
      return this;
    }
    @Override
    public field.math.graph.GraphNodeSearching.VisitCode f(field.core.dispatch.iVisualElement p) {
      return (field.math.graph.GraphNodeSearching.VisitCode) f.f(p);
    }
    @Override
    public iUpdateable updateable(field.core.dispatch.iVisualElement p) {

          return new iUpdateable(){
             public void update(){
                 arrayMethod(p);
             }
          };
    }
    @Override
    public iProvider<field.math.graph.GraphNodeSearching.VisitCode> bind(field.core.dispatch.iVisualElement p) {
      return new iProvider(){
              public Object get(){
                  return added(p0);
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
        } catch (Throwable ignored){
            Assert.assertEquals(expect,sw.toString())
        }
    }
}
