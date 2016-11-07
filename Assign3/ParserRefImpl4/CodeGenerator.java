import java.util.*;
public class CodeGenerator {

    public CodeGenerator() {}

    public void generate(TacCode code, SymbolTableEntry param1, SymbolTableEntry param2, SymbolTableEntry result) {
        QuadrupleList.addQuad(new Quadruple(code, param1, param2, result));
    }

    public void generateFParams(ArrayList<SymbolTableEntry> entries) {
        for (SymbolTableEntry entry:
             entries) {
            generate(TacCode.FPARAM, null, null, entry);
        }
    }

    public void generateAParams(ArrayList<SymbolTableEntry> entries) {
        for (SymbolTableEntry entry:
                entries) {
            generate(TacCode.APARAM, null, null, entry);
        }
    }

    public void print() {
        QuadrupleList.printList();
    }
}