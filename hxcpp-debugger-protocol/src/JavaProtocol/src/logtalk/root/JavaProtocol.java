package logtalk.root;

@SuppressWarnings(value={"rawtypes", "unchecked"})
public  class JavaProtocol extends logtalk.lang.HxObject
{
	public static void main(String[] args)
	{
		main();
	}
	static 
	{
          logtalk.root.JavaProtocol.IdErrorInternal = 0;
          logtalk.root.JavaProtocol.IdErrorNoSuchThread = 1;
          logtalk.root.JavaProtocol.IdErrorNoSuchFile = 2;
          logtalk.root.JavaProtocol.IdErrorNoSuchBreakpoint = 3;
          logtalk.root.JavaProtocol.IdErrorBadClassNameRegex = 4;
          logtalk.root.JavaProtocol.IdErrorBadFunctionNameRegex = 5;
          logtalk.root.JavaProtocol.IdErrorNoMatchingFunctions = 6;
          logtalk.root.JavaProtocol.IdErrorBadCount = 7;
          logtalk.root.JavaProtocol.IdErrorCurrentThreadNotStopped = 8;
          logtalk.root.JavaProtocol.IdErrorEvaluatingExpression = 9;
          logtalk.root.JavaProtocol.IdOK = 10;
          logtalk.root.JavaProtocol.IdExited = 11;
          logtalk.root.JavaProtocol.IdDetached = 12;
          logtalk.root.JavaProtocol.IdFiles = 13;
          logtalk.root.JavaProtocol.IdAllClasses = 14;
          logtalk.root.JavaProtocol.IdClasses = 15;
          logtalk.root.JavaProtocol.IdMemBytes = 16;
          logtalk.root.JavaProtocol.IdCompacted = 17;
          logtalk.root.JavaProtocol.IdCollected = 18;
          logtalk.root.JavaProtocol.IdThreadLocation = 19;
          logtalk.root.JavaProtocol.IdFileLineBreakpointNumber = 20;
          logtalk.root.JavaProtocol.IdClassFunctionBreakpointNumber = 21;
          logtalk.root.JavaProtocol.IdBreakpoints = 22;
          logtalk.root.JavaProtocol.IdBreakpointDescription = 23;
          logtalk.root.JavaProtocol.IdBreakpointStatuses = 24;
          logtalk.root.JavaProtocol.IdThreadsWhere = 25;
          logtalk.root.JavaProtocol.IdVariables = 26;
          logtalk.root.JavaProtocol.IdValue = 27;
          logtalk.root.JavaProtocol.IdStructured = 28;
          logtalk.root.JavaProtocol.IdThreadCreated = 29;
          logtalk.root.JavaProtocol.IdThreadTerminated = 30;
          logtalk.root.JavaProtocol.IdThreadStarted = 31;
          logtalk.root.JavaProtocol.IdThreadStopped = 32;
	}
	public    JavaProtocol(logtalk.lang.EmptyObject empty)
	{
		{
		}
		
	}
	
	
	public    JavaProtocol()
	{
		logtalk.root.JavaProtocol.__hx_ctor__JavaProtocol(this);
	}
	
	
	public static   void __hx_ctor__JavaProtocol(logtalk.root.JavaProtocol __temp_me12)
	{
		{
		}
		
	}
	
	
	public static  int IdErrorInternal;
	
	public static  int IdErrorNoSuchThread;
	
	public static  int IdErrorNoSuchFile;
	
	public static  int IdErrorNoSuchBreakpoint;
	
	public static  int IdErrorBadClassNameRegex;
	
	public static  int IdErrorBadFunctionNameRegex;
	
	public static  int IdErrorNoMatchingFunctions;
	
	public static  int IdErrorBadCount;
	
	public static  int IdErrorCurrentThreadNotStopped;
	
	public static  int IdErrorEvaluatingExpression;
	
	public static  int IdOK;
	
	public static  int IdExited;
	
	public static  int IdDetached;
	
	public static  int IdFiles;
	
	public static  int IdAllClasses;
	
	public static  int IdClasses;
	
	public static  int IdMemBytes;
	
	public static  int IdCompacted;
	
	public static  int IdCollected;
	
	public static  int IdThreadLocation;
	
	public static  int IdFileLineBreakpointNumber;
	
	public static  int IdClassFunctionBreakpointNumber;
	
	public static  int IdBreakpoints;
	
	public static  int IdBreakpointDescription;
	
	public static  int IdBreakpointStatuses;
	
	public static  int IdThreadsWhere;
	
	public static  int IdVariables;
	
	public static  int IdValue;
	
	public static  int IdStructured;
	
	public static  int IdThreadCreated;
	
	public static  int IdThreadTerminated;
	
	public static  int IdThreadStarted;
	
	public static  int IdThreadStopped;
	
	public static   void writeServerIdentification(java.io.OutputStream output)
	{
		debugger.LogtalkProtocol.writeServerIdentification(new _JavaProtocol.OutputAdapter(((java.io.OutputStream) (output) )));
	}
	
	
	public static   void readClientIdentification(java.io.InputStream input)
	{
		debugger.LogtalkProtocol.readClientIdentification(new _JavaProtocol.InputAdapter(((java.io.InputStream) (input) )));
	}
	
	
	public static   void writeCommand(java.io.OutputStream output, debugger.Command command)
	{
		debugger.LogtalkProtocol.writeCommand(new _JavaProtocol.OutputAdapter(((java.io.OutputStream) (output) )), command);
	}
	
	
	public static   debugger.Message readMessage(java.io.InputStream input)
	{
		return debugger.LogtalkProtocol.readMessage(new _JavaProtocol.InputAdapter(((java.io.InputStream) (input) )));
	}
	
	
	public static   int getMessageId(debugger.Message message)
	{
		switch (logtalk.root.Type.enumIndex(message))
		{
			case 0:
			{
				java.lang.String details = logtalk.lang.Runtime.toString(message.params.__get(0));
				return logtalk.root.JavaProtocol.IdErrorInternal;
			}
			
			
			case 1:
			{
				int number = ((int) (logtalk.lang.Runtime.toInt(message.params.__get(0))) );
				return logtalk.root.JavaProtocol.IdErrorNoSuchThread;
			}
			
			
			case 2:
			{
				java.lang.String fileName = logtalk.lang.Runtime.toString(message.params.__get(0));
				return logtalk.root.JavaProtocol.IdErrorNoSuchFile;
			}
			
			
			case 3:
			{
				int number = ((int) (logtalk.lang.Runtime.toInt(message.params.__get(0))) );
				return logtalk.root.JavaProtocol.IdErrorNoSuchBreakpoint;
			}
			
			
			case 4:
			{
				java.lang.String details = logtalk.lang.Runtime.toString(message.params.__get(0));
				return logtalk.root.JavaProtocol.IdErrorBadClassNameRegex;
			}
			
			
			case 5:
			{
				java.lang.String details = logtalk.lang.Runtime.toString(message.params.__get(0));
				return logtalk.root.JavaProtocol.IdErrorBadFunctionNameRegex;
			}
			
			
			case 6:
			{
				debugger.StringList u = ((debugger.StringList) (message.params.__get(2)) );
				java.lang.String f = logtalk.lang.Runtime.toString(message.params.__get(1));
				java.lang.String className = logtalk.lang.Runtime.toString(message.params.__get(0));
				return logtalk.root.JavaProtocol.IdErrorNoMatchingFunctions;
			}
			
			
			case 7:
			{
				int count = ((int) (logtalk.lang.Runtime.toInt(message.params.__get(0))) );
				return logtalk.root.JavaProtocol.IdErrorBadCount;
			}
			
			
			case 8:
			{
				int threadNumber = ((int) (logtalk.lang.Runtime.toInt(message.params.__get(0))) );
				return logtalk.root.JavaProtocol.IdErrorCurrentThreadNotStopped;
			}
			
			
			case 9:
			{
				java.lang.String details = logtalk.lang.Runtime.toString(message.params.__get(0));
				return logtalk.root.JavaProtocol.IdErrorEvaluatingExpression;
			}
			
			
			case 10:
			{
				return logtalk.root.JavaProtocol.IdOK;
			}
			
			
			case 11:
			{
				return logtalk.root.JavaProtocol.IdExited;
			}
			
			
			case 12:
			{
				return logtalk.root.JavaProtocol.IdDetached;
			}
			
			
			case 13:
			{
				debugger.StringList list = ((debugger.StringList) (message.params.__get(0)) );
				return logtalk.root.JavaProtocol.IdFiles;
			}
			
			
			case 14:
			{
				debugger.StringList list = ((debugger.StringList) (message.params.__get(0)) );
				return logtalk.root.JavaProtocol.IdAllClasses;
			}
			
			
			case 15:
			{
				debugger.ClassList list = ((debugger.ClassList) (message.params.__get(0)) );
				return logtalk.root.JavaProtocol.IdClasses;
			}
			
			
			case 16:
			{
				int bytes = ((int) (logtalk.lang.Runtime.toInt(message.params.__get(0))) );
				return logtalk.root.JavaProtocol.IdMemBytes;
			}
			
			
			case 17:
			{
				int a = ((int) (logtalk.lang.Runtime.toInt(message.params.__get(1))) );
				int bytesBefore = ((int) (logtalk.lang.Runtime.toInt(message.params.__get(0))) );
				return logtalk.root.JavaProtocol.IdCompacted;
			}
			
			
			case 18:
			{
				int a = ((int) (logtalk.lang.Runtime.toInt(message.params.__get(1))) );
				int bytesBefore = ((int) (logtalk.lang.Runtime.toInt(message.params.__get(0))) );
				return logtalk.root.JavaProtocol.IdCollected;
			}
			
			
			case 19:
			{
				int l = ((int) (logtalk.lang.Runtime.toInt(message.params.__get(5))) );
				java.lang.String fi = logtalk.lang.Runtime.toString(message.params.__get(4));
				java.lang.String f = logtalk.lang.Runtime.toString(message.params.__get(3));
				java.lang.String c = logtalk.lang.Runtime.toString(message.params.__get(2));
				int s = ((int) (logtalk.lang.Runtime.toInt(message.params.__get(1))) );
				int number = ((int) (logtalk.lang.Runtime.toInt(message.params.__get(0))) );
				return logtalk.root.JavaProtocol.IdThreadLocation;
			}
			
			
			case 20:
			{
				int number = ((int) (logtalk.lang.Runtime.toInt(message.params.__get(0))) );
				return logtalk.root.JavaProtocol.IdFileLineBreakpointNumber;
			}
			
			
			case 21:
			{
				debugger.StringList u = ((debugger.StringList) (message.params.__get(1)) );
				int number = ((int) (logtalk.lang.Runtime.toInt(message.params.__get(0))) );
				return logtalk.root.JavaProtocol.IdClassFunctionBreakpointNumber;
			}
			
			
			case 22:
			{
				debugger.BreakpointList list = ((debugger.BreakpointList) (message.params.__get(0)) );
				return logtalk.root.JavaProtocol.IdBreakpoints;
			}
			
			
			case 23:
			{
				debugger.BreakpointLocationList l = ((debugger.BreakpointLocationList) (message.params.__get(1)) );
				int number = ((int) (logtalk.lang.Runtime.toInt(message.params.__get(0))) );
				return logtalk.root.JavaProtocol.IdBreakpointDescription;
			}
			
			
			case 24:
			{
				debugger.BreakpointStatusList list = ((debugger.BreakpointStatusList) (message.params.__get(0)) );
				return logtalk.root.JavaProtocol.IdBreakpointStatuses;
			}
			
			
			case 25:
			{
				debugger.ThreadWhereList list = ((debugger.ThreadWhereList) (message.params.__get(0)) );
				return logtalk.root.JavaProtocol.IdThreadsWhere;
			}
			
			
			case 26:
			{
				debugger.StringList list = ((debugger.StringList) (message.params.__get(0)) );
				return logtalk.root.JavaProtocol.IdVariables;
			}
			
			
			case 28:
			{
				debugger.StructuredValue structuredValue = ((debugger.StructuredValue) (message.params.__get(0)) );
				return logtalk.root.JavaProtocol.IdStructured;
			}
			
			
			case 27:
			{
				java.lang.String v = logtalk.lang.Runtime.toString(message.params.__get(2));
				java.lang.String t = logtalk.lang.Runtime.toString(message.params.__get(1));
				java.lang.String expression = logtalk.lang.Runtime.toString(message.params.__get(0));
				return logtalk.root.JavaProtocol.IdValue;
			}
			
			
			case 29:
			{
				int number = ((int) (logtalk.lang.Runtime.toInt(message.params.__get(0))) );
				return logtalk.root.JavaProtocol.IdThreadCreated;
			}
			
			
			case 30:
			{
				int number = ((int) (logtalk.lang.Runtime.toInt(message.params.__get(0))) );
				return logtalk.root.JavaProtocol.IdThreadTerminated;
			}
			
			
			case 31:
			{
				int number = ((int) (logtalk.lang.Runtime.toInt(message.params.__get(0))) );
				return logtalk.root.JavaProtocol.IdThreadStarted;
			}
			
			
			case 32:
			{
				int l = ((int) (logtalk.lang.Runtime.toInt(message.params.__get(5))) );
				java.lang.String fi = logtalk.lang.Runtime.toString(message.params.__get(4));
				java.lang.String f = logtalk.lang.Runtime.toString(message.params.__get(3));
				java.lang.String c = logtalk.lang.Runtime.toString(message.params.__get(2));
				int s = ((int) (logtalk.lang.Runtime.toInt(message.params.__get(1))) );
				int number = ((int) (logtalk.lang.Runtime.toInt(message.params.__get(0))) );
				return logtalk.root.JavaProtocol.IdThreadStopped;
			}
			
			
		}
		
		return 0;
	}
	
	
	public static   java.lang.String commandToString(debugger.Command command)
	{
		return logtalk.root.Std.string(command);
	}
	
	
	public static   java.lang.String messageToString(debugger.Message message)
	{
		return logtalk.root.Std.string(message);
	}
	
	
	public static   void main()
	{
		java.io.OutputStream stdout = System.out;
		debugger.LogtalkProtocol.writeMessage(new _JavaProtocol.OutputAdapter(((java.io.OutputStream) (stdout) )), debugger.Message.ThreadsWhere(debugger.ThreadWhereList.Where(0, debugger.ThreadStatus.Running, debugger.FrameList.Frame(true, 0, "h", "i", "p", 10, debugger.FrameList.Terminator), debugger.ThreadWhereList.Terminator)));
		logtalk.root.Sys.stderr().writeString("Reading message\n");
		debugger.Message msg = logtalk.root.JavaProtocol.readMessage(System.in);
		logtalk.root.Sys.stderr().writeString("Read message\n");
		logtalk.root.Sys.stderr().writeString((("Message is: " + logtalk.root.Std.string(msg) ) + "\n" ));
	}
	
	
	public static   java.lang.Object __hx_createEmpty()
	{
		return new logtalk.root.JavaProtocol(((logtalk.lang.EmptyObject) (logtalk.lang.EmptyObject.EMPTY) ));
	}
	
	
	public static   java.lang.Object __hx_create(logtalk.root.Array arr)
	{
		return new logtalk.root.JavaProtocol();
	}
	
	
}


