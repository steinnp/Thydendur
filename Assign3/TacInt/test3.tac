VAR     t1
ADD     a   b   t1
VAR     t2
ADD     c   d   t2
VAR     t3
UMINUS  a   t3
VAR     t4
ADD     t3  b   t4
VAR     t5
ADD     t4  c   t5
VAR     t6
SUB     t2  t5  t6
VAR     t7
MULT    t1  t6  t7
APARAM  t7
CALL    writeln                              
RETURN                                             
