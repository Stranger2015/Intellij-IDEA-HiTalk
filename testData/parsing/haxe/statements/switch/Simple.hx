class Simple {
    function foo(){
        switch( v ) {
        case 0:
            e1();
        case foo(1):
            break;
        case 65, 90:
            e3();
        case bar: //<– logtalk plugin says 'or case expected, got default'
        default:
            return;
        }
    }
}