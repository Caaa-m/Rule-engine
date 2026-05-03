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
}