package debugger;

@SuppressWarnings(value={"rawtypes", "unchecked"})
public  class ThreadWhereList extends logtalk.lang.Enum
{
	static 
	{
		debugger.ThreadWhereList.constructs = new logtalk.root.Array<java.lang.String>(new java.lang.String[]{"Terminator", "Where"});
		debugger.ThreadWhereList.Terminator = new debugger.ThreadWhereList(((int) (0) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{})) ));
	}
	public    ThreadWhereList(int index, logtalk.root.Array<java.lang.Object> params)
	{
		super(index, params);
	}
	
	
	public static  logtalk.root.Array<java.lang.String> constructs;
	
	public static  debugger.ThreadWhereList Terminator;
	
	public static   debugger.ThreadWhereList Where(int number, debugger.ThreadStatus status, debugger.FrameList frameList, debugger.ThreadWhereList next)
	{
		return new debugger.ThreadWhereList(((int) (1) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{number, status, frameList, next})) ));
	}
	
	
}


