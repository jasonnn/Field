package field.util;

import field.launch.IUpdateable;
import field.launch.Launcher;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Submits runnables to the main Field update cycle.
 *
 * @author marc
 */
public
class FieldExecutorService extends AbstractExecutorService {

    public static final FieldExecutorService service = new FieldExecutorService();

    @Override
    public
    boolean awaitTermination(long arg0, TimeUnit arg1) throws InterruptedException {
        return false;
    }

    @Override
    public
    boolean isShutdown() {
        return false;
    }

    @Override
    public
    boolean isTerminated() {
        return false;
    }

    @Override
    public
    void shutdown() {
    }

    @NotNull
    @Override
    public
    List<Runnable> shutdownNow() {
        return null;
    }

    @Override
    public
    void execute(final Runnable arg0) {
        Launcher.getLauncher().registerUpdateable(new IUpdateable() {

            @Override
            public
            void update() {
                try {
                    arg0.run();
                } catch (Throwable t) {
                    System.out.println(" exception thrown in SendToField function");
                    t.printStackTrace();
                }
                Launcher.getLauncher().deregisterUpdateable(this);
            }
        });
    }

    public
    void executeLater(final Runnable arg0, final int delay) {
        Launcher.getLauncher().registerUpdateable(new IUpdateable() {

            int t = 0;

            @Override
            public
            void update() {
                if (t == delay) {
                    arg0.run();
                    Launcher.getLauncher().deregisterUpdateable(this);
                }
                t++;
            }
        });
    }

}
