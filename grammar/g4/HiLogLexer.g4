lexer grammar HiLogLexer;

@header{
    package org.ltc.grammar.hilog;
}

options
   {
        superClass = LogtalkLexer;
        language = Java;
   }

import PrologLexer;

tokens
   { TOKEN_REF , RULE_REF , LEXER_CHAR_SET }

channels
   { OFF_CHANNEL }
