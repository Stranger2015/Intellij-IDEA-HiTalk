package logtalk.ds;

@SuppressWarnings(value={"rawtypes", "unchecked"})
public  class IntMap_keys_332__Fun<T> extends logtalk.lang.Function
{
	public    IntMap_keys_332__Fun(logtalk.root.Array<java.lang.Object> i, logtalk.root.Array<logtalk.ds.IntMap> _g1, logtalk.root.Array<java.lang.Object> len)
	{
		super(0, 0);
		this.i = i;
		this._g1 = _g1;
		this.len = len;
	}
	
	
	@Override public   java.lang.Object __hx_invoke0_o()
	{
		{
			int _g = ((int) (logtalk.lang.Runtime.toInt(this.i.__get(0))) );
			while (( _g < ((int) (logtalk.lang.Runtime.toInt(this.len.__get(0))) ) ))
			{
				int j = _g++;
				if ( ! ((((((((logtalk.ds.IntMap<T>) (((logtalk.ds.IntMap) (this._g1.__get(0)) )) ).flags[(j >> 4 )] >>> ((((j & 15 )) << 1 )) ) & 3 )) != 0 ))) )
				{
					this.i.__set(0, j);
					return true;
				}
				
			}
			
		}
		
		return false;
	}
	
	
	public  logtalk.root.Array<java.lang.Object> i;
	
	public  logtalk.root.Array<logtalk.ds.IntMap> _g1;
	
	public  logtalk.root.Array<java.lang.Object> len;
	
}


