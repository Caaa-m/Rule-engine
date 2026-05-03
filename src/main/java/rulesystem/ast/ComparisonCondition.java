package rulesystem.ast;

import java.util.HashSet;
import java.util.Set;

public class ComparisonCondition implements AtomicCondition {
    private String variable;
    private String operator; // ">", "<", "="
    private int value;

    public ComparisonCondition(String variable, String operator, int value) {
        this.variable = variable;
        this.operator = operator;
        this.value = value;
    }

    public String getVariable() {
        return variable;
    }

    public String getOperator() {
        return operator;
    }

    public int getValue() {
        return value;
    }
    @Override
    public Set<String> extractFacts() {
        return new HashSet<>(); // no usa facts
    }
    @Override
    public String asString() {
    return variable + operator + value;
}
}