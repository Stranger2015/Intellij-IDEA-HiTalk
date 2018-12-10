package logtalk.lang;

@SuppressWarnings(value={"rawtypes", "unchecked"})
public  class LogtalkException extends java.lang.RuntimeException
{
	public    LogtalkException(java.lang.Object obj, java.lang.String msg, java.lang.Throwable cause)
	{
		super(msg, cause);
		if (( obj instanceof logtalk.lang.LogtalkException ))
		{
			logtalk.lang.LogtalkException _obj = ((logtalk.lang.LogtalkException) (obj) );
			obj = _obj.getObject();
		}
		
		this.obj = obj;
	}
	
	
	public static   java.lang.RuntimeException wrap(java.lang.Object obj)
	{
		if (( obj instanceof java.lang.RuntimeException )) 
		{
			return ((java.lang.RuntimeException) (obj) );
		}
		
		if (( obj instanceof java.lang.String )) 
		{
			return new logtalk.lang.LogtalkException(((java.lang.Object) (obj) ), logtalk.lang.Runtime.toString(obj), ((java.lang.Throwable) (null) ));
		}
		 else 
		{
			if (( obj instanceof java.lang.Throwable )) 
			{
				return new logtalk.lang.LogtalkException(((java.lang.Object) (obj) ), logtalk.lang.Runtime.toString(null), ((java.lang.Throwable) (obj) ));
			}
			
		}
		
		return new logtalk.lang.LogtalkException(((java.lang.Object) (obj) ), logtalk.lang.Runtime.toString(null), ((java.lang.Throwable) (null) ));
	}
	
	
	public  java.lang.Object obj;
	
	public   java.lang.Object getObject()
	{
		return this.obj;
	}
	
	
	@Override public   java.lang.String toString()
	{
		return ("Logtalk Exception: " + logtalk.root.Std.string(this.obj) );
	}
	
	
}


