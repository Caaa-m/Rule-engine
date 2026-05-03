package rulesystem.analyzer;

import rulesystem.ast.*;

import java.util.*;

public class RedundancyDetector {

    public void detect(Program program) {
        Map<String, List<String>> ruleMap = new HashMap<>();

        for (Rule rule : program.getRules()) {
            String signature = rule.getSignature();

            ruleMap
                .computeIfAbsent(signature, k -> new ArrayList<>())
                .add(rule.getName());
        }

        for (List<String> duplicates : ruleMap.values()) {
            if (duplicates.size() > 1) {
                System.out.println(
                    "Redundant rules: " + String.join(", ", duplicates)
                );
            }
        }
    }
}