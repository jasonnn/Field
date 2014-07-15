package field.apt;

import org.jetbrains.annotations.NotNull;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Set;

/**
 * Created by jason on 7/10/14.
 */
public abstract class BaseProcessor implements Processor {


    @NotNull
    @Override
    public final Set<String> getSupportedOptions() {
        return Collections.emptySet();
    }

    @NotNull
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton("*");
    }

    @Override
    public final SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    protected ProcessingEnvironment env;
    protected Filer filer;
    protected Elements elements;
    protected Types types;
    protected Messager log;

    @Override
    public void init(@NotNull ProcessingEnvironment processingEnv) {
        this.env = processingEnv;
        this.filer = processingEnv.getFiler();
        this.elements = processingEnv.getElementUtils();
        this.types = processingEnv.getTypeUtils();
        this.log = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        boolean claimed = false;
        try {
            //noinspection unchecked
            claimed = doProcess((Set<TypeElement>) annotations, roundEnv);
        } catch (Exception e) {
            handleException(e);
        }
        return claimed;
    }


    protected boolean doProcess(Set<TypeElement> annotations, RoundEnvironment roundEnv) throws Exception {
        return doProcess(roundEnv);
    }

    protected boolean doProcess(RoundEnvironment roundEnv) throws Exception {
        return CLAIM;
    }

    @NotNull
    @Override
    public final Iterable<? extends Completion> getCompletions(Element element, AnnotationMirror annotation, ExecutableElement member, String userText) {
        return Collections.emptySet();
    }


    protected void handleException(Exception e) {
        env.getMessager().printMessage(Diagnostic.Kind.ERROR, stackTrace(e));
    }

    protected static String stackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    protected static final boolean CLAIM = true;
    protected static final boolean DONT_CLAIM = false;
}
