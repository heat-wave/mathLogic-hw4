package utils;

import annotation.*;
import logic.*;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Deductor {
    private static final ArrayList<Statement> ruleSelf = Helper.parsePreloadedLines(
            "$1->$1->$1",
            "$1->($1->$1)->$1",
            "($1->$1->$1)->($1->($1->$1)->$1)->($1->$1)",
            "($1->($1->$1)->$1)->($1->$1)",
            "$1->$1"
    );
    private static final ArrayList<Statement> ruleAxiom = Helper.parsePreloadedLines(
            "$2",
            "$2->$1->$2",
            "$1->$2"
    );
    private static final ArrayList<Statement> ruleMP = Helper.parsePreloadedLines(
            "$1->$2",
            "$1->$2->$3",
            "($1->$2)->($1->$2->$3)->($1->$3)",
            "($1->$2->$3)->($1->$3)",
            "$1->$3"
    );

    private static final ArrayList<Statement> ruleIR1a = Helper.parsePreloadedLines(
            "($1->($2->$3))",
            "(($1->($2->$3))->(($1&$2)->($1->($2->$3))))",
            "(($1&$2)->($1->($2->$3)))",
            "(($1&$2)->$1)",
            "(($1&$2)->$2)",
            "((($1&$2)->$1)->((($1&$2)->($1->($2->$3)))->(($1&$2)->($2->$3))))",
            "((($1&$2)->($1->($2->$3)))->(($1&$2)->($2->$3)))",
            "(($1&$2)->($2->$3))",
            "((($1&$2)->$2)->((($1&$2)->($2->$3))->(($1&$2)->$3)))",
            "((($1&$2)->($2->$3))->(($1&$2)->$3))",
            "(($1&$2)->$3)"
    );

    private static final ArrayList<Statement> ruleIR1b = Helper.parsePreloadedLines(
            "(($1&$2)->$3)",
            "((($1&$2)->$3)->($1->(($1&$2)->$3)))",
            "($1->(($1&$2)->$3))",
            "((($1&$2)->$3)->($2->(($1&$2)->$3)))",
            "(((($1&$2)->$3)->($2->(($1&$2)->$3)))->($1->((($1&$2)->$3)->($2->(($1&$2)->$3)))))",
            "($1->((($1&$2)->$3)->($2->(($1&$2)->$3))))",
            "(($1->(($1&$2)->$3))->(($1->((($1&$2)->$3)->($2->(($1&$2)->$3))))->($1->($2->(($1&$2)->$3)))))",
            "(($1->((($1&$2)->$3)->($2->(($1&$2)->$3))))->($1->($2->(($1&$2)->$3))))",
            "($1->($2->(($1&$2)->$3)))",
            "($1->($2->($1&$2)))",
            "(($2->($1&$2))->(($2->(($1&$2)->$3))->($2->$3)))",
            "((($2->($1&$2))->(($2->(($1&$2)->$3))->($2->$3)))->($1->(($2->($1&$2))->(($2->(($1&$2)->$3))->($2->$3)))))",
            "($1->(($2->($1&$2))->(($2->(($1&$2)->$3))->($2->$3))))",
            "(($1->($2->($1&$2)))->(($1->(($2->($1&$2))->(($2->(($1&$2)->$3))->($2->$3))))->($1->(($2->(($1&$2)->$3))->($2->$3)))))",
            "(($1->(($2->($1&$2))->(($2->(($1&$2)->$3))->($2->$3))))->($1->(($2->(($1&$2)->$3))->($2->$3))))",
            "($1->(($2->(($1&$2)->$3))->($2->$3)))",
            "(($1->($2->(($1&$2)->$3)))->(($1->(($2->(($1&$2)->$3))->($2->$3)))->($1->($2->$3))))",
            "(($1->(($2->(($1&$2)->$3))->($2->$3)))->($1->($2->$3)))",
            "($1->($2->$3))"
    );

    private static final ArrayList<Statement> ruleIR2 = Helper.parsePreloadedLines(
            "($1->($2->$3))",
            "(($1->($2->$3))->($2->($1->($2->$3))))",
            "($2->($1->($2->$3)))",
            "($2->($1->$2))",
            "(($1->$2)->(($1->($2->$3))->($1->$3)))",
            "((($1->$2)->(($1->($2->$3))->($1->$3)))->($2->(($1->$2)->(($1->($2->$3))->($1->$3)))))",
            "($2->(($1->$2)->(($1->($2->$3))->($1->$3))))",
            "(($2->($1->$2))->(($2->(($1->$2)->(($1->($2->$3))->($1->$3))))->($2->(($1->($2->$3))->($1->$3)))))",
            "(($2->(($1->$2)->(($1->($2->$3))->($1->$3))))->($2->(($1->($2->$3))->($1->$3))))",
            "($2->(($1->($2->$3))->($1->$3)))",
            "(($2->($1->($2->$3)))->(($2->(($1->($2->$3))->($1->$3)))->($2->($1->$3))))",
            "(($2->(($1->($2->$3))->($1->$3)))->($2->($1->$3)))",
            "($2->($1->$3))"
    );

    private static void processRule(ArrayList<Statement> rule, ArrayList<AnnotatedStatement> out, Statement[] to) {
        out.addAll(rule.stream().map(aRule -> new Unannotated(aRule.substPatterns(to))).collect(Collectors.toList()));
    }

    public static ArrayList<AnnotatedStatement> deduce(ArrayList<AnnotatedStatement> proof, Statement assumption) {
        Statement[] to = new Statement[4];
        to[0] = assumption;

        ArrayList<AnnotatedStatement> enhancedProof = new ArrayList<>();

        for (AnnotatedStatement annStmt : proof) {
            Statement stmt = annStmt.statement;

            // A -> A
            if (stmt.equals(assumption)) {
                processRule(ruleSelf, enhancedProof, to);
            }

            // A -> a, a is assumption or axiom
            else if (annStmt instanceof AnnotatedAssumption || annStmt instanceof AnnotatedAxiom) {
                to[1] = stmt;
                processRule(ruleAxiom, enhancedProof, to);
            }

            // A -> b, b is modus ponens
            else if (annStmt instanceof AnnotatedMP) {
                to[1] = proof.get(((AnnotatedMP) annStmt).alpha).statement;
                to[2] = stmt;
                processRule(ruleMP, enhancedProof, to);
            } else if (annStmt instanceof AnnotatedIR) {
                AnnotatedIR irStmt = (AnnotatedIR) annStmt;
                Implication st = (Implication) proof.get(irStmt.lineNo).statement;
                Statement stL = st.left;
                Statement stR = st.right;

                // A -> c, c is inference rule 1
                if (irStmt.ruleId == 1) {
                    String varName = ((Forall) (((Implication) irStmt.statement).right)).varName;

                    to[1] = stL;
                    to[2] = stR;
                    processRule(ruleIR1a, enhancedProof, to);

                    Statement quantifier = new Forall(varName, stR);
                    Statement newLine = new Implication(new And(assumption, stL), quantifier);
                    enhancedProof.add(new Unannotated(newLine));

                    to[2] = quantifier;
                    processRule(ruleIR1b, enhancedProof, to);
                }

                // A -> d, d is inference rule 2
                else if (irStmt.ruleId == 2) {
                    String varName = ((Exists) (((Implication) irStmt.statement).left)).varName;

                    to[1] = stL;
                    to[2] = stR;
                    processRule(ruleIR2, enhancedProof, to);

                    Statement quantifier = new Exists(varName, stL);
                    Statement newLine = new Implication(quantifier, new Implication(assumption, stR));
                    enhancedProof.add(new Unannotated(newLine));

                    to[0] = quantifier;
                    to[1] = assumption;
                    processRule(ruleIR2, enhancedProof, to);
                    to[0] = assumption;
                }
            }
        }

        return enhancedProof;
    }
}
