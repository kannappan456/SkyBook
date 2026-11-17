import java.util.EnumMap;
import java.util.Map;

public class Flight {

    private final String flightNumber;
    private final String airline;
    private final String source;
    private final String destination;
    private final String date;
    private final String departureTime;

    private final Map<TravelClass, Integer> capacity =
            new EnumMap<>(TravelClass.class);

    private final Map<TravelClass, Integer> occupied =
            new EnumMap<>(TravelClass.class);

    private final Map<TravelClass, Double> baseFare =
            new EnumMap<>(TravelClass.class);

    public Flight(String flightNumber, String airline,
                  String source, String destination,
                  String date, String departureTime,
                  int economyCapacity, int businessCapacity,
                  double economyFare, double businessFare) {

        this.flightNumber = flightNumber;
        this.airline = airline;
        this.source = source;
        this.destination = destination;
        this.date = date;
        this.departureTime = departureTime;

        capacity.put(TravelClass.ECONOMY, economyCapacity);
        capacity.put(TravelClass.BUSINESS, businessCapacity);

        occupied.put(TravelClass.ECONOMY, 0);
        occupied.put(TravelClass.BUSINESS, 0);

        baseFare.put(TravelClass.ECONOMY, economyFare);
        baseFare.put(TravelClass.BUSINESS, businessFare);
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public String getAirline() {
        return airline;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public String getDate() {
        return date;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public int getCapacity(TravelClass travelClass) {
        return capacity.get(travelClass);
    }

    public int getOccupied(TravelClass travelClass) {
        return occupied.get(travelClass);
    }

    public int getAvailableSeats(TravelClass travelClass) {
        return getCapacity(travelClass) - getOccupied(travelClass);
    }

    public double getBaseFare(TravelClass travelClass) {
        return baseFare.get(travelClass);
    }

    public boolean reserveSeat(TravelClass travelClass) {
        if (getAvailableSeats(travelClass) <= 0) {
            return false;
        }

        occupied.put(travelClass, getOccupied(travelClass) + 1);
        return true;
    }

    public void releaseSeat(TravelClass travelClass) {
        if (getOccupied(travelClass) <= 0) {
            throw new IllegalStateException("No occupied seat to release.");
        }

        occupied.put(travelClass, getOccupied(travelClass) - 1);
    }

    @Override
    public String toString() {
        return String.format(
                "%-8s %-18s %-15s %-15s %-12s %-10s",
                flightNumber, airline, source, destination,
                date, departureTime
        );
    }
}