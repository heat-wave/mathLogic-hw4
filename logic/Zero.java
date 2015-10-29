package logic;

public class Zero extends AbstractExpression implements Expression {
    public Zero() {
    }

    public boolean equals(Object other) {
        return other instanceof Zero;
    }

    protected Expression substImpl(Expression haystack, Expression needle) {
        return this;
    }

    public String toString() {
        return "0";
    }

    public String toRPNString() {
        return "0";
    }

    public int hashCode() {
        return toRPNString().hashCode();
    }
}
