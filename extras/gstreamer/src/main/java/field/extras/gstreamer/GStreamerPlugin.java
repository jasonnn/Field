package field.extras.gstreamer;

import java.io.File;

import org.gstreamer.Gst;
import org.gstreamer.Registry;

import com.ochafik.lang.jnaerator.runtime.JNAeratorRuntime;
import com.sun.jna.NativeLibrary;

import field.core.dispatch.iVisualElement;
import field.core.plugins.BaseSimplePlugin;
import field.launch.SystemProperties;

public class GStreamerPlugin extends BaseSimplePlugin {

	@Override
	public void registeredWith(iVisualElement root) {
		super.registeredWith(root);

		String extensionsDir = "";

		if (field.core.Platform.isMac()) {
			extensionsDir = SystemProperties.getDirProperty("gstreamer_dir");

			System.setProperty("jna.library.path", System.getProperty("jna.library.path")+File.pathSeparator+extensionsDir + "/lib/macosx");

			System.out.println(" will load gstreamer from:" + new File(extensionsDir + "lib/macosx").getAbsolutePath());
		} else {
			extensionsDir = SystemProperties.getDirProperty("gstreamer_dir", "/usr/lib/gstreamer-0.10/");

			System.setProperty("jna.library.path", System.getProperty("jna.library.path")+File.pathSeparator+extensionsDir);

			System.out.println(" will load gstreamer from: " + new File(extensionsDir).getAbsolutePath());

			NativeLibrary.getProcess();
		}

		Gst.setUseDefaultContext(false);
		Gst.init("field", new String[] {});

		if (field.core.Platform.isMac()) {
			try {
				Registry.getDefault().scanPath(extensionsDir + "/lib/macosx/plugins");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	protected String getPluginNameImpl() {
		return "gstreamer";
	}

}
