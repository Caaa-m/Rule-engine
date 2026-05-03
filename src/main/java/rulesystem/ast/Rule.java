package rulesystem.ast;
public class Rule {
    private String name;
    private Condition condition;
    private Action action;

    public Rule(String name, Condition condition, Action action) {
        this.name = name;
        this.condition = condition;
        this.action = action;
    }

    public String getName() {
        return name;
    }

    public Condition getCondition() {
        return condition;
    }

    public Action getAction() {
        return action;
    }
    public String getSignature() {
        return condition.asString() + "->" + action.getFactName();
    }
}