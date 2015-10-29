package annotation;

import logic.Statement;

public abstract class AnnotatedStatement {
    public Statement statement;

    protected AnnotatedStatement(Statement statement) {
        this.statement = statement;
    }

    public boolean equals(Object other) {
        return this instanceof Unannotated && other instanceof Unannotated && statement.equals(((AnnotatedStatement) other).statement);
    }

    public int hashCode() {
        return statement.hashCode();
    }
}
