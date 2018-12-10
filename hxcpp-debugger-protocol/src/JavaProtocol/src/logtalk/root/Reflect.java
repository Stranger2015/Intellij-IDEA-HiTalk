package logtalk.root;

@SuppressWarnings(value={"rawtypes", "unchecked"})
public  class Reflect extends logtalk.lang.HxObject
{
	public    Reflect(logtalk.lang.EmptyObject empty)
	{
		{
		}
		
	}
	
	
	public    Reflect()
	{
		logtalk.root.Reflect.__hx_ctor__Reflect(this);
	}
	
	
	public static   void __hx_ctor__Reflect(logtalk.root.Reflect __temp_me18)
	{
		{
		}
		
	}
	
	
	public static   boolean hasField(java.lang.Object o, java.lang.String field)
	{
		
		if (o instanceof logtalk.lang.IHxObject)
		return ((logtalk.lang.IHxObject) o).__hx_getField(field, false, true, false) != logtalk.lang.Runtime.undefined;

		return logtalk.lang.Runtime.slowHasField(o, field);
	
	}
	
	
	public static   java.lang.Object field(java.lang.Object o, java.lang.String field)
	{
		
		if (o instanceof logtalk.lang.IHxObject)
			return ((logtalk.lang.IHxObject) o).__hx_getField(field, false, false, false);

		return logtalk.lang.Runtime.slowGetField(o, field, false);
	
	}
	
	
	public static   void setField(java.lang.Object o, java.lang.String field, java.lang.Object value)
	{
		
		if (o instanceof logtalk.lang.IHxObject)
			((logtalk.lang.IHxObject) o).__hx_setField(field, value, false);
		else
			logtalk.lang.Runtime.slowSetField(o, field, value);
	
	}
	
	
	public static   java.lang.Object getProperty(java.lang.Object o, java.lang.String field)
	{
		
		if (o instanceof logtalk.lang.IHxObject)
			return ((logtalk.lang.IHxObject) o).__hx_getField(field, false, false, true);

		if (logtalk.lang.Runtime.slowHasField(o, "get_" + field))
			return logtalk.lang.Runtime.slowCallField(o, "get_" + field, null);

		return logtalk.lang.Runtime.slowGetField(o, field, false);
	
	}
	
	
	public static   void setProperty(java.lang.Object o, java.lang.String field, java.lang.Object value)
	{
		
		if (o instanceof logtalk.lang.IHxObject)
			((logtalk.lang.IHxObject) o).__hx_setField(field, value, true);
		else if (logtalk.lang.Runtime.slowHasField(o, "set_" + field))
			logtalk.lang.Runtime.slowCallField(o, "set_" + field, new Array(new java.lang.Object[]{value} ));
		else
			logtalk.lang.Runtime.slowSetField(o, field, value);
	
	}
	
	
	public static   java.lang.Object callMethod(java.lang.Object o, java.lang.Object func, logtalk.root.Array args)
	{
		
		return ((logtalk.lang.Function) func).__hx_invokeDynamic(args);
	
	}
	
	
	public static   logtalk.root.Array<java.lang.String> fields(java.lang.Object o)
	{
		
		if (o instanceof logtalk.lang.IHxObject)
		{
			Array<String> ret = new Array<String>();
				((logtalk.lang.IHxObject) o).__hx_getFields(ret);
			return ret;
		} else if (o instanceof java.lang.Class) {
			return Type.getClassFields( (java.lang.Class) o);
		} else {
			return new Array<String>();
		}
	
	}
	
	
	public static   boolean isFunction(java.lang.Object f)
	{
		
		return f instanceof logtalk.lang.Function;
	
	}
	
	
	public static  <T> int compare(T a, T b)
	{
		
		return logtalk.lang.Runtime.compare(a, b);
	
	}
	
	
	public static   boolean compareMethods(java.lang.Object f1, java.lang.Object f2)
	{
		
		if (f1 == f2)
			return true;

		if (f1 instanceof logtalk.lang.Closure && f2 instanceof logtalk.lang.Closure)
		{
			logtalk.lang.Closure f1c = (logtalk.lang.Closure) f1;
			logtalk.lang.Closure f2c = (logtalk.lang.Closure) f2;

			return logtalk.lang.Runtime.refEq(f1c.obj, f2c.obj) && f1c.field.equals(f2c.field);
		}


		return false;
	
	}
	
	
	public static   boolean isObject(java.lang.Object v)
	{
		
		return v != null && !(v instanceof logtalk.lang.Enum || v instanceof logtalk.lang.Function || v instanceof java.lang.Enum || v instanceof java.lang.Number || v instanceof java.lang.Boolean);
	
	}
	
	
	public static   boolean isEnumValue(java.lang.Object v)
	{
		
		return v != null && (v instanceof logtalk.lang.Enum || v instanceof java.lang.Enum);
	
	}
	
	
	public static   boolean deleteField(java.lang.Object o, java.lang.String field)
	{
		
		return (o instanceof logtalk.lang.DynamicObject && ((logtalk.lang.DynamicObject) o).__hx_deleteField(field));
	
	}
	
	
	public static  <T> T copy(T o)
	{
		java.lang.Object o2 = new logtalk.lang.DynamicObject(new logtalk.root.Array<java.lang.String>(new java.lang.String[]{}), new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{}), new logtalk.root.Array<java.lang.String>(new java.lang.String[]{}), new logtalk.root.Array<java.lang.Object>(new java.lang.Object[]{}));
		{
			int _g = 0;
			logtalk.root.Array<java.lang.String> _g1 = logtalk.root.Reflect.fields(o);
			while (( _g < _g1.length ))
			{
				java.lang.String f = _g1.__get(_g);
				 ++ _g;
				logtalk.root.Reflect.setField(o2, f, logtalk.root.Reflect.field(o, f));
			}
			
		}
		
		return ((T) (o2) );
	}
	
	
	public static   java.lang.Object makeVarArgs(logtalk.lang.Function f)
	{
		return new logtalk.lang.VarArgsFunction(((logtalk.lang.Function) (f) ));
	}
	
	
	public static   java.lang.Object __hx_createEmpty()
	{
		return new logtalk.root.Reflect(((logtalk.lang.EmptyObject) (logtalk.lang.EmptyObject.EMPTY) ));
	}
	
	
	public static   java.lang.Object __hx_create(logtalk.root.Array arr)
	{
		return new logtalk.root.Reflect();
	}
	
	
}


