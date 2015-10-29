package logic;

public class Or extends BinOp {
    private static final boolean[] truthTable = new boolean[]{false, true, true, true};

    public Or(Statement left, Statement right) {
        super("|", left, right);
    }

    protected boolean evaluateImpl(boolean a, boolean b) {
        return a | b;
    }

    protected boolean[] getTruthTable() {
        return truthTable;
    }
}
