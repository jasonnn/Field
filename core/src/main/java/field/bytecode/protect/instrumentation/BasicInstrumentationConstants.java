package field.bytecode.protect.instrumentation;

import field.protect.asm.ASMMethod;
import org.objectweb.asm.Type;

/**
 * Created by jason on 7/20/14.
 */
public class BasicInstrumentationConstants {
    public static final Type BASIC_INSTRUMENTATION_TYPE = Type.getType("Lfield/bytecode/protect/instrumentation/BasicInstrumentation2;");

    // o == Object[]

    public static final ASMMethod handle_O_OSOSSS = new ASMMethod("handle", "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/Object");
    public static final ASMMethod handle_V_SOSSo = new ASMMethod("handle", "(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)V");
    public static final ASMMethod handle_V_SOSSoc = new ASMMethod("handle", "(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;[Ljava/lang/Class;)V");
    public static final ASMMethod handleYieldIndex_I_SOS = new ASMMethod("handle_yieldIndex", "(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/String;)I");
    public static final ASMMethod handleYieldLoad = new ASMMethod("handdle_yieldLoad", "Ljava/lang/String;Ljava/lang/Object;Ljava/lang/String;)[Ljava/lang/Object;");
    public static final ASMMethod handleYieldStore = new ASMMethod("handle_yieldStore", "(Ljava/lang/Object;[Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;Ljava/lang/String;I)Ljava/lang/Object;");
    public static final ASMMethod handleCancelFast_O_IOSo = new ASMMethod("handleCancelFast", "(ILjava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;");
    public static final ASMMethod handleFast_V_IOo = new ASMMethod("handleFast", "(ILjava/lang/Object;[Ljava/lang/Object;)V");
}
