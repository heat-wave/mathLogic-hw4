package utils;

import annotation.AnnotatedStatement;
import exceptions.AnnotatorException;
import logic.Statement;

import java.util.ArrayList;

// Helper class for convenience
public class Proof {
    private final ArrayList<Statement> context;
    private final ArrayList<AnnotatedStatement> statements;

    public Proof(ArrayList<Statement> context, ArrayList<AnnotatedStatement> statements) {
        this.context = context;
        this.statements = statements;
    }

    public ArrayList<AnnotatedStatement> getStatements() {
        return statements;
    }

    public Proof getAnnotatedProof() throws AnnotatorException {
        return new Proof(context, ProofAnnotator.getAnnotatedProof(context, statements));
    }

    public Proof deduce() throws AnnotatorException {
        ArrayList<Statement> newContext = new ArrayList<>(context);
        Statement assumption = newContext.remove(newContext.size() - 1);

        return new Proof(newContext, Deductor.deduce(ProofAnnotator.getAnnotatedProof(context, statements), assumption));
    }

    public Proof removeRedundantStatements() throws AnnotatorException {
        ArrayList<Statement> newStatements = Helper.removeRedundantStatements(ProofAnnotator.getAnnotatedProof(context, statements));

        return new Proof(context, Helper.annotateInContext(context, newStatements));
    }
}
