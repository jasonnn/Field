package field.apt.util;

import com.squareup.javawriter.JavaWriter;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jason on 7/10/14.
 */
public
class GenUtils {




    public static final String GEN_SUFFIX = "_m";

    public static
    String generatedSimpleName(TypeElement e) {
        return e.getSimpleName().toString() + GEN_SUFFIX;
    }

    public static
    String generatedFQN(TypeElement e) {
        return e.getQualifiedName().toString() + GEN_SUFFIX;
    }

    public static
    JavaWriter javaWriter(TypeElement e, ProcessingEnvironment env) throws IOException {
        return new JavaWriter(createSourceFile(e, env).openWriter());
    }

    public static
    JavaFileObject createSourceFile(TypeElement e, ProcessingEnvironment env) throws IOException {
        return env.getFiler().createSourceFile(generatedFQN(e), e);
    }

    public static
    String packageNameOf(TypeElement e) {
        return packageOf(e).getQualifiedName().toString();
    }

    public static
    PackageElement packageOf(TypeElement e) {
        for (Element parent = e; parent != null; parent = parent.getEnclosingElement()) {
            if (parent.getKind() == ElementKind.PACKAGE) return (PackageElement) parent;
        }
        throw new RuntimeException("??!!");
    }

    public static
    List<String> defaultImports(boolean mutable) {
        List<String> imports = Arrays.asList("java.lang.reflect.*",
                                             "java.util.*",
                                             "field.bytecode.mirror.*",
                                             "field.namespace.generic.Bind.*",
                                             "field.math.abstraction.*",
                                             "field.launch.*",
                                             "field.namespace.generic.ReflectionTools");

        return mutable ? new ArrayList<String>(imports) : imports;
    }

    public static
    List<String> defaultImports() {
        return defaultImports(false);
    }

}
