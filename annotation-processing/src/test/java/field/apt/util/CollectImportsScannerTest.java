package field.apt.util;

import field.apt.TestingProcessor;
import field.bytecode.protect.annotations.GenerateMethods;
import org.junit.Test;
import org.truth0.Truth;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

import static com.google.testing.compile.JavaFileObjects.forResource;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static org.junit.Assert.assertTrue;

public
class CollectImportsScannerTest {
    static
    class CollectImportsProcessor extends TestingProcessor implements Processor {
        public boolean found;

        Set<String> deps;

        @Override
        public
        boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
            for (Element e : roundEnv.getElementsAnnotatedWith(GenerateMethods.class)) {
                if (e.getSimpleName().contentEquals("ILine")) {
                    found = true;
                    deps = CollectImportsScanner.getVisibleDependencies(e);
                }
            }
            return true;
        }


    }

    @Test
    public
    void testImportsAreCollected() throws Exception {
        CollectImportsProcessor processor = new CollectImportsProcessor();
        Truth.ASSERT.about(javaSource())
                    .that(forResource(CollectImportsScannerTest.class.getResource("/tst/ILine.java")))
                    .processedWith(processor)
                    .compilesWithoutError();

        assertTrue(processor.found);
        System.out.println("processor.deps = " + processor.deps);


    }
}