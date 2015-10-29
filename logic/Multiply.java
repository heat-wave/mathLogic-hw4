package logic;

public class Multiply extends AbstractExpression implements Expression {
    public final Expression left;
    public final Expression right;

    public Multiply(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    public boolean equals(Object other) {
        if (other instanceof Multiply) {
            Multiply pOther = (Multiply) other;
            return left.equals(pOther.left) && right.equals(pOther.right);
        } else {
            return false;
        }
    }

    protected Expression substImpl(Expression haystack, Expression needle) {
        return new Multiply(left.substTerm(haystack, needle), right.substTerm(haystack, needle));
    }

    public String toString() {
        return "(" + left + " * " + right + ")";
    }

    public String toRPNString() {
        return "*" + left + right;
    }

    public int hashCode() {
        return toRPNString().hashCode();
    }
}
