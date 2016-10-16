package compilerproj;

public class SymbolTableEntry {
    String lexeme;
    public SymbolTableEntry(String lexeme) {
        this.lexeme = lexeme;
    }
    public String getLexeme() {
        return this.lexeme;
    }
}
