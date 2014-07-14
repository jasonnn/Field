package field.core.windowing.components;

import field.core.dispatch.iVisualElement.Rect;
import org.eclipse.swt.widgets.Event;


public interface iDraggableComponent {

	public abstract void setSelected(boolean selected);

	public abstract boolean isSelected();

	public abstract float isHit(Event event);

	public abstract iComponent hit(Event event);

	public abstract Rect getBounds();

	public abstract void setBounds(Rect r);

	public abstract void setHidden(boolean hidden);

	public abstract void setDirty();

}