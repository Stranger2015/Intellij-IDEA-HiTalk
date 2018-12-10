package logtalk.lang;

@SuppressWarnings(value={"rawtypes", "unchecked"})
public  class HxObject implements logtalk.lang.IHxObject
{
	public    HxObject(logtalk.lang.EmptyObject empty)
	{
		{
		}
		
	}
	
	
	public    HxObject()
	{
		logtalk.lang.HxObject.__hx_ctor_haxe_lang_HxObject(this);
	}
	
	
	public static   void __hx_ctor_haxe_lang_HxObject(logtalk.lang.HxObject __temp_me32)
	{
		{
		}
		
	}
	
	
	public static   java.lang.Object __hx_createEmpty()
	{
		return new logtalk.lang.HxObject(((logtalk.lang.EmptyObject) (logtalk.lang.EmptyObject.EMPTY) ));
	}
	
	
	public static   java.lang.Object __hx_create(logtalk.root.Array arr)
	{
		return new logtalk.lang.HxObject();
	}
	
	
	public   boolean __hx_deleteField(java.lang.String field)
	{
		return false;
	}
	
	
	public   java.lang.Object __hx_lookupField(java.lang.String field, boolean throwErrors, boolean isCheck)
	{
		if (isCheck) 
		{
			return logtalk.lang.Runtime.undefined;
		}
		 else 
		{
			if (throwErrors) 
			{
				throw logtalk.lang.LogtalkException.wrap("Field not found.");
			}
			 else 
			{
				return null;
			}
			
		}
		
	}
	
	
	public   double __hx_lookupField_f(java.lang.String field, boolean throwErrors)
	{
		if (throwErrors) 
		{
			throw logtalk.lang.LogtalkException.wrap("Field not found or incompatible field type.");
		}
		 else 
		{
			return 0.0;
		}
		
	}
	
	
	public   java.lang.Object __hx_lookupSetField(java.lang.String field, java.lang.Object value)
	{
		throw logtalk.lang.LogtalkException.wrap("Cannot access field for writing.");
	}
	
	
	public   double __hx_lookupSetField_f(java.lang.String field, double value)
	{
		throw logtalk.lang.LogtalkException.wrap("Cannot access field for writing or incompatible type.");
	}
	
	
	public   double __hx_setField_f(java.lang.String field, double value, boolean handleProperties)
	{
		{
			{
				return this.__hx_lookupSetField_f(field, value);
			}
			
		}
		
	}
	
	
	public   java.lang.Object __hx_setField(java.lang.String field, java.lang.Object value, boolean handleProperties)
	{
		{
			{
				return this.__hx_lookupSetField(field, value);
			}
			
		}
		
	}
	
	
	public   java.lang.Object __hx_getField(java.lang.String field, boolean throwErrors, boolean isCheck, boolean handleProperties)
	{
		{
			{
				return this.__hx_lookupField(field, throwErrors, isCheck);
			}
			
		}
		
	}
	
	
	public   double __hx_getField_f(java.lang.String field, boolean throwErrors, boolean handleProperties)
	{
		{
			{
				return this.__hx_lookupField_f(field, throwErrors);
			}
			
		}
		
	}
	
	
	public   java.lang.Object __hx_invokeField(java.lang.String field, logtalk.root.Array dynargs)
	{
		{
			{
				return ((logtalk.lang.Function) (this.__hx_getField(field, true, false, false)) ).__hx_invokeDynamic(dynargs);
			}
			
		}
		
	}
	
	
	public   void __hx_getFields(logtalk.root.Array<java.lang.String> baseArr)
	{
		{
		}
		
	}
	
	
}


