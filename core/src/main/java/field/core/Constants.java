package field.core;

import field.core.Platform.OS;
import field.launch.SystemProperties;
import field.math.linalg.Vector4;

import java.awt.*;

public class Constants {

	// static public final String defaultFont = "Gill Sans";
    public static final String defaultFont = Platform.getOS() == OS.mac ? SystemProperties.getProperty("systemFont", "Gill Sans") : "Source Sans Pro";
	public static final String defaultTextEditorFont = SystemProperties.getProperty("textEditorFont", defaultFont);
	public static int defaultFont_editorSize = SystemProperties.getIntProperty("editorFontSize", 16);

	// static public final String defaultTreeColor = "eeeeee";
    public static final String defaultTreeColor = "111111";
	public static final String defaultTreeColorDim = "666666";
	public static final float defaultTabMul = SystemProperties.getIntProperty("editorTabSize", 4);

	public static Vector4 execution_color = new Vector4(0.3f, 0.5f, 0.39f, 0.25f);
	public static Vector4 paused_execution_color = new Vector4(0.5f, 0.5f, 0.39f, 0.25f);

	public static final boolean invertedTextInCanvas = SystemProperties.getIntProperty("invertedText", 0) == 1;

	public static java.awt.Image plus;
	public static java.awt.Image minus;

	static {
		plus = Toolkit.getDefaultToolkit().getImage("icons/plus_alt_16x16.png");
		minus = Toolkit.getDefaultToolkit().getImage("icons/minus_alt_16x16.png");
	}

}
