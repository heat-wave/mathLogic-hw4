package utils;

import annotation.*;
import exceptions.AnnotatorException;
import logic.*;

import java.util.ArrayList;
import java.util.HashMap;

public class ProofAnnotator {
    public static ArrayList<AnnotatedStatement> getAnnotatedProof(ArrayList<Statement> context, ArrayList<AnnotatedStatement> proof) throws AnnotatorException {

        HashMap<String, Integer> contextLines = new HashMap<>(); // lookup map of context lines and their numbers
        HashMap<String, Integer> proofLines = new HashMap<>(); // lookup map of proof lines and their numbers

        HashMap<String, Integer> needAlpha = new HashMap<>(); // two maps for
        HashMap<String, MPPair> betas = new HashMap<>(); // modus ponens

        ArrayList<AnnotatedStatement> annotatedProof = new ArrayList<>();

        for (int lineNo = 0; lineNo < context.size(); lineNo++) {
            Statement assumption = context.get(lineNo);
            String assumptionString = assumption.toRPNString();

            contextLines.put(assumptionString, lineNo);
        }

        AnnotatedStatement annotatedStatement;
        Statement statement;
        String statementString = "";
        for (int lineNo = 0; lineNo < proof.size(); lineNo++) {
            if (lineNo > 0) {
                // add to lines list
                if (!proofLines.containsKey(statementString)) {
                    proofLines.put(statementString, lineNo - 1);
                }
            }

            annotatedStatement = proof.get(lineNo);
            statement = annotatedStatement.statement;
            statementString = statement.toRPNString();

            boolean isMP = false;
            // check if expression is modus ponens
            if (betas.containsKey(statementString)) {
                MPPair mp = betas.get(statementString);
                annotatedProof.add(new AnnotatedMP(statement, mp.alpha, mp.beta));
                isMP = true;
            }

            if (statement instanceof Implication) {
                // add to potential modus ponens-able list
                String alphaStr = ((Implication) statement).left.toRPNString();
                String betaStr = ((Implication) statement).right.toRPNString();

                // find potential alpha above alpha->beta
                if (proofLines.containsKey(alphaStr)) {
                    betas.put(betaStr, new MPPair(proofLines.get(alphaStr), lineNo));
                }
                // save alpha string for future use
                else {
                    needAlpha.put(alphaStr, lineNo);
                }
            }

            // check if some statement needs current statement
            if (needAlpha.containsKey(statementString)) {
                int alphaBetaNo = needAlpha.get(statementString);
                Statement beta = ((Implication) proof.get(alphaBetaNo).statement).right;
                String betaStr = beta.toRPNString();
                betas.put(betaStr, new MPPair(lineNo, alphaBetaNo));
                needAlpha.remove(statementString);
            }

            if (isMP) {
                continue;
            }

            // check if the expression is an assumption
            if (contextLines.containsKey(statementString)) {
                annotatedProof.add(new AnnotatedAssumption(statement, contextLines.get(statementString)));
            } else if (annotatedStatement instanceof AnnotatedAssumption) {
                annotatedProof.add(annotatedStatement);
            } else {
                boolean isAxiom = false;

                // check if the statement is axiom 1-10
                for (int i = 0; i < Helper.axioms.size(); i++) {
                    if (statement.equals(Helper.axioms.get(i))) {
                        annotatedProof.add(new AnnotatedAxiom(statement, i));
                        isAxiom = true;
                        break;
                    }
                }

                if (isAxiom) {
                    continue;
                }

                // check if the statement is arithmetic axiom 1-8
                for (int i = 0; i < Helper.arithmAxioms.size(); i++) {
                    PatternMatcher patternMatcher = new PatternMatcher(true);
                    patternMatcher.match(Helper.arithmAxioms.get(i), statement);
                    if (patternMatcher.isValid()) {
                        annotatedProof.add(new AnnotatedAxiom(statement, i));
                        isAxiom = true;
                        break;
                    }
                }

                if (isAxiom) {
                    continue;
                }

                // check if the statement is axiom 11-12 or the inference rule 1-2 of predicate calculus
                if (statement instanceof Implication) {
                    Statement stL = ((Implication) statement).left;
                    Statement stR = ((Implication) statement).right;

                    // (φ) → (ψ) ⇒ (φ) → ∀x(ψ)
                    if (stR instanceof Forall) {
                        String varName = ((Forall) stR).varName;
                        Statement subR = ((Forall) stR).child;

                        String toSearch = new Implication(stL, subR).toRPNString();

                        if (proofLines.containsKey(toSearch)) {
                            if (stL.getFreeVariables().contains(varName)) {
                                throw new AnnotatorException(statement, "variable " + varName + " is free in " + stL);
                            } else {
                                // statement is the inference rule 1
                                int bareLineNo = proofLines.get(toSearch);
                                annotatedProof.add(new AnnotatedIR(statement, 1, bareLineNo));
                                continue;
                            }
                        }
                    }

                    // (ψ) → (φ) ⇒ ∃x(ψ) → (φ)
                    if (stL instanceof Exists) {
                        String varName = ((Exists) stL).varName;
                        Statement subL = ((Exists) stL).child;

                        String toSearch = new Implication(subL, stR).toRPNString();

                        if (proofLines.containsKey(toSearch)) {
                            if (stR.getFreeVariables().contains(varName)) {
                                throw new AnnotatorException(statement, "variable " + varName + " is free in " + stR);
                            } else {
                                // statement is inference rule 2
                                int bareLineNo = proofLines.get(toSearch);
                                annotatedProof.add(new AnnotatedIR(statement, 2, bareLineNo));
                                continue;
                            }
                        }
                    }

                    // ∀x(ψ) → (ψ[x := θ])
                    if (stL instanceof Forall) {
                        String varName = ((Forall) stL).varName;
                        Statement subQ = ((Forall) stL).child;

                        Function var = new Function(varName, new ArrayList<>());
                        PatternMatcher patternMatcher = new PatternMatcher(false);

                        patternMatcher.match(subQ.substTerm(var, new ArithmeticPattern(0)), stR);
                        Expression theta = patternMatcher.matched[0];

                        if (theta != null) {
                            if (subQ.isFreeForSubstitution(varName, theta)) {
                                // statement is axiom 11
                                annotatedProof.add(new AnnotatedAxiom(statement, 10));
                                continue;
                            } else {
                                throw new AnnotatorException(statement, "term " + theta + " isn't free for substitution in " + subQ + " instead of variable " + varName);
                            }
                        }
                    }

                    // (ψ[x := θ]) → ∃x(ψ)
                    if (stR instanceof Exists) {
                        String varName = ((Exists) stR).varName;
                        Statement subQ = ((Exists) stR).child;

                        Function var = new Function(varName, new ArrayList<>());
                        PatternMatcher patternMatcher = new PatternMatcher(false);

                        patternMatcher.match(subQ.substTerm(var, new ArithmeticPattern(0)), stL);
                        Expression theta = patternMatcher.matched[0];

                        if (theta != null) {
                            if (subQ.isFreeForSubstitution(varName, theta)) {
                                // statement is axiom 12
                                annotatedProof.add(new AnnotatedAxiom(statement, 11));
                                continue;
                            } else {
                                throw new AnnotatorException(statement, "term " + theta + " isn't free for substitution in " + subQ + " instead of variable " + varName);
                            }
                        }
                    }

                    // induction check
                    // (ψ[x := 0]) & ∀x((ψ) → (ψ)[x := x']) → (ψ)
                    if (stL instanceof And) {
                        Statement stLL = ((And) stL).left;
                        Statement stLR = ((And) stL).right;

                        if (stLR instanceof Forall) {
                            String varName = ((Forall) stLR).varName;
                            Statement subQ = ((Forall) stLR).child;
                            Function var = new Function(varName, new ArrayList<>());

                            if (subQ instanceof Implication) {
                                Statement subQL = ((Implication) subQ).left;
                                Statement subQR = ((Implication) subQ).right;

                                boolean flag = subQL.equals(stR)
                                        && stLL.equals(stR.substTerm(var, new Zero()))
                                        && subQR.equals(stR.substTerm(var, new Suc(var)));

                                if (flag) {
                                    // statement is axiom A9
                                    annotatedProof.add(new AnnotatedAxiom(statement, 8));
                                    continue;
                                }
                            }
                        }
                    }
                }

                // error while annotating
                throw new AnnotatorException(statement, "unknown statement");
            }
        }

        return annotatedProof;
    }

}
