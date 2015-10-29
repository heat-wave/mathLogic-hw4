package logic;

import java.util.HashMap;

public class Equals extends AbstractStatement implements Statement {
    public final Expression left;
    public final Expression right;

    public Equals(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    protected boolean compareImpl(String[] patterns, Statement other) {
        if (other instanceof Equals) {
            Equals eOther = (Equals) other;
            return left.equals(eOther.left) && right.equals(eOther.right);
        } else {
            return false;
        }
    }

    public Statement substTerm(Expression haystack, Expression needle) {
        return new Equals(left.substTerm(haystack, needle), right.substTerm(haystack, needle));
    }

    public Statement substPatterns(Statement[] to) {
        return this;
    }

    public boolean evaluate(HashMap<String, Boolean> values) {
        // undefined for equals
        return false;
    }

    public String toString() {
        return "(" + left + " = " + right + ")";
    }

    public String toRPNString() {
        return "=" + left + right;
    }
}
