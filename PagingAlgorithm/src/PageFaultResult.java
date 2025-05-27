public class PageFaultResult {
    public final int faults;
    public final double rate;
    public final double eat;
    public PageFaultResult(int faults, double rate, double eat) {
        this.faults = faults;
        this.rate   = rate;
        this.eat    = eat;
    }
}
