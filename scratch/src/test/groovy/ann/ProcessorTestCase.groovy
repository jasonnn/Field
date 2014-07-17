package ann

import org.junit.BeforeClass
import org.junit.Test

import javax.annotation.processing.Processor
import javax.tools.*
import java.nio.charset.Charset

import static ann.TestingProcessor.fromClosure

/**
 * Created by jason on 7/16/14.
 */
class ProcessorTestCase {

    static final def SRC = new StringSrc('some.Thing', '''
pacakge some;
class Thing{}
''')

    private static final DiagnosticListener listener = { Diagnostic message ->
        System.err.println(message)
    }
    static final StandardJavaFileManager mgr = ToolProvider.systemJavaCompiler.getStandardFileManager(
            listener,
            Locale.default,
            Charset.forName('UTF-8'))

    @BeforeClass
    static void initStatic() {
        def coreJar = new File('/Users/jason/IdeaProjects/Field/core/build/libs/core-1.0.jar')
        def locations = mgr.getLocation(StandardLocation.CLASS_PATH).asList()
        mgr.setLocation(StandardLocation.CLASS_PATH, [*locations, coreJar])

    }

    static class TaskSpec {

        List<String> classes = []
        List<JavaFileObject> compilationUnits = []
        List<String> options = []
        List<Processor> processors = []

    }

    static void run(TaskSpec spec) {
        def task = ToolProvider.systemJavaCompiler.getTask(
                new PrintWriter(System.err, true),
                mgr,
                listener,
                spec.options,
                spec.classes,
                spec.compilationUnits)
        task.setProcessors(spec.processors)
        task.setLocale(Locale.default)
        def result = task.call()
        println "success: $result"

    }

    static TestingProcessor loadTestProcessor() {
        def processor = new TestingProcessor()
        def task = [classes   : [Runnable.name],
                    options   : ['-proc:only'],
                    processors: [processor]] as TaskSpec
        run task
        return processor
    }

    static void run(Map<String, Processor> args) {
        def task = [//compilationUnits: [SRC],
                    classes   : [Runnable.name],
                    options   : ['-proc:only'],
                    processors: args.values().asList()] as TaskSpec
        run(task)

    }

    @Test
    public void testSomething() throws Exception {
        def processor = new TestingProcessor()
        def task = [classes   : [Runnable.name],
                    options   : ['-proc:only'],
                    processors: [processor]] as TaskSpec
        run task

        assert processor.env

    }

    @Test
    public void testFromClosure() throws Exception {
        boolean called = false
        run processor: fromClosure {
            if (!firstRun) return
            println "hello from env: $env"
            called = true
        }

        assert called
    }
}
