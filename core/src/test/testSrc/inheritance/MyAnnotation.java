package inheritance;

import java.lang.annotation.*;

/**
 * Created by jason on 7/16/14.
 */
//It looks like Inherited is only used at runtime with reflection...
@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MyAnnotation {
}
