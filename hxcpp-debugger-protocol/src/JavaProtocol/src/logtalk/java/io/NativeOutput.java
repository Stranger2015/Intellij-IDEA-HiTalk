package logtalk.java.io;

@SuppressWarnings(value={"rawtypes", "unchecked"})
public  class NativeOutput extends logtalk.io.Output
{
	public    NativeOutput(logtalk.lang.EmptyObject empty)
	{
		super(((logtalk.lang.EmptyObject) (logtalk.lang.EmptyObject.EMPTY) ));
	}
	
	
	public    NativeOutput(java.io.OutputStream stream)
	{
		logtalk.java.io.NativeOutput.__hx_ctor_haxe_java_io_NativeOutput(this, stream);
	}
	
	
	public static   void __hx_ctor_haxe_java_io_NativeOutput(logtalk.java.io.NativeOutput __temp_me37, java.io.OutputStream stream)
	{
		__temp_me37.stream = stream;
	}
	
	
	public static   java.lang.Object __hx_createEmpty()
	{
		return new logtalk.java.io.NativeOutput(((logtalk.lang.EmptyObject) (logtalk.lang.EmptyObject.EMPTY) ));
	}
	
	
	public static   java.lang.Object __hx_create(logtalk.root.Array arr)
	{
		return new logtalk.java.io.NativeOutput(((java.io.OutputStream) (arr.__get(0)) ));
	}
	
	
	public  java.io.OutputStream stream;
	
	@Override public   void writeByte(int c)
	{
		try 
		{
			this.stream.write(((int) (c) ));
		}
		catch (java.io.EOFException e)
		{
			throw logtalk.lang.LogtalkException.wrap(new logtalk.io.Eof());
		}
		
		catch (java.io.IOException e)
		{
			throw logtalk.lang.LogtalkException.wrap(logtalk.io.Error.Custom(e));
		}
		
		
	}
	
	
	@Override public   java.lang.Object __hx_setField(java.lang.String field, java.lang.Object value, boolean handleProperties)
	{
		{
			boolean __temp_executeDef205 = true;
			switch (field.hashCode())
			{
				case -891990144:
				{
					if (field.equals("stream")) 
					{
						__temp_executeDef205 = false;
						this.stream = ((java.io.OutputStream) (value) );
						return value;
					}
					
					break;
				}
				
				
			}
			
			if (__temp_executeDef205) 
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
			boolean __temp_executeDef206 = true;
			switch (field.hashCode())
			{
				case -1406851705:
				{
					if (field.equals("writeByte")) 
					{
						__temp_executeDef206 = false;
						return ((logtalk.lang.Function) (new logtalk.lang.Closure(((java.lang.Object) (this) ), logtalk.lang.Runtime.toString("writeByte"))) );
					}
					
					break;
				}
				
				
				case -891990144:
				{
					if (field.equals("stream")) 
					{
						__temp_executeDef206 = false;
						return this.stream;
					}
					
					break;
				}
				
				
			}
			
			if (__temp_executeDef206) 
			{
				return super.__hx_getField(field, throwErrors, isCheck, handleProperties);
			}
			 else 
			{
				throw null;
			}
			
		}
		
	}
	
	
	@Override public   void __hx_getFields(logtalk.root.Array<java.lang.String> baseArr)
	{
		baseArr.push("stream");
		{
			super.__hx_getFields(baseArr);
		}
		
	}
	
	
}


