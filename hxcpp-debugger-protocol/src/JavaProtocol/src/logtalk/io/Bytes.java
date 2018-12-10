package logtalk.io;

@SuppressWarnings(value={"rawtypes", "unchecked"})
public  class Bytes extends logtalk.lang.HxObject
{
	public    Bytes(logtalk.lang.EmptyObject empty)
	{
		{
		}
		
	}
	
	
	public    Bytes(int length, byte[] b)
	{
		logtalk.io.Bytes.__hx_ctor_haxe_io_Bytes(this, length, b);
	}
	
	
	public static   void __hx_ctor_haxe_io_Bytes(logtalk.io.Bytes __temp_me29, int length, byte[] b)
	{
		__temp_me29.length = length;
		__temp_me29.b = b;
	}
	
	
	public static   logtalk.io.Bytes alloc(int length)
	{
		return new logtalk.io.Bytes(((int) (length) ), ((byte[]) (new byte[((int) (length) )]) ));
	}
	
	
	public static   logtalk.io.Bytes ofString(java.lang.String s)
	{
		try 
		{
			byte[] b = s.getBytes("UTF-8");
			return new logtalk.io.Bytes(((int) (b.length) ), ((byte[]) (b) ));
		}
		catch (java.lang.Throwable __temp_catchallException188)
		{
			java.lang.Object __temp_catchall189 = __temp_catchallException188;
			if (( __temp_catchall189 instanceof logtalk.lang.LogtalkException ))
			{
				__temp_catchall189 = ((logtalk.lang.LogtalkException) (__temp_catchallException188) ).obj;
			}
			
			{
				java.lang.Object e = __temp_catchall189;
				throw logtalk.lang.LogtalkException.wrap(e);
			}
			
		}
		
		
	}
	
	
	public static   java.lang.Object __hx_createEmpty()
	{
		return new logtalk.io.Bytes(((logtalk.lang.EmptyObject) (logtalk.lang.EmptyObject.EMPTY) ));
	}
	
	
	public static   java.lang.Object __hx_create(logtalk.root.Array arr)
	{
		return new logtalk.io.Bytes(((int) (logtalk.lang.Runtime.toInt(arr.__get(0))) ), ((byte[]) (arr.__get(1)) ));
	}
	
	
	public  int length;
	
	public  byte[] b;
	
	@Override public   java.lang.String toString()
	{
		try 
		{
			return new java.lang.String(((byte[]) (this.b) ), ((int) (0) ), ((int) (this.length) ), logtalk.lang.Runtime.toString("UTF-8"));
		}
		catch (java.lang.Throwable __temp_catchallException181)
		{
			java.lang.Object __temp_catchall182 = __temp_catchallException181;
			if (( __temp_catchall182 instanceof logtalk.lang.LogtalkException ))
			{
				__temp_catchall182 = ((logtalk.lang.LogtalkException) (__temp_catchallException181) ).obj;
			}
			
			{
				java.lang.Object e = __temp_catchall182;
				throw logtalk.lang.LogtalkException.wrap(e);
			}
			
		}
		
		
	}
	
	
	@Override public   double __hx_setField_f(java.lang.String field, double value, boolean handleProperties)
	{
		{
			boolean __temp_executeDef183 = true;
			switch (field.hashCode())
			{
				case -1106363674:
				{
					if (field.equals("length")) 
					{
						__temp_executeDef183 = false;
						this.length = ((int) (value) );
						return value;
					}
					
					break;
				}
				
				
			}
			
			if (__temp_executeDef183) 
			{
				return super.__hx_setField_f(field, value, handleProperties);
			}
			 else 
			{
				throw null;
			}
			
		}
		
	}
	
	
	@Override public   java.lang.Object __hx_setField(java.lang.String field, java.lang.Object value, boolean handleProperties)
	{
		{
			boolean __temp_executeDef184 = true;
			switch (field.hashCode())
			{
				case 98:
				{
					if (field.equals("b")) 
					{
						__temp_executeDef184 = false;
						this.b = ((byte[]) (value) );
						return value;
					}
					
					break;
				}
				
				
				case -1106363674:
				{
					if (field.equals("length")) 
					{
						__temp_executeDef184 = false;
						this.length = ((int) (logtalk.lang.Runtime.toInt(value)) );
						return value;
					}
					
					break;
				}
				
				
			}
			
			if (__temp_executeDef184) 
			{
				return super.__hx_setField(field, value, handleProperties);
			}
			 else 
			{
				throw null;
			}
			
		}
		
	}
	
	
	@Override public   java.lang.Object __hx_getField(java.lang.String field, boolean throwErrors, boolean isCheck, boolean handleProperties)
	{
		{
			boolean __temp_executeDef185 = true;
			switch (field.hashCode())
			{
				case -1776922004:
				{
					if (field.equals("toString")) 
					{
						__temp_executeDef185 = false;
						return ((logtalk.lang.Function) (new logtalk.lang.Closure(((java.lang.Object) (this) ), logtalk.lang.Runtime.toString("toString"))) );
					}
					
					break;
				}
				
				
				case -1106363674:
				{
					if (field.equals("length")) 
					{
						__temp_executeDef185 = false;
						return this.length;
					}
					
					break;
				}
				
				
				case 98:
				{
					if (field.equals("b")) 
					{
						__temp_executeDef185 = false;
						return this.b;
					}
					
					break;
				}
				
				
			}
			
			if (__temp_executeDef185) 
			{
				return super.__hx_getField(field, throwErrors, isCheck, handleProperties);
			}
			 else 
			{
				throw null;
			}
			
		}
		
	}
	
	
	@Override public   double __hx_getField_f(java.lang.String field, boolean throwErrors, boolean handleProperties)
	{
		{
			boolean __temp_executeDef186 = true;
			switch (field.hashCode())
			{
				case -1106363674:
				{
					if (field.equals("length")) 
					{
						__temp_executeDef186 = false;
						return ((double) (this.length) );
					}
					
					break;
				}
				
				
			}
			
			if (__temp_executeDef186) 
			{
				return super.__hx_getField_f(field, throwErrors, handleProperties);
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
			boolean __temp_executeDef187 = true;
			switch (field.hashCode())
			{
				case -1776922004:
				{
					if (field.equals("toString")) 
					{
						__temp_executeDef187 = false;
						return this.toString();
					}
					
					break;
				}
				
				
			}
			
			if (__temp_executeDef187) 
			{
				return super.__hx_invokeField(field, dynargs);
			}
			 else 
			{
				throw null;
			}
			
		}
		
	}
	
	
	@Override public   void __hx_getFields(logtalk.root.Array<java.lang.String> baseArr)
	{
		baseArr.push("b");
		baseArr.push("length");
		{
			super.__hx_getFields(baseArr);
		}
		
	}
	
	
}


