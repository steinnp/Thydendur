VAR t0
VAR i
VAR j
GOTO main
main: ASSIGN t0 t0
ASSIGN 1 j
ASSIGN 0 i
l2: ASSIGN t0 t0
VAR t1
LT i 10 l3
ASSIGN 0 t1
GOTO l4
l3: ASSIGN t0 t0
ASSIGN 1 t1
l4: ASSIGN t0 t0
EQ 0 t1 l1
VAR t2
ADD i j t2
ASSIGN t2 j
VAR t3
GT i 0 l5
ASSIGN 0 t3
GOTO l6
l5: ASSIGN t0 t0
ASSIGN 1 t3
l6: ASSIGN t0 t0
NE t3 0 l7
GOTO l8
l7: ASSIGN t0 t0
VAR t4
ADD j 1 t4
ASSIGN t4 j
GOTO l9
l8: ASSIGN t0 t0
l9: ASSIGN t0 t0
ADD i 1 i
GOTO l2
l1: ASSIGN t0 t0
APARAM i
CALL writeln
APARAM j
CALL writeln
RETURN
