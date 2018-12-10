package debugger;

@SuppressWarnings(value={"rawtypes", "unchecked"})
public  interface IController extends logtalk.lang.ILogtalkObject
{
	   debugger.Command getNextCommand();
	
	   void acceptMessage(debugger.Message message);
	
}


