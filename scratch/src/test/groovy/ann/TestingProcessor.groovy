package ann

import javax.annotation.processing.Completion
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

/**
 * Created by jason on 7/11/14.
 */
class TestingProcessor implements Processor {
    public static Processor fromClosure(@DelegatesTo(TestingProcessor.class) Closure<?> closure) {
        return new TestingProcessor(closure)
    }

    TestingProcessor() {}

    TestingProcessor(Closure closure) {
        this.closure = closure
        closure.delegate = this
        closure.resolveStrategy = Closure.DELEGATE_FIRST
    }

    private Closure<?> closure

    def Types types
    def Elements elements
    def ProcessingEnvironment env
    Set<? extends TypeElement> annotations
    RoundEnvironment roundEnv

    @Override
    Set<String> getSupportedOptions() {
        return Collections.emptySet()
    }

    @Override
    Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton('*')
    }

    @Override
    SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest()
    }


    @Override
    void init(ProcessingEnvironment processingEnv) {
        elements = processingEnv.elementUtils
        types = processingEnv.typeUtils
        env = processingEnv
    }

    boolean firstRun = true

    @Override
    boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        this.annotations = annotations
        this.roundEnv = roundEnv
        if (closure) closure.call(this)
        firstRun = false
        return false
    }


    @Override
    Iterable<? extends Completion> getCompletions(Element element, AnnotationMirror annotation, ExecutableElement member, String userText) {
        return Collections.emptySet()
    }
}
