package compilerproj;

public class Token {
    TokenCode tokenCode;
    OpType opType;
    DataType dataType;
    SymbolTableEntry symbol;
    public Token (TokenCode tokenCode, OpType opType, DataType dataType, String symbol) {
        this.tokenCode = tokenCode;
        this.opType = opType;
        this.dataType = dataType;
        if (!symbol.equals("")) {
            this.symbol = new SymbolTableEntry(symbol);
        } else {
            this.symbol = null;
        }
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

