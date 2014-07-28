import com.apple.concurrent.Dispatch;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *
 */
public
class OSXMain {
    static final String LAUNCHER = "field.launch.Launcher";
    public static
    void main(final String[] args) throws ClassNotFoundException, NoSuchMethodException {
        System.setProperty("main.class", "field.Blank2");
        Class<?> launcherClass = Class.forName(LAUNCHER);
        final Method mainMethod = launcherClass.getDeclaredMethod("main",String[].class);
        Dispatch.getInstance().getBlockingMainQueueExecutor().execute(new Runnable() {

            @Override
            public
            void run() {
                try {
                    mainMethod.invoke(null,(Object)args);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
