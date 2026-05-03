package rulesystem.ast;

import java.util.HashSet;
import java.util.Set;

public class AndCondition implements Condition {
    private Condition left;
    private Condition right;

    public AndCondition(Condition left, Condition right) {
        this.left = left;
        this.right = right;
    }

    public Condition getLeft() {
        return left;
    }

    public Condition getRight() {
        return right;
    }
    @Override
    public Set<String> extractFacts() {
        Set<String> facts = new HashSet<>();
        facts.addAll(left.extractFacts());
        facts.addAll(right.extractFacts());
        return facts;
    }
    @Override
    public String asString() {
    return "(" + left.asString() + " AND " + right.asString() + ")";
}
}