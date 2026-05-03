package rulesystem.ast;

import java.util.HashSet;
import java.util.Set;

public class FactCondition implements AtomicCondition {
    private String factName;

    public FactCondition(String factName) {
        this.factName = factName;
    }

    public String getFactName() {
        return factName;
    }
    @Override
    public Set<String> extractFacts() {
        Set<String> facts = new HashSet<>();
        facts.add(factName);
        return facts;
    }
    @Override
    public String asString() {
        return factName;
    }
}