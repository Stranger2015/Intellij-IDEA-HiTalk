  :- object(task,
           implements(forwarding),
           implements(targetp)).

  :- public([ task/2 ]).

   task( OldName, NewName ):-
        Target::find_replace(OldName,  NewName),
        fail.

   task( _, _ ).

 :- initialization(true).


:- end_object.

%======================================================================
:- protocol(targetp).

    :- public([find_replace/2 , sub_target/2 ]).
    :- protected([ goal/0 ]).
    :- dynamic(aux/2).

:- end_protocol.


%======================================================================
:- object( Any, %file(FileName),
          implements( targetp )).

      find_replace( OldName, NewName ) :-
              sub_target( Subtarget, OldName ),
              Subtarget::find_replace( OldName,  NewName ).

      find_replace( _, _ ):- retractall( string(_)).

      subTarget( string( Name ), Name ) :-
            string( Name ).

      subTarget( string( Content ), Name ):-
            Content \= Name.
%            sub_string( Content, before Name ),
      goal :-
          this( FileName ),
          load_file( FileName, Content ),
          assertz( string( Content ))

      :- initialization( goal ).


:- end_object.
%======================================================================
:- object( string( Name ),
          implements( target )).

      find_replace( OldName, NewName ) :-
              sub_target( OldName, NewName ),
              find_replace( OldName,  NewName ).

      find_replace( _, _ ) :-
              retractall( aux( _, _)).

      sub_target( "", "" ).
      sub_target( Name, Name1 ) :-
          sub_string( Name, Before, Length, After, Name1 ),
          sub_target( Before, Name1 ),
          sub_target( After, Name1 ).

      :- initialization( true ).

:- end_object.

%========================================================
:- object( java_file( FileName ),
          extends( file( FileName ))).

    subTarget( string( FileName )).

:- end_object.