package debugger;

import logtalk.root.Array;

import static logtalk.lang.EmptyObject.*;

@SuppressWarnings(value={"rawtypes", "unchecked"})
public class LogtalkProtocol extends logtalk.lang.LogtalkObject
{
	static 
	{
		debugger.LogtalkProtocol.gClientIdentification = "Logtalk debug client v1.1 coming at you!\n\n";
		debugger.LogtalkProtocol.gServerIdentification = "Logtalk debug server v1.1 ready and willing, sir!\n\n";
	}
	public    LogtalkProtocol(logtalk.lang.EmptyObject empty)
	{
		{
		}
		
	}
	
	
	public    LogtalkProtocol()
	{
		debugger.LogtalkProtocol.__lgt_ctor_debugger_LogtalkProtocol(this);
	}
	
	
	public static   void __lgt_ctor_debugger_LogtalkProtocol(debugger.LogtalkProtocol __temp_me23)
	{
		{
		}
		
	}
	
	
	public static   void writeClientIdentification(logtalk.io.Output output)
	{
		output.writeString(debugger.LogtalkProtocol.gClientIdentification);
	}
	
	
	public static   void writeServerIdentification(logtalk.io.Output output)
	{
		output.writeString(debugger.LogtalkProtocol.gServerIdentification);
	}
	
	
	public static   void readClientIdentification(logtalk.io.Input input)
	{
		logtalk.io.Bytes id = input.read(debugger.LogtalkProtocol.gClientIdentification.length());
		if ( ! (logtalk.lang.Runtime.valEq(id.toString(), debugger.LogtalkProtocol.gClientIdentification)) )
		{
			throw logtalk.lang.LogtalkException.wrap(( "Unexpected client identification string: " + logtalk.root.Std.string(id) ));
		}
		
	}
	
	
	public static   void readServerIdentification(logtalk.io.Input input)
	{
		logtalk.io.Bytes id = input.read(debugger.LogtalkProtocol.gServerIdentification.length());
		if ( ! (logtalk.lang.Runtime.valEq(id.toString(), debugger.LogtalkProtocol.gServerIdentification)) )
		{
			throw logtalk.lang.LogtalkException.wrap(( "Unexpected server identification string: " + logtalk.root.Std.string(id) ));
		}
		
	}
	
	
	public static   void writeCommand(logtalk.io.Output output, debugger.Command command)
	{
		debugger.LogtalkProtocol.writeDynamic(output, command);
	}
	
	
	public static   void writeMessage(logtalk.io.Output output, debugger.Message message)
	{
		debugger.LogtalkProtocol.writeDynamic(output, message);
	}
	
	
	public static   debugger.Command readCommand(logtalk.io.Input input)
	{
		java.lang.Object raw = debugger.LogtalkProtocol.readDynamic(input);
		try 
		{
			return ((debugger.Command) (raw) );
		}
		catch (java.lang.Throwable __temp_catchallException90)
		{
			java.lang.Object __temp_catchall91 = __temp_catchallException90;
			if (( __temp_catchall91 instanceof logtalk.lang.LogtalkException ))
			{
				__temp_catchall91 = ((logtalk.lang.LogtalkException) (__temp_catchallException90) ).obj;
			}
			
			{
				java.lang.Object e = __temp_catchall91;
				throw logtalk.lang.LogtalkException.wrap(( ( ( "Expected Command, but got " + logtalk.root.Std.string(raw) ) + ": " ) + logtalk.root.Std.string(e) ));
			}
			
		}
		
		
	}
	
	
	public static   debugger.Message readMessage(logtalk.io.Input input)
	{
		java.lang.Object raw = debugger.LogtalkProtocol.readDynamic(input);
		try 
		{
			return ((debugger.Message) (raw) );
		}
		catch (java.lang.Throwable __temp_catchallException92)
		{
			java.lang.Object __temp_catchall93 = __temp_catchallException92;
			if (( __temp_catchall93 instanceof logtalk.lang.LogtalkException ))
			{
				__temp_catchall93 = ((logtalk.lang.LogtalkException) (__temp_catchallException92) ).obj;
			}
			
			{
				java.lang.Object e = __temp_catchall93;
				throw logtalk.lang.LogtalkException.wrap(( ( ( "Expected Message, but got " + logtalk.root.Std.string(raw) ) + ": " ) + logtalk.root.Std.string(e) ));
			}
			
		}
		
		
	}
	
	
	public static   void writeDynamic(logtalk.io.Output output, java.lang.Object value)
	{
		java.lang.String string = logtalk.Serializer.run(value);
		int msg_len = string.length();
		logtalk.io.Bytes msg_len_raw = logtalk.io.Bytes.alloc(8);
		{
			int _g = 0;
			while (( _g < 8 ))
			{
				int i = _g++;
				msg_len_raw.b[( 7 - i )] = ((byte) (( ( msg_len % 10 ) + 48 )) );
				msg_len = ( msg_len / 10 );
			}
			
		}
		
		output.write(msg_len_raw);
		output.writeString(string);
	}
	
	
	public static   java.lang.Object readDynamic(logtalk.io.Input input)
	{
		logtalk.io.Bytes msg_len_raw = input.read(8);
		int msg_len = 0;
		{
			int _g = 0;
			while (( _g < 8 ))
			{
				int i = _g++;
				msg_len *= 10;
				msg_len += ( (( msg_len_raw.b[i] & 255 )) - 48 );
			}
			
		}
		
		if (( msg_len > 2097152 )) 
		{
			throw logtalk.lang.LogtalkException.wrap(( ( "Read bad message length: " + msg_len ) + "." ));
		}
		
		return logtalk.Unserializer.run(input.read(msg_len).toString());
	}
	
	
	public static  java.lang.String gClientIdentification;
	
	public static  java.lang.String gServerIdentification;
	
	public static   java.lang.Object __lgt_createEmpty()
	{
		return new debugger.LogtalkProtocol(EMPTY);
	}
	
	
	public static   java.lang.Object __lgt_create(Array arr)
	{
		return new debugger.LogtalkProtocol();
	}
	
	
}


