package compilerproj;

import java.util.ArrayList;

public class SymbolTable {
    private static ArrayList<SymbolTableEntry> entries = new ArrayList<SymbolTableEntry>();
    public static SymbolTableEntry addEntry(String lexeme) {
        SymbolTableEntry entry = new SymbolTableEntry(lexeme);
        entries.add(entry);
        return entry;
    }
    public static SymbolTableEntry findEntry(String lexeme) {
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).getLexeme().equals(lexeme)) {
                return entries.get(i);
            }
        }
        return null;
    }
    public static void printSymbolTable() {
        for (int i = 0; i < entries.size(); i++) {
            System.out.println(i + "    " + entries.get(i).getLexeme());
        }
    }
}
