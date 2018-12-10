:- object(thread_status,
      extends(enum(_))).

    :- jpackage([debugger]).

:- end_object.
%package debugger;
%
%@SuppressWarnings(value={"rawtypes", "unchecked"})
%public  class ThreadStatus extends logtalk.lang.Enum
%{
%	static
%	{
%		debugger.ThreadStatus.constructs = new logtalk.root.Array<java.lang.String>(new java.lang.String[]{"Running", "StoppedImmediate", "StoppedBreakpoint", "StoppedUncaughtException", "StoppedCriticalError"});
%		debugger.ThreadStatus.Running = new debugger.ThreadStatus(((int) (0) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{})) ));
%		debugger.ThreadStatus.StoppedImmediate = new debugger.ThreadStatus(((int) (1) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{})) ));
%		debugger.ThreadStatus.StoppedUncaughtException = new debugger.ThreadStatus(((int) (3) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{})) ));
%	}
%	public    ThreadStatus(int index, logtalk.root.Array<java.lang.Object> params)
%	{
%		super(index, params);
%	}
%
%
%	public static  logtalk.root.Array<java.lang.String> constructs;
%
%	public static  debugger.ThreadStatus Running;
%
%	public static  debugger.ThreadStatus StoppedImmediate;
%
%	public static   debugger.ThreadStatus StoppedBreakpoint(int number)
%	{
%		return new debugger.ThreadStatus(((int) (2) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{number})) ));
%	}
%
%
%	public static  debugger.ThreadStatus StoppedUncaughtException;
%
%	public static   debugger.ThreadStatus StoppedCriticalError(java.lang.String description)
%	{
%		return new debugger.ThreadStatus(((int) (4) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{description})) ));
%	}
%
%
%}
%
%
