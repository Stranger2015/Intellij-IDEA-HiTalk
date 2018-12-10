package logtalk.ds;

@SuppressWarnings(value={"rawtypes", "unchecked"})
public  class IntMap_keys_343__Fun<T> extends logtalk.lang.Function
{
	public    IntMap_keys_343__Fun(logtalk.root.Array<java.lang.Object> i, logtalk.root.Array<logtalk.ds.IntMap> _g1)
	{
		super(0, 1);
		this.i = i;
		this._g1 = _g1;
	}
	
	
	@Override public   double __hx_invoke0_f()
	{
		int ret = ((logtalk.ds.IntMap<T>) (((logtalk.ds.IntMap) (this._g1.__get(0)) )) )._keys[((int) (logtalk.lang.Runtime.toInt(this.i.__get(0))) )];
		((logtalk.ds.IntMap<T>) (((logtalk.ds.IntMap) (this._g1.__get(0)) )) ).cachedIndex = ((int) (logtalk.lang.Runtime.toInt(this.i.__get(0))) );
		((logtalk.ds.IntMap<T>) (((logtalk.ds.IntMap) (this._g1.__get(0)) )) ).cachedKey = ret;
		this.i.__set(0, (((int) (logtalk.lang.Runtime.toInt(this.i.__get(0))) ) + 1 ));
		return ((double) (ret) );
	}
	
	
	public  logtalk.root.Array<java.lang.Object> i;
	
	public  logtalk.root.Array<logtalk.ds.IntMap> _g1;
	
}


