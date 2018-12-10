package logtalk.root;

@SuppressWarnings(value={"rawtypes", "unchecked"})
public  class Type extends logtalk.lang.HxObject
{
	public    Type(logtalk.lang.EmptyObject empty)
	{
		{
		}
		
	}
	
	
	public    Type()
	{
		logtalk.root.Type.__hx_ctor__Type(this);
	}
	
	
	public static   void __hx_ctor__Type(logtalk.root.Type __temp_me22)
	{
		{
		}
		
	}
	
	
	public static  <T> java.lang.Class<T> getClass(T o)
	{
		
		if (o == null || o instanceof logtalk.lang.DynamicObject || o instanceof java.lang.Class)
			return null;

		return (java.lang.Class<T>) o.getClass();
	
	}
	
	
	public static   java.lang.Class getEnum(java.lang.Object o)
	{
		
		if (o instanceof java.lang.Enum || o instanceof logtalk.lang.Enum)
			return o.getClass();
		return null;
	
	}
	
	
	public static   java.lang.Class getSuperClass(java.lang.Class c)
	{
		
		java.lang.Class cl = (c == null) ? null : c.getSuperclass();
		if (cl != null && !cl.getName().equals("logtalk.lang.HxObject") && !cl.getName().equals("java.lang.Object") )
			return cl;
		return null;
	
	}
	
	
	public static   java.lang.String getClassName(java.lang.Class c)
	{
		java.lang.Class c1 = c;
		java.lang.String name = c1.getName();
		if (name.startsWith("logtalk.root."))
		{
			return logtalk.lang.StringExt.substr(name, 10, null);
		}
		
		if (name.startsWith("java.lang")) 
		{
			name = logtalk.lang.StringExt.substr(name, 10, null);
		}
		
		{
			java.lang.String __temp_svar87 = (name);
			int __temp_hash89 = __temp_svar87.hashCode();
			boolean __temp_executeDef88 = true;
			switch (__temp_hash89)
			{
				case -672261858:case 104431:
				{
					if (( (( ( __temp_hash89 == -672261858 ) && __temp_svar87.equals("Integer") )) || __temp_svar87.equals("int") )) 
					{
						__temp_executeDef88 = false;
						return "Int";
					}
					
					break;
				}
				
				
				case -1939501217:
				{
					if (__temp_svar87.equals("Object")) 
					{
						__temp_executeDef88 = false;
						return "Dynamic";
					}
					
					break;
				}
				
				
				case 2052876273:case -1325958191:
				{
					if (( (( ( __temp_hash89 == 2052876273 ) && __temp_svar87.equals("Double") )) || __temp_svar87.equals("double") )) 
					{
						__temp_executeDef88 = false;
						return "Float";
					}
					
					break;
				}
				
				
			}
			
			if (__temp_executeDef88) 
			{
				return name;
			}
			 else 
			{
				throw null;
			}
			
		}
		
	}
	
	
	public static   java.lang.String getEnumName(java.lang.Class e)
	{
		java.lang.Class c = e;
		java.lang.String ret = c.getName();
		if (ret.startsWith("logtalk.root."))
		{
			return logtalk.lang.StringExt.substr(ret, 10, null);
		}
		 else 
		{
			if ((logtalk.lang.Runtime.valEq(ret, "boolean") || logtalk.lang.Runtime.valEq(ret, "java.lang.Boolean") ))
			{
				return "Bool";
			}
			
		}
		
		return ret;
	}
	
	
	public static   java.lang.Class resolveClass(java.lang.String name)
	{
		
		try {
			if (name.indexOf(".") == -1)
				name = "logtalk.root." + name;
			return java.lang.Class.forName(name);
		}
		catch (java.lang.ClassNotFoundException e)
		{
			if (name.equals("logtalk.root.Int")) return int.class;
			else if (name.equals("logtalk.root.Float")) return double.class;
			else if (name.equals("logtalk.root.String")) return java.lang.String.class;
			else if (name.equals("logtalk.root.Math")) return java.lang.Math.class;
			else if (name.equals("logtalk.root.Class")) return java.lang.Class.class;
			else if (name.equals("logtalk.root.Dynamic")) return java.lang.Object.class;
			return null;
		}
	
	}
	
	
	public static   java.lang.Class resolveEnum(java.lang.String name)
	{
		
		if ("Bool".equals(name)) return boolean.class;
		Class r = resolveClass(name);
		if (r != null && (r.getSuperclass() == java.lang.Enum.class || r.getSuperclass() == logtalk.lang.Enum.class))
			return r;
		return null;
	
	}
	
	
	public static  <T> T createInstance(java.lang.Class<T> cl, logtalk.root.Array args)
	{
		
			int len = args.length;
			java.lang.Class[] cls = new java.lang.Class[len];
			java.lang.Object[] objs = new java.lang.Object[len];

			java.lang.reflect.Constructor[] ms = cl.getConstructors();
			int msl = ms.length;
			int realMsl = 0;
			for(int i =0; i < msl; i++)
			{
				if (!ms[i].isVarArgs() && ms[i].getParameterTypes().length != len)
				{
					ms[i] = null;
				} else {
					ms[realMsl] = ms[i];
					if (realMsl != i)
						ms[i] = null;
					realMsl++;
				}
			}

			boolean hasNumber = false;

			for (int i = 0; i < len; i++)
			{
				Object o = args.__get(i);
				objs[i]= o;
				cls[i] = o.getClass();
				boolean isNum = false;

				if (o instanceof java.lang.Number)
				{
					cls[i] = java.lang.Number.class;
					isNum = hasNumber = true;
				}

				msl = realMsl;
				realMsl = 0;

				for (int j = 0; j < msl; j++)
				{
					java.lang.Class[] allcls = ms[j].getParameterTypes();
					if (i < allcls.length)
					{
						if (! ((isNum && allcls[i].isPrimitive()) || allcls[i].isAssignableFrom(cls[i])) )
						{
							ms[j] = null;
						} else {
							ms[realMsl] = ms[j];
							if (realMsl != j)
								ms[j] = null;
							realMsl++;
						}
					}
				}

			}

			java.lang.reflect.Constructor found = ms[0];

			if (hasNumber)
			{
				java.lang.Class[] allcls = found.getParameterTypes();

				for (int i = 0; i < len; i++)
				{
					java.lang.Object o = objs[i];
					if (o instanceof java.lang.Number)
					{
						java.lang.Class curCls = null;
						if (i < allcls.length)
						{
							curCls = allcls[i];
							if (!curCls.isAssignableFrom(o.getClass()))
							{
								String name = curCls.getName();
								if (name.equals("double") || name.equals("java.lang.Double"))
								{
									objs[i] = ((java.lang.Number)o).doubleValue();
								} else if (name.equals("int") || name.equals("java.lang.Integer"))
								{
									objs[i] = ((java.lang.Number)o).intValue();
								} else if (name.equals("float") || name.equals("java.lang.Float"))
								{
									objs[i] = ((java.lang.Number)o).floatValue();
								} else if (name.equals("byte") || name.equals("java.lang.Byte"))
								{
									objs[i] = ((java.lang.Number)o).byteValue();
								} else if (name.equals("short") || name.equals("java.lang.Short"))
								{
									objs[i] = ((java.lang.Number)o).shortValue();
								}
							}
						} //else varargs not handled TODO
					}
				}
			}

		try {
			found.setAccessible(true);
			return (T) found.newInstance(objs);
		}
		catch (java.lang.reflect.InvocationTargetException e)
		{
			throw logtalk.lang.LogtalkException.wrap(e.getCause());
		}

		catch (Throwable t)
		{
			throw logtalk.lang.LogtalkException.wrap(t);
		}
	
	}
	
	
	public static  <T> T createEmptyInstance(java.lang.Class<T> cl)
	{
		if (logtalk.root.Reflect.hasField(cl, "__hx_createEmpty"))
		{
			return ((T) (logtalk.lang.Runtime.callField(cl, "__hx_createEmpty", null)) );
		}
		
		return logtalk.root.Type.createInstance(cl, new logtalk.root.Array(new java.lang.Object[]{}));
	}
	
	
	public static  <T> T createEnum(java.lang.Class<T> e, java.lang.String constr, logtalk.root.Array params)
	{
		
		if (params == null || params.length == 0)
		{
			java.lang.Object ret = logtalk.lang.Runtime.slowGetField(e, constr, true);
			if (ret instanceof logtalk.lang.Function)
				throw logtalk.lang.LogtalkException.wrap("Constructor " + constr + " needs parameters");
			return (T) ret;
		} else {
			return (T) logtalk.lang.Runtime.slowCallField(e, constr, params);
		}
	
	}
	
	
	public static  <T> T createEnumIndex(java.lang.Class<T> e, int index, logtalk.root.Array params)
	{
		logtalk.root.Array<java.lang.String> constr = logtalk.root.Type.getEnumConstructs(((java.lang.Class) (e) ));
		return logtalk.root.Type.createEnum(e, constr.__get(index), params);
	}
	
	
	public static   logtalk.root.Array<java.lang.String> getInstanceFields(java.lang.Class c)
	{
		
		if (c == java.lang.String.class)
		{
			return logtalk.lang.StringRefl.fields;
		}

		Array<String> ret = new Array<String>();
		for (java.lang.reflect.Field f : c.getFields())
		{
			java.lang.String fname = f.getName();
			if (!java.lang.reflect.Modifier.isStatic(f.getModifiers()) && !fname.startsWith("__hx_"))
				ret.push(fname);
		}

		for (java.lang.reflect.Method m : c.getMethods())
		{
			if (m.getDeclaringClass() == java.lang.Object.class)
				continue;
			java.lang.String mname = m.getName();
			if (!java.lang.reflect.Modifier.isStatic(m.getModifiers()) && !mname.startsWith("__hx_"))
				ret.push(mname);
		}

		return ret;
	
	}
	
	
	public static   logtalk.root.Array<java.lang.String> getClassFields(java.lang.Class c)
	{
		
		Array<String> ret = new Array<String>();
		if (c == java.lang.String.class)
		{
			ret.push("fromCharCode");
			return ret;
		}

		for (java.lang.reflect.Field f : c.getDeclaredFields())
		{
			java.lang.String fname = f.getName();
			if (java.lang.reflect.Modifier.isStatic(f.getModifiers()) && !fname.startsWith("__hx_"))
			ret.push(fname);
		}

		for (java.lang.reflect.Method m : c.getDeclaredMethods())
		{
			if (m.getDeclaringClass() == java.lang.Object.class)
				continue;
			java.lang.String mname = m.getName();
			if (java.lang.reflect.Modifier.isStatic(m.getModifiers()) && !mname.startsWith("__hx_"))
				ret.push(mname);
		}

		return ret;
	
	}
	
	
	public static   logtalk.root.Array<java.lang.String> getEnumConstructs(java.lang.Class e)
	{
		if (logtalk.root.Reflect.hasField(e, "constructs"))
		{
			return ((logtalk.root.Array<java.lang.String>) (logtalk.lang.Runtime.callField(
                          logtalk.lang.Runtime.getField(e, "constructs", true), "copy", null)) );
		}
		
		java.lang.Enum[] vals = ((java.lang.Enum[]) (logtalk.lang.Runtime.callField(e, "values", null)) );
		logtalk.root.Array<java.lang.String> ret = new logtalk.root.Array<java.lang.String>(new java.lang.String[]{});
		{
			int _g1 = 0;
			int _g = vals.length;
			while (( _g1 < _g ))
			{
				int i = _g1++;
				ret.__set(i, vals[i].name());
			}
			
		}
		
		return ret;
	}
	
	
	public static   logtalk.root.ValueType typeof(java.lang.Object v)
	{
		
		if (v == null) return ValueType.TNull;

		if (v instanceof logtalk.lang.IHxObject) {
			logtalk.lang.IHxObject vobj = (logtalk.lang.IHxObject) v;
			java.lang.Class cl = vobj.getClass();
			if (v instanceof logtalk.lang.DynamicObject)
				return ValueType.TObject;
			else
				return ValueType.TClass(cl);
		} else if (v instanceof java.lang.Number) {
			java.lang.Number n = (java.lang.Number) v;
			if (n.intValue() == n.doubleValue())
				return ValueType.TInt;
			else
				return ValueType.TFloat;
		} else if (v instanceof logtalk.lang.Function) {
			return ValueType.TFunction;
		} else if (v instanceof java.lang.Enum || v instanceof logtalk.lang.Enum) {
			return ValueType.TEnum(v.getClass());
		} else if (v instanceof java.lang.Boolean) {
			return ValueType.TBool;
		} else if (v instanceof java.lang.Class) {
			return ValueType.TObject;
		} else {
			return ValueType.TClass(v.getClass());
		}
	
	}
	
	
	public static  <T> boolean enumEq(T a, T b)
	{
		
			if (a instanceof logtalk.lang.Enum)
				return a.equals(b);
			else
				return logtalk.lang.Runtime.eq(a, b);
	
	}
	
	
	public static   java.lang.String enumConstructor(java.lang.Object e)
	{
		
		if (e instanceof java.lang.Enum)
			return ((java.lang.Enum) e).name();
		else
			return ((logtalk.lang.Enum) e).getTag();
	
	}
	
	
	public static   logtalk.root.Array enumParameters(java.lang.Object e)
	{
		
		return ( e instanceof java.lang.Enum ) ? new logtalk.root.Array() : ((logtalk.lang.Enum) e).params;
	
	}
	
	
	public static   int enumIndex(java.lang.Object e)
	{
		
		if (e instanceof java.lang.Enum)
			return ((java.lang.Enum) e).ordinal();
		else
			return ((logtalk.lang.Enum) e).index;
	
	}
	
	
	public static  <T> logtalk.root.Array<T> allEnums(java.lang.Class<T> e)
	{
		logtalk.root.Array<java.lang.String> ctors = logtalk.root.Type.getEnumConstructs(((java.lang.Class) (e) ));
		logtalk.root.Array<T> ret = new logtalk.root.Array<T>(((T[]) (new java.lang.Object[] {}) ));
		{
			int _g = 0;
			while (( _g < ctors.length ))
			{
				java.lang.String ctor = ctors.__get(_g);
				 ++ _g;
				T v = ((T) (logtalk.root.Reflect.field(e, ctor)) );
				if (logtalk.root.Std.is(v, e))
				{
					ret.push(v);
				}
				
			}
			
		}
		
		return ret;
	}
	
	
	public static   java.lang.Object __hx_createEmpty()
	{
		return new logtalk.root.Type(((logtalk.lang.EmptyObject) (logtalk.lang.EmptyObject.EMPTY) ));
	}
	
	
	public static   java.lang.Object __hx_create(logtalk.root.Array arr)
	{
		return new logtalk.root.Type();
	}
	
	
}


