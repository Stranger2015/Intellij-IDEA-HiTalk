package logtalk.root;

@SuppressWarnings(value={"rawtypes", "unchecked"})
public  class Date extends logtalk.lang.HxObject
{
	public    Date(logtalk.lang.EmptyObject empty)
	{
		{
		}
		
	}
	
	
	public    Date(int year, int month, int day, java.lang.Object hour, java.lang.Object min, java.lang.Object sec, java.lang.Object millisec)
	{
		logtalk.root.Date.__hx_ctor__Date(this, year, month, day, hour, min, sec, millisec);
	}
	
	
	public static   void __hx_ctor__Date(logtalk.root.Date __temp_me11, int year, int month, int day, java.lang.Object hour, java.lang.Object min, java.lang.Object sec, java.lang.Object millisec)
	{
		int __temp_millisec10 = ( (( millisec == null )) ? (((int) (0) )) : (((int) (logtalk.lang.Runtime.toInt(millisec)) )) );
		int __temp_sec9 = ( (( sec == null )) ? (((int) (0) )) : (((int) (logtalk.lang.Runtime.toInt(sec)) )) );
		int __temp_min8 = ( (( min == null )) ? (((int) (0) )) : (((int) (logtalk.lang.Runtime.toInt(min)) )) );
		int __temp_hour7 = ( (( hour == null )) ? (((int) (0) )) : (((int) (logtalk.lang.Runtime.toInt(hour)) )) );
		if (( year != 0 )) 
		{
			year = ( year - 1900 );
		}
		 else 
		{
			year = 0;
		}
		
		__temp_me11.date = new java.util.Date(((int) (year) ), ((int) (month) ), ((int) (day) ), ((int) (__temp_hour7) ), ((int) (__temp_min8) ), ((int) (__temp_sec9) ));
		if (( __temp_millisec10 > 0 )) 
		{
			long __temp_stmt60 = 0L;
			{
				long a = __temp_me11.date.getTime();
				long b = 0L;
				{
					long i = 0L;
					i = ((long) (( ( ((long) (0) ) << 32 ) | ( __temp_millisec10 & 0xffffffffL ) )) );
					b = i;
				}
				
				__temp_stmt60 = ((long) (( ((long) (a) ) + ((long) (b) ) )) );
			}
			
			__temp_me11.date = new java.util.Date(((long) (__temp_stmt60) ));
		}
		
	}
	
	
	public static   logtalk.root.Date fromUTC(int year, int month, int day, java.lang.Object hour, java.lang.Object min, java.lang.Object sec, java.lang.Object millisec)
	{
		int __temp_millisec5 = ( (( millisec == null )) ? (((int) (0) )) : (((int) (logtalk.lang.Runtime.toInt(millisec)) )) );
		int __temp_sec4 = ( (( sec == null )) ? (((int) (0) )) : (((int) (logtalk.lang.Runtime.toInt(sec)) )) );
		int __temp_min3 = ( (( min == null )) ? (((int) (0) )) : (((int) (logtalk.lang.Runtime.toInt(min)) )) );
		int __temp_hour2 = ( (( hour == null )) ? (((int) (0) )) : (((int) (logtalk.lang.Runtime.toInt(hour)) )) );
		logtalk.root.Date d = new logtalk.root.Date(((int) (year) ), ((int) (month) ), ((int) (day) ), ((java.lang.Object) (__temp_hour2) ), ((java.lang.Object) (__temp_min3) ), ((java.lang.Object) (__temp_sec4) ), ((java.lang.Object) (__temp_millisec5) ));
		return logtalk.root.Date.fromTime(((((((double) (d.date.getTime()) ) / 1000 ) + d.timezoneOffset() )) * 1000 ));
	}
	
	
	public static   logtalk.root.Date fromTime(double t)
	{
		logtalk.root.Date d = new logtalk.root.Date(((int) (0) ), ((int) (0) ), ((int) (0) ), ((java.lang.Object) (0) ), ((java.lang.Object) (0) ), ((java.lang.Object) (0) ), ((java.lang.Object) (null) ));
		d.date = new java.util.Date(((long) (t) ));
		return d;
	}
	
	
	public static   logtalk.root.Date fromString(java.lang.String s, java.lang.Object isUtc)
	{
		boolean __temp_isUtc6 = ((( isUtc == null )) ? (logtalk.lang.Runtime.toBool(false)) : (logtalk.lang.Runtime.toBool(isUtc)) );
		int _g = s.length();
		switch (_g)
		{
			case 8:
			{
				logtalk.root.Array<java.lang.String> k = logtalk.lang.StringExt.split(s, ":");
				if (__temp_isUtc6) 
				{
					return logtalk.root.Date.fromUTC(0, 0, 1, logtalk.root.Std.parseInt(k.__get(0)), logtalk.root.Std.parseInt(k.__get(1)), logtalk.root.Std.parseInt(k.__get(2)), null);
				}
				 else 
				{
					return new logtalk.root.Date(((int) (0) ), ((int) (0) ), ((int) (1) ), ((java.lang.Object) (logtalk.root.Std.parseInt(k.__get(0))) ), ((java.lang.Object) (logtalk.root.Std.parseInt(k.__get(1))) ), ((java.lang.Object) (logtalk.root.Std.parseInt(k.__get(2))) ), ((java.lang.Object) (null) ));
				}
				
			}
			
			
			case 10:
			{
				logtalk.root.Array<java.lang.String> k = logtalk.lang.StringExt.split(s, "-");
				if (__temp_isUtc6) 
				{
					return logtalk.root.Date.fromUTC(((int) (logtalk.lang.Runtime.toInt(
                                          logtalk.root.Std.parseInt(k.__get(0)))) ), (((int) (logtalk.lang.Runtime.toInt(
                                          logtalk.root.Std.parseInt(k.__get(1)))) ) - ((int) (1) ) ), ((int) (logtalk.lang.Runtime.toInt(
                                          logtalk.root.Std.parseInt(k.__get(2)))) ), null, null, null, null);
				}
				 else 
				{
					return new logtalk.root.Date(((int) (logtalk.lang.Runtime.toInt(logtalk.root.Std.parseInt(k.__get(0)))) ), ((int) ((((int) (logtalk.lang.Runtime.toInt(
                                          logtalk.root.Std.parseInt(k.__get(1)))) ) - ((int) (1) ) )) ), ((int) (logtalk.lang.Runtime.toInt(
                                          logtalk.root.Std.parseInt(k.__get(2)))) ), ((java.lang.Object) (null) ), ((java.lang.Object) (null) ), ((java.lang.Object) (null) ), ((java.lang.Object) (null) ));
				}
				
			}
			
			
			case 19:
			{
				logtalk.root.Array<java.lang.String> k = logtalk.lang.StringExt.split(s, " ");
				logtalk.root.Array<java.lang.String> y = logtalk.lang.StringExt.split(k.__get(0), "-");
				logtalk.root.Array<java.lang.String> t = logtalk.lang.StringExt.split(k.__get(1), ":");
				if (__temp_isUtc6) 
				{
					return logtalk.root.Date.fromUTC(((int) (logtalk.lang.Runtime.toInt(
                                          logtalk.root.Std.parseInt(y.__get(0)))) ), (((int) (logtalk.lang.Runtime.toInt(
                                          logtalk.root.Std.parseInt(y.__get(1)))) ) - ((int) (1) ) ), ((int) (logtalk.lang.Runtime.toInt(
                                          logtalk.root.Std.parseInt(y.__get(2)))) ), logtalk.root.Std.parseInt(t.__get(0)), logtalk.root.Std.parseInt(t.__get(1)), logtalk.root.Std.parseInt(t.__get(2)), null);
				}
				 else 
				{
					return new logtalk.root.Date(((int) (logtalk.lang.Runtime.toInt(logtalk.root.Std.parseInt(y.__get(0)))) ), ((int) ((((int) (logtalk.lang.Runtime.toInt(
                                          logtalk.root.Std.parseInt(y.__get(1)))) ) - ((int) (1) ) )) ), ((int) (logtalk.lang.Runtime.toInt(
                                          logtalk.root.Std.parseInt(y.__get(2)))) ), ((java.lang.Object) (logtalk.root.Std.parseInt(t.__get(0))) ), ((java.lang.Object) (logtalk.root.Std.parseInt(t.__get(1))) ), ((java.lang.Object) (logtalk.root.Std.parseInt(t.__get(2))) ), ((java.lang.Object) (null) ));
				}
				
			}
			
			
			default:
			{
				throw logtalk.lang.LogtalkException.wrap(("Invalid date format : " + s ));
			}
			
		}
		
	}
	
	
	public static   java.lang.Object __hx_createEmpty()
	{
		return new logtalk.root.Date(((logtalk.lang.EmptyObject) (logtalk.lang.EmptyObject.EMPTY) ));
	}
	
	
	public static   java.lang.Object __hx_create(logtalk.root.Array arr)
	{
		return new logtalk.root.Date(((int) (logtalk.lang.Runtime.toInt(arr.__get(0))) ), ((int) (logtalk.lang.Runtime.toInt(arr.__get(1))) ), ((int) (logtalk.lang.Runtime.toInt(arr.__get(2))) ), ((java.lang.Object) (arr.__get(3)) ), ((java.lang.Object) (arr.__get(4)) ), ((java.lang.Object) (arr.__get(5)) ), ((java.lang.Object) (arr.__get(6)) ));
	}
	
	
	public  java.util.Date date;
	
	public   int timezoneOffset()
	{
		return this.date.getTimezoneOffset();
	}
	
	
	@Override public   java.lang.String toString()
	{
		int m = ( this.date.getMonth() + 1 );
		int d = this.date.getDate();
		int h = this.date.getHours();
		int mi = this.date.getMinutes();
		int s = this.date.getSeconds();
		return ( ( ( ( ( ( ( ( ( ( ( this.date.getYear() + 1900 ) + "-" ) + (( (( m < 10 )) ? (( "0" + m )) : (( "" + m )) )) ) + "-" ) + (( (( d < 10 )) ? (( "0" + d )) : (( "" + d )) )) ) + " " ) + (( (( h < 10 )) ? (( "0" + h )) : (( "" + h )) )) ) + ":" ) + (( (( mi < 10 )) ? (( "0" + mi )) : (( "" + mi )) )) ) + ":" ) + (( (( s < 10 )) ? (( "0" + s )) : (( "" + s )) )) );
	}
	
	
	@Override public   java.lang.Object __hx_setField(java.lang.String field, java.lang.Object value, boolean handleProperties)
	{
		{
			boolean __temp_executeDef57 = true;
			switch (field.hashCode())
			{
				case 3076014:
				{
					if (field.equals("date")) 
					{
						__temp_executeDef57 = false;
						this.date = ((java.util.Date) (value) );
						return value;
					}
					
					break;
				}
				
				
			}
			
			if (__temp_executeDef57) 
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
			boolean __temp_executeDef58 = true;
			switch (field.hashCode())
			{
				case -1776922004:
				{
					if (field.equals("toString")) 
					{
						__temp_executeDef58 = false;
						return ((logtalk.lang.Function) (new logtalk.lang.Closure(((java.lang.Object) (this) ), logtalk.lang.Runtime.toString("toString"))) );
					}
					
					break;
				}
				
				
				case 3076014:
				{
					if (field.equals("date")) 
					{
						__temp_executeDef58 = false;
						return this.date;
					}
					
					break;
				}
				
				
				case -201721364:
				{
					if (field.equals("timezoneOffset")) 
					{
						__temp_executeDef58 = false;
						return ((logtalk.lang.Function) (new logtalk.lang.Closure(((java.lang.Object) (this) ), logtalk.lang.Runtime.toString("timezoneOffset"))) );
					}
					
					break;
				}
				
				
			}
			
			if (__temp_executeDef58) 
			{
				return super.__hx_getField(field, throwErrors, isCheck, handleProperties);
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
			boolean __temp_executeDef59 = true;
			switch (field.hashCode())
			{
				case -1776922004:
				{
					if (field.equals("toString")) 
					{
						__temp_executeDef59 = false;
						return this.toString();
					}
					
					break;
				}
				
				
				case -201721364:
				{
					if (field.equals("timezoneOffset")) 
					{
						__temp_executeDef59 = false;
						return this.timezoneOffset();
					}
					
					break;
				}
				
				
			}
			
			if (__temp_executeDef59) 
			{
				return super.__hx_invokeField(field, dynargs);
			}
			 else 
			{
				throw null;
			}
			
		}
		
	}
	
	
	@Override public   void __hx_getFields(logtalk.root.Array<java.lang.String> baseArr)
	{
		baseArr.push("date");
		{
			super.__hx_getFields(baseArr);
		}
		
	}
	
	
}


