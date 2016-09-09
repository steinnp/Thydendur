package compilerproj;

import java.util.ArrayList;

public class SymbolTable {
    ArrayList<SymbolTableEntry> entries;
    public SymbolTable() {
        this.entries = new ArrayList<SymbolTableEntry>();
    }
    public void addEntry(String lexeme) {
        if (this.findEntry(lexeme) == null) {
            this.entries.add(new SymbolTableEntry(lexeme));
        }
    }
    public SymbolTableEntry findEntry(String lexeme) {
        for (int i = 0; i < this.entries.size(); i++) {
            if (this.entries.get(i).getLexeme().equals(lexeme)) {
                return this.entries.get(i);
            }
        }
        return null;
    }
    public void printSymbolTable() {
        for (int i = 0; i < this.entries.size(); i++) {
            System.out.println(i + "    " + this.entries.get(i).getLexeme());
        }
    }
}
