package field.bytecode;

import field.bytecode.protect.Woven;
import field.bytecode.protect.annotations.SimplyWrapped;
import field.bytecode.protect.iProvidesWrapping;
import field.bytecode.protect.iWrappedExit;
import field.launch.iLaunchable;
import field.math.linalg.Vector3;

import java.lang.reflect.Method;

public class T_SimpleWrapped implements iLaunchable {
	
	@Woven
    public static
    class Banana implements iProvidesWrapping {
		
		@SimplyWrapped
		public static
        Object banana()
		{
            //System.out.println(" I'm a banana ");
            return new Vector3(0,0,0);
		}
		
		public iWrappedExit enter(Method m) {
            //System.out.println(" entering <"+m+">");
            return null;
		}
		
	}
	
	public void launch() {

		Banana.banana();
		
	}
}
