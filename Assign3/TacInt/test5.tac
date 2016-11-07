        VAR     n
        VAR     sum
        ASSIGN  0       sum
        ASSIGN  0       n
for:    GE      n       10      ret
        VAR     t1
        MULT     n       n       t1
        VAR     t2
        ADD     sum     t1      t2
        ASSIGN  t2      sum
        VAR     t3
        ADD     1       n       t3
        ASSIGN  t3      n
        GOTO    for
ret:    APARAM sum
        CALL writeln
