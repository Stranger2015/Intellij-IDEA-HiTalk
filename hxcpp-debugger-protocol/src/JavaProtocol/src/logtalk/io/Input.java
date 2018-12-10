package logtalk.io;

@SuppressWarnings(value={"rawtypes", "unchecked"})
public  class Input extends logtalk.lang.LgtObject
{
	public    Input(logtalk.lang.EmptyObject empty)
	{
		{
		}
		
	}
	
	
	public    Input()
	{
		logtalk.io.Input.__lgt_ctor_haxe_io_Input(this);
	}
	
	
	public static   void __lgt_ctor_haxe_io_Input(logtalk.io.Input __temp_me15)
	{
		{
		}
		
	}
	
	
	public static   java.lang.Object __lgt_createEmpty()
	{
		return new logtalk.io.Input(((logtalk.lang.EmptyObject) (logtalk.lang.EmptyObject.EMPTY) ));
	}
	
	
	public static   java.lang.Object __lgt_create(logtalk.root.Array arr)
	{
		return new logtalk.io.Input();
	}
	
	
	public   int readByte()
	{
		throw logtalk.lang.LogtalkException.wrap("Not implemented");
	}
	
	
	public   int readBytes(logtalk.io.Bytes s, int pos, int len)
	{
		int k = len;
		byte[] b = s.b;
		if (( ( ( pos < 0 ) || ( len < 0 ) ) || ( ( pos + len ) > s.length ) )) 
		{
			throw logtalk.lang.LogtalkException.wrap(logtalk.io.Error.OutsideBounds);
		}
		
		while (( k > 0 ))
		{
			b[pos] = ((byte) (this.readByte()) );
			pos++;
			k--;
		}
		
		return len;
	}
	
	
	public   logtalk.io.Bytes read(int nbytes)
	{
		logtalk.io.Bytes s = logtalk.io.Bytes.alloc(nbytes);
		int p = 0;
		while (( nbytes > 0 ))
		{
			int k = this.readBytes(s, p, nbytes);
			if (( k == 0 )) 
			{
				throw logtalk.lang.LogtalkException.wrap(logtalk.io.Error.Blocked);
			}
			
			p += k;
			nbytes -= k;
		}
		
		return s;
	}
	
	
	@Override public   java.lang.Object __lgt_getField(java.lang.String field, boolean throwErrors, boolean isCheck, boolean handleProperties)
	{
		{
			boolean __temp_executeDef65 = true;
			switch (field.hashCode())
			{
				case 3496342:
				{
					if (field.equals("read")) 
					{
						__temp_executeDef65 = false;
						return ((logtalk.lang.Function) (new logtalk.lang.Closure(((java.lang.Object) (this) ), logtalk.lang.Runtime.toString("read"))) );
					}
					
					break;
				}
				
				
				case -868060226:
				{
					if (field.equals("readByte")) 
					{
						__temp_executeDef65 = false;
						return ((logtalk.lang.Function) (new logtalk.lang.Closure(((java.lang.Object) (this) ), logtalk.lang.Runtime.toString("readByte"))) );
					}
					
					break;
				}
				
				
				case -1140063115:
				{
					if (field.equals("readBytes")) 
					{
						__temp_executeDef65 = false;
						return ((logtalk.lang.Function) (new logtalk.lang.Closure(((java.lang.Object) (this) ), logtalk.lang.Runtime.toString("readBytes"))) );
					}
					
					break;
				}
				
				
			}
			
			if (__temp_executeDef65) 
			{
				return super.__lgt_getField(field, throwErrors, isCheck, handleProperties);
			}
			 else 
			{
				throw null;
			}
			
		}
		
	}
	
	
	@Override public   java.lang.Object __lgt_invokeField(java.lang.String field, logtalk.root.Array dynargs)
	{
		{
			boolean __temp_executeDef66 = true;
			switch (field.hashCode())
			{
				case 3496342:
				{
					if (field.equals("read")) 
					{
						__temp_executeDef66 = false;
						return this.read(((int) (logtalk.lang.Runtime.toInt(dynargs.__get(0))) ));
					}
					
					break;
				}
				
				
				case -868060226:
				{
					if (field.equals("readByte")) 
					{
						__temp_executeDef66 = false;
						return this.readByte();
					}
					
					break;
				}
				
				
				case -1140063115:
				{
					if (field.equals("readBytes")) 
					{
						__temp_executeDef66 = false;
						return this.readBytes(((logtalk.io.Bytes) (dynargs.__get(0)) ), ((int) (logtalk.lang.Runtime.toInt(dynargs.__get(1))) ), ((int) (logtalk.lang.Runtime.toInt(dynargs.__get(2))) ));
					}
					
					break;
				}
				
				
			}
			
			if (__temp_executeDef66) 
			{
				return super.__lgt_invokeField(field, dynargs);
			}
			 else 
			{
				throw null;
			}
			
		}
		
	}
	
	
}


