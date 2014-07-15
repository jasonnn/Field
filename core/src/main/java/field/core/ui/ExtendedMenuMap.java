package field.core.ui;

import field.core.ui.SmallMenu.BetterPopup;
import field.launch.Launcher;
import field.launch.iUpdateable;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;

import java.util.LinkedHashMap;

/**
 * adds marking menu support to LinkedHashMap
 * 
 * @author marc
 * 
 */
public class ExtendedMenuMap extends LinkedHashMap<String, iUpdateable> {

	MarkingMenuBuilder builder = new MarkingMenuBuilder();

	public MarkingMenuBuilder getBuilder() {
		return builder;
	}

	public void doMenu(Canvas invoker, Point local) {
		if (builder.spec.size() > 0) {
//			builder.defaultMenu().currentlyBuilding.menu.putAll(this);

            local = Launcher.display.map(invoker, null, local);

            //TODO swt marking menu
			builder.getMenu(invoker, local);

		} else {
			
			
			
			BetterPopup m = new SmallMenu().createMenu(this, invoker.getShell(), null);
			m.show(Launcher.display.map(invoker, invoker.getShell(), local));
		}
	}

}