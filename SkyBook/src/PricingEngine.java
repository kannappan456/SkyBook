import java.util.HashMap;
import java.util.Map;

public class PricingEngine {

    private final Map<String, Integer> confirmedCounts =
            new HashMap<>();

    private String key(Flight flight, TravelClass travelClass) {
        return flight.getFlightNumber() + "-" + travelClass;
    }

    public double calculateFare(Flight flight,
                                TravelClass travelClass) {

        int count = confirmedCounts.getOrDefault(
                key(flight, travelClass), 0
        );

        int increases = count / 2;

        double baseFare = flight.getBaseFare(travelClass);

        return Math.round(
                baseFare * (1 + (increases * 0.10)) * 100.0
        ) / 100.0;
    }

    public void recordConfirmation(Flight flight,
                                   TravelClass travelClass) {

        String key = key(flight, travelClass);

        confirmedCounts.put(
                key,
                confirmedCounts.getOrDefault(key, 0) + 1
        );
    }

    public int getConfirmedCount(Flight flight,
                                 TravelClass travelClass) {

        return confirmedCounts.getOrDefault(
                key(flight, travelClass), 0
        );
    }
}