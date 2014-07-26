package field.launch;

import javax.swing.*;
import java.applet.Applet;

public
class AppletLauncher extends Applet {

    public static final Applet mainApplet = new Applet();

    @Override
    public
    void init() {
        super.init();

        add(new JLabel("banana"));

        //System.out.println(" about to launch ");
        Launcher.main(new String[]{"-main.class", "field.nonpackage.HelloApplet"});
        //System.out.println(" launched ");
    }
}
