package rulesystem.interpreter;
import rulesystem.ast.*;
import java.util.*;

public class Interpreter {

    public void execute(Program program, State state) {
        // Hechos activados SOLO por reglas (no incluye los del estado inicial)
        Set<String> derivedFacts = new HashSet<>();

        boolean changed = true;
        while (changed) {
            changed = false;
            for (Rule rule : program.getRules()) {
                if (evaluateCondition(rule.getCondition(), state)) {
                    String fact = rule.getAction().getFactName();
                    if (!state.hasFact(fact)) {
                        state.addFact(fact);
                        derivedFacts.add(fact);
                        changed = true;
                    }
                }
            }
        }

        printOutput(derivedFacts);
    }

    private boolean evaluateCondition(Condition condition, State state) {
        if (condition instanceof AndCondition) {
            AndCondition and = (AndCondition) condition;
            return evaluateCondition(and.getLeft(), state) && evaluateCondition(and.getRight(), state);
        }
        if (condition instanceof ComparisonCondition) {
            ComparisonCondition cmp = (ComparisonCondition) condition;
            if (!state.hasVariable(cmp.getVariable())) return false;
            int varValue = state.getVariable(cmp.getVariable());
            int cmpValue = cmp.getValue();
            switch (cmp.getOperator()) {
                case ">": return varValue > cmpValue;
                case "<": return varValue < cmpValue;
                case "=": return varValue == cmpValue;
                default:  return false;
            }
        }
        if (condition instanceof FactCondition) {
            return state.hasFact(((FactCondition) condition).getFactName());
        }
        return false;
    }

    private void printOutput(Set<String> derivedFacts) {
        if (derivedFacts.isEmpty()) { System.out.println("(no output)"); return; }
        List<String> sorted = new ArrayList<>(derivedFacts);
        Collections.sort(sorted);
        for (String fact : sorted) System.out.println(fact);
    }
}