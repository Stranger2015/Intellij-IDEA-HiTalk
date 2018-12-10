package debugger;

@SuppressWarnings(value={"rawtypes", "unchecked"})
public  class Message extends logtalk.lang.Enum
{
	static 
	{
		debugger.Message.constructs = new logtalk.root.Array<java.lang.String>(new java.lang.String[]{"ErrorInternal", "ErrorNoSuchThread", "ErrorNoSuchFile", "ErrorNoSuchBreakpoint", "ErrorBadClassNameRegex", "ErrorBadFunctionNameRegex", "ErrorNoMatchingFunctions", "ErrorBadCount", "ErrorCurrentThreadNotStopped", "ErrorEvaluatingExpression", "OK", "Exited", "Detached", "Files", "AllClasses", "Classes", "MemBytes", "Compacted", "Collected", "ThreadLocation", "FileLineBreakpointNumber", "ClassFunctionBreakpointNumber", "Breakpoints", "BreakpointDescription", "BreakpointStatuses", "ThreadsWhere", "Variables", "Value", "Structured", "ThreadCreated", "ThreadTerminated", "ThreadStarted", "ThreadStopped"});
		debugger.Message.OK = new debugger.Message(((int) (10) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{})) ));
		debugger.Message.Exited = new debugger.Message(((int) (11) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{})) ));
		debugger.Message.Detached = new debugger.Message(((int) (12) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{})) ));
	}
	public    Message(int index, logtalk.root.Array<java.lang.Object> params)
	{
		super(index, params);
	}
	
	
	public static  logtalk.root.Array<java.lang.String> constructs;
	
	public static   debugger.Message ErrorInternal(java.lang.String details)
	{
		return new debugger.Message(((int) (0) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{details})) ));
	}
	
	
	public static   debugger.Message ErrorNoSuchThread(int number)
	{
		return new debugger.Message(((int) (1) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{number})) ));
	}
	
	
	public static   debugger.Message ErrorNoSuchFile(java.lang.String fileName)
	{
		return new debugger.Message(((int) (2) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{fileName})) ));
	}
	
	
	public static   debugger.Message ErrorNoSuchBreakpoint(int number)
	{
		return new debugger.Message(((int) (3) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{number})) ));
	}
	
	
	public static   debugger.Message ErrorBadClassNameRegex(java.lang.String details)
	{
		return new debugger.Message(((int) (4) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{details})) ));
	}
	
	
	public static   debugger.Message ErrorBadFunctionNameRegex(java.lang.String details)
	{
		return new debugger.Message(((int) (5) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{details})) ));
	}
	
	
	public static   debugger.Message ErrorNoMatchingFunctions(java.lang.String className, java.lang.String functionName, debugger.StringList unresolvableClasses)
	{
		return new debugger.Message(((int) (6) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{className, functionName, unresolvableClasses})) ));
	}
	
	
	public static   debugger.Message ErrorBadCount(int count)
	{
		return new debugger.Message(((int) (7) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{count})) ));
	}
	
	
	public static   debugger.Message ErrorCurrentThreadNotStopped(int threadNumber)
	{
		return new debugger.Message(((int) (8) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{threadNumber})) ));
	}
	
	
	public static   debugger.Message ErrorEvaluatingExpression(java.lang.String details)
	{
		return new debugger.Message(((int) (9) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{details})) ));
	}
	
	
	public static  debugger.Message OK;
	
	public static  debugger.Message Exited;
	
	public static  debugger.Message Detached;
	
	public static   debugger.Message Files(debugger.StringList list)
	{
		return new debugger.Message(((int) (13) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{list})) ));
	}
	
	
	public static   debugger.Message AllClasses(debugger.StringList list)
	{
		return new debugger.Message(((int) (14) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{list})) ));
	}
	
	
	public static   debugger.Message Classes(debugger.ClassList list)
	{
		return new debugger.Message(((int) (15) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{list})) ));
	}
	
	
	public static   debugger.Message MemBytes(int bytes)
	{
		return new debugger.Message(((int) (16) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{bytes})) ));
	}
	
	
	public static   debugger.Message Compacted(int bytesBefore, int bytesAfter)
	{
		return new debugger.Message(((int) (17) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{bytesBefore, bytesAfter})) ));
	}
	
	
	public static   debugger.Message Collected(int bytesBefore, int bytesAfter)
	{
		return new debugger.Message(((int) (18) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{bytesBefore, bytesAfter})) ));
	}
	
	
	public static   debugger.Message ThreadLocation(int number, int stackFrame, java.lang.String className, java.lang.String functionName, java.lang.String fileName, int lineNumber)
	{
		return new debugger.Message(((int) (19) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{number, stackFrame, className, functionName, fileName, lineNumber})) ));
	}
	
	
	public static   debugger.Message FileLineBreakpointNumber(int number)
	{
		return new debugger.Message(((int) (20) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{number})) ));
	}
	
	
	public static   debugger.Message ClassFunctionBreakpointNumber(int number, debugger.StringList unresolvableClasses)
	{
		return new debugger.Message(((int) (21) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{number, unresolvableClasses})) ));
	}
	
	
	public static   debugger.Message Breakpoints(debugger.BreakpointList list)
	{
		return new debugger.Message(((int) (22) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{list})) ));
	}
	
	
	public static   debugger.Message BreakpointDescription(int number, debugger.BreakpointLocationList list)
	{
		return new debugger.Message(((int) (23) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{number, list})) ));
	}
	
	
	public static   debugger.Message BreakpointStatuses(debugger.BreakpointStatusList list)
	{
		return new debugger.Message(((int) (24) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{list})) ));
	}
	
	
	public static   debugger.Message ThreadsWhere(debugger.ThreadWhereList list)
	{
		return new debugger.Message(((int) (25) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{list})) ));
	}
	
	
	public static   debugger.Message Variables(debugger.StringList list)
	{
		return new debugger.Message(((int) (26) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{list})) ));
	}
	
	
	public static   debugger.Message Value(java.lang.String expression, java.lang.String type, java.lang.String value)
	{
		return new debugger.Message(((int) (27) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{expression, type, value})) ));
	}
	
	
	public static   debugger.Message Structured(debugger.StructuredValue structuredValue)
	{
		return new debugger.Message(((int) (28) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{structuredValue})) ));
	}
	
	
	public static   debugger.Message ThreadCreated(int number)
	{
		return new debugger.Message(((int) (29) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{number})) ));
	}
	
	
	public static   debugger.Message ThreadTerminated(int number)
	{
		return new debugger.Message(((int) (30) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{number})) ));
	}
	
	
	public static   debugger.Message ThreadStarted(int number)
	{
		return new debugger.Message(((int) (31) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{number})) ));
	}
	
	
	public static   debugger.Message ThreadStopped(int number, int stackFrame, java.lang.String className, java.lang.String functionName, java.lang.String fileName, int lineNumber)
	{
		return new debugger.Message(((int) (32) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{number, stackFrame, className, functionName, fileName, lineNumber})) ));
	}
	
	
}


