package field.apt.util;

import com.squareup.javawriter.JavaWriter;
import field.apt.CodeGen;
import field.apt.gen.CodeGenerator;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.SimpleElementVisitor6;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jason on 7/10/14.
 */
public class GenUtils {

    private static final CodeGen NOOP = new CodeGen() {
        @Override
        public void generate() throws IOException {

        }
    };

    public static class CodeGeneratorBuilder {
        private final Element element;

        CodeGeneratorBuilder(Element element) {
            this.element = element;
        }

        public CodeGen withEnv(ProcessingEnvironment env) throws IOException {
            assert element != null;
            TypeElement te = element.accept(new SimpleElementVisitor6<TypeElement, Void>() {
                @Override
                public TypeElement visitType(TypeElement e, Void aVoid) {
                    return e;
                }
            }, null);

            if (te == null) {
                env.getMessager().printMessage(Diagnostic.Kind.NOTE, "???!!!");
                return GenUtils.NOOP;
            }
            FieldsAndMethods fm = element.accept(new FieldsAndMethods(), null);
            JavaFileObject jfo = createSourceFile(te, env);
            return new CodeGenerator(env, jfo.openWriter(), te, fm.fields, fm.methods);
        }
    }

    public static CodeGeneratorBuilder generatorBuilderFor(Element e) {
        return new CodeGeneratorBuilder(e);
    }



    public static final String GEN_SUFFIX = "_m";

    public static String generatedSimpleName(TypeElement e) {
        return e.getSimpleName().toString() + GEN_SUFFIX;
    }

    public static String generatedFQN(TypeElement e) {
        return e.getQualifiedName().toString() + GEN_SUFFIX;
    }

    public static JavaWriter javaWriter(TypeElement e, ProcessingEnvironment env) throws IOException {
        return new JavaWriter(createSourceFile(e, env).openWriter());
    }

    public static JavaFileObject createSourceFile(TypeElement e, ProcessingEnvironment env) throws IOException {
        return env.getFiler().createSourceFile(generatedFQN(e), e);
    }

    public static String packageNameOf(TypeElement e) {
        return packageOf(e).getQualifiedName().toString();
    }

    public static PackageElement packageOf(TypeElement e) {
        for (Element parent = e; parent != null; parent = parent.getEnclosingElement()) {
            if (parent.getKind() == ElementKind.PACKAGE) return (PackageElement) parent;
        }
        throw new RuntimeException("??!!");
    }

    public static List<String> defaultImports(boolean mutable) {
        List<String> imports = Arrays.asList(
                "java.lang.reflect.*",
                "java.util.*",
                "field.bytecode.apt.*",
                "field.namespace.generic.Bind.*",
                "field.math.abstraction.*",
                "field.launch.*",
                "field.namespace.generic.ReflectionTools");

        return mutable ? new ArrayList<String>(imports) : imports;
    }

    public static List<String> defaultImports() {
        return defaultImports(false);
    }

}
