package logic;

import java.util.HashMap;

public class Exists extends AbstractStatement implements Statement {
    public final Statement child;
    public final String varName;

    public Exists(String varName, Statement child) {
        this.varName = varName;
        this.child = child;
    }

    protected boolean compareImpl(String[] patterns, Statement other) {
        if (other instanceof Exists && varName.equals(((Exists) other).varName)) {
            return child.compareInContext(patterns, ((Exists) other).child);
        } else {
            return false;
        }
    }

    public Statement substTerm(Expression haystack, Expression needle) {
        return new Exists(varName, child.substTerm(haystack, needle));
    }

    public Statement substPatterns(Statement[] to) {
        return new Exists(varName, child.substPatterns(to));
    }

    public boolean evaluate(HashMap<String, Boolean> values) {
        // undefined in predicate calculus
        return false;
    }

    public String toString() {
        return "?" + varName + child;
    }

    public String toRPNString() {
        return "?" + varName + child.toRPNString();
    }
}
