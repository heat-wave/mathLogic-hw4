package logic;

import java.util.HashMap;

public class Forall extends AbstractStatement implements Statement {
    public final Statement child;
    public final String varName;

    public Forall(String varName, Statement child) {
        this.varName = varName;
        this.child = child;
    }

    protected boolean compareImpl(String[] patterns, Statement other) {
        return other instanceof Forall && varName.equals(((Forall) other).varName) && child.compareInContext(patterns, ((Forall) other).child);
    }

    public Statement substTerm(Expression haystack, Expression needle) {
        return new Forall(varName, child.substTerm(haystack, needle));
    }

    public Statement substPatterns(Statement[] to) {
        return new Forall(varName, child.substPatterns(to));
    }

    public boolean evaluate(HashMap<String, Boolean> values) {
        // undefined in predicate calculus
        return false;
    }

    public String toString() {
        return "@" + varName + child;
    }

    public String toRPNString() {
        return "@" + varName + child.toRPNString();
    }
}
