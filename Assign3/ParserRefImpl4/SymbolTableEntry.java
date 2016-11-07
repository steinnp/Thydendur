public class SymbolTableEntry {
    private String m_lexeme;
    private String m_scope;
    private TokenCode tokenType;

    public SymbolTableEntry(String lexeme) {
        m_lexeme = lexeme;
    }

    public SymbolTableEntry(String lexeme, String scope) {
        m_lexeme = lexeme;
        m_scope = scope;
    }

    public String getLexeme() {
        return m_lexeme;
    }
    public String getScope() {
        return m_scope;
    }
    public void setScope(String scope) {
        m_scope = scope;
    }
    public void setTokenType(TokenCode _type) {
        tokenType = _type;
    }
    public TokenCode getTokenType() {
        return tokenType;
    }
}