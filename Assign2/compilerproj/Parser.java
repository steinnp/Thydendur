package compilerproj;

import java.io.*;
import java.util.stream.*;
import java.util.*;
import java.nio.file.*;

public class Parser {
    private Lexer lexer;
    private Token currentToken;
    private Token nextToken;
    private String fileName;
    private int errorCount;
    // if true there was an error and the input needs to be synchronized
    private boolean error;
   
    public Parser(String fileName) {
        try {
            this.lexer = new Lexer(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            System.err.println("Cannot find file " + this.fileName);
        }
        this.fileName = fileName;
        this.errorCount = 0;
    }

    private String readLine(int lineNumber) {
        try (Stream<String> lines = Files.lines(Paths.get(this.fileName))) {
                String line = lines.skip(lineNumber).findFirst().get();
                return line;
        } catch (FileNotFoundException e) {
            System.err.println("Cannot find file " + this.fileName);
            return "";
        } catch (IOException e) {
            System.err.println("Cannot read line number " + lineNumber);
            return "";
        }
    }

    private void printError(String message, int line, int column) {
        String errorMessage = Integer.toString(line + 1) + " : " + readLine(line) + "\n";
        for (int i = 0; i < column + 3 + Integer.toString(line).length(); i++) {
            errorMessage += " ";
        }
        errorMessage += "^ ";
        errorMessage += message;
        System.out.println(errorMessage);
    }

    private void displayError(TokenCode t) {
        switch(t) {
            case IDENTIFIER:
                printError("Expected Identifier", this.nextToken.getLine(), this.nextToken.getColumn());
                break;
            case NUMBER:
                printError("Expected Number", this.nextToken.getLine(), this.nextToken.getColumn());
                break;
            case INCDECOP:
                printError("Expected ++ or --", this.nextToken.getLine(), this.nextToken.getColumn());
                break;
            case RELOP:
                printError("Expected Relational operator", this.nextToken.getLine(), this.nextToken.getColumn());
                break;
            case MULOP:
                printError("Expected *", this.nextToken.getLine(), this.nextToken.getColumn());
                break;
            case ADDOP:
                printError("Expected + or -", this.nextToken.getLine(), this.nextToken.getColumn());
                break;
            case ASSIGNOP:
                printError("Expected =", this.nextToken.getLine(), this.nextToken.getColumn());
                break;
            case CLASS:
                printError("Expected class keyword", this.nextToken.getLine(), this.nextToken.getColumn());
                break;
            case STATIC:
                printError("Expected static keyword", this.nextToken.getLine(), this.nextToken.getColumn());
                break;
            case VOID:
                printError("Expected void keyword", this.nextToken.getLine(), this.nextToken.getColumn());
                break;
            case IF:
                printError("Expected if keyword", this.nextToken.getLine(), this.nextToken.getColumn());
                break;
            case ELSE:
                printError("Expected else keyword", this.nextToken.getLine(), this.nextToken.getColumn());
                break;
            case FOR:
                printError("Expected for keyword", this.nextToken.getLine(), this.nextToken.getColumn());
                break;
            case RETURN:
                printError("Expected return keyword", this.nextToken.getLine(), this.nextToken.getColumn());
                break;
            case BREAK:
                printError("Expected break keyword", this.nextToken.getLine(), this.nextToken.getColumn());
                break;
            case CONTINUE:
                printError("Expected continue keyword", this.nextToken.getLine(), this.nextToken.getColumn());
                break;
            case LBRACE:
                printError("Expected {", this.nextToken.getLine(), this.nextToken.getColumn());
                break;
            case RBRACE:
                printError("Expected }", this.nextToken.getLine(), this.nextToken.getColumn());
                break;
            case LBRACKET:
                printError("Expected [", this.nextToken.getLine(), this.nextToken.getColumn());
                break;
            case RBRACKET:
                printError("Expected ]", this.nextToken.getLine(), this.nextToken.getColumn());
                break;
            case LPAREN:
                printError("Expected (", this.nextToken.getLine(), this.nextToken.getColumn());
                break;
            case RPAREN:
                printError("Expected )", this.nextToken.getLine(), this.nextToken.getColumn());
                break;
            case SEMICOLON:
                printError("Expected semicolon", this.currentToken.getLine(), this.currentToken.getColumn() + 1);
                break;
            case COMMA:
                printError("Expected ,", this.nextToken.getLine(), this.nextToken.getColumn());
                break;
            case NOT:
                printError("Expected !", this.nextToken.getLine(), this.nextToken.getColumn());
                break;
            case INT:
                printError("Expected int keyword", this.nextToken.getLine(), this.nextToken.getColumn());
                break;
            case REAL:
                printError("Expected real keyword", this.nextToken.getLine(), this.nextToken.getColumn());
                break;
            case ERR_LONG_ID:
                printError("Identifier too long", this.nextToken.getLine(), this.nextToken.getColumn());
                break;
            case ERR_ILL_CHAR:
                printError("Illegal character", this.nextToken.getLine(), this.nextToken.getColumn());
                break;
            case EOF:
                printError("End of file expected", this.nextToken.getLine(), this.nextToken.getColumn());
                break;
            default:
                return;

        }
    }
    
    /*
     * takes in a list of synchronizing tokens and reads in tokens until one 
     * of the tokens in the list is identified.
     */
    private void synchronize(ArrayList<TokenCode> token_list) {
        while (this.nextToken.getTokenCode() != TokenCode.EOF &&
               !token_list.contains(this.nextToken.getTokenCode())
        ) {
            getNext();
        }
    }

    private void getNext() {
      currentToken = nextToken;
      try {
        nextToken = lexer.yylex();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    private boolean accept(TokenCode t) {
       if (t == this.nextToken.getTokenCode()) {
           if (t != TokenCode.EOF) {
               getNext();
           }
           this.error = false;
           return true;
       } else {
           return false;
       }
        
    }

    /*
     * returns true if the value of nextToken is the same as t
     * false otherwise.
     */
    private boolean expect(TokenCode t) {
       if (accept(t)) {
           return true;
       } else {
           if (!this.error) {
               // error handling
               errorCount++;
               error = true;
               displayError(t);
           }
           return false;
       }
    }

    static ArrayList<TokenCode> programTokens1 = new ArrayList<TokenCode>(Arrays.asList(TokenCode.LBRACE));
    static ArrayList<TokenCode> programTokens2 = new ArrayList<TokenCode>(Arrays.asList(TokenCode.INT, TokenCode.REAL, TokenCode.STATIC));
    private void program() {
        System.out.println("program");
        if (expect(TokenCode.CLASS) == false) {
            synchronize(programTokens1);
        }
        if (expect(TokenCode.IDENTIFIER) == false) {
            synchronize(programTokens1);
        }
        // synchronize lbrace
        if (expect(TokenCode.LBRACE) == false) {
            synchronize(programTokens2);
        }
        variable_declarations();

        method_declarations();
        expect(TokenCode.RBRACE);
    }

    private void variable_declarations() {
        System.out.println("variable_declarations");
        if (this.nextToken.getTokenCode() != TokenCode.INT &&
            this.nextToken.getTokenCode() != TokenCode.REAL
        ) {
            return;
        }
        type();
        variable_list();
        expect(TokenCode.SEMICOLON);
        variable_declarations();
    }

    static ArrayList<TokenCode> typeTokens = new ArrayList<TokenCode>(Arrays.asList(TokenCode.COMMA, TokenCode.LPAREN, TokenCode.RPAREN, TokenCode.SEMICOLON, TokenCode.RBRACE));
    private void type() {
        System.out.println("type");
        if (accept(TokenCode.INT)) {
            return;
        } else if (accept(TokenCode.REAL)) {
            return;
        } else {
            //error handling
            printError("Expected type", this.nextToken.getLine(), this.nextToken.getColumn());
            synchronize(typeTokens);
            this.error = true;
            this.errorCount++;
            return;
        }
    }

    private void variable_list() {
        System.out.println("variable_list");
        variable();
        variable_list2();
    }

    private void variable_list2() {
        System.out.println("variable_list2");
        if (this.nextToken.getTokenCode() != TokenCode.COMMA) {
            return;
        }
        expect(TokenCode.COMMA);
        variable();
        variable_list2();
    }
    static ArrayList<TokenCode> variableTokens = new ArrayList<TokenCode>(Arrays.asList(TokenCode.LBRACKET, TokenCode.SEMICOLON));
    private void variable() {
        System.out.println("variable");
        if (expect(TokenCode.IDENTIFIER) == false) {
            synchronize(variableTokens);
        }
        variable2();
    }
    static ArrayList<TokenCode> variable2Tokens = new ArrayList<TokenCode>(Arrays.asList(TokenCode.RBRACKET, TokenCode.SEMICOLON));
    static ArrayList<TokenCode> variable2Tokens2 = new ArrayList<TokenCode>(Arrays.asList(TokenCode.SEMICOLON));
    private void variable2() {
        System.out.println("variable2");
        if (accept(TokenCode.LBRACKET)) {
            if (expect(TokenCode.NUMBER) == false) {
                synchronize(variable2Tokens);
            }
            if (expect(TokenCode.RBRACKET) == false) {
                synchronize(variable2Tokens2);
            }
            return;
        }
    }

    private void method_declarations() {
        System.out.println("method_declarations");
        method_declaration();
        more_method_declarations();
    }

    private void more_method_declarations() {
        System.out.println("more_method_declarations");
        if (nextToken.getTokenCode() != TokenCode.STATIC) {
            if (nextToken.getTokenCode() == TokenCode.RBRACE) {
                return;
            }
        }
        method_declaration();
        more_method_declarations();
    }

    static ArrayList<TokenCode> method_declarationTokens1 = new ArrayList<TokenCode>(Arrays.asList(TokenCode.INT, TokenCode.REAL, TokenCode.VOID, TokenCode.LBRACE));
    static ArrayList<TokenCode> method_declarationTokens2 = new ArrayList<TokenCode>(Arrays.asList(TokenCode.LPAREN, TokenCode.LBRACE));
    static ArrayList<TokenCode> method_declarationTokens3 = new ArrayList<TokenCode>(Arrays.asList(TokenCode.RPAREN, TokenCode.LBRACE));
    static ArrayList<TokenCode> method_declarationTokens4 = new ArrayList<TokenCode>(Arrays.asList(TokenCode.LBRACE));
    private void method_declaration() {
        System.out.println("method_declaration");
        if (expect(TokenCode.STATIC) == false) {
           synchronize(method_declarationTokens1); 
        }
        method_return_type();
        if (expect(TokenCode.IDENTIFIER) == false) {
           synchronize(method_declarationTokens2); 
        }
        if (expect(TokenCode.LPAREN) == false) {
            synchronize(method_declarationTokens3);
        } else {
            parameters();
        }
        if (expect(TokenCode.RPAREN) == false) {
            synchronize(method_declarationTokens4);
        }
        expect(TokenCode.LBRACE);
        variable_declarations();
        statement_list();
        expect(TokenCode.RBRACE);
    }

    private void method_return_type() {
        System.out.println("method_return_type");
        if (accept(TokenCode.VOID)) {
            return; 
        } else {
            type();
        }
    }

    private void parameters() {
        System.out.println("parameters");
        if (this.nextToken.getTokenCode() == TokenCode.INT ||
            this.nextToken.getTokenCode() == TokenCode.REAL) {
            parameter_list();
        }
    }

    private void parameter_list() {
        System.out.println("parameter_list");
        type();
        expect(TokenCode.IDENTIFIER);
        parameter_list2();
    }

    private void parameter_list2() {
        System.out.println("parameter_list2");
        if (accept(TokenCode.COMMA)) {
            type();
            expect(TokenCode.IDENTIFIER);
            parameter_list2();
        }
    }

    private void statement_list() {
        System.out.println("statement_list");
        if (this.nextToken.getTokenCode() == TokenCode.IDENTIFIER ||
            this.nextToken.getTokenCode() == TokenCode.IF ||
            this.nextToken.getTokenCode() == TokenCode.FOR ||
            this.nextToken.getTokenCode() == TokenCode.RETURN ||
            this.nextToken.getTokenCode() == TokenCode.BREAK ||
            this.nextToken.getTokenCode() == TokenCode.CONTINUE ||
            this.nextToken.getTokenCode() == TokenCode.LBRACE
        ) {
            statement();
            statement_list();
        }
    }

    private void statement() {
        System.out.println("statement");
        if (accept(TokenCode.IDENTIFIER)) {
            statement2();
            return;
        } else if (accept(TokenCode.IF)) {
            expect(TokenCode.LPAREN);
            expression();
            expect(TokenCode.RPAREN);
            statement_block();
            optional_else();
            return;
        } else if (accept(TokenCode.FOR)) {
            expect(TokenCode.LPAREN);
            variable_loc();
            expect(TokenCode.ASSIGNOP);
            expression();
            expect(TokenCode.SEMICOLON);
            expression();
            expect(TokenCode.SEMICOLON);
            incr_decr_var();
            expect(TokenCode.RPAREN);
            statement_block();
            return;
        } else if (accept(TokenCode.RETURN)) {
            optional_expression();
            expect(TokenCode.SEMICOLON);
            return;
        } else if (accept(TokenCode.BREAK)) {
            expect(TokenCode.SEMICOLON);
            return;
        } else if (accept(TokenCode.CONTINUE)) {
            expect(TokenCode.SEMICOLON);
            return;
        } else {
            statement_block();
            return;
        }
    }

    private void statement2() {
        System.out.println("statement2");
        if (accept(TokenCode.LPAREN)) {
            expression();
            expect(TokenCode.RPAREN);
            statement_block();
            optional_else();
            return;
        } else {
            variable_loc2();
            statement3();
            return;
        }
    }

    private void statement3() {
        System.out.println("statement3");
        if (accept(TokenCode.INCDECOP)) {
            expect(TokenCode.SEMICOLON);
            return;
        } else {
            expect(TokenCode.ASSIGNOP);
            expression();
            expect(TokenCode.SEMICOLON);
            return;
        }
    }

    private void optional_expression() {
        System.out.println("optional_expression");
        if (this.nextToken.getTokenCode() == TokenCode.IDENTIFIER ||
            this.nextToken.getTokenCode() == TokenCode.NUMBER ||
            this.nextToken.getTokenCode() == TokenCode.LPAREN ||
            this.nextToken.getTokenCode() == TokenCode.NOT
        ) {
            expression();
        }
    }

    private void statement_block() {
        System.out.println("statement_block");
        expect(TokenCode.LBRACE);
        statement_list();
        expect(TokenCode.RBRACE);
    }

    private void incr_decr_var() {
        System.out.println("incr_decr_var");
        variable_loc();
        expect(TokenCode.INCDECOP);
    }

    private void optional_else() {
        System.out.println("optional_else");
        if (accept(TokenCode.ELSE)) {
            statement_block();
        }
    }

    private void expression_list() {
        System.out.println("expression_list");
        if (this.nextToken.getTokenCode() == TokenCode.IDENTIFIER ||
            this.nextToken.getTokenCode() == TokenCode.NUMBER ||
            this.nextToken.getTokenCode() == TokenCode.LPAREN ||
            this.nextToken.getTokenCode() == TokenCode.NOT
        ) {
            expression();
            more_expressions();
        }
    }

    private void more_expressions() {
        System.out.println("more_expressions");
        if (accept(TokenCode.COMMA)) {
            expression();
            more_expressions();
        }
    }

    private void expression() {
        System.out.println("expression");
        simple_expression();
        expression2();
    }

    private void expression2() {
        System.out.println("expression2");
        if (accept(TokenCode.RELOP)) {
            simple_expression();
        }
    }

    private void simple_expression() {
        System.out.println("simple_expression");
        if (this.nextToken.getTokenCode() == TokenCode.ADDOP) {
            sign();
            term();
            simple_expression2();
        } else {
            term();
            simple_expression2();
        }
    }

    private void simple_expression2() {
        System.out.println("simple_expression2");
        if (accept(TokenCode.ADDOP)) {
            term();
            simple_expression2();
        }
    }

    private void term() {
        System.out.println("term");
        factor();
        term2();
    }

    private void term2() {
        System.out.println("term2");
        if (accept(TokenCode.MULOP)) {
            factor();
            term2();
            return;
        }
    }

    private void factor() {
        System.out.println("factor");
        if (accept(TokenCode.IDENTIFIER)) {
            factor2();
            return;
        } else if (accept(TokenCode.NUMBER)) {
            return;
        } else if (accept(TokenCode.LPAREN)) {
            expression();
            expect(TokenCode.RPAREN);
            return;
        } else if (accept(TokenCode.NOT)) {
            factor();
        } else {
            // TODO error handling
            System.out.println("error in factor");
            System.out.println(this.currentToken.getTokenCode());
            System.out.println(this.nextToken.getTokenCode());
        }
    }

    private void factor2() {
        System.out.println("factor2");
        if (accept(TokenCode.LPAREN)) {
            expression_list();
            expect(TokenCode.RPAREN);
            return;
        } else if (this.nextToken.getTokenCode() == TokenCode.LBRACKET) {
            variable_loc2();
        }
    }

    private void variable_loc() {
        System.out.println("variable_loc");
        expect(TokenCode.IDENTIFIER);
        variable_loc2();
    }


    private void variable_loc2() {
        System.out.println("variable_loc2");
        if (accept(TokenCode.LBRACKET)) {
            expression();
            /*
            if (this.error) {
                synchronize(TokenCode.RBRACKET);
            }
            */
            expect(TokenCode.RBRACKET);
        }
    }

    private void sign() {
        System.out.println("sign");
        expect(TokenCode.ADDOP);
    }

    public void parse() {
        getNext();
        if (nextToken.getTokenCode() == TokenCode.EOF) {
            return;
        }
        program();
        if (this.errorCount == 0) {
            System.out.println("SUCCESS");
        } else {
            System.out.println("there were " + this.errorCount + " errors.");
        }
        return;
    }

    public static void main(String[] args) throws IOException {
        Parser p = new Parser(args[0]);
        p.parse();
    }

}
