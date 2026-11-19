package Task06;

/**
 * BusPass Domain Model
 * Student Name: Geetika T
 * Register Number: 192511469
 */
public class BusPass {
    private String passId;
    private String regNo;
    private String name;
    private String routeNo;
    private String boardingStop;
    private String validityDate;
    private double amountPaid;

    public BusPass(String passId, String regNo, String name, String routeNo,
                   String boardingStop, String validityDate, double amountPaid) {
        this.passId = passId;
        this.regNo = regNo;
        this.name = name;
        this.routeNo = routeNo;
        this.boardingStop = boardingStop;
        this.validityDate = validityDate;
        this.amountPaid = amountPaid;
    }

    public String getPassId() { return passId; }
    public String getRegNo() { return regNo; }
    public String getName() { return name; }
    public String getRouteNo() { return routeNo; }
    public String getBoardingStop() { return boardingStop; }
    public String getValidityDate() { return validityDate; }
    public double getAmountPaid() { return amountPaid; }

    public String toDelimitedLine() {
        return String.format("%s|%s|%s|%s|%s|%s|%.2f",
                passId, regNo, name, routeNo, boardingStop, validityDate, amountPaid);
    }

    @Override
    public String toString() {
        return String.format("BusPass[ID=%s, RegNo=%s, Name=%s, Route=%s, Stop=%s, Validity=%s, Amount=₹%.2f]",
                passId, regNo, name, routeNo, boardingStop, validityDate, amountPaid);
    }
}
