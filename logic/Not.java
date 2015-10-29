package logic;

import java.util.HashMap;

public class Not extends AbstractStatement implements Statement {
    public final Statement child;

    public Not(Statement child) {
        this.child = child;
    }

    protected boolean compareImpl(String[] patterns, Statement other) {
        return other instanceof Not && child.compareInContext(patterns, ((Not) other).child);
    }

    public Statement substPatterns(Statement[] to) {
        return new Not(child.substPatterns(to));
    }

    public Statement substTerm(Expression haystack, Expression needle) {
        return new Not(child.substTerm(haystack, needle));
    }

    public boolean evaluate(HashMap<String, Boolean> values) {
        return !child.evaluate(values);
    }

    public String toString() {
        return "!(" + child + ")";
    }

    public String toRPNString() {
        return "!" + child.toRPNString();
    }
}
