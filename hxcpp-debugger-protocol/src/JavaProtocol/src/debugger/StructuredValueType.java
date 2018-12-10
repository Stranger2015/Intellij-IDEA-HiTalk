package debugger;

@SuppressWarnings(value={"rawtypes", "unchecked"})
public  class StructuredValueType extends logtalk.lang.Enum
{
	static 
	{
		debugger.StructuredValueType.constructs = new logtalk.root.Array<java.lang.String>(new java.lang.String[]{"TypeNull", "TypeBool", "TypeInt", "TypeFloat", "TypeString", "TypeInstance", "TypeEnum", "TypeAnonymous", "TypeClass", "TypeFunction", "TypeArray"});
		debugger.StructuredValueType.TypeNull = new debugger.StructuredValueType(((int) (0) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{})) ));
		debugger.StructuredValueType.TypeBool = new debugger.StructuredValueType(((int) (1) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{})) ));
		debugger.StructuredValueType.TypeInt = new debugger.StructuredValueType(((int) (2) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{})) ));
		debugger.StructuredValueType.TypeFloat = new debugger.StructuredValueType(((int) (3) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{})) ));
		debugger.StructuredValueType.TypeString = new debugger.StructuredValueType(((int) (4) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{})) ));
		debugger.StructuredValueType.TypeFunction = new debugger.StructuredValueType(((int) (9) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{})) ));
		debugger.StructuredValueType.TypeArray = new debugger.StructuredValueType(((int) (10) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{})) ));
	}
	public    StructuredValueType(int index, logtalk.root.Array<java.lang.Object> params)
	{
		super(index, params);
	}
	
	
	public static  logtalk.root.Array<java.lang.String> constructs;
	
	public static  debugger.StructuredValueType TypeNull;
	
	public static  debugger.StructuredValueType TypeBool;
	
	public static  debugger.StructuredValueType TypeInt;
	
	public static  debugger.StructuredValueType TypeFloat;
	
	public static  debugger.StructuredValueType TypeString;
	
	public static   debugger.StructuredValueType TypeInstance(java.lang.String className)
	{
		return new debugger.StructuredValueType(((int) (5) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{className})) ));
	}
	
	
	public static   debugger.StructuredValueType TypeEnum(java.lang.String enumName)
	{
		return new debugger.StructuredValueType(((int) (6) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{enumName})) ));
	}
	
	
	public static   debugger.StructuredValueType TypeAnonymous(debugger.StructuredValueTypeList elements)
	{
		return new debugger.StructuredValueType(((int) (7) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{elements})) ));
	}
	
	
	public static   debugger.StructuredValueType TypeClass(java.lang.String className)
	{
		return new debugger.StructuredValueType(((int) (8) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{className})) ));
	}
	
	
	public static  debugger.StructuredValueType TypeFunction;
	
	public static  debugger.StructuredValueType TypeArray;
	
}


