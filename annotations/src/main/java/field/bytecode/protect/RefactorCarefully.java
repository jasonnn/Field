package field.bytecode.protect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation should be used on things which are called reflectively or used in code generation.
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.CONSTRUCTOR, ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PACKAGE, ElementType.TYPE})
public @interface RefactorCarefully {
    String[] callerClases() default {};
    String note() default "";
}
