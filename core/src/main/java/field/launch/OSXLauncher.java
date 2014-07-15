package field.launch;

import com.apple.concurrent.Dispatch;

/**
 * Created by jason on 7/14/14.
 */
public class OSXLauncher {
    public static void main(final String[] args) {
        System.setProperty("main.class","field.Blank2");
        Dispatch.getInstance()
                .getBlockingMainQueueExecutor()
                .execute(new Runnable() {

                    @Override
                    public void run() {
                        Launcher.main(args);
                    }
                });
    }
}
