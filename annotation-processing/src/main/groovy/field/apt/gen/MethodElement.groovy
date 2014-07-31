package field.apt.gen

import field.apt.util.GenUtils
import field.bytecode.protect.annotations.Mirror

import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeKind
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Created by jason on 7/12/14.
 */

class MethodElement implements ExecutableElement {
    private static final Logger log = Logger.getLogger(MethodElement.name)

    public static class Factory {
        final Types types
        final Elements elements
        final String defaultPrefix;

        Factory(ProcessingEnvironment env) {
            this(env, '')
        }

        Factory(ProcessingEnvironment env, String defaultPrefix) {
            this.types = env.typeUtils
            this.elements = env.elementUtils
            this.defaultPrefix = defaultPrefix
        }

        public MethodElement create(ExecutableElement e) {
            assert e.kind == ElementKind.METHOD

            return new MethodElement(e, types, elements, defaultPrefix)
        }
    }


    @Delegate
    final ExecutableElement delegate
    final Types types
    final Elements elements
    final String defaultPrefix

    MethodElement(ExecutableElement delegate, Types types, Elements elements, String defaultPrefix) {
        this.delegate = delegate
        this.types = types
        this.elements = elements
        this.defaultPrefix = defaultPrefix
        this.rawNames = GenUtils.&getRawTypeName.curry(types)
    }

    Closure<String> rawNames

    boolean hasReturnType() {
        return delegate.returnType.kind != TypeKind.VOID
    }
    String getBoxedReturnType(){
        GenUtils.getRawTypeName(true,types,returnType)
    }
    String getRawReturnType() {
        return rawNames(returnType)
    }

    String getGeneratedName() {
        return prefix + simpleName
    }

    TypeElement getParent() {
        enclosingElement as TypeElement
    }


    String getPrefix() {
        Mirror mirror = getAnnotation(Mirror.class);
        if (!mirror) log.log(Level.WARNING, "mirror was null for $delegate")
        String prefix = mirror?.prefix() ?: ""
        return prefix.isEmpty() ? defaultPrefix : prefix
    }

    public List<String> rawParamTypes() {
        return parameters.collect { rawNames(it.asType()) }
    }

    public String paramClassesCSV() {
        rawParamTypes().collect { it + '.class' }.join(', ')
    }

    boolean hasParams() {
        return !parameters.empty
    }

    Map<String, String> rawParams(boolean box) {
        parameters.inject(new LinkedHashMap<>()) { acc, var ->
            [*: acc, (var.simpleName.toString()): GenUtils.getRawTypeName(box, types, var.asType())]
        }

    }

    Map<String, String> getRawParams() {
        rawParams(false)
    }

    Map<String, String> params() {
        parameters.inject(new LinkedHashMap<>()) { acc, var ->
            [*: acc, (var.simpleName.toString()): GenUtils.getTypeName(var.asType())]
        }
    }

    MirrorKind getMirrorKind() {
        MirrorKind.forMethod(this)
    }


}


