package compilerproj;

import java.io.*;

public class TokenDumper {
    public static void main(String[] args) throws IOException {
        Lexer lexer = new Lexer(new FileReader(args[0]));
        while(true) {
            Token t = lexer.yylex(); System.out.print(t.getTokenCode().toString());
            if (!t.getSymbol().equals(null)) {
                System.out.print("(" + t.getSymbol() + ")");
            } else if (!t.getOpType().toString().equals("NONE")) {
                System.out.print("(" + t.getOpType().toString() + ")");
            }
            if (t.getTokenCode().toString().equals("EOF")) {
                System.out.println();
                System.out.println();
                break;
            }
            System.out.print(" ");
        }
        SymbolTable.printSymbolTable();
        return;
        // TODO: Print out the symbol table
    }
}
