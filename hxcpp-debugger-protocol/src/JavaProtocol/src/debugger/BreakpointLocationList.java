package debugger;

@SuppressWarnings(value={"rawtypes", "unchecked"})
public  class BreakpointLocationList extends logtalk.lang.Enum
{
	static 
	{
		debugger.BreakpointLocationList.constructs = new logtalk.root.Array<java.lang.String>(new java.lang.String[]{"Terminator", "FileLine", "ClassFunction"});
		debugger.BreakpointLocationList.Terminator = new debugger.BreakpointLocationList(((int) (0) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{})) ));
	}
	public    BreakpointLocationList(int index, logtalk.root.Array<java.lang.Object> params)
	{
		super(index, params);
	}
	
	
	public static  logtalk.root.Array<java.lang.String> constructs;
	
	public static  debugger.BreakpointLocationList Terminator;
	
	public static   debugger.BreakpointLocationList FileLine(java.lang.String fileName, int lineNumber, debugger.BreakpointLocationList next)
	{
		return new debugger.BreakpointLocationList(((int) (1) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{fileName, lineNumber, next})) ));
	}
	
	
	public static   debugger.BreakpointLocationList ClassFunction(java.lang.String className, java.lang.String functionName, debugger.BreakpointLocationList next)
	{
		return new debugger.BreakpointLocationList(((int) (2) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{className, functionName, next})) ));
	}
	
	
}


