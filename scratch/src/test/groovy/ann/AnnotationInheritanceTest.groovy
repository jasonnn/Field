package ann

import field.bytecode.protect.Woven
import org.junit.Test

import javax.lang.model.element.TypeElement

import static ann.TestingProcessor.fromClosure

/**
 * Created by jason on 7/16/14.
 */
class AnnotationInheritanceTest extends ProcessorTestCase {
    @Test
    public void testGetWoven() throws Exception {

        TypeElement woven

        run processor: fromClosure {

            println roundEnv.getElementsAnnotatedWith(Woven)
            if (firstRun) woven = elements.getTypeElement(Woven.name)


        }

        println woven.asType()

    }
}
