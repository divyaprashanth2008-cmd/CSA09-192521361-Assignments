import java.math.BigDecimal;
import java.time.LocalDate;
public record BusPass(String passId, String registerNo, String name, String routeNo,
                      String boardingStop, LocalDate validityDate, BigDecimal amountPaid) {
    public BusPass {
        if (passId == null || passId.isBlank() || registerNo == null || registerNo.isBlank()
                || name == null || name.isBlank() || routeNo == null || routeNo.isBlank()
                || boardingStop == null || validityDate == null || amountPaid == null
                || amountPaid.signum() < 0) throw new IllegalArgumentException("Invalid bus-pass data");
    }
    public String toCsv() {
        return String.join("|", passId, registerNo, name, routeNo, boardingStop,
                validityDate.toString(), amountPaid.toPlainString());
    }
    public static BusPass parse(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length != 7) throw new IllegalArgumentException("Expected 7 fields");
        return new BusPass(p[0],p[1],p[2],p[3],p[4],LocalDate.parse(p[5]),new BigDecimal(p[6]));
    }
}