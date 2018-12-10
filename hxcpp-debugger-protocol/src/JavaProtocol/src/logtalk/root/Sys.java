package logtalk.root;

@SuppressWarnings(value={"rawtypes", "unchecked"})
public  class Sys extends logtalk.lang.HxObject
{
	public    Sys(logtalk.lang.EmptyObject empty)
	{
		{
		}
		
	}
	
	
	public    Sys()
	{
		logtalk.root.Sys.__hx_ctor__Sys(this);
	}
	
	
	public static   void __hx_ctor__Sys(logtalk.root.Sys __temp_me21)
	{
		{
		}
		
	}
	
	
	public static   logtalk.io.Output stderr()
	{
		return new logtalk.java.io.NativeOutput(((java.io.OutputStream) (java.lang.System.err) ));
	}
	
	
	public static   java.lang.Object __hx_createEmpty()
	{
		return new logtalk.root.Sys(((logtalk.lang.EmptyObject) (logtalk.lang.EmptyObject.EMPTY) ));
	}
	
	
	public static   java.lang.Object __hx_create(logtalk.root.Array arr)
	{
		return new logtalk.root.Sys();
	}
	
	
}


