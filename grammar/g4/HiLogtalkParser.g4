parser grammar HiLogtalkParser;

@header{
    package org.ltc.grammar.hilogtalk;
}

tokens {
  HiLogtalkLexer
}

options
{
    superClass = Parser;
    language = Java;
}

@lexer::members{
 protected const int EOF = Eof;
 protected const int HIDDEN = Hidden;
}

import LogtalkParser, HiLogParser;

start_hlgt_rule:
      hlgt_file
      ;

hlgt_file:
      lgt_file |
      ( DOC_COMMENT | hlgt_entity)* EOF;

hlgt_clause:
        lgt_clause |
        hlgt_directive |
        hlgt_horn_clause;

hlgt_directive:
    lgt_directive;

hlgt_entity :
        lgt_entity |
        hlgt_object |
        hlgt_category
        ;

hlgt_object:
     object |
     (
         hlgt_begin_object
         ( hlgt_object_directive | hlgt_rule)*
         end_object
     );

     hlgt_rule:
        lgt_rule ;//todo

     hlgt_begin_object:
         IMPLIES_OP OBJECT_LPAREN hlgt_object_identifier ( COMMA hlgt_object_relation)* RPAREN END_OF_CLAUSE;

hlgt_object_relation:
    object_relation;

hlgt_object_identifier:
    object_identifier |
    hl_compound |
    atom;

hlgt_object_directive:
     object_directive;

hlgt_category:
     category |
     (
         hlgt_begin_category
         ( hlgt_category_directive | hlgt_horn_clause )*
         end_category
     );

hlgt_begin_category:
    IMPLIES_OP CATEGORY LPAREN hlgt_category_identifier ( COMMA category_relation )* RPAREN END_OF_CLAUSE;

hlgt_category_identifier :
    category_identifier |
    hl_compound |
    atom
    ;

hlgt_horn_clause:
    horn_clause |
    hlgt_current_clause |
    hlgt_module_directive COLON hlgt_current_clause
;

hlgt_current_clause:
    hlgt_callable (IMPLIES_OP hlgt_goal)? END_OF_CLAUSE;

hlgt_category_directive:
    category_directive
    ;

hlgt_module_directive:
    hl_module_directive
;

hlgt_callable:
    callable |
    hl_compound
    ;

hlgt_goal :
    hl_goal
;

