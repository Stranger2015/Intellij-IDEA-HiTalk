package logtalk.ds;

@SuppressWarnings(value={"rawtypes", "unchecked"})
public  class StringMap_keys_356__Fun<T> extends logtalk.lang.Function
{
	public    StringMap_keys_356__Fun(logtalk.root.Array<java.lang.Object> i, logtalk.root.Array<logtalk.ds.StringMap> _g1, logtalk.root.Array<java.lang.Object> len)
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
				if ( ! (((((((logtalk.ds.StringMap<T>) (((logtalk.ds.StringMap) (this._g1.__get(0)) )) ).hashes[j] & -2 )) == 0 ))) )
				{
					this.i.__set(0, j);
					return true;
				}
				
			}
			
		}
		
		return false;
	}
	
	
	public  logtalk.root.Array<java.lang.Object> i;
	
	public  logtalk.root.Array<logtalk.ds.StringMap> _g1;
	
	public  logtalk.root.Array<java.lang.Object> len;
	
}


