package logic;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractExpression implements Expression {
    public Expression substTerm(Expression haystack, Expression needle) {
        if (equals(haystack)) {
            return needle;
        } else {
            return substImpl(haystack, needle);
        }
    }

    public Set<String> getVariables() {
        Set<String> vars = new HashSet<>();

        if (this instanceof Function) {
            Function ex = (Function) this;
            if (ex.terms.size() == 0) {
                vars.add(ex.name);
            } else {
                for (Expression term : ex.terms) {
                    vars.addAll(term.getVariables());
                }
            }
        } else if (this instanceof Multiply) {
            Multiply ex = (Multiply) this;
            vars.addAll(ex.left.getVariables());
            vars.addAll(ex.right.getVariables());
        } else if (this instanceof Sum) {
            Sum ex = (Sum) this;
            vars.addAll(ex.left.getVariables());
            vars.addAll(ex.right.getVariables());
        } else if (this instanceof Suc) {
            Suc ex = (Suc) this;
            vars.addAll(ex.child.getVariables());
        }

        return vars;
    }

    protected abstract Expression substImpl(Expression haystack, Expression needle);
}
