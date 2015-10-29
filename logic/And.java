package logic;

public class And extends BinOp {
    private static final boolean[] truthTable = new boolean[]{false, false, false, true};

    public And(Statement left, Statement right) {
        super("&", left, right);
    }

    protected boolean evaluateImpl(boolean a, boolean b) {
        return a & b;
    }

    protected boolean[] getTruthTable() {
        return truthTable;
    }
}
