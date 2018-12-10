package logtalk.io;

@SuppressWarnings(value={"rawtypes", "unchecked"})
public  class Output extends logtalk.lang.LogtalkObject
{
	public    Output(logtalk.lang.EmptyObject empty)
	{
		{
		}
		
	}
	
	
	public    Output()
	{
		logtalk.io.Output.__lgt_ctor_logtalk_io_Output(this);
	}
	
	
	public static   void __lgt_ctor_logtalk_io_Output(logtalk.io.Output __temp_me13)
	{
		{
		}
		
	}
	
	
	public static   java.lang.Object __lgt_createEmpty()
	{
		return new logtalk.io.Output(logtalk.lang.EmptyObject.EMPTY);
	}
	
	
	public static   java.lang.Object __lgt_create(logtalk.root.Array arr)
	{
		return new logtalk.io.Output();
	}
	
	
	public   void writeByte(int c)
	{
		throw logtalk.lang.LogtalkException.wrap("Not implemented");
	}
	
	
	public   int writeBytes(logtalk.io.Bytes s, int pos, int len)
	{
		int k = len;
		byte[] b = s.b;
		if (( ( ( pos < 0 ) || ( len < 0 ) ) || ( ( pos + len ) > s.length ) )) 
		{
			throw logtalk.lang.LogtalkException.wrap(logtalk.io.Error.OutsideBounds);
		}
		
		while (( k > 0 ))
		{
			this.writeByte(((int) (b[pos]) ));
			pos++;
			k--;
		}
		
		return len;
	}
	
	
	public   void write(logtalk.io.Bytes s)
	{
		int l = s.length;
		int p = 0;
		while (( l > 0 ))
		{
			int k = this.writeBytes(s, p, l);
			if (( k == 0 )) 
			{
				throw logtalk.lang.LogtalkException.wrap(logtalk.io.Error.Blocked);
			}
			
			p += k;
			l -= k;
		}
		
	}
	
	
	public   void writeFullBytes(logtalk.io.Bytes s, int pos, int len)
	{
		while (( len > 0 ))
		{
			int k = this.writeBytes(s, pos, len);
			pos += k;
			len -= k;
		}
		
	}
	
	
	public   void writeString(java.lang.String s)
	{
		logtalk.io.Bytes b = logtalk.io.Bytes.ofString(s);
		this.writeFullBytes(b, 0, b.length);
	}
	
	
	@Override public   java.lang.Object __lgt_getField(java.lang.String field, boolean throwErrors, boolean isCheck, boolean handleProperties)
	{
		{
			boolean __temp_executeDef61 = true;
			switch (field.hashCode())
			{
				case 1412235472:
				{
					if (field.equals("writeString")) 
					{
						__temp_executeDef61 = false;
						return new logtalk.lang.Closure(this, logtalk.lang.Runtime.toString("writeString"));
					}
					
					break;
				}
				
				
				case -1406851705:
				{
					if (field.equals("writeByte")) 
					{
						__temp_executeDef61 = false;
						return new logtalk.lang.Closure(this, logtalk.lang.Runtime.toString("writeByte"));
					}
					
					break;
				}
				
				
				case 1188045309:
				{
					if (field.equals("writeFullBytes")) 
					{
						__temp_executeDef61 = false;
						return new logtalk.lang.Closure(this, logtalk.lang.Runtime.toString("writeFullBytes"));
					}
					
					break;
				}
				
				
				case -662729780:
				{
					if (field.equals("writeBytes")) 
					{
						__temp_executeDef61 = false;
						return new logtalk.lang.Closure(this, logtalk.lang.Runtime.toString("writeBytes"));
					}
					
					break;
				}
				
				
				case 113399775:
				{
					if (field.equals("write")) 
					{
						__temp_executeDef61 = false;
						return new logtalk.lang.Closure(this, logtalk.lang.Runtime.toString("write"));
					}
					
					break;
				}
				
				
			}
			
			if (__temp_executeDef61) 
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
			boolean __temp_executeDef62 = true;
			switch (field.hashCode())
			{
				case 1412235472:
				{
					if (field.equals("writeString")) 
					{
						__temp_executeDef62 = false;
						this.writeString(logtalk.lang.Runtime.toString(dynargs.__get(0)));
					}
					
					break;
				}
				
				
				case -1406851705:
				{
					if (field.equals("writeByte")) 
					{
						__temp_executeDef62 = false;
						this.writeByte(logtalk.lang.Runtime.toInt(dynargs.__get(0)));
					}
					
					break;
				}
				
				
				case 1188045309:
				{
					if (field.equals("writeFullBytes")) 
					{
						__temp_executeDef62 = false;
						this.writeFullBytes(((logtalk.io.Bytes) (dynargs.__get(0)) ),
								    logtalk.lang.Runtime.toInt(dynargs.__get(1)),
								    logtalk.lang.Runtime.toInt(dynargs.__get(2)));
					}
					
					break;
				}
				
				
				case -662729780:
				{
					if (field.equals("writeBytes")) 
					{
						__temp_executeDef62 = false;
						return this.writeBytes(((logtalk.io.Bytes) (dynargs.__get(0)) ),
								       logtalk.lang.Runtime.toInt(dynargs.__get(1)),
								       logtalk.lang.Runtime.toInt(dynargs.__get(2)));
					}
					
					break;
				}
				
				
				case 113399775:
				{
					if (field.equals("write")) 
					{
						__temp_executeDef62 = false;
						this.write(((logtalk.io.Bytes) (dynargs.__get(0)) ));
					}
					
					break;
				}
				
				
			}
			
			if (__temp_executeDef62) 
			{
				return super.__lgt_invokeField(field, dynargs);
			}
			
		}
		
		return null;
	}
	
	
}


