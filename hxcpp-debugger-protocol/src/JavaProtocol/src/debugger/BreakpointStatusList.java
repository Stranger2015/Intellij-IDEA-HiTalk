package debugger;

@SuppressWarnings(value={"rawtypes", "unchecked"})
public  class BreakpointStatusList extends logtalk.lang.Enum
{
	static 
	{
		debugger.BreakpointStatusList.constructs = new logtalk.root.Array<java.lang.String>(new java.lang.String[]{"Terminator", "Nonexistent", "Disabled", "AlreadyDisabled", "Enabled", "AlreadyEnabled", "Deleted"});
		debugger.BreakpointStatusList.Terminator = new debugger.BreakpointStatusList(((int) (0) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{})) ));
	}
	public    BreakpointStatusList(int index, logtalk.root.Array<java.lang.Object> params)
	{
		super(index, params);
	}
	
	
	public static  logtalk.root.Array<java.lang.String> constructs;
	
	public static  debugger.BreakpointStatusList Terminator;
	
	public static   debugger.BreakpointStatusList Nonexistent(int number, debugger.BreakpointStatusList next)
	{
		return new debugger.BreakpointStatusList(((int) (1) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{number, next})) ));
	}
	
	
	public static   debugger.BreakpointStatusList Disabled(int number, debugger.BreakpointStatusList next)
	{
		return new debugger.BreakpointStatusList(((int) (2) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{number, next})) ));
	}
	
	
	public static   debugger.BreakpointStatusList AlreadyDisabled(int number, debugger.BreakpointStatusList next)
	{
		return new debugger.BreakpointStatusList(((int) (3) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{number, next})) ));
	}
	
	
	public static   debugger.BreakpointStatusList Enabled(int number, debugger.BreakpointStatusList next)
	{
		return new debugger.BreakpointStatusList(((int) (4) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{number, next})) ));
	}
	
	
	public static   debugger.BreakpointStatusList AlreadyEnabled(int number, debugger.BreakpointStatusList next)
	{
		return new debugger.BreakpointStatusList(((int) (5) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{number, next})) ));
	}
	
	
	public static   debugger.BreakpointStatusList Deleted(int number, debugger.BreakpointStatusList next)
	{
		return new debugger.BreakpointStatusList(((int) (6) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{number, next})) ));
	}
	
	
}


