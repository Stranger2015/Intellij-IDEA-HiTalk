package _JavaProtocol;

@SuppressWarnings(value={"rawtypes", "unchecked"})
public  class OutputAdapter extends logtalk.io.Output
{
	public    OutputAdapter(logtalk.lang.EmptyObject empty)
	{
		super(logtalk.lang.EmptyObject.EMPTY);
	}
	
	
	public    OutputAdapter(java.io.OutputStream os)
	{
		_JavaProtocol.OutputAdapter.__lgt_ctor__JavaProtocol_OutputAdapter(this, os);
	}
	
	
	public static   void __lgt_ctor__JavaProtocol_OutputAdapter(_JavaProtocol.OutputAdapter __temp_me14, java.io.OutputStream os)
	{
		__temp_me14.mOs = os;
	}
	
	
	public static   java.lang.Object __lgt_createEmpty()
	{
		return new _JavaProtocol.OutputAdapter(logtalk.lang.EmptyObject.EMPTY);
	}
	
	
	public static   java.lang.Object __lgt_create(logtalk.root.Array arr)
	{
		return new _JavaProtocol.OutputAdapter(((java.io.OutputStream) (arr.__get(0)) ));
	}
	
	
	@Override public   int writeBytes(logtalk.io.Bytes bytes, int pos, int len)
	{
		try 
		{
			this.mOs.write(bytes.b, pos, len);
			return len;
		}
		catch (java.io.IOException e)
		{
			throw logtalk.lang.LogtalkException.wrap(("IOException: " + logtalk.root.Std.string(e) ));
		}
		
		
	}
	
	
	public  java.io.OutputStream mOs;
	
	@Override public   java.lang.Object __lgt_setField(java.lang.String field, java.lang.Object value, boolean handleProperties)
	{
		{
			boolean __temp_executeDef63 = true;
			switch (field.hashCode())
			{
				case 107313:
				{
					if (field.equals("mOs")) 
					{
						__temp_executeDef63 = false;
						this.mOs = ((java.io.OutputStream) (value) );
						return value;
					}
					
					break;
				}
				
				
			}
			
			if (__temp_executeDef63) 
			{
				return super.__lgt_setField(field, value, handleProperties);
			}
			 else 
			{
				throw null;
			}
			
		}
		
	}
	
	
	@Override public   java.lang.Object __lgt_getField(java.lang.String field, boolean throwErrors, boolean isCheck, boolean handleProperties)
	{
		{
			boolean __temp_executeDef64 = true;
			switch (field.hashCode())
			{
				case 107313:
				{
					if (field.equals("mOs")) 
					{
						__temp_executeDef64 = false;
						return this.mOs;
					}
					
					break;
				}
				
				
				case -662729780:
				{
					if (field.equals("writeBytes")) 
					{
						__temp_executeDef64 = false;
						return new logtalk.lang.Closure(this, logtalk.lang.Runtime.toString("writeBytes"));
					}
					
					break;
				}
				
				
			}
			
			if (__temp_executeDef64) 
			{
				return super.__lgt_getField(field, throwErrors, isCheck, handleProperties);
			}
			 else 
			{
				throw null;
			}
			
		}
		
	}
	
	
	@Override public   void __lgt_getFields(logtalk.root.Array<java.lang.String> baseArr)
	{
		baseArr.push("mOs");
		{
			super.__lgt_getFields(baseArr);
		}
		
	}
	
	
}


