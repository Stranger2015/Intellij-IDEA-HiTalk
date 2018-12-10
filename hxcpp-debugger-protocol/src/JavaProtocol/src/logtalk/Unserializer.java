package logtalk;

@SuppressWarnings(value={"rawtypes", "unchecked"})
public  class Unserializer extends logtalk.lang.HxObject
{
	static 
	{
          logtalk.Unserializer.DEFAULT_RESOLVER = logtalk.root.Type.class;
          logtalk.Unserializer.BASE64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789%:";
          logtalk.Unserializer.CODES = null;
	}
	public    Unserializer(logtalk.lang.EmptyObject empty)
	{
		{
		}
		
	}
	
	
	public    Unserializer(java.lang.String buf)
	{
		logtalk.Unserializer.__hx_ctor_haxe_Unserializer(this, buf);
	}
	
	
	public static   void __hx_ctor_haxe_Unserializer(logtalk.Unserializer __temp_me25, java.lang.String buf)
	{
		__temp_me25.buf = buf;
		__temp_me25.length = buf.length();
		__temp_me25.pos = 0;
		__temp_me25.scache = new logtalk.root.Array<java.lang.String>();
		__temp_me25.cache = new logtalk.root.Array();
		java.lang.Object r = logtalk.Unserializer.DEFAULT_RESOLVER;
		if (( r == null )) 
		{
			r = logtalk.root.Type.class;
                  logtalk.Unserializer.DEFAULT_RESOLVER = r;
		}
		
		__temp_me25.setResolver(r);
	}
	
	
	public static  java.lang.Object DEFAULT_RESOLVER;
	
	public static  java.lang.String BASE64;
	
	public static  logtalk.root.Array<java.lang.Object> CODES;
	
	public static   logtalk.root.Array<java.lang.Object> initCodes()
	{
		logtalk.root.Array<java.lang.Object> codes = new logtalk.root.Array<java.lang.Object>();
		{
			int _g1 = 0;
			int _g = logtalk.Unserializer.BASE64.length();
			while (( _g1 < _g ))
			{
				int i = _g1++;
				int __temp_stmt135 = 0;
				{
					java.lang.String s = logtalk.Unserializer.BASE64;
					__temp_stmt135 = ( (( i < s.length() )) ? (((int) (s.charAt(i)) )) : (-1) );
				}
				
				codes.__set(__temp_stmt135, i);
			}
			
		}
		
		return codes;
	}
	
	
	public static   java.lang.Object run(java.lang.String v)
	{
		return new logtalk.Unserializer(logtalk.lang.Runtime.toString(v)).unserialize();
	}
	
	
	public static   java.lang.Object __hx_createEmpty()
	{
		return new logtalk.Unserializer(((logtalk.lang.EmptyObject) (logtalk.lang.EmptyObject.EMPTY) ));
	}
	
	
	public static   java.lang.Object __hx_create(logtalk.root.Array arr)
	{
		return new logtalk.Unserializer(logtalk.lang.Runtime.toString(arr.__get(0)));
	}
	
	
	public  java.lang.String buf;
	
	public  int pos;
	
	public  int length;
	
	public  logtalk.root.Array cache;
	
	public  logtalk.root.Array<java.lang.String> scache;
	
	public  java.lang.Object resolver;
	
	public   void setResolver(java.lang.Object r)
	{
		if (( r == null )) 
		{
			{
				logtalk.lang.Function __temp_odecl100 = (((logtalk.Unserializer_setResolver_127__Fun.__hx_current != null )) ? (logtalk.Unserializer_setResolver_127__Fun.__hx_current) : (
                                  logtalk.Unserializer_setResolver_127__Fun.__hx_current = ((logtalk.Unserializer_setResolver_127__Fun) (new logtalk.Unserializer_setResolver_127__Fun()) )) );
				logtalk.lang.Function __temp_odecl101 = (((logtalk.Unserializer_setResolver_128__Fun.__hx_current != null )) ? (logtalk.Unserializer_setResolver_128__Fun.__hx_current) : (
                                  logtalk.Unserializer_setResolver_128__Fun.__hx_current = ((logtalk.Unserializer_setResolver_128__Fun) (new logtalk.Unserializer_setResolver_128__Fun()) )) );
				this.resolver = new logtalk.lang.DynamicObject(new logtalk.root.Array<java.lang.String>(new java.lang.String[]{"resolveClass", "resolveEnum"}), new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{__temp_odecl100, __temp_odecl101}), new logtalk.root.Array<java.lang.String>(new java.lang.String[]{}), new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{}));
			}
			
		}
		 else 
		{
			this.resolver = r;
		}
		
	}
	
	
	public   int readDigits()
	{
		int k = 0;
		boolean s = false;
		int fpos = this.pos;
		while (true)
		{
			int c = 0;
			{
				int p = this.pos;
				{
					java.lang.String s1 = this.buf;
					if (( p < s1.length() )) 
					{
						c = ((int) (s1.charAt(p)) );
					}
					 else 
					{
						c = -1;
					}
					
				}
				
			}
			
			if (( c == -1 )) 
			{
				break;
			}
			
			if (( c == 45 )) 
			{
				if (( this.pos != fpos )) 
				{
					break;
				}
				
				s = true;
				this.pos++;
				continue;
			}
			
			if (( ( c < 48 ) || ( c > 57 ) )) 
			{
				break;
			}
			
			k = ( ( k * 10 ) + (( c - 48 )) );
			this.pos++;
		}
		
		if (s) 
		{
			k *= -1;
		}
		
		return k;
	}
	
	
	public   void unserializeObject(java.lang.Object o)
	{
		while (true)
		{
			if (( this.pos >= this.length )) 
			{
				throw logtalk.lang.LogtalkException.wrap("Invalid object");
			}
			
			int __temp_stmt102 = 0;
			{
				int p = this.pos;
				{
					java.lang.String s = this.buf;
					__temp_stmt102 = ( (( p < s.length() )) ? (((int) (s.charAt(p)) )) : (-1) );
				}
				
			}
			
			if (( __temp_stmt102 == 103 )) 
			{
				break;
			}
			
			java.lang.String k = logtalk.lang.Runtime.toString(this.unserialize());
			if ( ! (( k instanceof java.lang.String )) ) 
			{
				throw logtalk.lang.LogtalkException.wrap("Invalid object key");
			}
			
			java.lang.Object v = this.unserialize();
			logtalk.root.Reflect.setField(o, k, v);
		}
		
		this.pos++;
	}
	
	
	public   java.lang.Object unserializeEnum(java.lang.Class<java.lang.Object> edecl, java.lang.String tag)
	{
		int __temp_stmt103 = 0;
		{
			int p = this.pos++;
			{
				java.lang.String s = this.buf;
				__temp_stmt103 = ( (( p < s.length() )) ? (((int) (s.charAt(p)) )) : (-1) );
			}
			
		}
		
		if (( __temp_stmt103 != 58 )) 
		{
			throw logtalk.lang.LogtalkException.wrap("Invalid enum format");
		}
		
		int nargs = this.readDigits();
		if (( nargs == 0 )) 
		{
			return logtalk.root.Type.createEnum(edecl, tag, null);
		}
		
		logtalk.root.Array args = new logtalk.root.Array();
		while (( nargs-- > 0 ))
		{
			args.push(this.unserialize());
		}
		
		return logtalk.root.Type.createEnum(edecl, tag, args);
	}
	
	
	public   java.lang.Object unserialize()
	{
		{
			int _g = 0;
			{
				int p = this.pos++;
				{
					java.lang.String s = this.buf;
					if (( p < s.length() )) 
					{
						_g = ((int) (s.charAt(p)) );
					}
					 else 
					{
						_g = -1;
					}
					
				}
				
			}
			
			switch (_g)
			{
				case 110:
				{
					return null;
				}
				
				
				case 116:
				{
					return true;
				}
				
				
				case 102:
				{
					return false;
				}
				
				
				case 122:
				{
					return 0;
				}
				
				
				case 105:
				{
					return this.readDigits();
				}
				
				
				case 100:
				{
					int p1 = this.pos;
					while (true)
					{
						int c = 0;
						{
							int p = this.pos;
							{
								java.lang.String s = this.buf;
								if (( p < s.length() )) 
								{
									c = ((int) (s.charAt(p)) );
								}
								 else 
								{
									c = -1;
								}
								
							}
							
						}
						
						if (( ( ( ( c >= 43 ) && ( c < 58 ) ) || ( c == 101 ) ) || ( c == 69 ) )) 
						{
							this.pos++;
						}
						 else 
						{
							break;
						}
						
					}
					
					return logtalk.root.Std.parseFloat(logtalk.lang.StringExt.substr(this.buf, p1, (this.pos - p1 )));
				}
				
				
				case 121:
				{
					int len = this.readDigits();
					int __temp_stmt106 = 0;
					{
						int p = this.pos++;
						{
							java.lang.String s = this.buf;
							__temp_stmt106 = ( (( p < s.length() )) ? (((int) (s.charAt(p)) )) : (-1) );
						}
						
					}
					
					boolean __temp_stmt105 = ( __temp_stmt106 != 58 );
					boolean __temp_boolv107 = false;
					if ( ! (__temp_stmt105) ) 
					{
						__temp_boolv107 = ( ( this.length - this.pos ) < len );
					}
					
					boolean __temp_stmt104 = ( __temp_stmt105 || __temp_boolv107 );
					if (__temp_stmt104) 
					{
						throw logtalk.lang.LogtalkException.wrap("Invalid string length");
					}
					
					java.lang.String s = logtalk.lang.StringExt.substr(this.buf, this.pos, len);
					this.pos += len;
					s = logtalk.root.StringTools.urlDecode(s);
					this.scache.push(s);
					return s;
				}
				
				
				case 107:
				{
					return java.lang.Double.NaN;
				}
				
				
				case 109:
				{
					return java.lang.Double.NEGATIVE_INFINITY;
				}
				
				
				case 112:
				{
					return java.lang.Double.POSITIVE_INFINITY;
				}
				
				
				case 97:
				{
					java.lang.String buf = this.buf;
					logtalk.root.Array a = new logtalk.root.Array();
					this.cache.push(a);
					while (true)
					{
						int c = 0;
						{
							int p = this.pos;
							{
								java.lang.String s = this.buf;
								if (( p < s.length() )) 
								{
									c = ((int) (s.charAt(p)) );
								}
								 else 
								{
									c = -1;
								}
								
							}
							
						}
						
						if (( c == 104 )) 
						{
							this.pos++;
							break;
						}
						
						if (( c == 117 )) 
						{
							this.pos++;
							int n = this.readDigits();
							a.__set(( ( a.length + n ) - 1 ), null);
						}
						 else 
						{
							a.push(this.unserialize());
						}
						
					}
					
					return a;
				}
				
				
				case 111:
				{
					java.lang.Object o = new logtalk.lang.DynamicObject(new logtalk.root.Array<java.lang.String>(new java.lang.String[]{}), new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{}), new logtalk.root.Array<java.lang.String>(new java.lang.String[]{}), new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{}));
					this.cache.push(o);
					this.unserializeObject(o);
					return o;
				}
				
				
				case 114:
				{
					int n = this.readDigits();
					if (( ( n < 0 ) || ( n >= this.cache.length ) )) 
					{
						throw logtalk.lang.LogtalkException.wrap("Invalid reference");
					}
					
					return this.cache.__get(n);
				}
				
				
				case 82:
				{
					int n = this.readDigits();
					if (( ( n < 0 ) || ( n >= this.scache.length ) )) 
					{
						throw logtalk.lang.LogtalkException.wrap("Invalid string reference");
					}
					
					return this.scache.__get(n);
				}
				
				
				case 120:
				{
					throw logtalk.lang.LogtalkException.wrap(this.unserialize());
				}
				
				
				case 99:
				{
					java.lang.String name = logtalk.lang.Runtime.toString(this.unserialize());
					java.lang.Class cl = ((java.lang.Class) (logtalk.lang.Runtime.callField(this.resolver, "resolveClass", new logtalk.root.Array(new java.lang.Object[]{name}))) );
					if (( ((java.lang.Object) (cl) ) == ((java.lang.Object) (null) ) )) 
					{
						throw logtalk.lang.LogtalkException.wrap(("Class not found " + name ));
					}
					
					java.lang.Object o = logtalk.root.Type.createEmptyInstance(cl);
					this.cache.push(o);
					this.unserializeObject(o);
					return o;
				}
				
				
				case 119:
				{
					java.lang.String name = logtalk.lang.Runtime.toString(this.unserialize());
					java.lang.Class edecl = ((java.lang.Class) (logtalk.lang.Runtime.callField(this.resolver, "resolveEnum", new logtalk.root.Array(new java.lang.Object[]{name}))) );
					if (( ((java.lang.Object) (edecl) ) == ((java.lang.Object) (null) ) )) 
					{
						throw logtalk.lang.LogtalkException.wrap(("Enum not found " + name ));
					}
					
					java.lang.Object e = this.unserializeEnum(edecl, logtalk.lang.Runtime.toString(this.unserialize()));
					this.cache.push(e);
					return e;
				}
				
				
				case 106:
				{
					java.lang.String name = logtalk.lang.Runtime.toString(this.unserialize());
					java.lang.Class edecl = ((java.lang.Class) (logtalk.lang.Runtime.callField(this.resolver, "resolveEnum", new logtalk.root.Array(new java.lang.Object[]{name}))) );
					if (( ((java.lang.Object) (edecl) ) == ((java.lang.Object) (null) ) )) 
					{
						throw logtalk.lang.LogtalkException.wrap(("Enum not found " + name ));
					}
					
					this.pos++;
					int index = this.readDigits();
					java.lang.String tag = logtalk.root.Type.getEnumConstructs(edecl).__get(index);
					if (( tag == null )) 
					{
						throw logtalk.lang.LogtalkException.wrap(((("Unknown enum index " + name ) + "@" ) + index ));
					}
					
					java.lang.Object e = this.unserializeEnum(edecl, tag);
					this.cache.push(e);
					return e;
				}
				
				
				case 108:
				{
					logtalk.root.List l = new logtalk.root.List();
					this.cache.push(l);
					java.lang.String buf = this.buf;
					do 
					{
						int __temp_stmt108 = 0;
						{
							int p = this.pos;
							{
								java.lang.String s = this.buf;
								__temp_stmt108 = ( (( p < s.length() )) ? (((int) (s.charAt(p)) )) : (-1) );
							}
							
						}
						
						if (( __temp_stmt108 != 104 )) 
						{
							l.add(this.unserialize());
						}
						 else 
						{
							break;
						}
						
					}
					while (true);
					this.pos++;
					return l;
				}
				
				
				case 98:
				{
					logtalk.ds.StringMap h = new logtalk.ds.StringMap();
					this.cache.push(h);
					java.lang.String buf = this.buf;
					do 
					{
						int __temp_stmt109 = 0;
						{
							int p = this.pos;
							{
								java.lang.String s = this.buf;
								__temp_stmt109 = ( (( p < s.length() )) ? (((int) (s.charAt(p)) )) : (-1) );
							}
							
						}
						
						if (( __temp_stmt109 != 104 )) 
						{
							java.lang.String s = logtalk.lang.Runtime.toString(this.unserialize());
							h.set(s, this.unserialize());
						}
						 else 
						{
							break;
						}
						
					}
					while (true);
					this.pos++;
					return h;
				}
				
				
				case 113:
				{
					logtalk.ds.IntMap h = new logtalk.ds.IntMap();
					this.cache.push(h);
					java.lang.String buf = this.buf;
					int c = 0;
					{
						int p = this.pos++;
						{
							java.lang.String s = this.buf;
							if (( p < s.length() )) 
							{
								c = ((int) (s.charAt(p)) );
							}
							 else 
							{
								c = -1;
							}
							
						}
						
					}
					
					while (( c == 58 ))
					{
						int i = this.readDigits();
						h.set(i, this.unserialize());
						{
							int p = this.pos++;
							{
								java.lang.String s = this.buf;
								if (( p < s.length() )) 
								{
									c = ((int) (s.charAt(p)) );
								}
								 else 
								{
									c = -1;
								}
								
							}
							
						}
						
					}
					
					if (( c != 104 )) 
					{
						throw logtalk.lang.LogtalkException.wrap("Invalid IntMap format");
					}
					
					return h;
				}
				
				
				case 77:
				{
					logtalk.ds.ObjectMap h = new logtalk.ds.ObjectMap();
					this.cache.push(h);
					java.lang.String buf = this.buf;
					do 
					{
						int __temp_stmt110 = 0;
						{
							int p = this.pos;
							{
								java.lang.String s = this.buf;
								__temp_stmt110 = ( (( p < s.length() )) ? (((int) (s.charAt(p)) )) : (-1) );
							}
							
						}
						
						if (( __temp_stmt110 != 104 )) 
						{
							java.lang.Object s = this.unserialize();
							h.set(s, this.unserialize());
						}
						 else 
						{
							break;
						}
						
					}
					while (true);
					this.pos++;
					return h;
				}
				
				
				case 118:
				{
					logtalk.root.Date d = logtalk.root.Date.fromString(
                                          logtalk.lang.StringExt.substr(this.buf, this.pos, 19), null);
					this.cache.push(d);
					this.pos += 19;
					return d;
				}
				
				
				case 115:
				{
					int len = this.readDigits();
					java.lang.String buf = this.buf;
					int __temp_stmt113 = 0;
					{
						int p = this.pos++;
						{
							java.lang.String s = this.buf;
							__temp_stmt113 = ( (( p < s.length() )) ? (((int) (s.charAt(p)) )) : (-1) );
						}
						
					}
					
					boolean __temp_stmt112 = ( __temp_stmt113 != 58 );
					boolean __temp_boolv114 = false;
					if ( ! (__temp_stmt112) ) 
					{
						__temp_boolv114 = ( ( this.length - this.pos ) < len );
					}
					
					boolean __temp_stmt111 = ( __temp_stmt112 || __temp_boolv114 );
					if (__temp_stmt111) 
					{
						throw logtalk.lang.LogtalkException.wrap("Invalid bytes length");
					}
					
					logtalk.root.Array<java.lang.Object> codes = logtalk.Unserializer.CODES;
					if (( codes == null )) 
					{
						codes = logtalk.Unserializer.initCodes();
                                          logtalk.Unserializer.CODES = codes;
					}
					
					int i = this.pos;
					int rest = ( len & 3 );
					int size = 0;
					size = ( ( (( len >> 2 )) * 3 ) + (( (( rest >= 2 )) ? (( rest - 1 )) : (0) )) );
					int max = ( i + (( len - rest )) );
					logtalk.io.Bytes bytes = logtalk.io.Bytes.alloc(size);
					int bpos = 0;
					while (( i < max ))
					{
						int __temp_stmt116 = 0;
						{
							int index = i++;
							__temp_stmt116 = ( (( index < buf.length() )) ? (((int) (buf.charAt(index)) )) : (-1) );
						}
						
						java.lang.Object __temp_stmt115 = codes.__get(__temp_stmt116);
						int c1 = ((int) (logtalk.lang.Runtime.toInt(__temp_stmt115)) );
						int __temp_stmt118 = 0;
						{
							int index = i++;
							__temp_stmt118 = ( (( index < buf.length() )) ? (((int) (buf.charAt(index)) )) : (-1) );
						}
						
						java.lang.Object __temp_stmt117 = codes.__get(__temp_stmt118);
						int c2 = ((int) (logtalk.lang.Runtime.toInt(__temp_stmt117)) );
						{
							int pos = bpos++;
							bytes.b[pos] = ((byte) (( ( c1 << 2 ) | ( c2 >> 4 ) )) );
						}
						
						int __temp_stmt120 = 0;
						{
							int index = i++;
							__temp_stmt120 = ( (( index < buf.length() )) ? (((int) (buf.charAt(index)) )) : (-1) );
						}
						
						java.lang.Object __temp_stmt119 = codes.__get(__temp_stmt120);
						int c3 = ((int) (logtalk.lang.Runtime.toInt(__temp_stmt119)) );
						{
							int pos = bpos++;
							bytes.b[pos] = ((byte) (( ( c2 << 4 ) | ( c3 >> 2 ) )) );
						}
						
						int __temp_stmt122 = 0;
						{
							int index = i++;
							__temp_stmt122 = ( (( index < buf.length() )) ? (((int) (buf.charAt(index)) )) : (-1) );
						}
						
						java.lang.Object __temp_stmt121 = codes.__get(__temp_stmt122);
						int c4 = ((int) (logtalk.lang.Runtime.toInt(__temp_stmt121)) );
						{
							int pos = bpos++;
							bytes.b[pos] = ((byte) (( ( c3 << 6 ) | c4 )) );
						}
						
					}
					
					if (( rest >= 2 )) 
					{
						int __temp_stmt124 = 0;
						{
							int index = i++;
							__temp_stmt124 = ( (( index < buf.length() )) ? (((int) (buf.charAt(index)) )) : (-1) );
						}
						
						java.lang.Object __temp_stmt123 = codes.__get(__temp_stmt124);
						int c1 = ((int) (logtalk.lang.Runtime.toInt(__temp_stmt123)) );
						int __temp_stmt126 = 0;
						{
							int index = i++;
							__temp_stmt126 = ( (( index < buf.length() )) ? (((int) (buf.charAt(index)) )) : (-1) );
						}
						
						java.lang.Object __temp_stmt125 = codes.__get(__temp_stmt126);
						int c2 = ((int) (logtalk.lang.Runtime.toInt(__temp_stmt125)) );
						{
							int pos = bpos++;
							bytes.b[pos] = ((byte) (( ( c1 << 2 ) | ( c2 >> 4 ) )) );
						}
						
						if (( rest == 3 )) 
						{
							int __temp_stmt128 = 0;
							{
								int index = i++;
								__temp_stmt128 = ( (( index < buf.length() )) ? (((int) (buf.charAt(index)) )) : (-1) );
							}
							
							java.lang.Object __temp_stmt127 = codes.__get(__temp_stmt128);
							int c3 = ((int) (logtalk.lang.Runtime.toInt(__temp_stmt127)) );
							{
								int pos = bpos++;
								bytes.b[pos] = ((byte) (( ( c2 << 4 ) | ( c3 >> 2 ) )) );
							}
							
						}
						
					}
					
					this.pos += len;
					this.cache.push(bytes);
					return bytes;
				}
				
				
				case 67:
				{
					java.lang.String name = logtalk.lang.Runtime.toString(this.unserialize());
					java.lang.Class cl = ((java.lang.Class) (logtalk.lang.Runtime.callField(this.resolver, "resolveClass", new logtalk.root.Array(new java.lang.Object[]{name}))) );
					if (( ((java.lang.Object) (cl) ) == ((java.lang.Object) (null) ) )) 
					{
						throw logtalk.lang.LogtalkException.wrap(("Class not found " + name ));
					}
					
					java.lang.Object o = logtalk.root.Type.createEmptyInstance(cl);
					this.cache.push(o);
					logtalk.lang.Runtime.callField(o, "hxUnserialize", new logtalk.root.Array(new java.lang.Object[]{this}));
					int __temp_stmt129 = 0;
					{
						int p = this.pos++;
						{
							java.lang.String s = this.buf;
							__temp_stmt129 = ( (( p < s.length() )) ? (((int) (s.charAt(p)) )) : (-1) );
						}
						
					}
					
					if (( __temp_stmt129 != 103 )) 
					{
						throw logtalk.lang.LogtalkException.wrap("Invalid custom data");
					}
					
					return o;
				}
				
				
				default:
				{
					{
					}
					
					break;
				}
				
			}
			
		}
		
		this.pos--;
		throw logtalk.lang.LogtalkException.wrap(((("Invalid char " + logtalk.lang.StringExt.charAt(this.buf, this.pos) ) + " at position " ) + this.pos ));
	}
	
	
	@Override public   double __hx_setField_f(java.lang.String field, double value, boolean handleProperties)
	{
		{
			boolean __temp_executeDef130 = true;
			switch (field.hashCode())
			{
				case -341328890:
				{
					if (field.equals("resolver")) 
					{
						__temp_executeDef130 = false;
						this.resolver = ((java.lang.Object) (value) );
						return value;
					}
					
					break;
				}
				
				
				case 111188:
				{
					if (field.equals("pos")) 
					{
						__temp_executeDef130 = false;
						this.pos = ((int) (value) );
						return value;
					}
					
					break;
				}
				
				
				case -1106363674:
				{
					if (field.equals("length")) 
					{
						__temp_executeDef130 = false;
						this.length = ((int) (value) );
						return value;
					}
					
					break;
				}
				
				
			}
			
			if (__temp_executeDef130) 
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
			boolean __temp_executeDef131 = true;
			switch (field.hashCode())
			{
				case -341328890:
				{
					if (field.equals("resolver")) 
					{
						__temp_executeDef131 = false;
						this.resolver = ((java.lang.Object) (value) );
						return value;
					}
					
					break;
				}
				
				
				case 97907:
				{
					if (field.equals("buf")) 
					{
						__temp_executeDef131 = false;
						this.buf = logtalk.lang.Runtime.toString(value);
						return value;
					}
					
					break;
				}
				
				
				case -908198161:
				{
					if (field.equals("scache")) 
					{
						__temp_executeDef131 = false;
						this.scache = ((logtalk.root.Array<java.lang.String>) (value) );
						return value;
					}
					
					break;
				}
				
				
				case 111188:
				{
					if (field.equals("pos")) 
					{
						__temp_executeDef131 = false;
						this.pos = ((int) (logtalk.lang.Runtime.toInt(value)) );
						return value;
					}
					
					break;
				}
				
				
				case 94416770:
				{
					if (field.equals("cache")) 
					{
						__temp_executeDef131 = false;
						this.cache = ((logtalk.root.Array) (value) );
						return value;
					}
					
					break;
				}
				
				
				case -1106363674:
				{
					if (field.equals("length")) 
					{
						__temp_executeDef131 = false;
						this.length = ((int) (logtalk.lang.Runtime.toInt(value)) );
						return value;
					}
					
					break;
				}
				
				
			}
			
			if (__temp_executeDef131) 
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
			boolean __temp_executeDef132 = true;
			switch (field.hashCode())
			{
				case -505039769:
				{
					if (field.equals("unserialize")) 
					{
						__temp_executeDef132 = false;
						return ((logtalk.lang.Function) (new logtalk.lang.Closure(((java.lang.Object) (this) ), logtalk.lang.Runtime.toString("unserialize"))) );
					}
					
					break;
				}
				
				
				case 97907:
				{
					if (field.equals("buf")) 
					{
						__temp_executeDef132 = false;
						return this.buf;
					}
					
					break;
				}
				
				
				case 1438134792:
				{
					if (field.equals("unserializeEnum")) 
					{
						__temp_executeDef132 = false;
						return ((logtalk.lang.Function) (new logtalk.lang.Closure(((java.lang.Object) (this) ), logtalk.lang.Runtime.toString("unserializeEnum"))) );
					}
					
					break;
				}
				
				
				case 111188:
				{
					if (field.equals("pos")) 
					{
						__temp_executeDef132 = false;
						return this.pos;
					}
					
					break;
				}
				
				
				case -657057146:
				{
					if (field.equals("unserializeObject")) 
					{
						__temp_executeDef132 = false;
						return ((logtalk.lang.Function) (new logtalk.lang.Closure(((java.lang.Object) (this) ), logtalk.lang.Runtime.toString("unserializeObject"))) );
					}
					
					break;
				}
				
				
				case -1106363674:
				{
					if (field.equals("length")) 
					{
						__temp_executeDef132 = false;
						return this.length;
					}
					
					break;
				}
				
				
				case -940119524:
				{
					if (field.equals("readDigits")) 
					{
						__temp_executeDef132 = false;
						return ((logtalk.lang.Function) (new logtalk.lang.Closure(((java.lang.Object) (this) ), logtalk.lang.Runtime.toString("readDigits"))) );
					}
					
					break;
				}
				
				
				case 94416770:
				{
					if (field.equals("cache")) 
					{
						__temp_executeDef132 = false;
						return this.cache;
					}
					
					break;
				}
				
				
				case 1647991432:
				{
					if (field.equals("setResolver")) 
					{
						__temp_executeDef132 = false;
						return ((logtalk.lang.Function) (new logtalk.lang.Closure(((java.lang.Object) (this) ), logtalk.lang.Runtime.toString("setResolver"))) );
					}
					
					break;
				}
				
				
				case -908198161:
				{
					if (field.equals("scache")) 
					{
						__temp_executeDef132 = false;
						return this.scache;
					}
					
					break;
				}
				
				
				case -341328890:
				{
					if (field.equals("resolver")) 
					{
						__temp_executeDef132 = false;
						return this.resolver;
					}
					
					break;
				}
				
				
			}
			
			if (__temp_executeDef132) 
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
			boolean __temp_executeDef133 = true;
			switch (field.hashCode())
			{
				case -341328890:
				{
					if (field.equals("resolver")) 
					{
						__temp_executeDef133 = false;
						return ((double) (logtalk.lang.Runtime.toDouble(this.resolver)) );
					}
					
					break;
				}
				
				
				case 111188:
				{
					if (field.equals("pos")) 
					{
						__temp_executeDef133 = false;
						return ((double) (this.pos) );
					}
					
					break;
				}
				
				
				case -1106363674:
				{
					if (field.equals("length")) 
					{
						__temp_executeDef133 = false;
						return ((double) (this.length) );
					}
					
					break;
				}
				
				
			}
			
			if (__temp_executeDef133) 
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
			boolean __temp_executeDef134 = true;
			switch (field.hashCode())
			{
				case -505039769:
				{
					if (field.equals("unserialize")) 
					{
						__temp_executeDef134 = false;
						return this.unserialize();
					}
					
					break;
				}
				
				
				case 1647991432:
				{
					if (field.equals("setResolver")) 
					{
						__temp_executeDef134 = false;
						this.setResolver(dynargs.__get(0));
					}
					
					break;
				}
				
				
				case 1438134792:
				{
					if (field.equals("unserializeEnum")) 
					{
						__temp_executeDef134 = false;
						return this.unserializeEnum(((java.lang.Class<java.lang.Object>) (dynargs.__get(0)) ), logtalk.lang.Runtime.toString(dynargs.__get(1)));
					}
					
					break;
				}
				
				
				case -940119524:
				{
					if (field.equals("readDigits")) 
					{
						__temp_executeDef134 = false;
						return this.readDigits();
					}
					
					break;
				}
				
				
				case -657057146:
				{
					if (field.equals("unserializeObject")) 
					{
						__temp_executeDef134 = false;
						this.unserializeObject(dynargs.__get(0));
					}
					
					break;
				}
				
				
			}
			
			if (__temp_executeDef134) 
			{
				return super.__hx_invokeField(field, dynargs);
			}
			
		}
		
		return null;
	}
	
	
	@Override public   void __hx_getFields(logtalk.root.Array<java.lang.String> baseArr)
	{
		baseArr.push("resolver");
		baseArr.push("scache");
		baseArr.push("cache");
		baseArr.push("length");
		baseArr.push("pos");
		baseArr.push("buf");
		{
			super.__hx_getFields(baseArr);
		}
		
	}
	
	
}


