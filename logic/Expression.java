package logic;

import java.util.Set;

public interface Expression {
    Expression substTerm(Expression haystack, Expression needle);
    Set<String> getVariables();
    String toRPNString();
}
