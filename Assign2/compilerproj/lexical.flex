// ... put any package or import statements that you wish the generated class
//      to have, here...
package compilerproj;
%%
%class Lexer  // The class name of the generated Lexical Analyzer class
%unicode
%line
%column
%standalone
%type Token  // The class name of the class returned by the Lexical
// Analyzer class when yylex() is called
// %debug    // Uncomment to trouble shoot your definitions
%eof{
%eof}
%eofval{
    return new Token (TokenCode.EOF, OpType.NONE, DataType.NONE, yytext(), yyline, yycolumn);
%eofval}
letter_          = [a-z]|[A-Z]|_
digit            = [0-9]
id       = {letter_} ({letter_} | {digit})*
digits = {digit}+
optional_fraction = (\.{digits})?
optional_exponent = (E(\+ | -)? {digits})?
num = {digits}{optional_fraction}{optional_exponent}

incdecop = \+\+|--
addop = \+ | - | \|\|
mulop = \* | \/ | % | &&
relop = \=\= | \!\= | \> | \< | \<\= | \>\=
assignop = \=
WS = [ \n\t\r]+
special = \( | \) | \{ | \} | ; | , | \[ | \] | \!
keyword = class | static | void | if | else | for | return | break | continue | int | real
comment = \/\*[^]*\*\/ | \/\/ .* \n

%{
    private SymbolTableEntry createOrInsertSymTabEntry() {
        SymbolTableEntry symTabEntry = SymbolTable.findEntry(yytext());
        if (symTabEntry == null) {
            symTabEntry = SymbolTable.addEntry(yytext());
        }
        return symTabEntry;
    }
%}

%%
{comment} {}
{digits} {
        return new Token(TokenCode.NUMBER, OpType.NONE, DataType.INT, yytext(), yyline, yycolumn, createOrInsertSymTabEntry());
}
{num} {
        return new Token(TokenCode.NUMBER, OpType.NONE, DataType.REAL, yytext(), yyline, yycolumn, createOrInsertSymTabEntry());
}
{incdecop} {
    if (yytext().equals("++")) {
        return new Token(TokenCode.INCDECOP, OpType.INC, DataType.OP, "", yyline, yycolumn);
    } else {
        return new Token(TokenCode.INCDECOP, OpType.DEC, DataType.OP, "", yyline, yycolumn);
    }
}
{addop} {
    switch(yytext()) {
        case "+":
            return new Token(TokenCode.ADDOP, OpType.PLUS, DataType.OP, "", yyline, yycolumn);
        case "-":
            return new Token(TokenCode.ADDOP, OpType.MINUS, DataType.OP, "", yyline, yycolumn);
        case "||":
            return new Token(TokenCode.ADDOP, OpType.OR, DataType.OP, "", yyline, yycolumn);
        default:
            break;
    }
}
{mulop} {
    switch(yytext()) {
        case "*":
            return new Token(TokenCode.MULOP, OpType.MULT, DataType.OP, "", yyline, yycolumn);
        case "/":
            return new Token(TokenCode.MULOP, OpType.DIV, DataType.OP, "", yyline, yycolumn);
        case "&&":
            return new Token(TokenCode.MULOP, OpType.AND, DataType.OP, "", yyline, yycolumn);
        case "%":
            return new Token(TokenCode.MULOP, OpType.MOD, DataType.OP, "", yyline, yycolumn);
        default:
            break;
    }
}
{relop} {
    switch(yytext()) {
        case "==":
            return new Token(TokenCode.RELOP, OpType.EQUAL, DataType.OP, "", yyline, yycolumn);
        case "!=":
            return new Token(TokenCode.RELOP, OpType.NOT_EQUAL, DataType.OP, "", yyline, yycolumn);
        case ">":
            return new Token(TokenCode.RELOP, OpType.GT, DataType.OP, "", yyline, yycolumn);
        case "<":
            return new Token(TokenCode.RELOP, OpType.LT, DataType.OP, "", yyline, yycolumn);
        case ">=":
            return new Token(TokenCode.RELOP, OpType.GTE, DataType.OP, "", yyline, yycolumn);
        case "<=":
            return new Token(TokenCode.RELOP, OpType.LTE, DataType.OP, "", yyline, yycolumn);
        default:
            break;
    }
}
{assignop} {
    return new Token(TokenCode.ASSIGNOP, OpType.NONE, DataType.NONE, "", yyline, yycolumn);
}
{special} {
    switch(yytext()) {
        case "(":
            return new Token(TokenCode.LPAREN, OpType.NONE, DataType.NONE, "", yyline, yycolumn);
        case ")":
            return new Token(TokenCode.RPAREN, OpType.NONE, DataType.NONE, "", yyline, yycolumn);
        case "{":
            return new Token(TokenCode.LBRACE, OpType.NONE, DataType.NONE, "", yyline, yycolumn);
        case "}":
            return new Token(TokenCode.RBRACE, OpType.NONE, DataType.NONE, "", yyline, yycolumn);
        case "[":
            return new Token(TokenCode.LBRACKET, OpType.NONE, DataType.NONE, "", yyline, yycolumn);
        case "]":
            return new Token(TokenCode.RBRACKET, OpType.NONE, DataType.NONE, "", yyline, yycolumn);
        case ";":
            return new Token(TokenCode.SEMICOLON, OpType.NONE, DataType.NONE, "", yyline, yycolumn);
        case ",":
            return new Token(TokenCode.COMMA, OpType.NONE, DataType.NONE, "", yyline, yycolumn);
        case "!":
            return new Token(TokenCode.NOT, OpType.NONE, DataType.NONE, "", yyline, yycolumn);
        default:
            break;
    }
}
{keyword} {
    switch(yytext()) {
        case "class":
            return new Token(TokenCode.CLASS, OpType.NONE, DataType.KEYWORD, "", yyline, yycolumn);
        case "static":
            return new Token(TokenCode.STATIC, OpType.NONE, DataType.KEYWORD, "", yyline, yycolumn);
        case "void":
            return new Token(TokenCode.VOID, OpType.NONE, DataType.KEYWORD, "", yyline, yycolumn);
        case "if":
            return new Token(TokenCode.IF, OpType.NONE, DataType.KEYWORD, "", yyline, yycolumn);
        case "else":
            return new Token(TokenCode.ELSE, OpType.NONE, DataType.KEYWORD, "", yyline, yycolumn);
        case "for":
            return new Token(TokenCode.FOR, OpType.NONE, DataType.KEYWORD, "", yyline, yycolumn);
        case "return":
            return new Token(TokenCode.RETURN, OpType.NONE, DataType.KEYWORD, "", yyline, yycolumn);
        case "break":
            return new Token(TokenCode.BREAK, OpType.NONE, DataType.KEYWORD, "", yyline, yycolumn);
        case "continue":
            return new Token(TokenCode.CONTINUE, OpType.NONE, DataType.KEYWORD, "", yyline, yycolumn);
        case "int":
            return new Token(TokenCode.INT, OpType.NONE, DataType.KEYWORD, "", yyline, yycolumn);
        case "real":
            return new Token(TokenCode.REAL, OpType.NONE, DataType.KEYWORD, "", yyline, yycolumn);
    }
}
{id} {
    if (yytext().length() < 33) {
    return new Token(TokenCode.IDENTIFIER, OpType.NONE, DataType.ID, yytext(), yyline, yycolumn, createOrInsertSymTabEntry());
    } else {
        return new Token(TokenCode.ERR_LONG_ID, OpType.NONE, DataType.NONE, "", yyline, yycolumn);
    }
}
{WS} {}
[^\ ] { return new Token(TokenCode.ERR_ILL_CHAR, OpType.NONE, DataType.NONE, "", yyline, yycolumn);}
