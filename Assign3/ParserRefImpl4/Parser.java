import java.io.*;
import java.util.*;

public class Parser {

    private Lexer m_lexer;
    private Token m_current;
    private Token m_prev;
    private CodeGenerator m_generator;
    private int nextTemp;
    private int nextLabel;
    private TokenCode currentType;
    private ArrayList<SymbolTableEntry> fparams;
    private Stack<SymbolTableEntry> continueStack;
    private Stack<SymbolTableEntry> breakStack;

    private ErrorHandler m_errorHandler;

    public Parser(Lexer lexer, String sourceFile, CodeGenerator _generator) {
        m_errorHandler = new ErrorHandler(lexer, sourceFile);
        continueStack = new Stack<>();
        breakStack = new Stack<>();
        m_lexer = lexer;
        m_generator = _generator;
        nextTemp = 1;
        nextLabel = 0;
        SymbolTable.insert("0");
        SymbolTable.insert("1");
        SymbolTable.insert("t0");
        m_generator.generate(TacCode.VAR, null, null, SymbolTable.lookup("t0"));
        readNextToken();
    }

    private SymbolTableEntry newTemp() {
        String tempNo = new Integer(nextTemp++).toString();
        String tempName = "t" + tempNo;
        SymbolTableEntry entry = SymbolTable.insert(tempName);
        m_generator.generate(TacCode.VAR, null, null, entry);
        return entry;
    }

    private SymbolTableEntry newLabel() {
        String labelNo = new Integer(nextLabel++).toString();
        String labelName = "l" + labelNo;
        SymbolTableEntry entry = SymbolTable.insert(labelName);
        return entry;
    }

    /*
      Reads the next token.
      If the compiler is in error recovery we do not actually read a new token, we just pretend we do. We will get match failures which the ErrorHandler will supress. When we leave the procedure with the offending non-terminal, the ErrorHandler will go out of recovery mode and start reading tokens again.
    */
    protected void readNextToken() {
        try {
            // If the Error handler is in recovery mode, we don't read new tokens!
            // We simply use current tokens until the Error handler exits the recovery mode
            if (!m_errorHandler.inRecovery()) {
                m_prev = m_current;
                m_current = m_lexer.yylex();
                trace("++ Next token read: " + m_current.getTokenCode());
                if (MyMain.TRACE)
                    if (m_prev != null && m_prev.getLineNum() != m_current.getLineNum())
                        System.out.println("Line " + m_current.getLineNum());
            }
            else
                trace("++ Next token skipped because of recovery: Still: " + m_current.getTokenCode());
            // System.out.println(m_current.getTokenCode() + String.valueOf(m_current.getLineNum()) + ", col: " + String.valueOf(m_current.getColumnNum()));
        }
        catch(IOException e) {
            System.out.println("IOException reading next token");
            System.exit(1);
        }
    }

    /* Returns the next token of the input, without actually reading it */
    protected Token lookahead() {
        return m_current;
    }

    /* Returns true if the lookahead token has the given tokencode */
    protected boolean lookaheadIs(TokenCode tokenCode) {
        return m_current.getTokenCode() == tokenCode;
    }

    /* Returns true if the lookahed token is included in the given array of token codes */
    protected boolean lookaheadIn(TokenCode[] tokenCodes) {
        for(int n=0;n<tokenCodes.length;n++)
            if (tokenCodes[n] == m_current.getTokenCode())
                return true;
        return false;
    }

    /* Returns true if the lookahed token is in the FIRST of EXPRESSION.
       Need to specially check if the token is ADDOP to make sure the token is +/-
       (by checking the OpType of the token)
     */
    protected boolean lookaheadIsFirstOfExpression() {
        if (!lookaheadIn(NonT.firstOf(NonT.EXPRESSION)))
            return false;
        if (lookaheadIs(TokenCode.ADDOP) && lookahead().getOpType() != OpType.PLUS && lookahead().getOpType() != OpType.MINUS)
            return false;
        else
            return true;
    }

    /*
    Return true if the lookahed is the first of sign (actually if the lexeme for the token was '+' or '-')
    */
    protected boolean lookaheadIsFirstOfSign() {
        return (lookaheadIs(TokenCode.ADDOP) && (lookahead().getOpType() == OpType.PLUS || lookahead().getOpType() == OpType.MINUS));
    }

    /*
    Match the the token and read next token if match is successful.

    If the match is unsuccessfull we let the ErrorHandler report the error and supply us with the next token to use. This next token will then not be used until we leave the parsing method where the mismatch occured.

    If the ErrorHandler is in the recovery state, it will suppress the error (not report it).
    */
    protected void match(TokenCode tokenCode) {
        if (m_current.getTokenCode() != tokenCode)
        {
            Token[] tokens = m_errorHandler.tokenMismatch(tokenCode, m_current, m_prev);
            m_current = tokens[0];
            m_prev = tokens[1];
            trace("  failed match for " + tokenCode + ". current: " + m_current.getTokenCode() + ", prev: " + m_prev.getTokenCode());
        }
        else {
            trace("  Matched " + tokenCode);
            readNextToken();
        }
    }

    /*
    Called when none the next token is none of the possible tokens for some given part of the non-terminal.
    Behaviour is the same as match except that we have no specific token to match against.
    */
    protected void noMatch() {
        Token[] tokens = m_errorHandler.noMatch(m_current, m_prev);
        m_current = tokens[0];
        m_prev = tokens[1];
    }


    // *** Start of nonTerminal functions ***

    // DONE
    protected void program() {
        m_errorHandler.startNonT(NonT.PROGRAM);
        match(TokenCode.CLASS);
        match(TokenCode.IDENTIFIER);
        match(TokenCode.LBRACE);
        variableDeclarations();
        if (m_errorHandler.errorFree()) {
            m_generator.generate(TacCode.GOTO, null, null, new SymbolTableEntry("main"));
        }
        methodDeclarations();
        match(TokenCode.RBRACE);
        m_errorHandler.stopNonT();
        if (m_errorHandler.errorFree()) {
            m_generator.print();
        }
    }

    // DONE
    protected void variableDeclarations() {
        m_errorHandler.startNonT(NonT.VARIABLE_DECLARATIONS);
        if (lookaheadIn(NonT.firstOf(NonT.TYPE))) {
            type();
            variableList();
            match(TokenCode.SEMICOLON);
            variableDeclarations();
        }
        m_errorHandler.stopNonT();
    }

    // DONE
    protected void type() {
        m_errorHandler.startNonT(NonT.TYPE);
        if (lookaheadIs(TokenCode.INT)) {
            match(TokenCode.INT);
            currentType = TokenCode.INT;
            if (m_errorHandler.errorFree()) {
                m_current.getSymTabEntry().setTokenType(TokenCode.INT);
            }
        } else if (lookaheadIs(TokenCode.REAL)) {
            match(TokenCode.REAL);
            currentType = TokenCode.REAL;
            if (m_errorHandler.errorFree()) {
                m_current.getSymTabEntry().setTokenType(TokenCode.REAL);
            }
        } else { // TODO: Add error context, i.e. type
            noMatch();
        }
        m_errorHandler.stopNonT();
    }

    // DONE
    protected void variableList() {
        m_errorHandler.startNonT(NonT.VARIABLE_LIST);
        variable();
        variableList2();
        m_errorHandler.stopNonT();
    }

    // DONE
    protected void variableList2() {
        m_errorHandler.startNonT(NonT.VARIABLE_LIST_2);
        if (lookaheadIs(TokenCode.COMMA)) {
            match(TokenCode.COMMA);
            variable();
            variableList2();
        }
        m_errorHandler.stopNonT();
    }

    // DONE
    protected void variable() {
        m_errorHandler.startNonT(NonT.VARIABLE);
        match(TokenCode.IDENTIFIER);
        if (m_errorHandler.errorFree()) {
            SymbolTableEntry entry = m_prev.getSymTabEntry();
            entry.setTokenType(currentType);
            m_generator.generate(TacCode.VAR, null, null, entry);
        }
        if (lookaheadIs(TokenCode.LBRACKET)) {
            match(TokenCode.LBRACKET);
            match(TokenCode.NUMBER);
            match(TokenCode.RBRACKET);
        }
        m_errorHandler.stopNonT();
    }

    // DONE
    protected void methodDeclarations() {
        m_errorHandler.startNonT(NonT.METHOD_DECLARATIONS);
        methodDeclaration();
        moreMethodDeclarations();
        m_errorHandler.stopNonT();
    }

    // DONE
    protected void moreMethodDeclarations() {
        m_errorHandler.startNonT(NonT.MORE_METHOD_DECLARATIONS);
        if (lookaheadIn(NonT.firstOf(NonT.METHOD_DECLARATION))) {
            methodDeclaration();
            moreMethodDeclarations();
        }
        m_errorHandler.stopNonT();
    }

    // DONE
    protected void methodDeclaration() {
        m_errorHandler.startNonT(NonT.METHOD_DECLARATION);
        match(TokenCode.STATIC);
        methodReturnType();
        match(TokenCode.IDENTIFIER);
        if (m_errorHandler.errorFree()) {
            SymbolTableEntry label = m_prev.getSymTabEntry();
            m_generator.generate(TacCode.LABEL, null, null, label);
            SymbolTable.pushCurrentScope(label.getLexeme());
        }
        match(TokenCode.LPAREN);
        if (m_errorHandler.errorFree()) {
            fparams = new ArrayList<>();
        }
        parameters();
        if (m_errorHandler.errorFree()) {
            m_generator.generateFParams(fparams);
        }
        match(TokenCode.RPAREN);
        match(TokenCode.LBRACE);
        variableDeclarations();
        statementList();
        match(TokenCode.RBRACE);
        if (m_errorHandler.errorFree()) {
            m_generator.generate(TacCode.RETURN, null, null, null);
        }
        m_errorHandler.stopNonT();
        SymbolTable.popCurrentScope();
    }

    // TODO
    protected void methodReturnType() {
        m_errorHandler.startNonT(NonT.METHOD_RETURN_TYPE);
        if (lookaheadIs(TokenCode.VOID))
            match(TokenCode.VOID);
        if (m_errorHandler.errorFree()) {
            m_current.getSymTabEntry().setTokenType(TokenCode.VOID);
        }
        else
            type();
        m_errorHandler.stopNonT();
    }

    // DONE
    protected void parameters() {
        m_errorHandler.startNonT(NonT.PARAMETERS);
        if (lookaheadIn(NonT.firstOf(NonT.PARAMETER_LIST))) {
            parameterList();
        }
        m_errorHandler.stopNonT();
    }

    // DONE
    protected void parameterList() {
        m_errorHandler.startNonT(NonT.PARAMETER_LIST);
        type();
        match(TokenCode.IDENTIFIER);
        if (m_errorHandler.errorFree()) {
            fparams.add(m_prev.getSymTabEntry());
        }
        parameterList2();
        m_errorHandler.stopNonT();
    }

    // DONE
    protected void parameterList2() {
        m_errorHandler.startNonT(NonT.PARAMETER_LIST2);
        if (lookaheadIs(TokenCode.COMMA) && !m_errorHandler.inRecovery()) {
            match(TokenCode.COMMA);
            type();
            match(TokenCode.IDENTIFIER);
            if (m_errorHandler.errorFree()) {
                fparams.add(m_prev.getSymTabEntry());
            }
            parameterList2();
        }
        m_errorHandler.stopNonT();
    }

    // DONE
    protected void statementList() {
        m_errorHandler.startNonT(NonT.STATEMENT_LIST);
        if (lookaheadIn(NonT.firstOf(NonT.STATEMENT))  && !m_errorHandler.inRecovery()) {
            statement();
            statementList();
        }
        m_errorHandler.stopNonT();
    }

    // DONE
    protected void idStartingStatement() {
        m_errorHandler.startNonT(NonT.ID_STARTING_STATEMENT);
        match(TokenCode.IDENTIFIER);
        System.out.println(SymbolTable.lookup(m_prev.getSymTabEntry().getLexeme()).getTokenType());
        if (SymbolTable.lookup(m_prev.getSymTabEntry().getLexeme()).getTokenType() == null
                && !m_prev.getSymTabEntry().getLexeme().equals("writeln")
                && !m_prev.getSymTabEntry().getLexeme().equals("write")) {
            m_errorHandler.raiseError();
            m_errorHandler.printErrorInfo("Error: function has not been declared", m_prev);
        }
        restOfIdStartingStatement();
        match(TokenCode.SEMICOLON);
        m_errorHandler.stopNonT();
    }

    // DONE
    protected void restOfIdStartingStatement() {
        m_errorHandler.startNonT(NonT.REST_OF_ID_STARTING_STATEMENT);
        SymbolTableEntry id = new SymbolTableEntry("");
        Token id_token = m_prev;
        if (m_errorHandler.errorFree()) {
            id = m_prev.getSymTabEntry();
        }
        if (lookaheadIs(TokenCode.LPAREN)) {
            match(TokenCode.LPAREN);
            if (m_errorHandler.errorFree()) {
                ArrayList<SymbolTableEntry> aparams = expressionList();
                m_generator.generateAParams(aparams);
                m_generator.generate(TacCode.CALL, null, null, id);
            }
            match(TokenCode.RPAREN);
        }
        else if (lookaheadIs(TokenCode.INCDECOP)) {
            match(TokenCode.INCDECOP);
            TacCode incDecOp = TacCode.ADD;
            if (m_errorHandler.errorFree()) {
                if (m_current.getOpType() == OpType.DEC) {
                    incDecOp = TacCode.SUB;
                }
                m_generator.generate(incDecOp, id, SymbolTable.lookup("1"), id);
            }
        }
        else if (lookaheadIs(TokenCode.ASSIGNOP)) {
            match(TokenCode.ASSIGNOP);
            if (id_token.getSymTabEntry().getTokenType() == null) {
                m_errorHandler.raiseError();
                m_errorHandler.printErrorInfo("Error: undeclared variable", id_token);
            }
            SymbolTableEntry entry = expression();
            if (m_errorHandler.errorFree()) {
                m_generator.generate(TacCode.ASSIGN, entry, null, id);
            }
        }
        else if (lookaheadIs(TokenCode.LBRACKET)) {
            match(TokenCode.LBRACKET);
            expression();
            match(TokenCode.RBRACKET);
            match(TokenCode.ASSIGNOP);
            expression();
        }
        else // TODO: Add error context, i.e. idStartingStatement
            noMatch();
        m_errorHandler.stopNonT();
    }

    // DONE
    protected void statement() {
        boolean noMatch = false;
        m_errorHandler.startNonT(NonT.STATEMENT);
        if (lookaheadIs(TokenCode.IDENTIFIER))
        {
            trace("idStartingStmt");
            idStartingStatement();
        }
        else if (lookaheadIs(TokenCode.IF)) {
            trace("if");
            match(TokenCode.IF);
            match(TokenCode.LPAREN);
            SymbolTableEntry eval = expression();
            SymbolTableEntry notDoneLabel = newLabel();
            SymbolTableEntry doneLabel = newLabel();
            if (m_errorHandler.errorFree()) {
                m_generator.generate(TacCode.NE, eval, SymbolTable.lookup("0"), notDoneLabel);
                m_generator.generate(TacCode.GOTO, null, null, doneLabel);
                m_generator.generate(TacCode.LABEL, null, null, notDoneLabel);
            }
            match(TokenCode.RPAREN);
            statementBlock();
            SymbolTableEntry ifDone = newLabel();
            if (m_errorHandler.errorFree()) {
                m_generator.generate(TacCode.GOTO, null, null, ifDone);
                m_generator.generate(TacCode.LABEL, null, null, doneLabel);
            }
            optionalElse();
            if (m_errorHandler.errorFree()) {
                m_generator.generate(TacCode.LABEL, null, null, ifDone);
            }
        }
        else if (lookaheadIs(TokenCode.FOR)) {
            trace("for");

            SymbolTableEntry cont = newLabel();
            SymbolTableEntry doneLabel = newLabel();

            if (m_errorHandler.errorFree()) {
                continueStack.push(cont);
                breakStack.push(doneLabel);
            }

            match(TokenCode.FOR);
            match(TokenCode.LPAREN);
            SymbolTableEntry var1 = variableLoc();
            match(TokenCode.ASSIGNOP);
            SymbolTableEntry result = expression();
            if (m_errorHandler.errorFree()) {
                m_generator.generate(TacCode.ASSIGN, result, null, var1);
            }
            match(TokenCode.SEMICOLON);

            SymbolTableEntry notDoneLabel = newLabel();
            if (m_errorHandler.errorFree()) {
                m_generator.generate(TacCode.LABEL, null, null, notDoneLabel);
            }
            SymbolTableEntry var2 = expression();
            if (m_errorHandler.errorFree()) {
                m_generator.generate(TacCode.EQ, SymbolTable.lookup("0"), var2, doneLabel);
            }
            match(TokenCode.SEMICOLON);
            SymbolTableEntry var3 = variableLoc();
            match(TokenCode.INCDECOP);
            TacCode incDecOp = TacCode.ADD;
            if (m_errorHandler.errorFree()) {
                if (m_prev.getOpType() == OpType.DEC) {
                    incDecOp = TacCode.SUB;
                }
            }
            match(TokenCode.RPAREN);
            statementBlock();
            if (m_errorHandler.errorFree()) {
                m_generator.generate(TacCode.LABEL, null, null, cont);
                m_generator.generate(incDecOp, var3, SymbolTable.lookup("1"), var3);
                m_generator.generate(TacCode.GOTO, null, null, notDoneLabel);
                m_generator.generate(TacCode.LABEL, null, null, doneLabel);
                continueStack.pop();
                breakStack.pop();
            }
        }
        else if (lookaheadIs(TokenCode.RETURN)) {
            trace("return");
            match(TokenCode.RETURN);
            SymbolTableEntry expr = optionalExpression();
            if (expr != null) {
                if (m_errorHandler.errorFree()) {
                    if (expr.getTokenType() != SymbolTable.lookup(SymbolTable.getCurrentScope()).getTokenType()) {
                        m_errorHandler.raiseError();
                        m_errorHandler.printErrorInfo("Error: Incompatible return type", m_prev);
                    }
                    m_generator.generate(TacCode.ASSIGN, expr, null, new SymbolTableEntry(SymbolTable.getCurrentScope()));
                }
            }
            if (m_errorHandler.errorFree()) {
                m_generator.generate(TacCode.RETURN, null, null, null);
            }
            match(TokenCode.SEMICOLON);
        }
        else if (lookaheadIs(TokenCode.BREAK)) {
            trace("break");
            match(TokenCode.BREAK);
            if (m_errorHandler.errorFree()) {
                m_generator.generate(TacCode.GOTO, null, null, breakStack.peek());
            }
            match(TokenCode.SEMICOLON);
        }
        else if (lookaheadIs(TokenCode.CONTINUE)) {
            trace("continue");
            match(TokenCode.CONTINUE);
            if (m_errorHandler.errorFree()) {
                m_generator.generate(TacCode.GOTO, null, null, continueStack.peek());
            }
            match(TokenCode.SEMICOLON);
        }
        else if (lookaheadIs(TokenCode.LBRACE)) {
            trace("block");
            statementBlock();
        }
        else {// TODO: Add error context, i.e. statement
            trace("noMatch");
            noMatch = true;
            m_errorHandler.stopNonT();
            noMatch();
        }
        if (!noMatch)
            m_errorHandler.stopNonT();
    }

    // TODO
    protected SymbolTableEntry optionalExpression() {
        m_errorHandler.startNonT(NonT.OPTIONAL_EXPRESSION);
        if (lookaheadIsFirstOfExpression()) {
            SymbolTableEntry expr =  expression();
            m_errorHandler.stopNonT();
            return expr;
        }
        m_errorHandler.stopNonT();
        return null;
    }

    // DONE
    protected void statementBlock() {
        m_errorHandler.startNonT(NonT.STATEMENT_BLOCK);
        match(TokenCode.LBRACE);
        statementList();
        match(TokenCode.RBRACE);
        m_errorHandler.stopNonT();
    }

    // DONE
    protected void optionalElse() {
        m_errorHandler.startNonT(NonT.OPTIONAL_ELSE);
        if (lookaheadIs(TokenCode.ELSE)) {
            match(TokenCode.ELSE);
            statementBlock();
        }
        m_errorHandler.stopNonT();
    }

    // DONE
    protected ArrayList<SymbolTableEntry> expressionList() {
        m_errorHandler.startNonT(NonT.EXPRESSION_LIST);
        if (lookaheadIsFirstOfExpression()) {
            SymbolTableEntry expression = expression();
            ArrayList<SymbolTableEntry> expressions = moreExpressions();
            if (m_errorHandler.errorFree()) {
                expressions.add(0, expression);
            }
            m_errorHandler.stopNonT();
            return expressions;
        } else {
            m_errorHandler.stopNonT();
            return new ArrayList<SymbolTableEntry>();
        }
    }

    // DONE
    protected ArrayList<SymbolTableEntry> moreExpressions() {
        m_errorHandler.startNonT(NonT.MORE_EXPRESSIONS);
        if (lookaheadIs(TokenCode.COMMA) && !m_errorHandler.inRecovery()) {
            match(TokenCode.COMMA);
            SymbolTableEntry expression = expression();
            ArrayList<SymbolTableEntry> expressions = moreExpressions();
            if (m_errorHandler.errorFree()) {
                expressions.add(0, expression);
            }
            m_errorHandler.stopNonT();
            return expressions;
        } else {
            m_errorHandler.stopNonT();
            return new ArrayList<SymbolTableEntry>();
        }
    }

    // DONE
    protected SymbolTableEntry expression() {
        m_errorHandler.startNonT(NonT.EXPRESSION);
        SymbolTableEntry entry1 = simpleExpression();
        if (lookaheadIs(TokenCode.RELOP)) {
            TacCode code = TacCode.NOOP;
            OpType lexeme = OpType.NONE;
            Token op = m_current;
            if (m_errorHandler.errorFree()) {
                lexeme = m_current.getOpType();
            }
            SymbolTableEntry entry2 = expression2();
            SymbolTableEntry tempLabel = newLabel();
            SymbolTableEntry cont = newLabel();
            SymbolTableEntry result = newTemp();
            if (m_errorHandler.errorFree()) {
                switch (lexeme) {
                    case LT:
                        code = TacCode.LT;
                        break;
                    case GT:
                        code = TacCode.GT;
                        break;
                    case LTE:
                        code = TacCode.LE;
                        break;
                    case GTE:
                        code = TacCode.GE;
                        break;
                    case EQUAL:
                        code = TacCode.EQ;
                        break;
                    case NOT_EQUAL:
                        code = TacCode.NE;
                        break;
                }
                if (entry2.getTokenType() != entry1.getTokenType()) {
                    m_errorHandler.raiseError();
                    m_errorHandler.printErrorInfo("Error: incompatible types", op);
                } else {
                    m_generator.generate(code, entry1, entry2, tempLabel);
                    m_generator.generate(TacCode.ASSIGN, SymbolTable.lookup("0"), null, result);
                    m_generator.generate(TacCode.GOTO, null, null, cont);
                    m_generator.generate(TacCode.LABEL, null, null, tempLabel);
                    m_generator.generate(TacCode.ASSIGN, SymbolTable.lookup("1"), null, result);
                    m_generator.generate(TacCode.LABEL, null, null, cont);
                }
            }
            m_errorHandler.stopNonT();
            return result;
        } else {
            m_errorHandler.stopNonT();
            return entry1;
        }
    }

    // DONE
    protected SymbolTableEntry expression2() {
        m_errorHandler.startNonT(NonT.EXPRESSION2);
            match(TokenCode.RELOP);
            SymbolTableEntry entry = simpleExpression();
        m_errorHandler.stopNonT();
        return entry;
    }

    // DONE
    protected SymbolTableEntry simpleExpression() {
        m_errorHandler.startNonT(NonT.SIMPLE_EXPRESSION);
        SymbolTableEntry entry1;
        if (lookaheadIn(NonT.firstOf(NonT.SIGN))) {
            TacCode code = sign();
            if (code == TacCode.UMINUS) {
                SymbolTableEntry param1 = term();
                SymbolTableEntry result = newTemp();
                if (m_errorHandler.errorFree()) {
                    m_generator.generate(code, param1, null, result);
                }
                entry1 = result;
            } else {
                entry1 = term();
            }
        } else {
            entry1 = term();
        }
        if (lookaheadIs(TokenCode.ADDOP)) {
            SymbolTableEntry result = newTemp();
            if (m_errorHandler.errorFree()) {
                TacCode addOp = TacCode.ADD;
                if (m_current.getOpType() == OpType.MINUS) {
                    addOp = TacCode.SUB;
                }
                SymbolTableEntry entry2 = simpleExpression2();
                m_generator.generate(addOp, entry1, entry2, result);
            }
            m_errorHandler.stopNonT();
            return result;
        } else {
            m_errorHandler.stopNonT();
            return entry1;
        }
    }

    // DONE
    protected SymbolTableEntry simpleExpression2() {
        m_errorHandler.startNonT(NonT.SIMPLE_EXPRESSION2);
        match(TokenCode.ADDOP);
        SymbolTableEntry entry1 = term();
        if (lookaheadIs(TokenCode.ADDOP)) {
            TacCode addOp = TacCode.ADD;
            Token op = m_current;
            OpType symbol = m_current.getOpType();
            if (symbol == OpType.MINUS) {
                addOp = TacCode.SUB;
            } else if (symbol == OpType.OR) {
                addOp = TacCode.OR;
            }
            SymbolTableEntry entry2 = simpleExpression2();
            SymbolTableEntry result = newTemp();
            if (m_errorHandler.errorFree()) {
                if (entry2.getTokenType() != entry1.getTokenType()) {
                    m_errorHandler.raiseError();
                    m_errorHandler.printErrorInfo("Error: incompatible types", op);
                } else {
                    result.setTokenType(entry2.getTokenType());
                    m_generator.generate(addOp, entry1, entry2, result);
                }
            }
            m_errorHandler.stopNonT();
            return result;
        } else {
            m_errorHandler.stopNonT();
            return entry1;
        }
    }

    // DONE
    protected SymbolTableEntry term() {
        m_errorHandler.startNonT(NonT.TERM);
        SymbolTableEntry entry1 = factor();
        if (lookaheadIs(TokenCode.MULOP)) {
            TacCode mulOp = TacCode.MULT;
            Token op = m_current;
            OpType symbol = m_current.getOpType();
            if (symbol == OpType.DIV) {
                mulOp = TacCode.DIV;
            } else if (symbol == OpType.MOD) {
                mulOp = TacCode.MOD;
            } else if (symbol == OpType.AND) {
                mulOp = TacCode.AND;
            }
            SymbolTableEntry entry2 = term2();
            SymbolTableEntry result = newTemp();
            if (entry2.getTokenType() != entry1.getTokenType()) {
                m_errorHandler.raiseError();
                m_errorHandler.printErrorInfo("Error: incompatible types", op);
            } else {
                result.setTokenType(entry2.getTokenType());
                if (m_errorHandler.errorFree()) {
                    m_generator.generate(mulOp, entry1, entry2, result);
                }
            }
            m_errorHandler.stopNonT();
            return result;
        } else {
            m_errorHandler.stopNonT();
            return entry1;
        }
    }

    // DONE
    protected SymbolTableEntry term2() {
        m_errorHandler.startNonT(NonT.TERM2);
        match(TokenCode.MULOP);
        SymbolTableEntry entry1 = factor();
        if (lookaheadIs(TokenCode.MULOP)) {
            TacCode mulOp = TacCode.MULT;
            Token op = m_current;
            if (m_errorHandler.errorFree()) {
                OpType symbol = m_current.getOpType();
                if (symbol == OpType.DIV) {
                    mulOp = TacCode.DIV;
                } else if (symbol == OpType.MOD) {
                    mulOp = TacCode.MOD;
                } else if (symbol == OpType.AND) {
                    mulOp = TacCode.AND;
                }
            }
            SymbolTableEntry entry2 = term2();
            SymbolTableEntry result = newTemp();
            if (entry2.getTokenType() != entry1.getTokenType()) {
                m_errorHandler.raiseError();
                m_errorHandler.printErrorInfo("Error: incompatible types", op);
            } else {
                result.setTokenType(entry2.getTokenType());
                if (m_errorHandler.errorFree()) {
                    m_generator.generate(mulOp, entry1, entry2, result);
                }
            }
            m_errorHandler.stopNonT();
            return result;
        }
        else {
            m_errorHandler.stopNonT();
            return entry1;
        }
    }

    // DONE
    protected SymbolTableEntry idStartingFactor() {
        m_errorHandler.startNonT(NonT.ID_STARTING_FACTOR);
        match(TokenCode.IDENTIFIER);
        SymbolTableEntry id = new SymbolTableEntry("");
        if (m_errorHandler.errorFree()) {
            id = m_prev.getSymTabEntry();
        }
        if (lookaheadIs(TokenCode.LPAREN)) {
            ArrayList<SymbolTableEntry> aparams = restOfIdStartingFactor();
            if (m_errorHandler.errorFree()) {
                m_generator.generateAParams(aparams);
                m_generator.generate(TacCode.CALL, null, null, id);
            }
        }
        m_errorHandler.stopNonT();
        return id;
    }

    // DONE
    protected ArrayList<SymbolTableEntry> restOfIdStartingFactor() {
        m_errorHandler.startNonT(NonT.REST_OF_ID_STARTING_FACTOR);
        ArrayList<SymbolTableEntry> aparams = new ArrayList<>();
        if (lookaheadIs(TokenCode.LPAREN)) {
            match(TokenCode.LPAREN);
            if (m_errorHandler.errorFree()) {
                aparams = expressionList();
            }
            match(TokenCode.RPAREN);
        }
        else if (lookaheadIs(TokenCode.LBRACKET)) {
            match(TokenCode.LBRACKET);
            expression();
            match(TokenCode.RBRACKET);
        }
        m_errorHandler.stopNonT();
        return aparams;
    }

    // DONE
    protected SymbolTableEntry factor() {
        SymbolTableEntry entry = new SymbolTableEntry("");
        m_errorHandler.startNonT(NonT.FACTOR);
        if (lookaheadIs(TokenCode.IDENTIFIER)) {
            entry = idStartingFactor();
        } else if (lookaheadIs(TokenCode.NUMBER)) {
            match(TokenCode.NUMBER);
            if (m_errorHandler.errorFree()) {
                entry = m_prev.getSymTabEntry();
            }
        } else if (lookaheadIs(TokenCode.LPAREN)) {
            match(TokenCode.LPAREN);
            entry = expression();
            match(TokenCode.RPAREN);
        }
        else if (lookaheadIs(TokenCode.NOT)) {
            match(TokenCode.NOT);
            SymbolTableEntry tempEntry = factor();
            entry = newTemp();
            if (m_errorHandler.errorFree()) {
                m_generator.generate(TacCode.NOT, tempEntry, null, entry);
            }

        }
        else {// TODO: Add error context, i.e. factor
            noMatch();
            entry = new SymbolTableEntry("error");
        }
        m_errorHandler.stopNonT();
        return entry;
    }

    protected SymbolTableEntry variableLoc() {
        m_errorHandler.startNonT(NonT.VARIABLE_LOC);
        match(TokenCode.IDENTIFIER);
        SymbolTableEntry entry = new SymbolTableEntry("");
        if (m_errorHandler.errorFree()) {
            entry = m_prev.getSymTabEntry();
            if (entry.getTokenType() == null) {
                m_errorHandler.raiseError();
                m_errorHandler.printErrorInfo("Error: undeclared variable", m_prev);
            }
        }
        variableLocRest();
        m_errorHandler.stopNonT();
        return entry;
    }

    protected void variableLocRest() {
        m_errorHandler.startNonT(NonT.VARIABLE_LOC_REST);
        if (lookaheadIs(TokenCode.LBRACKET)) {
            match(TokenCode.LBRACKET);
            expression();
            match(TokenCode.RBRACKET);
        }
        m_errorHandler.stopNonT();
    }

    protected TacCode sign() {
        m_errorHandler.startNonT(NonT.SIGN);
        TacCode code = TacCode.NOOP;
        if (lookaheadIsFirstOfSign()) {
            match(TokenCode.ADDOP);
            if (m_prev.getOpType() == OpType.MINUS) {
                code =  TacCode.UMINUS;
            }
        } else // TODO: Add error context, i.e. sign
            noMatch();
        m_errorHandler.stopNonT();
        return code;
    }

    protected void trace(String msg) {
        if (MyMain.TRACE)
            System.out.println(msg);
    }
}