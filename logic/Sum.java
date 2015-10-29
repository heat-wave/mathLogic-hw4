package logic;

public class Sum extends AbstractExpression implements Expression {
    public final Expression left;
    public final Expression right;

    public Sum(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    public boolean equals(Object other) {
        if (other instanceof Sum) {
            Sum sOther = (Sum) other;
            return left.equals(sOther.left) && right.equals(sOther.right);
        } else {
            return false;
        }
    }

    protected Expression substImpl(Expression haystack, Expression needle) {
        return new Sum(left.substTerm(haystack, needle), right.substTerm(haystack, needle));
    }

    public String toString() {
        return "(" + left + " + " + right + ")";
    }

    public String toRPNString() {
        return "+" + left + right;
    }

    public int hashCode() {
        return toRPNString().hashCode();
    }
}
