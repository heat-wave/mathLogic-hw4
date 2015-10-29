package logic;

import java.util.HashMap;

public class Variable extends AbstractStatement implements Statement {
    public final String name;

    public Variable(String name) {
        this.name = name;
    }

    protected boolean compareImpl(String[] patterns, Statement other) {
        return other instanceof Variable && name.equals(((Variable) other).name);
    }

    public Statement substTerm(Expression haystack, Expression needle) {
        return this;
    }

    public Statement substPatterns(Statement[] to) {
        return this;
    }

    public boolean evaluate(HashMap<String, Boolean> values) {
        // invariant: variable is present in values map
        return values.get(name);
    }

    public String toString() {
        return name;
    }

    public String toRPNString() {
        return name;
    }
}
