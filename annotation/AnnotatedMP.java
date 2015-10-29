package annotation;

import logic.Statement;

public class AnnotatedMP extends AnnotatedStatement {
    public int alpha;
    public int beta;

    public AnnotatedMP(Statement statement, int alpha, int beta) {
        super(statement);

        this.alpha = alpha;
        this.beta = beta;
    }
}
