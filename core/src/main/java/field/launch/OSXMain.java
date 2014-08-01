package field.launch;

import com.apple.concurrent.Dispatch;

/**
 *
 */
public
class OSXMain {

    public static
    void main(final String[] args) {
        System.setProperty("main.class", "field.Blank2");
        Dispatch.getInstance().getBlockingMainQueueExecutor().execute(new Runnable() {

            @Override
            public
            void run() {

                Thread.currentThread().setUncaughtExceptionHandler(UncaughtExceptions.HANDLER);
                Launcher.main(args);
            }
        });
    }


}
