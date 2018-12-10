package debugger;

@SuppressWarnings(value={"rawtypes", "unchecked"})
public  class BreakpointList extends logtalk.lang.Enum
{
	static 
	{
		debugger.BreakpointList.constructs = new logtalk.root.Array<java.lang.String>(new java.lang.String[]{"Terminator", "Breakpoint"});
		debugger.BreakpointList.Terminator = new debugger.BreakpointList(((int) (0) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{})) ));
	}
	public    BreakpointList(int index, logtalk.root.Array<java.lang.Object> params)
	{
		super(index, params);
	}
	
	
	public static  logtalk.root.Array<java.lang.String> constructs;
	
	public static  debugger.BreakpointList Terminator;
	
	public static   debugger.BreakpointList Breakpoint(int number, java.lang.String description, boolean enabled, boolean multi, debugger.BreakpointList next)
	{
		return new debugger.BreakpointList(((int) (1) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{number, description, enabled, multi, next})) ));
	}
	
	
}


