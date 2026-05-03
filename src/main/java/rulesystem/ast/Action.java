package rulesystem.ast;
public class Action {
    private String factName;

    public Action(String factName) {
        this.factName = factName;
    }

    public String getFactName() {
        return factName;
    }
}