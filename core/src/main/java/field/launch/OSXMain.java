package field.launch;

import com.apple.concurrent.Dispatch;

/**
 *
 */
public class OSXMain {
    public static void main(final String[] args) {
        System.setProperty("main.class", "field.Blank2");
        //assert Toolkit.getDefaultToolkit() != null : "awt not loaded";
        Dispatch.getInstance()
                .getNonBlockingMainQueueExecutor()
                .execute(new Runnable() {

                    @Override
                    public void run() {
                        Launcher.main(args);
                    }
                });
    }
}
