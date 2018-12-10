package debugger;

@SuppressWarnings(value={"rawtypes", "unchecked"})
public  class FrameList extends logtalk.lang.Enum
{
	static 
	{
		debugger.FrameList.constructs = new logtalk.root.Array<java.lang.String>(new java.lang.String[]{"Terminator", "Frame"});
		debugger.FrameList.Terminator = new debugger.FrameList(((int) (0) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{})) ));
	}
	public    FrameList(int index, logtalk.root.Array<java.lang.Object> params)
	{
		super(index, params);
	}
	
	
	public static  logtalk.root.Array<java.lang.String> constructs;
	
	public static  debugger.FrameList Terminator;
	
	public static   debugger.FrameList Frame(boolean isCurrent, int number, java.lang.String className, java.lang.String functionName, java.lang.String fileName, int lineNumber, debugger.FrameList next)
	{
		return new debugger.FrameList(((int) (1) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{isCurrent, number, className, functionName, fileName, lineNumber, next})) ));
	}
	
	
}


