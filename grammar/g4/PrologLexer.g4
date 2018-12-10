lexer grammar PrologLexer;
//TODO prevent operatore merging: e.g. [;\\+];

@header {
    package  org.ltc.grammar.prolog;
}

options
{
       superClass = org.antlr.jetbrains.adaptor.lexer.ANTLRLexerAdaptor;
       language = Java;
}

//import LexerBasic;

// Standard set of fragments
tokens
   { TOKEN_REF , RULE_REF , LEXER_CHAR_SET }

channels
   { OFF_CHANNEL }
   
   
   
//========================================================================================   

fragment SPECIAL: [#$&*+./:<=>?@^~] | MINUS ; // 6.5.1 graphic char (Speciafragment PROLOG_ARITY: '\u0001' .. '\uffff';
                                                                           fragment TRUE:  'true';
                                                                           fragment FAIL:  'fail';
                                                                           fragment FALSE: 'false';
                                                                           // 6.4.4
                                                                           fragment DECIMAL: DIGIT+ ;
                                                                           fragment BINARY: '0b' [01]+ ;
                                                                           fragment OCTAL: '0o' [0-7]+ ;
                                                                           fragment HEX: '0x' HEX_DIGIT+ ;
                                                                           fragment EXPONENT : [eE];
                                                                           fragment SIGN: [+-];
                                                                           // 6.4.2.1
                                                                           fragment SINGLE_QUOTE_CHAR: '\'' ;
                                                                           fragment DOUBLE_QUOTE_CHAR:  '"' ;
                                                                           fragment ACK_QUOTE_CHAR:  '`' ;
                                                                           fragment SPECIAL_TOKEN: ( SPECIAL | BACKSLASH )+ ; // 6.4.2
l)

//    | ALPHA_NUM
//    | PUNCTUATION // fixme really?
//    | '\u0020' // space char
//    | META_ESCAPE
//    | CONTROL_ESCAPE
//    | OCTAL_ESCAPE
//    | HEX_ESCAPE
//    ;
fragment META_ESCAPE: BACKSLASH [\\'"`] ; // meta char
fragment CONTROL_ESCAPE: BACKSLASH [abrftnv];
fragment OCTAL_ESCAPE: BACKSLASH [0-7]+ BACKSLASH ;
fragment HEX_ESCAPE: '\\x' HEX_DIGIT+ BACKSLASH ;
fragment UNICODE: '\\u' HEX_DIGIT+ BACKSLASH ;


fragment CONTINUATION_ESCAPE: '\\\n' ;
 // 6.5.2
fragment ALPHA_NUM: ALPHA | DIGIT ;
fragment ALPHA: LOWER_LETTER | UPPER_LETTER;
fragment LOWER_LETTER: [a-z];
fragment UPPER_LETTER: [A-Z];
fragment DIGIT: [0-9] ;
fragment HEX_DIGIT: [0-9a-fA-F] ;
//// 6.5.3
fragment PUNCTUATION:
        CUT |
        LPAREN | RPAREN |
        COMMA |
        SEMICOLON |
        LBRACE | RBRACE |
        LBRACKET | RBRACKET |
        OR |
        PERCENT;

fragment PERCENT: '%';
fragment NEW_LINE_CHAR: '\n' | '\r' | '\u2028' |'\u2029';
fragment NEW_LINE:'\r\n'| NEW_LINE_CHAR;
fragment WHITE_SPACE: '\u0020' | '\t' | '\f' |'\u00F1'/* '\v' */ | '\uFEFF' | '\u0000';
fragment SPACE: WHITE_SPACE | NEW_LINE;

//NO_SPACE : SPACE* ?
//{
//    throw new IllegalStateException("Syntax error: no space(s) allowed here");
//} ->  skip;



FORCED_SPACE :
        (( NON_COMBINABLE_OPERATOR_SYMBOLS SPACE+ COMBINABLE_OPERATOR_SYMBOLS)? |
        ( COMBINABLE_OPERATOR_SYMBOLS  SPACE+ NON_COMBINABLE_OPERATOR_SYMBOLS)? { merged_ops_detected = 0){

        }
        }) -> skip;




fragment LPAREN: '('; //{parensCount++;} -> pushMode(PARENS_BLOCK), mode( PARENS_BLOCK );//??? fixme

fragment RPAREN: ')' ;//{
//                if( --parensCount == 0 ){}
            //}
//              -> popMode;
//mode BRACES_BLOCK;
fragment LBRACE:   '[';// {braceCount++; } -> pushMode(BRACES_BLOCK), mode( BRACES_BLOCK );//???? fixme
fragment RBRACE:   ']';// {braceCount++; } -> pushMode(BRACES_BLOCK), mode( BRACES_BLOCK );//???? fixme
//                }
//                           -> popMode;
//

fragment LBRACKET:   '{';// {braceCount++; } -> pushMode(BRACES_BLOCK), mode( BRACES_BLOCK );//???? fixme

fragment RBRACKET:   '}';//  { if( --braceCount == 0 ){}
//                }

//mode SNIPS_MODE;
fragment LSNIPS:   '[!' ;//
fragment RSNIPS:   '!]' ;//

fragment CUT: '!';  // can be !/0 and !/1;
fragment SLASH: '/';
fragment SLASH_SLASH: '//';
fragment UNIV: '=..';
fragment DOT: '.';
fragment END_OF_CLAUSE: DOT;
fragment COMMA: ',';
fragment AND: COMMA;
fragment IMPLIES_OP: ':-';
fragment DCG_IMPLIES_OP: '-->';
fragment COLON : ':';
fragment COLON_COLON: '::';//self
fragment SEMICOLON : ';';
fragment OR : ';';

fragment VBAR : '|' ;
fragment MAP_OP: 'map';
fragment ANY : '\u0000' .. '\uFFFF'
;

STYLE_COMMENT: ('%!'|'%%')[^\r\n]*;
LINE_COMMENT: '%' ~[\n\r]* ( [\r\n]? | EOF )  -> channel(HIDDEN);
BLOCK_COMMENT: '/*' ( BLOCK_COMMENT | . )*? ('*/' | EOF ) -> channel(HIDDEN); // ( BLOCK_COMMENT | . ) fixme
DOC_COMMENT : '/**' DOC_COMMENT_CONTENT '*/';
fragment DOC_COMMENT_CONTENT: ( [^*] | '\\*' + [^/*])-> channel(HIDDEN);

fragment LBLOCK: '/*';
fragment LDOC: '/**';
fragment RBLOCK: '*/';
fragment RDOC: RBLOCK;

COMMENT: ( STYLE_COMMENT | LINE_COMMENT | BLOCK_COMMENT | DOC_COMMENT )*;
ANY_BLOCK_COMMENT: ( BLOCK_COMMENT | DOC_COMMENT )*;
NON_PRINTABLE: ( NEW_LINE | WHITE_SPACE )+;
NON_CODE: ( NON_PRINTABLE | ANY_BLOCK_COMMENT );

COMBINABLE_OPERATOR_SYMBOLS:
        LESS | GREATER |
        QUESTION | SLASH |
        SEMICOLON | COLON | BACKSLASH |
        OR | EQ | PLUS | MINUS | MULT |
        AMP | UP | DOLLAR | HASH | AT | TILDE;
        
fragment LESS: '<';
fragment GREATER: '>';
fragment UNDERSCORE: '_';
fragment QUESTION: '?';
fragment EQ: '=';
fragment PLUS: '+';
fragment MINUS: '-';
fragment MULT: '*';
fragment BACKSLASH: '\\';
fragment AMP: '&';
fragment DOLLAR: '$';
fragment HASH: '#';
fragment AT:   '@';
fragment TILDE: '~';
fragment UP: '^';
fragment EQ_PASCAL:      ':=';
fragment EQ_AT_EQ:       '=@=';
fragment NOT_EQ_AT_EQ:   '\\=@=';
fragment EQ_OR_LESS: '=<';
fragment EQ_EQ: '==';
fragment EQ_BS_EQ : '=\\=';
fragment GREATER_OR_EQ : '>=';
fragment NOT_EQ: '\\=';
fragment NOT_EQ_EQ : '\\==';
fragment AT_LESS :'@<';
fragment AT_LESS_OR_EQ:'@=<';
fragment AT_GREATER : '@>';
fragment AT_GREATER_EQ : '@>=';
fragment GRT_COLON_LESS : '>:<' ;
fragment COLON_LESS : ':<' ;
fragment AS : 'as';
fragment IS : 'is';
fragment DIV: 'div';
fragment RDIV: 'rdiv';
fragment LSHIFT: '<<';
fragment RSHIFT: '>>';
fragment MOD: 'mod';
fragment REM:'rem';
fragment NON_PROVABLE: '\\+';
fragment TABLE:              'table';
fragment DYNAMIC:            'dynamic';
fragment DISCONTIGUOUS:      'discontiguous';
fragment INITIALIZATION :    'initialization' ;
fragment META_PREDICATE :    'meta_predicate' ;
fragment MODULE_TRANSPARENT :'module_transparent' ;
fragment MULTIFILE :         'multifile' ;
fragment PUBLIC_OP :         'public'    ;
fragment THREAD_LOCAL :      'thread_local' ;
fragment THREAD_INITIALIZATION : 'thread_initialization' ;
fragment VOLATILE:               'volatile' ;
fragment PROLOG_QUERY: '?-';
fragment HILOG_QUERY:  HILOG SPACE* PROLOG_QUERY;
fragment LOGTALK_QUERY: '[|=]';
fragment HILOGTALK_QUERY: HILOG SPACE* LOGTALK_QUERY;

fragment  PROLOG:         'prolog';
fragment  HILOG:          'hilog';
fragment  LOGTALK:        'logtalk';
fragment  HILOGTALK:      'hilogtalk';

NON_COMBINABLE_OPERATOR_SYMBOLS:
        COMMA | CUT;

OPERATOR_SYMBOLS: //todo clarify
        NON_COMBINABLE_OPERATOR_SYMBOLS |
        COMBINABLE_OPERATOR_SYMBOLS+ |
        COMBINABLE_OPERATOR_SYMBOLS |
        END_OF_CLAUSE;

PAR_OPERATOR_SYMBOLS: //todo clarify
//parenthesized operator symbols
        (
            NON_COMBINABLE_OPERATOR_SYMBOLS |
            COMBINABLE_OPERATOR_SYMBOLS |
            END_OF_CLAUSE
        )+
        ;

fragment  VARNO: '$VARNO';
fragment  SOFT_CUT:  '*->';
fragment  MODULE:    'module';

//=======================
fragment PROLOG_TYPE:
        TERM |
        NONVAR |
        VAR |
        COMPOUND |
        GROUND |
        CALLABLE |
        LIST |
        ATOMIC |
        ATOM |
        NUMBER |
        INTEGER |
        FLOAT |
        STRING |
        CHAR
    ;

fragment ZERO: '0';

//fragment ASSOCIATIVITY:
//        XFX |
//        YFX |
//        XFY |
//        FX  |
//        XF  |
//        FY  |
//        YF;

fragment XFX:'xfx';
fragment YFX:'yfx';
fragment XFY:'xfy';
fragment FX:'fx';
fragment FY:'fy';
fragment XF:'xf';
fragment YF:'yf';

fragment PRECEDENCE: '\u0001' .. '\u04B0';//1..1200
fragment LARROW:'<-';
fragment RARROW:'->';

fragment TERM      : 'term';
fragment NONVAR    : 'nonvar';
fragment VAR       : 'var';
fragment COMPOUND   : 'compound';
fragment GROUND :     'ground';
fragment CALLABLE :   'callable';
fragment LIST :       'list';
fragment ATOMIC : 'atomic';
fragment ATOM : 'atom';
fragment NUMBER : 'number';
fragment INTEGER : 'integer';
fragment FLOAT : 'float';
fragment STRING : 'string';
fragment CHAR : 'char';

fragment AV : 'AV_' ATOM;
