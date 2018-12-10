package logtalk.lang;

@SuppressWarnings(value={"rawtypes", "unchecked"})
public  interface Iterator<T> extends logtalk.lang.IHxObject
{
	   boolean hasNext();
	
	   T next();
	
}


