package logtalk;

@SuppressWarnings(value={"rawtypes", "unchecked"})
public  class Serializer extends logtalk.lang.HxObject
{
	static 
	{
          logtalk.Serializer.USE_CACHE = false;
          logtalk.Serializer.USE_ENUM_INDEX = false;
          logtalk.Serializer.BASE64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789%:";
	}
	public    Serializer(logtalk.lang.EmptyObject empty)
	{
		{
		}
		
	}
	
	
	public    Serializer()
	{
		logtalk.Serializer.__hx_ctor_haxe_Serializer(this);
	}
	
	
	public static   void __hx_ctor_haxe_Serializer(logtalk.Serializer __temp_me24)
	{
		__temp_me24.buf = new logtalk.root.StringBuf();
		__temp_me24.cache = new logtalk.root.Array();
		__temp_me24.useCache = logtalk.Serializer.USE_CACHE;
		__temp_me24.useEnumIndex = logtalk.Serializer.USE_ENUM_INDEX;
		__temp_me24.shash = new logtalk.ds.StringMap<java.lang.Object>();
		__temp_me24.scount = 0;
	}
	
	
	public static  boolean USE_CACHE;
	
	public static  boolean USE_ENUM_INDEX;
	
	public static  java.lang.String BASE64;
	
	public static   java.lang.String run(java.lang.Object v)
	{
		logtalk.Serializer s = new logtalk.Serializer();
		s.serialize(v);
		return s.toString();
	}
	
	
	public static   java.lang.Object __hx_createEmpty()
	{
		return new logtalk.Serializer(((logtalk.lang.EmptyObject) (logtalk.lang.EmptyObject.EMPTY) ));
	}
	
	
	public static   java.lang.Object __hx_create(logtalk.root.Array arr)
	{
		return new logtalk.Serializer();
	}
	
	
	public  logtalk.root.StringBuf buf;
	
	public  logtalk.root.Array cache;
	
	public  logtalk.ds.StringMap<java.lang.Object> shash;
	
	public  int scount;
	
	public  boolean useCache;
	
	public  boolean useEnumIndex;
	
	@Override public   java.lang.String toString()
	{
		return this.buf.toString();
	}
	
	
	public   void serializeString(java.lang.String s)
	{
		java.lang.Object x = this.shash.get(s);
		if (( ! (( x == null )) )) 
		{
			this.buf.add("R");
			this.buf.add(x);
			return ;
		}
		
		this.shash.set(s, this.scount++);
		this.buf.add("y");
		s = logtalk.root.StringTools.urlEncode(s);
		this.buf.add(s.length());
		this.buf.add(":");
		this.buf.add(s);
	}
	
	
	public   boolean serializeRef(java.lang.Object v)
	{
		{
			int _g1 = 0;
			int _g = this.cache.length;
			while (( _g1 < _g ))
			{
				int i = _g1++;
				if (logtalk.lang.Runtime.eq(this.cache.__get(i), v))
				{
					this.buf.add("r");
					this.buf.add(i);
					return true;
				}
				
			}
			
		}
		
		this.cache.push(v);
		return false;
	}
	
	
	public   void serializeFields(java.lang.Object v)
	{
		{
			int _g = 0;
			logtalk.root.Array<java.lang.String> _g1 = logtalk.root.Reflect.fields(v);
			while (( _g < _g1.length ))
			{
				java.lang.String f = _g1.__get(_g);
				 ++ _g;
				this.serializeString(f);
				this.serialize(logtalk.root.Reflect.field(v, f));
			}
			
		}
		
		this.buf.add("g");
	}
	
	
	public   void serialize(java.lang.Object v)
	{
		{
			logtalk.root.ValueType _g = logtalk.root.Type.typeof(v);
			switch (logtalk.root.Type.enumIndex(_g))
			{
				case 0:
				{
					this.buf.add("n");
					break;
				}
				
				
				case 1:
				{
					if (logtalk.lang.Runtime.eq(v, 0))
					{
						this.buf.add("z");
						return ;
					}
					
					this.buf.add("i");
					this.buf.add(v);
					break;
				}
				
				
				case 2:
				{
					if (java.lang.Double.isNaN(((double) (logtalk.lang.Runtime.toDouble(v)) )))
					{
						this.buf.add("k");
					}
					 else 
					{
						if ( ! (logtalk.lang.Runtime.isFinite(((double) (logtalk.lang.Runtime.toDouble(v)) ))) )
						{
							this.buf.add((((logtalk.lang.Runtime.compare(v, 0) < 0 )) ? ("m") : ("p") ));
						}
						 else 
						{
							this.buf.add("d");
							this.buf.add(v);
						}
						
					}
					
					break;
				}
				
				
				case 3:
				{
					this.buf.add(((logtalk.lang.Runtime.toBool((v))) ? ("t") : ("f") ));
					break;
				}
				
				
				case 6:
				{
					java.lang.Class c = ((java.lang.Class) (_g.params.__get(0)) );
					{
						if (( ((java.lang.Object) (c) ) == ((java.lang.Object) (java.lang.String.class) ) )) 
						{
							this.serializeString(logtalk.lang.Runtime.toString(v));
							return ;
						}
						
						if (( this.useCache && this.serializeRef(v) )) 
						{
							return ;
						}
						
						{
							java.lang.Class __temp_switch94 = (c);
							if (logtalk.lang.Runtime.eq(__temp_switch94, logtalk.root.Array.class))
							{
								int ucount = 0;
								this.buf.add("a");
								int l = ((int) (logtalk.lang.Runtime.toInt(
                                                                  logtalk.lang.Runtime.getField(v, "length", true))) );
								{
									int _g1 = 0;
									while (( _g1 < ((int) (l) ) ))
									{
										int i = _g1++;
										if ((((java.lang.Object) (logtalk.lang.Runtime.callField(v, "__get", new logtalk.root.Array(new java.lang.Object[]{i}))) ) == null ))
										{
											ucount++;
										}
										 else 
										{
											if (( ucount > 0 )) 
											{
												if (( ucount == 1 )) 
												{
													this.buf.add("n");
												}
												 else 
												{
													this.buf.add("u");
													this.buf.add(ucount);
												}
												
												ucount = 0;
											}
											
											this.serialize(((java.lang.Object) (logtalk.lang.Runtime.callField(v, "__get", new logtalk.root.Array(new java.lang.Object[]{i}))) ));
										}
										
									}
									
								}
								
								if (( ucount > 0 )) 
								{
									if (( ucount == 1 )) 
									{
										this.buf.add("n");
									}
									 else 
									{
										this.buf.add("u");
										this.buf.add(ucount);
									}
									
								}
								
								this.buf.add("h");
							}
							 else 
							{
								if (logtalk.lang.Runtime.eq(__temp_switch94, logtalk.root.List.class))
								{
									this.buf.add("l");
									logtalk.root.List v1 = ((logtalk.root.List) (v) );
									{
										java.lang.Object __temp_iterator41 = v1.iterator();
										while (logtalk.lang.Runtime.toBool(
                                                                                  logtalk.lang.Runtime.callField(__temp_iterator41, "hasNext", null)))
										{
											java.lang.Object i = ((java.lang.Object) (logtalk.lang.Runtime.callField(__temp_iterator41, "next", null)) );
											this.serialize(i);
										}
										
									}
									
									this.buf.add("h");
								}
								 else 
								{
									if (logtalk.lang.Runtime.eq(__temp_switch94, logtalk.root.Date.class))
									{
										logtalk.root.Date d = ((logtalk.root.Date) (v) );
										this.buf.add("v");
										this.buf.add(d.toString());
									}
									 else 
									{
										if (logtalk.lang.Runtime.eq(__temp_switch94, logtalk.ds.StringMap.class))
										{
											this.buf.add("b");
											logtalk.ds.StringMap v1 = ((logtalk.ds.StringMap) (v) );
											{
												java.lang.Object __temp_iterator42 = v1.keys();
												while (logtalk.lang.Runtime.toBool(
                                                                                                  logtalk.lang.Runtime.callField(__temp_iterator42, "hasNext", null)))
												{
													java.lang.String k = logtalk.lang.Runtime.toString(
                                                                                                          logtalk.lang.Runtime.callField(__temp_iterator42, "next", null));
													this.serializeString(k);
													this.serialize(v1.get(k));
												}
												
											}
											
											this.buf.add("h");
										}
										 else 
										{
											if (logtalk.lang.Runtime.eq(__temp_switch94, logtalk.ds.IntMap.class))
											{
												this.buf.add("q");
												logtalk.ds.IntMap v1 = ((logtalk.ds.IntMap) (v) );
												{
													java.lang.Object __temp_iterator43 = v1.keys();
													while (logtalk.lang.Runtime.toBool(
                                                                                                          logtalk.lang.Runtime.callField(__temp_iterator43, "hasNext", null)))
													{
														int k = ((int) (logtalk.lang.Runtime.toInt(
                                                                                                                  logtalk.lang.Runtime.callField(__temp_iterator43, "next", null))) );
														this.buf.add(":");
														this.buf.add(k);
														this.serialize(v1.get(k));
													}
													
												}
												
												this.buf.add("h");
											}
											 else 
											{
												if (logtalk.lang.Runtime.eq(__temp_switch94, logtalk.ds.ObjectMap.class))
												{
													this.buf.add("M");
													logtalk.ds.ObjectMap v1 = ((logtalk.ds.ObjectMap) (v) );
													{
														java.lang.Object __temp_iterator44 = v1.keys();
														while (logtalk.lang.Runtime.toBool(
                                                                                                                  logtalk.lang.Runtime.callField(__temp_iterator44, "hasNext", null)))
														{
															java.lang.Object k = ((java.lang.Object) (logtalk.lang.Runtime.callField(__temp_iterator44, "next", null)) );
															this.serialize(k);
															this.serialize(v1.get(k));
														}
														
													}
													
													this.buf.add("h");
												}
												 else 
												{
													if (logtalk.lang.Runtime.eq(__temp_switch94, logtalk.io.Bytes.class))
													{
														logtalk.io.Bytes v1 = ((logtalk.io.Bytes) (v) );
														int i = 0;
														int max = ( v1.length - 2 );
														logtalk.root.StringBuf charsBuf = new logtalk.root.StringBuf();
														java.lang.String b64 = logtalk.Serializer.BASE64;
														while (( i < max ))
														{
															int b1 = 0;
															{
																int pos = i++;
																b1 = ( v1.b[pos] & 255 );
															}
															
															int b2 = 0;
															{
																int pos = i++;
																b2 = ( v1.b[pos] & 255 );
															}
															
															int b3 = 0;
															{
																int pos = i++;
																b3 = ( v1.b[pos] & 255 );
															}
															
															charsBuf.add(
                                                                                                                          logtalk.lang.StringExt.charAt(b64, (b1 >> 2 )));
															charsBuf.add(
                                                                                                                          logtalk.lang.StringExt.charAt(b64, ((((b1 << 4 ) | (b2 >> 4 ) )) & 63 )));
															charsBuf.add(
                                                                                                                          logtalk.lang.StringExt.charAt(b64, ((((b2 << 2 ) | (b3 >> 6 ) )) & 63 )));
															charsBuf.add(
                                                                                                                          logtalk.lang.StringExt.charAt(b64, (b3 & 63 )));
														}
														
														if (( i == max )) 
														{
															int b1 = 0;
															{
																int pos = i++;
																b1 = ( v1.b[pos] & 255 );
															}
															
															int b2 = 0;
															{
																int pos = i++;
																b2 = ( v1.b[pos] & 255 );
															}
															
															charsBuf.add(
                                                                                                                          logtalk.lang.StringExt.charAt(b64, (b1 >> 2 )));
															charsBuf.add(
                                                                                                                          logtalk.lang.StringExt.charAt(b64, ((((b1 << 4 ) | (b2 >> 4 ) )) & 63 )));
															charsBuf.add(
                                                                                                                          logtalk.lang.StringExt.charAt(b64, ((b2 << 2 ) & 63 )));
														}
														 else 
														{
															if (( i == ( max + 1 ) )) 
															{
																int b1 = 0;
																{
																	int pos = i++;
																	b1 = ( v1.b[pos] & 255 );
																}
																
																charsBuf.add(
                                                                                                                                  logtalk.lang.StringExt.charAt(b64, (b1 >> 2 )));
																charsBuf.add(
                                                                                                                                  logtalk.lang.StringExt.charAt(b64, ((b1 << 4 ) & 63 )));
															}
															
														}
														
														java.lang.String chars = charsBuf.toString();
														this.buf.add("s");
														this.buf.add(chars.length());
														this.buf.add(":");
														this.buf.add(chars);
													}
													 else 
													{
														this.cache.pop();
														if (logtalk.root.Reflect.hasField(v, "hxSerialize"))
														{
															this.buf.add("C");
															this.serializeString(
                                                                                                                          logtalk.root.Type.getClassName(c));
															this.cache.push(v);
															logtalk.lang.Runtime.callField(v, "hxSerialize", new logtalk.root.Array(new java.lang.Object[]{this}));
															this.buf.add("g");
														}
														 else 
														{
															this.buf.add("c");
															this.serializeString(
                                                                                                                          logtalk.root.Type.getClassName(c));
															this.cache.push(v);
															this.serializeFields(v);
														}
														
													}
													
												}
												
											}
											
										}
										
									}
									
								}
								
							}
							
						}
						
					}
					
					break;
				}
				
				
				case 4:
				{
					if (( this.useCache && this.serializeRef(v) )) 
					{
						return ;
					}
					
					this.buf.add("o");
					this.serializeFields(v);
					break;
				}
				
				
				case 7:
				{
					java.lang.Class e = ((java.lang.Class) (_g.params.__get(0)) );
					{
						if (( this.useCache && this.serializeRef(v) )) 
						{
							return ;
						}
						
						this.cache.pop();
						this.buf.add(( (this.useEnumIndex) ? ("j") : ("w") ));
						this.serializeString(logtalk.root.Type.getEnumName(e));
						if (this.useEnumIndex) 
						{
							this.buf.add(":");
							this.buf.add(logtalk.root.Type.enumIndex(v));
						}
						 else 
						{
							this.serializeString(logtalk.root.Type.enumConstructor(v));
						}
						
						this.buf.add(":");
						logtalk.root.Array arr = logtalk.root.Type.enumParameters(v);
						if (( arr != null )) 
						{
							this.buf.add(arr.length);
							{
								int _g1 = 0;
								while (( _g1 < arr.length ))
								{
									java.lang.Object v1 = arr.__get(_g1);
									 ++ _g1;
									this.serialize(v1);
								}
								
							}
							
						}
						 else 
						{
							this.buf.add("0");
						}
						
						this.cache.push(v);
					}
					
					break;
				}
				
				
				case 5:
				{
					throw logtalk.lang.LogtalkException.wrap("Cannot serialize function");
				}
				
				
				default:
				{
					throw logtalk.lang.LogtalkException.wrap(("Cannot serialize " + logtalk.root.Std.string(v) ));
				}
				
			}
			
		}
		
	}
	
	
	@Override public   double __hx_setField_f(java.lang.String field, double value, boolean handleProperties)
	{
		{
			boolean __temp_executeDef95 = true;
			switch (field.hashCode())
			{
				case -907763588:
				{
					if (field.equals("scount")) 
					{
						__temp_executeDef95 = false;
						this.scount = ((int) (value) );
						return value;
					}
					
					break;
				}
				
				
			}
			
			if (__temp_executeDef95) 
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
			boolean __temp_executeDef96 = true;
			switch (field.hashCode())
			{
				case 1284193930:
				{
					if (field.equals("useEnumIndex")) 
					{
						__temp_executeDef96 = false;
						this.useEnumIndex = logtalk.lang.Runtime.toBool(value);
						return value;
					}
					
					break;
				}
				
				
				case 97907:
				{
					if (field.equals("buf")) 
					{
						__temp_executeDef96 = false;
						this.buf = ((logtalk.root.StringBuf) (value) );
						return value;
					}
					
					break;
				}
				
				
				case -309504453:
				{
					if (field.equals("useCache")) 
					{
						__temp_executeDef96 = false;
						this.useCache = logtalk.lang.Runtime.toBool(value);
						return value;
					}
					
					break;
				}
				
				
				case 94416770:
				{
					if (field.equals("cache")) 
					{
						__temp_executeDef96 = false;
						this.cache = ((logtalk.root.Array) (value) );
						return value;
					}
					
					break;
				}
				
				
				case -907763588:
				{
					if (field.equals("scount")) 
					{
						__temp_executeDef96 = false;
						this.scount = ((int) (logtalk.lang.Runtime.toInt(value)) );
						return value;
					}
					
					break;
				}
				
				
				case 109400065:
				{
					if (field.equals("shash")) 
					{
						__temp_executeDef96 = false;
						this.shash = ((logtalk.ds.StringMap<java.lang.Object>) (value) );
						return value;
					}
					
					break;
				}
				
				
			}
			
			if (__temp_executeDef96) 
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
			boolean __temp_executeDef97 = true;
			switch (field.hashCode())
			{
				case -573479200:
				{
					if (field.equals("serialize")) 
					{
						__temp_executeDef97 = false;
						return ((logtalk.lang.Function) (new logtalk.lang.Closure(((java.lang.Object) (this) ), logtalk.lang.Runtime.toString("serialize"))) );
					}
					
					break;
				}
				
				
				case 97907:
				{
					if (field.equals("buf")) 
					{
						__temp_executeDef97 = false;
						return this.buf;
					}
					
					break;
				}
				
				
				case -7657031:
				{
					if (field.equals("serializeFields")) 
					{
						__temp_executeDef97 = false;
						return ((logtalk.lang.Function) (new logtalk.lang.Closure(((java.lang.Object) (this) ), logtalk.lang.Runtime.toString("serializeFields"))) );
					}
					
					break;
				}
				
				
				case 94416770:
				{
					if (field.equals("cache")) 
					{
						__temp_executeDef97 = false;
						return this.cache;
					}
					
					break;
				}
				
				
				case 861138323:
				{
					if (field.equals("serializeRef")) 
					{
						__temp_executeDef97 = false;
						return ((logtalk.lang.Function) (new logtalk.lang.Closure(((java.lang.Object) (this) ), logtalk.lang.Runtime.toString("serializeRef"))) );
					}
					
					break;
				}
				
				
				case 109400065:
				{
					if (field.equals("shash")) 
					{
						__temp_executeDef97 = false;
						return this.shash;
					}
					
					break;
				}
				
				
				case 375065361:
				{
					if (field.equals("serializeString")) 
					{
						__temp_executeDef97 = false;
						return ((logtalk.lang.Function) (new logtalk.lang.Closure(((java.lang.Object) (this) ), logtalk.lang.Runtime.toString("serializeString"))) );
					}
					
					break;
				}
				
				
				case -907763588:
				{
					if (field.equals("scount")) 
					{
						__temp_executeDef97 = false;
						return this.scount;
					}
					
					break;
				}
				
				
				case -1776922004:
				{
					if (field.equals("toString")) 
					{
						__temp_executeDef97 = false;
						return ((logtalk.lang.Function) (new logtalk.lang.Closure(((java.lang.Object) (this) ), logtalk.lang.Runtime.toString("toString"))) );
					}
					
					break;
				}
				
				
				case -309504453:
				{
					if (field.equals("useCache")) 
					{
						__temp_executeDef97 = false;
						return this.useCache;
					}
					
					break;
				}
				
				
				case 1284193930:
				{
					if (field.equals("useEnumIndex")) 
					{
						__temp_executeDef97 = false;
						return this.useEnumIndex;
					}
					
					break;
				}
				
				
			}
			
			if (__temp_executeDef97) 
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
			boolean __temp_executeDef98 = true;
			switch (field.hashCode())
			{
				case -907763588:
				{
					if (field.equals("scount")) 
					{
						__temp_executeDef98 = false;
						return ((double) (this.scount) );
					}
					
					break;
				}
				
				
			}
			
			if (__temp_executeDef98) 
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
			boolean __temp_executeDef99 = true;
			switch (field.hashCode())
			{
				case -573479200:
				{
					if (field.equals("serialize")) 
					{
						__temp_executeDef99 = false;
						this.serialize(dynargs.__get(0));
					}
					
					break;
				}
				
				
				case -1776922004:
				{
					if (field.equals("toString")) 
					{
						__temp_executeDef99 = false;
						return this.toString();
					}
					
					break;
				}
				
				
				case -7657031:
				{
					if (field.equals("serializeFields")) 
					{
						__temp_executeDef99 = false;
						this.serializeFields(dynargs.__get(0));
					}
					
					break;
				}
				
				
				case 375065361:
				{
					if (field.equals("serializeString")) 
					{
						__temp_executeDef99 = false;
						this.serializeString(logtalk.lang.Runtime.toString(dynargs.__get(0)));
					}
					
					break;
				}
				
				
				case 861138323:
				{
					if (field.equals("serializeRef")) 
					{
						__temp_executeDef99 = false;
						return this.serializeRef(dynargs.__get(0));
					}
					
					break;
				}
				
				
			}
			
			if (__temp_executeDef99) 
			{
				return super.__hx_invokeField(field, dynargs);
			}
			
		}
		
		return null;
	}
	
	
	@Override public   void __hx_getFields(logtalk.root.Array<java.lang.String> baseArr)
	{
		baseArr.push("useEnumIndex");
		baseArr.push("useCache");
		baseArr.push("scount");
		baseArr.push("shash");
		baseArr.push("cache");
		baseArr.push("buf");
		{
			super.__hx_getFields(baseArr);
		}
		
	}
	
	
}


