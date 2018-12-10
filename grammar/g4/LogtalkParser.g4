parser grammar LogtalkParser;

tokens {
        LogtalkLexer
}

options {
   language = Java;
  superClass = Parser;
}

@lexer::members{
// protected const int EOF = Eof;
// protected const int HIDDEN = Hidden;
}

@header {
    package  org.ltc.grammar.logtalk;
}

import PrologParser;

//==================================================

start_logtalk_rule:
    lgt_file;

lgt_file:
    pl_file |
    ( DOC_COMMENT | lgt_clause | lgt_entity )* EOF;

lgt_entity:
        object |
        category |
        protocol |
        pl_entity
        ;

lgt_clause:
    lgt_directive |
    lgt_horn_clause
;

lgt_directive:
    directive
;
//========================================================================
//Object definition

object:
    begin_object
    ( object_directive | lgt_rule )*
    end_object
    ;

begin_object:
    IMPLIES_OP OBJECT LPAREN object_identifier ( COMMA object_relation )* RPAREN END_OF_CLAUSE
;
end_object:
    IMPLIES_OP END_OBJECT END_OF_CLAUSE
;

initialization_directive:
    IMPLIES_OP INITIALIZATION LPAREN lgt_goal RPAREN END_OF_CLAUSE
;

object_relation :
    ( prototype_relation | non_prototype_relation )*
;

//prototype_relations :
//    prototype_relation ( COMMA prototype_relation )*
//    ;
//
prototype_relation :
    implements_protocols |
    imports_categories |
    extends_objects
    ;
//
//non_prototype_relations :
//    non_prototype_relation ( COMMA non_prototype_relation )*;

non_prototype_relation :
    implements_protocols |
    imports_categories |
    instantiates_classes |
    specializes_classes
    ;

//Category definition
//
category :
    begin_category 
    ( category_directive | lgt_rule )*

    end_category
    ;

begin_category :
    IMPLIES_OP CATEGORY LPAREN category_identifier  (COMMA category_relation )*  RPAREN END_OF_CLAUSE;

end_category :
    IMPLIES_OP END_CATEGORY END_OF_CLAUSE;

//category_relations :
//    category_relation ( COMMA category_relation )*
//;
//
category_relation :
    implements_protocols |
    extends_categories |
    complements_objects
;

//Protocol definition
protocol :
    begin_protocol
    ( protocol_directive )*
    end_protocol
;

begin_protocol :
    IMPLIES_OP PROTOCOL LPAREN
    protocol_identifier
    ( COMMA prototype_relation )*
    RPAREN END_OF_CLAUSE
;

end_protocol :
    IMPLIES_OP END_PROTOCOL END_OF_CLAUSE
;

//Entity relations

extends_protocols :
    EXTENDS LPAREN extended_protocols RPAREN
;
extends_objects :
    EXTENDS LPAREN extended_objects RPAREN
;
extends_categories :
    EXTENDS LPAREN extended_categories RPAREN
;
implements_protocols :
    IMPLEMENTS LPAREN implemented_protocols RPAREN
;
imports_categories :
    IMPORTS LPAREN imported_categories RPAREN
;
instantiates_classes :
    INSTANTIATES LPAREN instantiated_objects RPAREN
;
specializes_classes :
    SPECIALIZES LPAREN specialized_objects RPAREN
;
complements_objects :
    COMPLEMENTS LPAREN complemented_objects RPAREN
;

//Implemented protocols
implemented_protocols :
    implemented_protocol |
    implemented_protocol_sequence |
    implemented_protocol_list
;

implemented_protocol :
    protocol_identifier |
    scope COLON_COLON protocol_identifier
;
implemented_protocol_sequence :
    implemented_protocol ( COMMA implemented_protocol )*
;

implemented_protocol_list :
    LBRACE implemented_protocol_sequence RBRACE
;
//Extended protocols

extended_protocols :
    extended_protocol |
    extended_protocol_sequence |
    extended_protocol_list
;

extended_protocol :
    protocol_identifier |
    scope COLON_COLON protocol_identifier
;

extended_protocol_sequence :
    extended_protocol ( COMMA extended_protocol )*
;

extended_protocol_list :
    LBRACE extended_protocol_sequence RBRACE
;

//Imported categories
imported_categories :
    imported_category |
    imported_category_sequence |
    imported_category_list
;

imported_category :
    category_identifier |
    scope COLON_COLON category_identifier
;

imported_category_sequence :
    imported_category ( COMMA imported_category )*
;

imported_category_list :
    LBRACE imported_category_sequence RBRACE
;
//Extended objects

extended_objects :
    extended_object |
    extended_object_sequence |
    extended_object_list
;

extended_object :
    object_identifier |
    scope COLON_COLON object_identifier
;
extended_object_sequence :
    extended_object (COMMA extended_object )*
;

extended_object_list :
    LBRACE extended_object_sequence RBRACE;

//Extended categories

extended_categories :
    extended_category |
    extended_category_sequence |
    extended_category_list
;

extended_category :
    category_identifier |
    scope COLON_COLON category_identifier
;

extended_category_sequence :
    extended_category ( COMMA extended_category )*
;

extended_category_list :
    LBRACE extended_category_sequence RBRACE
;

//Instantiated objects

instantiated_objects :
    instantiated_object |
    instantiated_object_sequence |
    instantiated_object_list
;

instantiated_object :
    object_identifier |
    scope COLON_COLON object_identifier
;

instantiated_object_sequence :
    instantiated_object ( COMMA instantiated_object )*
;

instantiated_object_list :
    LBRACE instantiated_object_sequence RBRACE
;
//Specialized objects

specialized_objects :
    specialized_object |
    specialized_object_sequence |
    specialized_object_list
;

specialized_object :
    object_identifier |
    scope COLON_COLON object_identifier
;

specialized_object_sequence :
    specialized_object (COMMA specialized_object )*
;

specialized_object_list :
    LBRACE specialized_object_sequence RBRACE
;

//Complemented objects

complemented_objects :
    object_identifier |
    complemented_object_sequence |
    complemented_object_list
;

complemented_object_sequence :
    object_identifier (COMMA object_identifier)*
;

complemented_object_list :
    LBRACE complemented_object_sequence RBRACE
;

//Entity and predicate scope
scope :
    PUBLIC |
    PROTECTED |
    PRIVATE
;

//Entity identifiers

entity_identifiers :
    entity_identifier_sequence |
    entity_identifier_list
;

entity_identifier :
    object_identifier |
    protocol_identifier |
    category_identifier |
    module_identifier
;

entity_identifier_sequence :
    entity_identifier ( COMMA entity_identifier )*
;

entity_identifier_list :
    LBRACE entity_identifier_sequence RBRACE
;

//Object identifiers
object_identifiers :
    object_identifier |
    object_identifier_sequence |
    object_identifier_list
;

object_identifier :
    atom |
    compound
;

object_identifier_sequence :
    object_identifier ( COMMA object_identifier )*
;

object_identifier_list :
    LBRACE object_identifier_sequence RBRACE
;

//Category identifiers

category_identifiers :
    category_identifier |
    category_identifier_sequence |
    category_identifier_list
;

category_identifier :
    atom |
    compound
;

category_identifier_sequence :
    category_identifier ( COMMA category_identifier )*
;

category_identifier_list :
    LBRACE category_identifier_sequence RBRACE
;

//Protocol identifiers

protocol_identifiers :
    protocol_identifier |
    protocol_identifier_sequence |
    protocol_identifier_list
;

protocol_identifier :
    atom
;
protocol_identifier_sequence :
    protocol_identifier ( COMMA protocol_identifier )*
;

protocol_identifier_list :
    LBRACE protocol_identifier_sequence RBRACE
;

//Module identifiers
module_identifier_sequence :
    module_identifier ( COMMA module_identifier )*
;

module_identifier_list :
    LBRACE module_identifier_sequence RBRACE
;

module_identifier :
    atom
;

//Source file names

source_file_names :
    source_file_name |
    source_file_name_list
;

source_file_name :
    atom |
    library_source_file_name
;

library_source_file_name :
    library_name LPAREN atom RPAREN;

library_name :
    atom
;

source_file_name_sequence :
    source_file_name ( COMMA source_file_name )*
;

source_file_name_list :
    LBRACE source_file_name_sequence RBRACE
;

source_file_directive :
    IMPLIES_OP ENCODING LPAREN atom RPAREN END_OF_CLAUSE ;

 include_directive:
    IMPLIES_OP INCLUDE LPAREN source_file_name RPAREN END_OF_CLAUSE ;

   // prolog_directives
//Conditional compilation directives

conditional_compilation_directives :
    conditional_compilation_directive*
;

conditional_compilation_directive :
    IMPLIES_OP IF LPAREN callable RPAREN END_OF_CLAUSE |
    IMPLIES_OP ELIF LPAREN callable RPAREN END_OF_CLAUSE |
    IMPLIES_OP ELSE END_OF_CLAUSE |
    IMPLIES_OP ENDIF END_OF_CLAUSE
;

built_in_directive:
    IMPLIES_OP BUILT_IN END_OF_CLAUSE
;

info_directive:
    IMPLIES_OP INFO LPAREN entity_info_list RPAREN END_OF_CLAUSE
 ;


object_directive :
    initialization_directive |
    built_in_directive |
    threaded_directive |
    dynamic_directive |
    uses_object_directive |
    use_module_directive |
    set_logtalk_flag_directive |
    include_directive |
    predicate_directive*
;

set_logtalk_flag_directive:
     IMPLIES_OP SET_LOGTALK_FLAG LPAREN atom COMMA nonvar RPAREN END_OF_CLAUSE
     ;

    //    IMPLIES_OP USE_MODULE LPAREN module_identifier COMMA module_predicate_indicator_alias_list RPAREN END_OF_CLAUSE |
calls_directive:
    IMPLIES_OP CALLS LPAREN protocol_identifier+ RPAREN END_OF_CLAUSE
;

threaded_directive:
    IMPLIES_OP THREADED END_OF_CLAUSE
;

dynamic_directive:
    IMPLIES_OP DYNAMIC END_OF_CLAUSE
;

uses_object_directive:
        IMPLIES_OP USES LPAREN object_identifier RPAREN END_OF_CLAUSE
;

///Category directives
/*   ":- built_in." |
      ":- dynamic." |
      ":- uses(" object_identifier ")." |
      ":- use_module(" module_identifier "," predicate_indicator_alias_list ")." |
      ":- calls(" protocol_identifiers ")." |
      ":- info(" entity_info_list ")." |
      ":- set_logtalk_flag(" atom "," nonvar ")." |
      ":- include(" source_file_name ")." |
      predicate_directives

*/
category_directive :
    built_in_directive |
    dynamic_directive   |
    uses_directive |
    use_module_directive |
    calls_directive |
    info_directive |
    set_logtalk_flag_directive |
    include_directive |
    predicate_directive*
;
//Protocol directives

//protocol_directives :
//   protocol_directive*
//;

protocol_directive :
    built_in_directive |
    dynamic_directive |
    info_directive |
    set_logtalk_flag_directive |
    include_directive |
    predicate_directive*
;

//Predicate directives

predicate_directive :
    alias_directive |
    synchronized_directive |
    uses_directive |
    scope_directive |
    mode_directive |
    meta_predicate_directive |
    meta_non_terminal_directive |
    info_directive |
    dynamic_directive |
    discontiguous_directive |
    multifile_directive |
    coinductive_directive |
    operator_directive |
    directive //prolog def=.
;

alias_directive :
    IMPLIES_OP ALIAS LPAREN entity_identifier COMMA
    ( predicate_indicator_alias_list | non_terminal_indicator_alias_list )
    RPAREN
    END_OF_CLAUSE
;

synchronized_directive :
    IMPLIES_OP SYNCHRONIZED
    LPAREN predicate_indicator_term | non_terminal_indicator_term RPAREN END_OF_CLAUSE
;

uses_directive :
    IMPLIES_OP USES
    LPAREN object_identifier COMMA predicate_indicator_alias_list RPAREN END_OF_CLAUSE
;

scope_directive :
    IMPLIES_OP ( PUBLIC | PROTECTED | PRIVATE )
    LPAREN predicate_indicator_term | non_terminal_indicator_term RPAREN END_OF_CLAUSE
;

mode_directive :
    IMPLIES_OP MODE LPAREN predicate_mode_term |
    non_terminal_mode_term COMMA number_of_proofs RPAREN END_OF_CLAUSE
;

meta_predicate_directive :
    IMPLIES_OP META_PREDICATE LPAREN meta_predicate_template_term RPAREN END_OF_CLAUSE
;

meta_non_terminal_directive :
    IMPLIES_OP META_NON_TERMINAL LPAREN meta_non_terminal_template_term RPAREN END_OF_CLAUSE
;

discontiguous_directive :
    IMPLIES_OP DISCONTIGUOUS LPAREN predicate_indicator_term |
    non_terminal_indicator_term RPAREN END_OF_CLAUSE
;

multifile_directive :
    IMPLIES_OP MULTIFILE LPAREN predicate_indicator_term RPAREN END_OF_CLAUSE
;

coinductive_directive :
    IMPLIES_OP COINDUCTIVE LPAREN predicate_indicator_term |
    coinductive_predicate_template_term RPAREN END_OF_CLAUSE
;

non_terminal_indicator_term :
    non_terminal_indicator |
    non_terminal_indicator_sequence |
    non_terminal_indicator_list
;

non_terminal_indicator_sequence :
    non_terminal_indicator ( COMMA non_terminal_indicator )*
;

non_terminal_indicator_list :
    LBRACE non_terminal_indicator_sequence RBRACE
;

non_terminal_indicator :
    name SLASH_SLASH PROLOG_ARITY
;

non_terminal_indicator_alias :
    non_terminal_indicator |
    non_terminal_indicator AS non_terminal_indicator
    non_terminal_indicator COLON_COLON non_terminal_indicator
;

non_terminal_indicator_alias_sequence :
    non_terminal_indicator_alias |
    non_terminal_indicator_alias ( COMMA non_terminal_indicator_alias )*
;

non_terminal_indicator_alias_list :
    LBRACE non_terminal_indicator_alias_sequence RBRACE;

coinductive_predicate_template_term :
    coinductive_predicate_template |
    coinductive_predicate_template_sequence |
    coinductive_predicate_template_list
;

coinductive_predicate_template_sequence :
    coinductive_predicate_template ( COMMA coinductive_predicate_template )*
;

coinductive_predicate_template_list :
    LBRACE coinductive_predicate_template_sequence RBRACE;

coinductive_predicate_template :
    atom LPAREN coinductive_mode_terms RPAREN;

coinductive_mode_terms :
    coinductive_mode_term ( COMMA coinductive_mode_term )*
;

coinductive_mode_term :
    PLUS | MINUS
;

predicate_mode_term :
    atom LPAREN mode_terms RPAREN
;

non_terminal_mode_term :
    atom  LPAREN mode_terms RPAREN
;

mode_terms :
    mode_term ( COMMA mode_term )*
;

mode_term :
    AT ( type )? | PLUS ( type )? | MINUS ( type )? | QUESTION ( type )? |
    PLUS_PLUS ( type )? | MINUS_PLUS ( type )?
;

type :
    prolog_type | logtalk_type | user_defined_type
;

prolog_type :
    TERM |
    NONVAR | VAR |
    COMPOUND | GROUND | CALLABLE | LIST |
    ATOMIC | ATOM |
    NUMBER | INTEGER | FLOAT
;

logtalk_type :
    OBJECT | CATEGORY | PROTOCOL |
    EVENT | MODULE //todo
;

user_defined_type : entity_identifier;

number_of_proofs :
    ZERO |
    ZERO_OR_ONE |
    ZERO_OR_MORE |
    ONE |
    ONE_OR_MORE |
    ONE_OR_ERROR |
    ERROR
;

meta_predicate_template_term :
    meta_predicate_template |
    meta_predicate_template_sequence |
    meta_predicate_template_list
;

meta_predicate_template_sequence :
    meta_predicate_template |
    meta_predicate_template COMMA meta_predicate_template_sequence
;

meta_predicate_template_list :
    LBRACE meta_predicate_template_sequence RBRACE
;

meta_predicate_template :
    ( object_identifier | category_identifier )
     COLON_COLON atom LPAREN meta_predicate_specifiers RPAREN
;

meta_predicate_specifiers :
        meta_predicate_specifier ( COMMA meta_predicate_specifier )*
;

meta_predicate_specifier :
        NON_NEGATIVE_INTEGER | COLON_COLON | UP | MULT;

meta_non_terminal_template_term :
    meta_predicate_template_term
;

entity_info_list :
    nil |
    LBRACE entity_info_item IS nonvar OR entity_info_list RBRACE
;

entity_info_item :
    COMMENT_KEYWORD | REMARKS |
    AUTHOR | VERSION | DATE |
    COPYRIGHT | LICENSE |
    PARAMETERS | PARNAMES |
    SEE_ALSO |
    atom
;

predicate_info_list :
    nil |
    LBRACE predicate_info_item IS nonvar OR predicate_info_list RBRACE
;

predicate_info_item :
    COMMENT_KEYWORD | REMARKS |
    ARGUMENTS | ARGNAMES |
    REDEFINITION | ALLOCATION |
    EXAMPLES | EXCEPTIONS |
    atom
;
//Clauses and goals

lgt_horn_clause :
    lgt_rule |
    object_identifier COLON_COLON lgt_rule;

 lgt_rule:
     callable (IMPLIES_OP lgt_goal)? END_OF_CLAUSE
    ;

lgt_goal :
    message_sending |
    super_call |
    external_call |
    context_switching_call |
    goal
;


message_sending :
    message_to_object |
    message_delegation |
    message_to_self
;

message_to_object :
    receiver COLON_COLON messages
;

message_delegation :
    LBRACE message_to_object RBRACE
;

message_to_self :
    COLON_COLON messages
;

super_call :
    SUPER message
;

messages :
    message |
    LPAREN message COMMA messages RPAREN |
    LPAREN message SEMICOLON messages RPAREN |
    LPAREN message RARROW messages RPAREN
;

//message :
//    callable |
//    var
//;

receiver :
    LBRACKET callable RBRACKET |
    object_identifier |
    var
;

external_call :
    LBRACKET callable RBRACKET
;

context_switching_call :
    object_identifier LSHIFT goal
;
//Lambda expressions
//
//Logtalk supports lambda expressions. Lambda parameters are represented using a list with the (>>)/2 infix operator connecting them to the lambda. Some simple examples using library meta-predicates:
//
//?- {library(metapredicates_loader)}.
//yes
//
//?- meta::map([X,Y]>>(Y is 2*X), [1,2,3], Ys).
//Ys = [2,4,6]
//yes
//
//Currying is also supported:
//
//?- meta::map([X]>>([Y]>>(Y is 2*X)), [1,2,3], Ys).
//Ys = [2,4,6]
//yes
//
//Lambda free variables can be expressed using the extended syntax {Free1, ...}/[Parameter1, ...]>>Lambda.
lambda_expression :
    lambda_free_variables SLASH lambda_parameters RSHIFT callable |
    lambda_free_variables SLASH callable |
    lambda_parameters RSHIFT callable
;

lambda_free_variables :
    LBRACKET conjunction_of_variables RBRACKET |
    LBRACKET var RBRACKET |
    l_nil;

lambda_parameters :
    list |
    nil;

//nil : l_nil;
l_nil : LBRACE RBRACE;
a_nil : LPAREN RPAREN;
//Entity properties

category_property :
    STATIC |
    DYNAMIC |
    BUILT_IN |
    FILE LPAREN atom RPAREN |
    FILE LPAREN atom COMMA atom RPAREN |
    LINES LPAREN integer COMMA integer RPAREN |
    EVENTS |
    SOURCE_DATA |
    ( PUBLIC | PROTECTED | PRIVATE ) LPAREN predicate_indicator_list RPAREN |
    DECLARES LPAREN predicate_indicator COMMA predicate_declaration_property_list RPAREN |
    DEFINES LPAREN predicate_indicator COMMA predicate_definition_property_list RPAREN |
    (INCLUDES | PROVIDES) LPAREN predicate_indicator COMMA object_identifier |
    category_identifier COMMA predicate_definition_property_list RPAREN |
    ALIAS LPAREN predicate_indicator COMMA predicate_alias_property_list RPAREN |
    (CALLS | UPDATES) LPAREN predicate COMMA predicate_call_update_property_list RPAREN |
     LPAREN predicate COMMA predicate_call_update_property_list RPAREN |
    ( NUMBER_OF_CLAUSES | NUMBER_OF_RULES | NUMBER_OF_USER_CLAUSES | NUMBER_OF_USER_RULES )
    | LPAREN integer RPAREN
;

object_property :
    STATIC |
    DYNAMIC |
    BUILT_IN |
    THREADED |
    FILE LPAREN atom ( COMMA atom )? RPAREN |
    LINES LPAREN integer COMMA integer RPAREN |
    CONTEXT_SWITCHING_CALLS |
    DYNAMIC_DECLARATIONS | 
    EVENTS |
    SOURCE_DATA |
    COMPLEMENTS LPAREN ALLOW | RESTRICT RPAREN |
    COMPLEMENTS |
  (  PUBLIC | PROTECTED | PRIVATE) LPAREN predicate_indicator_list RPAREN|
    DECLARES LPAREN predicate_indicator COMMA predicate_declaration_property_list RPAREN |
    DEFINES LPAREN predicate_indicator COMMA predicate_definition_property_list RPAREN |
  (  INCLUDES | PROVIDES ) LPAREN predicate_indicator COMMA object_identifier | category_identifier COMMA predicate_definition_property_list RPAREN |
    ALIAS LPAREN predicate_indicator COMMA predicate_alias_property_list RPAREN |
    (CALLS | UPDATES)  LPAREN predicate COMMA predicate_call_update_property_list RPAREN |
    (NUMBER_OF_CLAUSES | NOMBER_OF_RULES|NUMBER_OF_USER_CLAUSES | NUMBER_OF_USER_RULES) LPAREN integer RPAREN
    ;

protocol_property :
    STATIC |
    DYNAMIC |
    BUILT_IN |
    SOURCE_DATA |
    FILE LPAREN atom (COMMA atom)? RPAREN |
    LINES LPAREN integer COMMA integer RPAREN |
    PUBLIC LPAREN predicate_indicator_list RPAREN |
    PROTECTED LPAREN predicate_indicator_list RPAREN |
    PRIVATE LPAREN predicate_indicator_list RPAREN |
    DECLARES LPAREN predicate_indicator COMMA predicate_declaration_property_list RPAREN |
    ALIAS LPAREN predicate_indicator COMMA predicate_alias_property_list RPAREN
;

predicate_declaration_property_list :
    LBRACE predicate_declaration_property_sequence RBRACE;

predicate_declaration_property_sequence :
    predicate_declaration_property (COMMA predicate_declaration_property)*
;

predicate_declaration_property :
    STATIC | DYNAMIC |
    ( SCOPE LPAREN scope RPAREN )|
    (PRIVATE | PROTECTED | PUBLIC ) |
    COINDUCTIVE |
    MULTIFILE |
    SYNCHRONIZED |
    META_PREDICATE LPAREN meta_predicate_template RPAREN |
    COINDUCTIVE LPAREN coinductive_predicate_template RPAREN |
    NON_TERMINAL LPAREN non_terminal_indicator RPAREN |
    INCLUDE LPAREN atom RPAREN |
    LINE_COUNT LPAREN integer RPAREN |
    MODE LPAREN predicate_mode_term | non_terminal_mode_term COMMA number_of_proofs RPAREN |
    INFO LPAREN list RPAREN
;

predicate_definition_property_list :
    LBRACE predicate_definition_property_sequence RBRACE
;

predicate_definition_property_sequence :
    predicate_definition_property (COMMA predicate_definition_property )*
;

predicate_definition_property :
    INLINE | AUXILIARY |
    NON_TERMINAL LPAREN non_terminal_indicator RPAREN |
    INCLUDE LPAREN atom RPAREN |
    LINE_COUNT LPAREN integer RPAREN |
    NUMBER_OF_CLAUSES LPAREN integer RPAREN |
    NUMBER_OF_RULES LPAREN integer RPAREN;

predicate_alias_property_list :
    LBRACE predicate_alias_property_sequence RBRACE;

predicate_alias_property_sequence :
    predicate_alias_property ( COMMA predicate_alias_property )*
;

predicate_alias_property :
    FOR LPAREN predicate_indicator RPAREN |
    FROM LPAREN entity_identifier RPAREN |
    NON_TERMINAL LPAREN non_terminal_indicator RPAREN |
    INCLUDE LPAREN atom RPAREN |
    LINE_COUNT LPAREN integer RPAREN
;

predicate :
    predicate_indicator |
    SUPER predicate_indicator |
    COLON_COLON predicate_indicator |
    var COLON_COLON predicate_indicator |
    object_identifier COLON_COLON predicate_indicator |
    var COLON predicate_indicator |
    module_identifier COLON predicate_indicator
;

predicate_call_update_property_list :
    LBRACE predicate_call_update_property_sequence RBRACE;

predicate_call_update_property_sequence :
    predicate_call_update_property ( COMMA predicate_call_update_property )*
;

predicate_call_update_property :
    CALLER LPAREN predicate_indicator RPAREN |
    INCLUDE LPAREN atom RPAREN |
    LINE_COUNT LPAREN integer RPAREN |
    AS LPAREN predicate_indicator RPAREN
;

//Predicate properties

predicate_property :
    (STATIC | DYNAMIC )|
    SCOPE LPAREN scope RPAREN |
    (PRIVATE | PROTECTED | PUBLIC) |
    (LOGTALK | PROLOG | HILOGTALK | HILOG | FOREIGN) |
    COINDUCTIVE LPAREN coinductive_predicate_template RPAREN |
   ( MULTIFILE | SYNCHRONIZED | BUILT_IN | INLINE )|
   ( DEFINED_IN REDEFINED_FROM)  LPAREN entity_identifier | object_identifier | category_identifier RPAREN |
    META_PREDICATE LPAREN meta_predicate_template RPAREN |
    ALIAS_OF LPAREN callable RPAREN |
    NON_TERMINAL LPAREN non_terminal_indicator RPAREN |
    MODE LPAREN predicate_mode_term | non_terminal_mode_term COMMA number_of_proofs RPAREN |
    INFO LPAREN list RPAREN |
    ( NUMBER_OF_CLAUSES | NUMBER_OF_RULES ) LPAREN integer RPAREN |
    ( DEFINED_IN REDEFINED_FROM) LPAREN object_identifier | category_identifier COMMA integer RPAREN |
    ( DECLARED_IN |  ALIAS_DECLARED_IN ) LPAREN entity_identifier (COMMA integer)? RPAREN
;

predicate_indicator:
     atom SLASH SLASH? arity;

arity: PROLOG_ARITY | var; //todo `p/N` template
// arity(Compound, Arity):-
// name_args(Compound, [Name | Args]), legnth(Args, Arity).

logtalk_flag :
    flag ( nonvar );

flag : atom;



//lgt_known_operator :
//        known_operator |
//        lgt_known_xfx_operator |
//        lgt_known_xfy_operator |
//        lgt_known_yfx_operator |
//        lgt_known_fx_operator |
//        lgt_known_xf_operator |
//        lgt_known_fy_operator |
//        lgt_known_yf_operator
//         ;


lgt_known_binary_operator :
;


lgt_known_left_operator  :
        PLUS_PLUS |
        MINUS_MINUS |
        DOLLAR|
        AT|
        COLON_COLON|
        UP_UP   //super5
 ;
//lgt_known_left_operator : //&valid_operator (
//                            IMPLIES_OP |
//                            NON_PROVABLE |
//                            QUESTION|
//                            PLUS | MINUS | BACKSLASH
//;

//super_call :
//    UP_UP message;
//
//messages :
//    message |
//    LPAREN message COMMA messages RPAREN |
//    LPAREN message SEMICOLON messages RPAREN |
//    LPAREN message RARROW messages RPAREN
//    ;
//+

message :
    callable |
    var;

//receiver :
//    LBRACKET callable RBRACKET |
//    object_identifier |
//    var;
//
//external_call :
//    LBRACKET callable RBRACKET;
//
//context_switching_call :
//    object_identifier RSHIFT goal;

//Lambda expressions

//;

//lambda_free_variables :
//    LBRACKET conjunction_of_variables RBRACKET |//todo
//    LBRACKET var RBRACKET |
//    CURLES
//;

//lambda_parameters :
//    list_of_terms | //todo
//    nil;

conjunction_of_variables:
     var (COMMA  var)*;//TODO

