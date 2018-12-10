package debugger;

@SuppressWarnings(value={"rawtypes", "unchecked"})
public  class StructuredValue extends logtalk.lang.Enum
{
	static 
	{
		debugger.StructuredValue.constructs = new logtalk.root.Array<java.lang.String>(new java.lang.String[]{"Elided", "Single", "List"});
	}
	public    StructuredValue(int index, logtalk.root.Array<java.lang.Object> params)
	{
		super(index, params);
	}
	
	
	public static  logtalk.root.Array<java.lang.String> constructs;
	
	public static   debugger.StructuredValue Elided(debugger.StructuredValueType type, java.lang.String getExpression)
	{
		return new debugger.StructuredValue(((int) (0) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{type, getExpression})) ));
	}
	
	
	public static   debugger.StructuredValue Single(debugger.StructuredValueType type, java.lang.String value)
	{
		return new debugger.StructuredValue(((int) (1) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{type, value})) ));
	}
	
	
	public static   debugger.StructuredValue List(debugger.StructuredValueListType type, debugger.StructuredValueList list)
	{
		return new debugger.StructuredValue(((int) (2) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{type, list})) ));
	}
	
	
}


