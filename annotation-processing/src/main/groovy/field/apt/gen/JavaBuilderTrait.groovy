package field.apt.gen

import javabuilder.JavaBuilder
import javabuilder.delegates.ClassHandler
import javabuilder.delegates.CompilationUnitHandler
import javabuilder.delegates.InterfaceHandler
import javabuilder.delegates.StatementHandler

/**
 * Created by jason on 7/30/14.
 */
trait JavaBuilderTrait
        implements CompilationUnitHandler,
                ClassHandler,
                InterfaceHandler,
                StatementHandler {
    @Delegate
    JavaBuilder javaBuilder

}