package logic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractStatement implements Statement {
    private static boolean comparePatternToStatement(String[] patterns, Pattern a, Statement s) {
        int patternId = a.patternId;
        String sStr = s.toString();

        if (patterns[patternId] == null) {
            patterns[patternId] = sStr;
            return true;
        } else return patterns[patternId].equals(sStr);
    }

    public static Set<String> freeImpl(Statement statement, List<String> bound) {
        Set<String> vars = new HashSet<>();

        if (statement instanceof BinOp) {
            BinOp st = (BinOp) statement;
            vars.addAll(freeImpl(st.left, bound));
            vars.addAll(freeImpl(st.right, bound));
        } else if (statement instanceof Not) {
            Not st = (Not) statement;
            vars.addAll(freeImpl(st.child, bound));
        } else if (statement instanceof Exists) {
            Exists st = (Exists) statement;
            List<String> newBound = new ArrayList<>(bound);
            newBound.add(st.varName);
            vars.addAll(freeImpl(st.child, newBound));
        } else if (statement instanceof Forall) {
            Forall st = (Forall) statement;
            List<String> newBound = new ArrayList<>(bound);
            newBound.add(st.varName);
            vars.addAll(freeImpl(st.child, newBound));
        } else if (statement instanceof Equals) {
            Equals st = (Equals) statement;
            vars.addAll(freeImpl(st.left, bound));
            vars.addAll(freeImpl(st.right, bound));
        } else if (statement instanceof Predicate) {
            Predicate st = (Predicate) statement;
            for (Expression term : st.terms) {
                vars.addAll(freeImpl(term, bound));
            }
        }

        return vars;
    }

    public static Set<String> freeImpl(Expression expression, List<String> bound) {
        Set<String> vars = new HashSet<>();

        if (expression instanceof Function) {
            Function ex = (Function) expression;
            if (ex.terms.size() == 0) {
                if (!bound.contains(ex.name)) {
                    vars.add(ex.name);
                }
            } else {
                for (Expression term : ex.terms) {
                    vars.addAll(freeImpl(term, bound));
                }
            }
        } else if (expression instanceof Multiply) {
            Multiply ex = (Multiply) expression;
            vars.addAll(freeImpl(ex.left, bound));
            vars.addAll(freeImpl(ex.right, bound));
        } else if (expression instanceof Sum) {
            Sum ex = (Sum) expression;
            vars.addAll(freeImpl(ex.left, bound));
            vars.addAll(freeImpl(ex.right, bound));
        } else if (expression instanceof Suc) {
            Suc ex = (Suc) expression;
            vars.addAll(freeImpl(ex.child, bound));
        }

        return vars;
    }

    public boolean compareInContext(String[] patterns, Statement other) {
        if (other == null) {
            return false;
        }

        if (this instanceof Pattern) {
            return comparePatternToStatement(patterns, (Pattern) this, other);
        } else if (other instanceof Pattern) {
            return comparePatternToStatement(patterns, (Pattern) other, this);
        } else {
            return compareImpl(patterns, other);
        }
    }

    public boolean equals(Object other) {
        if (other instanceof AbstractStatement) {
            String[] patterns = new String[Pattern.MAXCOUNT];

            return compareInContext(patterns, (Statement) other);
        } else {
            return false;
        }
    }

    public Set<String> getFreeVariables() {
        return freeImpl(this, new ArrayList<>());
    }

    public Set<String> getVariables() {
        Set<String> vars = new HashSet<>();

        if (this instanceof BinOp) {
            BinOp st = (BinOp) this;
            vars.addAll(st.left.getVariables());
            vars.addAll(st.right.getVariables());
        } else if (this instanceof Not) {
            Not st = (Not) this;
            vars.addAll(st.child.getVariables());
        } else if (this instanceof Exists) {
            Exists st = (Exists) this;
            vars.addAll(st.child.getVariables());
        } else if (this instanceof Forall) {
            Forall st = (Forall) this;
            vars.addAll(st.child.getVariables());
        } else if (this instanceof Equals) {
            Equals st = (Equals) this;
            vars.addAll(st.left.getVariables());
            vars.addAll(st.right.getVariables());
        } else if (this instanceof Predicate) {
            Predicate st = (Predicate) this;
            for (Expression term : st.terms) {
                vars.addAll(term.getVariables());
            }
        }

        return vars;
    }

    public Set<String> getBoundVariables() {
        Set<String> vars = new HashSet<>();

        if (this instanceof BinOp) {
            BinOp st = (BinOp) this;
            vars.addAll(st.left.getBoundVariables());
            vars.addAll(st.right.getBoundVariables());
        } else if (this instanceof Not) {
            Not st = (Not) this;
            vars.addAll(st.child.getBoundVariables());
        } else if (this instanceof Exists) {
            Exists st = (Exists) this;
            vars.addAll(st.child.getBoundVariables());
            vars.add(st.varName);
        } else if (this instanceof Forall) {
            Forall st = (Forall) this;
            vars.addAll(st.child.getBoundVariables());
            vars.add(st.varName);
        }

        return vars;
    }

    public boolean isFreeForSubstitution(String x, Expression theta) {
        Set<String> thetaVars = theta.getVariables();
        Function var = new Function(x, new ArrayList<>());
        Set<String> freeVars = substTerm(var, theta).getFreeVariables();
        return freeVars.containsAll(thetaVars);
    }

    public int hashCode() {
        return toRPNString().hashCode();
    }

    protected abstract boolean compareImpl(String[] patterns, Statement other);
}
