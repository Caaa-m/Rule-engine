public class FactCondition implements AtomicCondition {
    private String factName;

    public FactCondition(String factName) {
        this.factName = factName;
    }

    public String getFactName() {
        return factName;
    }
}