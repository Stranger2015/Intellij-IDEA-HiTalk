package logtalk.root;

@SuppressWarnings(value={"rawtypes", "unchecked"})
public  class StringTools extends logtalk.lang.HxObject
{
	public    StringTools(logtalk.lang.EmptyObject empty)
	{
		{
		}
		
	}
	
	
	public    StringTools()
	{
		logtalk.root.StringTools.__hx_ctor__StringTools(this);
	}
	
	
	public static   void __hx_ctor__StringTools(logtalk.root.StringTools __temp_me20)
	{
		{
		}
		
	}
	
	
	public static   java.lang.String urlEncode(java.lang.String s)
	{
		try 
		{
			return java.net.URLEncoder.encode(s, "UTF-8");
		}
		catch (java.lang.Throwable __temp_catchallException83)
		{
			java.lang.Object __temp_catchall84 = __temp_catchallException83;
			if (( __temp_catchall84 instanceof logtalk.lang.LogtalkException ))
			{
				__temp_catchall84 = ((logtalk.lang.LogtalkException) (__temp_catchallException83) ).obj;
			}
			
			{
				java.lang.Object e = __temp_catchall84;
				throw logtalk.lang.LogtalkException.wrap(e);
			}
			
		}
		
		
	}
	
	
	public static   java.lang.String urlDecode(java.lang.String s)
	{
		try 
		{
			return java.net.URLDecoder.decode(s, "UTF-8");
		}
		catch (java.lang.Throwable __temp_catchallException85)
		{
			java.lang.Object __temp_catchall86 = __temp_catchallException85;
			if (( __temp_catchall86 instanceof logtalk.lang.LogtalkException ))
			{
				__temp_catchall86 = ((logtalk.lang.LogtalkException) (__temp_catchallException85) ).obj;
			}
			
			{
				java.lang.Object e = __temp_catchall86;
				throw logtalk.lang.LogtalkException.wrap(e);
			}
			
		}
		
		
	}
	
	
	public static   java.lang.Object __hx_createEmpty()
	{
		return new logtalk.root.StringTools(((logtalk.lang.EmptyObject) (logtalk.lang.EmptyObject.EMPTY) ));
	}
	
	
	public static   java.lang.Object __hx_create(logtalk.root.Array arr)
	{
		return new logtalk.root.StringTools();
	}
	
	
}


