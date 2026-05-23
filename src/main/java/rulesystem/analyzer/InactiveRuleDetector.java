package rulesystem.analyzer;
import rulesystem.ast.*;
import rulesystem.interpreter.State;
import java.util.*;

public class InactiveRuleDetector {

    /**
     * Detecta reglas potencialmente inactivas.
     * Una regla se considera potencialmente inactiva si:
     *   1. Su condición NO se satisface con el estado inicial.
     *   2. Su acción no es requerida por ninguna otra regla (está aislada).
     *
     * El análisis solo aplica cuando hay más de una regla (en sistemas con
     * una sola regla no hay contexto de interacción entre reglas).
     * Las reglas redundantes se excluyen.
     */
    public void detect(Program program, State initialState) {
        if (program.getRules().size() <= 1) return;

        // Identificar firmas redundantes para excluirlas
        Set<String> redundantSignatures = new HashSet<>();
        Map<String, Integer> sigCount = new HashMap<>();
        for (Rule rule : program.getRules()) {
            String sig = rule.getSignature();
            sigCount.put(sig, sigCount.getOrDefault(sig, 0) + 1);
        }
        for (Map.Entry<String, Integer> e : sigCount.entrySet()) {
            if (e.getValue() > 1) redundantSignatures.add(e.getKey());
        }

        // Facts alcanzables = iniciales + producidos por reglas
        Set<String> reachableFacts = new HashSet<>(initialState.getActiveFacts());
        for (Rule rule : program.getRules()) {
            reachableFacts.add(rule.getAction().getFactName());
        }

        // Facts requeridos como FactCondition por al menos una regla
        Set<String> requiredByRules = new HashSet<>();
        for (Rule rule : program.getRules()) {
            requiredByRules.addAll(rule.getCondition().extractFacts());
        }

        for (Rule rule : program.getRules()) {
            if (redundantSignatures.contains(rule.getSignature())) continue;

            boolean conditionSatisfied = canFire(rule.getCondition(), reachableFacts, initialState);
            boolean actionRequiredByOthers = requiredByRules.contains(rule.getAction().getFactName());

            if (!conditionSatisfied && !actionRequiredByOthers) {
                System.out.println("Potentially inactive rule: " + rule.getName());
            }
        }
    }

    private boolean canFire(Condition condition, Set<String> reachableFacts, State initialState) {
        if (condition instanceof AndCondition) {
            AndCondition and = (AndCondition) condition;
            return canFire(and.getLeft(), reachableFacts, initialState)
                && canFire(and.getRight(), reachableFacts, initialState);
        }
        if (condition instanceof ComparisonCondition) {
            ComparisonCondition cmp = (ComparisonCondition) condition;
            if (!initialState.hasVariable(cmp.getVariable())) return false;
            int varValue = initialState.getVariable(cmp.getVariable());
            int cmpValue = cmp.getValue();
            switch (cmp.getOperator()) {
                case ">": return varValue > cmpValue;
                case "<": return varValue < cmpValue;
                case "=": return varValue == cmpValue;
                default:  return false;
            }
        }
        if (condition instanceof FactCondition) {
            return reachableFacts.contains(((FactCondition) condition).getFactName());
        }
        return false;
    }
}