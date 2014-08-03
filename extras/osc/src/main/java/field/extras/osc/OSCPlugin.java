package field.extras.osc;


import field.core.dispatch.IVisualElement;
import field.core.dispatch.VisualElement;
import field.core.dispatch.VisualElementProperty;
import field.core.dispatch.override.DefaultOverride;
import field.core.dispatch.override.Ref;
import field.core.plugins.BaseSimplePlugin;
import field.core.util.PythonCallableMap;
import field.launch.SystemProperties;
import field.math.graph.visitors.hint.StandardTraversalHint;
import field.math.graph.visitors.hint.TraversalHint;

public class OSCPlugin extends BaseSimplePlugin {

	static public final VisualElementProperty<OSCIn> oscin = new VisualElementProperty<OSCIn>("OSCIn");
	static public final VisualElementProperty<OSCOut> oscout = new VisualElementProperty<OSCOut>("OSCOut");

	int defaultOutputPort = SystemProperties.getIntProperty("oscOuutputPort", 5501);
	String defaultAddress = SystemProperties.getProperty("oscOuutputAddress", "255.255.255.255");
	int defaultInputPort = SystemProperties.getIntProperty("oscInputPort", 5500);

	@Override
	public void close() {
		;//;//System.out.println(" OSC Plugin is closing down ");
//		OSCOut o = oscout.get(root);
//		if (o != null)
//			o.close();
//		OSCIn i = oscin.get(root);
//		if (i != null)
//			i.close();

		;//;//System.out.println(" OSC Plugin is finished closing down ");
	}

	@Override
	protected String getPluginNameImpl() {
		return "osc";
	}

	@Override
	protected
    DefaultOverride newVisualElementOverrides() {
		return new DefaultOverride() {
			@Override
			public <T> TraversalHint getProperty(IVisualElement source, VisualElementProperty<T> prop, Ref<T> ref) {
				if (prop.equals(OSCIn.handleOsc)) {
					T p = source.getProperty(prop);
					if (p == null)
						source.setProperty(prop, p = (T) new PythonCallableMap());
					ref.set((T) p);
					return StandardTraversalHint.CONTINUE;
				}
				return super.getProperty(source, prop, ref);
			}
		};
	}

	static public OSCOut theOut= null;
	static public OSCIn theIn= null;

	
	@Override
	public void registeredWith(IVisualElement root) {
		super.registeredWith(root);

		if (theOut==null)
			theOut = new OSCOut(root, defaultOutputPort, defaultAddress);
		oscout.set(root, root, theOut);
		if (theIn==null)
			theIn = new OSCIn(root, defaultInputPort);
		
		oscin.set(root, root, theIn);
	}

}
