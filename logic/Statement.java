package logic;

import java.util.HashMap;
import java.util.Set;

public interface Statement {
    boolean compareInContext(String[] patterns, Statement other);

    Statement substPatterns(Statement[] to);

    Statement substTerm(Expression haystack, Expression needle);

    Set<String> getFreeVariables();

    Set<String> getVariables();

    Set<String> getBoundVariables();

    boolean isFreeForSubstitution(String x, Expression phi);

    boolean evaluate(HashMap<String, Boolean> values);

    String toRPNString();
}
