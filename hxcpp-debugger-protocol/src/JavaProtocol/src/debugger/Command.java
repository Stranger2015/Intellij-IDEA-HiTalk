package debugger;

@SuppressWarnings(value={"rawtypes", "unchecked"})
public  class Command extends logtalk.lang.Enum
{
	static 
	{
		debugger.Command.constructs = new logtalk.root.Array<>(
			new java.lang.String[]{"Exit", "Detach", "Files", "AllClasses", "Classes", "Mem", "Compact", "Collect",
				"SetCurrentThread", "AddFileLineBreakpoint", "AddClassFunctionBreakpoint", "ListBreakpoints",
				"DescribeBreakpoint", "DisableAllBreakpoints", "DisableBreakpointRange", "EnableAllBreakpoints",
				"EnableBreakpointRange", "DeleteAllBreakpoints", "DeleteBreakpointRange", "DeleteFileLineBreakpoint",
				"BreakNow", "Continue", "Step", "Next", "Finish", "WhereCurrentThread", "WhereAllThreads", "Up", "Down",
				"SetFrame", "Variables", "PrintExpression", "SetExpression", "GetStructured"});

		debugger.Command.Exit = new debugger.Command(0, new logtalk.root.Array<>(new Object[]{}));
		debugger.Command.Detach = new debugger.Command(1, new logtalk.root.Array<>(new Object[]{}));
		debugger.Command.Files = new debugger.Command(2, new logtalk.root.Array<>(new Object[]{}));
		debugger.Command.AllClasses = new debugger.Command(3, new logtalk.root.Array<>(new Object[]{}));
		debugger.Command.Mem = new debugger.Command(5, new logtalk.root.Array<>(new Object[]{}));
		debugger.Command.Compact = new debugger.Command(6, new logtalk.root.Array<>(new Object[]{}));
		debugger.Command.Collect = new debugger.Command(7, new logtalk.root.Array<>(new Object[]{}));
		debugger.Command.DisableAllBreakpoints = new debugger.Command(13, new logtalk.root.Array<>(new Object[]{}));
		debugger.Command.EnableAllBreakpoints = new debugger.Command(15, new logtalk.root.Array<>(new Object[]{}));
		debugger.Command.DeleteAllBreakpoints = new debugger.Command(17, new logtalk.root.Array<>(new Object[]{}));
		debugger.Command.BreakNow = new debugger.Command(20, new logtalk.root.Array<>(new Object[]{}));
		debugger.Command.WhereAllThreads = new debugger.Command(26, new logtalk.root.Array<>(new Object[]{}));
	}
	public    Command(int index, logtalk.root.Array<java.lang.Object> params)
	{
		super(index, params);
	}
	
	
	public static  logtalk.root.Array<java.lang.String> constructs;
	
	public static  debugger.Command Exit;
	
	public static  debugger.Command Detach;
	
	public static  debugger.Command Files;
	
	public static  debugger.Command AllClasses;
	
	public static   debugger.Command Classes(java.lang.String continuation)
	{
		return new debugger.Command(4, new logtalk.root.Array<Object>(new Object[]{continuation}));
	}
	
	
	public static  debugger.Command Mem;
	
	public static  debugger.Command Compact;
	
	public static  debugger.Command Collect;
	
	public static   debugger.Command SetCurrentThread(int number)
	{
		return new debugger.Command(8, new logtalk.root.Array<Object>(new Object[]{number}));
	}
	
	
	public static   debugger.Command AddFileLineBreakpoint(java.lang.String fileName, int lineNumber)
	{
		return new debugger.Command(9, new logtalk.root.Array<Object>(new Object[]{fileName, lineNumber}));
	}
	
	
	public static   debugger.Command AddClassFunctionBreakpoint(java.lang.String className, java.lang.String functionName)
	{
		return new debugger.Command(10, new logtalk.root.Array<Object>(new Object[]{className, functionName}));
	}
	
	
	public static   debugger.Command ListBreakpoints(boolean enabled, boolean disabled)
	{
		return new debugger.Command(11, new logtalk.root.Array<Object>(new Object[]{enabled, disabled}));
	}
	
	
	public static   debugger.Command DescribeBreakpoint(int number)
	{
		return new debugger.Command(12, new logtalk.root.Array<Object>(new Object[]{number}));
	}
	
	
	public static  debugger.Command DisableAllBreakpoints;
	
	public static   debugger.Command DisableBreakpointRange(int first, int last)
	{
		return new debugger.Command(14, new logtalk.root.Array<Object>(new Object[]{first, last}));
	}
	
	
	public static  debugger.Command EnableAllBreakpoints;
	
	public static   debugger.Command EnableBreakpointRange(int first, int last)
	{
		return new debugger.Command(16, new logtalk.root.Array<Object>(new Object[]{first, last}));
	}
	
	
	public static  debugger.Command DeleteAllBreakpoints;
	
	public static   debugger.Command DeleteBreakpointRange(int first, int last)
	{
		return new debugger.Command(18, new logtalk.root.Array<Object>(new Object[]{first, last}));
	}
	
	
	public static   debugger.Command DeleteFileLineBreakpoint(java.lang.String fileName, int lineNumber)
	{
		return new debugger.Command(19, new logtalk.root.Array<Object>(new Object[]{fileName, lineNumber}));
	}
	
	
	public static  debugger.Command BreakNow;
	
	public static   debugger.Command Continue(int count)
	{
		return new debugger.Command(21, new logtalk.root.Array<Object>(new Object[]{count}));
	}
	
	
	public static   debugger.Command Step(int count)
	{
		return new debugger.Command(22, new logtalk.root.Array<Object>(new Object[]{count}));
	}
	
	
	public static   debugger.Command Next(int count)
	{
		return new debugger.Command(23, new logtalk.root.Array<Object>(new Object[]{count}));
	}
	
	
	public static   debugger.Command Finish(int count)
	{
		return new debugger.Command(24, new logtalk.root.Array<Object>(new Object[]{count}));
	}
	
	
	public static   debugger.Command WhereCurrentThread(boolean unsafe)
	{
		return new debugger.Command(25, new logtalk.root.Array<Object>(new Object[]{unsafe}));
	}
	
	
	public static  debugger.Command WhereAllThreads;
	
	public static   debugger.Command Up(int count)
	{
		return new debugger.Command(27, new logtalk.root.Array<Object>(new Object[]{count}));
	}
	
	
	public static   debugger.Command Down(int count)
	{
		return new debugger.Command(28, new logtalk.root.Array<Object>(new Object[]{count}));
	}
	
	
	public static   debugger.Command SetFrame(int number)
	{
		return new debugger.Command(29, new logtalk.root.Array<Object>(new Object[]{number}));
	}
	
	
	public static   debugger.Command Variables(boolean unsafe)
	{
		return new debugger.Command(30, new logtalk.root.Array<Object>(new Object[]{unsafe}));
	}
	
	
	public static   debugger.Command PrintExpression(boolean unsafe, java.lang.String expression)
	{
		return new debugger.Command(31, new logtalk.root.Array<Object>(new Object[]{unsafe, expression}));
	}
	
	
	public static   debugger.Command SetExpression(boolean unsafe, java.lang.String lhs, java.lang.String rhs)
	{
		return new debugger.Command(32, new logtalk.root.Array<Object>(new Object[]{unsafe, lhs, rhs}));
	}
	
	
	public static   debugger.Command GetStructured(boolean unsafe, java.lang.String expression)
	{
		return new debugger.Command(33, new logtalk.root.Array<Object>(new Object[]{unsafe, expression}));
	}
	
	
}


