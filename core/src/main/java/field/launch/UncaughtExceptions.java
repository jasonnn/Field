package field.launch;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
* Created by jason on 7/31/14.
*/
enum UncaughtExceptions implements Thread.UncaughtExceptionHandler {
    HANDLER;


    private static final Logger log = Logger.getLogger(UncaughtExceptions.class.getName());

    public static void setHandler(Thread t){
        t.setUncaughtExceptionHandler(HANDLER);
    }
    public static void setHandler(){
        setHandler(Thread.currentThread());
    }

    @Override
    public
    void uncaughtException(Thread t, Throwable e) {
        log.log(Level.WARNING, "uncaught exception in thread: " + t, e);
    }
}
