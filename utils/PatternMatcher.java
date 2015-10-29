package utils;

import logic.*;

public class PatternMatcher {
    public Expression matched[];
    private int varCount;
    private boolean isValid;
    private boolean strictMode;

    public PatternMatcher(boolean strictMode) {
        this.varCount = 0;
        this.matched = new Expression[10];
        this.isValid = true;
        this.strictMode = strictMode;
    }

    public void match(Expression a, Expression b) {
        if (a instanceof ArithmeticPattern) {
            int patternId = ((ArithmeticPattern) a).patternId;
            varCount = Math.max(varCount, patternId + 1);
            if (matched[patternId] != null && !matched[patternId].equals(b)) {
                isValid = false;
            } else if (!strictMode || (b instanceof Function && ((Function) b).terms.size() == 0)) {
                matched[patternId] = b;
            }
        } else if (b instanceof ArithmeticPattern) {
            int patternId = ((ArithmeticPattern) b).patternId;
            varCount = Math.max(varCount, patternId + 1);
            if (matched[patternId] != null && !matched[patternId].equals(a)) {
                isValid = false;
            } else if (!strictMode || (a instanceof Function && ((Function) a).terms.size() == 0)) {
                matched[patternId] = a;
            }
        } else if (a instanceof Function && b instanceof Function) {
            Function stA = (Function) a;
            Function stB = (Function) b;
            if (stA.terms.size() == stB.terms.size()) {
                for (int i = 0; i < stA.terms.size(); i++) {
                    match(stA.terms.get(i), stB.terms.get(i));
                }
            }
        } else if (a instanceof Multiply && b instanceof Multiply) {
            Multiply stA = (Multiply) a;
            Multiply stB = (Multiply) b;
            match(stA.left, stB.left);
            match(stA.right, stB.right);
        } else if (a instanceof Sum && b instanceof Sum) {
            Sum stA = (Sum) a;
            Sum stB = (Sum) b;
            match(stA.left, stB.left);
            match(stA.right, stB.right);
        } else if (a instanceof Suc && b instanceof Suc) {
            Suc stA = (Suc) a;
            Suc stB = (Suc) b;
            match(stA.child, stB.child);
        } else if (!(a instanceof Zero) || !(b instanceof Zero)) {
            isValid = false;
        }
    }

    public void match(Statement a, Statement b) {
        if (a instanceof BinOp && b instanceof BinOp) {
            BinOp stA = (BinOp) a;
            BinOp stB = (BinOp) b;
            if (stA.opCode.equals(stB.opCode)) {
                match(stA.left, stB.left);
                match(stA.right, stB.right);
            }
        } else if (a instanceof Not && b instanceof Not) {
            Not stA = (Not) a;
            Not stB = (Not) b;
            match(stA.child, stB.child);
        } else if (a instanceof Exists && b instanceof Exists) {
            Exists stA = (Exists) a;
            Exists stB = (Exists) b;
            match(stA.child, stB.child);
        } else if (a instanceof Forall && b instanceof Forall) {
            Forall stA = (Forall) a;
            Forall stB = (Forall) b;
            match(stA.child, stB.child);
        } else if (a instanceof Equals && b instanceof Equals) {
            Equals stA = (Equals) a;
            Equals stB = (Equals) b;
            match(stA.left, stB.left);
            match(stA.right, stB.right);
        } else if (a instanceof Predicate && b instanceof Predicate) {
            Predicate stA = (Predicate) a;
            Predicate stB = (Predicate) b;
            if (stA.terms.size() == stB.terms.size()) {
                for (int i = 0; i < stA.terms.size(); i++) {
                    match(stA.terms.get(i), stB.terms.get(i));
                }
            }
        } else {
            isValid = false;
        }
    }

    public boolean isValid() {
        if (!isValid || varCount == 0) {
            return false;
        }
        for (int i = 0; i < varCount; i++) {
            if (matched[i] == null) {
                return false;
            }
        }
        return true;
    }
}
