package annotation;

import logic.Statement;

public class AnnotatedAssumption extends AnnotatedStatement {
    public int lineNo;

    public AnnotatedAssumption(Statement statement, int lineNo) {
        super(statement);

        this.lineNo = lineNo;
    }
}
