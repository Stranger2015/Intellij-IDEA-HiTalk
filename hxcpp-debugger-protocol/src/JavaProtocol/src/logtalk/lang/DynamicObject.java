package logtalk.lang;

@SuppressWarnings(value={"rawtypes", "unchecked"})
public  class DynamicObject extends logtalk.lang.HxObject
{
	public    DynamicObject(logtalk.lang.EmptyObject empty)
	{
		super(logtalk.lang.EmptyObject.EMPTY);
	}
	
	
	public    DynamicObject()
	{
		logtalk.lang.DynamicObject.__hx_ctor_haxe_lang_DynamicObject(((logtalk.lang.DynamicObject) (this) ));
	}
	
	
	public    DynamicObject(logtalk.root.Array<java.lang.String> __hx_hashes, logtalk.root.Array<java.lang.Object> __hx_dynamics, logtalk.root.Array<java.lang.String> __hx_hashes_f, logtalk.root.Array<java.lang.Object> __hx_dynamics_f)
	{
		logtalk.lang.DynamicObject.__hx_ctor_haxe_lang_DynamicObject(((logtalk.lang.DynamicObject) (this) ), ((logtalk.root.Array<java.lang.String>) (__hx_hashes) ), ((logtalk.root.Array<java.lang.Object>) (__hx_dynamics) ), ((logtalk.root.Array<java.lang.String>) (__hx_hashes_f) ), ((logtalk.root.Array<java.lang.Object>) (__hx_dynamics_f) ));
	}
	
	
	public static   void __hx_ctor_haxe_lang_DynamicObject(logtalk.lang.DynamicObject __temp_me34)
	{
		__temp_me34.__hx_hashes = new logtalk.root.Array<java.lang.String>(new java.lang.String[]{});
		__temp_me34.__hx_dynamics = new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{});
		__temp_me34.__hx_hashes_f = new logtalk.root.Array<java.lang.String>(new java.lang.String[]{});
		__temp_me34.__hx_dynamics_f = new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{});
	}
	
	
	public static   void __hx_ctor_haxe_lang_DynamicObject(logtalk.lang.DynamicObject __temp_me33, logtalk.root.Array<java.lang.String> __hx_hashes, logtalk.root.Array<java.lang.Object> __hx_dynamics, logtalk.root.Array<java.lang.String> __hx_hashes_f, logtalk.root.Array<java.lang.Object> __hx_dynamics_f)
	{
		__temp_me33.__hx_hashes = __hx_hashes;
		__temp_me33.__hx_dynamics = __hx_dynamics;
		__temp_me33.__hx_hashes_f = __hx_hashes_f;
		__temp_me33.__hx_dynamics_f = __hx_dynamics_f;
	}
	
	
	public static   java.lang.Object __hx_createEmpty()
	{
		return new logtalk.lang.DynamicObject(((logtalk.lang.EmptyObject) (logtalk.lang.EmptyObject.EMPTY) ));
	}
	
	
	public static   java.lang.Object __hx_create(logtalk.root.Array arr)
	{
		return new logtalk.lang.DynamicObject(((logtalk.root.Array<java.lang.String>) (arr.__get(0)) ), ((logtalk.root.Array<java.lang.Object>) (arr.__get(1)) ), ((logtalk.root.Array<java.lang.String>) (arr.__get(2)) ), ((logtalk.root.Array<java.lang.Object>) (arr.__get(3)) ));
	}
	
	
	@Override public   java.lang.String toString()
	{
		logtalk.lang.Function ts = ((logtalk.lang.Function) (logtalk.lang.Runtime.getField(this, "toString", false)) );
		if (( ts != null )) 
		{
			return logtalk.lang.Runtime.toString(ts.__hx_invoke0_o());
		}
		
		logtalk.root.StringBuf ret = new logtalk.root.StringBuf();
		ret.add("{");
		boolean first = true;
		{
			int _g = 0;
			logtalk.root.Array<java.lang.String> _g1 = logtalk.root.Reflect.fields(this);
			while (( _g < _g1.length ))
			{
				java.lang.String f = _g1.__get(_g);
				 ++ _g;
				if (first) 
				{
					first = false;
				}
				 else 
				{
					ret.add(",");
				}
				
				ret.add(" ");
				ret.add(f);
				ret.add(" : ");
				ret.add(logtalk.root.Reflect.field(this, f));
			}
			
		}
		
		if ( ! (first) ) 
		{
			ret.add(" ");
		}
		
		ret.add("}");
		return ret.toString();
	}
	
	
	@Override public   boolean __hx_deleteField(java.lang.String field)
	{
		int res = logtalk.lang.FieldLookup.findHash(field, this.__hx_hashes);
		if (( res >= 0 )) 
		{
			this.__hx_hashes.splice(res, 1);
			this.__hx_dynamics.splice(res, 1);
			return true;
		}
		 else 
		{
			res = logtalk.lang.FieldLookup.findHash(field, this.__hx_hashes_f);
			if (( res >= 0 )) 
			{
				this.__hx_hashes_f.splice(res, 1);
				this.__hx_dynamics_f.splice(res, 1);
				return true;
			}
			
		}
		
		return false;
	}
	
	
	public  logtalk.root.Array<java.lang.String> __hx_hashes = new logtalk.root.Array<java.lang.String>(new java.lang.String[]{});
	
	public  logtalk.root.Array<java.lang.Object> __hx_dynamics = new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{});
	
	public  logtalk.root.Array<java.lang.String> __hx_hashes_f = new logtalk.root.Array<java.lang.String>(new java.lang.String[]{});
	
	public  logtalk.root.Array<java.lang.Object> __hx_dynamics_f = new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{});
	
	@Override public   java.lang.Object __hx_lookupField(java.lang.String field, boolean throwErrors, boolean isCheck)
	{
		int res = logtalk.lang.FieldLookup.findHash(field, this.__hx_hashes);
		if (( res >= 0 )) 
		{
			return this.__hx_dynamics.__get(res);
		}
		 else 
		{
			res = logtalk.lang.FieldLookup.findHash(field, this.__hx_hashes_f);
			if (( res >= 0 )) 
			{
				return ((double) (logtalk.lang.Runtime.toDouble(this.__hx_dynamics_f.__get(res))) );
			}
			
		}
		
		if (isCheck) 
		{
			return logtalk.lang.Runtime.undefined;
		}
		 else 
		{
			return null;
		}
		
	}
	
	
	@Override public   double __hx_lookupField_f(java.lang.String field, boolean throwErrors)
	{
		int res = logtalk.lang.FieldLookup.findHash(field, this.__hx_hashes_f);
		if (( res >= 0 )) 
		{
			return ((double) (logtalk.lang.Runtime.toDouble(this.__hx_dynamics_f.__get(res))) );
		}
		 else 
		{
			res = logtalk.lang.FieldLookup.findHash(field, this.__hx_hashes);
			if (( res >= 0 )) 
			{
				return ((double) (logtalk.lang.Runtime.toDouble(this.__hx_dynamics.__get(res))) );
			}
			
		}
		
		return 0.0;
	}
	
	
	@Override public   java.lang.Object __hx_lookupSetField(java.lang.String field, java.lang.Object value)
	{
		int res = logtalk.lang.FieldLookup.findHash(field, this.__hx_hashes);
		if (( res >= 0 )) 
		{
			return this.__hx_dynamics.__set(res, value);
		}
		 else 
		{
			int res2 = logtalk.lang.FieldLookup.findHash(field, this.__hx_hashes_f);
			if (( res >= 0 )) 
			{
				this.__hx_hashes_f.splice(res2, 1);
				this.__hx_dynamics_f.splice(res2, 1);
			}
			
		}
		
		this.__hx_hashes.insert( ~ (res) , field);
		this.__hx_dynamics.insert( ~ (res) , value);
		return value;
	}
	
	
	@Override public   double __hx_lookupSetField_f(java.lang.String field, double value)
	{
		int res = logtalk.lang.FieldLookup.findHash(field, this.__hx_hashes_f);
		if (( res >= 0 )) 
		{
			return ((double) (logtalk.lang.Runtime.toDouble(this.__hx_dynamics_f.__set(res, value))) );
		}
		 else 
		{
			int res2 = logtalk.lang.FieldLookup.findHash(field, this.__hx_hashes);
			if (( res >= 0 )) 
			{
				this.__hx_hashes.splice(res2, 1);
				this.__hx_dynamics.splice(res2, 1);
			}
			
		}
		
		this.__hx_hashes_f.insert( ~ (res) , field);
		this.__hx_dynamics_f.insert( ~ (res) , value);
		return value;
	}
	
	
	@Override public   void __hx_getFields(logtalk.root.Array<java.lang.String> baseArr)
	{
		{
			{
				java.lang.Object __temp_iterator45 = this.__hx_hashes.iterator();
				while (logtalk.lang.Runtime.toBool(logtalk.lang.Runtime.callField(__temp_iterator45, "hasNext", null)))
				{
					java.lang.String __temp_field36 = logtalk.lang.Runtime.toString(
                                          logtalk.lang.Runtime.callField(__temp_iterator45, "next", null));
					baseArr.push(__temp_field36);
				}
				
			}
			
			{
				java.lang.Object __temp_iterator46 = this.__hx_hashes_f.iterator();
				while (logtalk.lang.Runtime.toBool(logtalk.lang.Runtime.callField(__temp_iterator46, "hasNext", null)))
				{
					java.lang.String __temp_field35 = logtalk.lang.Runtime.toString(
                                          logtalk.lang.Runtime.callField(__temp_iterator46, "next", null));
					baseArr.push(__temp_field35);
				}
				
			}
			
			super.__hx_getFields(baseArr);
		}
		
	}
	
	
}


