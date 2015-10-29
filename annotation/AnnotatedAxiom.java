package annotation;

import logic.Statement;

public class AnnotatedAxiom extends AnnotatedStatement {
    public int axiomId;

    public AnnotatedAxiom(Statement statement, int axiomId) {
        super(statement);

        this.axiomId = axiomId;
    }
}
