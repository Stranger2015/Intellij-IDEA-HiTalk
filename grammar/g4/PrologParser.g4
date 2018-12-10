parser grammar PrologParser;

tokens {
    PrologLexer
}

options
{
    superClass = org.antlr.jetbrains.adaptor.parser.ANTLRParserAdaptor;
    language = Java;
}




@lexer::members
{
   protected const int EOF = Eof;
   protected const int HIDDEN = Hidden;
   public int merged_ops_detected = 1 ;
}


@header {
    package  org.ltc.grammar.prolog;
}


/*

 * Parser Rules
 */

start_prolog_rule:
    pl_file;

pl_file: ( DOC_COMMENT | clause | pl_entity )* EOF
;

pl_entity :

//
predicate_indicator_term :
    predicate_indicator |
    pl_module
    ;

pl_module:
    pl_module_directive
    directive*
    horn_clause*
;
    predicate_indicator_sequence |
    predicate_indicator_list
;

predicate_indicator_sequence :
    predicate_indicator ( COMMA predicate_indicator )*
;

predicate_indicator_list :
    LBRACE predicate_indicator_sequence RBRACE
;

predicate_indicator_alias :
    predicate_indicator |
    predicate_indicator AS predicate_indicator |
    predicate_indicator COLON_COLON predicate_indicator |
    predicate_indicator COLON predicate_indicator
;

predicate_indicator_alias_sequence :
    predicate_indicator_alias ( COMMA predicate_indicator_alias )*
;

predicate_indicator_alias_list :
    LBRACE predicate_indicator_alias_sequence RBRACE
;

module_predicate_indicator_alias :
    predicate_indicator |
    predicate_indicator AS predicate_indicator |
    predicate_indicator COLON predicate_indicator
;

module_predicate_indicator_alias_sequence :
    module_predicate_indicator_alias ( COMMA module_predicate_indicator_alias )*
;

module_predicate_indicator_alias_list :
    LBRACE module_predicate_indicator_alias_sequence RBRACE;

use_module_directive:
    IMPLIES_OP
    USE_MODULE
    LPAREN module_identifier COMMA
    module_predicate_indicator_alias_list RPAREN END_OF_CLAUSE
;

pl_module_directive:
    IMPLIES_OP MODULE LPAREN module_identifier RPAREN END_OF_CLAUSE;

clause:
    directive |
    horn_clause
;

module_identifier:
    atom
;

directive :
    operator_directive |
    pl_module_directive
;

horn_clause:
    rule |
    module_identifier COLON rule
;

operator_directive:
    IMPLIES_OP OP LPAREN
    PRECEDENCE COMMA
    ASSOCIATIVITY COMMA
    atom RPAREN END_OF_CLAUSE
    ;
    
rule:
    callable (IMPLIES_OP goal)? END_OF_CLAUSE
;

goal:
    callable |
    conjunction |
    disjunction |
    naf |
    if_then_else |//IF-THEN-ELSE
    //if_then |//
    soft_cut |
    case |
    var | //preprocessed with call(Var)
// Var -> call(Var)
    snips | // GREEN SNIPS
    curles  // EXECUTED WITHOUT PREPROCESSING// EOPROC
//    dcg_goal
    ;
   conjunction : TRUE | callable ( AND | OR ) ( callable | conjunction)*
;
   disjunction : callable ( OR | AND) ( callable | disjunction)*
;

naf: NON_PROVABLE callable
;

if_then_else: callable RARROW callable OR callable
;

case_then: callable RARROW callable
;

soft_cut: callable SOFT_CUT callable
;
//conk
//ARITY PROLOG case(+[A1 -> B1, A2 -> B2,...|C])
case: CASE_LPAREN_LBRACE case_then+ ALT callable RBRACE_RPAREN
;


nil: LBRACE RBRACE;

curles: LBRACKET RBRACKET;

cut : CUT;

callable : atom | compound;

snips : LSNIPS conjunction RSNIPS; // cannot bу

compound: name LPAREN args RPAREN; //| compound_2;

name : atom;

args: LPAREN (term ( COMMA term)*)? ( VBAR term )? RPAREN ;

list : LBRACE (term ( COMMA term)*)? ( VBAR term )? RBRACE;

predicate_indicator : atom ( SLASH | SLASH_SLASH ) PROLOG_ARITY;

term_0:
        var | nonvar |
        parenthesized_block |
        braced_block |
        map_ref |
        jref
        ;

nonvar :
        compound |
        list |
        atomic
        ;

//============================================
chars:
    | back_quote_string # back_quoted_chars
    | SEMICOLON         # semicolon //fixme its binary operator!!  compound arity 2
    | cut               # cut_term
    | number            # number_term
    | map_ref           # map_ref_term
    | jref              # java_reference
;


atom:
    UNQUOTED_ATOM |
    SYMBOLIC_ATOM  |
    QUOTED_STRING
;

atomic:
    atom                # atom_term
    | EMPTY_PARENS      # empty_parens
    | EMPTY_BRACES      # empty_braces
    | SPECIAL_TOKEN     # special_token //???
    | string            # quote_chars// string???
    | double_quote_string #dqs
;

    jref: JREF LPAREN atom RPAREN;

//skolem : VARNO LPAREN integer  RPAREN;// TODO and name

number: integer | float; /*| big_integer | big_decimal rationals */

string : STRING_LITERAL; //depends on flag ( at least in SWI)todo char_code* ( bnf )

//rational:
//blob/2
//ground:
//ground:
//  numbervars(0);
//skolem: Skolem -> skip;
//cyclic_term/1
//acyclic_term/1

term :
    operation |
    term_0
    ;

//native_operation :
//    native_binary_operation | native_unary_operation
//;

//custom_operation:
//    custom_binary_operation | custom_unary_operation
//;

binary_operation :
     term_0 operator ( known | custom ) ( xfx | xfy | yfx ) term
// ;

//operation :
//    binary_operation | unary_operation
//     ;

native_unary_operation :
     known_fx_operator term_0 |
     known_fy_operator term_0 |
     term_0 known_xf_operator |
     term_0 known_yf_operator
;

custom_binary_operation :
    term_0 custom_xfx_operator term |
    term_0 custom_xfy_operator term |
    term_0 custom_yfx_operator term
;

custom_unary_operation :
    custom_fx_operator term_0 |
    custom_fy_operator term_0 |
    term_0 custom_xf_operator |
    term_0 custom_yf_operator
;

braced_block : LBRACE term RBRACE;

parenthesized_block : LPAREN term RPAREN;

curled_block : LBRACKET callable RBRACKET;

map_ref :
        ( braced_block | var )
        MAP_OP
        ( atom | integer | var )
        ;


//=======================================================================================
// Lexer (6.4 & 6.5): Tokens formed from Characters
custom_xfx_operator:
term_0 custom_xfx_operator term
{
    current_op();
};

custom_xfy_operator:
{
    lookup_operator();
};

custom_yfx_operator:
{
    lookup_operator();
};

custom_fx_operator: {lookup_operator();}
        ;
custom_fy_operator: {lookup_operator();}
        ;
custom_xf_operator: {lookup_operator();}
        ;
custom_yf_operator: {lookup_operator();}
    ;
known_xfx_operator :
        IMPLIES_OP | DCG_IMPLIES_OP |
        EQ_PASCAL | UNIV |
        LESS | EQ | EQ_AT_EQ | NOT_EQ_AT_EQ | 
        ARITH_EQ_EQ | EQ_OR_LESS | EQ_EQ | EQ_BS_EQ | 
        GREATER | GREATER_OR_EQ | NOT_EQ | NOT_EQ_EQ |
        AT_LESS | AT_LESS_OR_EQ | AT_GREATER | AT_GREATER_EQ |
        GRT_COLON_LESS | COLON_LESS |
        AS | IS
        ;

known_xfy_operator :
        COLON |
        UP |
        COMMA |
        SEMICOLON |
        OR
        ;

known_yfx_operator :
        END_OF_CLAUSE | MULT | SLASH | SLASH_SLASH | DIV | RDIV | LSHIFT | RSHIFT | MOD | REM
        ;

known_fy_operator :
        PLUS | MINUS | BACKSLASH
;

known_fx_operator :
        IMPLIES_OP |
        PLUS  |
        MINUS |
        COLON_COLON |
        BACKSLASH |
        QUESTION  |
        NON_PROVABLE |
        TABLE |
        DYNAMIC |
        HILOG |
        DISCONTIGUOUS |
        INITIALIZATION |
        META_PREDICATE |
        MODULE_TRANSPARENT |
        MULTIFILE |
        PUBLIC_OP |
        PRIVATE_OP |
        PROTECTED_OP |
        THREAD_LOCAL |
        THREAD_INITIALIZATION |
        VOLATILE |
        DOLLAR //precedence = 1
        ;

known_yf_operator :
        PLUS | MINUS | BACKSLASH
        ;
known_xf_operator:
        ;

unquoted_atom:
    LOWER_LETTER ( UNDERSCORE | ALPHA_NUM )*?
    ;

integer: // 6.4.4
      DECIMAL
    | char_code_const
    | BINARY
    | OCTAL
    | HEX
    ;

char_code_const:
    ZERO SINGLE_QUOTE_CHAR integer;

//=====================================================================

letter_digit: ALPHA_NUM+; // 6.4.2

quote_string:
    SINGLE_QUOTE_CHAR
    ( CONTINUATION_ESCAPE | SINGLE_QUOTE_CHAR )*?//FIXME
    SINGLE_QUOTE_CHAR ; // 6.4.2

double_quote_string:
    DOUBLE_QUOTE_CHAR
    ( CONTINUATION_ESCAPE | DOUBLE_QUOTE_CHAR )*?//FIXME
    DOUBLE_QUOTE_CHAR; // 6.4.6

back_quote_string:
    BACK_QUOTE_CHAR
    ( CONTINUATION_ESCAPE | DOUBLE_QUOTE_CHAR )*?//FIXME
    BACK_QUOTE_CHAR; //6.4.7

symbolic_atom:
    COMBINABLE_OPERATOR_SYMBOLS | NON_COMBINABLE_OPERATOR_SYMBOLS;

//op_merge:
op_merge: symbolic_atom symbolic_atom
{
    throw new Error("Operator merging. space will be inserted in between");
};

var : named_variable | anonymous_variable;

named_variable :
    UPPER_LETTER | UNDERSCORE |    // 6.4.3 todo
    ( UPPER_LETTER | ALPHA_NUM | UNDERSCORE )* ;

anonymous_variable : UNDERSCORE;

attributed_variable : AV;

float:
       DECIMAL DOT DIGIT+ ( EXPONENT SIGN DECIMAL )? ;


