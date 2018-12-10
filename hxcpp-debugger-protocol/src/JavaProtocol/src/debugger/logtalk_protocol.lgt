/*
 * Copyright 2000-2013 JetBrains s.r.o.
 * Copyright 2014-2018 AS3Boyan
 * Copyright 2014-2014 Elias Ku
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
%package debugger;
%
%import logtalk.root.array;
%
%import static logtalk.lang.empty_object.empty;

%@SuppressWarnings(value={"rawtypes", "unchecked"})
:- object( logtalk_protocol,
              extends( logtalk_object )).

    :- jpackage([ debugger ]).
    :- jimport([ logtalk, root, array ]).
    :- jimport( static, [ logtalk, root, array ]).

    :- annotation('SuppressWarnings', value(rawtypes, unchecked)).

    :- private([
          gclient_identification/1,
          gserver_identification/1
    ]).

    :- mode('__lgt_ctor_debugger_LogtalkProtocol'( ?logtalk_protocol ), one ).


       gclient_identification('Logtalk debug client v1.1 coming at you!\n\n').

       gserver_identification('Logtalk debug server v1.1 ready and willing, sir!\n\n').


    :- initialization( logtalk_protocol::'__lgt_ctor_debugger_LogtalkProtocol'(self) ).


%	public logtalk_protocol(logtalk.lang.emptyobject empty)
%	{
%		{
%		}
%
%	}
%
%
%	public logtalk_protocol()
%	{
%		logtalk_protocol::'__lgt_ctor_debugger_LogtalkProtocol'(this);
%	}


'__lgt_ctor_debugger_LogtalkProtocol'(LogtalkProtocol /*__temp_me23*/).
%	{
%		{
%		}
%
%	}


write_client_identification( Output ) :-
      /*logtalk_protocol::*/gclient_identification( String ),
      Output::write_string( String ).


write_server_identification( Output ) :-
       /*logtalk_protocol::*/gserver_identification( SLID ),
      Output::write_string(SLID ).

read_clent_identification( Input ) :-
          /*logtalk_protocol::*/gclient_identification( CLID ),
        strlen( CLID, Length ),
        Input::read( Length, Id ),
        logtalk_runtime( Runtime ),
	Runtime::val_eq( Id, CLID ) ->
	true
	;
        throw(LE).% logtalk.lang.LogtalkException.wrap(( "Unexpected client identification string: " + logtalk.root.Std.string(id) ));


read_server_identification( Input ) :-
        /*logtalk_protocol::*/gserver_identification( SLID ),
        strlen( SLID, Length ),
        Input::read( Length, Id ),
        logtalk_runtime( Runtime ),
        Runtime::val_eq( Id, SLID ) ->
        true
        ;
        throw(LE).% logtalk.lang.LogtalkException.wrap(( "Unexpected client identification string: " + logtalk.root.Std.string(id) ));


	public static   void writeCommand(logtalk.io.Output output, Command command)
	{
		logtalk_protocol.writeDynamic(output, command);
	}


	public static   void writeMessage(logtalk.io.Output output, Message message)
	{
		logtalk_protocol.writeDynamic(output, message);
	}


	public static   Command readCommand(logtalk.io.Input input)
	{
		Object raw = logtalk_protocol.readDynamic(input);
		try
		{
			return ((Command) (raw) );
		}
		catch (Throwable __temp_catchallException90)
		{
			Object __temp_catchall91 = __temp_catchallException90;
			if (( __temp_catchall91 instanceof logtalk.lang.LogtalkException ))
			{
				__temp_catchall91 = ((logtalk.lang.LogtalkException) (__temp_catchallException90) ).obj;
			}

			{
				Object e = __temp_catchall91;
				throw logtalk.lang.LogtalkException.wrap(( ( ( "Expected Command, but got " + logtalk.root.Std.string(raw) ) + ": " ) + logtalk.root.Std.string(e) ));
			}

		}


	}


	public static   Message readMessage(logtalk.io.Input input)
	{
		Object raw = logtalk_protocol.readDynamic(input);
		try
		{
			return ((Message) (raw) );
		}
		catch (Throwable __temp_catchallException92)
		{
			Object __temp_catchall93 = __temp_catchallException92;
			if (( __temp_catchall93 instanceof logtalk.lang.LogtalkException ))
			{
				__temp_catchall93 = ((logtalk.lang.LogtalkException) (__temp_catchallException92) ).obj;
			}

			{
				Object e = __temp_catchall93;
				throw logtalk.lang.LogtalkException.wrap(( ( ( "Expected Message, but got " + logtalk.root.Std.string(raw) ) + ": " ) + logtalk.root.Std.string(e) ));
			}

		}


	}


	public static   void writeDynamic(logtalk.io.Output output, Object value)
	{
		String string = logtalk.Serializer.run(value);
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


	public static   Object readDynamic(logtalk.io.Input input)
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


	public static  String gClientIdentification;

	public static  String gServerIdentification;

	public static   Object __lgt_createEmpty()
	{
		return new logtalk_protocol(EMPTY);
	}


	public static   Object __lgt_create(Array arr)
	{
		return new logtalk_protocol();
	}
	
	
}


