package debugger;

@SuppressWarnings(value={"rawtypes", "unchecked"})
public  class StructuredValueListType extends logtalk.lang.Enum
{
	static 
	{
		debugger.StructuredValueListType.constructs = new logtalk.root.Array<java.lang.String>(new java.lang.String[]{"Anonymous", "Instance", "_Array", "Class"});
		debugger.StructuredValueListType.Anonymous = new debugger.StructuredValueListType(((int) (0) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{})) ));
		debugger.StructuredValueListType._Array = new debugger.StructuredValueListType(((int) (2) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{})) ));
		debugger.StructuredValueListType.Class = new debugger.StructuredValueListType(((int) (3) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{})) ));
	}
	public    StructuredValueListType(int index, logtalk.root.Array<java.lang.Object> params)
	{
		super(index, params);
	}
	
	
	public static  logtalk.root.Array<java.lang.String> constructs;
	
	public static  debugger.StructuredValueListType Anonymous;
	
	public static   debugger.StructuredValueListType Instance(java.lang.String className)
	{
		return new debugger.StructuredValueListType(((int) (1) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{className})) ));
	}
	
	
	public static  debugger.StructuredValueListType _Array;
	
	public static  debugger.StructuredValueListType Class;
	
}


