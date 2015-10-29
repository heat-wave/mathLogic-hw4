package logic;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public abstract class BinOp extends AbstractStatement implements Statement {
    public final String opCode;
    public final Statement left;
    public final Statement right;
    private Constructor<?> defaultConstructor = null;

    protected BinOp(String opCode, Statement left, Statement right) {
        try {
            defaultConstructor = getClass().getConstructor(Statement.class, Statement.class);
        } catch (NoSuchMethodException ignore) {
        }

        this.opCode = opCode;
        this.left = left;
        this.right = right;
    }

    protected boolean compareImpl(String[] patterns, Statement other) {
        if (other instanceof BinOp) {
            BinOp bother = (BinOp) other;
            return opCode.equals(bother.opCode)
                    && left.compareInContext(patterns, bother.left)
                    && right.compareInContext(patterns, bother.right);
        } else {
            return false;
        }
    }

    public Statement substTerm(Expression haystack, Expression needle) {
        Statement result = null;
        try {
            result = (Statement) defaultConstructor.newInstance(left.substTerm(haystack, needle), right.substTerm(haystack, needle));
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException ignore) {
        }
        return result;
    }

    public Statement substPatterns(Statement[] to) {
        Statement result = null;
        try {
            result = (Statement) defaultConstructor.newInstance(left.substPatterns(to), right.substPatterns(to));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException ignore) {
        }
        return result;
    }

    public boolean evaluate(HashMap<String, Boolean> values) {
        return evaluateImpl(left.evaluate(values), right.evaluate(values));
    }

    public String toString() {
        return "(" + left + " " + opCode + " " + right + ")";
    }

    public String toRPNString() {
        return opCode + left.toRPNString() + right.toRPNString();
    }

    protected abstract boolean evaluateImpl(boolean a, boolean b);

    protected abstract boolean[] getTruthTable();
}
