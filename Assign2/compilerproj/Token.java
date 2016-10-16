package compilerproj;

public class Token {
    private TokenCode tokenCode;
    private OpType opType;
    private DataType dataType;
    private SymbolTableEntry symbol;
    private int line;
    private int column;
    public Token (TokenCode tokenCode, OpType opType, DataType dataType, String symbol, int line, int column) {
        this.tokenCode = tokenCode;
        this.opType = opType;
        this.dataType = dataType;
        this.line = line;
        this.column = column;
        if (!symbol.equals("")) {
            this.symbol = new SymbolTableEntry(symbol);
        } else {
            this.symbol = null;
        }
    }
    public Token (
            TokenCode tokenCode,
            OpType opType,
            DataType dataType,
            String symbol,
            int line,
            int column,
            SymbolTableEntry entry
            ) {
        this.tokenCode = tokenCode;
        this.opType = opType;
        this.dataType = dataType;
        this.line = line;
        this.column = column;
        this.symbol = entry;
    }
    public TokenCode getTokenCode (){
        return this.tokenCode;
    }
    public String getSymbol (){
        if (this.symbol == null) {
            return "";
        }
        return this.symbol.getLexeme();
    }
    public OpType getOpType (){
        return this.opType;
    }
    public DataType getDataType (){
        return this.dataType;
    }
}

