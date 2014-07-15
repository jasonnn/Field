package field.apt

import com.google.testing.compile.JavaFileObjects
import com.google.testing.compile.JavaSourceSubjectFactory
import field.apt.gen.GCodeGenerator
import field.apt.gen.GeneratorBase
import field.apt.util.GenUtils
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
class SomeTest {

    static Types types
    static Elements elements
    static ProcessingEnvironment env
    static TypeElement e
    static ExecutableElement m

    @BeforeClass
    static void initStatic() {
        def jfo = JavaFileObjects.forSourceString('some.pkg.MyIFace',
                '''
package some.pkg;
import java.util.List;
import field.bytecode.protect.annotations.GenerateMethods;
import field.bytecode.protect.annotations.Mirror;

@GenerateMethods
public interface MyIFace{
@Mirror public String added(int newSource,List<String> listOfStr);
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
        m = e.enclosedElements.find {
            it.kind == ElementKind.METHOD && it.simpleName.contentEquals('added')
        } as ExecutableElement

        assert env
        assert types
        assert elements
        assert e
        assert m
    }

    @Test
    public void testGeneratorBase() throws Exception {
        def gen = GCodeGenerator.create(e, env) as GeneratorBase
        assert gen.isInterface
        assert gen.methods.size() == 1
        assert gen.fields.isEmpty()
    }

    @Test
    public void testPkgName() throws Exception {
        assert 'some.pkg' == GenUtils.packageNameOf(e)
        def cg = GCodeGenerator.create(e, env)
    }

}
