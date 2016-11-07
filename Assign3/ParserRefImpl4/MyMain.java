import java.io.*;

public class MyMain {
  public static final boolean TRACE = false;

  public static void main(String [] args) throws IOException {
    Lexer lexer = new Lexer(new FileReader(args[0]));
    CodeGenerator generator = new CodeGenerator();
    Parser parser = new Parser(lexer, args[0], generator);
    parser.program();
    
  }
}