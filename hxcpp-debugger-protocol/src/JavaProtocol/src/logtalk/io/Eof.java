package logtalk.io;

@SuppressWarnings(value={"rawtypes", "unchecked"})
public  class Eof extends logtalk.lang.HxObject
{
	public    Eof(logtalk.lang.EmptyObject empty)
	{
		{
		}
		
	}
	
	
	public    Eof()
	{
		logtalk.io.Eof.__hx_ctor_logtalk_io_Eof(this);
	}
	
	
	public static   void __hx_ctor_logtalk_io_Eof(logtalk.io.Eof __temp_me30)
	{
		{
		}
		
	}
	
	
	public static   java.lang.Object __hx_createEmpty()
	{
		return new logtalk.io.Eof(((logtalk.lang.EmptyObject) (logtalk.lang.EmptyObject.EMPTY) ));
	}
	
	
	public static   java.lang.Object __hx_create(logtalk.root.Array arr)
	{
		return new logtalk.io.Eof();
	}
	
	
	@Override public   java.lang.String toString()
	{
		return "Eof";
	}
	
	
	@Override public   java.lang.Object __hx_getField(java.lang.String field, boolean throwErrors, boolean isCheck, boolean handleProperties)
	{
		{
			boolean __temp_executeDef190 = true;
			switch (field.hashCode())
			{
				case -1776922004:
				{
					if (field.equals("toString")) 
					{
						__temp_executeDef190 = false;
						return ((logtalk.lang.Function) (new logtalk.lang.Closure(((java.lang.Object) (this) ), logtalk.lang.Runtime.toString("toString"))) );
					}
					
					break;
				}
				
				
			}
			
			if (__temp_executeDef190) 
			{
				return super.__hx_getField(field, throwErrors, isCheck, handleProperties);
			}
			 else 
			{
				throw null;
			}
			
		}
		
	}
	
	
	@Override public   java.lang.Object __hx_invokeField(java.lang.String field, logtalk.root.Array dynargs)
	{
		{
			boolean __temp_executeDef191 = true;
			switch (field.hashCode())
			{
				case -1776922004:
				{
					if (field.equals("toString")) 
					{
						__temp_executeDef191 = false;
						return this.toString();
					}
					
					break;
				}
				
				
			}
			
			if (__temp_executeDef191) 
			{
				return super.__hx_invokeField(field, dynargs);
			}
			 else 
			{
				throw null;
			}
			
		}
		
	}
	
	
}


