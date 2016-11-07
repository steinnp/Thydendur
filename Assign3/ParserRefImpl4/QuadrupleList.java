import java.util.*;

public class QuadrupleList {
    private static ArrayList<Quadruple> quadList = new ArrayList<>();
    public QuadrupleList() {
  }

    public ArrayList<Quadruple> getQuadList() {
        return quadList;
    }

    public static void addQuad(Quadruple quad) {
        quadList.add(quad);
    }

    public static void printList() {
        for (Quadruple quad: quadList) {
            if (quad.getTacCode() == TacCode.LABEL) {
                System.out.println(quad.getResult().getLexeme() + ": ASSIGN t0 t0");
            } else {
                TacCodePrinter.printTacCode(quad.getTacCode());
                if (quad.getParam1() != null) {
                    System.out.print(" " + quad.getParam1().getLexeme());
                }
                if (quad.getParam2() != null) {
                    System.out.print(" " + quad.getParam2().getLexeme());
                }
                if (quad.getResult() != null) {
                    System.out.print(" " + quad.getResult().getLexeme());
                }
                System.out.println();
            }
        }
    }
}
