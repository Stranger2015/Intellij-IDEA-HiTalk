package logtalk.root;

@SuppressWarnings(value={"rawtypes", "unchecked"})
public  class List_iterator_165__Fun extends logtalk.lang.Function
{
	public    List_iterator_165__Fun(logtalk.root.Array<logtalk.root.Array> h)
	{
		super(0, 0);
		this.h = h;
	}
	
	
	@Override public   java.lang.Object __hx_invoke0_o()
	{
		return ( this.h.__get(0) != null );
	}
	
	
	public  logtalk.root.Array<logtalk.root.Array> h;
	
}


