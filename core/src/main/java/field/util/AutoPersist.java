package field.util;

import field.core.ui.FieldMenus2;

public class AutoPersist {

    public static
    <T> T persist(final String name, T defaultValue) {
        T t = defaultValue;
		final String filename = FieldMenus2.fieldDir + '/' + name + ".xml";
		try {
			t = (T) PythonUtils.loadAsXML(filename);
		} catch (Throwable x) {
			x.printStackTrace();
			t = defaultValue;
		}

		if (t == null) t = defaultValue;
		
		final T ft = t;
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				
				if (ft instanceof org.eclipse.swt.graphics.Rectangle)
					;//System.out.println(" -- persit <"+name+" is <"+ft+"> to <"+filename+">");
				
				new PythonUtils().persistAsXML(ft, filename);
			}
		}));
		return ft;
	}

}
