lexer grammar HiLogtalkLexer;

@header {
    package org.ltc.grammar.hilogtalk;
}

options
{
        language = Java;
        superClass = HiLogLexer;
}

import LogtalkLexer;//, HiLogLexer;// Standard set of fragments

tokens
   { TOKEN_REF , RULE_REF , LEXER_CHAR_SET }

channels
   { OFF_CHANNEL }


