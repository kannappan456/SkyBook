import java.util.UUID;

public class PNRGenerator {

    public static String generatePNR() {
        return "SB" + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();
    }
}