        LT          i       j       if
        VAR         t1
        DIV         j       2       t1
        ASSIGN      t1      j
        GOTO        ret
if:     VAR         t2
        MULT        j       2       t2
        ASSIGN      t2      j
ret:    APARAM      j
        CALL        writeln
        


