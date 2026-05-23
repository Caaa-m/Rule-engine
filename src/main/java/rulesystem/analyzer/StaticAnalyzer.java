package rulesystem.analyzer;
import rulesystem.ast.Program;
import rulesystem.interpreter.State;

public class StaticAnalyzer {

    private ConflictDetector conflictDetector;
    private RedundancyDetector redundancyDetector;
    private InactiveRuleDetector inactiveRuleDetector;

    public StaticAnalyzer() {
        this.conflictDetector = new ConflictDetector();
        this.redundancyDetector = new RedundancyDetector();
        this.inactiveRuleDetector = new InactiveRuleDetector();
    }

    public void analyze(Program program, State initialState) {
        conflictDetector.detect(program);
        redundancyDetector.detect(program);
        inactiveRuleDetector.detect(program, initialState);
    }
}