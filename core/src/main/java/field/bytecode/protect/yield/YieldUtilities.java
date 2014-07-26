package field.bytecode.protect.yield;

public class YieldUtilities {

	public static
    Object yield(Object ret)
	{
		return ret;
	}

	public static
    Boolean yield(Boolean ret)
	{
		return ret;
	}
	
	public static
    class Finished extends RuntimeException
	{}
	
}
