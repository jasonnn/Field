package field.core.plugins;

import field.core.dispatch.IVisualElement;
import field.launch.IUpdateable;

public
interface iPlugin extends IUpdateable {

    public
    void close();

    public
    Object getPersistanceInformation();

    public
    IVisualElement getWellKnownVisualElement(String id);

    public
    void registeredWith(IVisualElement root);

    public
    void setPersistanceInformation(Object o);

}
