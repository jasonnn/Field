package field.launch;

import com.apple.concurrent.Dispatch;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public
class OSXMainTest {


    public static
    void main(String[] args) {
        Dispatch.getInstance().getBlockingMainQueueExecutor().execute(new Runnable() {
            @Override
            public
            void run() {
                appMenu();
            }
        });
    }


    static
    void appMenu() {
        final Display display = new Display();
        Shell shell = new Shell(display);
        shell.setLayout(new GridLayout(1, false));
        Menu appMenuBar = display.getMenuBar();
        if (appMenuBar == null) {
            appMenuBar = new Menu(shell, SWT.BAR);
            shell.setMenuBar(appMenuBar);
        }
        MenuItem file = new MenuItem(appMenuBar, SWT.CASCADE);
        file.setText("File");
        Menu dropdown = new Menu(appMenuBar);
        file.setMenu(dropdown);
        MenuItem exit = new MenuItem(dropdown, SWT.PUSH);
        exit.setText("Exit");
        exit.addSelectionListener(new SelectionAdapter() {
            @Override
            public
            void widgetSelected(SelectionEvent e) {
                display.dispose();
            }
        });
        Button b = new Button(shell, SWT.PUSH);
        b.setText("Test");
        shell.pack();
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) display.sleep();
        }
        display.dispose();
    }

}