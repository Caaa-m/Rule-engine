package rulesystem.interpreter;

import rulesystem.ast.AndCondition;
import rulesystem.ast.ComparisonCondition;
import rulesystem.ast.Condition;
import rulesystem.ast.FactCondition;
import rulesystem.ast.Program;
import rulesystem.ast.Rule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class Interpreter {

    public void execute(Program program, State state) {
        boolean changed = true;

        while (changed) {
            changed = false;

            for (Rule rule : program.getRules()) {
                if (evaluateCondition(rule.getCondition(), state)) {
                    String fact = rule.getAction().getFactName();

                    if (!state.hasFact(fact)) {
                        state.addFact(fact);
                        changed = true;
                    }
                }
            }
        }

        printOutput(state.getActiveFacts());
    }

    private boolean evaluateCondition(Condition condition, State state) {
        if (condition instanceof AndCondition) {
            AndCondition and = (AndCondition) condition;
            return evaluateCondition(and.getLeft(), state)
                && evaluateCondition(and.getRight(), state);
        }

        if (condition instanceof ComparisonCondition) {
            ComparisonCondition cmp = (ComparisonCondition) condition;

            if (!state.hasVariable(cmp.getVariable())) {
                return false;
            }

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
            FactCondition fact = (FactCondition) condition;
            return state.hasFact(fact.getFactName());
        }

        return false;
    }

    private void printOutput(Set<String> activeFacts) {
        if (activeFacts.isEmpty()) {
            System.out.println("(no output)");
            return;
        }

        List<String> sorted = new ArrayList<>(activeFacts);
        Collections.sort(sorted);

        for (String fact : sorted) {
            System.out.println(fact);
        }
    }
}