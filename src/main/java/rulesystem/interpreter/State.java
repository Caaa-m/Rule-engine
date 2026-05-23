package rulesystem.interpreter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class State {
    private Map<String, Integer> variables;
    private Set<String> activeFacts;

    public State() { this.variables = new HashMap<>(); this.activeFacts = new HashSet<>(); }

    public void setVariable(String name, int value) { variables.put(name, value); }
    public boolean hasVariable(String name) { return variables.containsKey(name); }
    public int getVariable(String name) { return variables.get(name); }

    public void addFact(String fact) { activeFacts.add(fact); }
    public boolean hasFact(String fact) { return activeFacts.contains(fact); }
    public Set<String> getActiveFacts() { return activeFacts; }

    /** Expone las variables para poder hacer un snapshot del estado */
    public Map<String, Integer> getVariables() { return variables; }
}