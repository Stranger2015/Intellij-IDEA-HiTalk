package _JavaProtocol;

@SuppressWarnings(value={"rawtypes", "unchecked"})
public  class InputAdapter extends logtalk.io.Input
{
	public    InputAdapter(logtalk.lang.EmptyObject empty)
	{
		super(logtalk.lang.EmptyObject.EMPTY);
	}
	
	
	public    InputAdapter(java.io.InputStream is)
	{
		_JavaProtocol.InputAdapter.__lgt_ctor__JavaProtocol_InputAdapter(this, is);
	}
	
	
	public static   void __lgt_ctor__JavaProtocol_InputAdapter(_JavaProtocol.InputAdapter __temp_me16, java.io.InputStream is)
	{
		__temp_me16.mIs = is;
	}
	
	
	public static   java.lang.Object __lgt_createEmpty()
	{
		return new _JavaProtocol.InputAdapter(logtalk.lang.EmptyObject.EMPTY);
	}
	
	
	public static   java.lang.Object __lgt_create(logtalk.root.Array arr)
	{
		return new _JavaProtocol.InputAdapter(((java.io.InputStream) (arr.__get(0)) ));
	}
	
	
	@Override public   int readBytes(logtalk.io.Bytes bytes, int pos, int len)
	{
		try 
		{
			return this.mIs.read(bytes.b, pos, len);
		}
		catch (java.io.IOException e)
		{
			throw logtalk.lang.LogtalkException.wrap(( "IOException: " + logtalk.root.Std.string(e) ));
		}
		
		
	}
	
	
	public  java.io.InputStream mIs;
	
	@Override
	public   java.lang.Object __lgt_setField(java.lang.String field, java.lang.Object value, boolean handleProperties)
	{
		{
			boolean __temp_executeDef67 = true;
			switch (field.hashCode())
			{
				case 107127:
				{
					if (field.equals("mIs")) 
					{
						__temp_executeDef67 = false;
						this.mIs = ((java.io.InputStream) (value) );
						return value;
					}
					
					break;
				}
				
				
			}
			
			if (__temp_executeDef67) 
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
			boolean __temp_executeDef68 = true;
			switch (field.hashCode())
			{
				case 107127:
				{
					if (field.equals("mIs")) 
					{
						__temp_executeDef68 = false;
						return this.mIs;
					}
					
					break;
				}
				
				
				case -1140063115:
				{
					if (field.equals("readBytes")) 
					{
						__temp_executeDef68 = false;
						return new logtalk.lang.Closure(this, logtalk.lang.Runtime.toString("readBytes"));
					}
					
					break;
				}
				
				
			}
			
			if (__temp_executeDef68) 
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
		baseArr.push("mIs");
		{
			super.__lgt_getFields(baseArr);
		}
		
	}
	
	
}


