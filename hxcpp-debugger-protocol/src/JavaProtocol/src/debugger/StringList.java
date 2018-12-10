package debugger;

@SuppressWarnings(value={"rawtypes", "unchecked"})
public  class StringList extends logtalk.lang.Enum
{
	static 
	{
		debugger.StringList.constructs = new logtalk.root.Array<java.lang.String>(new java.lang.String[]{"Terminator", "Element"});
		debugger.StringList.Terminator = new debugger.StringList(((int) (0) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{})) ));
	}
	public    StringList(int index, logtalk.root.Array<java.lang.Object> params)
	{
		super(index, params);
	}
	
	
	public static  logtalk.root.Array<java.lang.String> constructs;
	
	public static  debugger.StringList Terminator;
	
	public static   debugger.StringList Element(java.lang.String string, debugger.StringList next)
	{
		return new debugger.StringList(((int) (1) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{string, next})) ));
	}
	
	
}


