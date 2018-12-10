package logtalk.root;

@SuppressWarnings(value={"rawtypes", "unchecked"})
public  class ValueType extends logtalk.lang.Enum
{
	static 
	{
          logtalk.root.ValueType.constructs = new logtalk.root.Array<java.lang.String>(new java.lang.String[]{"TNull", "TInt", "TFloat", "TBool", "TObject", "TFunction", "TClass", "TEnum", "TUnknown"});
          logtalk.root.ValueType.TNull = new logtalk.root.ValueType(((int) (0) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{})) ));
          logtalk.root.ValueType.TInt = new logtalk.root.ValueType(((int) (1) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{})) ));
          logtalk.root.ValueType.TFloat = new logtalk.root.ValueType(((int) (2) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{})) ));
          logtalk.root.ValueType.TBool = new logtalk.root.ValueType(((int) (3) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{})) ));
          logtalk.root.ValueType.TObject = new logtalk.root.ValueType(((int) (4) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{})) ));
          logtalk.root.ValueType.TFunction = new logtalk.root.ValueType(((int) (5) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{})) ));
          logtalk.root.ValueType.TUnknown = new logtalk.root.ValueType(((int) (8) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{})) ));
	}
	public    ValueType(int index, logtalk.root.Array<java.lang.Object> params)
	{
		super(index, params);
	}
	
	
	public static  logtalk.root.Array<java.lang.String> constructs;
	
	public static  logtalk.root.ValueType TNull;
	
	public static  logtalk.root.ValueType TInt;
	
	public static  logtalk.root.ValueType TFloat;
	
	public static  logtalk.root.ValueType TBool;
	
	public static  logtalk.root.ValueType TObject;
	
	public static  logtalk.root.ValueType TFunction;
	
	public static   logtalk.root.ValueType TClass(java.lang.Class c)
	{
		return new logtalk.root.ValueType(((int) (6) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{c})) ));
	}
	
	
	public static   logtalk.root.ValueType TEnum(java.lang.Class e)
	{
		return new logtalk.root.ValueType(((int) (7) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{e})) ));
	}
	
	
	public static  logtalk.root.ValueType TUnknown;
	
}


