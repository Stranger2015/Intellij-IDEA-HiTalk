package logtalk.ds;

@SuppressWarnings(value={"rawtypes", "unchecked"})
public  class ObjectMap_keys_367__Fun<V, K> extends logtalk.lang.Function
{
	public    ObjectMap_keys_367__Fun(logtalk.root.Array<java.lang.Object> i, logtalk.root.Array<logtalk.ds.ObjectMap> _g1)
	{
		super(0, 0);
		this.i = i;
		this._g1 = _g1;
	}
	
	
	@Override public   java.lang.Object __hx_invoke0_o()
	{
		K ret = ((logtalk.ds.ObjectMap<K, V>) (((logtalk.ds.ObjectMap) (this._g1.__get(0)) )) )._keys[((int) (logtalk.lang.Runtime.toInt(this.i.__get(0))) )];
		((logtalk.ds.ObjectMap<K, V>) (((logtalk.ds.ObjectMap) (this._g1.__get(0)) )) ).cachedIndex = ((int) (logtalk.lang.Runtime.toInt(this.i.__get(0))) );
		((logtalk.ds.ObjectMap<K, V>) (((logtalk.ds.ObjectMap) (this._g1.__get(0)) )) ).cachedKey = ret;
		this.i.__set(0, (((int) (logtalk.lang.Runtime.toInt(this.i.__get(0))) ) + 1 ));
		return ret;
	}
	
	
	public  logtalk.root.Array<java.lang.Object> i;
	
	public  logtalk.root.Array<logtalk.ds.ObjectMap> _g1;
	
}


