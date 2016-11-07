public class Quadruple {

  private TacCode code;
  private SymbolTableEntry param1;
  private SymbolTableEntry param2;
  private SymbolTableEntry result;

  public Quadruple (TacCode _code, SymbolTableEntry _param1, SymbolTableEntry _param2, SymbolTableEntry _result) {
    this.code = _code;
    this.param1 = _param1;
    this.param2 = _param2;
    this.result = _result;
  }

  public Quadruple (TacCode _code, SymbolTableEntry _param1, SymbolTableEntry _result) {
    this.code = _code;
    this.param1 = _param1;
    this.result = _result;
  }

  public Quadruple (TacCode _code, SymbolTableEntry _result) {
    this.code = _code;
    this.result = _result;
  }

  public Quadruple (TacCode _code) {
    this.code = _code;
  }

  public TacCode getTacCode() {
    return code;
  }

  public SymbolTableEntry getParam1() {
    return param1;
  }

  public SymbolTableEntry getParam2() {
    return param2;
  }

  public SymbolTableEntry getResult() {
    return result;
  }
}
