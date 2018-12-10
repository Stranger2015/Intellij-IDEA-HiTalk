lexer grammar LogtalkLexer ;

@header {
    package  org.ltc.grammar.logtalk;
}

options
{
      superClass =  PrologLexer;
      language   = Java;
}

// Standard set of fragments
tokens
   { TOKEN_REF , RULE_REF , LEXER_CHAR_SET }

channels
   { OFF_CHANNEL }

fragment OBJECT:            'object'  ;
fragment PROTOCOL:          'protocol'  ;
fragment CATEGORY:          'category'  ;
fragment EXTENDS:           'extends'  ;

fragment IMPLIES_OP_OBJECT_LPAREN:        ':- object(' ;
fragment IMPLIES_OP_PROTOCOL_LPAREN:      ':- protocol(' ;
fragment IMPLIES_OP_CATEGORY_LPAREN:      ':- category(' ;
fragment IMPLIES_OP_END_OBJECT_END_OF_CLAUSE:   ':- end_object.' ;
fragment IMPLIES_OP_END_PROTOCOL_END_OF_CLAUSE: ':- end_protocol.' ;
fragment IMPLIES_OP_END_CATEGORY_END_OF_CLAUSE: ':- end_category.' ;
fragment DECLARED_IN: 'declared_in' ;
fragment DEFINED_IN: 'defined_in' ;
fragment SCOPE: 'scope' ;
fragment NON_NEGATIVE_INTEGER: ZERO | POSITIVE_INTEGER+ ;
POSITIVE_INTEGER: [1..1844561817809806950]; //64-bit

//term_tag_schema64: int61 | int63 ;
fragment IMPLEMENTS:        'implements'  ;
fragment SPECIALIZES:       'specializes'  ;
fragment IMPORTS:           'imports'  ;
fragment INSTANTIATES:      'instantiates'  ;
fragment COMPLEMENTS:       'complements'  ;
fragment UP_UP:             '^^';//SUPEfragment SYNCHRONIZED:      'synchronized'  ;
fragment COMMENT_KEYWORD:   'comment'  ;
fragment REMARKS:           'remarks'  ;
fragment ARGUMENTS:         'arguments'  ;
fragment ARGNAMES:          'argnames'  ;
fragment REDEFINITION:      'redefinition'  ;
fragment ALLOCATION:        'allocation'  ;
fragment EXCEPTIONS:        'exceptions' ;
fragment ALLOW:             'allow' ;
fragment RESTRICT:          'restrict' ;
fragment NUMBER_OF_CLAUSES: 'number_of_clauses'  ;
fragment NUMBER_OF_RULES:   'number_of_rules'  ;
fragment NUMBER_OF_USER_CLAUSES: 'number_of_user_clauses'  ;
fragment NUMBER_OF_USER_RULES:   'number_of_user_rules'  ;
fragment COINDUCTIVE:        'coinductive' ;
fragment ZERO :        'zero'   ;
fragment ZERO_OR_ONE:  'zero_or_one'  ;
fragment ZERO_OR_MORE: 'zero_or_more'  ;
fragment ONE :         'one' ;
fragment ONE_OR_MORE:  'one_or_more' ;
fragment ONE_OR_ERROR: 'one_or_error' ;
fragment ERROR:        'error' ;
fragment CONTEXT_SWITCHING_CALLS:'context_switching_calls' ;
fragment DYNAMIC_DECLARATIONS:'dynamic_declarations' ;
//term_tag_schema32: int29 | int31 ; int 38

fragment FOR: 'for' ;
fragment FROM: 'from' ;
fragment FILE: 'file' ;
fragment LINES: 'lines' ;
fragment SOURCE_DATA: 'source_data' ;
fragment BUILT_IN: 'built_in' ;
fragment EVENTS: 'events' ;
fragment DECLARES: 'declares' ;
fragment DEFINES: 'defines' ;
fragment PROVIDES: 'provides' ;
fragment ALIAS:'alias' ;
fragment CALLS:'calls' ;
fragment UPDATES:'updates' ;
fragment DYNAMIC:'dynamic' ;
fragment THREADED:'threaded' ;
fragment NON_TERMINAL:'non_terminal' ;
fragment LINE_COUNT:'line_count' ;

fragment INITIALIZATION: 'initialization' ;

fragment USES:'uses' ;
fragment USE_MODULE:'use_module' ;
fragment SET_LOGTALK_FLAG:'set_logtalk_flag' ;
fragment GET_LOGTALK_FLAG:'get_logtalk_flag' ;
fragment CURRENT_LOGTALK_FLAG:'current_logtalk_flag' ;
fragment INCLUDE:     'include' ;
fragment META_NON_TERMINAL: 'meta_non_terminal' ;
fragment AUXILIARY:'auxiliary';
fragment NOT_IMPLEMENTED_YET: 'not_implemented_yet';
fragment PROTECTED: 'protected' ;
fragment PRIVATE: 'private';