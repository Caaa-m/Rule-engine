package rulesystem.ast;

import java.util.Set;

public interface Condition {
    Set<String> extractFacts();
    
    String asString();
}