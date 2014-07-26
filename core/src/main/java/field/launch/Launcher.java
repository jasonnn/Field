package field.launch;

import field.core.Platform;
import field.core.util.AppleScript;
import field.util.MiscNative;
import org.eclipse.swt.widgets.Display;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public
class Launcher {
    public static final Object lock = new Object();

    static {
        try {
            resourcesDirectory = new File(".").getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final Logger log = Logger.getLogger(Launcher.class.getName());
    public static String resourcesDirectory;
    public static iLaunchable mainInstance;
    public static String[] args = {};
    public static Display display;
    public static volatile boolean shuttingDown = false;
    protected static Launcher launcher = null;
    static List<iOpenFileHandler> openFileHandlers = new ArrayList<iOpenFileHandler>();
    public Thread mainThread = null;
    protected List<iUpdateable> updateables = Collections.synchronizedList(new ArrayList<iUpdateable>());
    protected List<iUpdateable> postUpdateables = Collections.synchronizedList(new ArrayList<iUpdateable>());
    protected Set<iUpdateable> paused = Collections.synchronizedSet(new LinkedHashSet<iUpdateable>());
    protected Set<iUpdateable> willPause = Collections.synchronizedSet(new LinkedHashSet<iUpdateable>());
    protected Set<iUpdateable> willUnPause = Collections.synchronizedSet(new LinkedHashSet<iUpdateable>());
    protected Set<iUpdateable> willRemove = Collections.synchronizedSet(new LinkedHashSet<iUpdateable>());
    protected Set<iUpdateable> willAdd = Collections.synchronizedSet(new LinkedHashSet<iUpdateable>());
    protected iUpdateable currentUpdating;
    protected boolean isPaused = false;
    boolean dying = false;
    double interval = SystemProperties.getDoubleProperty("timer.interval", 0.01f);
    List<iExceptionHandler> exceptionHandlers = new ArrayList<iExceptionHandler>();
    List<iUpdateable> shutdown = new ArrayList<iUpdateable>();
    private iContinuation continuation;
    private Runnable timer;

    public
    Launcher(String[] args) {

        assert launcher == null : launcher;
        Launcher.launcher = this;
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public
            void run() {
                dying = true;
            }
        }));
        String trampoline =
                System.getProperty("trampoline.class", "field.bytecode.protect.trampoline.StandardTrampoline");
        boolean isTrampoline = trampoline != null;

        if (trampoline == null) trampoline = System.getProperty("main.class");
        boolean success = false;
        try {
            final Class<?> c = Class.forName(trampoline);
            try {
                Method main = c.getDeclaredMethod("main", String[].class);
                try {
                    main.invoke(null, new Object[]{args});
                    constructMainTimer();
                    return;
                } catch (Throwable e) {
                    e.printStackTrace();
                    if (SystemProperties.getIntProperty("exitOnException", 0) == 1) System.exit(1);
                }
                return;
            } catch (SecurityException e) {
                e.printStackTrace();
                return;
            } catch (NoSuchMethodException ignored) {
            }

            // swing utilities?
            if (isTrampoline) {
                //StandardTrampoline is created here
                (mainInstance = (iLaunchable) c.newInstance()).launch();
                constructMainTimer();
            }
            else {

                mainThread = Thread.currentThread();
                (mainInstance = (iLaunchable) c.newInstance()).launch();
            }

        } catch (Throwable e) {
            e.printStackTrace();
            if (SystemProperties.getIntProperty("exitOnException", 0) == 1) System.exit(1);
        }

    }

    public static
    Launcher getLauncher() {
        return launcher;
    }

    public static
    void main(final String[] args) {

        log.info(" hello ");
        //TODO swt/appkit/? doesnt seem to like this (when using OSXMain)
        display = new Display();
        Display.setAppName("Field");
        display.syncExec(new Runnable() {

            @Override
            public
            void run() {
                Launcher.args = args;

                if (SystemProperties.getIntProperty("headless", 0) == 1) {
                    log.info(" we are headless ");
                    System.setProperty("java.awt.headless", "true");
                }


                launcher = new Launcher(args);
                MiscNative.doSplash();

                if ((Platform.getOS() == Platform.OS.mac) && (SystemProperties.getIntProperty("above", 0) == 1))
                    new AppleScript("tell application \"Field\"\n activate\nend tell", false);

            }
        });

        while (!display.isDisposed()) {

            try {
                if (!display.readAndDispatch()) {
                    display.sleep();
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        //System.out.println(" display has gone");
        Launcher.getLauncher().runRegisteredShutdownHooks();
    }

    public static
    void registerOpenHandler(iOpenFileHandler h) {
        // if (openFileHandlers.size() == 0) {
        // for (ApplicationEvent a : openEvents) {
        // h.open(a.getFilename());
        // }
        // openEvents.clear();

        //}
        openFileHandlers.add(h);
    }

    public
    void deregisterUpdateable(iUpdateable up) {
        getWillRemove().add(up);
    }

    public
    boolean isRegisteredUpdateable(iUpdateable up) {
        return getUpdateables().contains(up) || getPaused().contains(up);
    }

    /**
     * Call this method to register a <code>iUpadteable</code> for updating
     * at the main timer's frequency.
     *
     * @param target - the <code>iUpdateable</code> which will be updated.
     */
    public
    void registerUpdateable(iUpdateable target) {
        getWillAdd().add(target);
    }

    public
    void addPostUpdateable(iUpdateable target) {
        postUpdateables.add(target);
    }

    /**
     * Call this method to register an <code>iUpdateable</code> for updating
     * at the specified divisor (<code>updateDivisor</code>) of the main
     * timer's update frequency. For example, if <code>updateDivisor</code>
     * is 2 and the main timer runs at 60 Hz, then the target will be
     * updated at 30 Hz.
     *
     * @param target        The <code>iUpdateable</code> which will be updated.
     * @param updateDivisor Will be updated at the main timer's frequency divided
     *                      by this parameter.
     */
    public
    void registerUpdateable(final iUpdateable target, final int updateDivisor) {
        // The anonymous class used here wraps the
        // update target and implements
        // the logic necessary to support updating at a
        // specified frequency
        // relative to the main timer.
        registerUpdateable(new iUpdateable() {
            int tick = 0;

            public
            void update() {
                tick++;
                if (tick % updateDivisor == 0) {
                    target.update();
                }
            }
        });
    }

    public
    void setContinuation(iContinuation continuation) {
        this.continuation = continuation;
    }

    protected
    void constructMainTimer() {

        timer = new Runnable() {

            int in = 0;

            long timeIn = 0;

            public
            void run() {

                // new Exception().printStackTrace();

//				if (!dying)
//					display.timerExec((int) (interval * 1000), timer);

                timeIn = System.currentTimeMillis();

                if (continuation != null) {
                    try {
                        iContinuation was = continuation;
                        continuation = null;
                        was.next();
                    } catch (Throwable t) {
                        //System.out.println(" exception thrown in continuation <" + continuation + ">");
                        t.printStackTrace();
                    }

                    return;
                }

                mainThread = Thread.currentThread();
                synchronized (lock) {
                    if (dying) return;

                    if (!isPaused) {
                        in++;
                        try {
                            if (in == 1) for (int i = 0; i < getUpdateables().size(); i++) {
                                iUpdateable up = getUpdateables().get(i);
                                if (!getPaused().contains(up)) try {
                                    setCurrentUpdating(up);

                                    up.update();

                                    if (continuation != null) {
                                        return;
                                    }

                                } catch (Throwable tr) {
                                    log.log(Level.WARNING,
                                            "Launcher reporting an exception while updating <" + up + '>',
                                            tr);
                                    tr.printStackTrace();
                                    handle(tr);
                                    if (SystemProperties.getIntProperty("exitOnException", 0) == 1) System.exit(1);
                                }
                                setCurrentUpdating(null);
                            }
                        } finally {
                            in--;
                        }
                        getPaused().addAll(getWillPause());
                        getPaused().removeAll(getWillUnPause());
                        getWillPause().clear();
                        getWillUnPause().clear();
                    }
                    getUpdateables().addAll(getWillAdd());
                    getUpdateables().removeAll(getWillRemove());
                    getWillRemove().clear();
                    getWillAdd().clear();

                    for (iUpdateable u : new ArrayList<iUpdateable>(postUpdateables)) {
                        u.update();
                    }
                }

                long duration = System.currentTimeMillis() - timeIn;

                int next = (int) ((long) (interval * 1000) - duration);
                if (next < 5) next = 5;
                if (!dying) display.timerExec(next, timer);

            }
        };

        display.timerExec((int) (interval * 1000), timer);
    }

    public
    void handle(Throwable tr) {
        for (iExceptionHandler exceptionHandler : exceptionHandlers)
            if (exceptionHandler.handle(tr)) return;
    }

    public
    void addExceptionHandler(iExceptionHandler e) {
        exceptionHandlers.add(e);
    }

    protected
    iUpdateable getCurrentUpdating() {
        return currentUpdating;
    }

    protected
    void setCurrentUpdating(iUpdateable currentUpdating) {
        this.currentUpdating = currentUpdating;
    }

    protected
    Set<iUpdateable> getPaused() {
        return paused;
    }

//    protected void setPaused(Set paused) {
//        this.paused = paused;
//    }

    protected
    List<iUpdateable> getUpdateables() {
        return updateables;
    }

//    protected void setUpdateables(List<iUpdateable> updateables) {
//        this.updateables = updateables;
//    }

    protected
    Set<iUpdateable> getWillAdd() {
        return willAdd;
    }

//    protected void setWillAdd(Set willAdd) {
//        this.willAdd = willAdd;
//    }

    protected
    Set<iUpdateable> getWillPause() {
        return willPause;
    }

//    protected void setWillPause(Set willPause) {
//        this.willPause = willPause;
//    }

    protected
    Set<iUpdateable> getWillRemove() {
        return willRemove;
    }

//    protected void setWillRemove(Set willRemove) {
//        this.willRemove = willRemove;
//    }

    protected
    Set<iUpdateable> getWillUnPause() {
        return willUnPause;
    }

//    protected void setWillUnPause(Set willUnPause) {
//        this.willUnPause = willUnPause;
//    }

    public
    void nextCycle(final iUpdateable updateable) {
        registerUpdateable(new iUpdateable() {

            public
            void update() {
                updateable.update();
                deregisterUpdateable(this);
            }
        });
    }

    public
    void runRegisteredShutdownHooks() {

        shuttingDown = true;
        //System.out.println(" running down ");
        for (iUpdateable u : shutdown) {
            u.update();
        }

        //System.out.println(" exiting ");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    public
    void addShutdown(iUpdateable u) {
        shutdown.add(u);
    }

    public
    void removeShutdownHook(iUpdateable shutdownhook) {
        shutdown.remove(shutdownhook);
    }

    public static
    interface iOpenFileHandler {
        public
        void open(String file);
    }

    public static
    interface iContinuation {
        public
        void next();
    }

    public static
    interface iExceptionHandler {
        public
        boolean handle(Throwable t);
    }

}
