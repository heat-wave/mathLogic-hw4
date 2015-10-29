package logic;

public class Suc extends AbstractExpression implements Expression {
    public final Expression child;

    public Suc(Expression child) {
        this.child = child;
    }

    public boolean equals(Object other) {
        return other instanceof Suc && child.equals(((Suc) other).child);
    }

    protected Expression substImpl(Expression haystack, Expression needle) {
        return new Suc(child.substTerm(haystack, needle));
    }

    public String toString() {
        return "" + child + "'";
    }

    public String toRPNString() {
        return "'" + child;
    }

    public int hashCode() {
        return toRPNString().hashCode();
    }
}
