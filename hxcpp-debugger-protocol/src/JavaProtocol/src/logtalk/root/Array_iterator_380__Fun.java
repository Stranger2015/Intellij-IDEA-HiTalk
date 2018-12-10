package logtalk.root;

@SuppressWarnings(value={"rawtypes", "unchecked"})
public  class Array_iterator_380__Fun extends logtalk.lang.Function
{
	public    Array_iterator_380__Fun(logtalk.root.Array<java.lang.Object> len, logtalk.root.Array<java.lang.Object> i)
	{
		super(0, 0);
		this.len = len;
		this.i = i;
	}
	
	
	@Override public   java.lang.Object __hx_invoke0_o()
	{
		return (((int) (logtalk.lang.Runtime.toInt(this.i.__get(0))) ) < ((int) (logtalk.lang.Runtime.toInt(this.len.__get(0))) ) );
	}
	
	
	public  logtalk.root.Array<java.lang.Object> len;
	
	public  logtalk.root.Array<java.lang.Object> i;
	
}


