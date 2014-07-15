package field.apt;

import field.apt.util.GenUtils;
import field.bytecode.protect.annotations.GenerateMethods;
import org.jetbrains.annotations.NotNull;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Collections;
import java.util.Set;

/**
 * Created by jason on 7/10/14.
 */
public class GenMethodsProcessor extends BaseProcessor {


    @Override
    protected boolean doProcess(Set<TypeElement> annotations, RoundEnvironment roundEnv) throws Exception {
        if (annotations.isEmpty()) return DONT_CLAIM;
        check(annotations);
        return super.doProcess(annotations, roundEnv);
    }

    @Override
    protected boolean doProcess(RoundEnvironment roundEnv) throws Exception {
        for (Element e : roundEnv.getElementsAnnotatedWith(GenerateMethods.class)) {
            CodeGen gen = GenUtils.generatorBuilderFor(e).withEnv(env);
            gen.generate();
        }
        return CLAIM;
    }

    private static void check(Set<TypeElement> annotations) {
        if (annotations.size() != 1) throw new IllegalArgumentException("wrong number of annotations!");
        TypeElement ann = annotations.iterator().next();
        if (!ann.getQualifiedName().contentEquals(GenerateMethods.class.getName())) {
            throw new IllegalArgumentException("wrong annotation!");
        }
    }

    @Override
    public void init(@NotNull ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
    }

    @NotNull
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(GenerateMethods.class.getName());
    }


}
