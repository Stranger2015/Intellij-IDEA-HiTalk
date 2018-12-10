package logtalk.io;

@SuppressWarnings(value={"rawtypes", "unchecked"})
public  class Error extends logtalk.lang.Enum
{
	static 
	{
          logtalk.io.Error.constructs = new logtalk.root.Array<java.lang.String>(new java.lang.String[]{"Blocked", "Overflow", "OutsideBounds", "Custom"});
          logtalk.io.Error.Blocked = new logtalk.io.Error(((int) (0) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{})) ));
          logtalk.io.Error.Overflow = new logtalk.io.Error(((int) (1) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{})) ));
          logtalk.io.Error.OutsideBounds = new logtalk.io.Error(((int) (2) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{})) ));
	}
	public    Error(int index, logtalk.root.Array<java.lang.Object> params)
	{
		super(index, params);
	}
	
	
	public static  logtalk.root.Array<java.lang.String> constructs;
	
	public static  logtalk.io.Error Blocked;
	
	public static  logtalk.io.Error Overflow;
	
	public static  logtalk.io.Error OutsideBounds;
	
	public static   logtalk.io.Error Custom(java.lang.Object e)
	{
		return new logtalk.io.Error(((int) (3) ), ((logtalk.root.Array<java.lang.Object>) (new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{e})) ));
	}
	
	
}


