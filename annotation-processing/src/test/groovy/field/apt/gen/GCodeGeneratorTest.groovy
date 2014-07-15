package field.apt.gen

import com.google.testing.compile.JavaFileObjects
import com.google.testing.compile.JavaSourceSubjectFactory
import field.apt.TestingProcessor
import org.junit.BeforeClass
import org.junit.Test
import org.truth0.Truth

import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

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
    MethodElement enhance(ExecutableElement ex){
        return methodFactory.create(ex)
    }

    @Test
    public void testMethodElementParams() throws Exception {
        def me = methodFactory.create(m)
        assert me.hasParams()
        assert ['int', 'java.util.List', 'java.util.Date'] == me.paramClassNames()
        assert me.hasReturnType()
        assert me.returnTypeName=='String'
    }

    @Test
    public void testSimpleGenericMethod() throws Exception {
        def me =enhance(simpleGeneric)
        assert ['Object'] == me.paramClassNames()
    }

    @Test
    public void testTrickyGenericMethod() throws Exception {
        def me =enhance(trickyGeneric)
        assert ['Enum']==me.paramClassNames()
    }

    @Test
    public void testArrayMethod() throws Exception {
        def me =enhance(arrayMethod)
        assert ['float[][]']==me.paramClassNames()
    }

    @Test
    public void testPrefix() throws Exception {
       enhance(simpleGeneric).with{
           assert prefix==''
           assert reflectionFieldName=='simpleGeneric'
       }
       enhance(arrayMethod).with {
           assert prefix=='dude'
           assert reflectionFieldName=='dudearrayMethod'
       }
        new MethodElement.Factory(env,'something').create(simpleGeneric).with {
            assert prefix=='something'
            assert reflectionFieldName=='somethingsimpleGeneric'
        }


    }
}
