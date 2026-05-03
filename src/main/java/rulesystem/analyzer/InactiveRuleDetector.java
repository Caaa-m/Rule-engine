package rulesystem.analyzer;

import rulesystem.ast.*;

import java.util.*;

public class InactiveRuleDetector {

    public void detect(Program program) {
        Set<String> producedFacts = new HashSet<>();

        // hechos generados
        for (Rule rule : program.getRules()) {
            producedFacts.add(rule.getAction().getFactName());
        }

        // revisar cada regla
        for (Rule rule : program.getRules()) {
            Set<String> requiredFacts = rule.getCondition().extractFacts();

            boolean possible = true;

            for (String fact : requiredFacts) {
                if (!producedFacts.contains(fact)) {
                    possible = false;
                    break;
                }
            }

            if (!possible) {
                System.out.println(
                    "Potentially inactive rule: " + rule.getName()
                );
            }
        }
    }
}