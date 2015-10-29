package logic;

public class ArithmeticPattern extends AbstractExpression implements Expression {
    public int patternId;

    public ArithmeticPattern(int patternId) {
        this.patternId = patternId;
    }

    public boolean equals(Object other) {
        return other instanceof ArithmeticPattern && patternId == ((ArithmeticPattern) other).patternId;
    }

    protected Expression substImpl(Expression haystack, Expression needle) {
        return this;
    }

    public String toString() {
        return "#" + patternId;
    }

    public String toRPNString() {
        return toString();
    }

    public int hashCode() {
        return toRPNString().hashCode();
    }
}