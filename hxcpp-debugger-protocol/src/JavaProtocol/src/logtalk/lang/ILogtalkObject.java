package logtalk.lang;

//@SuppressWarnings(value={"rawtypes", "unchecked"})
public interface ILogtalkObject
{
	   boolean __lgt_deleteField(java.lang.String field);
	
	   java.lang.Object __lgt_lookupField(java.lang.String field, boolean throwErrors, boolean isCheck);
	
	   double __lgt_lookupField_f(java.lang.String field, boolean throwErrors);
	
	   java.lang.Object __lgt_lookupSetField(java.lang.String field, java.lang.Object value);
	
	   double __lgt_lookupSetField_f(java.lang.String field, double value);
	
	   double __lgt_setField_f(java.lang.String field, double value, boolean handleProperties);
	
	   java.lang.Object __lgt_setField(java.lang.String field, java.lang.Object value, boolean handleProperties);
	
	   java.lang.Object __lgt_getField(java.lang.String field, boolean throwErrors, boolean isCheck, boolean handleProperties);
	
	   double __lgt_getField_f(java.lang.String field, boolean throwErrors, boolean handleProperties);
	
	   java.lang.Object __lgt_invokeField(java.lang.String field, logtalk.root.Array dynargs);
	
	   void __lgt_getFields(logtalk.root.Array<java.lang.String> baseArr);
	
}


