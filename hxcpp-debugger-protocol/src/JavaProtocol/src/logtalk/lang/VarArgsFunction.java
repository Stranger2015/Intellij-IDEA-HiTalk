package logtalk.lang;

@SuppressWarnings(value={"rawtypes", "unchecked"})
public  class VarArgsFunction extends logtalk.lang.VarArgsBase
{
	public    VarArgsFunction(logtalk.lang.Function fun)
	{
		super(-1, -1);
		this.fun = fun;
	}
	
	
	public  logtalk.lang.Function fun;
	
	@Override public   java.lang.Object __hx_invokeDynamic(logtalk.root.Array dynArgs)
	{
		return this.fun.__hx_invoke1_o(0.0, dynArgs);
	}
	
	
}


