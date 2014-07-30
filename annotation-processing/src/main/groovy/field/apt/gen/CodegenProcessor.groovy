package field.apt.gen

import field.apt.BaseProcessor
import field.bytecode.protect.annotations.GenerateMethods
import field.bytecode.protect.annotations.Mirror

import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.TypeElement

/**
 * Created by jason on 7/29/14.
 */
class CodegenProcessor extends BaseProcessor {


    @Override
    Set<String> getSupportedAnnotationTypes() {
        return [GenerateMethods.name, Mirror.name]
    }

    @Override
    protected boolean doProcess(Set<TypeElement> annotations, RoundEnvironment roundEnv) throws Exception {
        def toProcess = roundEnv.getElementsAnnotatedWith(GenerateMethods)

        for (te in toProcess) {
            def codeGen = new GCodeGenerator(env, te as TypeElement)
            try{
                codeGen.generate()
            }
            finally{
                codeGen.close()
            }
        }

        return super.doProcess(annotations, roundEnv)
    }
}
