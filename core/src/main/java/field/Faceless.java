package field;

import field.core.execution.AutoEngage;
import field.core.execution.PhantomFluidSheet;
import field.launch.ILaunchable;
import field.launch.IUpdateable;
import field.launch.Launcher;
import field.launch.SystemProperties;
import field.util.MiscNative;

public
class Faceless implements ILaunchable {
    public double t;

    private PhantomFluidSheet phantom;

    public
    PhantomFluidSheet getSheet() {
        return phantom;
    }

    public
    void launch() {

        phantom = new PhantomFluidSheet(System.getProperty("user.home")
                                        + "/Documents/FieldWorkspace/"
                                        + SystemProperties.getProperty("field.scratch", "field.Blank.field"),
                                        false,
                                        false);

        Launcher.getLauncher().registerUpdateable(new IUpdateable() {

            public
            void update() {
                phantom.update((float) t);
            }
        });

        if (SystemProperties.getIntProperty("black", 0) == 1) {
            MiscNative.goToBlack();

        }

        String a = SystemProperties.getProperty("auto", "");
        if (a != null && !a.isEmpty()) {
            AutoEngage auto = new AutoEngage(phantom.getRoot());
            auto.start(a);
        }
    }
}
