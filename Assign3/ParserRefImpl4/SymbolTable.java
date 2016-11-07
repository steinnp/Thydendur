import java.util.*;

public class SymbolTable {
    private static Hashtable<String, Hashtable<String, SymbolTableEntry>> s_lookupList = new Hashtable<String, Hashtable<String, SymbolTableEntry>>();
    private static ArrayList<SymbolTableEntry> s_seqList = new ArrayList<SymbolTableEntry>();
    private static String currentScope = "global";
    private static Stack<String> scopes = new Stack<String>();


    public static SymbolTableEntry lookup(String lexeme) {
        for (SymbolTableEntry entry:
                s_seqList) {
            if (entry.getLexeme().equals(lexeme)) {
                return entry;
            }
        }
        return null;
    }

    public static SymbolTableEntry lookup(String lexeme, String scope) {
        SymbolTableEntry entry = s_lookupList.get(scope).get(lexeme);
        if (entry == null) {
            return s_lookupList.get("global").get(lexeme);
        } else {
            return entry;
        }
    }

    public static SymbolTableEntry insert(String lexeme) {
        SymbolTableEntry symTabEntry = new SymbolTableEntry(lexeme);
        getLookUp(currentScope).put(lexeme, symTabEntry);
        s_seqList.add(symTabEntry);
        return symTabEntry;
    }

    private static Hashtable<String, SymbolTableEntry> getLookUp(String scope) {
        Hashtable<String, SymbolTableEntry> list = s_lookupList.get(scope);
        if (list == null) {
            list = new Hashtable<String, SymbolTableEntry>();
            s_lookupList.put(scope, list);
        }
        return list;
    }
    public static int size() {
        return s_seqList.size();
    }

    public static SymbolTableEntry getEntry(int idx)
    {
        return s_seqList.get(idx);
    }

    public static void pushCurrentScope(String scope) {
        scopes.push(currentScope);
        currentScope = scope;
    }

    public static void popCurrentScope() {
        if (scopes.size() > 0) {
            currentScope = scopes.pop();
        } else {
            currentScope = "global";
        }
    }

    public static String getCurrentScope() {
        return currentScope;
    }
}