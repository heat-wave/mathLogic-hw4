package utils;

import annotation.*;
import exceptions.ParserException;
import logic.Statement;

import java.util.ArrayList;

public class Helper {
    public static ArrayList<Statement> axioms = parsePreloadedLines(
            "$1->$2->$1",
            "($1->$2)->($1->$2->$3)->($1->$3)",
            "$1->$2->$1&$2",
            "$1&$2->$1",
            "$1&$2->$2",
            "$1->$1|$2",
            "$2->$1|$2",
            "($1->$3)->($2->$3)->($1|$2->$3)",
            "($1->$2)->($1->!$2)->!$1",
            "!!$1->$1"
    );

    public static ArrayList<Statement> arithmAxioms = parsePreloadedLines(
            "#1=#2->#1'=#2'",
            "#1=#2->#1=#3->#2=#3",
            "#1'=#2'->#1=#2",
            "!#1'=0",
            "#1+#2'=(#1+#2)'",
            "#1+0=#1",
            "#1*0=0",
            "#1*#2'=#1*#2+#1"
    );

    public static ArrayList<Statement> parseSomeLines(String... lines) throws ParserException {
        PredicateParser parser = new PredicateParser();
        ArrayList<Statement> statements = new ArrayList<>(lines.length);

        for (String line : lines) {
            Statement statement = parser.parse(line);
            statements.add(statement);
        }

        return statements;
    }

    public static ArrayList<Statement> parsePreloadedLines(String... lines) {
        try {
            return parseSomeLines(lines);
        } catch (ParserException ignore) {
            return null;
        }
    }

    // just a dfs-like check of used statements
    private static void markImportantStatements(boolean[] used, ArrayList<AnnotatedStatement> statements, int current) {
        AnnotatedStatement annStatement = statements.get(current);

        used[current] = true;

        if (annStatement instanceof AnnotatedMP) {
            int alpha = ((AnnotatedMP) annStatement).alpha;
            int beta = ((AnnotatedMP) annStatement).beta;

            used[alpha] = true;
            used[beta] = true;
            markImportantStatements(used, statements, alpha);
            markImportantStatements(used, statements, beta);
        } else if (annStatement instanceof AnnotatedIR) {
            int lineNo = ((AnnotatedIR) annStatement).lineNo;

            used[lineNo] = true;
            markImportantStatements(used, statements, lineNo);
        }
    }

    public static ArrayList<Statement> removeRedundantStatements(ArrayList<AnnotatedStatement> statements) {
        ArrayList<Statement> relevantStatements = new ArrayList<>();
        boolean[] used = new boolean[statements.size()];

        for (int i = 0; i < statements.size(); i++) {
            if (statements.get(i) instanceof Unannotated) {
                used[i] = true;
            }
        }

        markImportantStatements(used, statements, statements.size() - 1);

        for (int i = 0; i < statements.size(); i++) {
            if (used[i]) {
                relevantStatements.add(statements.get(i).statement);
            }
        }

        return relevantStatements;
    }

    public static ArrayList<AnnotatedStatement> annotateInContext(ArrayList<Statement> context, ArrayList<Statement> statements) {
        ArrayList<AnnotatedStatement> annStatements = new ArrayList<>(statements.size());

        for (Statement stmt : statements) {
            int assumptionNum = -1;

            for (int i = 0; i < context.size(); i++) {
                if (stmt.equals(context.get(i))) {
                    assumptionNum = i;
                    break;
                }
            }

            if (assumptionNum != -1) {
                annStatements.add(new AnnotatedAssumption(stmt, assumptionNum));
            } else {
                annStatements.add(new Unannotated(stmt));
            }
        }

        return annStatements;
    }

}
