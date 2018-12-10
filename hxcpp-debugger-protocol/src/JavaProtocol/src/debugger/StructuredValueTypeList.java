package debugger;

@SuppressWarnings(value={"rawtypes", "unchecked"})
public  class StructuredValueTypeList extends logtalk.lang.Enum
{
	static 
	{
		debugger.StructuredValueTypeList.constructs = new logtalk.root.Array<java.lang.String>(new java.lang.String[]{"Terminator", "_Type"});
		debugger.StructuredValueTypeList.Terminator = new debugger.StructuredValueTypeList(((int) (0) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{})) ));
	}
	public    StructuredValueTypeList(int index, logtalk.root.Array<java.lang.Object> params)
	{
		super(index, params);
	}
	
	
	public static  logtalk.root.Array<java.lang.String> constructs;
	
	public static  debugger.StructuredValueTypeList Terminator;
	
	public static   debugger.StructuredValueTypeList _Type(debugger.StructuredValueType type, debugger.StructuredValueTypeList next)
	{
		return new debugger.StructuredValueTypeList(((int) (1) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{type, next})) ));
	}
	
	
}


