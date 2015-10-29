package logic;

import java.util.HashMap;

public class Pattern extends AbstractStatement implements Statement {

    public static final int MAXCOUNT = 10;

    public final int patternId;

    public Pattern(int patternId) {
        this.patternId = patternId;
    }

    protected boolean compareImpl(String[] patterns, Statement other) {
        return other instanceof Pattern && patternId == ((Pattern) other).patternId;
    }

    public Statement substTerm(Expression haystack, Expression needle) {
        return this;
    }

    public Statement substPatterns(Statement[] to) {
        if (to[patternId] != null) {
            return to[patternId];
        } else {
            return this;
        }
    }

    public boolean evaluate(HashMap<String, Boolean> values) {
        // undefined behavior
        return true;
    }

    public String toString() {
        return "$" + patternId;
    }

    public String toRPNString() {
        return toString();
    }
}
