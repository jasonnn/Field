package field.launch;

import java.util.logging.Level;

/**
* Created by jason on 7/31/14.
*/
enum UncaughtExceptions implements Thread.UncaughtExceptionHandler {
    INSTANCE;

    @Override
    public
    void uncaughtException(Thread t, Throwable e) {
        OSXMain.log.log(Level.WARNING, "uncaught exception in thread: " + t, e);
    }
}
