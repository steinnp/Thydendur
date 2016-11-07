public class TacCodePrinter {
    public TacCodePrinter(){};
    public static void printTacCode(TacCode tac) {
        switch (tac) {
            case LABEL:
                System.out.print("LABEL");
                break;
            case UMINUS:
                System.out.print("UMINUS");
                break;
            case ASSIGN:
                System.out.print("ASSIGN");
                break;
            case ADD:
                System.out.print("ADD");
                break;
            case SUB:
                System.out.print("SUB");
                break;
            case MULT:
                System.out.print("MULT");
                break;
            case DIVIDE:
                System.out.print("DIVIDE");
                break;
            case DIV:
                System.out.print("DIV");
                break;
            case MOD:
                System.out.print("MOD");
                break;
            case OR:
                System.out.print("OR");
                break;
            case AND:
                System.out.print("AND");
                break;
            case NOT:
                System.out.print("NOT");
                break;
            case LT:
                System.out.print("LT");
                break;
            case LE:
                System.out.print("LE");
                break;
            case GT:
                System.out.print("GT");
                break;
            case GE:
                System.out.print("GE");
                break;
            case EQ:
                System.out.print("EQ");
                break;
            case NE:
                System.out.print("NE");
                break;
            case GOTO:
                System.out.print("GOTO");
                break;
            case CALL:
                System.out.print("CALL");
                break;
            case APARAM:
                System.out.print("APARAM");
                break;
            case FPARAM:
                System.out.print("FPARAM");
                break;
            case VAR:
                System.out.print("VAR");
                break;
            case RETURN:
                System.out.print("RETURN");
                break;
            case NOOP:
                System.out.print("NOOP");
                break;
        }
    }
}