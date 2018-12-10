parser grammar HiLogParser;

tokens{
    HiLogLexer
}

options
{
    superClass = PrologParser;
    language = Java;
}

@lexer::members{
 protected const int EOF = Eof;
 protected const int HIDDEN = Hidden;
// public int merged_ops_detected = 1;
}

@header {
    package  org.ltc.hilog.grammar;
}

import PrologParser;

start_hilog_rule:
        hl_file
;

hl_file:
    pl_file |
    ( DOC_COMMENT | hl_clause | hl_entity )* EOF
;

hl_entity:
    pl_entity;

hl_compound:
    compound |
    operation |
    hl_name LPAREN args RPAREN
;

//hl_args:
//term ( COMMA term )*  |
//term? BAR list | var
//;

hl_name:
    name |
    hl_atom |
    var |
    hl_compound
    ;

hl_clause :
    hl_directive |
    hl_horn_clause;


hl_directive:
   directive |
   hl_module_directive |
   IMPLIES_OP hl_goal END_OF_CLAUSE
   ;

hl_module_directive:
    pl_module_directive |
    hilog_directive
;

hilog_directive :
    IMPLIES_OP HILOG non_empty_term_sequence END_OF_CLAUSE
;

non_empty_term_sequence :
    term ( COMMA term )*
;

hl_goal:
    goal |
    hl_compound | hl_atom | var
;

hl_horn_clause:
    horn_clause |
    hl_rule |
    hl_module_directive COLON hl_rule
;

hl_atom:
    atom PAREN RPAREN
;

hl_rule:
    callable ( IMPLIES_OP hl_goal )? END_OF_CLAUSE
;